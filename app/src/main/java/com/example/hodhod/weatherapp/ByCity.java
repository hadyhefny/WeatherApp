package com.example.hodhod.weatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ByCity extends AppCompatActivity {

    private ImageButton backButton;
    private Button currentButton;
    private EditText cityEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_by_city);

        backButton = findViewById(R.id.back_button);
        cityEditText = findViewById(R.id.city_editText);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cityEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String cityName = cityEditText.getText().toString();
                Intent cityNameIntent = new Intent(ByCity.this, MainActivity.class);
                cityNameIntent.putExtra("cityName", cityName);
                finish();
                startActivity(cityNameIntent);
                if (cityName != null){
                    MainActivity.finishThis.finish();
                }
                return false;
            }

        });

        currentButton = findViewById(R.id.current_button);
        currentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ByCity.this, MainActivity.class);
                MainActivity.finishThis.finish();
                startActivity(intent);
                finish();

            }
        });


    }
}
