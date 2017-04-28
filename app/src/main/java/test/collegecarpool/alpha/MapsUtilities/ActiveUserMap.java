package test.collegecarpool.alpha.MapsUtilities;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import test.collegecarpool.alpha.UserClasses.UserProfile;

import static test.collegecarpool.alpha.Tools.Variables.shouldZoom;

public class ActiveUserMap{

    private GoogleMap googleMap;
    private Context context;
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
    private FirebaseAuth auth;
    private FirebaseUser user;
    private static String TAG = "ACTIVE USER MAP";
    private PolylineOptions polylineOptions;
    private HashMap<Polyline, ArrayList<LatLng>> polyLines;
    ArrayList<LatLng> tempPoly;
    ArrayList <LatLng> waypoints;

    public ActiveUserMap(final Context context, final GoogleMap googleMap){
        this.context = context;
        this.googleMap = googleMap;
        polyLines = new HashMap<>();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        tempPoly = new ArrayList<>();
        waypoints = new ArrayList<>();
        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                Log.d(TAG, "Polyline Clicked");
                showPolyLineWaypoints(polyline);
                Toast.makeText(context, "Polyline Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                Log.d(TAG, "Map Cleared");
                refreshActiveJourneys();
            }
        });
    }

    /*Triggers A Data Change to Redisplay The Map*/
    private void refreshActiveJourneys() {
        DatabaseReference activeJourneys = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> flagMap = new HashMap<>();
        flagMap.put("/ActiveJourneys/Flag/", 1);
        activeJourneys.updateChildren(flagMap);
        flagMap = new HashMap<>();
        flagMap.put("/ActiveJourneys/Flag/", 0);
        activeJourneys.updateChildren(flagMap);
    }

    /*Display Waypoints Associated With A Polyline*/
    private void showPolyLineWaypoints(Polyline polyline) {
        for(Map.Entry<Polyline, ArrayList<LatLng>> polyMarker : polyLines.entrySet()){
            Polyline poly = polyMarker.getKey();
            if(poly.equals(polyline)){
                ArrayList<LatLng> markers = polyMarker.getValue();
                for(LatLng latlng : markers){
                    googleMap.addMarker(new MarkerOptions().position(latlng));
                }
            }
            else
                poly.remove();
        }
    }

    /*Listen For Any Users Who Are Travelling*/
    public void displayActiveJourneys(){
        DatabaseReference activeJourneys = FirebaseDatabase.getInstance().getReference("ActiveJourneys");
        activeJourneys.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot dataSnapshot1 : dataSnapshots){ //Cycle Through User IDs
                    Iterable<DataSnapshot> dataSnapshots1 = dataSnapshot1.getChildren();
                    for(DataSnapshot dataSnapshot2 : dataSnapshots1){ //Cycle Through Markers and Poly Lines
                        if(dataSnapshot2.getKey().equals("Markers")){
                            Iterable<DataSnapshot> dataSnapshots2 = dataSnapshot2.getChildren();
                            for(DataSnapshot dataSnapshot3 : dataSnapshots2){ //Cycle Through Elements of The Markers List
                                test.collegecarpool.alpha.MapsUtilities.LatLng myLatLng = dataSnapshot3.getValue(test.collegecarpool.alpha.MapsUtilities.LatLng.class);
                                Log.d(TAG, "Lat id: " + myLatLng.toString());
                                LatLng latLng = myLatLng.toGoogleLatLng();
                                waypoints.add(latLng);
                                Log.d(TAG, "Waypoint is: " + latLng.toString());
                            }
                        }
                        if(dataSnapshot2.getKey().equals("Polyline")){
                            String encodedPoly = dataSnapshot2.getValue(String.class);
                            Log.d(TAG, "Encoded Poly Is " + encodedPoly);
                            tempPoly = (ArrayList<LatLng>) PolyUtil.decode(encodedPoly);
                            Log.d(TAG, "Decoded Poly Is: " + tempPoly.toString());
                        }
                    }
                    Log.d(TAG, "Decoded Poly Is: " + tempPoly.toString());
                    Log.d(TAG, "Waypoints Is: " + waypoints.toString());
                    polylineOptions = new PolylineOptions();
                    polylineOptions.addAll(tempPoly);
                    polylineOptions.width(10);
                    polylineOptions.color(Color.BLUE);
                    polylineOptions.clickable(true);
                    polylineOptions.isClickable();
                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                    Log.d(TAG, "Polyline is: " + polyline.toString());
                    polyLines.put(polyline, waypoints);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void displayUserLocations(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable <DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot dataSnapshot1 : dataSnapshots){
                    UserProfile userProfile = dataSnapshot1.getValue(UserProfile.class);
                    if(auth.getCurrentUser() != null && auth.getCurrentUser() == user && shouldZoom){
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(userProfile.getLatitude(), userProfile.getLongitude()))// Sets the center of the map to location user
                                .zoom(15)
                                .build();
                        Log.d(TAG, "Marker Added For User at " + userProfile.getLatitude() + "/" + userProfile.getLongitude() + ", Zoom: " + shouldZoom);
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        shouldZoom = false;
                    }
                    else {
                        if(userProfile.getBroadcastLocation()) {
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(userProfile.getLatitude(), userProfile.getLongitude())).title(userProfile.getFirstName()));
                        }
                    }
                }
                /*Redraw Poly lines*/
                displayActiveJourneys();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
