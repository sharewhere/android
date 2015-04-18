package co.share.share.net;

import android.app.Service;
import android.content.Context;
import android.net.Network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

// cookie name is connect.sid

public class NetworkService {

    public final static String baseURL = "http://hernan.de:8000"; // XXX: HTTPS PLEASE
    //public final static String baseURL = "http://104.211.0.206"; // XXX: HTTPS PLEASE
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static AsyncHttpClient getInstance() {
        return client;
    }
    public static void get(String rel, RequestParams param, AsyncHttpResponseHandler handler) {
        client.get(getAbsURL(rel), param, handler);
    }

    public static void post(String rel, RequestParams param, AsyncHttpResponseHandler handler) {
        client.post(getAbsURL(rel), param, handler);
    }

    public static String getImageURL(String imageName)
    {
        return getAbsURL("/images/" + imageName);
    }

    private static String getAbsURL(String rel) {
        return baseURL + rel;
    }
}
