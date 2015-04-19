package co.share.share;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import co.share.share.models.Shareable;
import co.share.share.net.NetworkService;

import co.share.share.net.ShareWhereRespHandler;
import co.share.share.util.Constants;
import co.share.share.views.FloatingActionButton;


public class ItemCreateActivity extends ShareWhereActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final String UPLOAD_FILE_NAME = "upload.jpg";

    Context mContext;

    ImageView mImageView;
    int mTargetW;
    int mTargetH;
    EditText mItemTitleText;
    EditText mDescriptionText;
    TextView mStartDateText;
    TextView mEndDateText;
    Menu mOptionsMenu;
    Constants.CreateType mCreateType;

    // image handling
    Bitmap mBitmapThumbnail;
    File mImageFile;
    String mCurrentImagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_create);

        Bundle extras = getIntent().getExtras();
        mCreateType = (Constants.CreateType) extras.get(Constants.CREATE_TYPE);

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
        mImageView = (ImageView) findViewById(R.id.item_image);

        // floating action button
        findViewById(R.id.action_add_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                resetImage();

                try {
                    mImageFile = createImageFile();
                } catch (IOException e) {
                    Log.wtf(this.getClass().getSimpleName(), "Failed to create picture file");
                    return;
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mImageFile));

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                else {
                    resetImage();
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
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if(resultCode == RESULT_OK) {
                //mImageView.setImageBitmap(mBitmapThumbnail);
                //setPic();
            }
            else {
                resetImage();
            }
        }
    }

    private void resetImage()
    {
        mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image));
        mCurrentImagePath = "";
        mImageFile = null;
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

    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, UPLOAD_FILE_NAME);

        // clean out the old uploaded image
        if(image.exists()) {
            image.delete();
        }

        image.createNewFile();

        mCurrentImagePath = "file:" + image.getAbsolutePath();

        return image;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        mTargetW = mImageView.getWidth();
        mTargetH = mImageView.getHeight();
    }

    private void setPic() {
        // Get the dimensions of the View
        //int targetW = mImageView.getWidth();
        //int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentImagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/mTargetW, photoH/mTargetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentImagePath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    private String convertToServerDate(String input)
    {
        if(input.equals(""))
            return "";

        // parse the date fields if any
        SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
        Date testDate = null;
        try {
            testDate = sdf.parse(input);
        } catch(Exception ex){
            Log.wtf(this.getClass().getSimpleName(), "Failed to parse the input date");
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        String newFormat = formatter.format(testDate);

        return newFormat;
    }

    private void createShareable()
    {
        Shareable s = new Shareable();
        s.shar_name = mItemTitleText.getText().toString();
        s.description = mDescriptionText.getText().toString();
        s.start_date = mStartDateText.getText().toString();
        s.end_date = mEndDateText.getText().toString();

        // Reset errors.
        mItemTitleText.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid shareable name
        if (TextUtils.isEmpty(s.shar_name)) {
            mItemTitleText.setError(getString(R.string.error_invalid_sharable_name));
            focusView = mItemTitleText;
            cancel = true;
        }

        // Check for a valid shareable description
        if (TextUtils.isEmpty(s.description)) {
            mDescriptionText.setError(getString(R.string.error_invalid_description));
            if(focusView == null)
                focusView = mItemTitleText;
            cancel = true;
        }

        // Check for a valid shareable image
        if (mImageFile == null) {
            Toast failToast = Toast.makeText(getApplicationContext(),
                    getText(R.string.error_missing_sharable_pic),
                    Toast.LENGTH_SHORT);
            failToast.show();

            if(focusView == null)
                focusView = mItemTitleText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return;
        }

        RequestParams params = new RequestParams();

        // parse the date fields if any
        if(!TextUtils.isEmpty(s.start_date))
        {
            s.start_date = convertToServerDate(s.start_date);
            params.put("start_date", s.start_date);
        }

        if(!TextUtils.isEmpty(s.end_date))
        {
            s.end_date = convertToServerDate(s.end_date);
            params.put("end_date", s.end_date);
        }

        try {
            params.put("picture", new FileInputStream(mImageFile));
        } catch (FileNotFoundException e) {
            Log.wtf(this.getClass().getSimpleName(), "Image file pulled out from under us");
            resetImage();
            return;
        }

        showProgress(true);

        params.put("shar_name", s.shar_name);
        params.put("description", s.description);

        String endpoint;
        if (mCreateType == Constants.CreateType.OFFER)
            endpoint = "/makeshareableoffer";
        else
            endpoint = "/makeshareablerequest";

        final Shareable shareable = s;

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
                i.putExtra(Constants.SHAREABLE, shareable);
                i.putExtra("data", mBitmapThumbnail);
                /*
                i.putExtra("title", shareable_name);
                i.putExtra("description", description);
                */
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
