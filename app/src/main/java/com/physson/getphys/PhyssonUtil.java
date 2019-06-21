package com.physson.getphys;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import cz.msebera.android.httpclient.conn.ssl.NoopHostnameVerifier;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.ssl.SSLContextBuilder;
import cz.msebera.android.httpclient.ssl.SSLContexts;

/**
 * Created by olatunde on 7/19/2017.
 */

public class PhyssonUtil {
    public static String getLocalDateTimeFromStamp(String ts){
        //System.out.println("Tunde time "+ts);
        long timestamp = Long.parseLong(ts); //Example -> in ms
        Date d = new Date(timestamp );
        return d.toLocaleString();
    }

    public void confirmAction(Context mContext){

    }



    public static SSLContext getSSLContext(Context mcontext){
        SSLContext context=null;

        try{
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput= mcontext.getAssets().open("pushlocation_me.crt");

            Certificate ca;

            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
        }catch (IOException | NoSuchAlgorithmException |KeyManagementException |KeyStoreException |CertificateException io){
        //System.out.println("Tunde isnot connected.........");
        io.printStackTrace();
        }

        return context;
    }

    public static boolean connected(Context mcontext){
        boolean isConnected = false;
        try {
            // URL url = new URL("http://79.137.20.205:8888/nsvr/auth/partner/telephone/");



            URL url = new URL("https://pushlocation.me/d");
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            conn.setSSLSocketFactory(getSSLContext(mcontext).getSocketFactory());

            conn.setRequestMethod("GET");
            conn.connect();

            int respCode = conn.getResponseCode();
            if (respCode == HttpURLConnection.HTTP_OK || respCode == HttpURLConnection.HTTP_NOT_FOUND) {
                isConnected = true;
                //System.out.println("Tunde is connected........."+String.valueOf(respCode));
            }else {
                //System.out.println("Tunde is not connected........."+String.valueOf(respCode));

            }
        }catch (IOException io){
            //System.out.println("Tunde isnot connected.........");
            io.printStackTrace();
        }
        return isConnected;

    }

    public static String getMD5EncryptedString(String encTarget){
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            //System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while ( md5.length() < 32 ) {
            md5 = "0"+md5;
        }
        return md5;
    }


    public static int getDistancebtw(Double lat1,Double lon1,Double lat2,Double lon2){
        float[] results = new float[1];
        Location.distanceBetween(lat1,lon1,
                lat2, lon2, results);
        float distanceb = results[0];
        int distance = (int)distanceb;
        return distance;
    }

    public static String getDeviceId(Activity context){
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return telephonyManager.getDeviceId();

            // We do not have this permission. Let's ask the user
        }else {
            return telephonyManager.getDeviceId();

        }
    }

    public static boolean isgpsenabled(final Context context) {
        Boolean res = true;
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            res= false;
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
            dialog.setMessage("Location service disabled");
            dialog.setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
        return res;
    }


}
