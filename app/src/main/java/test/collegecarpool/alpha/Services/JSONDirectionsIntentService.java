package test.collegecarpool.alpha.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

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
import java.util.HashMap;
import java.util.List;

/**
 * Created by casseds95 for 4TH YEAR Project on 13/02/2017.
 */

public class JSONDirectionsIntentService extends IntentService {

    final static String APIKEY = "AIzaSyD7LLJg_QOR-VzqRPYaXazOnbJHBgiQd3k";
    private DatabaseReference dirRef;
    private FirebaseAuth auth;
    private URL url;
    private HttpURLConnection connection;
    final static String TAG = "DIRECTIONS SERVICE";

    JSONDirectionsIntentService(String name){ super(name); }

    JSONDirectionsIntentService(){ super("JSONDirectionsIntentService"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        auth = FirebaseAuth.getInstance();
        dirRef = FirebaseDatabase.getInstance().getReference("TestDirection");
        try {
            String jsonString = getDirectionsAsString("https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood4&key=" + APIKEY);
            HashMap<String, String> directionMap = new HashMap<>();
            if(auth.getCurrentUser() != null) {
                directionMap.put(auth.getCurrentUser().getUid(), jsonString);
            }
            dirRef.setValue(directionMap);
        }
        catch (IOException e) {
            Log.d(TAG, "COULDN'T RETRIEVE JSON");
        }
    }

    public String getDirectionsAsString(String s) throws IOException {
        url = new URL(s);
        connection = (HttpURLConnection) url.openConnection();
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
        return jsonString;
    }

    public List<List<HashMap<String, String>>> convertToJson(String s){
        JSONObject jsonObject;
        List<List<HashMap<String, String>>> directionsList = null;
        try {
            jsonObject = new JSONObject(s);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return  directionsList;
    }
}
