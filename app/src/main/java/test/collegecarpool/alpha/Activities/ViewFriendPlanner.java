package test.collegecarpool.alpha.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import test.collegecarpool.alpha.Adapters.ViewJourneyPlannerAdapter;
import test.collegecarpool.alpha.LoginAndRegistrationActivities.SigninActivity;
import test.collegecarpool.alpha.MapsUtilities.Journey;
import test.collegecarpool.alpha.MapsUtilities.LatLng;
import test.collegecarpool.alpha.MapsUtilities.Waypoint;
import test.collegecarpool.alpha.MessagingActivities.ChatRoomActivity;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.UserClasses.Date;

public class ViewFriendPlanner extends AppCompatActivity {

    private ArrayList<Journey> journeys;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth auth;
    private ListView listView;
    private String friendID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend_planner);

        auth = FirebaseAuth.getInstance();
        journeys = new ArrayList<>();
        friendID = getIntent().getStringExtra("FriendID");

        initDrawer();
        getPlannedJourneys();
        initListView();
    }

    /*Initialise the ListView*/
    private void initListView() {
        ViewJourneyPlannerAdapter adapter = new ViewJourneyPlannerAdapter(this, R.layout.journey_planner_list, journeys);
        if(journeys != null)
            listView = (ListView) findViewById(R.id.journey_planner_list_view);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    /*Get a List Of All Of The Planned Journeys That Are Valid*/
    private void getPlannedJourneys() {
        DatabaseReference journeyRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(friendID).child("JourneyPlanner");
        journeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable <DataSnapshot> timeStamps = dataSnapshot.getChildren();
                for(DataSnapshot timeStamp : timeStamps){
                    Journey journey = new Journey();
                    Iterable <DataSnapshot> details = timeStamp.getChildren();
                    Date date = new Date();
                    ArrayList<Waypoint> waypoints = new ArrayList<>();
                    for(DataSnapshot detail : details){
                        if(detail.getKey().equals("date")){
                            date = detail.getValue(Date.class);
                        }
                        if(detail.getKey().equals("journeyWaypoints")){
                            Iterable<DataSnapshot> elements = detail.getChildren();
                            for(DataSnapshot element : elements){
                                Waypoint waypoint = new Waypoint();
                                Iterable<DataSnapshot> elemDetails = element.getChildren();
                                for(DataSnapshot elemDetail : elemDetails){
                                    if(elemDetail.getKey().equals("name")){
                                        waypoint.setName(elemDetail.getValue(String.class));
                                    }
                                    if(elemDetail.getKey().equals("latLng")){
                                        waypoint.setLatLng(elemDetail.getValue(LatLng.class));
                                    }
                                }
                                waypoints.add(waypoint);
                            }
                        }
                    }
                    journey.setDate(date);
                    journey.setWaypoints(waypoints);
                    journeys.add(journey);
                }
                initListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(ViewFriendPlanner.this, HomeScreenActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_journey:
                        startActivity(new Intent(ViewFriendPlanner.this, PlanJourneyActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_messages:
                        startActivity(new Intent(ViewFriendPlanner.this, ChatRoomActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_payment:
                        startActivity(new Intent(ViewFriendPlanner.this, PaymentActivity.class));
                        onStart();
                        return true;
                    case R.id.nav_logout:
                        auth.signOut();
                        startActivity(new Intent(ViewFriendPlanner.this, SigninActivity.class));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
