package test.collegecarpool.alpha.MapsUtilities;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

public class MyPlace {

    ArrayList<String> myPlaces;

    public MyPlace(){
        myPlaces = new ArrayList<>();
    }

    public ArrayList<String> convertPlacesToMyPlace(ArrayList<Place> places){
        for(Place place : places){
            String placeName = (String) place.getName();
            myPlaces.add(placeName);
        }
        return myPlaces;
    }
}
