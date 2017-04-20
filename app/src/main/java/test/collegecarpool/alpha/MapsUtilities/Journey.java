package test.collegecarpool.alpha.MapsUtilities;

import java.util.ArrayList;
import java.util.HashMap;

import test.collegecarpool.alpha.UserClasses.Date;

public class Journey {

    private Date date;
    private ArrayList<String> places;
    private String timeStamp;

    public Journey(){}

    public Journey(Date date, ArrayList<String> places){
        this.date = date;
        this.places = places;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date) { this.date = date; }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> info = new HashMap<>();
        info.put("date", date);
        info.put("places", places);
        return info;
    }

    public ArrayList<String> getPlaces(){
        return places;
    }

    public void setPlaces(ArrayList<String> places) { this.places = places; }

    public String toString(){
        return this.date.toString() + " : " + this.places.toString();
    }
}
