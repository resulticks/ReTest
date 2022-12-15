package com.sdk.sample;

import android.app.Application;
import android.os.StrictMode;

import io.mob.resu.reandroidsdk.ReAndroidSDK;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ReAndroidSDK.getInstance(this);
    }

}


