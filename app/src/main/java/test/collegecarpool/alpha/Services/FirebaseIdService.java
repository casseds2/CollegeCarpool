package test.collegecarpool.alpha.Services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by casseds95 on 06/02/2017.
 */

public class FirebaseIdService extends FirebaseInstanceIdService {

    public String getToken(){
        return FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public void onTokenRefresh(){
        String refreshToken = getToken();
        Log.e("Id Service", "Refreshed Token: " + refreshToken);
        //sendTokenToServer(refreshToken)
        //Unimplemented Method?
    }
}
