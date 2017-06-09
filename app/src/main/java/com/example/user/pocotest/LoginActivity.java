package com.example.user.pocotest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {

    private String email, password;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private final String LOG = "OKHTTP3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Login Activity");
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mProgressView = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.login_form);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private void attemptLogin() {

        mEmailView.setError(null);
        mPasswordView.setError(null);

        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            if (TextUtils.isEmpty(email)) {
                focusView = mEmailView;
                cancel = true;
            } else {
                focusView = mPasswordView;
                cancel = true;
            }
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            new FinishLogin().execute();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        hideSoftKeyboard();
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
                    .getWindowToken(), 0);
        }
    }

    private class FinishLogin extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showProgress(true);
                }
            });
            Log.d(LOG, "Post called");
            JSONObject actualData = createJsonObject();
            Request request = createRequest(actualData);
            return getResponse(request);
        }

    }

    private JSONObject createJsonObject() {
        JSONObject actualData = new JSONObject();
        try {
            actualData.put("email", email);
            actualData.put("password", password);
        } catch (JSONException e) {
            Log.d(LOG, "JSONException");
            e.printStackTrace();
        }
        return actualData;
    }

    private Request createRequest(JSONObject actualData) {
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        String url = "https://poco-test.herokuapp.com/login";
        RequestBody postData = RequestBody.create(JSON, actualData.toString());
        Log.d(LOG, "RequestBody created");
        return new Request.Builder()
                .url(url)
                .post(postData)
                .build();
    }

    private String getResponse(Request request) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.d(LOG, "Request done, got the response");
            String result = response.body().string();
            JSONObject jsonObject = new JSONObject(result);
            parseResponse(jsonObject);
            return result;
        } catch (IOException e) {
            Log.d(LOG, "Response IOException");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d(LOG, "Response JSONException");
            e.printStackTrace();
        }
        return null;
    }

    private void parseResponse(JSONObject jsonObject) {
        try {
            if (jsonObject.has("data")) {
                Intent intent = new Intent(LoginActivity.this, CongratulationActivity.class);
                intent.putExtra("message", "Congratulations on the successful login!");
                Log.d(LOG, "success");
                startActivity(intent);
            } else if (jsonObject.has("error")) {
                handleErrorResponse(jsonObject.get("error").toString());
            }
        } catch (JSONException e) {
            Log.d(LOG, "Format result JSONException");
            e.printStackTrace();
        }
    }

    private void handleErrorResponse(String error) {
        switch (error) {
            case "err.wrong.credentials":
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "wrong credentials!", Toast.LENGTH_LONG)
                                .show();
                    }
                });
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }


}



