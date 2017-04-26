package test.collegecarpool.alpha.Adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import test.collegecarpool.alpha.R;

public class PlanJourneyAdapter extends ArrayAdapter<String>{

    private ArrayList<String> list;

    public PlanJourneyAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
        this.list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater view = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = view.inflate(R.layout.plan_journey_list, parent, false);
        TextView rowInfo = (TextView) row.findViewById(R.id.plan_journey_text);
        int rowPosition = position + 1; //Index starts at 0
        rowInfo.setText("Stop " + rowPosition + ")  " + list.get(position));
        //rowPosition = rowPosition + 1;
        return row;
    }
}
