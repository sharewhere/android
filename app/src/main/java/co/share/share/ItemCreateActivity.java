package co.share.share;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import co.share.share.models.Shareable;
import co.share.share.net.NetworkService;

import co.share.share.net.ShareWhereRespHandler;
import co.share.share.views.FloatingActionButton;


public class ItemCreateActivity extends ShareWhereActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    Context mContext;
    Bitmap mBitmap;
    EditText mItemTitleText;
    EditText mDescriptionText;
    TextView mStartDateText;
    TextView mEndDateText;
    Menu mOptionsMenu;
    private CreateType mCreateType;

    public static final String CREATE_TYPE = "CREATE_TYPE";

    public enum CreateType {
        OFFER,
        REQUEST
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_create);

        Bundle extras = getIntent().getExtras();
        mCreateType = (CreateType) extras.get(CREATE_TYPE);

        // Set up action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mContext = this;
        mItemTitleText = (EditText) findViewById(R.id.item_title);
        mDescriptionText = (EditText) findViewById(R.id.item_description);
        mStartDateText = (TextView) findViewById(R.id.start_date);
        mEndDateText = (TextView) findViewById(R.id.end_date);

        // floating action button
        findViewById(R.id.action_add_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }

        });

        Calendar c = Calendar.getInstance();
        final int startYear = c.get(Calendar.YEAR);
        final int startMonth = c.get(Calendar.MONTH);
        final int startDay = c.get(Calendar.DAY_OF_MONTH);

        findViewById(R.id.start_date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = + monthOfYear + "/" + dayOfMonth + "/" +  year;
                        mStartDateText.setText(date);
                    }
                }, startYear, startMonth, startDay).show();
            }
        });

        findViewById(R.id.end_date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = + monthOfYear + "/" + dayOfMonth + "/" +  year;
                        mEndDateText.setText(date);
                    }
                }, startYear, startMonth, startDay).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu; // keep a reference for progress changes

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.action_create:
                createShareable();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            mBitmap = (Bitmap) extras.get("data");

            ImageView mImageView = (ImageView) findViewById(R.id.item_image);
            mImageView.setImageBitmap(mBitmap);
        }
    }

    private void showProgress(boolean state)
    {
        // grab the target action bar view
        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.action_create);

        if (refreshItem != null) {
            if (state) {
                refreshItem.setActionView(R.layout.actionbar_unknown_progress);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }

    private void createShareable()
    {
        final String shareable_name = mItemTitleText.getText().toString();
        final String description = mDescriptionText.getText().toString();

        // Reset errors.
        mItemTitleText.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid shareable name
        if (TextUtils.isEmpty(shareable_name)) {
            mItemTitleText.setError(getString(R.string.error_invalid_sharable_name));
            focusView = mItemTitleText;
            cancel = true;
        }

        // Check for a valid shareable description
        if (TextUtils.isEmpty(description)) {
            mDescriptionText.setError(getString(R.string.error_invalid_description));
            if(focusView == null)
                focusView = mItemTitleText;
            cancel = true;
        }

        // Check for a valid shareable image
        if (mBitmap == null) {
            Toast failToast = Toast.makeText(getApplicationContext(),
                    getText(R.string.error_missing_sharable_pic),
                    Toast.LENGTH_SHORT);
            failToast.show();

            if(focusView == null)
                focusView = mItemTitleText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if(focusView != null)
                focusView.requestFocus();
            return;
        }

        showProgress(true);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);

        RequestParams params = new RequestParams();
        params.put("picture", new ByteArrayInputStream(output.toByteArray()));
        params.put("shar_name", shareable_name);
        params.put("description", description);

        String endpoint;
        if (mCreateType == CreateType.OFFER)
            endpoint = "/makeshareableoffer";
        else
            endpoint = "/makeshareablerequest";

        NetworkService.post(endpoint, params, new ShareWhereRespHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject resp) {
                if(logoutIfInvalidCookie(resp, ItemCreateActivity.this))
                    return;

                try {
                    boolean success = resp.getBoolean("success");
                    if (!success)
                        return;

                } catch (JSONException e) {
                    Log.wtf(this.getClass().getSimpleName(), "JSON Exception in ItemCreateActivity");
                    return;
                }

                Log.d("ItemCreateActivity", resp.toString());

                Intent i = new Intent(ItemCreateActivity.this, ItemDetailActivity.class);
                i.putExtra("data", mBitmap);
                i.putExtra("title", shareable_name);
                i.putExtra("description", description);
                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(int code, Header [] wow, String wat, Throwable e) {
                Toast failToast = Toast.makeText(getApplicationContext(), "Failed to create sharable", Toast.LENGTH_LONG);
                failToast.show();
            }

            @Override
            public void onFinish()
            {
                showProgress(false);
            }
        });

    }
}
