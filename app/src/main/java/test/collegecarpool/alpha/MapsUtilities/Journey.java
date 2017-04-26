package test.collegecarpool.alpha.MapsUtilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import test.collegecarpool.alpha.UserClasses.Date;

public class Journey implements Serializable { //Implements Serializable So That It can Be Sent as an Extra

    private Date date;
    private ArrayList<Waypoint> waypoints;

    public Journey() {
    }

    public Journey(Date date, ArrayList<Waypoint> waypoints) {
        this.date = date;
        this.waypoints = waypoints;
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

    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(ArrayList<Waypoint> places) {
        this.waypoints = places;
    }

    public String toString() {
        return this.date.toString() + " : " + this.waypoints.toString();
    }

    public boolean isElementOf(ArrayList<Journey> journeys){
        for(Journey journey : journeys){
            if(journey.compareTo(this))
                return true;
        }
        return false;
    }

    /*Sort A List of Journeys Based On Their Date in A Bubble Sort Fashion*/
    public ArrayList<Journey> sortJourneys(ArrayList<Journey> journeys){
        if(journeys.size() > 1) {
            for (int i = 0; i < journeys.size() - 1; i++) {
                Date dateFirst = journeys.get(i).getDate();
                Date dateSecond = journeys.get(i+1).getDate();
                if(!dateFirst.inThePastTo(dateSecond)) {
                    Collections.swap(journeys, i, i + 1);
                    i = 0;
                }
            }
        }
        return journeys;
    }

    /*Compares One Journey To Another*/
    public boolean compareTo(Journey journey) {
        if(journey == null)
            return false;
        if(this.getDate() == null || this.getWaypoints() == null || journey.getDate() == null || journey.getWaypoints() == null)
            return false;
        if (this.date.compareTo(journey.getDate())) {
            ArrayList<Waypoint> tempList = journey.getWaypoints();
            if (this.waypoints.size() == tempList.size()) {
                for (int i = 0; i < this.waypoints.size(); i++) {
                    if (this.waypoints.get(i).getName().equals(tempList.get(i).getName())) {
                        return this.waypoints.get(i).getLatLng().equals(tempList.get(i).getLatLng());
                    }
                }
                return false;
            }
            return false;
        }
        return false;
    }
}
