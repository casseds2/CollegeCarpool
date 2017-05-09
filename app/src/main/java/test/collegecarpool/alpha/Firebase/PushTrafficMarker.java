package test.collegecarpool.alpha.Firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import test.collegecarpool.alpha.MapsUtilities.LatLng;

public class PushTrafficMarker {

        public PushTrafficMarker(){
        }

        public void pushTrafficMarkerToFirebase(LatLng latLng) {
            DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("MapMarkers");
            HashMap<String, Object> token = new HashMap<>();
            token.put("/trafficMarkers/", latLng);
            tokenRef.updateChildren(token);
        }
}
