package test.collegecarpool.alpha.MessagingActivities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;

import test.collegecarpool.alpha.Activities.FriendActivity;
import test.collegecarpool.alpha.Activities.HomeScreenActivity;
import test.collegecarpool.alpha.Activities.PaymentActivity;
import test.collegecarpool.alpha.Activities.PlanJourneyActivity;
import test.collegecarpool.alpha.LoginAndRegistrationActivities.SigninActivity;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.UserClasses.UserProfile;

public class MessageActivity extends AppCompatActivity {

    private EditText message;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    final static String TAG = "MessageActivity";

    private DatabaseReference receiverChatRef;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private String receiverID;
    private TextView messageList;

    String myUserName;

    ArrayList<String> allMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        /*Initialise Firebase Components*/
        auth = FirebaseAuth.getInstance();
        if(auth != null)
            user = auth.getCurrentUser();

        /*The ID of the Person We Are Sending A Message To*/
        receiverID = getIntent().getStringExtra("ReceiverID");
        Log.d(TAG, "Receiver ID is: " + receiverID);

        /*Initialise the References Required*/
        receiverChatRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(receiverID);

        /*Set The Activity Name To The User You Are Sending To*/
        getUserNames();

        message = (EditText) findViewById(R.id.message);
        messageList = (TextView) findViewById(R.id.chatMessageList);

        /*Populate the Page With Any Previous Messages*/
        populateMessages();

        initDrawer();

        Button btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = message.getText().toString();
                Message myMessage = new Message(myUserName, messageText);
                sendMessage(myMessage);
                message.setText("");
            }
        });

        /*Set Up The Broadcast Receiver*/
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("message_received"));
    }

    /*On Receive Of A Broadcast*/
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("ReceiverID") != null) {
                receiverID = intent.getStringExtra("ReceiverID");
                receiverChatRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(receiverID);
                getUserNames();
                populateMessages();
                Log.d(TAG, "MESSAGE RECEIVED");
            }
            else
                Toast.makeText(MessageActivity.this, "Could Not Read Request LatLng", Toast.LENGTH_SHORT).show();
        }
    };

    /*Send A Message*/
    private void sendMessage(Message m){
        HashMap<String, Object> receiverMap = new HashMap<>();
        receiverMap.put("/Messaging/" + user.getUid() + "/" + m.getTimeStamp() + "/", m.toMap());
        receiverChatRef.updateChildren(receiverMap);
        Log.d(TAG, "MESSAGE SENT");
    }

    /*Pull Messages Down And Show Them in Text View*/
    private void populateMessages() {
        Log.d(TAG, "POPULATING MESSAGES");
        receiverChatRef.child("Messaging").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*List Of All Messages (To Be Sent To Message Adapter)*/
                allMessages = new ArrayList<>();
                messageList.setText("");
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for(DataSnapshot dataSnap : data){ //Cycle Through Users
                    Log.d(TAG, "KEY IS: " + dataSnap.getKey());
                    if(dataSnap.getKey().equals(user.getUid())) {
                        Log.d(TAG, "RECEIVER ID MATCHED");
                        Iterable<DataSnapshot> data1 = dataSnap.getChildren();
                        for (DataSnapshot dataSnap1 : data1) {
                            Message storedMessage = dataSnap1.getValue(Message.class);
                            Log.d(TAG, "Message: " + storedMessage.getMessage());
                            allMessages.add(storedMessage.getSender() + ": " + storedMessage.getMessage());
                            Log.d(TAG, "STORED MESSAGE IS: " + storedMessage.getMessage());
                        }
                    }
                }
                for(String s : allMessages){
                    messageList.append(s + "\n");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*Sets the Chat To The Name of The Person I'm Sending To*/
    private void getUserNames(){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                for(DataSnapshot dataSnap : data){
                    String userID = dataSnap.getKey();
                    if(userID.equals(user.getUid())){
                        UserProfile userProfile = dataSnap.getValue(UserProfile.class);
                        myUserName = userProfile.getFirstName() + " " + userProfile.getSecondName();
                    }
                    if(userID.equals(receiverID)){
                        UserProfile receiverUserProfile = dataSnap.getValue(UserProfile.class);
                        String chatName = receiverUserProfile.getFirstName() + " " + receiverUserProfile.getSecondName();
                        if(getSupportActionBar() != null){
                            getSupportActionBar().setTitle(chatName);
                        }
                    }
                }
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
                        startActivity(new Intent(MessageActivity.this, HomeScreenActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_messages:
                        startActivity(new Intent(MessageActivity.this, ChatRoomActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_payment:
                        startActivity(new Intent(MessageActivity.this, PaymentActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_journey:
                        startActivity(new Intent(MessageActivity.this, PlanJourneyActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_friends:
                        startActivity(new Intent(MessageActivity.this, FriendActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_logout:
                        auth.signOut();
                        startActivity(new Intent(MessageActivity.this, SigninActivity.class));
                        onStop();
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
