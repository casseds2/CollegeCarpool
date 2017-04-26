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
