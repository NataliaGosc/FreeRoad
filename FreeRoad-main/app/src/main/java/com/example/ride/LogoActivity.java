package com.example.ride;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LogoActivity extends AppCompatActivity {

    private final int TIMEOUT = 5000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent helloIntent = new Intent(LogoActivity.this, WeatherActivity.class);
                startActivity(helloIntent);
                finish();
            }
        }, TIMEOUT);



    }
}