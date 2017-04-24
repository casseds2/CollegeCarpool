package test.collegecarpool.alpha.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import test.collegecarpool.alpha.MapsUtilities.Journey;
import test.collegecarpool.alpha.MapsUtilities.Waypoint;
import test.collegecarpool.alpha.UserClasses.UserProfile;

public class NavigationService extends Service {

    private Journey journey;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference userRef;
    private ArrayList<Waypoint> waypoints;

    public NavigationService(){}

    /*Called At the Start of A Service*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        journey = (Journey) intent.getSerializableExtra("SelectedJourney"); //The Journey the User Has Selected
        waypoints = journey.getWaypoints(); //The Waypoints of the Selected Journey
        return START_STICKY; //Run Until It is Specifically Stopped
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void listenForLocationChanges(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                double lat = userProfile.getLatitude();
                double lng = userProfile.getLongitude();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
