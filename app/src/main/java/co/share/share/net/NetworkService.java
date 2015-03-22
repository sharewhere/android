package co.share.share.net;

import android.app.Service;
import android.content.Context;
import android.net.Network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class NetworkService {

    public final static String baseURL = "http://hernan.de:8000"; // XXX: HTTPS PLEASE
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String rel, RequestParams param, AsyncHttpResponseHandler handler) {
        client.get(getAbsURL(rel), param, handler);
    }

    public static void post(String rel, RequestParams param, AsyncHttpResponseHandler handler) {
        client.post(getAbsURL(rel), param, handler);
    }

    private static String getAbsURL(String rel) {
        return baseURL + rel;
    }
}
