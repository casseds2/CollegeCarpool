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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import test.collegecarpool.alpha.MessagingActivities.Message;
import test.collegecarpool.alpha.R;

public class MessageAdapter extends ArrayAdapter<Message> {

    private ArrayList<Message> list;
    private FirebaseUser user;

    public MessageAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Message> objects) {
        super(context, resource, objects);
        this.list = objects;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Message message = getItem(position);
        Log.d("Adapter: ", message.getMessage());
        //If User 1/2 Inflate Different Text Views
        if(message.getUid().equals(user.getUid())){
            Log.d("Message", "My Message");
            row = inflater.inflate(R.layout.message_list_left, parent, false);
        }
        else{
            Log.d("Message", "Their Message");
            row = inflater.inflate(R.layout.message_list_right, parent, false);
        }
        TextView messageText = (TextView) row.findViewById(R.id.message_text);
        messageText.setText(list.get(position).getMessage());
        Log.d("TextView", (String) messageText.getText());
        return row;
    }
}
