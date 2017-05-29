package com.example.user.pocotest.registration;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.user.pocotest.CongratulationActivity;
import com.example.user.pocotest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RegistrationThird extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText mCityView, mPostalCodeView;
    private String country, email, password, city, postalCode;
    private View mProgressView;
    private View mLoginFormView;
    private int CALLS_TO_SERVER = 0;
    private final String LOG = "OKHTTP3";
    private final String url = "https://poco-test.herokuapp.com/addUser";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        email = intent.getStringExtra("Email");
        password = intent.getStringExtra("Password");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_third);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("3/3   Sign up               "+getEmailFirstFiveLettes());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setUpSpinner();
        mCityView = (EditText)findViewById(R.id.signUpCity);
        mPostalCodeView = (EditText)findViewById(R.id.signUpPostalCode);
        mProgressView = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.login_form);
        Button continueButton = (Button)findViewById(R.id.signUpThirdContinue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishRegistration();
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

    private void finishRegistration() {
        mCityView.setError(null);
        mPostalCodeView.setError(null);

        // Store values at the time of the activity_login attempt.
        city = mCityView.getText().toString();
        postalCode = mPostalCodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(city)) {
            mCityView.setError(getString(R.string.error_field_required));
            focusView = mCityView;
            cancel = true;
        }
        if (TextUtils.isEmpty(postalCode)) {
            mPostalCodeView.setError(getString(R.string.error_field_required));
            if (TextUtils.isEmpty(city)) {
                focusView = mCityView;
                cancel = true;
            }else {
                focusView = mPostalCodeView;
                cancel = true;
            }
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            new FeedTask().execute();

        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public class FeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showProgress(true);
                }
            });
            Log.d(LOG, "Post called");
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json;charset=utf-8");
            JSONObject actualData = new JSONObject();
            try {
                actualData.put("email", email);
                actualData.put("password", password);
                actualData.put("country", formatCountry(country));
                Log.d("strana", formatCountry(country));
                actualData.put("city", city);
                actualData.put("postal_code", postalCode);
            } catch (JSONException e) {
                Log.d(LOG, "RequestBody JSONException");
                e.printStackTrace();
            }
            RequestBody postData = RequestBody.create(JSON, actualData.toString());
            Log.d(LOG, "RequestBody created");
            Request request = new Request.Builder()
                    .url(url)
                    .post(postData)
                    .build();
            try {
                okhttp3.Response response = client.newCall(request).execute();
                Log.d(LOG, "Request done, got the response");
                String result = response.body().string();
                JSONObject jsonObject = new JSONObject(result);
                handleResult(jsonObject);
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

    }

    private String formatCountry(String country) {
        if (country.equals("Estonia")) {country = "ET";}
        else if (country.equals("Latvia")) {country = "LV";}
        else if (country.equals("Lithuania")) {country = "LT";}
        else if (country.equals("Finland")) {country = "FI";}
        else if (country.equals("Russia")) {country = "RU";}
        return country;
    }

    private void handleResult(JSONObject jsonObject) {
        try {
            Intent intent;
            if (jsonObject.has("error")) {
                if (jsonObject.get("error").toString().equals("err.password.too.short")) {
                    intent = new Intent(RegistrationThird.this, RegistrationSecond.class);
                    intent.putExtra("error", "password is too short!");
                    intent.putExtra("email", email);
                    startActivity(intent);
                } else if (jsonObject.get("error").toString().equals("err.wrong.credentials")) {
                    intent = new Intent(RegistrationThird.this, RegistrationSecond.class);
                    intent.putExtra("error", "wrong credentials!");
                    startActivity(intent);

                } else if (jsonObject.get("error").toString().equals("err.user.exists")) {
                    intent = new Intent(RegistrationThird.this, RegistrationSecond.class);
                    intent.putExtra("error", "user already exists!");
                    startActivity(intent);
                } else if (jsonObject.get("error").toString().equals("err.timeout")) {
                    if(CALLS_TO_SERVER<5){
                        CALLS_TO_SERVER++;
                        new FeedTask().execute();
                    }else {
                        intent = new Intent(RegistrationThird.this,RegistrationSecond.class);
                        intent.putExtra("error","connection problem!");
                        startActivity(intent);
                    }
                }
            } else if (jsonObject.has("data")) {
                if (jsonObject.get("data").toString().equals("success")) {
                    intent = new Intent(RegistrationThird.this, CongratulationActivity.class);
                    intent.putExtra("message","Congratulations on the successful registration!");
                    Log.d(LOG, "success");
                    startActivity(intent);
                }
            }
        } catch (JSONException e) {
            Log.d(LOG, "Handle result JSONException");
            e.printStackTrace();
        }
    }

    private void setUpSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.countries_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private String getEmailFirstFiveLettes() {
        if(email.length()<5){
            return email;
        }else {
            return String.valueOf(email.subSequence(0,5));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        country = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}