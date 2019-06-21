package com.physson.getphys;

import android.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by olatunde on 5/11/2017.
 */

public class Home_fragment1 extends Fragment  {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPageNo;
    private TextView nText,sText;
    private ListView listViewv;
    private List<PhyLocation> phyLocationList;
    private FloatingActionButton fab;
    private AdView mAdView;
    LocationListAdapter adapter;
    Singleton1 singleton1;
    ArrayAdapter arrayAdapter;
    private static final int  MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    Boolean needPerm = false;


    public static Home_fragment1 newInstance(int pageNo) {

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        Home_fragment1 fragment = new Home_fragment1();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNo = getArguments().getInt(ARG_PAGE);
        singleton1 = Singleton1.getInstance(getContext());
        phyLocationList = new ArrayList<>();
        new Jsonparse(getContext()).getLocationList(phyLocationList,Constants.LOCATION);
        if (singleton1.getPrefKey(Constants.TOKENREFRESH).equals("Y")){
            PhyLocation phyLocation = new PhyLocation();

            Long ts = System.currentTimeMillis();
            phyLocation.setTimesent(ts.toString());
            phyLocation.setSender(singleton1.getPrefKey(Constants.TELEPHONE));
            phyLocation.setNt(singleton1.getPrefKey(Constants.FIRETOKEN));
            phyLocation.setOt(singleton1.getPrefKey(Constants.OFIRETOKEN));
            String numb = new Gson().toJson(phyLocation,PhyLocation.class);
            new TrackAsyncphoto(false, "T",numb, Constants.SAVE_PHOTO, getActivity(), new AsynTaskCallback() {
                @Override
                public void processFinish(String str) {
                    String mess = new Jsonparse(getContext()).getMessage(str);
                    String code = new Jsonparse(getContext()).getCode(str);
                    if (code.equals("1")){
                        Singleton1.getInstance(getContext()).addStringSharedPreff(Constants.TOKENREFRESH,"N");

                    }
                    //System.out.println("Tunde  ....token update  .."+str);
                    Toast.makeText(getContext(),mess,Toast.LENGTH_SHORT).show();

                }
            }).execute();
        }


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1, container, false);
        listViewv = (ListView)view.findViewById(R.id.listlocation);
        nText = (TextView)view.findViewById(R.id.user_profile_name);
        sText = (TextView)view.findViewById(R.id.user_profile_short_bio);
        sText.setText((phyLocationList.size()>1)?"You have "+String.valueOf(phyLocationList.size())+" locations":"You have "+String.valueOf(phyLocationList.size())+" location");
        nText.setText(Singleton1.getInstance(getContext()).getPrefKey(Constants.TELEPHONELABEL));
        sText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Singleton1.getInstance(getContext()).addStringSharedPreff("stage","0");
                Toast.makeText(getContext(),"Reset!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),Splash.class);
                startActivity(intent);
            }
        });
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    // only for gingerbread and newer versions


                    if   (getActivity().checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        needPerm =true;
                    }else  if   (getActivity().checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        needPerm = true;
                    }else  if   (getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                        needPerm = true;
                    } else if (getActivity().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
                        needPerm = true;
                    }


                    if (needPerm) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.ACCESS_FINE_LOCATION , android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    } else {
                        Intent intent =new Intent(getContext(),Addlocation.class);
                        getContext().startActivity(intent);
                    }
                } else {

                }


            }
        });

        adapter = new LocationListAdapter(getContext(), phyLocationList, new AsynTaskCallback() {
            @Override
            public void processFinish(String str) {
                phyLocationList.clear();
                new Jsonparse(getContext()).getLocationList(phyLocationList,Constants.LOCATION);
                sText.setText((phyLocationList.size()>1)?"You have "+String.valueOf(phyLocationList.size())+" locations":"You have "+String.valueOf(phyLocationList.size())+" location");
                adapter.notifyDataSetChanged();
            }
        });
        listViewv.setAdapter(adapter);
        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        return view;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission granted...", Toast.LENGTH_LONG).show();
                    needPerm = false;
                    // Add your function here which open camera
                } else {
                    Toast.makeText(getContext(), "Permission required", Toast.LENGTH_LONG).show();
                } return; }
        }
    }



}