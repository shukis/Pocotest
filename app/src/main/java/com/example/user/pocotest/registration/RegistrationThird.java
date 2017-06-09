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
import okhttp3.Response;

public class RegistrationThird extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText mCityView, mPostalCodeView;
    private String country, email, password, city, postalCode;
    private View mProgressView;
    private View mLoginFormView;
    private int CALLS_TO_SERVER = 0;
    private final String LOG = "OKHTTP3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        email = intent.getStringExtra("Email");
        password = intent.getStringExtra("Password");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_third);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                String title = "3/3   Sign up               " + getEmailFirstFiveLettes();
                getSupportActionBar().setTitle(title);
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setUpSpinner();
        mCityView = (EditText) findViewById(R.id.signUpCity);
        mPostalCodeView = (EditText) findViewById(R.id.signUpPostalCode);
        mProgressView = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.login_form);
        Button continueButton = (Button) findViewById(R.id.signUpThirdContinue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
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

    private void attemptSignUp() {
        mCityView.setError(null);
        mPostalCodeView.setError(null);

        city = mCityView.getText().toString();
        postalCode = mPostalCodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

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
            } else {
                focusView = mPostalCodeView;
                cancel = true;
            }
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            new FinishRegistration().execute();

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
            InputMethodManager inputMethodManager = (InputMethodManager)
                    getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
                    .getWindowToken(), 0);
        }
    }

    private class FinishRegistration extends AsyncTask<String, Void, String> {
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
        String formattedCountry = formatCountry(country);
        try {
            actualData.put("email", email);
            actualData.put("password", password);
            actualData.put("country", formattedCountry);
            actualData.put("city", city);
            actualData.put("postal_code", postalCode);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return actualData;
    }

    private Request createRequest(JSONObject actualData) {
        String url = "https://poco-test.herokuapp.com/addUser";
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
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

    private String formatCountry(String country) {
        switch (country) {
            case "Estonia":
                country = "ET";
                break;
            case "Latvia":
                country = "LV";
                break;
            case "Lithuania":
                country = "LT";
                break;
            case "Finland":
                country = "FI";
                break;
            case "Russia":
                country = "RU";
                break;
        }
        return country;
    }

    private void parseResponse(JSONObject jsonObject) {
        try {
            if (jsonObject.has("error")) {
                handleErrorResponse(jsonObject.get("error").toString());
            } else if (jsonObject.has("data")) {
                if (jsonObject.get("data").toString().equals("success")) {
                    Intent intent = new Intent(RegistrationThird.this, CongratulationActivity.class);
                    intent.putExtra("message", "Congratulations on the successful registration!");
                    Log.d(LOG, "success");
                    startActivity(intent);
                }
            }
        } catch (JSONException e) {
            Log.d(LOG, "Parse response JSONException");
            e.printStackTrace();
        }
    }

    private void handleErrorResponse(String error) {
        Intent intent;
        switch (error) {
            case "err.password.too.short":
                intent = new Intent(RegistrationThird.this, RegistrationSecond.class);
                intent.putExtra("error", "password is too short!");
                intent.putExtra("email", email);
                startActivity(intent);
                break;
            case "err.wrong.credentials":
                intent = new Intent(RegistrationThird.this, RegistrationSecond.class);
                intent.putExtra("error", "wrong credentials!");
                startActivity(intent);
                break;
            case "err.user.exists":
                intent = new Intent(RegistrationThird.this, RegistrationSecond.class);
                intent.putExtra("error", "user already exists!");
                startActivity(intent);
                break;
            case "err.timeout":
                if (CALLS_TO_SERVER < 5) {
                    CALLS_TO_SERVER++;
                    new FinishRegistration().execute();
                } else {
                    intent = new Intent(RegistrationThird.this, RegistrationSecond.class);
                    intent.putExtra("error", "connection problem!");
                    startActivity(intent);
                }
                break;
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
        if (email.length() < 5) {
            return email;
        } else {
            return String.valueOf(email.subSequence(0, 5));
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
