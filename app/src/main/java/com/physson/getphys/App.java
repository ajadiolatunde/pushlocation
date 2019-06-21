package com.physson.getphys;

import android.app.Application;
import android.content.Context;

/**
 * Created by olatunde on 8/12/17.
 */

public class App extends Application {

    protected static Context context = null;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

}