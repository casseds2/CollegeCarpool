package test.collegecarpool.alpha.UserClasses;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

public class UserProfile {

    private String firstName;
    private String secondName;
    private String email;
    private double latitude;
    private double longitude;
    private boolean broadcastLocation;
    private double wallet;

    public UserProfile(){}

    public UserProfile(String firstName, String secondName, String email,double lat, double lon, boolean broadcastLocation, double wallet) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.latitude = lat;
        this.longitude = lon;
        this.broadcastLocation = broadcastLocation;
        this.wallet = wallet;
    }

    @Exclude
    public HashMap<String, Object> toMap(){
        HashMap<String, Object> info = new HashMap<>();
        info.put("firstName", firstName);
        info.put("secondName", secondName);
        info.put("email", email);
        info.put("latitude", latitude);
        info.put("longitude", longitude);
        info.put("broadcastLocation", broadcastLocation);
        info.put("wallet", wallet);
        return info;
    }

    public double getLongitude(){ return this.longitude; }

    public double getLatitude(){ return latitude; }

    public String getFirstName(){ return this.firstName; }

    public String getSecondName(){ return this.secondName; }

    public String getEmail(){
        return this.email;
    }

    public boolean getBroadcastLocation(){
        return this.broadcastLocation;
    }

    public double getWallet() { return this.wallet; }

    public String toString(){
        return "Name: " + firstName + " " + secondName + "\n" + email + "\n" + "LAT/LNG: " + getLatitude() + "/" + getLongitude();
    }
}
