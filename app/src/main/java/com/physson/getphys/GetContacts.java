package com.physson.getphys;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olatunde on 7/28/2017.
 */

public class GetContacts extends AppCompatActivity implements AsynTaskCallback{
    private ListView listView;
    private List<Contact> list;
    AsynTaskCallback callback;
    GetContactListAdapter adapter;
    PhyLocation phyLocation;
    String json,type ;
    Boolean upload;
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        json =i.getStringExtra("push");
        type =i.getStringExtra("type");
        upload = i.getBooleanExtra("upload",false);

        phyLocation = new Gson().fromJson(json,PhyLocation.class);
        setContentView(R.layout.getcontactfragment_2);
        callback = this;
        list = new ArrayList<>();
        search=(EditText)findViewById(R.id.searchEv);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = search.getText().toString().toLowerCase();

                adapter.filter(text);
            }
        });
        new Jsonparse(getApplicationContext()).getContactList(list,Constants.CONTACT);

        listView = (ListView)findViewById(R.id.listcontact);
        adapter = new GetContactListAdapter(getApplicationContext(), list,this);
        listView.setAdapter(adapter);
    }
    @Override
    public void processFinish(String str){
        //System.out.println("Tunde ...process . json.."+json);
        phyLocation.setReciever(str);
        phyLocation.setFt(Singleton1.getInstance(getApplicationContext()).getPrefKey(Constants.FIRETOKEN));
        phyLocation.setSender(Singleton1.getInstance(getApplicationContext()).getPrefKey(Constants.TELEPHONE));
        Long ts = System.currentTimeMillis();

        //Time sent
        phyLocation.setTimesent(ts.toString());
        String dat = new Gson().toJson(phyLocation,PhyLocation.class);
        //System.out.println("Tunde ...process dat ..."+dat);
        new TrackAsyncphoto(upload,type,dat, Constants.SAVE_PHOTO, this, new AsynTaskCallback() {
            @Override
            public void processFinish(String str) {
                String res = str;
                //System.out.print("Tunde akin .."+res);
                String code  = new Jsonparse(getApplicationContext()).getCode(res);
                String mes = new  Jsonparse(getApplicationContext()).getMessage(res);

                if (code.equals("1")) {
                    Toast.makeText(getApplicationContext(),mes,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(GetContacts.this, Home.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),mes,Toast.LENGTH_SHORT).show();
                }
            }
        }).execute();
    }
}
