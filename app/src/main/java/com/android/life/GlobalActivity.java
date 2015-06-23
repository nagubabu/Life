package com.android.life;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Nag on 6/22/15.
 */
public class GlobalActivity extends Activity {
    private BroadcastReceiver networkUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        networkUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager)
                        context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

                boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
                if (isConnected) {
                    //Crouton.makeText(GlobalActivity.this, "Connected to internet", Style.CONFIRM).show();
                } else
                    Crouton.makeText(GlobalActivity.this, "No internet connection", Style.ALERT).show();
            }
        };
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        registerReceiver(networkUpdateReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkUpdateReceiver);
        super.onDestroy();
    }
}
