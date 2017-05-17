package test.collegecarpool.alpha.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import test.collegecarpool.alpha.Firebase.FCMTokenPusher;
import test.collegecarpool.alpha.Firebase.FireLocationMapZoom;
import test.collegecarpool.alpha.LoginAndRegistrationActivities.SigninActivity;
import test.collegecarpool.alpha.MessagingActivities.ChatRoomActivity;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.Services.BackgroundLocationService;
import test.collegecarpool.alpha.MapsUtilities.ActiveUserMap;
import test.collegecarpool.alpha.Tools.GoogleClientBuilder;

import static test.collegecarpool.alpha.Tools.Variables.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class HomeScreenActivity extends AppCompatActivity implements OnMapReadyCallback, Transformation{

    private GoogleApiClient googleApiClient = null;
    private static String TAG = "HomeScreenActivity";
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private SupportMapFragment mapFragment;
    private GoogleClientBuilder googleClientBuilder;
    private ActiveUserMap activeUserMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"FCM Token is: " + fcmToken);
        new FCMTokenPusher().pushFCMToken(fcmToken);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        checkPermissions();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        initDrawer();

        Button btnPlanJourney = (Button) findViewById(R.id.plan_journey);
        btnPlanJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "PLAN JOURNEY");
                startActivity(new Intent(HomeScreenActivity.this, PlanJourneyActivity.class));
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        startService(new Intent(this, BackgroundLocationService.class));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setBuildingsEnabled(true);
        //googleMap.setTrafficEnabled(true);

        /*Display Users Broadcasting Location and PolyLines of People Travelling*/
        activeUserMap = new ActiveUserMap(this, googleMap);
        activeUserMap.displayActiveJourneys();

        /*Zoom On My Firebase Location*/
        new FireLocationMapZoom(googleMap).zoomMyLocation();
    }

    /*Checks If The User Has Given Fine Location Permissions*/
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "NO PERMISSIONS ALREADY GRANTED");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            Log.d(TAG, "PERMISSIONS ALREADY GRANTED");
            googleClientBuilder = new GoogleClientBuilder(this, googleApiClient);
            googleClientBuilder.buildLocationClient();
            googleClientBuilder.startLocationUpdates();
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapFragment.getMapAsync(this);
                    Log.d(TAG, "ACCESS TO FINE LOCATION GRANTED");
                    googleClientBuilder = new GoogleClientBuilder(this, googleApiClient);
                    googleClientBuilder.buildLocationClient();
                    googleClientBuilder.startLocationUpdates();
                }
                else {
                    Log.d(TAG, "NEED LOCATION PERMISSIONS TO BE GRANTED");
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void initDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_journey:
                        startActivity(new Intent(HomeScreenActivity.this, PlanJourneyActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_messages:
                        startActivity(new Intent(HomeScreenActivity.this, ChatRoomActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_payment:
                        startActivity(new Intent(HomeScreenActivity.this, PaymentActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_friends:
                        startActivity(new Intent(HomeScreenActivity.this, FriendActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_logout:
                        auth.signOut();
                        startActivity(new Intent(HomeScreenActivity.this, SigninActivity.class));
                        onStop();
                        return true;
                }
                return false;
            }
        });
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        activeUserMap.stopListeningForJourneys();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public Bitmap transform(Bitmap source) {
        return null;
    }

    @Override
    public String key() {
        return null;
    }
}
