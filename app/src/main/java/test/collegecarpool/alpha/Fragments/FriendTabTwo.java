package test.collegecarpool.alpha.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import test.collegecarpool.alpha.Adapters.FriendTabTwoAdapter;
import test.collegecarpool.alpha.R;

public class FriendTabTwo extends Fragment{

    private FirebaseUser user;
    private final String TAG = "FriendTabTwo";
    private HashMap<String, Object> requestMap;
    private ListView listView;
    private ArrayList<String> userNames;
    private View fragLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragLayout = inflater.inflate(R.layout.friend_tab_2, container, false);
        listView = (ListView) fragLayout.findViewById(R.id.friend_tab_2_list_view);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        requestMap = new HashMap<>();
        userNames = new ArrayList<>();
        initRequestList();
        return inflater.inflate(R.layout.friend_tab_2, container, false);
    }

    private void initListView() {
        getUserNamesFromMap();
        Log.d(TAG, "Context: " + String.valueOf(getContext()));
        FriendTabTwoAdapter adapter = new FriendTabTwoAdapter(getContext(), R.layout.friend_tab_2_list, userNames);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    private void getUserNamesFromMap(){
        for(Map.Entry entry : requestMap.entrySet()){
            userNames.add((String) entry.getValue());
            Log.d(TAG, "Added To Array: " + String.valueOf(entry.getValue()));
        }
    }

    private void initRequestList(){
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid()).child("FriendRequest");
        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
