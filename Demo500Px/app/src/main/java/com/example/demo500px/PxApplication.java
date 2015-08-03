package com.example.demo500px;

import com.example.demo500px.network.PxApi;

import android.app.Application;

/**
 * Created by Kanghee on 15-08-03.
 */
public class PxApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PxApi.initHttpClient(this);
    }
}
