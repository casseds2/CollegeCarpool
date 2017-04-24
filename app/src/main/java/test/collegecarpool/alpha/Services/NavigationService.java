package test.collegecarpool.alpha.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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
import java.util.ArrayList;

import test.collegecarpool.alpha.MapsUtilities.Journey;
import test.collegecarpool.alpha.MapsUtilities.Waypoint;
import test.collegecarpool.alpha.Tools.DirectionParser;
import test.collegecarpool.alpha.Tools.PolyURLBuilder;
import test.collegecarpool.alpha.UserClasses.UserProfile;

public class NavigationService extends Service {

    private DirectionParser directionParser;
    private final String TAG = "NAVIGATION SERVICE";

    public NavigationService(){}

    /*Called At the Start of A Service*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Journey journey = (Journey) intent.getSerializableExtra("SelectedJourney");
        ArrayList<Waypoint> waypoints = journey.getWaypoints();
        ArrayList<LatLng> latLngs = getLatLngsFromWaypoint(waypoints);
        listenForLocationChanges();

        URL url = new PolyURLBuilder(latLngs).buildPolyURL(); //Coming Through as NUll because can't return URL from inner class PolyURLBuilder
        Log.d(TAG, "URL IS: " + String.valueOf(url));

        return START_STICKY; //Run Until It is Specifically Stopped
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void listenForLocationChanges(){
        Log.d(TAG, "NAV LISTENING FOR LOCATION CHANGES");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                double lat = userProfile.getLatitude();
                double lng = userProfile.getLongitude();

                /*UPDATE THE URL LINK HERE IF OFF ROUTE AND GET NEW DIRECTIONS*/

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<LatLng> getLatLngsFromWaypoint(ArrayList<Waypoint> waypoints){
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for(Waypoint waypoint : waypoints){
            double lat = waypoint.getLatLng().getLat();
            double lng = waypoint.getLatLng().getLng();
            LatLng latLng = new LatLng(lat, lng);
            latLngs.add(latLng);
        }
        return latLngs;
    }
}
