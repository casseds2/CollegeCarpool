package test.collegecarpool.alpha.Firebase;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class PolyLinePusher {

    private FirebaseUser user;
    private DatabaseReference databaseReference;

    public PolyLinePusher(FirebaseUser user){
        this.user = user;
    }

    public void pushPolyLine(String encodedPoly, List<LatLng> markers){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> polyMap = new HashMap<>();
        HashMap<String, Object> markerMap = new HashMap<>();
        polyMap.put("/ActiveJourneys/" + user.getUid() + "/Polyline/", encodedPoly);
        markerMap.put("/ActiveJourneys/" + user.getUid() + "/Markers", markers);
        databaseReference.updateChildren(polyMap);
        databaseReference.updateChildren(markerMap);
    }

    public void nullify(){
        databaseReference.child("/ActiveJourneys/" + user.getUid()).setValue(null);
    }
}
