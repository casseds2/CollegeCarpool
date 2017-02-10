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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import test.collegecarpool.alpha.Activities.HomeScreenActivity;
import test.collegecarpool.alpha.Activities.ProfileActivity;
import test.collegecarpool.alpha.Activities.SettingsActivity;
import test.collegecarpool.alpha.Activities.LoginAndRegistrationActivities.SigninActivity;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.UserClasses.Message;
import test.collegecarpool.alpha.UserClasses.UserProfile;

public class MessageActivity extends AppCompatActivity {

    private EditText message;
    private TextView chatMessageList;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private String carChatName;

    private String user;

    final static String TAG = "MessageActivity";

    private DatabaseReference carChatNameRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initDrawer();

        Button btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
        message = (EditText) findViewById(R.id.message);
        chatMessageList = (TextView) findViewById(R.id.chatMessageList);

        carChatName = (String) getIntent().getExtras().get("carChatName");
        Log.d(TAG, carChatName);

        carChatNameRef = FirebaseDatabase.getInstance().getReference("CarChatGroups");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserProfile");
        auth = FirebaseAuth.getInstance();
        user = String.valueOf(userRef.getKey());

        setTitle(carChatName);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = message.getText().toString();
                Message myMessage = new Message(messageText, user);
                carChatNameRef.child(carChatName).push().setValue(myMessage);
                message.setText("");
            }
        });

        carChatNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMessage(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                addMessage(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable <DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot dataSnapshot1 : dataSnapshots) {
                    UserProfile userProfile = dataSnapshot1.getValue(UserProfile.class);
                    if(auth.getCurrentUser() != null && userProfile.getEmail().equals(auth.getCurrentUser().getEmail())){
                        user = userProfile.getFirstName() + " " + userProfile.getSecondName();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addMessage(DataSnapshot dataSnapshot){
        chatMessageList.setText("");
        Iterable <DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
        for(DataSnapshot data : dataSnapshots){
            Message temp = data.getValue(Message.class);
            String firebaseMessage = temp.getMessage();
            String firebaseUserName = temp.getMessageSender();
            chatMessageList.append(firebaseUserName + ": " + firebaseMessage + "\n");
        }
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
                    case R.id.nav_profile:
                        startActivity(new Intent(MessageActivity.this, ProfileActivity.class));
                        onStop();
                        return true;
                    case R.id.nav_settings:
                        startActivity(new Intent(MessageActivity.this, SettingsActivity.class));
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
