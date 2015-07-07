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

import com.android.life.Helpers.NetworkUtil;
import com.android.life.Helpers.User;
import com.android.life.Helpers.UserDbManager;
import com.android.life.Helpers.UsersAdapter;
import com.android.life.Helpers.ServiceHandler;
import com.android.life.Helpers.UserPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class MainActivity extends GlobalActivity {

    UserPreferenceManager userPrefs;
    UserDbManager userDbManager;
    private View mProgressView;
    // URL to get contacts JSON
    private static String url = "http://medi.orgfree.com/members.php";

    // JSON Node names
    private static final String TAG_RESPONSE = "response";

    // donors JSONArray
    JSONArray users = null;

    ArrayList<User> arrayOfUsers = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userPrefs = new UserPreferenceManager(this);
        mProgressView = findViewById(R.id.login_progress);

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

    public class GetUsersTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Void doInBackground(String... url) {
            userDbManager = new UserDbManager(getApplicationContext());
            if (NetworkUtil.isConnected()) {
                // Creating service handler class instance
                ServiceHandler sh = new ServiceHandler();
                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(MainActivity.url, ServiceHandler.GET);
                //Log.d("Response: ", "> " + jsonStr);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        // Getting JSON Array node
                        users = jsonObj.getJSONArray(TAG_RESPONSE);

                        // looping through response objects
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject c = users.getJSONObject(i);
                            //Log.d("userId", c.getString("userId"));
                            User newUser = new User(c);
                            arrayOfUsers.add(newUser);
                        }

                        // Insert or update into DB table
                        userDbManager.insertOrReplaceUser(users);
                        Log.d("Reading: ", "Reading all contacts..");
                        List<User> users = userDbManager.getAllUsers();

                        for (User user : users) {
                            String log = "Id: " + user.getUserID() + " ,Name: " + user.getName() + " ,Phone: " + user.getPhone();
                            // Writing Contacts to log
                            Log.d("Name: ", log);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");

                }
            } else {
                // Reading all contacts
                Log.d("Offline: ", "Reading all contacts..");
                List<User> users = userDbManager.getAllUsers();

                for (User user : users) {
                    //Log.d("User => ", user.getName().toString());
                    arrayOfUsers.add(user);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void success) {
            showProgress(false);

            ListView listView = (ListView) findViewById(R.id.lv_donors);

            // Create the adapter to convert the array to views

            UsersAdapter adapter = new UsersAdapter(getApplicationContext(), arrayOfUsers);

            // Attach the adapter to a ListView
            listView.setAdapter(adapter);
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}
