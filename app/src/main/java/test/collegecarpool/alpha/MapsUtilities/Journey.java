package test.collegecarpool.alpha.MapsUtilities;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import test.collegecarpool.alpha.UserClasses.Date;

public class Journey implements Serializable { //Implements Serializable So That It can Be Sent as an Extra

    private Date date;
    private ArrayList<Waypoint> waypoints;

    public Journey() {
        date = new Date();
        waypoints = new ArrayList<>();
    }

    public Journey(Date date, ArrayList<Waypoint> waypoints) {
        this.date = date;
        this.waypoints = waypoints;
    }

    public void addWaypoint(Waypoint waypoint){
        waypoints.add(waypoint);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> info = new HashMap<>();
        info.put("date", date);
        info.put("journeyWaypoints", waypoints); //WAS ORIGINALLY ARRAY LIST<STRING>
        return info;
    }

    /*Used To Remove Markers*/
    public void removeWaypoint(Waypoint waypoint){
        for(Waypoint marker : waypoints){
            if(null != marker && waypoint != null && waypoint.isTheSameAs(marker)) {
                waypoints.remove(waypoint);
                Log.d("JOURNEY", waypoint.toString() + " WAS REMOVED");
                return;
            }
        }
    }

    /*So the User Can Push S Serializable LatLngs to Firebase From Service Active Journey*/
    public ArrayList<LatLng> convertToMyLatLngs(List<com.google.android.gms.maps.model.LatLng> toBeConverted){
        ArrayList<LatLng> myLatLngs = new ArrayList<>();
        for(com.google.android.gms.maps.model.LatLng latLng : toBeConverted){
            myLatLngs.add(new LatLng(latLng.latitude, latLng.longitude));
        }
        return myLatLngs;
    }

    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(ArrayList<Waypoint> places) {
        this.waypoints = places;
    }

    public String toString() {
        return this.date.toString() + " : " + this.waypoints.toString();
    }

    /*Return True If A Journey is a Part of A Journey List*/
    public boolean isElementOf(ArrayList<Journey> journeys){
        if(journeys.isEmpty())
            return false;
        for(Journey journey : journeys){
            if(this.isTheSameAs(journey)) {
                return true;
            }
        }
        return false;
    }

    /*Compares One Journey To Another*/
    /*Return false If Journeys Are Not The Same, True if they are the same*/
    public boolean isTheSameAs(Journey journey) {
        if(journey == null)
            return false;
        if(this.getDate() == null || this.getWaypoints() == null || journey.getDate() == null || journey.getWaypoints() == null)
            return false;
        if (date.isEqualTo(journey.getDate())) { //If dates are the same
            ArrayList<Waypoint> tempList = journey.getWaypoints();
            for(int i = 0; i < waypoints.size()-1; i++){
                if(!waypoints.get(i).toGoogleLatLng().equals(tempList.get(i).toGoogleLatLng())){
                   return false;
                }
                if(!waypoints.get(i).getName().equals(tempList.get(i).getName())){
                    return false;
                }
            }
            return true;
        }
        else
            return false;
    }

    /*Return true if journey exists in list*/
    public boolean isContainedIn(ArrayList<Journey> journeys){
        for(Journey journey: journeys){
            if(this.date.isEqualTo(journey.getDate())){ //First If the Dates Are Equal
                ArrayList<Waypoint> thisWaypoints = this.getWaypoints();
                ArrayList<Waypoint> journeyWaypoints = journey.getWaypoints();
                if(thisWaypoints.size() == journeyWaypoints.size()){
                    for(int i = 0; i < thisWaypoints.size(); i++){ //Compare this waypoints with journey waypoints
                        Waypoint thisWaypoint = thisWaypoints.get(i);
                        Waypoint journeyWaypoint = journeyWaypoints.get(i);
                        if(thisWaypoint.isTheSameAs(journeyWaypoint)){
                            if(i == thisWaypoints.size()){
                                return true;
                            }
                        }
                        else
                            break;
                    }
                }
            }
        }
        return false;
    }
}























