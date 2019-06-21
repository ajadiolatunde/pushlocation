package com.physson.getphys;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by olatunde on 7/18/2017.
 */

public class Jsonparse {
    private Context context;
    Singleton1 singleton1;
    public Jsonparse(Context context){
        this.context=context;
        singleton1 = Singleton1.getInstance(context);
    }
    public String getCodeMeassage(String res){
        return "";
    }

    public void addLocation(PhyLocation l,String item){
        String location = singleton1.getPrefKey(item);

        if (location.equals("")){
            JSONObject loc = new JSONObject();
            try {
                loc.put(l.getId(),new Gson().toJson(l, PhyLocation.class));
                singleton1.addStringSharedPreff(item,loc.toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
        }else {
            try {
                JSONObject loc = new JSONObject(location);
                //serializes json
                loc.put(l.getId(),new Gson().toJson(l, PhyLocation.class));
                singleton1.addStringSharedPreff(item,loc.toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

    }
    public void delLocationFromList(String id,String item){
        String location = singleton1.getPrefKey(item);
        try {
            JSONObject jlist = new JSONObject(location);
            jlist.remove(id);
            singleton1.addStringSharedPreff(item,jlist.toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    //Id is the uniuqe
    public void getLocationList(List<PhyLocation> list,String item){
        String location = singleton1.getPrefKey(item);
        //System.out.println("Tunde loc all  "+location);
        singleton1.getAllpref();
        list.clear();

        if (!location.equals("")){
            try {
                JSONObject jlist = new JSONObject(location);
                Iterator<String> keysIterator = jlist.keys();
                while (keysIterator.hasNext())
                {
                    String keyStr = (String)keysIterator.next();
                    String json =jlist.getString(keyStr);
                    // deserializes json
                    PhyLocation phyLocation = new Gson().fromJson(json, PhyLocation.class);
                    list.add(phyLocation);
                }
                Collections.sort(list, new Comparator<PhyLocation>() {
                    @Override
                    public int compare(PhyLocation o1, PhyLocation o2) {
                        return o2.getTstamp().compareTo(o1.getTstamp());
                    }
                });
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
    //Phone is the unique
    public void addContact(Contact c,String prefitem){
        String contact = singleton1.getPrefKey(prefitem);

        if (contact.equals("")){
            JSONObject con = new JSONObject();
            try {
                con.put(c.getPhone(),new Gson().toJson(c, Contact.class));
                singleton1.addStringSharedPreff(prefitem,con.toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
        }else {
            try {
                JSONObject con = new JSONObject(contact);
                //serializes json
                con.put(c.getPhone(),new Gson().toJson(c, Contact.class));
                singleton1.addStringSharedPreff(prefitem,con.toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

    }

    public void delContactFromList(String id,String prefitem){
        String con = singleton1.getPrefKey(prefitem);
        try {
            JSONObject jlist = new JSONObject(con);
            jlist.remove(id);
            singleton1.addStringSharedPreff(prefitem,jlist.toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getContactList(List<Contact> list,String item){
        String con = singleton1.getPrefKey(item);
        //System.out.println("Tunde con all  "+con);
        singleton1.getAllpref();
        list.clear();

        if (!con.equals("")){
            try {
                JSONObject jlist = new JSONObject(con);
                Iterator<String> keysIterator = jlist.keys();
                while (keysIterator.hasNext())
                {
                    String keyStr = (String)keysIterator.next();
                    String json =jlist.getString(keyStr);
                    // deserializes json
                    Contact cont = new Gson().fromJson(json, Contact.class);
                    list.add(cont);
                }
                Collections.sort(list, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact o1, Contact o2) {
                        return o1.getName().toUpperCase().compareTo(o2.getName().toUpperCase());
                    }
                });
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    public Boolean numberExist(String phone){
        String contact = singleton1.getPrefKey(Constants.CONTACT);
        Boolean status = false;
        if (!contact.equals("")){
            try {
                JSONObject jlist = new JSONObject(contact);
                //Iterator<String> keysIterator = jlist.keys();
                if (jlist.has(phone)){
                    status=true;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return status;
    }

    public String getPoi(String json){
        String result ="";
        int count=0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray pjsonArray = jsonObject.getJSONArray("results");

            for (int i =0;i<pjsonArray.length();i++){

                //System.out.println("Tunde ..."+ pjsonArray.getJSONObject(i));
                count=i+1;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return String.valueOf(count);
    }
    public List<String> getPoiArray(String json,Double dlat,Double dlon){
        String result ="";
        List<String> list =new ArrayList<String>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray pjsonArray = jsonObject.getJSONArray("results");

            for (int i =0;i<pjsonArray.length();i++){
                //System.out.println("Tunde ..."+ pjsonArray.getJSONObject(i));
                JSONObject js = pjsonArray.getJSONObject(i);
                JSONObject ge = js.getJSONObject("geometry");
                    JSONObject lo = ge.getJSONObject("location");
                    String lat = lo.getString("lat");
                    String lon = lo.getString("lng");
                    String  na = js.getString("name");

                //System.out.println("Tunde latlon "+na+" "+lat+" "+lon+" "+String.valueOf(dlat)+" "+String.valueOf(dlon));

                Double nlat=Double.parseDouble(lat);
                Double nlon =Double.parseDouble(lon);
                int distance = PhyssonUtil.getDistancebtw(dlat,dlon,nlat,nlon);
                String all = String.valueOf(i+1)+". "+na+", "+String.valueOf(distance)+"m to destination";
                list.add(all);



            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public String getCode(String code){
        String result ="";
        try {
            JSONObject jsonObject = new JSONObject(code);
            result = jsonObject.getString("code");

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }
    public String getMessage(String str){
        String result ="";
        try {
            JSONObject jsonObject = new JSONObject(str);
            result = jsonObject.getString(Constants.MESSAGE);

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }

    public String getData(String str){
        String result ="";
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONObject dt = jsonObject.getJSONObject(Constants.DATA);
            result = dt.getString("uiid");
            //System.out.println("Tunde  "+result);

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }

    public String getCountry(){
        String json = null;
        try {
            InputStream is= context.getAssets().open("country.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
        return json;
    }

}
