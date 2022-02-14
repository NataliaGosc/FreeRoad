package com.example.ride;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CityChangeActivity extends AppCompatActivity {
    private Button mapPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_change);
        final EditText editText=findViewById(R.id.searchCity);
        ImageView backButton=findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

    editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            String newCity= editText.getText().toString();
            Intent intent=new Intent(CityChangeActivity.this, WeatherActivity.class);
            intent.putExtra("City",newCity);
            startActivity(intent);

            return false;
        }
    });
        mapPage = findViewById(R.id.mapsbutton);
        mapPage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                openMapsActivity();
            }
        });
    }

    public void openMapsActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}