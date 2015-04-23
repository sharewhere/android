package co.share.share.net;

import android.app.Activity;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import co.share.share.R;
import co.share.share.ShareWhereActivity;

public class ShareWhereRespHandler extends JsonHttpResponseHandler {
    public boolean isLoggedIn(JSONObject resp)
    {
        try {
            boolean valid = resp.getBoolean("cookieValid");

            return valid;
        } catch(JSONException e) {
            return true;
        }
    }

    public boolean logoutIfInvalidCookie(JSONObject resp, Activity act)
    {
        if(act != null && !isLoggedIn(resp)) {
            ((ShareWhereActivity)act).logout(act.getResources().getString(R.string.logout_session_expired));
            return true;
        }
        else
        {
            return false;
        }
    }
}
