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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

public class GoogleClientBuilder extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    private GoogleApiClient googleApiClient;
    private static String TAG = "GOOGLE CLIENT BUILDER";
    private LocationSettings locationSettings;
    Place currentPlace;

    public GoogleClientBuilder(Context context, GoogleApiClient googleApiClient){
        this.context = context;
        this.googleApiClient = googleApiClient;
    }

    /*Disconnect the Client*/
    public void disconnect(){
        this.googleApiClient.disconnect();
    }

    /*Initiate Location Updates*/
    public void startLocationUpdates(){
        locationSettings = new LocationSettings(context);
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

    /*Google Client Builder For Places With Its Own Connection Callbacks*/
    public void buildPlacesClient(){
        if(googleApiClient == null && checkGooglePlayServicesAvailable()){
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) throws SecurityException{
                            Log.d(TAG, "SEARCHING FOR USER PLACE");
                            PendingResult<PlaceLikelihoodBuffer> placeResult = Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null);
                            placeResult.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                                @Override
                                public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                                    PlaceLikelihood bestMatch = placeLikelihoods.get(0);
                                    for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
                                        if (placeLikelihood.getLikelihood() > bestMatch.getLikelihood()) {
                                            bestMatch = placeLikelihood;
                                            Log.d(TAG, "BestMatch: " + bestMatch.getPlace().getName().toString());
                                        }
                                    }
                                    currentPlace = bestMatch.getPlace();
                                    Log.d(TAG, "My Place Is: " + currentPlace.getName().toString() + " & Prob is " + bestMatch.getLikelihood());
                                }
                            });
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addOnConnectionFailedListener(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    //.enableAutoManage((FragmentActivity) context, this)
                    .build();
            Log.d(TAG, "PLACES CLIENT BUILT");
            googleApiClient.connect();
        }
    }

    public Place getCurrentPlace(){
        return this.currentPlace;
    }

    public boolean checkGooglePlayServicesAvailable() {
        Log.d(TAG, "CHECKING PLAY SERVICES");
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return result == ConnectionResult.SUCCESS;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle){
        /*If A Location Client Is In Use*/
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
