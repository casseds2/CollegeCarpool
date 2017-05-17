package test.collegecarpool.alpha.Services.FirebaseMessaging;

import android.app.*;
import android.app.Notification;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

import test.collegecarpool.alpha.BroadcastReceivers.RideBroadcastRejectReceiver;
import test.collegecarpool.alpha.MapsUtilities.LatLng;
import test.collegecarpool.alpha.MessagingActivities.MessageActivity;
import test.collegecarpool.alpha.R;
import test.collegecarpool.alpha.BroadcastReceivers.RideBroadcastAcceptReceiver;

public class MessagingService extends FirebaseMessagingService {

    private final String TAG = "MESSAGING SERVICE";
    private FirebaseUser user;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        /*Handle Data Data*/
        if(remoteMessage.getData() != null){
            Map<String, String> data = remoteMessage.getData();
            if(data.get("type").equals("message")){
                Log.d(TAG, "TYPE: MESSAGE");
                handleMessage(data, remoteMessage);
            }
            if(data.get("type").equals("rideRequest")) {
                Log.d(TAG, "TYPE: rideREQUEST");
                handleRideRequest(data, remoteMessage);
            }
            if(data.get("type").equals("requestResponse")){
                Log.d(TAG, "TYPE: requestResponse");
                handleRequestResponse(data, remoteMessage);
            }
            if(data.get("type").equals("friendRequest")){
                Log.d(TAG, "TYPE: friendRequest");
                handleFriendRequest(data, remoteMessage);
            }
        }
    }

    private void handleFriendRequest(Map<String, String> data, RemoteMessage remoteMessage){
        String userName = data.get("username");
        Uri tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_person_outline_white_24dp)
                .setColor(Color.BLACK)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(userName + " sent you a friend request."))
                .setSound(tone)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    private void handleRequestResponse(Map<String, String> data, RemoteMessage remoteMessage) {
        String response = data.get("status");
        Log.d(TAG, "Response Was " + response);
        String message = "Your Ride Request Was " + response;
        Uri tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_directions_car_black_24dp)
                .setColor(Color.BLACK)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setSound(tone)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    /*Handle If A Message Was Received*/
    private void handleMessage(Map<String, String> data, RemoteMessage remoteMessage) {
        if(!data.get("senderID").equals(user.getUid())) {
            String message = data.get("message");
            Log.d(TAG, "Message is: " + message);
            Uri tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Intent intent = new Intent(this, MessageActivity.class);
            intent.putExtra("ReceiverID", data.get("senderID"));
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);

        /*Build Message Notification*/
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_mail_white_24dp)
                    .setColor(Color.BLACK)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(remoteMessage.getNotification().getBody() + message))
                    .setSound(tone)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_menu_send, "Respond", pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());
        }
    }

    /*Handle If A Ride Request Was Received*/
    private void handleRideRequest(Map<String, String> data, RemoteMessage remoteMessage) {

        /*Generate Unique IF For Service So We Can Clear It Later*/
        int notificationID = (int) System.currentTimeMillis();

        double lat = Double.parseDouble(data.get("lat"));
        double lng = Double.parseDouble(data.get("lng"));
        LatLng latLng = new LatLng(lat, lng);
        Log.d(TAG, "LAT/LNG OF REQUEST: " + latLng);
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try{
            /*Get the Street Address of the Request*/
            List<Address> geoAddress = geocoder.getFromLocation(lat, lng, 1);
            Address address = geoAddress.get(0);
            String street = address.getAddressLine(0);
            street = street + "\n" + address.getAddressLine(1);
            street = street + "\n" + address.getAddressLine(2);
            Log.d(TAG, "ADDRESS: " + street);


            /*Process This LatLng if Accept is Hit*/
            Intent acceptIntent = new Intent(getApplicationContext(), RideBroadcastAcceptReceiver.class);
            acceptIntent.putExtra("latLng", latLng);
            acceptIntent.putExtra("requestID", data.get("user"));
            acceptIntent.putExtra("notificationID", notificationID);
            PendingIntent pendingAccept = PendingIntent.getBroadcast(getApplicationContext(), 0, acceptIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            /*Alert Requester if Reject Hit*/
            Intent rejectIntent = new Intent(getApplicationContext(), RideBroadcastRejectReceiver.class);
            rejectIntent.putExtra("requestID", data.get("user"));
            rejectIntent.putExtra("notificationID", notificationID);
            PendingIntent pendingReject = PendingIntent.getBroadcast(getApplicationContext(), 0, rejectIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            /*Build Notification*/
            Uri tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_directions_car_black_24dp)
                    .setColor(Color.BLACK)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(remoteMessage.getNotification().getBody() + street))
                    .setSound(tone)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .addAction(R.mipmap.ic_done_black_24dp, "Accept", pendingAccept)
                    .addAction(R.mipmap.ic_clear_black_24dp, "Reject", pendingReject)
                    .setPriority(Notification.PRIORITY_MAX) //Displays at top of tray so all text can be seen
                    .setContentText(remoteMessage.getNotification().getBody() + "\n" + street + "\n" + data.get("time"));
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(notificationID, builder.build());
        }
        catch(Exception e){
            Log.d(TAG, "UNABLE TO OBTAIN ADDRESS");
        }
    }
}
