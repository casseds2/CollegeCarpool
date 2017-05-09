package test.collegecarpool.alpha.PolyDirectionsTools;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import test.collegecarpool.alpha.Activities.Nav2;

public class NavTwoReceiver extends ResultReceiver {

    private Nav2 nav2;

    public NavTwoReceiver(Handler handler, Nav2 nav2) {
        super(handler);
        this.nav2 = nav2;
    }

    /*Call updateUI() on the navigation MaP*/
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        nav2.updateUI(
                (ArrayList<LatLng>) resultData.getSerializable("WaypointLatLngs"),
                (ArrayList<LatLng>) resultData.getSerializable("PolyLatLngs"),
                resultData.getBoolean("JourneyFinished"),
                resultData.getBoolean("RemovedCloseWaypoint"),
                resultData.getBoolean("UserAtStartStep"),
                resultData.getBoolean("UserAtEndStep"),
                resultData.getString("Instruction"),
                resultData.getString("Maneuver"),
                resultData.getFloat("Bearing"));
    }
}
