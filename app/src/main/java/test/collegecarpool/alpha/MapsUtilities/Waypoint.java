package test.collegecarpool.alpha.MapsUtilities;

import java.io.Serializable;
import java.util.ArrayList;

public class Waypoint implements Serializable{

    private String name;
    private LatLng latLng;

    public Waypoint(){}

    public Waypoint(String name, LatLng latLng){
        this.name = name;
        this.latLng = latLng;
    }

   com.google.android.gms.maps.model.LatLng toGoogleLatLng(){
        return new com.google.android.gms.maps.model.LatLng(this.latLng.getLat(), this.latLng.getLng());
    }

    boolean isTheSameAs(Waypoint waypoint){
        return this.name.equals(waypoint.getName()) && this.latLng.equals(waypoint.getLatLng());
    }

    public String getName(){
        return this.name;
    }

    public LatLng getLatLng(){
        return this.latLng;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String toString(){
        return this.name + " : " + this.latLng.toString();
    }

}
