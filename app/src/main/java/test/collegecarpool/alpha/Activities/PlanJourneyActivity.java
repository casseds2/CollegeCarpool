package test.collegecarpool.alpha.Activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.MalformedURLException;
import java.net.URL;

import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.Tools.DrawDirections;
import test.collegecarpool.alpha.UserClasses.UserProfile;

public class PlanJourneyActivity extends FragmentActivity implements OnMapReadyCallback {

    private final static String TAG = "PlanJourneyActivity";
    private final static String APIKEY = "AIzaSyD7LLJg_QOR-VzqRPYaXazOnbJHBgiQd3k";

    private UserProfile userProfile = new UserProfile();
    private double lat, lon;

    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_journey);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        if(user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userProfile = dataSnapshot.getValue(UserProfile.class);
                    lat = userProfile.getLatitude();
                    lon = userProfile.getLongitude();

                    /**getMapAsync() Triggers onMapReady, NEED TO GET LAT/LNG FIRST!!!!!**/
                    mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(PlanJourneyActivity.this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + lat + "," + lon + "&destination=53.385713,-6.231124&mode=walking&key=" + APIKEY);
            Log.d(TAG, String.valueOf(url));
            DrawDirections routeDirections = new DrawDirections(this, googleMap);
            routeDirections.execute(url);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(53.386123, -6.254953))
                    .zoom(15)
                    .tilt(20)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        catch (MalformedURLException e) {
            Log.d(TAG, "MALFORMED URL");
        }
    }
}
