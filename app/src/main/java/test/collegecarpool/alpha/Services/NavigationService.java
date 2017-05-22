package test.collegecarpool.alpha.Services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;

import test.collegecarpool.alpha.Firebase.PolyLinePusher;
import test.collegecarpool.alpha.MapsUtilities.DirectionStep;
import test.collegecarpool.alpha.PolyDirectionsTools.DirectionParser;
import test.collegecarpool.alpha.PolyDirectionsTools.PolyDirections;
import test.collegecarpool.alpha.PolyDirectionsTools.PolyURLBuilder;
import test.collegecarpool.alpha.Tools.Variables;
import test.collegecarpool.alpha.UserClasses.UserProfile;

public class NavigationService extends Service {

    private final String TAG = "NavigationService";
    private DatabaseReference userRef;
    private String encodedPolyLine;
    private String stepInstruction;
    private ArrayList<LatLng> waypointLatLngs;
    private ArrayList<LatLng> polyLatLngs;
    private ArrayList<DirectionStep> directionSteps;
    private ResultReceiver navTwoReceiver;
    private PolyDirections polyDirections;
    private PolyLinePusher polyLinePusher;
    private DirectionStep currentStep;
    private DirectionParser directionParser;
    private LatLng nearWaypoint;
    private LatLng userLatLng;
    private int stepCount;
    private boolean myLocationAdded;
    private boolean journeyFinished;
    private boolean userAtStartOfStep;
    private boolean userAtEndOfStep;
    private boolean removedCloseWaypoint;
    private boolean polyLineRecalculated;
    private float bearing;

    public NavigationService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "NavServiceTwo Started");

        Variables.SAT_NAV_ENABLED = true;
        myLocationAdded = false;
        polyLinePusher = new PolyLinePusher();
        journeyFinished = false;
        stepCount = 0;
        userAtStartOfStep = false;
        stepInstruction = "";
        userAtEndOfStep = false;
        removedCloseWaypoint = false;
        polyLineRecalculated = false;
        bearing = 0.0f;

        /*Get Info From The Intent*/
        waypointLatLngs = intent.getParcelableArrayListExtra("WaypointLatLngs");
        navTwoReceiver = intent.getParcelableExtra("ResultReceiver");

        /*Start Up Firebase Listener*/
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "User Location Changed");

                /*Obtain User Position*/
                UserProfile myProfile = dataSnapshot.getValue(UserProfile.class);
                userLatLng = new LatLng(myProfile.getLatitude(), myProfile.getLongitude());

                /*If SAT_NAV Finished During Loop, Shut it Down*/
                if(!Variables.SAT_NAV_ENABLED){
                    shutDownService();
                    userRef.removeEventListener(this);
                }

                /*Add User To LatLng to Waypoints So We Can Draw PolyLine From Them*/
                if (!myLocationAdded) {
                    try {
                        waypointLatLngs.add(0, userLatLng);
                        myLocationAdded = true;
                        Log.d(TAG, "My Location Added To Waypoint Array And Polyline Calculated");
                        polyDirections = new PolyDirections();
                        polyLatLngs = polyDirections.execute(new PolyURLBuilder(waypointLatLngs).buildPolyURL()).get();
                        polyLineRecalculated = true;
                        directionParser = polyDirections.getDirectionParser();
                        directionSteps = directionParser.getDirectionSteps();
                        currentStep = directionSteps.get(stepCount);
                        encodedPolyLine = PolyUtil.encode(polyLatLngs);
                        polyLinePusher.pushPolyLine(encodedPolyLine, waypointLatLngs.subList(1, waypointLatLngs.size()));
                    } catch (Exception e) {
                        Log.d(TAG, "Concurrent Exception With Initial API Request");
                    }
                }
                else
                    waypointLatLngs.set(0, userLatLng);

                /*If The User Isn't on The Correct Route (100m)*/
                if (!userOnRoute(userLatLng)) {
                    stepCount = 0;
                    try {
                        Log.d(TAG, "User Wasn't On The Correct Route So Polyline Re-calculated");
                        polyDirections = new PolyDirections();
                        polyLatLngs = polyDirections.execute(new PolyURLBuilder(waypointLatLngs).buildPolyURL()).get();
                        polyLineRecalculated = true;
                        directionParser = polyDirections.getDirectionParser();
                        directionSteps = directionParser.getDirectionSteps();
                        currentStep = directionSteps.get(stepCount);
                        encodedPolyLine = PolyUtil.encode(polyLatLngs);
                        polyLinePusher.pushPolyLine(encodedPolyLine, waypointLatLngs.subList(1, waypointLatLngs.size()));
                    } catch (Exception e) {
                        Log.d(TAG, "Already Requesting User Route");
                    }
                }
                /*The User Is on The Correct Route*/
                else {
                    Log.d(TAG, "User Is On Correct Route");

                    currentStep = directionSteps.get(stepCount);

                    if (waypointLatLngs.size() > 1) {
                        /*Check If The User Is At A Waypoint*/
                        if (userAtWaypoint()) {
                            Log.d(TAG, "User At A Waypoint: " + nearWaypoint.toString());
                            waypointLatLngs.remove(nearWaypoint);
                            removedCloseWaypoint = true;
                            if (waypointLatLngs.size() == 1 && myLocationAdded) {
                                Log.d(TAG, "All Waypoints Reached, Stopping Service");
                                userRef.removeEventListener(this);
                                shutDownService();
                            }
                        }

                        /*CHECK IF USER IS 20M FROM NEXT START STEP BEFORE REASSIGNING CURRENT STEP*/
                        /*REALLY TRY GET IT OUT, LAST DAY*/
                        /*TEST IN MORNING*/

                        /*Check If The User Is At A Start or End Step*/
                        if (directionSteps.size() > 0) {
                            /*Start Step (40m)*/
                            if (isNear(userLatLng, currentStep.getStart(), 40.0f)) {
                                Log.d(TAG, "User Is At a Start Step");
                                stepInstruction = currentStep.getHtmlInstruction();
                                userAtStartOfStep = true;
                            }
                            /*End Step (100m)*/
                            if (isNear(userLatLng, currentStep.getEnd(), 100.0f) && directionSteps.size()-1 > stepCount) { //if there's another step
                                Log.d(TAG, "User Is At an End Step");
                                stepCount++;
                                currentStep = directionSteps.get(stepCount);
                                stepInstruction = directionSteps.get(stepCount).getHtmlInstruction();
                                Log.d(TAG, "Instruction: " + stepInstruction);
                                userAtEndOfStep = true;
                            }
                            /*Check If User Is At Both A Start And End Step*/
                            if (userAtStartOfStep && userAtEndOfStep) {
                                Log.d(TAG, "User Is At Both Start and End Step");
                                /*Find Out Which One Is Closest*/
                                float [] dist = new float[1];
                                Location.distanceBetween(myProfile.getLatitude(), myProfile.getLongitude(), currentStep.getEnd().latitude, currentStep.getEnd().longitude, dist);
                                Log.d(TAG, "Distance to end : " + dist[0]);
                                float distanceToEnd = dist[0];
                                dist = new float[1];
                                Location.distanceBetween(myProfile.getLatitude(), myProfile.getLongitude(), currentStep.getStart().latitude, currentStep.getStart().longitude, dist);
                                Log.d(TAG, "Distance to start : " + dist[0]);
                                float distanceToStart = dist[0];
                                if (distanceToEnd < distanceToStart || distanceToEnd < 100.0f) { //If Distance to End Is Less Than Distance to Start or less than 100m
                                    userAtStartOfStep = false;
                                    Log.d(TAG, "userAtStartStep Set To False So No Changes Happen");
                                } else {
                                    userAtEndOfStep = false;
                                    Log.d(TAG, "userAtEndStep Set To False So Instruction Gets Priority");
                                }
                                if(distanceToStart == distanceToEnd){
                                    userAtStartOfStep = false;
                                    Log.d(TAG, "Distances Are Equal Apart");
                                }
                            }
                        }
                        /*Stops the CallBack That Will Continue Running And Nullifies the Active Journey*/
                        if (!Variables.SAT_NAV_ENABLED) {
                            Log.d(TAG, "Service Stopped Because SAT_NAV_ENABLED Was False");
                            shutDownService();
                            userRef.removeEventListener(this);
                        }
                    }
                    /*Waypoints Array Size is Less Than One*/
                    else {
                        Log.d(TAG, "Journey Finished - Only My Position Left in Array");
                        shutDownService();
                        userRef.removeEventListener(this);
                    }
                }
                /*Remove Any LatLngs I'm Beside And Send the Bundle*/
                updatePolyLtLngs();
                getBearing();
                sendBundle();
                removedCloseWaypoint = false;
                polyLineRecalculated = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return START_NOT_STICKY;
    }

    private void shutDownService(){
        Variables.SAT_NAV_ENABLED = false;
        journeyFinished = true;
        if (polyDirections != null) {
            polyDirections.cancel(true);
            polyDirections = null;
        }
        stopSelf();
    }

    /*If My Location is Near Any of The PolyLatLngs, Remove Them (5m)*/
    private void updatePolyLtLngs(){
        int indexMax = 0;
        for(LatLng latLng : polyLatLngs){
            if(isNear(userLatLng, latLng, 15.0f)) {
                indexMax = polyLatLngs.indexOf(latLng);
            }
        }
        Log.d(TAG, "Index of Max is: " + indexMax);
        /*Attempt to Stop Straight Line Affect*/
        polyLatLngs = new ArrayList<>(polyLatLngs.subList(indexMax, polyLatLngs.size()));

        /*TEST CODE*/
        polyLineRecalculated = true;
    }

    /*Get The Bearing Of the UserLatLng to The first Element of PolyLatLngs - Formula Igis Map*/
    private void getBearing(){
        /*Get The Bearing of Travelling Direction With Atan2 Formula*/
        if(stepCount < directionSteps.size()) {
            LatLng poly = directionSteps.get(stepCount).getEnd();
            float Y = (float) Math.cos(poly.latitude) * (float) Math.sin(Math.abs(userLatLng.longitude - poly.longitude));
            float X = (float) Math.cos(userLatLng.latitude) * (float) Math.sin(poly.latitude) - (float) Math.sin(userLatLng.latitude) * (float) Math.cos(poly.latitude) * (float) Math.cos(Math.abs(userLatLng.longitude - poly.longitude));
            bearing = (float) Math.toDegrees(Math.atan2(Y, X));
            Log.d(TAG, "Bearing is " + bearing);
        }
    }

    /*Send the Bundle To the Results Receiver*/
    private void sendBundle(){
        Bundle bundle = new Bundle();
        bundle.putSerializable("WaypointLatLngs", waypointLatLngs);
        bundle.putSerializable("PolyLatLngs", polyLatLngs);
        bundle.putBoolean("JourneyFinished", journeyFinished);
        bundle.putBoolean("RemovedCloseWaypoint", removedCloseWaypoint);
        bundle.putString("Instruction", stepInstruction);
        bundle.putBoolean("UserAtStartStep", userAtStartOfStep);
        bundle.putBoolean("UserAtEndStep", userAtEndOfStep);
        bundle.putBoolean("PolyLineRecalculated", polyLineRecalculated);
        bundle.putFloat("Bearing", bearing);
        navTwoReceiver.send(0, bundle);
        Log.d(TAG, "Bundle Was Sent To Result Receiver");
    }

    /*Check if User Is At a Waypoint (60m)*/
    private boolean userAtWaypoint(){
        for(LatLng latLng : waypointLatLngs.subList(1, waypointLatLngs.size())){
            if(isNear(userLatLng, latLng, 60.0f)){
                nearWaypoint = latLng;
                return true;
            }
        }
        return false;
    }

    /*Check If One LatLng (User) is Close to Another*/
    private boolean isNear(LatLng userLatLng, LatLng other, float threshold){
        float [] distance = new float [1];
        Location.distanceBetween(userLatLng.latitude, userLatLng.longitude, other.latitude, other.longitude, distance);
        return distance[0] < threshold;
    }

    /*Check If User is Near the Polyline (50m)*/
    private boolean userOnRoute(LatLng userLatLng) {
        return PolyUtil.isLocationOnPath(userLatLng, polyLatLngs, false, 50);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}