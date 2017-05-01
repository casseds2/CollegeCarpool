package test.collegecarpool.alpha.Services.FirebaseMessaging;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import test.collegecarpool.alpha.R;

public class MessagingService extends FirebaseMessagingService {

    private final String TAG = "MESSAGING SERVICE";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody());
        //Map<String, String> data = remoteMessage.getData();
        //String senderID = data.get("senderID");
        //Log.d(TAG, "SENDER IS: " + senderID);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
