package test.collegecarpool.alpha.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class NotifyUserRideRequestStatus {

    private FirebaseUser user;

    public NotifyUserRideRequestStatus(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    public void notifyUserAccepted(String requestID){
        DatabaseReference rejectRef = FirebaseDatabase.getInstance().getReference("UserProfile/" + user.getUid());
        HashMap<String, Object> response = new HashMap<>();
        HashMap<String, Object> status = new HashMap<>();
        status.put("Status", "Accepted");
        response.put("/RideRequests/" + requestID + "/Response/", status);
        rejectRef.updateChildren(response);
        FirebaseDatabase.getInstance().getReference("UserProfile/" + user.getUid() + "/RideRequests/" + requestID).setValue(null);
    }

    public void notifyUserRejected(String requestID){
        DatabaseReference rejectRef = FirebaseDatabase.getInstance().getReference("UserProfile/" + user.getUid());
        HashMap<String, Object> response = new HashMap<>();
        HashMap<String, Object> status = new HashMap<>();
        status.put("Status", "Rejected");
        response.put("/RideRequests/" + requestID + "/Response/", status);
        rejectRef.updateChildren(response);
        FirebaseDatabase.getInstance().getReference("UserProfile/" +user.getUid() + "/RideRequests/" + requestID).setValue(null);
    }
}
