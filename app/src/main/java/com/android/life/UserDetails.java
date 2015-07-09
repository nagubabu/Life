package com.android.life;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.life.Helpers.ServiceHandler;
import com.android.life.Helpers.User;
import com.android.life.Helpers.UserDbManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Nag on 6/30/15.
 */
public class UserDetails extends GlobalActivity implements View.OnClickListener {

    UserDbManager userDbManager;
    private ProgressDialog progressDialog;
    Button callBtn, smsBtn;

    User userDetails;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = ProgressDialog.show(this, "", "Loading...");

        userDbManager = new UserDbManager(getApplicationContext());

        setContentView(R.layout.user_details);

        callBtn = (Button) findViewById(R.id.btn_call);
        callBtn.setOnClickListener(this);
        smsBtn = (Button) findViewById(R.id.btn_sms);
        smsBtn.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = Integer.valueOf(extras.getString("USER_ID"));
            //Crouton.makeText(this,userId, Style.CONFIRM).show();
            userDetails = userDbManager.getUser(userId);
            showUserDetails(userDetails);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        TextView tvPhone = (TextView) findViewById(R.id.tv_phone);
        String phoneNumber = tvPhone.getText().toString();

        if (v == callBtn) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        }

        if (v == smsBtn) {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", phoneNumber);
            smsIntent.putExtra("sms_body", "Urgent, blood required immediately. Plz help me!");
            startActivity(smsIntent);
        }
    }

    private void showUserDetails(User userDetails) {

        TextView userName = (TextView) findViewById(R.id.tv_userName);
        TextView bloodGroup = (TextView) findViewById(R.id.tv_bloodGroup);
        TextView address = (TextView) findViewById(R.id.tv_address);
        TextView phone = (TextView) findViewById(R.id.tv_phone);
        if (!userDetails.getName().equals(""))
            userName.setText(userDetails.getName());
        if (!userDetails.getBloodGroup().equals(""))
            bloodGroup.setText(userDetails.getBloodGroup());
        if (!userDetails.getAddress().equals(""))
            address.setText(userDetails.getAddress());
        if (!userDetails.getPhone().equals(""))
            phone.setText(userDetails.getPhone());

        progressDialog.dismiss();
    }
}
