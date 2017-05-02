package test.collegecarpool.alpha.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import test.collegecarpool.alpha.Firebase.PolyLinePusher;
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
    private PolyDirectionResultReceiver polyDirectionResultReceiver;
    private WaypointsInitializer waypointsInitializer;
    private PolyLinePusher polyLinePusher;
    private LatLng requestLatLng;
    private boolean acceptedRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        /*Start The SAT_NAV Service*/
        Variables.SAT_NAV_ENABLED = true;

        acceptedRequest = false;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        /*So we Can Nullify Journey From Here*/
        polyLinePusher = new PolyLinePusher();

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

        /*Set Up The Broadcast Receiver*/
        LocalBroadcastManager.getInstance(this).registerReceiver(rideRequestReceiver, new IntentFilter("ride_request_latLng"));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /*On Receive Of A Broadcast*/
    private BroadcastReceiver rideRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getSerializableExtra("request_latLng") != null) {
                test.collegecarpool.alpha.MapsUtilities.LatLng rideRequestLatLng = (test.collegecarpool.alpha.MapsUtilities.LatLng) intent.getSerializableExtra("request_latLng");
                Log.d(TAG, "BROADCAST RECEIVED: " + rideRequestLatLng.toString());
                //Toast.makeText(NavigationActivity.this, "Broadcast: " + rideRequestLatLng, Toast.LENGTH_SHORT).show();
                requestLatLng = rideRequestLatLng.toGoogleLatLng();
                acceptedRequest = true;
                updateUI(journeyLatLngs, polyLatLngs, false);
            }
            else
                Toast.makeText(NavigationActivity.this, "Could Not Read Request LatLng", Toast.LENGTH_SHORT).show();
        }
    };

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

    /*Where Abouts in the Journey to Put the */
    private void insertRequestIntoJourney(LatLng latLng){
        Log.d(TAG, "NEW REQUEST FOUND");
        float [] distance = new float[1];
        double minDistance = 100000;
        int minIndex = 1;
        for(LatLng latLng1 : journeyLatLngs.subList(1, journeyLatLngs.size())){
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng1.latitude, latLng1.longitude, distance);
            if(distance[0] < minDistance){
                minIndex = journeyLatLngs.indexOf(latLng1);
                minDistance = distance[0];
                Log.d(TAG, "MinDistance is: " + String.valueOf(minDistance) + "\n MinIndex is: " + minIndex);
            }
        }
        journeyLatLngs.add(minIndex, latLng);
    }

    /*Update the Activity Based on the Result Received From The Service*/
    public void updateUI(ArrayList<LatLng> journeyLatLngs, ArrayList<LatLng> polyLatLngs, boolean journeyFinished){
        Log.d(TAG, "updateUI CALLED");

        /*Clear The Map If Journey Is Over*/
        if(googleMap != null && journeyFinished && polyline != null){
            googleMap.clear();
        }
        /*If the Journey Is Not Finished*/
        if(!journeyFinished && googleMap != null) {
            this.journeyLatLngs = journeyLatLngs;
            this.polyLatLngs = polyLatLngs;

            /*On Data Change, Follow the User*/
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(setCamera(journeyLatLngs.get(0))));

            /*If A Driver Accepted A Request, then Add the LatLng*/
            if(acceptedRequest) {
                Log.d(TAG, "A REQUEST WAS ACCEPTED");
                insertRequestIntoJourney(requestLatLng);
                journey.addWaypoint(new Waypoint("Pickup", new test.collegecarpool.alpha.MapsUtilities.LatLng(requestLatLng.latitude, requestLatLng.longitude)));
                acceptedRequest = false;
                if(journeyLatLngs.size() > 0) {
                    try {
                        polyLatLngs = new PolyDirections().execute(new PolyURLBuilder(journeyLatLngs).buildPolyURL()).get();
                        String encodePolyLine = PolyUtil.encode(polyLatLngs);
                        polyLinePusher.pushPolyLine(encodePolyLine, journeyLatLngs.subList(1, journeyLatLngs.size()));
                        Log.d(TAG, "NEW POLYLATLNGS CALCULATED AFTER REMOVING WAYPOINT");

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    /*Inject The New Extras Into A Service*/
                    intent.putExtra("PolyLatLngs", polyLatLngs);
                    intent.putExtra("JourneyLatLngs", journeyLatLngs);
                    intent.putExtra("ResultReceiver", polyDirectionResultReceiver);

                    startService(intent);
                    Log.d(TAG, "SAT_NAVE SERVICE STARTED AGAIN AFTER ACCEPTING REQUEST");
                }
            }

            /*Handle If A Waypoint Was Removed Manually*/
            if(waypointsInitializer.waypointRemoved() && journeyLatLngs.size() > 1){

                if(waypointsInitializer.waypointRemoved())
                    Log.d(TAG, "A WAYPOINT WAS REMOVED MANUALLY");

                /*Clear the Map of Old Waypoints*/
                googleMap.clear();

                /*Retrieve The Updated List From WaypointsInitializer*/
                journey = waypointsInitializer.getJourney();

                /*If Waypoints Have Changed, Update Service With New Info*/
                journeyLatLngs = getLatLngsFromWaypoint(journey.getWaypoints());

                /*If A Waypoint Was Removed Manually, Re-calculate PolyLine*/
                if(journeyLatLngs.size() > 0) {
                    try {
                        polyLatLngs = new PolyDirections().execute(new PolyURLBuilder(journeyLatLngs).buildPolyURL()).get();
                        String encodePolyLine = PolyUtil.encode(polyLatLngs);
                        polyLinePusher.pushPolyLine(encodePolyLine, journeyLatLngs.subList(1, journeyLatLngs.size()));
                        Log.d(TAG, "NEW POLYLATLNGS CALCULATED AFTER REMOVING WAYPOINT");

                    }
                    catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    /*Inject The New Extras Into A Service*/
                    intent.putExtra("PolyLatLngs", polyLatLngs);
                    intent.putExtra("JourneyLatLngs", journeyLatLngs);
                    intent.putExtra("ResultReceiver", polyDirectionResultReceiver);

                    startService(intent);
                    Log.d(TAG, "SAT_NAVE SERVICE STARTED AGAIN AFTER DELETED WAYPOINTS");

                /*Reset Boolean For If A Waypoint Was Removed*/
                    waypointsInitializer.resetWaypointRemoved();
                }
                else{
                    googleMap.clear();
                    polyLinePusher.nullify();
                    stopService(intent);
                    Log.d(TAG, "JourneyLatLngsSize is: " + journeyLatLngs.size() + " so Journey Finished");
                }
            }
            /*Refreshes the User UI*/
            if(journeyLatLngs.size() > 0) {
                waypointsInitializer.displayWaypoints(journey, this.journeyLatLngs);
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(polyLatLngs).width(8).color(Color.BLUE);
                polyline = googleMap.addPolyline(polylineOptions);
            }
            else{
                polyLinePusher.nullify();
                googleMap.clear();
                stopService(intent);
                Log.d(TAG, "JourneyLatLngsSize is: " + journeyLatLngs.size() + " so Journey Finished");
            }
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

    /*When the Actvity is Stopped, Kill the Service*/
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
