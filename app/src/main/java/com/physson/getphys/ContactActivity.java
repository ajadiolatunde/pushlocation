package com.physson.getphys;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactActivity extends AppCompatActivity {
    private ListView mListView;
    private ProgressDialog pDialog;
    private Handler updateBarHandler;
    List<String> contactList;
    List<Phone>  phoneList;
    Cursor cursor;
    PhoneAdapter adapter;
    Boolean requests=false;
    final int  MY_PERMISSIONS_REQUEST_READ_CONTACTS =1;
    int counter;
    String data ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String message = savedInstanceState.getString("message");
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        PhyLocation p = new Gson().fromJson(i.getStringExtra("push"),PhyLocation.class);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        phoneList = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requests = true;
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "Permission not  granted  ", Toast.LENGTH_LONG).show();
                // Show an expanation to the user *asynchronously* -- don't bloc
            }

        }else {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Reading contacts...");
            pDialog.setCancelable(false);
            pDialog.show();

            mListView = (ListView) findViewById(R.id.listt);
            updateBarHandler =new Handler();
            // Since reading contacts takes more time, let's run it on a separate thread.
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getContacts();
                }
            }).start();
            // Set onclicklistener to the list item.
        }
        if (requests){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("message", "This is my message to be reloaded");
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Toast.makeText(this,"Permission granted now you can read ",Toast.LENGTH_LONG).show();

            }else{

                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void getContacts() {
        contactList = new ArrayList<String>();
        String phoneNumber = null;
        String email = null;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;
        StringBuffer output;
        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(CONTENT_URI, null,null, null, null);
        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {
            counter = 0;
            while (cursor.moveToNext()) {
                int p_count =0;
                Phone p = new Phone();
                output = new StringBuffer();
                // Update the progress message
                updateBarHandler.post(new Runnable() {
                    public void run() {
                        pDialog.setMessage("Reading contacts : "+ counter++ +"/"+cursor.getCount());
                    }
                });
                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
                p.setName(name);
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
                Boolean hasnumber = false;
                if (hasPhoneNumber > 0) {
                    hasnumber =true;
                    output.append("\n First Name:" + name);
                    //This is to read multiple    phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
                     p_count =0;
                    String numbers="";

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER))
                                .replace(" ","")
                                .replace("+","")
                                .replace("-","");
                        if (phoneNumber.length()>8) {
                            p_count += 1;
                            numbers = numbers + ":" + phoneNumber;
                            //output.append("\n Phone number:" + phoneNumber);
                        }
                    }
                    int pc =0;
                    if(p_count>1){

                        String nb="";
                        String[] num = numbers.substring(1).split(":");
                        pc =num.length;
                        HashSet<String> hs = new HashSet<String>();
                        for (String nums:num){
                            if (!hs.add(nums)) {
                                pc--;
                            }
                        }
                        p_count =pc;
                        for (Object ar:hs.toArray()){
                            String dar = (String)ar;
                            nb = nb + ":" + dar;
                        }
                        numbers=nb;
                    }
                    p.setPhones(String.valueOf(p_count)+numbers);
                    p.setPhonecount(p_count);
                    phoneCursor.close();
                }

                if(hasnumber && p_count>0) {

                    phoneList.add(p);
                    //contactList.add(output.toString());
                }
            }

            // ListView has to be updated using a ui thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new PhoneAdapter(getApplicationContext(),phoneList);
                    mListView.setAdapter(adapter);
                }
            });
            // Dismiss the progressbar after 500 millisecondds
            updateBarHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pDialog.cancel();
                }
            }, 500);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
