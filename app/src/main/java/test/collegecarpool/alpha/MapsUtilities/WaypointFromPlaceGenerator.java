package test.collegecarpool.alpha.MapsUtilities;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

public class WaypointFromPlaceGenerator {

    private ArrayList<Waypoint> myWaypoints;

    public WaypointFromPlaceGenerator(){
        myWaypoints = new ArrayList<>();
    }

    public ArrayList<Waypoint> convertPlacesToWayPoints(ArrayList<Place> places){
        for(Place place : places){
            String placeName = String.valueOf(place.getName());
            double lat = place.getLatLng().latitude;
            double lon = place.getLatLng().longitude;
            Waypoint waypoint= new Waypoint(placeName, new LatLng(lat, lon));
            myWaypoints.add(waypoint);
        }
        return myWaypoints;
    }
}
