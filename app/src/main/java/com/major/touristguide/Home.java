package com.major.touristguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        createNewTrip();
    }

    public void createNewTrip() {
        Button createNewTripButton = (Button)findViewById(R.id.create_trip_button_home);
        createNewTripButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Home.this, MapsActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }
}
