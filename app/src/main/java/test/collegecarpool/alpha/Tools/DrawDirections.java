package test.collegecarpool.alpha.Tools;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DrawDirections extends AsyncTask<URL, Void, ArrayList<LatLng>> {

    private static String TAG = "ROUTE DIRECTIONS";
    public Context context;
    private GoogleMap googleMap;

    public DrawDirections(Context context, GoogleMap googleMap){
        this.context = context;
        this.googleMap = googleMap;
    }

    @Override
    protected ArrayList<LatLng> doInBackground(URL... params) {
        DirectionParser directionParser = new DirectionParser();
        URL url = params[0];
        ArrayList<LatLng> latLngArray = new ArrayList<>();
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            assert connection != null;
            connection.connect();
            BufferedInputStream stream = new BufferedInputStream(connection.getInputStream());
            BufferedReader read = new BufferedReader(new InputStreamReader(stream));
            String jsonString = "";
            String nextLine = read.readLine();
            while (nextLine != null) {
                jsonString = jsonString + " " + nextLine;
                nextLine = read.readLine();
            }
            read.close();
            stream.close();
            connection.disconnect();
            Log.i(TAG, jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            latLngArray = directionParser.getDirectionsAsList(jsonObject);
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return latLngArray;
    }

    @Override
    protected void onPostExecute(ArrayList<LatLng> latLngArray) {
        super.onPostExecute(latLngArray);
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(latLngArray).width(5).color(Color.BLUE);
        googleMap.addPolyline(polylineOptions);
        Log.d(TAG, "POLYLINE DRAWN");
    }
}
