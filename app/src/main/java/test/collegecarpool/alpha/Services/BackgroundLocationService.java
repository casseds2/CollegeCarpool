package test.collegecarpool.alpha.Services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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

public class BackgroundLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;


    private ServiceHandler serviceHandler;

    private FirebaseAuth auth;
    private DatabaseReference userRef;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
    }

    @Override
    public void onCreate() {
        HandlerThread handlerThread = new HandlerThread("BackgroundLocationService", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        serviceHandler = new ServiceHandler(looper);
        if (checkGooglePlayServicesAvailable()) {
            buildGoogleClient();
            setLocationRequestParams();
        }
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
        auth = FirebaseAuth.getInstance();
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        showToast("onStartCommand");
        Message message = serviceHandler.obtainMessage();
        message.arg1 = startID;
        serviceHandler.sendMessage(message);

        googleApiClient.connect();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopSelf();
        showToast("ServiceStopped");
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
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

    private void requestLocationUpdates() {
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
    }

    private void pushLocationToFirebase(double latitude, double longitude){
        userRef.child(auth.getCurrentUser().getUid()).child("longitudeFromUser").setValue(longitude);
        userRef.child(auth.getCurrentUser().getUid()).child("latitudeFromUser").setValue(latitude);
    }
}
