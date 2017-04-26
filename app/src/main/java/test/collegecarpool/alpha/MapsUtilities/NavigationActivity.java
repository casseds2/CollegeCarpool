package test.collegecarpool.alpha.MapsUtilities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.Services.NavigationService;
import test.collegecarpool.alpha.Tools.PolyDirectionResultReceiver;
import test.collegecarpool.alpha.Tools.PolyDirections;
import test.collegecarpool.alpha.Tools.PolyURLBuilder;
import test.collegecarpool.alpha.Tools.Variables;

import static test.collegecarpool.alpha.Tools.Variables.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static test.collegecarpool.alpha.Tools.Variables.shouldZoom;

public class NavigationActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = "NAVIGATION ACTIVITY";
    private Intent intent;
    private ArrayList<LatLng> polyLatLngs;
    private ArrayList<LatLng> journeyLatLngs;
    private Journey journey;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        /*Start The SAT_NAV Service*/
        Variables.SAT_NAV_ENABLED = true;

        /*Retrieve The Journey LatLngs*/
        journey = (Journey) getIntent().getSerializableExtra("SelectedJourney");
        Log.d(TAG, journey.toString());
        journeyLatLngs = getLatLngsFromWaypoint(journey.getWaypoints());

        /*Get the ArrayList of LatLngs From The PolyLine By Calling the Async Method, the .get() will wait for it to complete--does block UI*/
        PolyDirections polyDirections = new PolyDirections();
        try {
            polyLatLngs = polyDirections.execute(new PolyURLBuilder(journeyLatLngs).buildPolyURL()).get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        /*Initialize the PolyDirectionResultReceiver*/
        PolyDirectionResultReceiver polyDirectionsResultReceiver = new PolyDirectionResultReceiver(null, this);
        /*Start The Navigation Service And Pass the PolyPoints To It*/
        intent = new Intent(NavigationActivity.this, NavigationService.class);
        intent.putExtra("ResultReceiver", polyDirectionsResultReceiver);
        intent.putExtra("PolyLatLngs", polyLatLngs);
        intent.putExtra("JourneyLatLngs", journeyLatLngs);
        startService(intent);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /*Obtain the Waypoints' LatLngs from the Journey Object*/
    private ArrayList<LatLng> getLatLngsFromWaypoint(ArrayList<Waypoint> waypoints){
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for(Waypoint waypoint : waypoints){
            double lat = waypoint.getLatLng().getLat();
            double lng = waypoint.getLatLng().getLng();
            LatLng latLng = new LatLng(lat, lng);
            latLngs.add(latLng);
        }
        return latLngs;
    }

    /*Update the Activity Based on the Result Received From The Service*/
    public void updateUI(ArrayList<LatLng> journeyLatLngs, ArrayList<LatLng> polyLatLngs){
        if(googleMap != null){
            googleMap.clear();
            //Also Set Camera To My Location
        }
        this.journeyLatLngs = journeyLatLngs;
        this.polyLatLngs = polyLatLngs;
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(polyLatLngs).width(8).color(Color.BLUE);
        googleMap.addPolyline(polylineOptions);
        new WaypointsInitializer(googleMap).displayWaypoints(journey);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        shouldZoom = true; //So that Active User Map Will Zoom On My Location
        googleMap.setMyLocationEnabled(true);
        googleMap.setBuildingsEnabled(true);

        /*Show The Waypoints On The Map*/
        new WaypointsInitializer(googleMap).displayWaypoints(journey);

        /*Draw the PolyLine On The Map, Bigger Journeys Will Not Work Without It*/
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(polyLatLngs).width(8).color(Color.BLUE);
        googleMap.addPolyline(polylineOptions);
        zoomPoly(googleMap, polyLatLngs);

        Log.d(TAG, "MAP READY");
    }

    private void zoomPoly(GoogleMap googleMap, ArrayList<LatLng> latLngArray){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(LatLng latLng : latLngArray){
            builder.include(latLng);
        }

        /*Got Error Message Telling Me To Do This*/
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (screenWidth * 0.05);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), screenWidth, screenHeight, padding));
    }

    @Override
    protected void onStop(){
        super.onStop();
        stopService(intent);
        Variables.SAT_NAV_ENABLED = false; //Re-enable the viewing of Journeys When Out of Navigation mode
    }

    @Override
    protected void onPause(){
        super.onPause();
        stopService(intent);
        Variables.SAT_NAV_ENABLED = false;
    }
}
