package com.ub700.augmentedlock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        boolean authStatus = intent.getExtras().getBoolean("authStatus");
        setContentView(R.layout.activity_welcome);

        TextView textView = findViewById(R.id.textView);
        if (authStatus) {
            textView.setText(R.string.welcome);
        } else {
            textView.setText(R.string.manyRetires);
        }
    }
}
