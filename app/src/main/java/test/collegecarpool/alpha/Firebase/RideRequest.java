package test.collegecarpool.alpha.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import test.collegecarpool.alpha.UserClasses.Time;
import test.collegecarpool.alpha.UserClasses.UserProfile;

public class RideRequest {

    private FirebaseUser user;
    private double lat, lng;
    private String userName;

    public RideRequest(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth != null)
            user = auth.getCurrentUser();
        getMyInfo();
    }

    public void requestRide(String riderID){
        DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference("UserProfile/" + riderID);
        HashMap<String, Object> request = new HashMap<>();
        HashMap<String, Object> info = new HashMap<>();
        Time time = new Time();
        info.put("lat", String.valueOf(lat));
        info.put("lng", String.valueOf(lng));
        info.put("time", time.toString());
        info.put("user", user.getUid());
        info.put("username", userName);
        request.put("/RideRequests/" + user.getUid(), info);
        riderRef.updateChildren(request);
    }

    private void getMyInfo(){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                userName = userProfile.getFirstName() + " " + userProfile.getSecondName();
                lat = userProfile.getLatitude();
                lng = userProfile.getLongitude();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
