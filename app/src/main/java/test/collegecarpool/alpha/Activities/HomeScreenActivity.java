package test.collegecarpool.alpha.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.Services.BackgroundLocationIntentService;
import test.collegecarpool.alpha.UserClasses.UserProfile;

public class HomeScreenActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    final static String TAG = "HomeScreenActivity";

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean broadcastIsClicked = false;

    private Intent intentServiceLocation;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Button btnBroadcast;
    private Button btnFindMe;

    private FirebaseAuth firebaseUserAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userRef;
    private DatabaseReference broadcastRef;

    private double personalLat = 0;
    private double personalLong = 0;

    private boolean shouldZoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        shouldZoom = true;

        //Button broadcastBtn = (Button) findViewById(R.id.broadcast_location);

        /**If User has Not Signed Out Specifically, Their Auth Instance Will Remain and They Can Skip Login**/
        firebaseUserAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseUserAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
        broadcastRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(firebaseUser.getUid()).child("broadcastLocation");

        btnBroadcast = (Button) findViewById(R.id.broadcast_location);
        btnFindMe = (Button) findViewById(R.id.find_me);

        broadcastRef.setValue(false);

        if(firebaseUser == null){
            startActivity(new Intent(this, SigninActivity.class));
            finish();
        }
        else{
            intentServiceLocation = new Intent(this, BackgroundLocationIntentService.class);
            startService(intentServiceLocation);
            displayUserLocations();
            //checkGPS();
            initDrawer();

            btnBroadcast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Broadcast Button Pressed");
                    broadcastRef.setValue(true);
                    broadcastIsClicked = true;
                }
            });

            btnFindMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(personalLat, personalLong))// Sets the center of the map to location user
                            .zoom(15) // Sets the zoom
                            .tilt(20) // Sets the tilt of the camera to 30 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });
        }
    }

    public void initDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_logout:
                        firebaseUserAuth.signOut();
                        startActivity(new Intent(HomeScreenActivity.this, SigninActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(HomeScreenActivity.this, ProfileActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_messages:
                        startActivity(new Intent(HomeScreenActivity.this, ChatRoomActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_settings:
                        startActivity(new Intent(HomeScreenActivity.this, SettingsActivity.class));
                        onStop();
                        return true;
                }
                return false;
            }
        });
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions();
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        displayUserLocations();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Location Permissions Granted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Need Location Permissions Granted", Toast.LENGTH_SHORT).show();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void displayUserLocations(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable <DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                mMap.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshots){
                    UserProfile userProfile = dataSnapshot1.getValue(UserProfile.class);
                    if(userProfile.getEmail().equals(firebaseUserAuth.getCurrentUser().getEmail()) && shouldZoom){
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(userProfile.getLatitude(), userProfile.getLongitude()))// Sets the center of the map to location user
                                .zoom(15) // Sets the zoom
                                .tilt(20) // Sets the tilt of the camera to 30 degrees
                                .build();
                        // Creates a CameraPosition from the builder
                        //mMap.addMarker(new MarkerOptions().position(new LatLng(userProfile.getLatitude(), userProfile.getLongitude())).title(userProfile.getFirstName()));
                        personalLat = userProfile.getLatitude();
                        personalLong = userProfile.getLongitude();
                        Log.d(TAG, "Marker Added For User, Zoom: " + shouldZoom);
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        shouldZoom = false;
                    }
                    else {
                        if(userProfile.getBroadcastLocation()) {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(userProfile.getLatitude(), userProfile.getLongitude())).title(userProfile.getFirstName()));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void buildGoogleClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }
    public void checkGPS(){
        buildGoogleClient();
        googleApiClient.connect();
        createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()){
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try{
                            status.startResolutionForResult((Activity) getApplicationContext(), 1000);
                        }
                        catch(Exception e){

                        }
                }
            }
        });
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BackgroundLocationIntentService.stopThread = false;
        if(broadcastIsClicked)
            userRef.child(firebaseUserAuth.getCurrentUser().getUid()).child("broadcastLocation").setValue(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BackgroundLocationIntentService.stopThread = true; //Will kill the location thread
        if(firebaseUserAuth.getCurrentUser() != null)
            broadcastRef.setValue(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundLocationIntentService.pauseThread = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackgroundLocationIntentService.pauseThread = true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
