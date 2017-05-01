package test.collegecarpool.alpha.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FCMTokenPusher {

    private FirebaseUser user;

    public FCMTokenPusher(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    public void pushFCMToken(String fcmToken){
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid());
        HashMap<String, Object> token = new HashMap<>();
        token.put("/fcmToken/", fcmToken);
        tokenRef.updateChildren(token);
    }
}
