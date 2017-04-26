package test.collegecarpool.alpha.Tools;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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
    private Place place;

    public GoogleClientBuilder(Context context, GoogleApiClient googleApiClient){
        this.context = context;
        this.googleApiClient = googleApiClient;
    }

    public GoogleClientBuilder(Context context, GoogleApiClient googleApiClient, Place place){
        this.context = context;
        this.googleApiClient = googleApiClient;
        this.place = place;
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

    /*Google Client Builder For Places*/
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

    /*Gets the Current Place Client Is in*/
    public void getClientPlace(){
        if(googleApiClient != null && checkGooglePlayServicesAvailable()){
            try {
                PendingResult<PlaceLikelihoodBuffer> placeResult = Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null);
                placeResult.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                        PlaceLikelihood bestMatch = placeLikelihoods.get(0);
                        for(PlaceLikelihood placeLikelihood : placeLikelihoods){
                            if(placeLikelihood.getLikelihood() > bestMatch.getLikelihood()){
                                bestMatch = placeLikelihood;
                            }
                        }
                        place = bestMatch.getPlace();
                        Log.d(TAG, "My Place Is: " + place.getName().toString() + " & Prob is " + bestMatch.getLikelihood());
                    }
                });
            }
            catch(SecurityException e){
                Log.d(TAG, "ISSUE WITH SECURITY IN GETTING CURRENT LOCATION");
            }
        }
    }

    public Place getPlace(){
        return this.place;
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
