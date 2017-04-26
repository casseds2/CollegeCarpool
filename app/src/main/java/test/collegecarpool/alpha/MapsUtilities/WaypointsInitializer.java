package test.collegecarpool.alpha.MapsUtilities;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class WaypointsInitializer {

    private GoogleMap googleMap;
    private final String TAG = "WAYPOINT INITIALIZER";

    public WaypointsInitializer(GoogleMap googleMap){
        this.googleMap = googleMap;
        Log.d(TAG, "Waypoint Initialized");
    }

    /*Display Waypoints From an Ongoing Journey*/
    void displayWaypoints(Journey journey, ArrayList<com.google.android.gms.maps.model.LatLng> latLngs){
        if(googleMap != null){
            googleMap.clear();
        }
        ArrayList<Waypoint> validWaypoints = new ArrayList<>();
        ArrayList<LatLng> temp = new ArrayList<>(); //List of My Home Made LatLngs
        LatLng wayLatLng;

        /*Convert the JourneyLatLngs Array to My Personal LatLngs*/
        for(com.google.android.gms.maps.model.LatLng latLng : latLngs){
            wayLatLng = new LatLng(latLng.latitude, latLng.longitude);
            temp.add(wayLatLng);
        }
        Log.d(TAG, "TEMP IS: " + temp.toString());

        /*Original WayPoints of The Journey*/
        ArrayList<Waypoint> waypoints = journey.getWaypoints();
        Log.d(TAG, "ORIGINAL WAYPOINTS: " + waypoints.toString());

        /*Check If the Journey's List of Waypoints are equal to the Obtained List*/
        for(Waypoint waypoint : waypoints){
            Log.d(TAG, "Waypoint is: " + waypoint.getLatLng().toString());
            for(LatLng latLng : temp){
                if(waypoint.toGoogleLatLng().equals(latLng.toGoogleLatLng())){
                    Log.d(TAG, "WAYPOINTS EQUAL");
                    validWaypoints.add(waypoint);
                }
            }
        }

        /*Set Map Waypoints to the Journey Waypoints*/
        Journey journey1 = new Journey(null, validWaypoints);
        displayWaypoints(journey1);
    }

    /*Display the Waypoints From A Journey*/
    void displayWaypoints(Journey journey){
        ArrayList<Waypoint> waypoints = journey.getWaypoints();
        for(Waypoint waypoint : waypoints){
            Log.d(TAG, waypoint.toString());
            LatLng latLng = waypoint.getLatLng();
            com.google.android.gms.maps.model.LatLng latLngGoogle = new com.google.android.gms.maps.model.LatLng(latLng.getLat(), latLng.getLng());
            googleMap.addMarker(new MarkerOptions()
                    .position(latLngGoogle)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                    .setTitle(waypoint.getName());
            googleMap.addMarker(new MarkerOptions().position(latLngGoogle)).setTitle(waypoint.getName());
            Log.d(TAG, "WAYPOINT ADDED FOR " + waypoint.getName() + " at " + latLngGoogle.toString());
        }
    }

    public void displayWaypoints(ArrayList<com.google.android.gms.maps.model.LatLng> latLngs){
        for(com.google.android.gms.maps.model.LatLng latLng : latLngs){
            googleMap.addMarker(new MarkerOptions().position(latLng)).setTitle("Waypoint " + (latLngs.indexOf(latLng) + 1));
        }
    }
}
