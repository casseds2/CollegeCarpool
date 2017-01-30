package test.collegecarpool.alpha.UserClasses;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by casseds95 on 23/01/2017.
 */

public class UserProfile {

    private String firstName;
    private String secondName;
    public String email;
    private double latitude;
    private double longitude;

    public UserProfile(){}

    public UserProfile(String firstName, String secondName, String email, double latitude, double longitude) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Exclude //Marks as excluded from database
    public Map<String, Object> toMap(){
        HashMap<String, Object> info = new HashMap<>();
        info.put("firstName", firstName);
        info.put("secondName", secondName);
        info.put("email", email);
        info.put("latitude", latitude);
        info.put("longitude", longitude);
        return info;
    }

    public double getLongitudeFromUser(){
        return longitude;
    }

    public double getLatitudeFromUser(){
        return latitude;
    }

    public void setLongitudeForUser(double longitude){
        this.longitude = longitude;
    }

    public void setLatitudeForUser(double latitude){
        this.latitude = latitude;
    }

    public String getFirstNameFromUser(){ return this.firstName; }
}
