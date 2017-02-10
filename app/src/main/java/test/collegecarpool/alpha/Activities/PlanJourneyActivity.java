package test.collegecarpool.alpha.Activities;

import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import test.collegecarpool.alpha.R;

public class PlanJourneyActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    final static String TAG = "PlanJourneyActivity";

    final static String APIKEY = "AIzaSyD7LLJg_QOR-VzqRPYaXazOnbJHBgiQd3k";
    private DatabaseReference dirRef;
    private FirebaseAuth auth;
    private URL url;
    private HttpURLConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_journey);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        auth = FirebaseAuth.getInstance();
        dirRef = FirebaseDatabase.getInstance().getReference("TestDirection");
        try {
            String jsonString = getDirectionsAsString("https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood4&key=" + APIKEY);
            HashMap<String, String> directionMap = new HashMap<>();
            directionMap.put(auth.getCurrentUser().getUid(), jsonString);
            dirRef.setValue(directionMap);
        }
        catch (IOException e) {
            Log.d(TAG, "COULDN'T RETRIEVE JSON");
        }

    }

    public String append(String a, String b){
        a = a + b;
        return a;
    }

    public String getDirectionsAsString(String s) throws IOException {
        url = new URL (s);
        connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        BufferedInputStream stream = new BufferedInputStream(connection.getInputStream());
        BufferedReader read = new BufferedReader(new InputStreamReader(stream));
        //JsonReader reader = new JsonReader(new InputStreamReader(stream));
        String jsonString = "";
        String nextLine = read.readLine();
        //String next = reader.nextString();
        while(nextLine != null){
            jsonString = append(jsonString, nextLine);
            nextLine = read.readLine();
        }
        read.close();
        stream.close();
        //reader.close();
        connection.disconnect();
        return jsonString;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
