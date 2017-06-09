package com.example.user.pocotest.activities.registration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.pocotest.R;
import com.example.user.pocotest.activities.LoginActivity;

public class RegistrationSecondActivity extends AppCompatActivity {
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView, mConfirmPasswordView;
    private CheckBox terms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_second);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("2/3   Sign up");
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mEmailView = (AutoCompleteTextView) findViewById(R.id.signUpEmail);
        mPasswordView = (EditText) findViewById(R.id.signUpPassword);
        mConfirmPasswordView = (EditText) findViewById(R.id.signUpConfirmPassword);
        terms = (CheckBox) findViewById(R.id.terms);
        checkErrors();

        Button continueButton = (Button) findViewById(R.id.signUpSecondContinue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchNextStep();
            }
        });
    }

    private void checkErrors() {
        Intent intent = getIntent();
        if (intent.hasExtra("error")) {
            String error = intent.getStringExtra("error");
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            if (intent.hasExtra("email")) {
                String email = intent.getStringExtra("email");
                View focusView;
                mEmailView.setText(email);
                focusView = mPasswordView;
                focusView.requestFocus();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, RegistrationFirstActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, RegistrationFirstActivity.class));

    }

    private void switchNextStep() {
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
        terms.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmedPassword = mConfirmPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!new LoginActivity().isEmailValid(email)) {
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
        } else if (!arePasswordsCorrect(password, confirmedPassword)) {
            mPasswordView.setError(getString(R.string.error_passwords_are_not_same));
            mPasswordView.setText("");
            mConfirmPasswordView.setText("");
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(confirmedPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_field_required));
            if (TextUtils.isEmpty(email)) {
                focusView = mEmailView;
                cancel = true;
            } else if (TextUtils.isEmpty(password)) {
                focusView = mPasswordView;
                cancel = true;
            } else {
                focusView = mConfirmPasswordView;
                cancel = true;
            }
        }
        if (!terms.isChecked()) {
            focusView = mEmailView;
            Toast.makeText(this, getString(R.string.error_accept_terms), Toast.LENGTH_SHORT)
                    .show();
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            Intent intent = new Intent(this, RegistrationThirdActivity.class);
            intent.putExtra("Email", email);
            intent.putExtra("Password", password);
            startActivity(intent);
        }
    }

    private boolean arePasswordsCorrect(String password, String confirmedPassword) {
        return password.equals(confirmedPassword);
    }
}
