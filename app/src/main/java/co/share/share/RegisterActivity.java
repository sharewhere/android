package co.share.share;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import co.share.share.net.NetworkService;
import co.share.share.util.ProgressSpinner;


public class RegisterActivity extends ShareWhereActivity {


    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mEmailView;
    private EditText mZipView;

    private View mProgressView;
    private View mLoginFormView;

    private ProgressSpinner mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the register form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailView = (EditText) findViewById(R.id.email);
        mZipView = (EditText) findViewById(R.id.zipcode);

        // Initialize the progress spinner
        mLoginFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
        mProgress = new ProgressSpinner(getApplicationContext(), mProgressView, mLoginFormView);

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister() {

        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String zipcode = mZipView.getText().toString();
        String email = mEmailView.getText().toString();

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mZipView.setError(null);
        mEmailView.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));

            if(focusView == null)
                focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));

            if(focusView == null)
                focusView = mPasswordView;
            cancel = true;
        } else if(!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));

            if(focusView == null)
                focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            if(focusView == null)
                focusView = mEmailView;
            cancel = true;
        } else {
            if(!isEmailValid(email)) {
                mEmailView.setError(getString(R.string.error_invalid_email));

                if(focusView == null)
                    focusView = mEmailView;
                cancel = true;
            }
        }

        // Check for a valid zipcode, if present
        if (zipcode.length() != 5 && zipcode.length() > 0) {
            mZipView.setError(getString(R.string.error_invalid_zipcode));
            if(focusView == null)
                focusView = mZipView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return;
        }

        mProgress.show();

        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        params.put("zipcode", zipcode);
        params.put("email_address", email);

        //showProgress(true);

        NetworkService.post("/register", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject resp) {
                try {
                    Boolean text = resp.getBoolean("success");

                    Toast toast = Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_SHORT);
                    toast.show();

                    finish();
                } catch (JSONException exp) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Failed to code JSON", Toast.LENGTH_SHORT);
                    toast.show();
                }

                mProgress.hide();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResp) {
                System.out.println("Failure! Code: " + statusCode);
                Toast failToast = Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT);
                failToast.show();

                mProgress.hide();
            }
        });
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isEmailValid(String email) {
        // TODO: use regex
        return email.contains("@");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
