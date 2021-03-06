package test.collegecarpool.alpha.MapsUtilities;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import test.collegecarpool.alpha.Activities.HomeScreenActivity;
import test.collegecarpool.alpha.Activities.PaymentActivity;
import test.collegecarpool.alpha.Activities.PlanJourneyActivity;
import test.collegecarpool.alpha.LoginAndRegistrationActivities.SigninActivity;
import test.collegecarpool.alpha.MessagingActivities.ChatRoomActivity;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.PolyDirectionsTools.PolyURLBuilder;
public class ViewJourneyActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private ArrayList<LatLng> latLngs;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_journey);
        auth = FirebaseAuth.getInstance();
        initDrawer();
        latLngs = getIntent().getParcelableArrayListExtra("LAT/LNG");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        new PolyURLBuilder(this, googleMap, latLngs).buildPolyURL();
        for(LatLng latLng : latLngs){
            googleMap.addMarker(new MarkerOptions().position(latLng));
        }
    }
    public void initDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(ViewJourneyActivity.this, HomeScreenActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_journey:
                        startActivity(new Intent(ViewJourneyActivity.this, PlanJourneyActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_messages:
                        startActivity(new Intent(ViewJourneyActivity.this, ChatRoomActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_payment:
                        startActivity(new Intent(ViewJourneyActivity.this, PaymentActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_friends:
                        startActivity(new Intent(ViewJourneyActivity.this, PaymentActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_logout:
                        auth.signOut();
                        startActivity(new Intent(ViewJourneyActivity.this, SigninActivity.class));
                        onStop();
                        return true;
                }
                return false;
            }
        });
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }
    }
    @Override
    public void onStop(){
        super.onStop();
        googleMap.clear();
    }
}