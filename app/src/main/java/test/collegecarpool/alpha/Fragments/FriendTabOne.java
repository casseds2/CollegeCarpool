package test.collegecarpool.alpha.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import test.collegecarpool.alpha.R;


public class FriendTabOne extends Fragment{

    private FirebaseUser user;
    private HashMap<String, Object> friendMap;
    private final String TAG = "FriendActivity";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        friendMap = new HashMap<>();
        initFriendList();
        return inflater.inflate(R.layout.friend_tab_1, container, false);
    }

    private void initFriendList(){
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid()).child("Friends");
        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> friends = dataSnapshot.getChildren(); //friend userIDs
                if(null != dataSnapshot.getChildren()){
                    String friendID = "";
                    String friendName = "";
                    for(DataSnapshot friend : friends){
                        Iterable<DataSnapshot> friendDetails = friend.getChildren();
                        for(DataSnapshot detail : friendDetails){
                            if(detail.getKey().equals("friendID")){
                                friendID = detail.getValue(String.class);
                            }
                            if(detail.getKey().equals("userName")){
                                friendName = detail.getValue(String.class);
                            }
                        }
                        if(null != friendID && friendName != null) {
                            friendMap.put(friendID, friendName);
                            Log.d(TAG, "Friend ID: " + friendID + ", Name: " + friendName);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
