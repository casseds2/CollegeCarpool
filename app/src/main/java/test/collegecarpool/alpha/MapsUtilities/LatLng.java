package test.collegecarpool.alpha.MapsUtilities;

import java.io.Serializable;

public class LatLng implements Serializable{

    private double lat, lng;

    public LatLng(){}

    public LatLng(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    boolean equals(LatLng latLng){
        return this.lat == latLng.getLat() && this.lng == latLng.getLng();
    }

    com.google.android.gms.maps.model.LatLng toGoogleLatLng(){
        return new com.google.android.gms.maps.model.LatLng(this.getLat(), this.getLng());
    }

    @Override
    public String toString() {
        return "LatLng{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
