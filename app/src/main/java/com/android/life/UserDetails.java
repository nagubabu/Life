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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Nag on 6/30/15.
 */
public class UserDetails extends GlobalActivity implements View.OnClickListener {

    private View mProgressView;
    private ProgressDialog progressDialog;
    Button callBtn, smsBtn;


    // URL to get contacts JSON
    private static String url = "http://medi.orgfree.com/getUser.php";

    // JSON Node names
    private static final String TAG_RESPONSE = "response";

    // donors JSONArray
    JSONObject user = null;
    User newUser;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressView = findViewById(R.id.login_progress);
        progressDialog = ProgressDialog.show(this, "", "Loading...");

        setContentView(R.layout.user_details);

        callBtn = (Button) findViewById(R.id.btn_call);
        callBtn.setOnClickListener(this);
        smsBtn = (Button) findViewById(R.id.btn_sms);
        smsBtn.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("USER_ID");
            //Crouton.makeText(this,userId, Style.CONFIRM).show();
            getUserDetails();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows the progress UI and hides the activity.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void getUserDetails() {
        new GetUsersDetailsTask().execute();
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

    public class GetUsersDetailsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showProgress(true);
        }

        @Override
        protected Void doInBackground(String... params) {

            List<NameValuePair> param = new LinkedList<NameValuePair>();
            param.add(new BasicNameValuePair("user_id", String.valueOf(userId)));

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(UserDetails.url, ServiceHandler.GET, param);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    user = jsonObj.getJSONObject(TAG_RESPONSE);
                    newUser = new User(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void success) {
            //showProgress(false);
            TextView userName = (TextView) findViewById(R.id.tv_userName);
            TextView bloodGroup = (TextView) findViewById(R.id.tv_bloodGroup);
            TextView address = (TextView) findViewById(R.id.tv_address);
            TextView phone = (TextView) findViewById(R.id.tv_phone);
            if (!userName.equals(""))
                userName.setText(newUser.name);
            if (!bloodGroup.equals(""))
                bloodGroup.setText(newUser.blood_group);
            if (!address.equals(""))
                address.setText(newUser.address);
            if (!phone.equals(""))
                phone.setText(newUser.phone);

            progressDialog.dismiss();

        }

        @Override
        protected void onCancelled() {
            //showProgress(false);
            progressDialog.dismiss();
        }
    }
}
