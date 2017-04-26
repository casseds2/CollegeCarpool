package test.collegecarpool.alpha.Tools;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PolyURLBuilder {

    private Context context;
    private GoogleMap googleMap;
    private URL url = null;
    private ArrayList<LatLng> places;


    private final String TAG = "POLYURL BUILDER";

    public PolyURLBuilder(Context context, GoogleMap googleMap, ArrayList<LatLng> places){
        this.context = context;
        this.googleMap = googleMap;
        this.places = places;
        Log.d(TAG, "PolyURLBuilder Initialised");
    }

    public PolyURLBuilder(ArrayList<LatLng> places){
        this.places = places;
        Log.d(TAG, "PolyURLBuilder Initialised");
    }

    /*Build A URL for Directions Request*/
    public URL buildPolyURL(){
        double lat = places.get(0).latitude;
        double lon = places.get(0).longitude;

        String origin = "https://maps.googleapis.com/maps/api/directions/json?origin=";
        String urlString = origin + lat + "," + lon + "&waypoints=optimize:true|";
        for(int i = 1; i < places.size()-1; i++){
            urlString = urlString + places.get(i).latitude + "," + places.get(i).longitude + "|";
            Log.d(TAG, urlString);
        }
        urlString = urlString + "&destination=" + places.get(places.size()-1).latitude + "," + places.get(places.size()-1).longitude;
        try {
            url = new URL(urlString);
            if(!Variables.SAT_NAV_ENABLED) //IF NOT IN SAT_NAV MODE DRAW IT
                new PolyDirections(context, googleMap).execute(url);
            Log.d(TAG, "PolyURLBuilt");
            Log.d(TAG, urlString);
        }
        catch(MalformedURLException e){
            Log.d("URLBuilder", "MalformedURL");
        }
        return url;
    }
}
