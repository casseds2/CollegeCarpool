package test.collegecarpool.alpha.Tools;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URL;

public class URLBuilder {

    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private FirebaseUser firebaseUser;
    private URL url;
    private String urlStart = "https://maps.googleapis.com/maps/api/directions/json?";

    public URLBuilder(){}

    public URL URLBuilder(LatLng one){
        return url;
    }

    public URL URLBuilder(LatLng one, LatLng two){
        return url;
    }

    public URL URLBuilder(LatLng one, LatLng two, LatLng three){
        return url;
    }

    public URL URLBuilder(LatLng  one, LatLng two, LatLng three, LatLng four){
        return url;
    }

    private void initFirebase(FirebaseAuth auth){
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            firebaseUser = auth.getCurrentUser();
            userRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(firebaseUser.getUid()).child("Location");
        }
    }
}
