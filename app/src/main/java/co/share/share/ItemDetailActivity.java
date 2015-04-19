package co.share.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import co.share.share.models.Shareable;
import co.share.share.models.Transaction;
import co.share.share.net.NetworkService;
import co.share.share.net.ShareWhereRespHandler;
import co.share.share.util.Constants;
import co.share.share.util.UserProfile;
import co.share.share.views.NotifyScrollView;


public class ItemDetailActivity extends ActionBarActivity implements NotifyScrollView.Callback {

    private NotifyScrollView mNotifyScrollView;

    private FrameLayout mImageFrameLayout;

    private LinearLayout mContentDetailLayout;
    private ListView mTransactionList;

    private ImageView mImageView;
    private TextView mDescription;
    private TextView mCreator;

    private Shareable mSharable = null;
    private List<Transaction> mTransactions;

    private boolean didUserCreate = false;
    private boolean shouldDisableAction = false;

    private LinearLayout mContentLinearLayout;

    private LinearLayout mToolbarLinearLayout;
    private Toolbar mToolbar;
    private FloatingActionButton mButton;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // view matching
        mNotifyScrollView = (NotifyScrollView) findViewById(R.id.notify_scroll_view);

        mImageFrameLayout = (FrameLayout) findViewById(R.id.image_frame_layout);
        mContentDetailLayout = (LinearLayout) findViewById(R.id.content_detail_layout);
        mTransactionList = (ListView) findViewById(R.id.transaction_list);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mDescription = (TextView) findViewById(R.id.description);
        mCreator = (TextView) findViewById(R.id.creator);

        mContentLinearLayout = (LinearLayout) findViewById(R.id.content_linear_layout);

        mToolbarLinearLayout = (LinearLayout) findViewById(R.id.toolbar_linear_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);

        mButton = (FloatingActionButton) findViewById(R.id.action_item);

        if(getIntent().getExtras() != null) {
            Bitmap b = (Bitmap) getIntent().getExtras().get("data");
            if (b != null) /* TODO dont do it this way from the deal */
                mImageView.setImageBitmap(b);
            Bundle extras = getIntent().getExtras();

            mSharable = (Shareable) extras.getSerializable(Constants.SHAREABLE);

            /* TODO Rework this so that I dont have to load placeholder initially */
            mImageView.setImageDrawable(getResources().getDrawable(R.drawable.placeholder));

            //ImageLoader.getInstance().displayImage(NetworkService.getImageURL(pic_name), mImageView);
            if (mSharable.shar_pic_name != null && !mSharable.shar_pic_name.isEmpty()) {
                ImageLoader.getInstance().loadImage(NetworkService.getImageURL(mSharable.shar_pic_name), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // Do whatever you want with Bitmap
                        mImageView.setImageBitmap(loadedImage);
                    }
                });
            }
            getSupportActionBar().setTitle(mSharable.shar_name);
            mCreator.setText("Created by " + mSharable.username);
            mDescription.setText(mSharable.description);
        }

        /* set up button to do a deal based on the current deal */
        if (mSharable != null) {

            // check if user created item dont show item if user created it
            if (mSharable.username.equals(UserProfile.getInstance().getUserName())) {
                mButton.setVisibility(View.INVISIBLE);
                didUserCreate = true;
            }

            // Tells us if offer or request
            switch (mSharable.state_name) {
            case Constants.REQ:
            case Constants.REQ_OFR:
                Log.i(ItemDetailActivity.class.getSimpleName(), "THIS IS A REQUEST");
                mButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_offer));
                mButton.setOnClickListener(offerClickListener);
                break;
            case Constants.OFR:
            case Constants.OFR_REQ:
                Log.i(ItemDetailActivity.class.getSimpleName(), "THIS IS AN OFFER");
                mButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_request));
                mButton.setOnClickListener(requestClickListener);
                break;
            }

            NetworkService.get("/viewreqoffshareable?shar_id=" + mSharable.shar_id, null, new ShareWhereRespHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject resp) {
                    if (logoutIfInvalidCookie(resp, ItemDetailActivity.this))
                        return;

                    try {
                        boolean success = resp.getBoolean("success");
                        if (!success)
                            return;

                        // Get sharable were looking at we dont really need this now
                        //JSONObject shar = resp.getJSONObject("shareable");
                        //Shareable s = gson.fromJson(shar.toString(), Shareable.class);

                        // Created by user if list other user otherwise

                        if (!resp.isNull("transactions")) {
                            JSONArray ts = resp.getJSONArray("transactions");
                            Type listType = new TypeToken<List<Transaction>>() {
                            }.getType();
                            mTransactions = gson.fromJson(ts.toString(), listType);
                        }
                        else if (!resp.isNull("transaction")) {
                            JSONObject transaction = resp.getJSONObject("transaction");
                            Transaction t = gson.fromJson(transaction.toString(), Transaction.class);
                            shouldDisableAction = true;
                        }

                    } catch (JSONException e) {
                        Log.wtf(this.getClass().getSimpleName(), "JSON Exception at viewoffreq");
                    }

                    if (shouldDisableAction) {
                        mButton.setEnabled(false);
                    }

                    // show transactions
                    if (didUserCreate) {
                        mTransactionList.setVisibility(View.VISIBLE);
                        mContentDetailLayout.setVisibility(View.INVISIBLE);
                        if (mTransactions != null)
                            mTransactionList.setAdapter(new TransactionAdapter(ItemDetailActivity.this, mTransactions));
                    }
                }
                @Override
                public void onFinish() {
                }

            });
        }

        // more setup
        setupNotifyScrollView();
        setupToolbar();

    }

    public class TransactionAdapter extends ArrayAdapter<Transaction> {
        private final Context context;
        private final List<Transaction> transactions;

        public TransactionAdapter(Context context, List<Transaction> transactions) {
            super(context, R.layout.transaction_list_item, transactions);
            this.context = context;
            this.transactions = transactions;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.transaction_list_item, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.transaction_title);
            Transaction t = transactions.get(position);

            textView.setText(t.borrower);

            return rowView;
        }
    }

    private View.OnClickListener offerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // show modal with thing
            RequestParams params = new RequestParams();
            params.add("shar_id", "" + mSharable.shar_id);
            //params.add("description", "I'll decide later");

            NetworkService.post("/offeronrequest", params, new ShareWhereRespHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                }
            });

        }
    };


    private View.OnClickListener requestClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RequestParams params = new RequestParams();
            params.add(Constants.SHAR_ID, "" + mSharable.shar_id);
            //params.add("description", "I'll decide later");

            NetworkService.post("/requestonoffer", params, new ShareWhereRespHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                }
            });
        }
    };


    private void setupNotifyScrollView() {
        mNotifyScrollView.setCallback(this);

        ViewTreeObserver viewTreeObserver = mNotifyScrollView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // get size
                    int toolbarLinearLayoutHeight = mToolbarLinearLayout.getHeight();
                    int imageHeight = mImageView.getHeight();

                    // adjust image frame layout height
                    ViewGroup.LayoutParams layoutParams = mImageFrameLayout.getLayoutParams();
                    if (layoutParams.height != imageHeight) {
                        layoutParams.height = imageHeight;
                        mImageFrameLayout.setLayoutParams(layoutParams);
                    }

                    // adjust top margin of content linear layout
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mContentLinearLayout.getLayoutParams();
                    if (marginLayoutParams.topMargin != toolbarLinearLayoutHeight + imageHeight) {
                        marginLayoutParams.topMargin = toolbarLinearLayoutHeight + imageHeight;
                        mContentLinearLayout.setLayoutParams(marginLayoutParams);
                    }

                    // call onScrollChange to update initial properties.
                    onScrollChanged(0, 0, 0, 0);
                }
            });
        }
    }

    private void setupToolbar() {
        // set ActionBar as Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
        // get scroll y
        int scrollY = mNotifyScrollView.getScrollY();

        // calculate new y (for toolbar translation)
        float newY = Math.max(mImageView.getHeight(), scrollY);

        // translate toolbar linear layout and image frame layout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mToolbarLinearLayout.setTranslationY(newY);
            mImageFrameLayout.setTranslationY(scrollY * 0.5f);
        } else {
            ViewCompat.setTranslationY(mToolbarLinearLayout, newY);
            ViewCompat.setTranslationY(mImageFrameLayout, scrollY * 0.5f);
            ViewCompat.setTranslationY(mButton, newY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showTransactionList() {

    }

}
