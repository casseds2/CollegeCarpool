package test.collegecarpool.alpha.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;

import test.collegecarpool.alpha.Tools.GoogleClientBuilder;
import test.collegecarpool.alpha.Tools.Variables;

public class BackgroundLocationIntentService extends IntentService{

    GoogleApiClient googleApiClient;

    private static final String TAG = "LocationIntentService";

    public BackgroundLocationIntentService(String name) {
        super(name);
    }

    public BackgroundLocationIntentService(){
        super("BackgroundLocationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        new GoogleClientBuilder(this, googleApiClient).buildLocationClient();
        Log.d(TAG, "HANDLED THREAD");
    }
}
