package com.physson.getphys;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olatunde on 5/11/2017.
 */

public class Home_fragment2 extends Fragment implements AsynTaskCallback{
    public static final String ARG_PAGE = "ARG_PAGE";
    private static final int ARG_PHOTO_REQUEST =2;
    private int mPageNo;
    private ListView listView;
    private List<Contact> list;
    //private  ImageView addImg;
    FloatingActionButton addImg;
    AsynTaskCallback callback;
    ContactListAdapter adapter;
    private TextView cText;

    private Singleton1 singleton1;
    private Button savebtn;

    public static Home_fragment2 newInstance(int pageNo) {

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        Home_fragment2 fragment = new Home_fragment2();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNo = getArguments().getInt(ARG_PAGE);
        callback = this;
        list = new ArrayList<>();
        new Jsonparse(getContext()).getContactList(list,Constants.CONTACT);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        listView = (ListView)view.findViewById(R.id.listcontact);
        cText = (TextView)view.findViewById(R.id.user_profile_contact);
        cText.setText((list.size()>1)?"You have "+String.valueOf(list.size())+" contacts":"You have "+String.valueOf(list.size())+" contact");

        adapter = new ContactListAdapter(getContext(), list,this);
        listView.setAdapter(adapter);
        addImg =(FloatingActionButton) view.findViewById(R.id.addContactImg);
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog settingsDialog = new Dialog(getContext());
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(LayoutInflater.from(getContext()).inflate(R.layout.contactdialog, null));

                final EditText editTextPhone = (EditText) settingsDialog.findViewById(R.id.numberContactEv);
                Drawable image = getContext().getResources().getDrawable( R.drawable.ic_phone_black_24dp);
                editTextPhone.setCompoundDrawablesWithIntrinsicBounds(image,null,null,null);
                final EditText editTextName=(EditText)settingsDialog.findViewById(R.id.usernameContact);
                Drawable person = getContext().getResources().getDrawable( R.drawable.ic_person_black_24dp);
                editTextName.setCompoundDrawablesWithIntrinsicBounds(person,null,null,null);
                savebtn = (Button)settingsDialog.findViewById(R.id.contactsavebtn);

                savebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Boolean error=false;
                        if (editTextName.getText().toString().equals("") ){
                            editTextName.setError("Required");
                            error =true;
                        }else if(editTextPhone.getText().toString().equals("")){
                            editTextPhone.setError("Required");
                            error = true;
                        }else if(editTextPhone.getText().toString().length()<11){
                            editTextPhone.setError("Invalid");
                            error =true;
                        }else if(new Jsonparse(getContext()).numberExist(editTextPhone.getText().toString())){
                            error = true;
                            Toast.makeText(getContext(),"Number exist!",Toast.LENGTH_SHORT).show();
                        }

                        if (!error){
                            Long tsLong = System.currentTimeMillis();
                            Contact c = new Contact();
                            c.setName(editTextName.getText().toString());
                            c.setPhone(editTextPhone.getText().toString());
                            c.setDate( tsLong.toString());
                            new Jsonparse(getContext()).addContact(c, Constants.CONTACT);
                            Toast.makeText(getContext(),"Saved",Toast.LENGTH_SHORT).show();
                            callback.processFinish("1");

                            settingsDialog.dismiss();
                        }else {


                        }
                    }
                });

                settingsDialog.show();
            }
        });
        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }
    @Override
    public void processFinish(String str) {
        list.clear();

        new Jsonparse(getContext()).getContactList(list,Constants.CONTACT);
        adapter.notifyDataSetChanged();
    }

}