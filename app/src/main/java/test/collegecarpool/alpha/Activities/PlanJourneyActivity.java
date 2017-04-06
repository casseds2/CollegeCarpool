package test.collegecarpool.alpha.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import test.collegecarpool.alpha.R;

public class PlanJourneyActivity extends AppCompatActivity {

    private TextView entry1, entry2, entry3, entry4;
    static final String TAG = "PLAN JOURNEY";
    private ArrayList<Place> places = new ArrayList<>();
    private PlaceAutocompleteFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_journey);
        initSubmitButton();
        initSearchBar();
        initAddressFields();
        initRemoveButtons();
        initViewJourney();
        clearUI();
    }

    /*Log list of elements currently entered*/
    private void printPlacesArray(ArrayList<Place> p){
        for(int i = 0; i < p.size(); i++){
            Place pTemp = p.get(i);
            Log.d(TAG, "Element(" + i + ") is " + pTemp.getName());
        }
    }

    /*Initialize the Remove Entry Buttons*/
    private void initRemoveButtons(){
        Button removeEntry1 = (Button) findViewById(R.id.remove_entry1);
        removeEntry1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUI();
                if(!places.isEmpty()) {
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
                if(!places.isEmpty()) {
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
                if(!places.isEmpty()) {
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
                if(!places.isEmpty()) {
                    Log.d(TAG, "Removed : " + places.get(3).getName());
                    places.remove(3);
                }
                updateUiAddress(places);
                printPlacesArray(places);
            }
        });
    }

    /*Initialize the View Journey Button*/
    private void initViewJourney(){
        Button btn2 = (Button) findViewById(R.id.view_journey);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HANDLE LOGIC FOR GETTING THE GPS LOCATIONS OF THE ENTERED FIELDS FROM PLACES ARRAY
                //MAP THE POLYLINE
                //ESTIMATE TIME IT WILL TAKE
                //DJAKSTRAS ALGORITHM FOR TRAVELLING SALESMAN PROBLEM
                //CHECK OUT GOOGLE'S OPTIMIZE METHOD
                ArrayList<LatLng> latLngs = new ArrayList<>();
                for(int i = 0; i < places.size(); i++){
                    LatLng latLng = places.get(i).getLatLng();
                    latLngs.add(latLng);
                }
                Intent intent = new Intent(PlanJourneyActivity.this, ViewJourneyActivity.class);
                intent.putExtra("LAT/LNG", latLngs);
                startActivity(intent);
            }
        });
    }

    /*Initialize the submit button to confirm an address*/
    private void initSubmitButton(){
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
    private void initAddressFields(){
        entry1 = (TextView) findViewById(R.id.entry_1);
        entry2 = (TextView) findViewById(R.id.entry_2);
        entry3 = (TextView) findViewById(R.id.entry_3);
        entry4 = (TextView) findViewById(R.id.entry_4);
    }

    /*Initialise the Autocomplete Fragment Search Bar*/
    private void initSearchBar(){
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.autocom);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if(places.size() == 4) {
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

    private void clearUI(){
        entry1.setText(getResources().getString(R.string.stop1));
        entry2.setText(getResources().getString(R.string.stop2));
        entry3.setText(getResources().getString(R.string.stop3));
        entry4.setText(getResources().getString(R.string.stop4));
    }

    /*Update the Route List in the UI*/
    private void updateUiAddress(ArrayList<Place> places){
        if(places != null) {
            for (int i = 0; i < places.size(); i++) {
                switch(i){
                    case 0 :
                        entry1.append(places.get(i).getName());
                        Log.d(TAG, "ADDED " + places.get(i).getName());
                        break;
                    case 1 :
                        entry2.append(places.get(i).getName());
                        Log.d(TAG, "ADDED " + places.get(i).getName());
                        break;
                    case 2 :
                        entry3.append(places.get(i).getName());
                        Log.d(TAG, "ADDED " + places.get(i).getName());
                        break;
                    case 3 :
                        entry4.append(places.get(i).getName());
                        Log.d(TAG, "ADDED " + places.get(i).getName());
                        break;
                }
            }
        }
    }
}
