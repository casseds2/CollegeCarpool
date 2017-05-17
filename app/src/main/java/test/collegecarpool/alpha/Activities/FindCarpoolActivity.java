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
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import test.collegecarpool.alpha.Adapters.FindCarpoolAdapter;
import test.collegecarpool.alpha.LoginAndRegistrationActivities.SigninActivity;
import test.collegecarpool.alpha.MapsUtilities.Journey;
import test.collegecarpool.alpha.MapsUtilities.LatLng;
import test.collegecarpool.alpha.MapsUtilities.ViewJourneyActivity;
import test.collegecarpool.alpha.MapsUtilities.Waypoint;
import test.collegecarpool.alpha.MessagingActivities.ChatRoomActivity;
import test.collegecarpool.alpha.MessagingActivities.MessageActivity;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.UserClasses.Date;
import test.collegecarpool.alpha.UserClasses.UserProfile;

public class FindCarpoolActivity extends AppCompatActivity {

    private ArrayList<Journey> allJourneys;
    private final String TAG = "FindCarpool";
    private Date todayDate;
    private String searchTerm;
    private boolean containsSearchTerm;
    private EditText searchField;
    private FirebaseAuth auth;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private HashMap<String, Journey> userJourneys;
    private FirebaseUser user;
    private UserProfile myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_carpool);
        containsSearchTerm = false;
        myUser = new UserProfile();
        auth = FirebaseAuth.getInstance();
        userJourneys = new HashMap<>();
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        searchTerm = " ";
        allJourneys = new ArrayList<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        todayDate = new Date(day, month, year);
        Log.d(TAG, "Today's Date: " + todayDate.toString());
        initSearchField();
        initSearchButton();
        initDrawer();
    }

    /*registerForContextMenu Callback ... Allows onLongCLick of Menu Items*/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.find_carpool_popup_menu, menu);
    }

    /*Context Menu For Popup Menu*/
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String userID;
        int index;
        switch(item.getItemId()){
            case R.id.message_user :
                index = adapterContextMenuInfo.position;
                Journey selectedJourney = allJourneys.get(index);
                userID = findJourneyOwner(selectedJourney);
                //Log.d(TAG, "UserID: " + userID);
                //Toast.makeText(this, "Message User at " + index, Toast.LENGTH_SHORT).show();
                if(!userID.equals(user.getUid())) {
                    //Toast.makeText(this, "UserID: " + userID, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FindCarpoolActivity.this, MessageActivity.class);
                    intent.putExtra("ReceiverID", userID);
                    startActivity(intent);
                }
                break;

            case R.id.add_friend :
                index = adapterContextMenuInfo.position;
                selectedJourney = allJourneys.get(index);
                userID = findJourneyOwner(selectedJourney);
                //Log.d(TAG, "UserID: " + userID);
                //Toast.makeText(this, "Add Friend at " + index, Toast.LENGTH_SHORT).show();
                if(!userID.equals(user.getUid())) {
                    //Toast.makeText(this, "UserID: " + userID, Toast.LENGTH_SHORT).show();
                    HashMap<String, Object> friendRequest = new HashMap<>();
                    friendRequest.put("requestID", user.getUid());
                    friendRequest.put("userName", myUser.getFirstName() + " " + myUser.getSecondName());
                    DatabaseReference receiverFriendRequestList = FirebaseDatabase.getInstance().getReference("UserProfile").child(userID).child("FriendRequest").child(user.getUid());
                    receiverFriendRequestList.updateChildren(friendRequest);
                    Toast.makeText(this, "Friend Request Sent", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.view_journey :
                index = adapterContextMenuInfo.position;
                selectedJourney = allJourneys.get(index);
                Intent intent = new Intent(FindCarpoolActivity.this, ViewJourneyActivity.class);
                ArrayList<com.google.android.gms.maps.model.LatLng> latLngs = new ArrayList<>();
                for(Waypoint waypoint : selectedJourney.getWaypoints()){
                    latLngs.add(waypoint.getLatLng().toGoogleLatLng());
                }
                intent.putExtra("LAT/LNG", latLngs);
                startActivity(intent);
                break;

            default :
                return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    private String findJourneyOwner(Journey selectedJourney) {
        for(Map.Entry entry : userJourneys.entrySet()){
            if(entry.getValue().equals(selectedJourney))
                return (String) entry.getKey();
        }
        return "";
    }

    /*Initialize The Search Field*/
    private void initSearchField(){
        searchField = (EditText) findViewById(R.id.carpool_search_bar);
        searchField.setHint("Enter Search Destination");
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Toast.makeText(FindCarpoolActivity.this, "Input: " + v.getText().toString(), Toast.LENGTH_SHORT).show();
                searchTerm = v.getText().toString();
                return false;
            }
        });
    }

    private void initSearchButton(){
        Button searchButton = (Button) findViewById(R.id.carpool_search_button);
        searchButton.setText("Search");
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allJourneys = new ArrayList<>();
                searchTerm = searchField.getText().toString().toLowerCase();
                initListView();
                getAllJourneys();
            }
        });
    }

    /*Initialise the ListView*/
    private void initListView() {
        FindCarpoolAdapter adapter = new FindCarpoolAdapter(this, R.layout.find_carpool_list, allJourneys);
        ListView listView = (ListView) findViewById(R.id.find_carpool_listview);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    private void getAllJourneys(){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> userIDs = dataSnapshot.getChildren();
                for(DataSnapshot userID : userIDs){
                    Iterable<DataSnapshot> userChildren = userID.getChildren();
                    if(userID.getKey().equals(user.getUid())){
                        myUser = userID.getValue(UserProfile.class);
                        Log.d(TAG, "UserProfile: " + myUser.toString());
                    }
                    for(DataSnapshot userChild : userChildren){
                        if(userChild.getKey().equals("JourneyPlanner")){
                            Iterable<DataSnapshot> journeyPlanner = userChild.getChildren();
                            for(DataSnapshot journeyItem : journeyPlanner){ //per Unique Journey
                                Journey journey = new Journey();
                                containsSearchTerm = false;
                                Iterable<DataSnapshot> journeyChildren = journeyItem.getChildren();
                                for(DataSnapshot journeyChild : journeyChildren){
                                    Date date;
                                    if(journeyChild.getKey().equals("date")){
                                        date = journeyChild.getValue(Date.class);
                                        journey.setDate(date);
                                        Log.d(TAG, "Journey Date: " + date.toString());
                                    }
                                    if(journeyChild.getKey().equals("journeyWaypoints")){
                                        Iterable<DataSnapshot> waypointItem = journeyChild.getChildren();
                                        for(DataSnapshot listElement : waypointItem){
                                            Iterable<DataSnapshot> waypointElements = listElement.getChildren();
                                            Waypoint waypoint = new Waypoint();
                                            LatLng latLng;
                                            String name;
                                            for(DataSnapshot waypointElem : waypointElements){
                                                if(waypointElem.getKey().equals("latLng")){
                                                    latLng = waypointElem.getValue(LatLng.class);
                                                    Log.d(TAG, "Waypoint LatLng: " + latLng.toString());
                                                    waypoint.setLatLng(latLng);
                                                }
                                                if(waypointElem.getKey().equals("name")){
                                                    name = waypointElem.getValue(String.class);
                                                    if(name.toLowerCase().contains(searchTerm)) {
                                                        containsSearchTerm = true;
                                                        Log.d(TAG, name + " contains " + searchTerm);
                                                    }
                                                    Log.d(TAG, "Waypoint Name: " + name);
                                                    waypoint.setName(name);
                                                }
                                            }
                                            Log.d(TAG, "Waypoint Details: " + waypoint.toString());
                                            journey.addWaypoint(waypoint);
                                        }
                                    }
                                }
                                if(todayDate.isBefore(journey.getDate())){
                                    Log.d(TAG, "Journey: " + journey.getDate().toString() + " || Today: " + todayDate.toString() + " - Journey in The Future");
                                }
                                else
                                    Log.d(TAG, "Journey: " + journey.getDate().toString() + " || Today: " + todayDate.toString() + " - Journey in The Past");
                                if(journey.getWaypoints().size() > 0 && todayDate.isBefore(journey.getDate()) && containsSearchTerm) {
                                    allJourneys.add(journey);
                                    userJourneys.put(userID.getKey(), journey);
                                    Log.d(TAG, "User ID: " + userID.getKey());
                                    Log.d(TAG, journey.toString());
                                }
                            }
                        }
                    }
                }
                initListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void initDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_journey:
                        startActivity(new Intent(FindCarpoolActivity.this, PlanJourneyActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_messages:
                        startActivity(new Intent(FindCarpoolActivity.this, ChatRoomActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_payment:
                        startActivity(new Intent(FindCarpoolActivity.this, PaymentActivity.class));
                        onStart();
                        return true;
                    case R.id.nav_logout:
                        auth.signOut();
                        startActivity(new Intent(FindCarpoolActivity.this, SigninActivity.class));
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
}
