package com.physson.getphys;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by olatunde on 7/17/2017.
 */

public class PhoneAdapter extends ArrayAdapter<Phone> {

    static class ViewHolder {
        TextView nameTv;
        TextView phoneTv;
        Button [] buttons;


    }

    public PhoneAdapter(Context context, List<Phone> phones) {
        super(context, 0, phones);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Phone p = getItem(position);
        //int M = p.getPhonecount();
        String[] names=p.getPhones().split(":");
        final String c = String.valueOf(p.getPhonecount());
        //int m =Integer.parseInt(c);
        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user, parent, false);
            LinearLayout ll1 = (LinearLayout) convertView.findViewById(R.id.ll);
            ll1.setBackgroundColor(getContext().getResources().getColor(  R.color.colorPhysoon));


            Button[] b = new Button[p.getPhonecount()];

            for (int i = 0; i < p.getPhonecount(); i++) {
                b[i] = new Button(getContext());
                b[i].setText("T.."+String.valueOf(p.getPhonecount()));
                b[i].setTextSize(10);
                b[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                ll1.addView(b[i]);
            }
            viewHolder.buttons=b;
            viewHolder.nameTv  = (TextView) convertView.findViewById(R.id.p_Name);
            viewHolder.phoneTv = (TextView) convertView.findViewById(R.id.p_phone);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Lookup view for data population
        viewHolder.nameTv.setText(p.getName());
        viewHolder.phoneTv.setText(p.getPhones());
        int t =1;


        for(Button button : viewHolder.buttons){
            button.setText(names[t]);
            button.setTextSize(10);
            button.setId(convertView.getId());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),c ,Toast.LENGTH_SHORT).show();
                }
            });
            t++;
            //doo something
        }

        // Return the completed view to render on screen
        return convertView;
    }
    @Override
    public int getItemViewType(int position) {
// TODO Auto-generated method stub
        return position;
    }
    @Override
    public int getViewTypeCount() {
// TODO Auto-generated method stub
        return getCount();
    }
}
