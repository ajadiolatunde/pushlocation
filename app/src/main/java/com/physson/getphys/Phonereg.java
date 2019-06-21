package com.physson.getphys;

import android.*;
import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olatunde on 5/9/2017.
 */

public class Phonereg extends AppCompatActivity implements AsynTaskCallback,View.OnClickListener {
    private EditText phoneEdit;
    private EditText smsEdit;
    private Button  phoneButton;
    private Button smsButton;
    private TrackAsyncreg trackAsync;
    private AsynTaskCallback ataskCallback;
    private boolean phone_only = true,sms_only = false;
    private Singleton1 singleton1;
    private String phoneNumber,phonelabel;
    private Spinner spinner;
    ProgressBar progressBar;
    private static final int  MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    List<Country> ct;
    TextInputLayout smsWrapper;
    ImageView flagv;
    Boolean first = false;
    String dialcode ;
    Boolean needPerm = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonereg);




        progressBar = (ProgressBar)findViewById(R.id.progressreg);
        progressBar.setVisibility(View.GONE);
        flagv =(ImageView)findViewById(R.id.flagsVv);

        phoneEdit = (EditText)findViewById(R.id.phoneNumber);
        smsWrapper =(TextInputLayout)findViewById(R.id.smsWrapper);
        //phoneButton = (Button)findViewById(R.id.phoneBUtton);
        smsButton = (Button)findViewById(R.id.verifyNumberButton);
        smsButton.setText("Send");
        smsEdit = (EditText)findViewById(R.id.verifyNumberText);
        smsWrapper.setVisibility(View.INVISIBLE);
        singleton1=Singleton1.getInstance(getApplicationContext());
        smsButton.setOnClickListener(this);

        spinner =(Spinner)findViewById(R.id.flagspinner);
        ct = singleton1.getCountyList();
        List<String> list = new ArrayList<String>();
        for (Country country:ct){
            list.add(country.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition("Nigeria"));
        flagv.setImageDrawable(getApplication().getResources().getDrawable(R.mipmap.ng));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Context context =flagv.getContext();
                String items = spinner.getSelectedItem().toString();
                int id = context.getResources().getIdentifier(getDial(items), "mipmap", context.getPackageName());


                flagv.setImageResource(id);

               //Toast.makeText(getBaseContext(),getDial(items)+" "+String.valueOf(id),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplication(), "Permission granted...", Toast.LENGTH_LONG).show();
                    needPerm = false;
                    // Add your function here which open camera
                } else {
                    Toast.makeText(getApplication(), "Permission required", Toast.LENGTH_LONG).show();
                } return; }
        }
    }


    @Override
    public void onClick(View view) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // only for gingerbread and newer versions


            if   (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                needPerm =true;
            }else  if   (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                needPerm = true;
            }else  if   (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                needPerm = true;
            } else if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
                needPerm = true;
            }


            if (needPerm) {
                ActivityCompat.requestPermissions(Phonereg.this, new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS,Manifest.permission.ACCESS_FINE_LOCATION ,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {


                if (phone_only) {
                    String number = phoneEdit.getText().toString();
                    if (number.length() >= 10 && number.length()<=12 && !number.startsWith("+")) {
                        String url = Constants.AUTH_CLIENT_TELEPHONE;
                        phoneNumber =  (number.startsWith("0"))? dialcode+number.substring(1):dialcode+number;
                        phonelabel = number;
                        //Toast.makeText(getBaseContext(),phoneNumber,Toast.LENGTH_SHORT).show();
                        trackAsync = new TrackAsyncreg("T#"+phoneNumber, url, Phonereg.this, progressBar,Phonereg.this);
                        trackAsync.execute();
                        first =true;

                    }else {
                        Toast.makeText(getBaseContext(),"Number not valid",Toast.LENGTH_SHORT).show();
                    }
                }
                if (sms_only) {
                    String smsCode = smsEdit.getText().toString();
                    if (smsCode.length() == 6) {
                        trackAsync = new TrackAsyncreg("V#"+phoneNumber+"#"+smsCode, Constants.VERIFY_CLIENT, Phonereg.this, progressBar,Phonereg.this);
                        trackAsync.execute();
                    } else {
                        Toast.makeText(getBaseContext(), "Invalid code", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            // Add your function here which open camera
        }


    }
    @Override
    public void processFinish(String str){
        //code 1 suc.code 2 ,exist,
        String code = new  Jsonparse(Phonereg.this).getCode(str);


        //Toast.makeText(getBaseContext(),code , Toast.LENGTH_SHORT).show();
        Intent i  ;
        switch (code) {
            //Already verified
            case "16":
                Toast.makeText(getBaseContext(), "Already verified", Toast.LENGTH_SHORT).show();
                i= new Intent(this,Home.class);
                singleton1.addStringSharedPreff("stage","1");
                singleton1.addStringSharedPreff(Constants.TELEPHONE,phoneNumber);
                singleton1.addStringSharedPreff(Constants.TELEPHONELABEL,phonelabel);
                startActivity(i);
                break;

            case "1":
                //Verified
                //    Sms sent number correct and verified
                String id = new Jsonparse(Phonereg.this).getData(str);
                singleton1.addStringSharedPreff(Constants.UIID,id);
                Toast.makeText(getBaseContext(), "Succesful", Toast.LENGTH_SHORT).show();
                singleton1.addStringSharedPreff("stage","1");
                singleton1.addStringSharedPreff(Constants.TELEPHONE,phoneNumber);
                singleton1.addStringSharedPreff(Constants.TELEPHONELABEL,phonelabel);
                singleton1.addStringSharedPreff(Constants.DIAL_CODE,dialcode);
                i= new Intent(this,Home.class);
                startActivity(i);
                break;
            case "15":
                //Validation code sent
                Toast.makeText(getBaseContext(), "Validation code sent", Toast.LENGTH_SHORT).show();
                phone_only = false;
                sms_only = true;
                smsWrapper.setVisibility(View.VISIBLE);
                smsButton.setText("Verify");
                break;
            case "i":
                Toast.makeText(getBaseContext(), "Invalid code try again", Toast.LENGTH_SHORT).show();
                break;

        }
    }
    private String getDial(String name){
        String res =null;
        for (Country c:ct){
            if (c.getName().equals(name)){
                res =(c.getCode().toLowerCase().equals("do"))? "doo":c.getCode().toLowerCase();
                dialcode=c.getDial_code().replace(" ","");
            }
        }
        return res;
    }

}
