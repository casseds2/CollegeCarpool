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

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

import test.collegecarpool.alpha.Activities.NavigationActivity;
import test.collegecarpool.alpha.MapsUtilities.LatLng;
import test.collegecarpool.alpha.MessagingActivities.MessageActivity;
import test.collegecarpool.alpha.R;

public class MessagingService extends FirebaseMessagingService {

    private final String TAG = "MESSAGING SERVICE";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

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
        }
    }

    private void handleMessage(Map<String, String> data, RemoteMessage remoteMessage) {
        String message = data.get("message");
        Log.d(TAG, "Message is: " + message);
        Uri tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("ReceiverID", data.get("senderID"));
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);

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

    private void handleRideRequest(Map<String, String> data, RemoteMessage remoteMessage) {

        double lat = Double.parseDouble(data.get("lat"));
        double lng = Double.parseDouble(data.get("lng"));
        LatLng latLng = new LatLng(lat, lng);
        Log.d(TAG, "LAT/LNG OF REQUEST: " + latLng);
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try{
            List<Address> geoAddress = geocoder.getFromLocation(lat, lng, 1);
            Address address = geoAddress.get(0);
            String street = address.getAddressLine(0);
            street = street + "\n" + address.getAddressLine(1);
            street = street + "\n" + address.getAddressLine(2);
            Log.d(TAG, "ADDRESS: " + street);

            /*Process This LatLng if Accept is Hit*/
            Intent acceptIntent = new Intent(this, NavigationActivity.class);
            acceptIntent.putExtra("latLng", latLng);
            PendingIntent pendingAccept = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), acceptIntent, 0);


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
                    .addAction(R.mipmap.ic_done_black_24dp, "Accept", pendingAccept)
                    .addAction(R.mipmap.ic_clear_black_24dp, "Reject", pendingAccept)
                    .setPriority(Notification.PRIORITY_MAX) //Displays at top of tray so all text can be seen
                    .setContentText(remoteMessage.getNotification().getBody() + "\n" + street);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
        catch(Exception e){
            Log.d(TAG, "UNABLE TO OBTAIN ADDRESS");
        }
    }
}
