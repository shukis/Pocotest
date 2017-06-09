package com.example.user.pocotest.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.user.pocotest.R;
import com.example.user.pocotest.activities.registration.RegistrationFirstActivity;

public class WelcomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Button logIn = (Button) findViewById(R.id.welcomeLogIn);
        Button signUp = (Button) findViewById(R.id.welcomeSignUp);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActivity("login");
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActivity("registration");
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showActivity(String action) {
        if (action.equals("login")) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (action.equals("registration")) {
            startActivity(new Intent(this, RegistrationFirstActivity.class));
        }
    }
}
