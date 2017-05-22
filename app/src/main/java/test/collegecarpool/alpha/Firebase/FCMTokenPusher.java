package test.collegecarpool.alpha.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FCMTokenPusher {

    private FirebaseUser user;
    private FirebaseAuth auth;

    public FCMTokenPusher(){
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null)
            user = auth.getCurrentUser();
    }

    public void pushFCMToken(String fcmToken){
        if(auth.getCurrentUser() != null) {
            DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid());
            HashMap<String, Object> token = new HashMap<>();
            token.put("/fcmToken/", fcmToken);
            tokenRef.updateChildren(token);
        }
    }
}
