package test.collegecarpool.alpha.PolyDirectionsTools;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import test.collegecarpool.alpha.Activities.NavigationActivity;

public class NavigationReceiver extends ResultReceiver {

    private NavigationActivity navigationActivity;

    public NavigationReceiver(Handler handler, NavigationActivity navigationActivity) {
        super(handler);
        this.navigationActivity = navigationActivity;
    }

    /*Call updateUI() on the navigation MaP*/
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        navigationActivity.updateUI(
                (ArrayList<LatLng>) resultData.getSerializable("WaypointLatLngs"),
                (ArrayList<LatLng>) resultData.getSerializable("PolyLatLngs"),
                resultData.getBoolean("JourneyFinished"),
                resultData.getBoolean("RemovedCloseWaypoint"),
                resultData.getBoolean("UserAtStartStep"),
                resultData.getBoolean("UserAtEndStep"),
                resultData.getBoolean("PolyLineRecalculated"),
                resultData.getString("Instruction"),
                resultData.getFloat("Bearing"));
    }
}