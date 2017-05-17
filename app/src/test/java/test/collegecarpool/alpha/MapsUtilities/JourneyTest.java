package test.collegecarpool.alpha.MapsUtilities;

import org.junit.Test;

import java.util.ArrayList;

import test.collegecarpool.alpha.UserClasses.Date;

import static org.junit.Assert.*;

public class JourneyTest {

    private Journey journey = new Journey();

    @Test
    public void addWaypoint() throws Exception {
        Waypoint waypoint = new Waypoint("Me", new LatLng(1, 1));
        journey.addWaypoint(waypoint);
        assertEquals(journey.getWaypoints().get(0), waypoint);
    }

    @Test
    public void getDate() throws Exception {
        journey.setDate(new Date(17, 5, 2017));
        assertEquals(true, journey.getDate().isEqualTo(new Date(17, 5, 2017)));
    }

    @Test
    public void setDate() throws Exception {
        journey.setDate(new Date(17, 5, 2017));
        assertEquals(true, journey.getDate().isEqualTo(new Date(17, 5, 2017)));
    }

    @Test
    public void removeWaypoint() throws Exception {
        Waypoint waypoint1 = new Waypoint("Me", new LatLng(1, 1));
        Waypoint waypoint2 = new Waypoint("You", new LatLng(1, 1));
        journey.addWaypoint(waypoint1);
        journey.addWaypoint(waypoint2);
        journey.removeWaypoint(waypoint1);
        assertEquals(true, journey.getWaypoints().get(0).isTheSameAs(waypoint2));
    }

    @Test
    public void getWaypoints() throws Exception {
        ArrayList<Waypoint> waypoints= new ArrayList<>();
        waypoints.add(new Waypoint("A", new LatLng(1,1)));
        waypoints.add(new Waypoint("B", new LatLng(1,2)));
        journey.setWaypoints(waypoints);
        assertEquals(true, journey.getWaypoints().equals(waypoints));
    }

    @Test
    public void setWaypoints() throws Exception {
        ArrayList<Waypoint> waypoints= new ArrayList<>();
        waypoints.add(new Waypoint("A", new LatLng(1,1)));
        waypoints.add(new Waypoint("B", new LatLng(1,2)));
        journey.setWaypoints(waypoints);
        assertEquals(true, journey.getWaypoints().equals(waypoints));
    }

    @Test
    public void isTheSameAs() throws Exception {
        Waypoint waypoint1 = new Waypoint("ME", new LatLng(1, 1));
        Waypoint waypoint2 = new Waypoint("ME", new LatLng(1, 1));
        assertEquals(true, waypoint1.isTheSameAs(waypoint2));
    }

    @Test
    public void isContainedIn() throws Exception {
        ArrayList<Journey> journeys = new ArrayList<>();
        Waypoint waypoint = new Waypoint("ME", new LatLng(1,1));
        journey.addWaypoint(waypoint);
        journeys.add(journey);
        assertEquals(true, journey.isContainedIn(journeys));
    }

}