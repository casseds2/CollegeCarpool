package test.collegecarpool.alpha.Tools;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PolyDirections extends AsyncTask<URL, Void, ArrayList<LatLng>> {
    private static String TAG = "ROUTE DIRECTIONS";
    public Context context;
    private GoogleMap googleMap;
    private DirectionParser directionParser;
    private ArrayList<LatLng> latLngArray;

    /*Return the Direction Parser For Use in the Navigation Service*/
    public DirectionParser getDirectionParser(){
        return directionParser;
    }

    public PolyDirections(){}

    PolyDirections(Context context, GoogleMap googleMap){
        this.context = context;
        this.googleMap = googleMap;
    }

    @Override
    protected ArrayList<LatLng> doInBackground(URL... params) {
        URL url = params[0];
        latLngArray = new ArrayList<>();
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
            directionParser = new DirectionParser(jsonString);
            latLngArray = directionParser.getDirectionsAsList(); //Convert The Json String to List of LAT/LNG
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return latLngArray;
    }
    @Override
    protected void onPostExecute(ArrayList<LatLng> latLngArray) {
        super.onPostExecute(latLngArray);
        if(!Variables.SAT_NAV_ENABLED && null != latLngArray && googleMap != null) { //IF NOT IN SAT NAV MODE, EXECUTE THE DIRECTIONS
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(latLngArray).width(8).color(Color.BLUE);
            googleMap.addPolyline(polylineOptions);
            zoomPoly(latLngArray);
            Log.d(TAG, "POLYLINE DRAWN");
        }
    }

    private void zoomPoly(ArrayList<LatLng> latLngArray){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(LatLng latLng : latLngArray){
            builder.include(latLng);
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 20));
    }
}
