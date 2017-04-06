package test.collegecarpool.alpha.Activities;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.Tools.GoogleClientBuilder;
import test.collegecarpool.alpha.Tools.PolyURLBuilder;

public class ViewJourneyActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient = null;
    private ArrayList<LatLng> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_journey);

        GoogleClientBuilder googleClientBuilder = new GoogleClientBuilder(this, googleApiClient);
        if(googleClientBuilder.checkGooglePlayServicesAvailable())
            googleClientBuilder.buildLocationClient();
        Intent intent = getIntent();
        places = intent.getParcelableArrayListExtra("LAT/LNG");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        getPolyLine();
    }

    public void getPolyLine(){
        PolyURLBuilder urlBuilder = new PolyURLBuilder(this, googleMap, places);
        urlBuilder.buildPolyURL();
    }
}
