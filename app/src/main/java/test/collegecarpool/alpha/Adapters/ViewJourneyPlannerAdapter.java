package test.collegecarpool.alpha.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import test.collegecarpool.alpha.MapsUtilities.Journey;
import test.collegecarpool.alpha.MapsUtilities.Waypoint;
import test.collegecarpool.alpha.R;

public class ViewJourneyPlannerAdapter extends ArrayAdapter<Journey> {

    private ArrayList<Journey> list;
    private final String TAG = "PlannerAdapter";

    public ViewJourneyPlannerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Journey> objects) {
        super(context, resource, objects);
        this.list = objects;
        sortJourneys();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater view = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = view.inflate(R.layout.journey_planner_list, parent, false);
        TextView rowInfo = (TextView) row.findViewById(R.id.journey_planner_text);
        Journey journey = list.get(position);
        String date = journey.getDate().toString();
        ArrayList<Waypoint> journeyWaypoints = journey.getWaypoints();
        String waypoints = "";
        for(int i = 0; i < journeyWaypoints.size() - 1; i++){
            Waypoint waypoint = journey.getWaypoints().get(i);
            waypoints = waypoints + waypoint.getName() + " -> ";
        }
        if(journeyWaypoints.size() > 0)
            waypoints = waypoints + journeyWaypoints.get(journeyWaypoints.size()-1).getName();
        rowInfo.setText(date + ")  " + waypoints);
        Log.d(TAG, "Set Journey");
        return row;
    }

    /*Sort A List of Journeys Based On Their Date in A Bubble Sort Fashion*/
    private void sortJourneys(){
        if (list.size() > 1) {
            for (int i = 0; i < list.size() - 1; i++) {
                Journey j1 = list.get(i);
                Journey j2 = list.get(i+1);
                if (j2.getDate().isBefore(j1.getDate())) {
                    Log.d(TAG, j2.getDate().toString() + " is Before " + j1.getDate().toString());
                    //Collections.swap(list, i, i+1);
                    list.set(i, j2);
                    list.set(i+1, j1);
                    i = -1;
                }
            }
        }
    }
}
