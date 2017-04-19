package test.collegecarpool.alpha.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import test.collegecarpool.alpha.MapsUtilities.Journey;
import test.collegecarpool.alpha.MapsUtilities.MyPlace;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.UserClasses.Date;

public class PlanJourneyActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView entry1, entry2, entry3, entry4;
    static final String TAG = "PLAN JOURNEY";
    private ArrayList<Place> places = new ArrayList<>();
    private PlaceAutocompleteFragment autocompleteFragment;
    private DatePickerDialog datePickerDialog;
    private Date date;
    private boolean dateChosen = false;
    private ArrayList<LatLng> latLngs;
    private FirebaseUser user;
    private DatabaseReference userRef;
    private Journey journey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_journey);

        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(PlanJourneyActivity.this, PlanJourneyActivity.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        initSubmitButton();
        initSearchBar();
        initAddressFields();
        initRemoveButtons();
        initViewJourney();
        initFirebase();
        clearUI();
    }

    /*Log list of elements currently entered*/
    private void printPlacesArray(ArrayList<Place> p) {
        for (int i = 0; i < p.size(); i++) {
            Place pTemp = p.get(i);
            Log.d(TAG, "Element(" + i + ") is " + pTemp.getName());
        }
    }

    /*Initialize Firebase*/
    private void initFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid());
    }

    /*Initialize the  Buttons*/
    private void initRemoveButtons() {
        Button removeEntry1 = (Button) findViewById(R.id.remove_entry1);
        removeEntry1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUI();
                if (!places.isEmpty()) {
                    Log.d(TAG, "Removed : " + places.get(0).getName());
                    places.remove(0);
                }
                updateUiAddress(places);
                printPlacesArray(places);
            }
        });
        Button removeEntry2 = (Button) findViewById(R.id.remove_entry2);
        removeEntry2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUI();
                if (!places.isEmpty()) {
                    Log.d(TAG, "Removed : " + places.get(1).getName());
                    places.remove(1);
                }
                updateUiAddress(places);
                printPlacesArray(places);
            }
        });
        Button removeEntry3 = (Button) findViewById(R.id.remove_entry3);
        removeEntry3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUI();
                if (!places.isEmpty()) {
                    Log.d(TAG, "Removed : " + places.get(2).getName());
                    places.remove(2);
                }
                updateUiAddress(places);
                printPlacesArray(places);
            }
        });
        Button removeEntry4 = (Button) findViewById(R.id.remove_entry4);
        removeEntry4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUI();
                if (!places.isEmpty()) {
                    Log.d(TAG, "Removed : " + places.get(3).getName());
                    places.remove(3);
                }
                updateUiAddress(places);
                printPlacesArray(places);
            }
        });

        Button dateDialog = (Button) findViewById(R.id.dateDialog);
        dateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
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
                if (latLngs.size() > 0 && dateChosen) {
                    journey = new Journey(date, new MyPlace().convertPlacesToMyPlace(places));
                    pushJourneyToFirebase();
                    startActivity(intent);
                }
                if (!dateChosen)
                    Toast.makeText(getApplicationContext(), "Pick A Date", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*Push the new Journey Planner to Firebase...Pull Planner down to local variable, add to it and re-upload*/
    private void pushJourneyToFirebase() {
        HashMap<String, Object> map = new HashMap<>();
        userRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid());
        String timeStamp = Long.toString(System.currentTimeMillis()); //Use timeStamps cause they are unique
        map.put("/JourneyPlanner/" + timeStamp, journey);
        userRef.updateChildren(map);
    }

    /*Initialize the submit button to confirm an address*/
    private void initSubmitButton() {
        Button btn1 = (Button) findViewById(R.id.submit_address);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUiAddress(places);
                autocompleteFragment.setText("");
            }
        });
    }

    /*Initialize te Text Views for the addresses*/
    private void initAddressFields() {
        entry1 = (TextView) findViewById(R.id.entry_1);
        entry2 = (TextView) findViewById(R.id.entry_2);
        entry3 = (TextView) findViewById(R.id.entry_3);
        entry4 = (TextView) findViewById(R.id.entry_4);
    }

    /*Initialise the Autocomplete Fragment Search Bar*/
    private void initSearchBar() {
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.autocom);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (places.size() == 4) {
                    Toast.makeText(PlanJourneyActivity.this, "Only allowed 4 stops", Toast.LENGTH_LONG).show();
                }
                places.add(place);
                autocompleteFragment.setText("");
            }

            @Override
            public void onError(Status status) {
                Log.d(TAG, "Error obtaining place name " + status);
            }
        });

        AutocompleteFilter countryFilter = new AutocompleteFilter.Builder().setCountry("IE").build();
        autocompleteFragment.setFilter(countryFilter);
    }

    /*Clear the stops*/
    private void clearUI() {
        entry1.setText(getResources().getString(R.string.stop1));
        entry2.setText(getResources().getString(R.string.stop2));
        entry3.setText(getResources().getString(R.string.stop3));
        entry4.setText(getResources().getString(R.string.stop4));
    }

    /*Update the Route List in the UI*/
    private void updateUiAddress(ArrayList<Place> places) {
        if (places != null) {
            for (int i = 0; i < places.size(); i++) {
                switch (i) {
                    case 0:
                        entry1.append(places.get(i).getName());
                        Log.d(TAG, "ADDED " + places.get(i).getName());
                        break;
                    case 1:
                        entry2.append(places.get(i).getName());
                        Log.d(TAG, "ADDED " + places.get(i).getName());
                        break;
                    case 2:
                        entry3.append(places.get(i).getName());
                        Log.d(TAG, "ADDED " + places.get(i).getName());
                        break;
                    case 3:
                        entry4.append(places.get(i).getName());
                        Log.d(TAG, "ADDED " + places.get(i).getName());
                        break;
                }
            }
        }
    }

    /*Callback for when a date is chosen from dialog*/
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = new Date(dayOfMonth, month + 1, year); //+1 to accomodate for the ol' [0-11] array being 12 in size...
        dateChosen = true;
    }
}
