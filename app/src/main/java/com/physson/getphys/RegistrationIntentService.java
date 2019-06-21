package com.physson.getphys;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOError;

/**
 * Created by olatunde on 6/7/2017.
 */

public class RegistrationIntentService extends IntentService {
    SharedPreferences mypref;
    Singleton1 singleton1;

    // abbreviated tag name
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Make a call to Instance API
        FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
        String senderId = getResources().getString(R.string.gcm_defaultSenderId);
        try {
            // request token that will be used by the server to send push notifications
            String token = instanceID.getToken();
            Log.d(TAG, "FCM Registration Token:    " + token);
            singleton1 = Singleton1.getInstance(App.getContext());
            singleton1.setmSharedPrefrence();
            if (singleton1.getPrefKey(Constants.OFIRETOKEN).length()<10){
                 //nerver used
                singleton1.addStringSharedPreff(Constants.OFIRETOKEN,token);
                singleton1.addStringSharedPreff(Constants.FIRETOKEN,token);
                singleton1.addStringSharedPreff(Constants.TOKENREFRESH,"Y");
            }else{

                String otoken = singleton1.getPrefKey(Constants.FIRETOKEN);
                if (!otoken.equals(token)){
                    singleton1.addStringSharedPreff(Constants.OFIRETOKEN,otoken);
                    singleton1.addStringSharedPreff(Constants.FIRETOKEN,token);
                    singleton1.addStringSharedPreff(Constants.TOKENREFRESH,"Y");
                }
            }

            //System.out.println("Tunde...token all "+Singleton1.getInstance(getApplicationContext()).mSharedPrefrence.getAll());

            // pass along this data
            //sendRegistrationToServer(token);
        } catch (IOError e) {
            e.printStackTrace();
        }
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }
}
