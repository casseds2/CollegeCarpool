package test.collegecarpool.alpha.BroadcastReceivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import test.collegecarpool.alpha.Firebase.NotifyUserRideRequestStatus;

import static android.content.Context.NOTIFICATION_SERVICE;

public class RideBroadcastRejectReceiver extends BroadcastReceiver{

    private final String TAG = "BROADCAST REJECT";

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationID = intent.getIntExtra("notificationID", 0);
        String requestID = intent.getStringExtra("requestID");
        Log.d(TAG, "RECEIVED REJECT ID: " + requestID);
        notifyUserWasRejected(requestID);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationID);
    }

    public void notifyUserWasRejected(String requestID){
        Log.d(TAG, "Notifying User of Rejection");
        new NotifyUserRideRequestStatus().notifyUserRejected(requestID);
    }
}
