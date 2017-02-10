package test.collegecarpool.alpha.Services;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

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
 * Created by casseds95 30/01/2017.
 */

public class BackgroundLocationIntentService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference userRef;

    public static volatile boolean stopThread = false; //http://stackoverflow.com/questions/11258083/how-to-force-an-intentservice-to-stop-immediately-with-a-cancel-button-from-an-a
    public static volatile boolean pauseThread = false;

    private static final String TAG = "LocationIntentService";

    public BackgroundLocationIntentService(String name) {
        super(name);
    }

    public BackgroundLocationIntentService(){
        super("BackgroundLocationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (checkGooglePlayServicesAvailable()) {
            buildGoogleClient();
            googleApiClient.connect();
            setLocationRequestParams();
        }
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
        auth = FirebaseAuth.getInstance();
        Log.d(TAG, "HANDLED THREAD");
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
        locationRequest.setInterval(2 * 1000); //Once every 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(15 * 1000); //Once every 15 Seconds
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            if(!pauseThread && !stopThread) {
                requestLocationUpdates();
                Log.d(TAG, "REQUESTING LOCATION");
            }
            else{
                Log.d(TAG, "THREAD STOPPED FOR SOME REASON");
            }
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
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    private void pushLocationToFirebase(double latitude, double longitude){
        if(auth.getCurrentUser() != null) {
            userRef.child(auth.getCurrentUser().getUid()).child("longitude").setValue(longitude);
            userRef.child(auth.getCurrentUser().getUid()).child("latitude").setValue(latitude);
        }
    }
}
