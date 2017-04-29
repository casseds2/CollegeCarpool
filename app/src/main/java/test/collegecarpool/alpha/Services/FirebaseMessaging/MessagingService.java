package test.collegecarpool.alpha.Services.FirebaseMessaging;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    private final String TAG = "MESSAGING SERVICE";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String image = remoteMessage.getNotification().getIcon();
        String text = remoteMessage.getNotification().getBody();
        String title = remoteMessage.getNotification().getTitle();
        Log.d(TAG, "Message Received");
        this.sendNotification(new Notification(title, image, text));
    }

    /*Send Custom Notification To Device On Message Received*/
    private void sendNotification(Notification notification){

    }
}
