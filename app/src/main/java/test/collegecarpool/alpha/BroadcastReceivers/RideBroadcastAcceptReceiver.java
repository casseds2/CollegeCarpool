package test.collegecarpool.alpha.BroadcastReceivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import test.collegecarpool.alpha.Firebase.NotifyUserRideRequestStatus;
import test.collegecarpool.alpha.MapsUtilities.LatLng;

import static android.content.Context.NOTIFICATION_SERVICE;

public class RideBroadcastAcceptReceiver extends BroadcastReceiver{

    private final String TAG = "BROADCAST ACCEPT";

    @Override
    public void onReceive(Context context, Intent intent) {
        LatLng latLng = (LatLng) intent.getSerializableExtra("latLng");
        int notificationID = intent.getIntExtra("notificationID", 0);
        String requestID =  intent.getStringExtra("requestID");
        Log.d(TAG, "LatLng Received in Broadcast: " + latLng.toString());
        sendBroadcastLatLng(context, latLng, requestID);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationID);
    }

    private void sendBroadcastLatLng(Context context, LatLng latLng, String requestID){
        Log.d(TAG, "BEGAN BROADCASTING");
        new NotifyUserRideRequestStatus().notifyUserAccepted(requestID);
        Intent intent = new Intent("ride_request_latLng");
        intent.putExtra("request_latLng", latLng);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        Log.d(TAG, "Broadcast Sent With: " + latLng);
    }
}
