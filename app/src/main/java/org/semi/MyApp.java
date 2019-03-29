package org.semi;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;

public class MyApp extends Application {

    private static MyApp instance;
    //new
    private Gson mGSon;

    public MyApp() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGSon = new Gson();
    }

    //new
    public static Context getContext() {
        return instance;
    }
    public Gson getGSon(){
        return mGSon;
    }
    public static MyApp getInstancee() {
        return instance;
    }
}
