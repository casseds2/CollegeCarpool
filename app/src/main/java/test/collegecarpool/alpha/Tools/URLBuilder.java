package test.collegecarpool.alpha.Tools;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;

import test.collegecarpool.alpha.UserClasses.UserProfile;

public class URLBuilder {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference userRef;
    private URL url;
    private String urlString;
    private String urlStart = "https://maps.googleapis.com/maps/api/directions/json?";
    private String origin = "origin=";
    private UserProfile userProfile;
    private double lat, lon;

    private final String TAG = "URL BUILDER";

    public URLBuilder(){}

    public URL URLBuilder(LatLng one){ //May need to be changed to linked list of places from PLANJOURNEY
        /*MAY NOT NEED DIRECTION TO BE FROM USER LOCATION, THIS IS TEMPORARY JUST TO SEE IF IT WORKS*/
        userProfile = getMyUser();
        lat = userProfile.getLatitude();
        lon = userProfile.getLongitude();
        urlString = urlStart + origin + lat + "," + lon + "&";
        try {
            url = new URL(urlString);
        }
        catch (Exception e){
            Log.d(TAG, "MALFORMED URL");
        }
        return url;
    }

    public URL URLBuilder(LatLng one, LatLng two){
        userProfile = getMyUser();
        lat = userProfile.getLatitude();
        lon = userProfile.getLongitude();
        urlString = urlStart + origin + lat + "," + lon + "&";
        try {
            url = new URL(urlString);
        }
        catch (Exception e){
            Log.d(TAG, "MALFORMED URL");
        }
        return url;
    }

    public URL URLBuilder(LatLng one, LatLng two, LatLng three){
        userProfile = getMyUser();
        lat = userProfile.getLatitude();
        lon = userProfile.getLongitude();
        urlString = urlStart + origin + lat + "," + lon + "&";
        try {
            url = new URL(urlString);
        }
        catch (Exception e){
            Log.d(TAG, "MALFORMED URL");
        }
        return url;
    }

    public URL URLBuilder(LatLng  one, LatLng two, LatLng three, LatLng four){
        userProfile = getMyUser();
        lat = userProfile.getLatitude();
        lon = userProfile.getLongitude();
        urlString = urlStart + origin + lat + "," + lon + "&";
        try {
            url = new URL(urlString);
        }
        catch (Exception e){
            Log.d(TAG, "MALFORMED URL");
        }
        return url;
    }

    private void initFirebase(FirebaseAuth auth){
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            FirebaseUser firebaseUser = auth.getCurrentUser();
            userRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(firebaseUser.getUid()).child("Location");
        }
    }

    private UserProfile getMyUser(){
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable <DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot dataSnapshot1 : dataSnapshots) {
                    UserProfile temp = dataSnapshot1.getValue(UserProfile.class);
                    if(temp.getEmail().equals(auth.getCurrentUser().getEmail()))
                        userProfile = temp;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return userProfile;
    }
}
