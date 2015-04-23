package co.share.share.net;

import android.app.Service;
import android.content.Context;
import android.net.Network;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HttpContext;

import java.util.List;

// cookie name is connect.sid

public class NetworkService {

    public static String baseURL = "http://hernan.de:8000"; // XXX: HTTPS PLEASE
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

    public static PersistentCookieStore getCookies() {
        HttpContext ctx = NetworkService.getInstance().getHttpContext();
        PersistentCookieStore cookies = (PersistentCookieStore) ctx.getAttribute(ClientContext.COOKIE_STORE);

        return cookies;
    }

    public static boolean isLoggedin()
    {
        // TODO: check network
        PersistentCookieStore storage = getCookies();
        List<Cookie> cookies = storage.getCookies();

        for(Cookie c : cookies)
        {
            if(c.getName().equals("connect.sid")) {
                return true;
            }
        }

        return false;
    }

    public static String getImageURL(String imageName)
    {
        return getAbsURL("/images/" + imageName);
    }

    private static String getAbsURL(String rel) {
        return baseURL + rel;
    }

    public static void setEndpoint(String endpoint) {
        baseURL = endpoint;
    }
}
