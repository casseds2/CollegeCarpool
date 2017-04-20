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

import test.collegecarpool.alpha.Services.BackgroundLocationIntentService;

public class GoogleClientBuilder extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    private GoogleApiClient googleApiClient;
    private static String TAG = "GOOGLE CLIENT BUILDER";
    private LocationSettings locationSettings = new LocationSettings(context);

    public GoogleClientBuilder(Context context, GoogleApiClient googleApiClient){
        this.context = context;
        this.googleApiClient = googleApiClient;
    }

    /**Google Client Builder For Location**/
    public void buildLocationClient() {
        if(googleApiClient == null && checkGooglePlayServicesAvailable()) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            googleApiClient.connect();
            GPSChecker gpsChecker = new GPSChecker(context, googleApiClient);
            gpsChecker.checkGPS();
            Log.d(TAG, "LOCATION CLIENT BUILT");
        }
    }

    /*
    //Google Client Builder For Places
    public void buildPlacesClient(){
        if(googleApiClient == null && checkGooglePlayServicesAvailable()){
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage((FragmentActivity) context, this)
                    .build();
            googleApiClient.connect();
            Log.d(TAG, "PLACES CLIENT BUILT");
        }
    }
    */

    public boolean checkGooglePlayServicesAvailable() {
        Log.d(TAG, "CHECKING PLAY SERVICES");
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return result == ConnectionResult.SUCCESS;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(!BackgroundLocationIntentService.pauseThread)
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
