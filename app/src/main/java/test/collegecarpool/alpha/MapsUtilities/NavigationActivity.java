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

import static test.collegecarpool.alpha.Tools.Variables.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static test.collegecarpool.alpha.Tools.Variables.shouldZoom;

public class NavigationActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = "NAVIGATION ACTIVITY";
    private Journey journey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        journey = (Journey) getIntent().getSerializableExtra("SelectedJourney");
        Log.d(TAG, journey.toString());

        /*Start The Navigation Service*/
        Intent intent = new Intent(NavigationActivity.this, NavigationService.class);
        intent.putExtra("SelectedJourney", journey);
        startService(intent);

        /*This Journey Contains the LAT/LNG of each place/waypoint!*/
        /*Need to Parse these from the String Array from Journey and pass to the Nav Service*/
        /*Also Add markers to the waypoints on the map once we have locations*/
        /*Also add option for using my location when building the waypoints for the Journey, don't rely on PolyURLBuilder to get my location and build directions from that*/
        /*All locations must be entered from PlanJourney Activity*/

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
}
