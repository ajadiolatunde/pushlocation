package com.physson.getphys;

/**
 * Created by olatunde on 6/7/2017.
 */

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.messaging.RemoteMessage.Notification;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

//import android.app.Notification;
//https://stackoverflow.com/questions/20594936/communication-between-activity-and-service
public class FCMMessageHandler extends FirebaseMessagingService {
    private LocalBroadcastManager broadcaster;
    public static final int MESSAGE_NOTIFICATION_ID = 435345;
    Singleton1 singleton1 =Singleton1.getInstance(App.getContext());
    NotificationCompat.Builder mBuilder;
    String from;


    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(App.getContext());
        //Bundle extras = getintent().getExtras();

    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        from = remoteMessage.getFrom();


        Notification notification = remoteMessage.getNotification();
//        System.out.println("Tunde notice ............. "+notification.getBody());
//        System.out.println("Tunde data ............. "+data.get("body"));

        createNotification(notification,data.get("body"));



        //issue
        try {
            PhyLocation p = new Gson().fromJson(data.get("body"), PhyLocation.class);
            Long ts = System.currentTimeMillis();
            p.setTimerecieved(ts.toString());
            new Jsonparse(getApplicationContext()).addLocation(p, Constants.NOTIFICATION);
            Intent intent = new Intent(Constants.NOTIFICATION);
            intent.putExtra("body", data.get("body"));
            broadcaster.sendBroadcast(intent);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    // Creates notification based on title and body received
    //http://techqa.info/programming/question/37718412/firebase-cloud-messaging,-issues-in-receiving-notification
    private void createNotification(Notification notification,String dataBody) {
        Context context =this;
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("not",notification.getBody());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //System.out.println("Tunde body  "+dataBody);
        PhyLocation phyLocation = new Gson().fromJson(dataBody,PhyLocation.class);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_directions_black_24dp).setContentTitle(notification.getTitle())
                .setSound(defaultSoundUri)
                .setContentText("Notice from "+phyLocation.getSender())
                //.setExtras()
                .setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        Intent intent1 = new Intent(App.getContext(), Home.class);
        Bundle extras = intent.getExtras();
        String body = extras.getString("body");

        PhyLocation p = new Gson().fromJson(body, PhyLocation.class);
        Long ts = System.currentTimeMillis();
        p.setTimerecieved(ts.toString());
        new Jsonparse(getApplicationContext()).addLocation(p, Constants.NOTIFICATION);

        for (String key: extras.keySet())
        {
            //System.out.println ("myApplication"+key + " is a key in  handle the bundle");
        }

       // System.out.println("Tunde loghandleintent ..........."+body+ " "+extras.keySet());



        intent1.putExtra("not",body);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);


    }


}
