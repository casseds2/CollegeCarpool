package test.collegecarpool.alpha.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.Services.JSONDirectionsIntentService;

public class PlanJourneyActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    final static String TAG = "PlanJourneyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        startService(new Intent(PlanJourneyActivity.this, JSONDirectionsIntentService.class));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_journey);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
