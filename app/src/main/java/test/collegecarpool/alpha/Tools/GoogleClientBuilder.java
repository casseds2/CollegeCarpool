package test.collegecarpool.alpha.Tools;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class GoogleClientBuilder extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    private GoogleApiClient googleApiClient;
    private static String TAG = "GOOGLE CLIENT BUILDER";
    private LocationSettings locationSettings = new LocationSettings(context);

    public GoogleClientBuilder(Context context, GoogleApiClient googleApiClient){
        this.context = context;
        this.googleApiClient = googleApiClient;
    }

    /*Google Client Builder For Location*/
    public void buildLocationClient() {
        if(googleApiClient == null && checkGooglePlayServicesAvailable()) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            googleApiClient.connect();
            new GPSChecker(context, googleApiClient).checkGPS();
            Log.d(TAG, "LOCATION CLIENT BUILT");
        }
        else
            Log.d(TAG, "ERROR BUILDING CLIENT");
    }

    public boolean checkGooglePlayServicesAvailable() {
        Log.d(TAG, "CHECKING PLAY SERVICES");
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return result == ConnectionResult.SUCCESS;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
            locationSettings.requestLocationUpdates(googleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GOOGLE CLIENT SUSPENDED");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "GOOGLE CLIENT UNABLE TO CONNECT");
    }
}
