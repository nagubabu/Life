package com.android.life;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.android.life.Helpers.ServiceHandler;
import com.android.life.Helpers.UserDbManager;
import com.android.life.Helpers.UserPreferenceManager;
import com.android.life.Helpers.WallPostAdapter;
import com.android.life.models.WallPost;
import com.android.life.utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nag on 18/08/15.
 */
public class WallPostActivity extends GlobalActivity {

    UserPreferenceManager userPrefs;
    UserDbManager userDbManager;
    private View mProgressView;
    private ProgressDialog progressDialog;

    private static String URL = "http://medi.orgfree.com/getWallPosts.php";
    // JSON Node names
    private static final String TAG_RESPONSE = "response";

    ArrayList<WallPost> wallPostsList = new ArrayList<WallPost>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        userPrefs = new UserPreferenceManager(this);
        mProgressView = findViewById(R.id.wall_progress);
        progressDialog = ProgressDialog.show(this, "", "Loading posts...");

        userDbManager = new UserDbManager(getApplicationContext());

        populateListView();
        //registerClickCallback();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_wall_posts);
        item.setVisible(false);
        return true;
    }



    private void populateListView() {
        //displayTheList();
        new GetWallPostsTask().execute();
    }

    public class GetWallPostsTask extends AsyncTask<String, Void, String> {

        String $error;

        @Override
        protected String doInBackground(String... url) {
            if (NetworkUtil.isConnected()) {

                // Creating service handler class instance
                ServiceHandler sh = new ServiceHandler();
                JSONArray wallPosts = null;

                // Making a request to url and getting response
                String latestWallPostsJsonStr = sh.makeServiceCall(WallPostActivity.URL, ServiceHandler.GET);
                //Log.d("Response: ", latestWallPostsJsonStr);

                if (latestWallPostsJsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(latestWallPostsJsonStr);
                        if (jsonObj.get(TAG_RESPONSE) instanceof JSONArray) {
                            // Getting JSON Array node
                            wallPosts = jsonObj.getJSONArray(TAG_RESPONSE);
                            // looping through response objects
                            for (int i = 0; i < wallPosts.length(); i++) {
                                JSONObject c = wallPosts.getJSONObject(i);
                                WallPost post = new WallPost(c);
                                wallPostsList.add(post);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        $error = e.getMessage();
                    }
                }

            }else{
                $error = getResources().getString(R.string.no_internet);
            }

            if ($error != null && !$error.isEmpty()) {
                return $error;
            } else {
                return getResources().getString(R.string.success);
            }
        }

        @Override
        protected void onPostExecute(String resp) {
            //progressDialog.dismiss();
            try {
                if ((progressDialog != null) && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (final IllegalArgumentException e) {
                // Handle or log or ignore
            } catch (final Exception e) {
                // Handle or log or ignore
            } finally {
                progressDialog = null;
            }
            if (resp.equals(getResources().getString(R.string.success))) {
                displayTheList();
            }
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
        }
    }

    private void displayTheList() {

        ListView listView = (ListView) findViewById(R.id.lv_wall_posts);

        // Create the adapter to convert the array to views
        WallPostAdapter adapter = new WallPostAdapter(getApplicationContext(), wallPostsList);

        // Attach the adapter to a ListView
        listView.setAdapter(adapter);
    }

}
