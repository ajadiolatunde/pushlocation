package com.physson.getphys;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by olatunde on 5/11/2017.
 */

public class Home_fragment3 extends Fragment implements AsynTaskCallback {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPageNo;
    private TextView bkTextView,sText;
    private ListView noticeListView;
    List<PhyLocation> phyNoticeList;
    NoticeListAdapter adapter;
    private TrackAsync trackAsync;
    private RecyclerView recyclerView;
    Activity activity;
    private boolean isViewShown = false;


    public static Home_fragment3 newInstance(int pageNo) {

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        Home_fragment3 fragment = new Home_fragment3();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mPageNo = getArguments().getInt(ARG_PAGE);
        phyNoticeList = new ArrayList<>();


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_3, container, false);
        sText = (TextView)view.findViewById(R.id.noticeText);
        new Jsonparse(getContext()).getLocationList(phyNoticeList,Constants.NOTIFICATION);
        sText.setText((phyNoticeList.size()>1)?"You have "+String.valueOf(phyNoticeList.size())+" notices":"You have "+String.valueOf(phyNoticeList.size())+" ntice");

        noticeListView = (ListView)view.findViewById(R.id.listlocation);
        adapter = new NoticeListAdapter(getContext(), phyNoticeList, new AsynTaskCallback() {
            @Override
            public void processFinish(String str) {
               new Jsonparse(getContext()).getLocationList(phyNoticeList,Constants.NOTIFICATION);
                sText.setText((phyNoticeList.size()>1)?"You have "+String.valueOf(phyNoticeList.size())+" notices":"You have "+String.valueOf(phyNoticeList.size())+" ntice");
                adapter.notifyDataSetChanged();
            }
        });

        noticeListView.setAdapter(adapter);

        return view;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String body =intent.getExtras().getString("body");
            //System.out.println("Tunde ....broadcast  "+body);
           new Jsonparse(getContext()).getLocationList(phyNoticeList,Constants.NOTIFICATION);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver((mMessageReceiver), new IntentFilter(Constants.NOTIFICATION));
    }

    @Override
    public void onStop() {
        super.onStop();
        //Had issue when context was getActivity
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
    }


    @Override
    public void onAttach(Activity activity) {

        this.activity = activity;
        super.onAttach(activity);
    }
        @Override
    public void processFinish(String str){
        String response = str;

    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }
}