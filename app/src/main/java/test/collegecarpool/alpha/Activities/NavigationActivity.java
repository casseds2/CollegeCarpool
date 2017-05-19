package test.collegecarpool.alpha.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.Locale;

import test.collegecarpool.alpha.Firebase.PolyLinePusher;
import test.collegecarpool.alpha.MapsUtilities.Journey;
import test.collegecarpool.alpha.MapsUtilities.Waypoint;
import test.collegecarpool.alpha.PolyDirectionsTools.NavigationReceiver;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.Services.NavigationService;
import test.collegecarpool.alpha.Tools.Variables;

import static test.collegecarpool.alpha.Tools.Variables.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class NavigationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private TextView instructions;
    private final String TAG = "NavigationActivity";
    private Journey journey;
    private ArrayList<LatLng> waypointLatLngs;
    private ArrayList<LatLng> polyLatLngs;
    private NavigationReceiver navReceiver;
    private Intent intent;
    private TextToSpeech speaker;
    private boolean waypointWasRemovedManually;
    private boolean journeyFinished;
    private boolean userAtStartStep;
    private boolean userAtEndStep;
    private String instruction = "";
    private boolean serviceEnded;
    private boolean polyLineRecalculated;
    private boolean removedCloseWaypoint;
    private boolean acceptedRequest;
    private PolyLinePusher polyLinePusher;
    private LatLng requestLatLng;
    private float bearing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        /*Initialise Text To Speech As English*/
        speaker = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    speaker.setLanguage(Locale.UK);
                    Log.d(TAG, "Speaker Initialized");
                }
                else{
                    Log.d(TAG, "Speaker Had an Error");
                }
            }
        });

        waypointWasRemovedManually = false;
        serviceEnded = false;
        acceptedRequest = false;
        polyLineRecalculated = false;
        polyLinePusher = new PolyLinePusher();
        instructions = (TextView) findViewById(R.id.driving_instructions);

        /*Keep Screen From Locking When Activity Is On Display*/
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

         /*Set Up The Broadcast Receiver*/
        LocalBroadcastManager.getInstance(this).registerReceiver(rideRequestReceiver, new IntentFilter("ride_request_latLng"));

        /*Retrieve The Journey LatLngs*/
        journey = (Journey) getIntent().getSerializableExtra("SelectedJourney");
        /*Gets the Waypoint LatLngs From The Journey*/
        waypointLatLngs = getWaypointLatLngs();
        /*Initialize the PolyDirectionResultReceiver*/
        navReceiver = new NavigationReceiver(new Handler(), this);
        /*Set Up The Intent*/
        intent = new Intent(NavigationActivity.this, NavigationService.class);
        intent.putExtra("ResultReceiver", navReceiver);
        intent.putExtra("WaypointLatLngs", waypointLatLngs);
        startService(intent);

        mapFragment.getMapAsync(this);
    }

    /*On Receive Of A Broadcast*/
    private BroadcastReceiver rideRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getSerializableExtra("request_latLng") != null) {
                test.collegecarpool.alpha.MapsUtilities.LatLng rideRequestLatLng = (test.collegecarpool.alpha.MapsUtilities.LatLng) intent.getSerializableExtra("request_latLng");
                requestLatLng = rideRequestLatLng.toGoogleLatLng();
                acceptedRequest = true;
                Log.d(TAG, "Received Ride Request");
                updateUI(waypointLatLngs, polyLatLngs, journeyFinished, removedCloseWaypoint, userAtStartStep, userAtEndStep, polyLineRecalculated, null, bearing);
            }
            else
                Toast.makeText(NavigationActivity.this, "Could Not Read Request LatLng", Toast.LENGTH_SHORT).show();
        }
    };

    /*Where Abouts in the Journey to Put the Ride Request*/
    public void insertRequestIntoWaypointLatLngs(LatLng latLng){
        Log.d(TAG, "New Ride Request Received");
        float [] distance = new float[1];
        double minDistance = 100000;
        int minIndex = 1;
        for(LatLng latLng1 : waypointLatLngs.subList(1, waypointLatLngs.size())){
            Location.distanceBetween(latLng.latitude, latLng.longitude, latLng1.latitude, latLng1.longitude, distance);
            if(distance[0] < minDistance){
                minIndex = waypointLatLngs.indexOf(latLng1);
                minDistance = distance[0];
                Log.d(TAG, "MinDistance is: " + String.valueOf(minDistance) + "\n MinIndex is: " + minIndex);
            }
        }
        journey.addWaypoint(new Waypoint("Pickup", new test.collegecarpool.alpha.MapsUtilities.LatLng(latLng.latitude, latLng.longitude)));
        waypointLatLngs.add(minIndex, latLng);
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

        drawWaypointsOnMap();
        googleMap.setOnMarkerClickListener(this);
    }

    /*Update The UI With Info From the Service*/
    public void updateUI(ArrayList<LatLng> waypointLatLngs, ArrayList<LatLng> polyLatLngs, boolean journeyFinished, boolean removedCloseWaypoint, boolean userAtStartStep, boolean userAtEndStep, boolean polyLineRecalculated, String instruction, float bearing){

        /*So We Have Local Variables*/
        this.waypointLatLngs = waypointLatLngs;
        this.polyLatLngs = polyLatLngs;
        this.journeyFinished = journeyFinished;
        this.removedCloseWaypoint = removedCloseWaypoint;
        this.userAtStartStep = userAtStartStep;
        this.userAtEndStep = userAtEndStep;
        this.polyLineRecalculated = polyLineRecalculated;
        this.bearing = bearing;

        instructions.setText(instruction);

        /*Check If The User Was At A Waypoint*/
        if(removedCloseWaypoint){
            speaker.speak("You Have Reached A Stop", TextToSpeech.QUEUE_FLUSH, null);
            googleMap.clear();
            polyLineRecalculated = true;
            if(waypointLatLngs.size() == 1)
                journeyFinished = true;
        }

        /*Check If The Journey Is Finished*/
        if(journeyFinished && !serviceEnded){
            googleMap.clear();
            serviceEnded = true;
            Log.d(TAG, "Journey Is Finished");
            speaker.speak("Journey Finished", TextToSpeech.QUEUE_FLUSH, null);
            stopService(intent);
        }

        /*If the User Accepted A Ride Request*/
        if(acceptedRequest){
            //googleMap.clear();
            insertRequestIntoWaypointLatLngs(requestLatLng);
            acceptedRequest = false;
            /*Reset So Instruction Can Be Repeated If Journey Changes*/
            this.instruction = "";
            /*Set Up The Intent*/
            intent = new Intent(NavigationActivity.this, NavigationService.class);
            intent.putExtra("ResultReceiver", navReceiver);
            intent.putExtra("WaypointLatLngs", waypointLatLngs);
            startService(intent);
        }

        /*If A Waypoint Was Removed Manually, Restart The Service With Updated Route*/
        if(waypointWasRemovedManually && waypointLatLngs.size() > 1){
            waypointWasRemovedManually = false;
            speaker.speak("Waypoint Removed", TextToSpeech.QUEUE_FLUSH, null);
            /*Reset So Instruction Can Be Repeated If Journey Changes*/
            this.instruction = "";
            /*Set Up The Intent*/
            intent = new Intent(NavigationActivity.this, NavigationService.class);
            intent.putExtra("ResultReceiver", navReceiver);
            intent.putExtra("WaypointLatLngs", waypointLatLngs);
            startService(intent);
        }

        /*Set Camera On User Position & Update waypointLatLngs With My Position*/
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(setCamera(waypointLatLngs.get(0))));

        /*Manage If User At Start Or End Step*/
        if(userAtStartStep || userAtEndStep) {
            if (instruction != null) {
                if (!this.instruction.equals(instruction)) {
                    Log.d(TAG, "User At Start Point");
                    Log.d(TAG, "Instruction: " + instruction);
                    this.instruction = instruction;
                    speaker.speak(instruction, TextToSpeech.QUEUE_FLUSH, null);
                } else
                    Log.d(TAG, "Already Spoke Instruction");
            }
        }

        /*Stop The Lines Being Drawn At the End Of A Journey*/
        if(!journeyFinished) {
            if(polyLineRecalculated || userAtEndStep) { //Redraw at line update or at end step
                /*Clear Map*/
                googleMap.clear();
                /*Add Markers To The Map*/
                drawWaypointsOnMap();
                /*Add the Polyline to The Map*/
                //PolylineOptions polylineOptions = new PolylineOptions();
                //polylineOptions.addAll(polyLatLngs).width(8).color(Color.BLUE);
                //googleMap.addPolyline(polylineOptions);
                /*Set Up How Polyline Looks*/
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(polyLatLngs);
                polylineOptions.width(10);
                polylineOptions.color(Color.BLUE);
                polylineOptions.clickable(true);
                polylineOptions.isClickable();
                polylineOptions.geodesic(true);
                Polyline polyline = googleMap.addPolyline(polylineOptions);
                /*Set PolyLine Styles - New Update*/
                polyline.setStartCap(new RoundCap());
                polyline.setEndCap(new RoundCap());
                polyline.setJointType(JointType.ROUND);
            }
        }
    }

    /*Draws Any Waypoints from Waypoints LatLng On the Map*/
    private void drawWaypointsOnMap(){
        ArrayList<Waypoint> waypoints = journey.getWaypoints();
        if(waypointLatLngs.size() > 0){
            for(LatLng latLng : waypointLatLngs){
                for(Waypoint waypoint : waypoints){
                    if(waypoint.getLatLng().toGoogleLatLng().equals(latLng)){
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng));
                        marker.setTitle(waypoint.getName());
                        marker.setTag(waypoint);
                        Log.d(TAG, "Added " + latLng.toString() + " to Map");
                    }
                }
            }
        }
        else{
            Log.d(TAG, "No Waypoints Left");
        }
    }

    /*Sets Camera To Follow User Around*/
    private CameraPosition setCamera(LatLng userLatLng){
        return new CameraPosition.Builder()
                .target(userLatLng)
                .zoom(16)
                .bearing(bearing)
                .build();
    }

    /*Gets the LatLngs From the Journey Objects as Google LatLngs*/
    public ArrayList<LatLng> getWaypointLatLngs(){
        ArrayList<Waypoint> waypoints = journey.getWaypoints();
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for(Waypoint waypoint : waypoints){
            latLngs.add(new LatLng(waypoint.getLatLng().getLat(), waypoint.getLatLng().getLng()));
        }
        Log.d(TAG,  "Got Waypoint Lat/Lngs From Journey");
        return latLngs;
    }

    /*Click Listener For Markers*/
    @Override
    public boolean onMarkerClick(final Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_AppCompat_DayNight))
                .setTitle("Remove Waypoint?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Removed Waypoint Manually");
                        waypointWasRemovedManually = true;
                        Waypoint waypoint = (Waypoint) marker.getTag();
                        if (waypoint != null) {
                            Log.d(TAG, "Waypoints Was: " + waypointLatLngs.toString());
                            LatLng latLng = waypoint.getLatLng().toGoogleLatLng();
                            waypointLatLngs.remove(latLng);
                            journey.removeWaypoint(waypoint);
                            if(waypointLatLngs.size() == 1)
                                journeyFinished = true;
                            Log.d(TAG, "Waypoints Now: " + waypointLatLngs.toString());
                            updateUI(waypointLatLngs, polyLatLngs, journeyFinished, removedCloseWaypoint, userAtStartStep, userAtEndStep, polyLineRecalculated, null, bearing);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
        return false;
    }

    /*When the Actvity is Stopped, Kill the Service*/
    @Override
    protected void onStop(){
        super.onStop();
        speaker.shutdown();
        stopService(intent);
        polyLinePusher.nullify();
        Log.d(TAG, "NAV_SERVICE STOPPED");
        Variables.SAT_NAV_ENABLED = false;
    }
}
