package com.pastors.pastorssms;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by techiestown on 10/5/16.
 */
public class MyAppClass extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context newBase) {
        MultiDex.install(newBase);
        super.attachBaseContext(newBase);
    }
}