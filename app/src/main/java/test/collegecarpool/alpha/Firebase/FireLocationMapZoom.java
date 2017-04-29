package test.collegecarpool.alpha.Firebase;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import test.collegecarpool.alpha.UserClasses.UserProfile;

public class FireLocationMapZoom {

    private GoogleMap googleMap;
    private FirebaseUser user;
    private FirebaseAuth auth;

    public FireLocationMapZoom(GoogleMap googleMap){
        this.googleMap = googleMap;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    /*Sets Camera To Follow User Around*/
    private CameraPosition setCamera(LatLng userLatLng){
        return new CameraPosition.Builder()
                .target(userLatLng)// Sets the center of the map to location user
                .zoom(16) // Sets the zoom
                .build();
    }

    public void zoomMyLocation(){
        if(auth != null) {
            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid());
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(setCamera(new LatLng(userProfile.getLatitude(), userProfile.getLongitude()))));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
