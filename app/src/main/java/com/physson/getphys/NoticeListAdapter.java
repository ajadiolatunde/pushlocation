package com.physson.getphys;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;
import java.util.Random;

/**
 * Created by olatunde on 7/17/2017.
 */

public class NoticeListAdapter extends ArrayAdapter<PhyLocation> {
    AsynTaskCallback callback;
    List<PhyLocation> plist;
    Singleton1 singleton1;
    GlideUrl glideUrl;

    static class ViewHolder {
        TextView desTv;
        TextView timeTv;
        ImageView imgVw;
        Button del,vw,forwardB;

    }
    public NoticeListAdapter(Context context, List<PhyLocation> plist, AsynTaskCallback callback) {
        super(context, 0, plist);
        this.callback =callback;
        this.plist=plist;
        singleton1=Singleton1.getInstance(getContext());
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final PhyLocation  p = getItem(position);


        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.frag1noticelist, parent, false);
            viewHolder.desTv  = (TextView) convertView.findViewById(R.id.descTv);
            viewHolder.timeTv = (TextView) convertView.findViewById(R.id.timeTv);
            viewHolder.imgVw =(ImageView)convertView.findViewById(R.id.imgVw);
            viewHolder.del = (Button) convertView.findViewById(R.id.delBtn);
            viewHolder.vw = (Button)convertView.findViewById(R.id.viewBtn);
            viewHolder.forwardB=(Button)convertView.findViewById(R.id.pushBtn);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Lookup view for data population
        viewHolder.desTv.setText(p.getDescription());
        viewHolder.timeTv.setText(PhyssonUtil.getLocalDateTimeFromStamp(p.getTimesent()));
        //System.out.println("Tund from notice "+((p.getPhoto().startsWith("N"))?null:Constants.HTTPURL+Constants.GET_IMAGE+p.getId()+".jpg"));
        glideUrl = new GlideUrl(((p.getPhoto().startsWith("N")))?null:Constants.HTTPURL+Constants.GET_IMAGE+p.getId()+".jpg", new LazyHeaders.Builder()
                .addHeader("Authorization", "CredentialPhysson")
                .build());
        Glide.with(getContext())
                .load(glideUrl)
                //.load((p.getPhoto().startsWith("N"))?null:Constants.HTTPURL+Constants.GET_IMAGE+p.getId()+".jpg")
                .placeholder(R.drawable.dr)
                .error(R.drawable.ic_done_black_24dp)
                .centerCrop()
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .into(viewHolder.imgVw);
        viewHolder.imgVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog settingsDialog = new Dialog(getContext());
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(LayoutInflater.from(getContext()).inflate(R.layout.imagedialog, null));
                ImageView imgView=(ImageView)settingsDialog.findViewById(R.id.imgViewDialog);
                //Button cancel = (Button)settingsDialog.findViewById((R.id.okDialogBtn));

                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        settingsDialog.dismiss();
                    }
                });
                GlideUrl glideUrls = new GlideUrl(((p.getPhoto().startsWith("N")))?null:Constants.HTTPURL+Constants.GET_IMAGE+p.getId()+".jpg", new LazyHeaders.Builder()
                        .addHeader("Authorization", "CredentialPhysson")
                        .build());
                Glide.with(getContext())
                        .load(glideUrls)
                        .placeholder(R.drawable.ic_home_black_24dp)
                        .error(R.drawable.ic_done_black_24dp)
                        .centerCrop()
                        //.diskCacheStrategy(DiskCacheStrategy.NONE)
                        //.skipMemoryCache(true)
                        //.signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .into(imgView);

                settingsDialog.show();
            }
        });
        viewHolder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAction(p.getId());
            }
        });
        viewHolder.vw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhyssonUtil.isgpsenabled(getContext())) {
                    Intent i = new Intent(getContext(), MapsActivityOut.class);
                    i.putExtra("lat", p.getLat());
                    i.putExtra("lon", p.getLon());
                    i.putExtra("id", p.getId());
                    getContext().startActivity(i);
                }else {
                    Toast.makeText(getContext(),"Please enable location service",Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewHolder.forwardB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhyLocation phyLocation =p;
                phyLocation.setForward("Y");
                phyLocation.setSender(singleton1.getPrefKey(Constants.TELEPHONE));
                Intent intent = new Intent(getContext(),GetContacts.class);
                intent.putExtra("push",new Gson().toJson(phyLocation,PhyLocation.class));
                intent.putExtra("upload",false);
                intent.putExtra("type","D");
                getContext().startActivity(intent);
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
                            new Jsonparse(getContext()).delLocationFromList(pId, Constants.NOTIFICATION);
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
        if (plist.size() > 0) {
            count = getCount();
        } else {
            count = 1;
        }
        return count;
        //return getCount();
    }
}
