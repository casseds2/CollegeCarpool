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

import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.Services.FirebaseIdService;

public class ChatRoomActivity extends AppCompatActivity {

    private final static String TAG = "MessageActivity";

    private Button btnAddCar;
    private EditText carNameField;

    private ListView listview;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList <String> carList = new ArrayList<>();

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private DatabaseReference carNameRef = FirebaseDatabase.getInstance().getReference("CarChatGroups");
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        btnAddCar = (Button) findViewById(R.id.btnAddCar);
        carNameField = (EditText) findViewById(R.id.carName);
        listview = (ListView) findViewById(R.id.listView);
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
                    map.put(carNameField.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                //Insert Intent to start chat room on selected Chat
                Intent intent = new Intent(ChatRoomActivity.this, MessageActivity.class);
                intent.putExtra("carChatName", ((TextView)view).getText().toString());
                startActivity(intent);
            }
        });
    }

    public void initDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_logout:
                        auth.signOut();
                        startActivity(new Intent(ChatRoomActivity.this, SigninActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(ChatRoomActivity.this, ProfileActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_messages:
                        startActivity(new Intent(ChatRoomActivity.this, ChatRoomActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_settings:
                        startActivity(new Intent(ChatRoomActivity.this, SettingsActivity.class));
                        onStop();
                        return true;
                }
                return false;
            }
        });
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
    }
}
