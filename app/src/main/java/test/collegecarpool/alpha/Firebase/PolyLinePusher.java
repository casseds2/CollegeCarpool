package test.collegecarpool.alpha.Firebase;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import test.collegecarpool.alpha.MapsUtilities.Journey;

public class PolyLinePusher {

    private FirebaseUser user;
    private DatabaseReference databaseReference;

    public PolyLinePusher(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth != null){
            user = auth.getCurrentUser();
        }
    }

    /*Push A Polyline To Firebase*/
    public void pushPolyLine(String encodedPoly, List<LatLng> markers){
        HashMap<String, test.collegecarpool.alpha.MapsUtilities.LatLng> ways = new HashMap<>();
        ArrayList<test.collegecarpool.alpha.MapsUtilities.LatLng> myLatLngs = new Journey().convertToMyLatLngs(markers);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> polyMap = new HashMap<>();
        HashMap<String, Object> markerMap = new HashMap<>();
        polyMap.put("/ActiveJourneys/" + user.getUid() + "/Polyline/", encodedPoly);
        for(test.collegecarpool.alpha.MapsUtilities.LatLng latLng : myLatLngs){
            ways.put(String.valueOf(myLatLngs.indexOf(latLng)), latLng);
        }
        markerMap.put("/ActiveJourneys/" + user.getUid() + "/Markers", ways);
        databaseReference.updateChildren(polyMap);
        databaseReference.updateChildren(markerMap);
    }

    public void nullify(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if(databaseReference.child("/ActiveJourneys/" + user.getUid()) != null)
            databaseReference.child("/ActiveJourneys/" + user.getUid()).setValue(null);
    }
}
