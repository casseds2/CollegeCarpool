package test.collegecarpool.alpha.MapsUtilities;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import test.collegecarpool.alpha.UserClasses.UserProfile;

import static test.collegecarpool.alpha.Tools.Variables.shouldZoom;

public class ActiveUserMap {

    private GoogleMap googleMap;
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private static String TAG = "ACTIVE USER MAP";

    public ActiveUserMap(GoogleMap googleMap){
        this.googleMap = googleMap;
    }

    public void displayUserLocations(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable <DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                if(googleMap != null)
                    googleMap.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshots){
                    UserProfile userProfile = dataSnapshot1.getValue(UserProfile.class);
                    if(auth.getCurrentUser() != null && userProfile.getEmail().equals(auth.getCurrentUser().getEmail()) && shouldZoom){
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(userProfile.getLatitude(), userProfile.getLongitude()))// Sets the center of the map to location user
                                .zoom(15) // Sets the zoom
                                .build();
                        Log.d(TAG, "Marker Added For User, Zoom: " + shouldZoom);
                        if(googleMap != null)
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        shouldZoom = false;
                    }
                    else {
                        if(userProfile.getBroadcastLocation()) {
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(userProfile.getLatitude(), userProfile.getLongitude())).title(userProfile.getFirstName()));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
