package test.collegecarpool.alpha.Tools;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import test.collegecarpool.alpha.UserClasses.UserProfile;

public class PolyURLBuilder {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference userRef;
    private URL url = null;
    private String urlString;
    private String origin = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    private UserProfile userProfile;
    private double lat, lon;
    private Context context;
    private GoogleMap googleMap;
    private ArrayList<LatLng> places;

    private final String TAG = "POLYURL BUILDER";

    public PolyURLBuilder(Context context, GoogleMap googleMap, ArrayList<LatLng> places){
        this.context = context;
        this.googleMap = googleMap;
        this.places = places;
    }

    private void initFirebase(){
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
        }
    }

    public URL buildPolyURL(){
        initFirebase();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable <DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot dataSnapshot1 : dataSnapshots) {
                    UserProfile temp = dataSnapshot1.getValue(UserProfile.class);
                    if(auth.getCurrentUser() != null) {
                        if (temp.getEmail().equals(auth.getCurrentUser().getEmail())) {
                            userProfile = dataSnapshot1.getValue(UserProfile.class);
                            lat = userProfile.getLatitude();
                            lon = userProfile.getLongitude();

                            urlString = origin + lat + "," + lon + "&waypoints=optimize:true|";
                            for(int i = 0; i < places.size()-1; i++){
                                urlString = urlString + places.get(i).latitude + "," + places.get(i).longitude + "|";
                                Log.d(TAG, urlString);
                            }
                            urlString = urlString + "&destination=" + places.get(places.size()-1).latitude + "," + places.get(places.size()-1).longitude;
                            Log.d(TAG, urlString);
                            try {
                                url = new URL(urlString);
                                PolyDirections polyDirections = new PolyDirections(context, googleMap);
                                polyDirections.execute(url);
                                Log.d(TAG, "PolyURLBuilt");
                            }
                            catch(MalformedURLException e){
                                Log.d("URLBuilder", "MalformedURL");
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d(TAG, String.valueOf(url));
        return url;
    }
}
