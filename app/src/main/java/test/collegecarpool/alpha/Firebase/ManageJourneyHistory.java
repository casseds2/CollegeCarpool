package test.collegecarpool.alpha.Firebase;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import test.collegecarpool.alpha.MapsUtilities.Journey;
import test.collegecarpool.alpha.MapsUtilities.Waypoint;

public class ManageJourneyHistory {

    private DatabaseReference historyRef;
    private FirebaseUser user;

    public ManageJourneyHistory(FirebaseUser user){
        this.user = user;
        historyRef = FirebaseDatabase.getInstance().getReference("UserProfile").child(user.getUid()).child("JourneyPlanner").child("History");
        //pastWaypoints = new ArrayList<>();
    }
    public void pushJourneyToHistory(Journey journey){
        /*Split the Journey Into Its Basic Waypoints*/
        ArrayList<Waypoint> journeyWaypoints = journey.getWaypoints();
        /*For Every Waypoint, If Not An Element of Firebase Already*/
        /*Due to Nature Of Firebase, Don't need to Check If it Already Exists because It Will Just Be Overwritten*/
        for(Waypoint waypoint : journeyWaypoints){
            HashMap<String, Object> waypointObject = new HashMap<>();
            waypointObject.put(waypoint.getName(), waypoint);
            historyRef.updateChildren(waypointObject);
        }
    }

    /*Will Need Puller Method to Populate List View In View History Activity*/
}
