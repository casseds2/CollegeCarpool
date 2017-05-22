package test.collegecarpool.alpha.BroadcastReceivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MessageReceiver extends BroadcastReceiver {

    private final String TAG = "MESSAGE RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "MessageReceiver");
        int notificationID = intent.getIntExtra("notificationID", 0);
        String receiverID = intent.getStringExtra("ReceiverID");
        Intent intent1 = new Intent("message_received");
        intent1.putExtra("ReceiverID", receiverID);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationID);
        Log.d(TAG, "Message Sent");
    }
}
