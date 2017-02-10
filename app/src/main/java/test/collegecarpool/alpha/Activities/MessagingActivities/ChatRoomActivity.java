package test.collegecarpool.alpha.Activities.MessagingActivities;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import test.collegecarpool.alpha.Activities.HomeScreenActivity;
import test.collegecarpool.alpha.Activities.ProfileActivity;
import test.collegecarpool.alpha.Activities.SettingsActivity;
import test.collegecarpool.alpha.Activities.LoginAndRegistrationActivities.SigninActivity;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.Services.FirebaseIdService;

public class ChatRoomActivity extends AppCompatActivity {

    private final static String TAG = "MessageActivity";

    private EditText carNameField;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList <String> carList = new ArrayList<>();

    private DatabaseReference carNameRef = FirebaseDatabase.getInstance().getReference("CarChatGroups");
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        Button btnAddCar = (Button) findViewById(R.id.btnAddCar);
        carNameField = (EditText) findViewById(R.id.carName);
        ListView listview = (ListView) findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carList);
        listview.setAdapter(arrayAdapter);

        Intent intent = new Intent(this, FirebaseIdService.class);
        startService(intent);

        Log.d(TAG, "Token: " + FirebaseInstanceId.getInstance().getToken());

        initDrawer();

        btnAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carNameField.getText() != null){
                    Map<String, Object> map = new HashMap<>();
                    if(auth.getCurrentUser() != null) {
                        map.put(carNameField.getText().toString(), auth.getCurrentUser().getUid());
                    }
                    carNameRef.updateChildren(map);
                    carNameField.setText("");
                }
            }
        });

        carNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iter = dataSnapshot.getChildren().iterator();
                HashSet <String> tempSet = new HashSet<>();

                while(iter.hasNext()){
                    tempSet.add(((DataSnapshot) iter.next()).getKey());
                }
                carList.clear();
                carList.addAll(tempSet);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChatRoomActivity.this, MessageActivity.class);
                intent.putExtra("carChatName", ((TextView)view).getText().toString());
                startActivity(intent);
            }
        });
    }

    public void initDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(ChatRoomActivity.this, HomeScreenActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_messages:
                        actionBarDrawerToggle.syncState();
                        startActivity(new Intent(ChatRoomActivity.this, MessageActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(ChatRoomActivity.this, ProfileActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_settings:
                        startActivity(new Intent(ChatRoomActivity.this, SettingsActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_logout:
                        auth.signOut();
                        startActivity(new Intent(ChatRoomActivity.this, SigninActivity.class));
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}