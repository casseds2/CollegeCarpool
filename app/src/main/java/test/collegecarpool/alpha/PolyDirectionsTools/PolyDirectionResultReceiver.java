package test.collegecarpool.alpha.PolyDirectionsTools;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import test.collegecarpool.alpha.Activities.NavigationActivity;

public class PolyDirectionResultReceiver extends ResultReceiver{

    private NavigationActivity navigationActivity;

    public PolyDirectionResultReceiver(Handler handler, NavigationActivity navigationActivity) {
        super(handler);
        this.navigationActivity = navigationActivity;
    }

    /*Call updateUI() on the navigation MaP*/
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        //super.onReceiveResult(resultCode, resultData);
        navigationActivity.updateUI((ArrayList<LatLng>) resultData.getSerializable("JourneyLatLngs"),
                (ArrayList<LatLng>) resultData.getSerializable("PolyLatLngs"),
                resultData.getBoolean("JourneyFinished"),
                resultData.getString("Instruction"),
                resultData.getBoolean("atStartStep"),
                resultData.getBoolean("atEndStep"),
                resultData.getString("Maneuver"),
                resultData.getInt("Distance"),
                resultData.getInt("Duration"));
    }
}
