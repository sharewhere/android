package co.share.share;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HttpContext;

import java.net.URL;
import java.util.List;

import co.share.share.net.NetworkService;

public class ShareWhereActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getCookies() == null) {
            System.out.println("No cookie store found, creating new one");
            // before anything setup our network cookie storage
            PersistentCookieStore cookies = new PersistentCookieStore(this);
            NetworkService.getInstance().setCookieStore(cookies);
        }
        else
        {
            System.out.println("Found cookies!");
            System.out.println(getCookies().getCookies());
        }
    }

    private PersistentCookieStore getCookies() {
        HttpContext ctx = NetworkService.getInstance().getHttpContext();
        PersistentCookieStore cookies = (PersistentCookieStore) ctx.getAttribute(ClientContext.COOKIE_STORE);

        return cookies;
    }

    public boolean isLoggedin()
    {
        PersistentCookieStore storage = getCookies();
        List<Cookie> cookies = storage.getCookies();

        for(Cookie c : cookies)
        {
            System.out.println("Cookie name " + c.getName());
            if(c.getName().equals("connect.sid")) {
                System.out.println("Logged in!");
                return true; // && c.getDomain() == (new URL("http://hernan.de:8000")))
            }
        }

        System.out.println("Not logged in (" + cookies.size() + ")");

        return false;
    }

    public void logout(String message)
    {
        logout();
    }

    public void logout()
    {
        PersistentCookieStore cookies = getCookies();

        if(cookies != null) {
            // start fresh
            cookies.clear();
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }
}