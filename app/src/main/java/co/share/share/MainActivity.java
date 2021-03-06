package co.share.share;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import com.astuetz.PagerSlidingTabStrip;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.util.ArrayList;

import co.share.share.fragments.OffersFragment;
import co.share.share.fragments.RequestsFragment;
import co.share.share.net.NetworkService;
import co.share.share.util.Constants;
import co.share.share.util.UserProfile;

public class MainActivity extends ShareWhereActivity {
    private final String TAG = this.getClass().getSimpleName();
    private SearchView mSearchView;

    private MediaPlayer mMediaPlayer;
    private ArrayList<ViewSwitcher> mMlgSwitchedViews;
    private boolean mMlgActive;

    @Override
    protected void onResume()
    {
        super.onResume();

        // Get username
        String username = PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.SHAREWHERE_USERNAME, null);
        UserProfile.getInstance().setUserName(username);

        // check if we are still logged in!
        if(!NetworkService.isLoggedin())
            doLogin();
    }

    @Override
    protected void onDestroy()
    {
        mMediaPlayer.release();
        mMediaPlayer = null;

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check to see if the cookie exists, otherwise login
        if(!NetworkService.isLoggedin())
            doLogin();

        if(mMediaPlayer == null)
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.spooky);

        // initialize image streaming
        // Create global configuration and initialize ImageLoader with this config
        if(!ImageLoader.getInstance().isInited()) {

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .defaultDisplayImageOptions(defaultOptions)
                    .diskCacheSize(50 * 1024 * 1024)
                    .memoryCacheSize(20 * 1024 * 1024)
                    //.writeDebugLogs()
                    .build();
            ImageLoader.getInstance().init(config);
        }

        setContentView(R.layout.activity_main);

        // Set up action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setIndicatorColor(getResources().getColor(R.color.color_accent));
        tabs.setIndicatorHeight(12);
        tabs.setDividerColor(getResources().getColor(R.color.color_primary_dark));
        tabs.setTextColor(Color.WHITE);
        tabs.setShouldExpand(true);
        tabs.setViewPager(pager);

        // floating action button
        findViewById(R.id.action_offer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ItemCreateActivity.class);
                intent.putExtra(Constants.CREATE_TYPE, Constants.CreateType.OFFER);
                startActivity(intent);
            }
        });

        findViewById(R.id.action_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ItemCreateActivity.class);
                intent.putExtra(Constants.CREATE_TYPE, Constants.CreateType.REQUEST);
                startActivity(intent);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        Log.d(TAG, "Got result " + resultCode + ", request "  + requestCode);
        if(requestCode == 50)
        {
            Log.d("test", "Got result " + resultCode);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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
                //mlg_active();
                Intent s = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(s);
                return true;
            case R.id.action_illuminati:
                mlg_active();
                return true;
            case R.id.action_logout:
                logout();
                return true;
            case R.id.action_search:
                mSearchView.setIconified(false);
                return true;
            case R.id.action_profile:
                Intent i = new Intent(this, ProfileActivity.class);
                startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    private void mlg_active()
    {
        if(mMlgActive)
            return;

        mMlgActive = true;

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        FragmentStatePagerAdapter a = (FragmentStatePagerAdapter) pager.getAdapter();
        final OffersFragment offers = (OffersFragment) a.instantiateItem(pager, 0);
        final RequestsFragment req = (RequestsFragment) a.instantiateItem(pager, 1);

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mlgSwitchViewsBack(mMlgSwitchedViews);
                mMlgActive = false;
            }
        });

        mMediaPlayer.seekTo(0); // heat it up
        mMediaPlayer.start();

        mMlgSwitchedViews = mlgSwitchViews(offers.getRecycler());
        mMlgSwitchedViews.addAll(mlgSwitchViews(req.getRecycler()));
    }

    private ArrayList<ViewSwitcher> mlgSwitchViews(RecyclerView recycler)
    {
        ArrayList<ViewSwitcher> switched = new ArrayList<>(recycler.getChildCount());

        for(int i = 0; i < recycler.getChildCount(); i++) {
            View v = recycler.getChildAt(i);

            if(v == null)
                continue;

            v = v.findViewById(R.id.list_item_switch);

            if(v != null) {
                ViewSwitcher sw = (ViewSwitcher)v;
                switched.add(sw);
                sw.showNext();
            }
        }

        return switched;
    }

    private void mlgSwitchViewsBack(ArrayList<ViewSwitcher> switched)
    {
        for(ViewSwitcher v : switched)
            if(v != null)
                v.setDisplayedChild(0);

        switched.clear();
    }

    private void doLogin()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {

        private final String[] TITLES = {"Offers", "Requests"};

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return new OffersFragment();
            else
                return new RequestsFragment();

        }
    }
}
