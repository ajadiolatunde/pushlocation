package com.physson.getphys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.client.methods.RequestBuilder;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;


/**
 * Created by olatunde on 5/12/2017.
 */

public class TrackAsyncphoto extends  AsyncTask<Void, Void, String>  {
    private String urlString,number,data;
    private Activity myactivity;
    private StringBuilder stringBuilder;
    private ProgressDialog progressDialog;
    private AsynTaskCallback listener;
    private HttpURLConnection httpURLConnection;
    private Boolean upload;
    private String type;
    private TextView textView;

    public TrackAsyncphoto(Boolean upload ,String type,String number, String urlString, Activity activity, AsynTaskCallback callbackAsy) {
        this.urlString = urlString;
        this.listener = callbackAsy;
        this.myactivity = activity;
        this.upload =upload;
        //Types are P,D,T
        this.type = type;
        progressDialog = new ProgressDialog(this.myactivity);
        this.number = number;

        //this.textView = tv;

    }
    @Override
    protected void onPreExecute() {
        this.progressDialog.show();
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(Void... params) {
        String ans =processRequest();
        return ans;
    }
    @Override
    protected void onPostExecute(String result) {
         String ans = result;
        this.progressDialog.dismiss();
        this.listener.processFinish(ans);
    }


    private String processRequest(){
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                PhyssonUtil.getSSLContext(myactivity),
                new String[] { "TLSv1" },
                null,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); //TODO

        CloseableHttpClient httpclient = HttpClients.custom()
                .setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER) //TODO
                .setSSLSocketFactory(sslsf)
                .build();        TimeZone tz = TimeZone.getDefault();
        //String data;

        long ts = System.currentTimeMillis();
        String timestamp = String.valueOf(ts);

        PhyLocation phyLocation = new Gson().fromJson(number,PhyLocation.class);
        data = Base64.encodeToString(number.getBytes(), Base64.NO_WRAP).trim();
        CloseableHttpResponse response = null;
        try {

            byte[] dataBytes = data.getBytes("UTF-8");
            if (upload) {
                //String token =Singleton1.getInstance(myactivity).getPrefKey(Constants.FIRETOKEN);
                String filePath = Singleton1.getInstance(myactivity).getPhotoFile(phyLocation.getId()).getPath() + ".jpg";
                File picFIle = new File(filePath);
                //System.out.ln("Tunde tracphoto size  " + String.valueOf(picFIle.length()));
                HttpUriRequest auth = RequestBuilder.post()
                        .setUri(new URI(Constants.HTTPURL + urlString +"d"))
                        .setEntity(MultipartEntityBuilder.create()
                                .addPart("photo", new FileBody(picFIle))
                                .addTextBody("type",type)
                                .addTextBody("name",data)
                                .addTextBody("code",Home.phyIn(phyLocation.getTimesent()))
                                .build())
                        .addHeader("Authorization", new StringBuilder("CredentialPhysson")

                                .toString())
                        .build();

                response = httpclient.execute(auth);
            }else {
                HttpUriRequest auth = RequestBuilder.post()
                        .setUri(new URI(Constants.HTTPURL + urlString +"d"))
                        .setEntity(MultipartEntityBuilder.create()
                                .addTextBody("name",data)
                                .addTextBody("type",type)
                                .addTextBody("code",Home.phyIn(phyLocation.getTimesent()))
                                .build())
                        .addHeader("Authorization", new StringBuilder("CredentialPhysson")

                                .toString())
                        .build();

                response = httpclient.execute(auth);
            }
            HttpEntity entity = response.getEntity();


            //System.out.println(EntityUtils.toString(response.getEntity()));
            String res= EntityUtils.toString(response.getEntity());
            EntityUtils.consumeQuietly(entity);
            return res;
        } catch (Exception ex) {
            ex.printStackTrace();
            //Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            return "Nothing ...";
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    //Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }

}
