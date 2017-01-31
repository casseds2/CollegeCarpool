package test.collegecarpool.alpha.Services;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by casseds95 on 30/01/2017.
 */

public class BackgroundLocationIntentService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private FirebaseAuth auth;
    private DatabaseReference userRef;

    public static boolean continueThread; //http://stackoverflow.com/questions/11258083/how-to-force-an-intentservice-to-stop-immediately-with-a-cancel-button-from-an-a
    public static boolean pauseThread;

    private static final String TAG = "LocationIntentService";

    public BackgroundLocationIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        showToast("Starting Background Location Intent Service");
        if (checkGooglePlayServicesAvailable()) {
            buildGoogleClient();
            googleApiClient.connect();
            setLocationRequestParams();
        }
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
        auth = FirebaseAuth.getInstance();
    }

    public BackgroundLocationIntentService(){
        this(BackgroundLocationIntentService.class.getName());
    }

    protected void showToast(final String msg){
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // run this code in the main thread
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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

    public boolean checkGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);
        return result == ConnectionResult.SUCCESS;
    }

    public void setLocationRequestParams() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10 * 1000); //Once a Minute
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setFastestInterval(15 * 1000); //Once every 15 Seconds
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            requestLocationUpdates();
        }
        catch(InterruptedException e){
            Log.d(TAG, "Error with location intent thread");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        pushLocationToFirebase(latitude, longitude);
    }

    private void requestLocationUpdates() throws InterruptedException {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        if(!continueThread){
            showToast("Thread Killed!");
            stopSelf();
        }
        if(pauseThread){
            while(pauseThread){
                showToast("Thread Paused");
                Thread.sleep(1000);
            }
        }
    }

    private void pushLocationToFirebase(double latitude, double longitude){
        userRef.child(auth.getCurrentUser().getUid()).child("longitude").setValue(longitude);
        userRef.child(auth.getCurrentUser().getUid()).child("latitude").setValue(latitude);
    }
}
