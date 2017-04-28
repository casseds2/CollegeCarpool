package test.collegecarpool.alpha.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import test.collegecarpool.alpha.MapsUtilities.Journey;
import test.collegecarpool.alpha.MapsUtilities.Waypoint;
import test.collegecarpool.alpha.MapsUtilities.WaypointsInitializer;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.Services.NavigationService;
import test.collegecarpool.alpha.PolyDirectionsTools.PolyDirectionResultReceiver;
import test.collegecarpool.alpha.PolyDirectionsTools.PolyDirections;
import test.collegecarpool.alpha.PolyDirectionsTools.PolyURLBuilder;
import test.collegecarpool.alpha.Tools.Variables;

import static test.collegecarpool.alpha.Tools.Variables.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class NavigationActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = "NAVIGATION ACTIVITY";
    private Intent intent;
    private ArrayList<LatLng> polyLatLngs;
    private ArrayList<LatLng> journeyLatLngs;
    private Journey journey;
    private GoogleMap googleMap;
    private Polyline polyline;
    protected PolyDirectionResultReceiver polyDirectionResultReceiver;
    private WaypointsInitializer waypointsInitializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        /*Start The SAT_NAV Service*/
        Variables.SAT_NAV_ENABLED = true;

        /*Keep Screen From Locking When Activity Is On Display*/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        /*Retrieve The Journey LatLngs*/
        journey = (Journey) getIntent().getSerializableExtra("SelectedJourney");

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
        polyDirectionResultReceiver = new PolyDirectionResultReceiver(new Handler(), this);

        /*Start The Navigation Service And Pass the PolyPoints To It*/
        intent = new Intent(NavigationActivity.this, NavigationService.class);
        intent.putExtra("ResultReceiver", polyDirectionResultReceiver);
        intent.putExtra("PolyLatLngs", polyLatLngs);
        intent.putExtra("JourneyLatLngs", journeyLatLngs);
        startService(intent);
        Log.d(TAG, "SAT_NAVE SERVICE STARTED");

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
    public void updateUI(ArrayList<LatLng> journeyLatLngs, ArrayList<LatLng> polyLatLngs, boolean journeyFinished){
        /*Clear The Map If Journey Is Over*/
        if(googleMap != null && journeyFinished && polyline != null){
            googleMap.clear();
        }
        /*If the Journey Is Not Finished*/
        if(!journeyFinished && googleMap != null) {
            this.journeyLatLngs = journeyLatLngs;
            this.polyLatLngs = polyLatLngs;
            googleMap.clear();

            /*Lat Set to Zero to Trigger onDataChanged So If its 0, Don't Zoom On It*/
            if(journeyLatLngs.get(0).latitude != 0)
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(setCamera(journeyLatLngs.get(0))));

            /*Handle If A Waypoint Was Removed Manually*/
            if(waypointsInitializer.waypointRemoved() && journeyLatLngs.size() > 1){
                Log.d(TAG, "A WAYPOINT WAS REMOVED MANUALLY");
                waypointsInitializer.resetWaypointRemoved();

                /*If Waypoints Have Changed, Update Service With New Info*/
                journeyLatLngs = getLatLngsFromWaypoint(journey.getWaypoints());

                //Clear the Map of Old Waypoints
                googleMap.clear();

                /*Inject The New Extras Into A Service*/
                intent.putExtra("PolyLatLngs", polyLatLngs);
                intent.putExtra("JourneyLatLngs", journeyLatLngs);
                intent.putExtra("ResultReceiver", polyDirectionResultReceiver);
                startService(intent);
                Log.d(TAG, "SAT_NAVE SERVICE STARTED AGAIN AFTER DELETED WAYPOINTS");
            }
            /*Refreshes the User UI*/
            waypointsInitializer.displayWaypoints(journey, this.journeyLatLngs);//Will Remove All WayPoints if not used
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(polyLatLngs).width(8).color(Color.BLUE);
            polyline = googleMap.addPolyline(polylineOptions);
        }
    }

    /*Sets Camera To Follow User Around*/
    private CameraPosition setCamera(LatLng userLatLng){
        return new CameraPosition.Builder()
                .target(userLatLng)// Sets the center of the map to location user
                .zoom(16) // Sets the zoom
                .build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        /*Draw the PolyLine On The Map, Bigger Journeys Will Not Work Without It*/
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(polyLatLngs).width(8).color(Color.BLUE);
        zoomPoly(googleMap, polyLatLngs);

        /*Initialize The Waypoint Initializer*/
        waypointsInitializer = new WaypointsInitializer(this, googleMap);

        /*Show The Waypoints On The Map*/
        waypointsInitializer.displayWaypoints(journey);
        googleMap.addPolyline(polylineOptions);
    }

    /*Zoom Map Around Entire Journey Before Zooming in on User*/
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

    /*When the Actviity is Stopped, Kill the Service*/
    @Override
    protected void onStop(){
        super.onStop();
        stopService(intent);
        Log.d(TAG, "NAV_SERVICE STOPPED");
        Variables.SAT_NAV_ENABLED = false; //Re-enable the viewing of Journeys When Out of Navigation mode
    }

    @Override
    protected void onResume(){
        super.onResume();
        startService(intent);
    }
}
