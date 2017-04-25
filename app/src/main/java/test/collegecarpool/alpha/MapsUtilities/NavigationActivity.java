package test.collegecarpool.alpha.MapsUtilities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.Services.NavigationService;
import test.collegecarpool.alpha.Tools.Variables;

import static test.collegecarpool.alpha.Tools.Variables.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static test.collegecarpool.alpha.Tools.Variables.shouldZoom;

public class NavigationActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = "NAVIGATION ACTIVITY";
    private Journey journey;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        journey = (Journey) getIntent().getSerializableExtra("SelectedJourney");
        Log.d(TAG, journey.toString());

        /*Start The Navigation Service*/
        intent = new Intent(NavigationActivity.this, NavigationService.class);
        intent.putExtra("SelectedJourney", journey);
        startService(intent);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        shouldZoom = true; //So that Active User Map Will Zoom On My Location
        googleMap.setMyLocationEnabled(true);
        googleMap.setBuildingsEnabled(true);
        //googleMap.setTrafficEnabled(true);
        new WaypointsInitializer(googleMap).displayWaypoints(journey);
        Log.d(TAG, "MAP READY");
    }

    @Override
    protected void onStop(){
        super.onStop();
        stopService(intent);
        Variables.SAT_NAV_ENABLED = false; //Re-enable the viewing of Journeys When Out of Navigation mode
    }
}
