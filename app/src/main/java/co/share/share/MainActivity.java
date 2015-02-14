package co.share.share;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import co.share.share.util.ItemAdapter;
import co.share.share.views.FloatingActionButton;


public class MainActivity extends ActionBarActivity implements FloatingActionButton.OnCheckedChangeListener {
    private final String TAG = this.getClass().getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SearchView mSearchView;
    private static final int SPAN_COUNT = 2; // num columns in grid
    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        // Set up action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);


        // floating action button
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab1.setOnCheckedChangeListener(this);


        // set up recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.item_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);


        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // test data
        String[] myDataset = new String[20];
        for(int i = 0; i < 20; i++)
            myDataset[i] = "item " + i;

        // specify an adapter (see also next example)
        mAdapter = new ItemAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);

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
                return true;
            case R.id.action_search:
                mSearchView.setIconified(false);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(FloatingActionButton fabView, boolean isChecked) {
        // When a FAB is toggled, log the action.
        switch (fabView.getId()){
            case R.id.fab_1:
                //Log.d(TAG, String.format("FAB 1 was %s.", isChecked ? "checked" : "unchecked"));
                Intent intent = new Intent(this, ItemCreateActivity.class);
                startActivity(intent);
                break;
            case R.id.fab_image:
                Log.i("fab", "so fab");
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                break;
            default:
                break;
        }
    }


    private class ActiveItemClickListener implements AbsListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }
}
