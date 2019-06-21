package com.physson.getphys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.TextView;

import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.TimeZone;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.client.methods.RequestBuilder;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;


/**
 * Created by olatunde on 5/12/2017.
 */

public class TrackAsyncsegun extends  AsyncTask<Void, Void, String>  {
    private String urlString,number,data;
    private Activity myactivity;
    private Context mContext;
    private StringBuilder stringBuilder;
    private ProgressDialog progressDialog;
    private AsynTaskCallback listener;
    private HttpURLConnection httpURLConnection;
    private Boolean upload;
    private TextView textView;
    private Singleton1 singleton1;

    public TrackAsyncsegun(String number, String urlString, Activity activity, AsynTaskCallback callbackAsy) {
        this.urlString = urlString;
        this.listener = callbackAsy;
        this.myactivity = activity;
        String all = Home.phyIn("ade");
       // this.mContext = context;
        progressDialog = new ProgressDialog(this.myactivity);
        this.number = number;
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
        String ans =processRequest();

        return ans;
    }
    @Override
    protected void onPostExecute(String result) {
         String ans = result;
        progressDialog.dismiss();
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
                .build();
        TimeZone tz = TimeZone.getDefault();
        //String data;

        long ts = System.currentTimeMillis();
        String timestamp = String.valueOf(ts);
        ServiceReq request = new ServiceReq();
        request.setTelephone(singleton1.getPrefKey(Constants.TELEPHONE));
        request.setUiid(singleton1.getPrefKey(Constants.UIID));
        request.setFt(singleton1.getPrefKey(Constants.FIRETOKEN));

        //Reg phone number
        if (number.startsWith("T")) {
            String[] all = number.split("#");
            request.setTelephone(all[1]);

        }
        if (number.startsWith("V")) {
            String[] all = number.split("#");
            request.setTelephone(all[1]);
            request.setVerification(all[2]);
        }


        data = new Gson().toJson(request, ServiceReq.class);

        CloseableHttpResponse response = null;
        try {
            //

            HttpUriRequest auth = RequestBuilder.post()
                    .setUri(new URI(Constants.HTTPURL+urlString + data))
                    .setEntity(new StringEntity(data, StandardCharsets.UTF_8))

                    .addHeader("Authorization", new StringBuilder("CredentialPhysson")
                            .toString())
                    .build();
            response = httpclient.execute(auth);
            HttpEntity entity = response.getEntity();

            //System.out.println(EntityUtils.toString(response.getEntity()));
            String res= EntityUtils.toString(response.getEntity());
            EntityUtils.consumeQuietly(entity);
            return res;
        } catch (Exception ex) {
            ex.printStackTrace();
            //Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            return "Nothing ...";
        }
    }

}
