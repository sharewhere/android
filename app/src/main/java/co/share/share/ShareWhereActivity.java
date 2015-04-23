package co.share.share;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Toast;

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

        if (NetworkService.getCookies() == null) {
            //System.out.println("No cookie store found, creating new one");
            // before anything setup our network cookie storage
            PersistentCookieStore cookies = new PersistentCookieStore(this);
            NetworkService.getInstance().setCookieStore(cookies);
        }
        else
        {
            //System.out.println("Found cookies!");
            //System.out.println(getCookies().getCookies());
        }
    }

    public void logout(String message)
    {
        Toast logoutToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        logoutToast.show();

        logout();
    }

    public void logout()
    {
        PersistentCookieStore cookies = NetworkService.getCookies();

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