package test.collegecarpool.alpha.MapsUtilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import test.collegecarpool.alpha.Firebase.RideRequest;
import test.collegecarpool.alpha.MessagingActivities.MessageActivity;
import test.collegecarpool.alpha.R;

public class ActiveUserMap{

    private GoogleMap googleMap;
    private static String TAG = "ACTIVE USER MAP";
    private PolylineOptions polylineOptions;
    private ArrayList<LatLng> polyLatLngs;
    private ArrayList <LatLng> polyWaypoints;
    private ArrayList<Marker> markers;
    private HashMap<String, ArrayList<LatLng>> userAndMarkers;
    private DatabaseReference activeJourneysRef;
    private ValueEventListener activeListener;
    private String userID;
    private RideRequest rideRequest;

    public ActiveUserMap(final Context context, final GoogleMap googleMap){
        this.googleMap = googleMap;
        polyLatLngs = new ArrayList<>();
        polyWaypoints = new ArrayList<>();
        rideRequest = new RideRequest();
        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                Log.d(TAG, "Polyline Clicked");
                polyWaypoints = new ArrayList<>();
                markers = new ArrayList<>();
                userAndMarkers = (HashMap<String, ArrayList<LatLng>>) polyline.getTag();
                drawPolyWaypoints();
                //Toast.makeText(context, "Polyline Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(markers != null){
                    for(Marker marker : markers){
                        marker.remove();
                    }
                    Toast.makeText(context, "Removed Markers", Toast.LENGTH_SHORT).show();
                }
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.Theme_AppCompat_DayNight))
                        .setTitle("What Would You Like To Do?")
                        .setPositiveButton("Message user", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, MessageActivity.class);
                                intent.putExtra("ReceiverID", (String) marker.getTag());
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton("Request Ride", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                rideRequest.requestRide((String) marker.getTag());
                                Toast.makeText(context, "Ride Request Sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.create().show();
                return false;
            }
        });
    }

    private void drawPolyWaypoints() {
        for(Map.Entry entry : userAndMarkers.entrySet()){
            for(LatLng latLng : (ArrayList<LatLng>) entry.getValue()){
                Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng));
                marker.setTag(entry.getKey());
                markers.add(marker);
            }
        }
    }

    public void stopListeningForJourneys(){
        activeJourneysRef.removeEventListener(activeListener);
        Log.d(TAG, "STOPPED VALUE EVENT LISTENER");
    }

    /*Listen For Any Users Who Are Travelling*/
    public void displayActiveJourneys(){
        activeJourneysRef = FirebaseDatabase.getInstance().getReference("ActiveJourneys");
        activeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                googleMap.clear();
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot dataSnapshot1 : dataSnapshots){ //Cycle Through User IDs
                    userID = dataSnapshot1.getKey();
                    polyLatLngs = new ArrayList<>();
                    polyWaypoints = new ArrayList<>();
                    userAndMarkers = new HashMap<>();
                    Iterable<DataSnapshot> dataSnapshots1 = dataSnapshot1.getChildren();
                    for(DataSnapshot dataSnapshot2 : dataSnapshots1){ //Cycle Through Markers and Poly Lines
                        if(dataSnapshot2.getKey().equals("Markers")){
                            Iterable<DataSnapshot> dataSnapshots2 = dataSnapshot2.getChildren();
                            for(DataSnapshot dataSnapshot3 : dataSnapshots2){ //Cycle Through Elements of The Markers List
                                test.collegecarpool.alpha.MapsUtilities.LatLng myLatLng = dataSnapshot3.getValue(test.collegecarpool.alpha.MapsUtilities.LatLng.class);
                                Log.d(TAG, "Lat id: " + myLatLng.toString());
                                LatLng latLng = myLatLng.toGoogleLatLng();
                                polyWaypoints.add(latLng);
                                Log.d(TAG, "Waypoint is: " + latLng.toString());
                            }
                        }
                        if(dataSnapshot2.getKey().equals("Polyline")){
                            String encodedPoly = dataSnapshot2.getValue(String.class);
                            Log.d(TAG, "Encoded Poly Is " + encodedPoly);
                            polyLatLngs = (ArrayList<LatLng>) PolyUtil.decode(encodedPoly);
                            Log.d(TAG, "Decoded Poly Is: " + polyLatLngs.toString());
                        }
                    }
                    /*Set Up How Polyline Looks*/
                    polylineOptions = new PolylineOptions();
                    polylineOptions.addAll(polyLatLngs);
                    polylineOptions.width(10);
                    polylineOptions.color(Color.BLUE);
                    polylineOptions.clickable(true);
                    polylineOptions.isClickable();
                    polylineOptions.geodesic(true);
                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                    /*Set PolyLien Styles - New Update*/
                    polyline.setEndCap(new RoundCap());
                    polyline.setJointType(JointType.ROUND);
                    userAndMarkers.put(userID, polyWaypoints);
                    /*Associate A User ID and Waypoints List With A PolyLine*/
                    polyline.setTag(userAndMarkers);
                    Log.d(TAG, "Polyline is: " + polyline.toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        activeJourneysRef.addValueEventListener(activeListener);
    }
}
