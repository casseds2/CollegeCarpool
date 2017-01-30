package test.collegecarpool.alpha.UserLocations;

/**
 * Created by casseds95 on 25/01/2017.
 */

public class UserLocation {

    private double latitude;
    private double longitude;

    public UserLocation(){}

    public UserLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude= longitude;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public void setLatitude(double latitude) {this.latitude = latitude;}

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setCoordinates(double latitude, double longitude){
        this.latitude= latitude;
        this.longitude = longitude;
    }
}
