package test.collegecarpool.alpha.Services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by casseds95 on 06/02/2017.
 */

public class MessagingService extends FirebaseMessagingService {

    final static String TAG = "Messaging Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.d(TAG, "From" + remoteMessage.getFrom());

        if(remoteMessage.getData().size() > 0){
            Log.d(TAG, "Message data: " + remoteMessage.getData());
        }

        if(remoteMessage.getNotification() != null){
            Log.d(TAG, "Notification: " + remoteMessage.getNotification().getBody());
        }
    }
}
