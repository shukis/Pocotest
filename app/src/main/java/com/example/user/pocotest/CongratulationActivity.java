package com.example.user.pocotest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CongratulationActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulation);
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        TextView congratulationTextView = (TextView)findViewById(R.id.congratulationTextView);
        congratulationTextView.setText(message);
        Button congratulationButton = (Button)findViewById(R.id.congratulationButton);
        congratulationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CongratulationActivity.this,WelcomeActivity.class));
            }
        });
    }
    @Override
    public void onBackPressed(){
        startActivity(new Intent(this,WelcomeActivity.class));
        finish();

    }
}
