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
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import test.collegecarpool.alpha.LoginAndRegistrationActivities.SigninActivity;
import test.collegecarpool.alpha.MapsUtilities.Journey;
import test.collegecarpool.alpha.MapsUtilities.LatLng;
import test.collegecarpool.alpha.MapsUtilities.NavigationActivity;
import test.collegecarpool.alpha.MapsUtilities.Waypoint;
import test.collegecarpool.alpha.MessagingActivities.ChatRoomActivity;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.UserClasses.Date;

import static test.collegecarpool.alpha.Tools.Variables.SAT_NAV_ENABLED;

public class ViewJourneyPlannerActivity extends AppCompatActivity {

    private DatabaseReference journeyRef;
    private ArrayList<Journey> journeys = new ArrayList<>(); //SEND THIS TO NAVIGATION - PICK CORRESPONDING JOURNEY TO CLICKED JOURNEY OUT OF IT AND SEND TO NAVIGATION
    private final String TAG = "JOURNEY PLANNER";
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth auth;
    private ArrayAdapter<String> adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_journey_planner);

        initDrawer();
        initFirebase();
        getPlannedJourneys();
        initListView();
    }

    /*Initialise Firebase Components*/
    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(null != user)
            journeyRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid()).child("JourneyPlanner");
    }

    /*registerForContextMenu Callback ... Allows onLongCLick of Menu Items*/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.journey_planner_popup_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.remove_planner_item_id :
                //REMOVE THE ITEM FROM THE LIST || && FIREBASE
                int index = adapterContextMenuInfo.position;
                removeJourney(getSelectedJourney(index)); //Remove Journey From Firebase
                journeys.remove(getSelectedJourney(index)); //Remove the Journey From Local Array
                Toast.makeText(ViewJourneyPlannerActivity.this, "Removed Journey", Toast.LENGTH_SHORT).show();
                updateUI();
                break;
            case R.id.start_journey_planner_item_id :
                //Start A Navigation Service for This Journey
                index = adapterContextMenuInfo.position;
                Intent intent = new Intent(ViewJourneyPlannerActivity.this, NavigationActivity.class);
                intent.putExtra("SelectedJourney", getSelectedJourney(index)); //Adds the Journey (Date, ArrayList<Place>) to an extra
                SAT_NAV_ENABLED = true; //Set the static variable to true, make sure to disable when leaving navigation activity or can't view journeys
                startActivity(intent);
                break;
            default :
                return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    /*Return Journey Selected in Menu*/
    private Journey getSelectedJourney(int index){
        return journeys.get(index);
    }

    /*Convert an Array of Waypoints into String of Names*/
    private ArrayList<String> stringifyJourneys(){
        journeys = new Journey().sortJourneys(journeys);
        ArrayList<String> stops = new ArrayList<>();
        for(Journey j : journeys) {
            ArrayList<Waypoint> waypoints = j.getWaypoints();
            String journeyString = j.getDate().toString() + ": ";
            for(Waypoint waypoint : waypoints){
                String wayName = waypoint.getName();
                if(waypoint != waypoints.get(waypoints.size()-1))
                    journeyString = journeyString + wayName + " -> ";
                else
                    journeyString = journeyString + wayName;
            }
            stops.add(journeyString);
        }
        return stops;
    }

    private void initListView() {
        adapter = new ArrayAdapter<>(this, R.layout.journey_planner_list, stringifyJourneys());
        listView = (ListView) findViewById(R.id.journey_planner_list_view);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    private void updateUI(){
        adapter = new ArrayAdapter<>(this, R.layout.journey_planner_list, stringifyJourneys());
        listView = (ListView) findViewById(R.id.journey_planner_list_view);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    private void removeJourney(final Journey journey){
        journeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot data1 : dataSnapshots){ //For Each TimeStamp
                    Iterable<DataSnapshot> dataSnapshots1 = data1.getChildren();
                    Journey tempJourney = new Journey();
                    for(DataSnapshot data2 : dataSnapshots1){ //FOR EACH DATE / Waypoints list
                        if(data2.getKey().equals("date")){
                            Date date = data2.getValue(Date.class);
                            tempJourney.setDate(date);
                        }
                        if(data2.getKey().equals("journeyWaypoints")){
                            Iterable<DataSnapshot> dataSnapshots2 = data2.getChildren();
                            ArrayList<Waypoint> waypoints = new ArrayList<>();
                            for(DataSnapshot data3 :  dataSnapshots2){ //FOR EACH LIST ITEM, ERROR CAUSED CAUSE IM NOT AT RIGHT LEVEL YET
                                Iterable<DataSnapshot> dataSnapshots3 = data3.getChildren();
                                Waypoint waypoint = new Waypoint();
                                for(DataSnapshot data4 : dataSnapshots3) {
                                    if (data4.getKey().equals("latLng")) {
                                        LatLng latLng = data4.getValue(LatLng.class);
                                        Log.d(TAG, "LAT/LNG IS " + latLng.toString());
                                        waypoint.setLatLng(latLng);
                                    }
                                    if (data4.getKey().equals("name")) {
                                        String name = data4.getValue(String.class);
                                        Log.d(TAG, "NAME IS " + name);
                                        waypoint.setName(name);
                                    }
                                }
                                waypoints.add(waypoint);
                            }
                            tempJourney.setWaypoints(waypoints);
                        }
                    }
                    //Log.d(TAG, "BOOLEAN JOURNEY: " + journey.compareTo(tempJourney));
                    //Log.d(TAG, "BOOLEAN DATE: " + journey.getDate().compareTo(tempJourney.getDate()));
                    //Log.d(TAG, "BOOLEAN WAYPOINTS: " + journey.getWaypoints().equals(tempJourney.getWaypoints()));
                    if(journey.compareTo(tempJourney)){
                        data1.getRef().setValue(null);
                        Log.d(TAG, "JOURNEYS FIRE " + journeys.toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPlannedJourneys() {
        journeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable <DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot data1 : dataSnapshots){ //FOR EACH TIMESTAMP
                    Iterable<DataSnapshot> dataSnapshots1 = data1.getChildren();
                    Journey journey = new Journey();
                    for(DataSnapshot data2 : dataSnapshots1){ //FOR EACH DATE / PLACES LIST
                        if(data2.getKey().equals("date")){
                            Date date = data2.getValue(Date.class);
                            journey.setDate(date);
                        }
                        if(data2.getKey().equals("journeyWaypoints")){
                            Iterable<DataSnapshot> dataSnapshots2 = data2.getChildren();
                            ArrayList<Waypoint> waypoints = new ArrayList<>();
                            for(DataSnapshot data3 :  dataSnapshots2){ //FOR EACH LIST ITEM, ERROR CAUSED CAUSE IM NOT AT RIGHT LEVEL YET
                                Iterable<DataSnapshot> dataSnapshots3 = data3.getChildren();
                                Waypoint waypoint = new Waypoint();
                                for(DataSnapshot data4 : dataSnapshots3) { //FOR EACH WAYPOINT
                                    if (data4.getKey().equals("latLng")) {
                                        LatLng latLng = data4.getValue(LatLng.class);
                                        Log.d(TAG, "LAT/LNG IS " + latLng.toString());
                                        waypoint.setLatLng(latLng);
                                    }
                                    if (data4.getKey().equals("name")) {
                                        String name = data4.getValue(String.class);
                                        Log.d(TAG, "NAME IS " + name);
                                        waypoint.setName(name);
                                    }
                                }
                                waypoints.add(waypoint);
                            }
                            journey.setWaypoints(waypoints);
                        }
                    }
                    journeys.add(journey);
                }
                initListView(); //When Journeys Are Down, Initialize ListView
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
                        startActivity(new Intent(ViewJourneyPlannerActivity.this, HomeScreenActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_journey:
                        startActivity(new Intent(ViewJourneyPlannerActivity.this, PlanJourneyActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_messages:
                        startActivity(new Intent(ViewJourneyPlannerActivity.this, ChatRoomActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_payment:
                        startActivity(new Intent(ViewJourneyPlannerActivity.this, PaymentActivity.class));
                        onStart();
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(ViewJourneyPlannerActivity.this, ProfileActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_settings:
                        startActivity(new Intent(ViewJourneyPlannerActivity.this, SettingsActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_logout:
                        auth.signOut();
                        startActivity(new Intent(ViewJourneyPlannerActivity.this, SigninActivity.class));
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
