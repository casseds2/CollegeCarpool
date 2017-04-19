package test.collegecarpool.alpha.MapsUtilities;

import java.util.ArrayList;
import java.util.HashMap;

import test.collegecarpool.alpha.UserClasses.Date;

public class Journey {

    private Date date;
    private ArrayList<String> places;

    public Journey(){}

    public Journey(Date date, ArrayList<String> places){
        this.date = date;
        this.places = places;
    }

    public Date getDate(){
        return date;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> info = new HashMap<>();
        info.put("date", date);
        info.put("places", places);
        return info;
    }

    public ArrayList<String> getPlaces(){
        return places;
    }
}
