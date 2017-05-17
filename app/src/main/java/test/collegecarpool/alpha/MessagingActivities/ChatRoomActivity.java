package test.collegecarpool.alpha.MessagingActivities;

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
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import test.collegecarpool.alpha.Activities.FriendActivity;
import test.collegecarpool.alpha.Activities.HomeScreenActivity;
import test.collegecarpool.alpha.Activities.PaymentActivity;
import test.collegecarpool.alpha.Activities.PlanJourneyActivity;
import test.collegecarpool.alpha.Adapters.MyChatsAdapter;
import test.collegecarpool.alpha.LoginAndRegistrationActivities.SigninActivity;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.UserClasses.UserProfile;

public class ChatRoomActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private final static String TAG = "ChatActivity";
    private FirebaseAuth auth;
    private FirebaseUser user;
    private HashMap<String, String> userNames;
    private HashMap<String, String> myChats;
    private ArrayList<String> userNamesList;


    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        userNamesList = new ArrayList<>();
        userNames = new HashMap<>();
        myChats = new HashMap<>();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        getUserNames();

        initDrawer();
    }

    /*Initialise the List View Adapter*/
    private void initListView() {
        userNamesList = getUsersFromMap();
        MyChatsAdapter chatNames = new MyChatsAdapter(this, R.layout.chat_list, userNamesList);
        ListView listView = (ListView) findViewById(R.id.chat_room_list_view);
        listView.setAdapter(chatNames);
        listView.setClickable(true);
        listView.setOnItemClickListener(this);
        Log.d(TAG, "List View Initialized");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(ChatRoomActivity.this, MessageActivity.class);
        String userName = userNamesList.get(position);
        String userID = "";
        Log.d(TAG, "UserName For Message: " + userName);
        for(Map.Entry entry : myChats.entrySet()){
            if(entry.getValue().equals(userName)) {
                userID = (String) entry.getKey();
                Log.d(TAG, "UserID For Message: " + userID);
            }
        }
        intent.putExtra("ReceiverID", userID);
        startActivity(intent);
    }


    /*Transform the UserMap Into a List So We Can Manipulate the List*/
    private ArrayList<String> getUsersFromMap(){
        ArrayList<String> names = new ArrayList<>();
        for(Map.Entry entry : myChats.entrySet()){
            String userName = entry.getValue().toString();
            names.add(userName);
            Log.d(TAG, "Added " + userName + " to my Chats");
        }
        return names;
    }

    /*Get A Map of User Names and Their Unique User IDs*/
    private void getUserNames(){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot data : dataSnapshots){ //FOR EACH USER PROFILE
                    UserProfile userProfile = data.getValue(UserProfile.class);
                    String userName = userProfile.getFirstName() + " " + userProfile.getSecondName();
                    userNames.put(data.getKey(), userName);
                    Log.d(TAG, data.getKey() + " " + userName);
                }
                getMyChats();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*Get A List Of My Chats From the Map*/
    private void getMyChats(){
        DatabaseReference myChatRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid()).child("Messaging");
        myChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                Log.d(TAG, "Children: " + dataSnapshot.toString());
                Log.d(TAG, "getMyChats() Entered");
                Log.d(TAG, users.toString());
                for(DataSnapshot data : users) {
                    Log.d(TAG, "Key: " + data.getKey());
                    String userID = data.getKey();
                    Log.d(TAG, "UserID: " + userID);
                    if (userNames.containsKey(userID)) {
                        Log.d(TAG, "I've A Chat With " + userNames.get(userID));
                        myChats.put(userID, userNames.get(userID));
                    }
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
                        return true;
                    case R.id.nav_friends:
                        startActivity(new Intent(ChatRoomActivity.this, FriendActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_journey:
                        startActivity(new Intent(ChatRoomActivity.this, PlanJourneyActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_payment:
                        startActivity(new Intent(ChatRoomActivity.this, PaymentActivity.class));
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
