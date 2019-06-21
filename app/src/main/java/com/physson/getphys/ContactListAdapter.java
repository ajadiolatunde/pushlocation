package com.physson.getphys;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.client.cache.Resource;

/**
 * Created by olatunde on 7/17/2017.
 */

public class ContactListAdapter extends ArrayAdapter<Contact> {
    AsynTaskCallback callback;
    List<Contact> clist;
    Singleton1 singleton1;

    static class ViewHolder {
        TextView nameTv,timeTv,phoneTv,iconVw;
        ImageView delVw;


    }
    public ContactListAdapter(Context context, List<Contact> clist, AsynTaskCallback callback) {
        super(context, 0, clist);
        this.callback =callback;
        this.clist=clist;
        singleton1=Singleton1.getInstance(getContext());
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Contact  c = getItem(position);


        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.frag1contactlist, parent, false);
            viewHolder.nameTv  = (TextView) convertView.findViewById(R.id.c_nameTV);
            viewHolder.timeTv = (TextView) convertView.findViewById(R.id.c_dateTv);
            viewHolder.phoneTv =(TextView)convertView.findViewById(R.id.c_phoneTv);
            viewHolder.delVw=(ImageView) convertView.findViewById(R.id.c_delImg);
            viewHolder.iconVw=(TextView) convertView.findViewById(R.id.c_iconImg);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.timeTv.setText(PhyssonUtil.getLocalDateTimeFromStamp(c.getDate()));
        viewHolder.nameTv.setText(c.getName());
        viewHolder.phoneTv.setText(c.getPhone());
        viewHolder.iconVw.setText(c.getName().substring(0,1).toUpperCase());
//        int col = R.color.colorPhysoon;
//        viewHolder.iconVw.setBackgroundColor(getContext().getResources().getColor(col));
        viewHolder.delVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAction(c.getPhone());
            }
        });


        // Return the completed view to render on screen
        return convertView;
    }
    public void confirmAction(final String pId){
        final String  p = pId;
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        Random rand = new Random();
        final int random = rand.nextInt(100) + 999;
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete entry")
                .setView(input)
                .setMessage("Please enter "+String.valueOf(random)+" to cancel")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String num = input.getText().toString();
                        if (random == Integer.parseInt(num)) {
                            new Jsonparse(getContext()).delContactFromList(pId, Constants.CONTACT);
                            Toast.makeText(getContext(), pId, Toast.LENGTH_LONG).show();
                            callback.processFinish("1");
                        }else {

                            Toast.makeText(getContext(),"Invalid code!",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // doo nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }


    @Override
    public int getItemViewType(int position) {

        return position;
    }
    @Override
    public int getViewTypeCount() {

        int count;
        if (clist.size() > 0) {
            count = getCount();
        } else {
            count = 1;
        }
        return count;
        //return getCount();
    }

}
