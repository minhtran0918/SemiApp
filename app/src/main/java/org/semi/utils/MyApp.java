package org.semi.utils;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;

public class MyApp extends Application {

    private static MyApp mInstance;
    //put object to shared preference
    private Gson mGSon;

    public static MyApp getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mGSon = new Gson();
    }

    //new

    public Gson getGSon() {
        return mGSon;
    }

    public void HideKeyboardSystem(View v) {
        //If not hide Keybroad -> The dialog interface is not correct
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
