package co.share.share.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import co.share.share.R;
import co.share.share.models.Shareable;
import co.share.share.net.NetworkService;
import co.share.share.util.ItemAdapter;

public class RequestsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final int SPAN_COUNT = 2; // num columns in grid

    private SwipeRefreshLayout mListViewContainer;

    private List<Shareable> requestList;
    Gson gson = new Gson();

    public static RequestsFragment newInstance(int index) {
        RequestsFragment f = new RequestsFragment();

        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        final View view = inflater.inflate(R.layout.fragment_requests, container, false);

        mListViewContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        mListViewContainer.setOnRefreshListener(this);
        mListViewContainer.setColorSchemeColors(
                Color.parseColor("#0091EA"),
                Color.parseColor("#00B0FF"),
                Color.parseColor("#40C4FF"),
                Color.parseColor("#80D8FF")
        );

        // set up recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.request_list_view);

        if(mRecyclerView == null)
            return null;

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);


        // use a grid layout manager
        mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);

        getRequests();

        return view;
    }

    // for MLG
    public RecyclerView getRecycler()
    {
        return mRecyclerView;
    }

    public void getRequests() {
        mListViewContainer.setRefreshing(true);
        NetworkService.get("/browserequests", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject resp) {
                try {
                    boolean success = resp.getBoolean("success");
                    if (!success)
                        return;

                    JSONArray requests = resp.getJSONArray("requests");

                    Type listType = new TypeToken<List<Shareable>>() {
                    }.getType();
                    requestList = gson.fromJson(requests.toString(), listType);

                    mAdapter = new ItemAdapter(requestList, getActivity());
                    mRecyclerView.setAdapter(mAdapter);

                } catch (JSONException e) {
                    Log.wtf(this.getClass().getSimpleName(), "JSON Exception at offers");
                }
                mListViewContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResp) {
                mListViewContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        getRequests();
    }

}
