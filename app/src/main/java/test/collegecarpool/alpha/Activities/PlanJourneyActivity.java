package test.collegecarpool.alpha.Activities;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import test.collegecarpool.alpha.Adapters.PlanJourneyAdapter;
import test.collegecarpool.alpha.LoginAndRegistrationActivities.SigninActivity;
import test.collegecarpool.alpha.MapsUtilities.Journey;
import test.collegecarpool.alpha.MapsUtilities.Waypoint;
import test.collegecarpool.alpha.PolyDirectionsTools.WaypointFromPlaceGenerator;
import test.collegecarpool.alpha.MessagingActivities.ChatRoomActivity;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.Tools.GoogleClientBuilder;
import test.collegecarpool.alpha.UserClasses.Date;

public class PlanJourneyActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private final String TAG = "PLAN JOURNEY";
    private ArrayList<Place> places = new ArrayList<>();
    private ArrayList<String> placeNames = new ArrayList<>();
    private DatePickerDialog datePickerDialog;
    private Date date;
    private boolean dateChosen = false;
    private ArrayList<LatLng> latLngs;
    private FirebaseUser user;
    private DatabaseReference userRef;
    private Journey journey;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private PlanJourneyAdapter adapter;
    private PlaceAutocompleteFragment autocompleteFragment;
    private Calendar calendar = Calendar.getInstance();
    private ArrayList<Journey> fireJourneys = new ArrayList<>();
    private GoogleApiClient googleApiClient = null;
    private GoogleClientBuilder googleClientBuilder;
    private Place currentPlace;
    private Place tempPlace = null;
    private boolean locationButtonEnabled = true;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_journey);

        datePickerDialog = new DatePickerDialog(PlanJourneyActivity.this, PlanJourneyActivity.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        googleClientBuilder = new GoogleClientBuilder(this, googleApiClient);
        googleClientBuilder.buildPlacesClient();

        initDrawer();
        initSearchBar();
        initButtons();
        initViewJourney();
        initFirebase();
        initListView();
        initAddMyLocation();
        getCurrentJourneys();
    }

    /*Initialise the List View Adapter*/
    private void initListView() {
        //adapter = new ArrayAdapter<>(this, R.layout.plan_journey_list, placeNames);
        adapter = new PlanJourneyAdapter(this, R.layout.plan_journey_list, placeNames);
        listView = (ListView) findViewById(R.id.plan_journey_list_view);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    /*registerForContextMenu Callback ... Allows onLongCLick of Menu Items*/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.plan_journey_popup_menu, menu);
    }

    /*Context Menu For Popup Menu*/
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.remove_stop_item_id :
                //Issue with concurrent modification Exception
                //Arise from iterating over construct while another part
                //is writing/reading from it
                //Interrupts UI slightly...
                try {
                    int index = adapterContextMenuInfo.position;
                    places.removeAll(removeStringFromPlaces(placeNames.get(index)));
                    if(null != tempPlace){
                        if(tempPlace.getName().toString().equals(placeNames.get(index))){
                            locationButtonEnabled = true; //If removed is my location, re-enable use of button
                        }
                    }
                    if(places.size() == 0)
                        locationButtonEnabled = true;
                    placeNames.remove(index);
                    printPlaceNamesArray();
                    adapter.notifyDataSetChanged();
                }
                catch (ConcurrentModificationException c){
                    Log.d(TAG, "Concurrent Modification Exception");
                }
                break;

            case R.id.move_stop_up_item_id :
                int index = adapterContextMenuInfo.position;
                moveStringUp(placeNames.get(index));
                adapter.notifyDataSetChanged();
                break;

            case R.id.move_stop_down_item_id :
                index = adapterContextMenuInfo.position;
                moveStringDown(placeNames.get(index));
                adapter.notifyDataSetChanged();
                break;
            default :
                return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    /*Move The Item Down In The List of PLaceNames and Places*/
    private void moveStringDown(String s) {
        int index = placeNames.indexOf(s); //position in array
        if(placeNames.size() > index + 1 && places.size() > index + 1) { //Need statement that checks if element after is not null
            for(int i = 0; i < places.size() - 1; i++){
                if(places.get(i).getName().toString().equals(placeNames.get(index))){
                    Collections.swap(places, i, i + 1);
                }
            }
            Collections.swap(placeNames, index, index + 1);
        }
        else
            Toast.makeText(PlanJourneyActivity.this, "Can't Move Down", Toast.LENGTH_SHORT).show();
    }

    /*Move the Item Up In The List of PlaceNames and Places*/
    private void moveStringUp(String s) {
        int index = placeNames.indexOf(s);
        if(placeNames.size() > 1 && index > 0) {
            for(int i = 0; i < places.size(); i++){
                if(places.get(i).getName().toString().equals(placeNames.get(index))){
                    Collections.swap(places, i, i - 1);
                }
            }
            Collections.swap(placeNames, index, index - 1);
        }
        else
            Toast.makeText(PlanJourneyActivity.this, "Can't Move Up", Toast.LENGTH_SHORT).show();
    }

    /*Remove the Item From the List*/
    private ArrayList<Place> removeStringFromPlaces(String s) {
        ArrayList<Place> temp = new ArrayList<>();
        for(Place p : places){
            if(p.getName().toString().equals(s))
                temp.add(p);
        }
        return temp;
    }

    /*Log list of elements currently entered*/
    private void printPlacesArray() {
        for (int i = 0; i < places.size(); i++) {
            Log.d(TAG, "Element(" + i + ") is " + places.get(i).getName().toString() + " in Places");
        }
    }

    /*Print the Place Name Array*/
    private void printPlaceNamesArray(){
        for(int i = 0; i < placeNames.size(); i++){
            Log.d(TAG, "Element(" + i + ") is " + placeNames.get(i) + " in PlaceNames");
        }
    }

    /*Initialize Firebase*/
    private void initFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid());
    }

    /*Initialise Button For Journey Planner*/
    private void initButtons(){
        Button viewJourneyPlanner = (Button) findViewById(R.id.view_journey_planner);
        viewJourneyPlanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlanJourneyActivity.this, ViewJourneyPlannerActivity.class));
            }
        });

        Button dateDialog = (Button) findViewById(R.id.date_dialog);
        dateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        Button saveJourney = (Button) findViewById(R.id.save_journey);
        saveJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (places.size() > 1 && dateChosen) {
                    journey = new Journey(date, new WaypointFromPlaceGenerator().convertPlacesToWayPoints(places)); //Pushes a Journey with a date and a list of place names taken from MyPlaces
                    Log.d(TAG, "Fire Journey: " + fireJourneys.toString());
                    Log.d(TAG, "BOOL: " + !journey.isElementOf(fireJourneys));
                    if (!journey.isElementOf(fireJourneys)) {
                        pushJourneyToFirebase(); //new MyPlace().convertPlacesToMyPlace(places) converts the ArrayList<Places> to and ArrayList<String> of the place names
                        dateChosen = false;
                        Toast.makeText(PlanJourneyActivity.this, "Saved Journey to Planner", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(PlanJourneyActivity.this, "Already in Planner", Toast.LENGTH_SHORT).show();
                }
                else
                    if(places.size() < 2 && dateChosen)
                    Toast.makeText(PlanJourneyActivity.this, "Enter at Least Two Waypoints", Toast.LENGTH_SHORT).show();
                else
                    if(places.size() > 1 && !dateChosen)
                    Toast.makeText(PlanJourneyActivity.this, "Pick A Date", Toast.LENGTH_SHORT).show();
                else
                    if(places.size() == 1 && !dateChosen)
                    Toast.makeText(PlanJourneyActivity.this, "Enter Another Journey & Date", Toast.LENGTH_SHORT).show();
                else
                    if(places.size() == 0 && !dateChosen)
                        Toast.makeText(PlanJourneyActivity.this, "Enter At Least Two Waypoints & Date", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*Push the new Journey Planner to Firebase...Pull Planner down to local variable, add to it and re-upload*/
    private void pushJourneyToFirebase() {
        HashMap<String, Object> children = new HashMap<>();
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid());
        String timeStamp = Long.toString(System.currentTimeMillis()); //Use timeStamps cause they are unique
        children.put("JourneyPlanner/" + timeStamp, journey.toMap());
        userRef.updateChildren(children);
        getCurrentJourneys();
    }

    /*Initialize Button That Adds My Place to List*/
    private void initAddMyLocation(){
        Button btn1 = (Button) findViewById(R.id.use_my_location);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationButtonEnabled) {
                    currentPlace = googleClientBuilder.getCurrentPlace();
                    if (!places.contains(currentPlace)) {
                        places.add(currentPlace);
                        placeNames.add(currentPlace.getName().toString());
                        adapter.notifyDataSetChanged();
                    }
                    else
                        Toast.makeText(PlanJourneyActivity.this, "Already Picked My Location", Toast.LENGTH_SHORT).show();
                    printPlacesArray();
                    locationButtonEnabled = false;
                }
                else
                    Toast.makeText(PlanJourneyActivity.this, "Already Using My Location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*Initialize the View Journey Button*/
    private void initViewJourney() {
        Button btn2 = (Button) findViewById(R.id.view_journey);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latLngs = new ArrayList<>();
                for (int i = 0; i < places.size(); i++) {
                    LatLng latLng = places.get(i).getLatLng();
                    latLngs.add(latLng);
                }
                Intent intent = new Intent(PlanJourneyActivity.this, ViewJourneyActivity.class);
                intent.putExtra("LAT/LNG", latLngs);
                Log.d(TAG, "LAT/LNG Extra: " + latLngs.toString());
                if (latLngs.size() > 1) {
                    startActivity(intent);
                }
                else
                    Toast.makeText(PlanJourneyActivity.this, "Enter At Least Two Stops", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*Initialise the Autocomplete Fragment Search Bar*/
    private void initSearchBar() {
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.autocom);
        autocompleteFragment.setHint("Enter Address");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if(places.contains(place))
                    Toast.makeText(PlanJourneyActivity.this, "Already Picked", Toast.LENGTH_SHORT).show();
                if(places.size() == 5)
                    Toast.makeText(PlanJourneyActivity.this, "Only Allowed 5 Places", Toast.LENGTH_SHORT).show();
                autocompleteFragment.setText("");
                if(!places.contains(place) && places.size() < 5) {
                    if(place.getLatLng() != null) {
                        places.add(place);
                        Log.d(TAG, "ADDED " + place.getName() + " " + place.getLatLng().toString());
                        placeNames.add(place.getName().toString());
                        printPlacesArray();
                        printPlaceNamesArray();
                        adapter.notifyDataSetChanged();
                    }
                    else
                        Toast.makeText(PlanJourneyActivity.this, "No LAT/LNG Available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Status status) {
                Log.d(TAG, "Error obtaining place name " + status);
            }
        });

        AutocompleteFilter countryFilter = new AutocompleteFilter.Builder().setCountry("IE").build();
        autocompleteFragment.setFilter(countryFilter);
    }

    /*Callback for when a date is chosen from dialog*/
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = new Date(dayOfMonth, month + 1, year); //+1 to accommodate for the ol' [0-11] array being 12 in size...
        Date today = new Date(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        if(!today.inThePastTo(date))
            dateChosen = true;
        else {
            dateChosen = false;
            Toast.makeText(PlanJourneyActivity.this, "Date in the Past", Toast.LENGTH_SHORT).show();
        }
    }

    /*Initialize the Nav Drawer*/
    private void initDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(PlanJourneyActivity.this, HomeScreenActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_journey:
                        startActivity(new Intent(PlanJourneyActivity.this, PlanJourneyActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_messages:
                        startActivity(new Intent(PlanJourneyActivity.this, ChatRoomActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_payment:
                        startActivity(new Intent(PlanJourneyActivity.this, PaymentActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(PlanJourneyActivity.this, ProfileActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_settings:
                        startActivity(new Intent(PlanJourneyActivity.this, SettingsActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_logout:
                        auth.signOut();
                        startActivity(new Intent(PlanJourneyActivity.this, SigninActivity.class));
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

    /*Get All of the Currently Selected Journeys so User Can't Repeat Same Journey on Same Date*/
    private void getCurrentJourneys(){
        DatabaseReference journeyRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid()).child("JourneyPlanner");
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
                                        test.collegecarpool.alpha.MapsUtilities.LatLng latLng = data4.getValue(test.collegecarpool.alpha.MapsUtilities.LatLng.class);
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
                    //TEMP JOURNEY IS EQUAL DON'T UPLOAD IT
                    fireJourneys.add(tempJourney);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*Possibly Bundle Places and Place Names*/
    @Override
    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
    }

    /*Possibly Restore Places and Place Names*/
    @Override
    public void onRestoreInstanceState(Bundle bundle){
        super.onRestoreInstanceState(bundle);
    }

    @Override
    protected void onStop(){
        super.onStop();
        googleClientBuilder.disconnect();
        //places = new ArrayList<>();
        //placeNames = new ArrayList<>();
        //locationButtonEnabled = true;
        //listView.setAdapter(null);
    }
}
