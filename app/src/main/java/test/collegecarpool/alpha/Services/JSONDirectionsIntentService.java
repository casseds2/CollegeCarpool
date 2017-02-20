package test.collegecarpool.alpha.Services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import test.collegecarpool.alpha.UserClasses.DirectionParser;

/**
 * Created by casseds95 for 4TH YEAR Project on 13/02/2017.
 */

public class JSONDirectionsIntentService extends IntentService {

    final static String APIKEY = "AIzaSyD7LLJg_QOR-VzqRPYaXazOnbJHBgiQd3k";
    final static String TAG = "DIRECTIONS SERVICE";
    public static PolylineOptions polyLineOptions = null;

    public JSONDirectionsIntentService(String name){ super(name); }

    public JSONDirectionsIntentService(){ super("JSONDirectionsIntentService"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        DatabaseReference dirRef = FirebaseDatabase.getInstance().getReference("TestDirection");
        try {
            ArrayList <LatLng> jsonDirections = getDirectionsAsList("https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood4&key=" + APIKEY);
            dirRef.child("TEST DIRECTION").setValue(jsonDirections);
            for(int i = 0; i < jsonDirections.size(); i++){
                LatLng latLng = jsonDirections.get(i);
                polyLineOptions = new PolylineOptions()
                        .add(latLng)
                        .width(5)
                        .color(Color.BLUE);
                System.out.println("Latitude: " + latLng.latitude + ", Longitude: " + latLng.longitude);
                Log.d(TAG, String.valueOf(polyLineOptions));
            }
        }
        catch (IOException e) {
            Log.d(TAG, "COULDN'T RETRIEVE JSON");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "HANDLED SERVICE");
    }

    public ArrayList<LatLng> getDirectionsAsList(String s) throws IOException, JSONException {
        DirectionParser directionParser = new DirectionParser();
        URL url = new URL(s);
        ArrayList<LatLng> latLngArray;
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        BufferedInputStream stream = new BufferedInputStream(connection.getInputStream());
        BufferedReader read = new BufferedReader(new InputStreamReader(stream));
        String jsonString = "";
        String nextLine = read.readLine();
        while(nextLine != null){
            jsonString = jsonString + " " + nextLine;
            nextLine = read.readLine();
        }
        read.close();
        stream.close();
        connection.disconnect();
        Log.i(TAG, jsonString);
        JSONObject jsonObject = new JSONObject(jsonString);
        latLngArray = directionParser.getDirectionsAsList(jsonObject);
        return latLngArray;
    }
}
