package test.collegecarpool.alpha.MapsUtilities;

import com.google.android.gms.maps.model.LatLng;

public class DirectionStep {

    private int distance;
    private LatLng start, end;
    private int duration;
    private String htmlInstruction;
    private String maneuver;

    public DirectionStep(int distance, int duration, LatLng start, LatLng end, String maneuver, String htmlInstruction){
        this.distance = distance;
        this.duration = duration;
        this.start = start;
        this.end = end;
        this.maneuver = maneuver;
        this.htmlInstruction = htmlInstruction;
    }

    public String toString(){
        return "\n" + "Distance: " +  distance + "\n" + "Duration: " + duration + "\n"
                + "Start: " + start.toString() + "\n" + "End: " + end.toString() + "\n" + "Maneuver: " + maneuver + "\n"
                + "Html: " + htmlInstruction;
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
