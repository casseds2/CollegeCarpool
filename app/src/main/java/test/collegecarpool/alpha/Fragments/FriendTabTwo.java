package test.collegecarpool.alpha.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.Map;

import test.collegecarpool.alpha.Adapters.FriendTabTwoAdapter;
import test.collegecarpool.alpha.Firebase.FirebaseFriend;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.UserClasses.Friend;
import test.collegecarpool.alpha.UserClasses.UserProfile;

public class FriendTabTwo extends Fragment{

    private FirebaseUser user;
    private final String TAG = "FriendTabTwo";
    private HashMap<String, String> requestMap;
    private ArrayList<String> userNames;
    private View fragLayout;
    private  FriendTabTwoAdapter adapter;
    private String myUserName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragLayout = inflater.inflate(R.layout.friend_tab_2, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        getMyUserName();
        requestMap = new HashMap<>();
        userNames = new ArrayList<>();
        return fragLayout;
    }

    /*Fragment View Created and Good to Go*/
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRequestList();
    }

    /*registerForContextMenu Callback ... Allows onLongCLick of Menu Items*/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.friend_tab_2_popup_menu, menu);
    }

    /*Manages Clicked Item In the Context Menu*/
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = adapterContextMenuInfo.position;
        String userName, userID;
        FirebaseFriend firebaseFriend = new FirebaseFriend();
        switch(item.getItemId()){
            case R.id.accept_friend_request :
                userName = userNames.get(index);
                userID = getUserIDFromMap(userName);
                Friend friend = new Friend(userID, userName);
                HashMap<String, Object> friendMap = new HashMap<>();
                HashMap<String, Object> userMap = new HashMap<>();
                friendMap.put(userID, friend.toMap());
                Friend me = new Friend(user.getUid(), myUserName);
                userMap.put(user.getUid(), me.toMap());
                firebaseFriend.pushFriendToFirebase(friendMap, userMap, userID);
                firebaseFriend.removeRequest(userID);
                requestMap.remove(userID);
                userNames.remove(index);
                Toast.makeText(getContext(), "Accepted Request", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                break;
            case R.id.reject_friend_request :
                userName = userNames.get(index);
                userID = getUserIDFromMap(userName);
                firebaseFriend.removeRequest(userID);
                requestMap.remove(userID);
                userNames.remove(index);
                Toast.makeText(getContext(), "Rejected Request", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }

    /*Find the corresponding user ID to userName*/
    private String getUserIDFromMap(String userName){
        for(Map.Entry entry : requestMap.entrySet()){
            if(entry.getValue().equals(userName))
                return (String) entry.getKey();
        }
        return " ";
    }

    /*Initialize the ListView*/
    private void initListView() {
        getUserNamesFromMap();
        Log.d(TAG, "Context: " + String.valueOf(fragLayout.getContext()));
        Log.d(TAG, "UserNames: " + userNames);
        adapter = new FriendTabTwoAdapter(fragLayout.getContext(), R.layout.friend_tab_2_list, userNames);
        ListView listView = (ListView) fragLayout.findViewById(R.id.friend_tab_2_list_view);
        listView.setAdapter(adapter);
        Log.d(TAG, "ListView" + listView);
        registerForContextMenu(listView);
    }

    /*Return all userNames to display in UI*/
    private void getUserNamesFromMap(){
        for(Map.Entry entry : requestMap.entrySet()){
            userNames.add((String) entry.getValue());
            Log.d(TAG, "Added To Array: " + String.valueOf(entry.getValue()));
        }
    }

    private void getMyUserName(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                myUserName = userProfile.getFirstName() + " " + userProfile.getSecondName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*Retrieve All Requests From Database*/
    private void initRequestList(){
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid()).child("FriendRequest");
        requestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> requestIDs = dataSnapshot.getChildren();
                for(DataSnapshot requestID : requestIDs){
                    Iterable<DataSnapshot> details = requestID.getChildren();
                    String userID = "";
                    String userName = "";
                    for(DataSnapshot detail : details){
                        if(detail.getKey().equals("requestID")){
                            userID = detail.getValue(String.class);
                        }
                        if(detail.getKey().equals("userName")){
                            userName = detail.getValue(String.class);
                        }
                    }
                    requestMap.put(userID, userName);
                    Log.d(TAG, "User: " + userName + ", ID: " + userID);
                }
                initListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
