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

public class MessageAdapter extends ArrayAdapter<String> {

    private ArrayList<String> list;

    public MessageAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
        this.list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater view = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = view.inflate(R.layout.message_list, parent, false);
        TextView rowInfo = (TextView) row.findViewById(R.id.message_text);
        rowInfo.setText(list.get(position));
        return row;
    }
}
