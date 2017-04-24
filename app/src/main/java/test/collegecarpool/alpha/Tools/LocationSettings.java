package test.collegecarpool.alpha.Tools;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

class LocationSettings implements LocationListener {

    public Context context;
    private LocationRequest locationRequest;
    private static String TAG = "LOCATION SETTINGS";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserProfile");

    LocationSettings(Context context) {
        this.context = context;
        setLocationRequestParams();
    }

    private void setLocationRequestParams() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(2 * 1000); //Once every 2 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2 * 1000); //Once every 15 Seconds
        Log.d(TAG, "LOCATION PARAMS SET");
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        pushLocationToFirebase(latitude, longitude);
        Log.d(TAG, "LOCATION CHANGED");
    }

    private void pushLocationToFirebase(double latitude, double longitude) {
        if (auth.getCurrentUser() != null) {
            userRef.child(auth.getCurrentUser().getUid()).child("longitude").setValue(longitude);
            userRef.child(auth.getCurrentUser().getUid()).child("latitude").setValue(latitude);
        }
    }

    void requestLocationUpdates(GoogleApiClient googleApiClient) {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }
}
