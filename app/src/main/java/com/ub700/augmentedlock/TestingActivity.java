package com.ub700.augmentedlock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

public class TestingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        EditText keyText = findViewById(R.id.editText);
        EditText lengthText = findViewById(R.id.editText1);
        EditText retriesText = findViewById(R.id.editText2);
        Switch midAir = findViewById(R.id.switch1);

        Button button = findViewById(R.id.button);
        button.setOnClickListener((view) -> {
            startNextActivity(keyText, lengthText, retriesText, midAir.isChecked());
        });
    }

    private void startNextActivity(EditText keyText, EditText lengthText, EditText retriesText, Boolean midAir) {
        Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
        if (keyText.getText() == null || keyText.getText().toString().length() == 0) {
            keyText.setError("Please enter a key!");
            return;
        }
        intent.putExtra("key", keyText.getText().toString());

        if (lengthText.getText() != null && lengthText.getText().toString().length() > 0) {
            intent.putExtra("length", Integer.parseInt(lengthText.getText().toString()));
        }
        if (retriesText.getText() != null && retriesText.getText().toString().length() > 0) {
            intent.putExtra("retries", Integer.parseInt(retriesText.getText().toString()));
        }
        intent.putExtra("midAir", midAir);

        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
