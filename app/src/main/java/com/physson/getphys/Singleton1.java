package com.physson.getphys;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by olatunde on 7/18/2017.
 */
public class Singleton1 {
   SharedPreferences mSharedPrefrence;
    private static Singleton1 ourInstance;
    private Context mContext;
    private File photo;
    private String latlon;
    Boolean pass;

    public Boolean getPass() {
        return pass;
    }

    public void setPass(Boolean pass) {
        this.pass = pass;
    }

    public static Singleton1 getInstance(Context mContext) {
        if (ourInstance == null){

            ourInstance = new Singleton1(mContext);
        }
        return ourInstance;
    }



    private Singleton1(Context mContext) {
        this.mContext = mContext;
    }

    public void setmSharedPrefrence(){
        //https://stackoverflow.com/questions/30806342/pendingintent-cause-error
        //mSharedPrefrence = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSharedPrefrence = mContext.getSharedPreferences(Constants.MYPREF,Activity.MODE_PRIVATE);
    }
    private boolean isPrefCreated(){

        String value = mSharedPrefrence.getString("test","");
        return (value.equals(""))? false:true;
    }

    public String getToken(){
        return mSharedPrefrence.getString("firetoken","NO firetoken ");
    }

    public void addStringSharedPreff(String key, String value){
        if (isPrefCreated()){

            SharedPreferences.Editor editor = mSharedPrefrence.edit();
            editor.putString(key,value);
            editor.commit();

        }else {
            setmSharedPrefrence();

            SharedPreferences.Editor editor = mSharedPrefrence.edit();
            editor.putString("test","init");
            editor.putString(key,value);
            editor.commit();
        }

    }

    public List<Country> getCountyList(){
        String json = new Jsonparse(mContext).getCountry();
        List<Country> cl = new ArrayList<>();
        try{
            //JSONObject j_obj= new JSONObject(json);
            JSONArray array = new JSONArray(json);
            for (int i=0;i<array.length();i++){
                Country country = new Country();
                JSONObject jb = array.getJSONObject(i);
                country.setName(jb.getString("name"));
                country.setCode(jb.getString("code"));
                country.setDial_code(jb.getString("dial_code"));
                cl.add(country);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        return cl;
    }

    public void getAllpref(){
        //System.out.println("Tunde...prefkeys  "+getToken()+"  "+mSharedPrefrence.getAll());
    }


    public String getPrefKey(String key){
       setmSharedPrefrence();
        String value = mSharedPrefrence.getString(key,"");
        return value;
    }
    public File getPhotoFile(String name){
        File externalFileDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(externalFileDir == null){
            return null;
        }
        return  new File(externalFileDir,name);
    }
    public void setGpsLatlon(String ll){
        this.latlon =ll;
    }


}
