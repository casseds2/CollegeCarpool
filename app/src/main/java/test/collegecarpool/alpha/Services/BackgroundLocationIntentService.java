package test.collegecarpool.alpha.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import test.collegecarpool.alpha.Tools.GoogleClientBuilder;

public class BackgroundLocationIntentService extends IntentService{

    GoogleApiClient googleApiClient;

    public static volatile boolean stopThread = false;
    public static volatile boolean pauseThread = false;

    private static final String TAG = "LocationIntentService";

    public BackgroundLocationIntentService(String name) {
        super(name);
    }

    public BackgroundLocationIntentService(){
        super("BackgroundLocationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GoogleClientBuilder googleClientBuilder = new GoogleClientBuilder(this, googleApiClient);
        if(googleClientBuilder.checkGooglePlayServicesAvailable()) {
            googleClientBuilder.buildGoogleClient();
        }
        Log.d(TAG, "HANDLED THREAD");
    }
}
