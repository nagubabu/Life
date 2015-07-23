package com.android.life;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.life.Helpers.UserPreferenceManager;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Nag on 6/22/15.
 */
public class GlobalActivity extends ActionBarActivity {

    private BroadcastReceiver networkUpdateReceiver;
    UserPreferenceManager userPreferenceManager;
    int onStartCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onStartCount = 1;
        if (savedInstanceState == null) // 1st time
        {
            this.overridePendingTransition(R.anim.slide_in_left,
                    R.anim.slide_out_left);
        } else // already created so reverse animation
        {
            onStartCount = 2;
        }
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
                    Crouton.makeText(GlobalActivity.this, getResources().getString(R.string.no_internet), Style.ALERT).show();
            }
        };
        userPreferenceManager = new UserPreferenceManager(this);
    }

    private void checkLoginStatus() {
        if(userPreferenceManager.checkLogin()){
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_profile:
                gotoProfileFrag();
                return true;
            case R.id.action_logout:
                appLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void gotoProfileFrag() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("showFragment","Profile");
        startActivity(intent);
    }

    private void appLogout() {
        userPreferenceManager.logoutUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (onStartCount > 1) {
            this.overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_right);

        } else if (onStartCount == 1) {
            onStartCount++;
        }

    }

    @Override
    protected void onResume() {
        registerReceiver(networkUpdateReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        super.onResume();
        checkLoginStatus();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkUpdateReceiver);
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }
}
