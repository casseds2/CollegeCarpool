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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import test.collegecarpool.alpha.LoginAndRegistrationActivities.SigninActivity;
import test.collegecarpool.alpha.MessagingActivities.ChatRoomActivity;
import test.collegecarpool.alpha.R;

public class ViewJourneyPlannerActivity extends AppCompatActivity {

    private DatabaseReference journeyRef;
    ArrayList<ArrayList<String>> places = new ArrayList<>();
    ArrayList<String> placesJson = new ArrayList<>();
    private final String TAG = "JOURNEY PLANNER";
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_journey_planner);

        initDrawer();
        initListView();
        initFirebase();
        getPlannedJourneys();
        //makeListClickable();
        

    }

    /*Initialise Firebase Components*/
    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(null != user)
            journeyRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid()).child("JourneyPlanner");
    }

    /*registerForContextMenu Callback .. Allows onLongCLick of Menu Items*/
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
                Toast.makeText(this, "Up for Deletion", Toast.LENGTH_SHORT).show();
                //REMOVE THE ITEM FROM THE LIST || && FIREBASE
                //int s = adapterContextMenuInfo.position; //How We Identify What Has Been Pressed
                break;
            case R.id.edit_planner_item_id :
                Toast.makeText(this, "Up for Editing", Toast.LENGTH_SHORT).show();
                //EDIT THE ENTRY
                break;
            default :
                return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    /*Make List Items Clickable and Long Clickable*/
    /*
    private void makeListClickable() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String message = "Short Click";
                Toast.makeText(ViewJourneyPlannerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        //Original Pop Up Menu Item

        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String message = "Long Click";
                Toast.makeText(ViewJourneyPlannerActivity.this, message, Toast.LENGTH_SHORT).show();
                //PopupMenu popupMenu = new PopupMenu(ViewJourneyPlannerActivity.this, view);
                //MenuInflater inflater = popupMenu.getMenuInflater();
                //inflater.inflate(R.menu.journey_planner_popup_menu, popupMenu.getMenu());
                //popupMenu.show();
                return false;
            }
        });
    }
    */

    /*Turn the place Json Object into start and end place of journey Strings*/
    private ArrayList<String> stringifyPlaces(){
        ArrayList<String> stops = new ArrayList<>();
        String temp;
        for(ArrayList<String> array : places) { //Cycles Through Array of Node Arrays
            if(array.size() < 4)
                temp = array.get(0);
            else
                temp = array.get(0) + " -> " + array.get(array.size() - 3);
            stops.add(temp);
        }
        return stops;
    }

    private void initListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.journey_planner_list, stringifyPlaces());
        ListView listView = (ListView) findViewById(R.id.journey_planner_list_view);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        //makeListClickable();
    }

    private void getPlannedJourneys() {
        final GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {}; //Cannot retrieve List Without
        journeyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable <DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot data1 : dataSnapshots){
                    Iterable<DataSnapshot> dataSnapshots1 = data1.getChildren();
                    for(DataSnapshot data2 : dataSnapshots1){
                        if(data2.getKey().equals("places")){
                            placesJson = data2.getValue(t); //assign the place to a list
                            Log.d(TAG, placesJson.toString());
                            places.add(placesJson); //put this list in a list of places
                        }
                    }
                }
                initListView(); //Once all 'places' nodes have been stored
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
