package co.share.share;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import co.share.share.net.NetworkService;
import co.share.share.views.FloatingActionButton;
import co.share.share.views.NotifyScrollView;


public class ItemDetailActivity extends ActionBarActivity implements NotifyScrollView.Callback {

    private NotifyScrollView mNotifyScrollView;

    private FrameLayout mImageFrameLayout;
    private ImageView mImageView;
    private TextView mDescription;

    private LinearLayout mContentLinearLayout;

    private LinearLayout mToolbarLinearLayout;
    private Toolbar mToolbar;
    private FloatingActionButton mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // view matching
        mNotifyScrollView = (NotifyScrollView) findViewById(R.id.notify_scroll_view);

        mImageFrameLayout = (FrameLayout) findViewById(R.id.image_frame_layout);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mDescription = (TextView) findViewById(R.id.description);

        mContentLinearLayout = (LinearLayout) findViewById(R.id.content_linear_layout);

        mToolbarLinearLayout = (LinearLayout) findViewById(R.id.toolbar_linear_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);

        //mButton = (FloatingActionButton) findViewById(R.id.borrow);

        if(getIntent().getExtras() != null) {
            Bitmap b = (Bitmap) getIntent().getExtras().get("data");
            if (b != null) /* TODO dont do it this way from the deal */
                mImageView.setImageBitmap(b);
            String name = getIntent().getExtras().getString("shar_name");
            String pic_name = getIntent().getExtras().getString("shar_pic_name");
            String desc = getIntent().getExtras().getString("shar_desc");

            /* TODO Rework this so that I dont have to load placeholder initially */
            mImageView.setImageDrawable(getResources().getDrawable(R.drawable.placeholder));

            //ImageLoader.getInstance().displayImage(NetworkService.getImageURL(pic_name), mImageView);
            if (pic_name != null && !pic_name.isEmpty()) {
                ImageLoader.getInstance().loadImage(NetworkService.getImageURL(pic_name), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // Do whatever you want with Bitmap
                        mImageView.setImageBitmap(loadedImage);
                    }
                });
            }
            getSupportActionBar().setTitle(name);
            mDescription.setText(desc);
        }

        // more setup
        setupNotifyScrollView();
        setupToolbar();

    }


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

}
