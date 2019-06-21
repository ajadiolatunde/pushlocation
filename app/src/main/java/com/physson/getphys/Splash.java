package com.physson.getphys;

/**
 * Created by olatunde on 10/30/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOError;

/**
 * Created by olatunde on 10/30/2016.
 */



public class Splash extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private Boolean mCOnnectionStatus = Boolean.FALSE;
    private Boolean mFirstTimeLogin = Boolean.FALSE;
    private SharedPreferences mSharedPrefrence;
    private int stageStatus;
    ImageView imageView;
    private Singleton1 singleton1;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "RegIntentService";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.splash);
//        imageView = (ImageView) findViewById(R.id.splashimg);
//        imageView.setImageResource(R.mipmap.ic_launcher);
        singleton1 = Singleton1.getInstance(App.getContext());

        //Create shared file

        singleton1.setmSharedPrefrence();

        if (checkCOnnection()) {
            if(checkPlayServices()) {
                Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
                startService(intent);
            }
           new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i;
                // This method will be executed once the timer is ov
                int stages = getStage();
                switch (stages){
                    case 0:
                        singleton1.addStringSharedPreff("stage","0");
                        i = new Intent(Splash.this, Phonereg.class);
                        startActivity(i);
                        break;
                    case 1:

                        i = new Intent(Splash.this, Home.class);
                        startActivity(i);
                        break;

                }
                // close this activity
                finish();
            }
           }, SPLASH_TIME_OUT);
        }else {
            Toast.makeText(getBaseContext(),"No network connection",Toast.LENGTH_SHORT).show();
        }
    }
    private  boolean checkCOnnection(){
        //Assume connected
       Runnable runnable = new Runnable() {
           @Override
           public void run() {
               mCOnnectionStatus = PhyssonUtil.connected(getApplication());
           }
       };
        new Thread(runnable).start();

        SystemClock.sleep(5000);
        return mCOnnectionStatus;
    }

    private int getStage(){
        String st = singleton1.getPrefKey("stage");
        stageStatus = Integer.parseInt((st).equals("")? "0":st);
        return  stageStatus;
    }
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        singleton1.setmSharedPrefrence();
        // put your code here...
        if (checkCOnnection()) {
            if(checkPlayServices()) {
                Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
                startService(intent);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent i;
                    // This method will be executed once the timer is ov
                    int stages = getStage();
                    switch (stages){
                        case 0:
                            singleton1.addStringSharedPreff("stage","0");
                            i = new Intent(Splash.this, Phonereg.class);
                            startActivity(i);
                            break;
                        case 1:

                            i = new Intent(Splash.this, Home.class);
                            startActivity(i);
                            break;

                    }
                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }else {
            Toast.makeText(getBaseContext(),"No network connection",Toast.LENGTH_SHORT).show();
        }

    }



}

