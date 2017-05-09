package test.collegecarpool.alpha.MapsUtilities;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

public class DirectionStep implements Serializable{

    private int distance;
    private LatLng start, end;
    private int duration;
    private String htmlInstruction;
    private String maneuver;
    private ArrayList<LatLng> polyStep;

    public DirectionStep(){}

    public DirectionStep(int distance, int duration, LatLng start, LatLng end, String maneuver, String htmlInstruction,ArrayList<LatLng> polyStep){
        this.distance = distance;
        this.duration = duration;
        this.start = start;
        this.end = end;
        this.maneuver = maneuver;
        this.htmlInstruction = htmlInstruction;
        this.polyStep = polyStep;
    }

    public String toString(){
        return "\n" + "Distance: " +  distance + "\n" + "Duration: " + duration + "\n"
                + "Start: " + start.toString() + "\n" + "End: " + end.toString() + "\n" + "Maneuver: " + maneuver + "\n"
                + "Html: " + htmlInstruction + "\n"
                + "Encoded Poly: " + polyStep.toString();
    }

    public ArrayList<LatLng> getPolyStep() {
        return polyStep;
    }

    public void setPolyStep(ArrayList<LatLng> polyStep) {
        this.polyStep = polyStep;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public LatLng getStart() {
        return start;
    }

    public void setStart(LatLng start) {
        this.start = start;
    }

    public LatLng getEnd() {
        return end;
    }

    public void setEnd(LatLng end) {
        this.end = end;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getHtmlInstruction() {
        return htmlInstruction;
    }

    public void setHtmlInstruction(String htmlInstruction) {
        this.htmlInstruction = htmlInstruction;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }
}
