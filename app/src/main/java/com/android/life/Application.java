package com.android.life;

import android.content.Context;

import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by Nag on 6/18/15.
 */
public class Application extends android.app.Application {
    private static Application instance;
    // http client
    public static DefaultHttpClient httpClient = new DefaultHttpClient();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Application getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

}
