package test.collegecarpool.alpha.MapsUtilities;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

public class MyPlace {

    private ArrayList<String> myPlaces;

    public MyPlace(){
        myPlaces = new ArrayList<>();
    }

    public ArrayList<String> convertPlacesToMyPlace(ArrayList<Place> places){
        for(Place place : places){
            String placeName = String.valueOf(place.getName());
            double lat = place.getLatLng().latitude;
            double lon = place.getLatLng().longitude;
            myPlaces.add(placeName);
            myPlaces.add(String.valueOf(lat));
            myPlaces.add(String.valueOf(lon));
        }
        return myPlaces;
    }
}
