package com.android.life.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.life.Application;

/**
 * Created by Nag on 6/18/15.
 */
public class NetworkUtil {
    public static boolean isConnected() {
        return isConnected(Application.getAppContext());
    }

    private static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
