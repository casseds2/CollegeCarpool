package test.collegecarpool.alpha.UserClasses;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class Location {

    private LatLng latLng;

    public Location(){}

    public Location(Double lat, Double lon){
        latLng = new LatLng(lat, lon);
    }

    public LatLng getLatLng(){
        return latLng;
    }

    public HashMap<String, Double> locationToMap(LatLng latLng){
        HashMap<String, Double> location = new HashMap<>();
        location.put("Latitude", latLng.latitude);
        location.put("Longitude", latLng.longitude);
        return location;
    }
}
