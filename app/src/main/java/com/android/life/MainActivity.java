package com.android.life;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.life.utils.NetworkUtil;
import com.android.life.Helpers.User;
import com.android.life.Helpers.UserDbManager;
import com.android.life.Helpers.UsersAdapter;
import com.android.life.Helpers.ServiceHandler;
import com.android.life.Helpers.UserPreferenceManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends GlobalActivity {

    UserPreferenceManager userPrefs;
    UserDbManager userDbManager;
    private View mProgressView;

    // URL to get contacts JSON
    private static String url = "http://medi.orgfree.com/getUsers.php";
    // JSON Node names
    private static final String TAG_RESPONSE = "response";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userPrefs = new UserPreferenceManager(this);
        mProgressView = findViewById(R.id.login_progress);

        userDbManager = new UserDbManager(getApplicationContext());

        populateListView();
        registerClickCallback();
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

    private void populateListView() {
        showProgress(true);
        displayTheList();
        new GetUsersTask().execute();
    }

    private void registerClickCallback() {
        ListView donorsList = (ListView) findViewById(R.id.lv_donors);
        donorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View clickedItem, int position, long id) {
                TextView dnrTv = (TextView) clickedItem.findViewById(R.id.tv_donor);
                TextView userIdTv = (TextView) clickedItem.findViewById(R.id.tv_userId);
                //String userName = dnrTv.getText().toString();
                String userId = userIdTv.getText().toString();
                //Crouton.makeText(MainActivity.this, "#" + userId + " " + userName, Style.INFO).show();
                Intent intent = new Intent(MainActivity.this, UserDetails.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });
    }

    public class GetUsersTask extends AsyncTask<String, Void, String> {

        String $error;

        @Override
        protected String doInBackground(String... url) {
            ArrayList<User> usersList = new ArrayList<User>();
            if (NetworkUtil.isConnected()) {

                // Creating service handler class instance
                ServiceHandler sh = new ServiceHandler();
                JSONArray users = null;

                String latestCreatedDate = userDbManager.getLatestCreatedDate();
                if(latestCreatedDate!=null) {
                    // Creating service handler class instance
                    ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                    nameValuePair.add(new BasicNameValuePair("createdDate", latestCreatedDate));
                    nameValuePair.add(new BasicNameValuePair("tag", "created"));

                    // Making a request to url and getting response
                    String latestNewUsersJsonStr = sh.makeServiceCall(MainActivity.url, ServiceHandler.GET, nameValuePair);
                    Log.d("Response: ", "> " + latestNewUsersJsonStr);
                    if (latestNewUsersJsonStr != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(latestNewUsersJsonStr);
                            // Getting JSON Array node
                            users = jsonObj.getJSONArray(TAG_RESPONSE);
                            Log.d("Action-Object Length: ", String.valueOf(users.length()));
                            // looping through response objects
                            for (int i = 0; i < users.length(); i++) {
                                JSONObject c = users.getJSONObject(i);
                                //Log.d("userId", c.getString("userId"));
                                User newUser = new User(c);
                                usersList.add(newUser);
                            }

                            // Insert into DB table
                            userDbManager.insertUsers(users);

                            Log.d("Action-Users count: ", String.valueOf(userDbManager.getUsersCount()));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            $error = e.getMessage();
                            //Log.d("Error-c: ", $error);
                        }
                    }
                }

                String latestUpdatedDate = userDbManager.getLatestUpdatedDate();
                if(latestUpdatedDate!=null) {
                    usersList = new ArrayList<User>();
                    // Creating service handler class instance
                    ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                    nameValuePair.add(new BasicNameValuePair("updatedDate", latestUpdatedDate));
                    nameValuePair.add(new BasicNameValuePair("tag", "updated"));

                    // Making a request to url and getting response
                    String latestUpdatedUsersJsonStr = sh.makeServiceCall(MainActivity.url, ServiceHandler.GET, nameValuePair);
                    //Log.d("Response: ", "> " + jsonStr);
                    if (latestUpdatedUsersJsonStr != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(latestUpdatedUsersJsonStr);
                            // Getting JSON Array node
                            users = jsonObj.getJSONArray(TAG_RESPONSE);
                            Log.d("Action-Object Length: ", String.valueOf(users.length()));
                            // looping through response objects
                            for (int i = 0; i < users.length(); i++) {
                                JSONObject c = users.getJSONObject(i);
                                //Log.d("userId", c.getString("userId"));
                                User newUser = new User(c);
                                usersList.add(newUser);
                            }

                            // Insert into DB table
                            userDbManager.updateUsers(users);

                            Log.d("Action-Users count: ", String.valueOf(userDbManager.getUsersCount()));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            $error = e.getMessage();
                            //Log.d("Error-u: ", $error);
                        }
                    }
                }
            }

            if($error != null  && !$error.isEmpty()) {
                return $error;
            }else if (usersList.isEmpty()) {
                return getResources().getString(R.string.no_data_from_server);
                //return "False";
            }else {
                return getResources().getString(R.string.success);
            }
        }

        @Override
        protected void onPostExecute(String success) {
            if (success.equals(getResources().getString(R.string.success))) {
                displayTheList();
            } else Crouton.makeText(MainActivity.this, success, Style.ALERT).show();
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    private void displayTheList() {

        ArrayList<User> usersList = userDbManager.getAllUsers();

        ListView listView = (ListView) findViewById(R.id.lv_donors);

        // Create the adapter to convert the array to views
        UsersAdapter adapter = new UsersAdapter(getApplicationContext(), usersList);

        // Attach the adapter to a ListView
        listView.setAdapter(adapter);

        showProgress(false);
    }
}
