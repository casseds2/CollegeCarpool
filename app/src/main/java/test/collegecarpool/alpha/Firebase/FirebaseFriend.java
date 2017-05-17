package test.collegecarpool.alpha.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FirebaseFriend {

    private FirebaseUser user;

    public FirebaseFriend(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    public void pushFriendToFirebase(HashMap<String, Object> friendMap, HashMap<String, Object> userMap, String userID){
        DatabaseReference myFriendsRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid()).child("Friends");
        myFriendsRef.updateChildren(friendMap);
        DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(userID).child("Friends");
        friendRef.updateChildren(userMap);
    }

    public void removeRequest(String requestID){
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid()).child("FriendRequest").child(requestID);
        requestRef.setValue(null);
    }

    public void removeFriend(String friendID, String myID){
        DatabaseReference myFriendRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid()).child("Friends").child(friendID);
        myFriendRef.setValue(null);
        DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(friendID).child("Friends").child(myID);
        friendRef.setValue(null);
    }
}
