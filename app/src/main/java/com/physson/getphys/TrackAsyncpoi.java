package com.physson.getphys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.TimeZone;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.client.methods.RequestBuilder;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;


/**
 * Created by olatunde on 5/12/2017.
 */

public class TrackAsyncpoi extends  AsyncTask<Void, Void, String>  {
    private String urlString,number,data;
    private Activity myactivity;
    private Context mContext;
    private StringBuilder stringBuilder;
    private ProgressDialog progressDialog;
    private AsynTaskCallback listener;
    private HttpURLConnection httpURLConnection;
    private Boolean upload;
    String latlon;
    private TextView textView;
    private Singleton1 singleton1;

    public TrackAsyncpoi(String latlon, Activity activity, AsynTaskCallback callbackAsy) {

        this.listener = callbackAsy;
        this.myactivity = activity;
        this.latlon = latlon;
        String all = Home.phyIn("ade");
       // this.mContext = context;
        progressDialog = new ProgressDialog(this.myactivity);
        singleton1 = Singleton1.getInstance(activity);

        //this.textView = tv;

    }
    @Override
    protected void onPreExecute() {
        progressDialog.show();
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(Void... params) {
        String ans = "";
        try {
            String link ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+latlon+"&radius=100&key=AIzaSyDBLZr1VpaILjKMnFUb6W13m7kfc9tsPN8";

            ans =downloadUrl(link);

        } catch (Exception e) {
        Log.d("Background Task", e.toString());
         }

        return ans;
    }
    @Override
    protected void onPostExecute(String result) {
         String ans = result;
        progressDialog.dismiss();
        this.listener.processFinish(ans);
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}
