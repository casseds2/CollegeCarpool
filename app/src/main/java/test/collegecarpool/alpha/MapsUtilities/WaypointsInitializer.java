package test.collegecarpool.alpha.MapsUtilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class WaypointsInitializer extends Activity implements GoogleMap.OnMarkerClickListener{

    private Context context;
    private GoogleMap googleMap;
    private final String TAG = "WAYPOINT INITIALIZER";
    private Journey journey;
    boolean removedWaypoint = false;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference data;

    public WaypointsInitializer(Context context, GoogleMap googleMap){
        this.context = context;
        this.googleMap = googleMap;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        data = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid()).child("latitude");
        Log.d(TAG, "Waypoint Initialized");
    }

    /*Display Waypoints From an Ongoing Journey*/
    public void displayWaypoints(Journey journey, ArrayList<com.google.android.gms.maps.model.LatLng> latLngs){
        if(googleMap != null){
            googleMap.clear();
        }

        /*So We Can Manipulate Desired Waypoints in AlertDialog*/
        this.journey = journey;

        ArrayList<Waypoint> validWaypoints = new ArrayList<>();
        ArrayList<LatLng> temp = new ArrayList<>(); //List of My Home Made LatLngs
        LatLng wayLatLng;

        /*Convert the JourneyLatLngs Array to My Personal LatLngs*/
        for(com.google.android.gms.maps.model.LatLng latLng : latLngs){
            wayLatLng = new LatLng(latLng.latitude, latLng.longitude);
            temp.add(wayLatLng);
        }

        /*Original WayPoints of The Journey*/
        ArrayList<Waypoint> waypoints = journey.getWaypoints();

        /*Check If the Journey's List of Waypoints are equal to the Obtained List*/
        for(Waypoint waypoint : waypoints){
            for(LatLng latLng : temp){
                if(waypoint.getLatLng().equals(latLng)){
                    validWaypoints.add(waypoint);
                }
            }
        }

        /*Set Map Waypoints to the Journey Waypoints*/
        Journey journey1 = new Journey(null, validWaypoints);
        displayWaypoints(journey1);
    }

    /*Display the Waypoints From A Journey*/
    public void displayWaypoints(Journey journey){
        ArrayList<Waypoint> waypoints = journey.getWaypoints();
        for(Waypoint waypoint : waypoints){
            LatLng latLng = waypoint.getLatLng();
            com.google.android.gms.maps.model.LatLng latLngGoogle = new com.google.android.gms.maps.model.LatLng(latLng.getLat(), latLng.getLng());

            /*Makes a Marker With The Waypoint Name And Sets Its Tag With A Waypoint Object*/
            Marker waypointMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latLngGoogle)
                    .title(waypoint.getName()));
            waypointMarker.setTag(waypoint);

            Log.d(TAG, "WAYPOINT ADDED FOR " + waypoint.getName() + " at " + latLngGoogle.toString());

            /*Allow the Markers To Be Clickable*/
            googleMap.setOnMarkerClickListener(this);
        }
    }

    public void displayWaypoints(ArrayList<com.google.android.gms.maps.model.LatLng> latLngs){
        for(com.google.android.gms.maps.model.LatLng latLng : latLngs){
            googleMap.addMarker(new MarkerOptions().position(latLng)).setTitle("Waypoint " + (latLngs.indexOf(latLng) + 1));
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Remove Waypoint?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "REMOVED A WAYPOINT MANUALLY");
                        Waypoint waypoint = (Waypoint) marker.getTag();
                        Log.d(TAG, "JOURNEY WAS: " + journey.toString());
                        journey.removeWaypoint(waypoint);
                        Log.d(TAG, "JOURNEY IS: " + journey.toString());
                        googleMap.clear(); //Removes Waypoint and Current Polyline
                        displayWaypoints(journey); //Re-display Updated Waypoints
                        removedWaypoint = true;
                        data.setValue(null); //Triggers onDataChanged in Nav_Service
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
        return false;
    }

    public boolean waypointRemoved(){
        return removedWaypoint;
    }

    public void resetWaypointRemoved(){
        removedWaypoint = false;
    }
}
