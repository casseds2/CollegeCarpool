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
import test.collegecarpool.alpha.Tools.PolyDirections;
import test.collegecarpool.alpha.Tools.PolyURLBuilder;
import test.collegecarpool.alpha.Tools.Variables;
import test.collegecarpool.alpha.UserClasses.UserProfile;

public class NavigationService extends Service {

    private DirectionParser directionParser;
    private final String TAG = "NAVIGATION SERVICE";
    double lat = 0;
    double lng = 0;
    ArrayList<LatLng> latLngs = new ArrayList<>();
    ArrayList<Waypoint> waypoints = new ArrayList<>();

    public NavigationService(){}

    /*Called At the Start of A Service*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Journey journey = (Journey) intent.getSerializableExtra("SelectedJourney");
        waypoints = journey.getWaypoints();
        latLngs = getLatLngsFromWaypoint(waypoints);

        URL url = new PolyURLBuilder(latLngs).buildPolyURL(); //Coming Through as NUll because can't return URL from inner class PolyURLBuilder
        Log.d(TAG, "URL IS: " + String.valueOf(url));

        return START_STICKY; //Run Until It is Specifically Stopped
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*Set Up a Listener for Location Changes*/
    public void listenForLocationChanges(){
        Log.d(TAG, "NAV LISTENING FOR LOCATION CHANGES");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                lat = userProfile.getLatitude();
                lng = userProfile.getLongitude();
                LatLng latLng = new LatLng(lat, lng); //Get the new LatLng Of the User
                latLngs.set(0, latLng); //Overwrite position one(i.e. "From" Location) to the new User Location
                /*UPDATE THE URL LINK HERE IF OFF ROUTE / LOCATION CHANGED AND GET NEW DIRECTIONS*/
                DirectionParser directionParser = new DirectionParser(); //Initializes a Direction Parser


                Log.d(TAG, "JSON is : " + directionParser.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*Obtain the Waypoints' LatLngs from the Journey Object*/
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

    /*Called First When The Service Is Started*/
    @Override
    public void onCreate(){
        super.onCreate();
        listenForLocationChanges(); //Start Listening For Location Changes
    }


    /*Called When the Service is Stopped*/
    @Override
    public void onDestroy(){
        super.onDestroy();
        this.stopSelf();
        Variables.SAT_NAV_ENABLED = false;
    }

}
