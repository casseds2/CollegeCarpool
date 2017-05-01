package test.collegecarpool.alpha.Services.FirebaseMessaging;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import test.collegecarpool.alpha.Firebase.FCMTokenPusher;

public class MessagingTokenRefreshService extends FirebaseInstanceIdService{

    private final String TAG = "TOKEN REFRESH SERVICE";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token is: " + token);
        new FCMTokenPusher().pushFCMToken(token);
    }
}
