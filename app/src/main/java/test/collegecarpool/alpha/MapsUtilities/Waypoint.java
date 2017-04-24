package test.collegecarpool.alpha.MapsUtilities;

import java.io.Serializable;

public class Waypoint implements Serializable{

    private String name;
    private LatLng latLng;

    public Waypoint(){}

    public Waypoint(String name, LatLng latLng){
        this.name = name;
        this.latLng = latLng;
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
