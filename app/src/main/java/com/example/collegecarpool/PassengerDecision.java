package com.example.collegecarpool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PassengerDecision extends AppCompatActivity {

    /**Define UI Elements**/
    private Button btnDriver, btnPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_decision);

        btnDriver = (Button)findViewById(R.id.driver);
        btnPassenger = (Button)findViewById(R.id.passenger);

        btnDriver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                driverScreen();
            }
        });

        btnPassenger.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                passengerScreen();
            }
        });
    }

    private void passengerScreen() {
        Intent passengerActivity = new Intent(PassengerDecision.this, PassengerActivity.class);
        startActivity(passengerActivity);
    }

    private void driverScreen() {
        Intent driverActivity = new Intent(PassengerDecision.this, DriverActivity.class);
        startActivity(driverActivity);
    }
}
