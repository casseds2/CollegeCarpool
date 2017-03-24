package test.collegecarpool.alpha.Activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Polygon;

import java.net.URL;

import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.Tools.GPSChecker;
import test.collegecarpool.alpha.Tools.PolyDirections;

public class ViewJourneyActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_journey);

        checkLocationAvailable();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    public void getPolyLine(URL url){
        PolyDirections polyDirections = new PolyDirections(this, googleMap);
        polyDirections.execute(url);
    }

    public void checkLocationAvailable(){
        GPSChecker gpsChecker = new GPSChecker(this, googleApiClient);
        gpsChecker.checkGPS();
    }
}
