package com.physson.getphys;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by olatunde on 7/17/2017.
 */

public class GetContactListAdapter extends ArrayAdapter<Contact> {
    AsynTaskCallback callback;
    List<Contact> clist;
    List<Contact> flist;
    Singleton1 singleton1;

    static class ViewHolder {
        TextView nameTv,timeTv,phoneTv,iconVw;
        ImageView delVw;
        LinearLayout layout;


    }
    public GetContactListAdapter(Context context, List<Contact> clist, AsynTaskCallback callback) {
        super(context, 0, clist);
        this.callback =callback;
        this.clist=clist;
        this.flist=new ArrayList<>();
        this.flist.addAll(clist);
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
            viewHolder.layout = (LinearLayout)convertView.findViewById(R.id.layout1);
            viewHolder.nameTv  = (TextView) convertView.findViewById(R.id.c_nameTV);
            viewHolder.timeTv = (TextView) convertView.findViewById(R.id.c_dateTv);
            viewHolder.phoneTv =(TextView)convertView.findViewById(R.id.c_phoneTv);
            viewHolder.delVw=(ImageView) convertView.findViewById(R.id.c_delImg);
            viewHolder.iconVw=(TextView) convertView.findViewById(R.id.c_iconImg);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        View.OnClickListener fg = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),c.getName(),Toast.LENGTH_SHORT).show();
                String code =singleton1.getPrefKey(Constants.DIAL_CODE);
                callback.processFinish((c.getPhone().startsWith("0"))?code+c.getPhone().substring(1):code+c.getPhone());
            }
        };
        viewHolder.delVw.setVisibility(View.GONE);
        viewHolder.timeTv.setText(PhyssonUtil.getLocalDateTimeFromStamp(c.getDate()));
        viewHolder.nameTv.setText(c.getName());
        viewHolder.phoneTv.setText(c.getPhone());
        viewHolder.iconVw.setText(c.getName().substring(0,1).toUpperCase());
//        int col = R.color.colorPhysoon;
//        viewHolder.iconVw.setBackgroundColor(getContext().getResources().getColor(col));
        viewHolder.layout.setOnClickListener(fg);
        viewHolder.nameTv.setOnClickListener(fg);
        viewHolder.phoneTv.setOnClickListener(fg);
        viewHolder.timeTv.setOnClickListener(fg);



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
        if (clist.size() > 0) {
            count = getCount();
        } else {
            count = 1;
        }
        return count;
        //return getCount();
    }
    public void filter(String cText){
        cText = cText.toLowerCase(Locale.getDefault());
        clist.clear();
        if (cText.length()==0){
            clist.addAll(flist);

        }else{
            for (Contact ct: flist){
                if (ct.getName().toLowerCase(Locale.getDefault()).contains(cText) || ct.getPhone().contains(cText)){
                    clist.add(ct);
                }
            }
        }
        notifyDataSetChanged();
    }

}
