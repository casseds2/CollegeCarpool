package test.collegecarpool.alpha.Fragments;

import android.content.Intent;
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

import test.collegecarpool.alpha.Activities.ViewFriendPlanner;
import test.collegecarpool.alpha.Adapters.FriendTabOneAdapter;
import test.collegecarpool.alpha.Firebase.FirebaseFriend;
import test.collegecarpool.alpha.MessagingActivities.MessageActivity;
import test.collegecarpool.alpha.R;

public class FriendTabOne extends Fragment{

    private FirebaseUser user;
    private HashMap<String, Object> friendMap;
    private ArrayList<String> userNames;
    private FriendTabOneAdapter adapter;
    private final String TAG = "FriendTabOne";
    private View fragLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        fragLayout = inflater.inflate(R.layout.friend_tab_1, container, false);
        friendMap = new HashMap<>();
        userNames = new ArrayList<>();
        return fragLayout;
    }

    /*Fragment View Created and Good to Go*/
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFriendList();
    }

    private void getUserNamesFromMap(){
        for(Map.Entry entry : friendMap.entrySet()){
            userNames.add((String) entry.getValue());
            Log.d(TAG, "Added To Array: " + String.valueOf(entry.getValue()));
        }
    }

    private void initListView() {
        getUserNamesFromMap();
        Log.d(TAG, "Context: " + String.valueOf(fragLayout.getContext()));
        Log.d(TAG, "UserNames: " + userNames);
        adapter = new FriendTabOneAdapter(fragLayout.getContext(), R.layout.friend_tab_1_list, userNames);
        ListView listView = (ListView) fragLayout.findViewById(R.id.friend_tab_1_list_view);
        listView.setAdapter(adapter);
        Log.d(TAG, "ListView" + listView);
        registerForContextMenu(listView);
    }

    /*registerForContextMenu Callback ... Allows onLongCLick of Menu Items*/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.friend_tab_1_popup_menu, menu);
    }

    /*Find the corresponding user ID to userName*/
    private String getUserIDFromMap(String userName){
        for(Map.Entry entry : friendMap.entrySet()){
            if(entry.getValue().equals(userName))
                return (String) entry.getKey();
        }
        return " ";
    }

    /*Manages Clicked Item In the Context Menu*/
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = adapterContextMenuInfo.position;
        String userName, userID;
        switch(item.getItemId()){
            case R.id.message_friend :
                userName = userNames.get(index);
                userID = getUserIDFromMap(userName);
                Intent intent = new Intent(getActivity().getApplicationContext(), MessageActivity.class);
                intent.putExtra("ReceiverID", userID);
                startActivity(intent);
                break;
            case R.id.delete_friend :
                userName = userNames.get(index);
                userID = getUserIDFromMap(userName);
                new FirebaseFriend().removeFriend(userID, user.getUid());
                friendMap.remove(userID);
                userNames.remove(userName);
                adapter.notifyDataSetChanged();
                break;
            case R.id.view_friend_planner :
                userName = userNames.get(index);
                userID = getUserIDFromMap(userName);
                Intent viewFriendPlanner = new Intent(getActivity().getApplicationContext(), ViewFriendPlanner.class);
                viewFriendPlanner.putExtra("FriendID", userID);
                startActivity(viewFriendPlanner);
        }
        return super.onContextItemSelected(item);
    }

    /*Get the Friend List*/
    private void initFriendList(){
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid()).child("Friends");
        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> friends = dataSnapshot.getChildren();
                if(null != dataSnapshot.getChildren()){
                    for(DataSnapshot friend : friends) {
                        String userID = "";
                        String userName = "";
                        Iterable<DataSnapshot> details = friend.getChildren();
                        for(DataSnapshot detail : details){
                            if(detail.getKey().equals("friendID"))
                                userID = detail.getValue(String.class);
                            if(detail.getKey().equals("userName"))
                                userName = detail.getValue(String.class);
                        }
                        friendMap.put(userID, userName);
                        Log.d(TAG, "Friend ID: " + userID + ", Name: " + userName);
                    }
                }
                initListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
