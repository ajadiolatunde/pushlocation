package com.physson.getphys;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

/**
 * Created by olatunde on 7/17/2017.
 */

public class LocationListAdapter extends ArrayAdapter<PhyLocation> {
    AsynTaskCallback callback;
    List<PhyLocation> plist;

    static class ViewHolder {
        TextView desTv;
        TextView timeTv;
        ImageView imgVw;
        Button del,vw,pushB;

    }
    public LocationListAdapter(Context context, List<PhyLocation> plist,AsynTaskCallback callback) {
        super(context, 0, plist);
        this.callback =callback;
        this.plist=plist;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final PhyLocation  p = getItem(position);


        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.frag1locationlist, parent, false);
            viewHolder.desTv  = (TextView) convertView.findViewById(R.id.descTv);
            viewHolder.timeTv = (TextView) convertView.findViewById(R.id.timeTv);
            viewHolder.imgVw =(ImageView)convertView.findViewById(R.id.imgVw);
            viewHolder.del = (Button) convertView.findViewById(R.id.delBtn);
            viewHolder.vw = (Button)convertView.findViewById(R.id.viewBtn);
            viewHolder.pushB=(Button)convertView.findViewById(R.id.pushBtn);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Lookup view for data population
        viewHolder.desTv.setText(p.getDescription());
        viewHolder.timeTv.setText(PhyssonUtil.getLocalDateTimeFromStamp(p.getTstamp()));

        Glide.with(getContext())
                .load(Singleton1.getInstance(getContext()).getPhotoFile(p.getId())+".jpg")
                .placeholder(R.drawable.dr)
                .centerCrop()
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                //.signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(viewHolder.imgVw);
        viewHolder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Jsonparse(getContext()).delLocationFromList(p.getId(),Constants.LOCATION);
                if(p.getPhoto().equals("Yes")){
                    new File(Singleton1.getInstance(getContext()).getPhotoFile(p.getId())+".jpg").delete();
                    Toast.makeText(getContext(),"File deleted",Toast.LENGTH_LONG).show();
                }
                callback.processFinish("1");
            }
        });
        viewHolder.vw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhyssonUtil.isgpsenabled(getContext())) {
                    Intent i = new Intent(getContext(), MapsActivity.class);
                    i.putExtra("lat", p.getLat());
                    i.putExtra("lon", p.getLon());
                    i.putExtra("id", p.getId());
                    getContext().startActivity(i);
                }else {
                    Toast.makeText(getContext(),"Please enable location service",Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewHolder.pushB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),GetContacts.class);
                intent.putExtra("upload",true);
                intent.putExtra("type","P");
                intent.putExtra("push",new Gson().toJson(p,PhyLocation.class));
                getContext().startActivity(intent);
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }
    @Override
    public int getViewTypeCount() {

        int count;
        if (plist.size() > 0) {
            count = getCount();
        } else {
            count = 1;
        }
        return count;
        //return getCount();
    }



}
