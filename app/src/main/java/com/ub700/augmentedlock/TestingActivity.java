package com.ub700.augmentedlock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TestingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        EditText editText = findViewById(R.id.editText);
        editText.setOnEditorActionListener((TextView textView, int id, KeyEvent keyEvent) -> {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    startNextActivity(editText);
                    return true;
                }
                return false;
        });

        Button button = findViewById(R.id.button);
        button.setOnClickListener((view) -> {
            startNextActivity(editText);
        });
    }

    private void startNextActivity(EditText editText) {
        Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
        intent.putExtra("Key", editText.getText().toString());
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
