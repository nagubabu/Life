package com.android.life;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.life.Helpers.UserPreferenceManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class MainActivity extends GlobalActivity {

    UserPreferenceManager userPrefs;
    private View mProgressView;
    // URL to get contacts JSON
    private static String url = "http://medi.orgfree.com/donors.php";

    // JSON Node names
    private static final String TAG_CONTACTS = "contacts";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_GENDER = "gender";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_PHONE_MOBILE = "mobile";
    private static final String TAG_PHONE_HOME = "home";
    private static final String TAG_PHONE_OFFICE = "office";

    private GetDonorsTask getDonorsTask;

    // contacts JSONArray
    JSONArray contacts = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> donorsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userPrefs = new UserPreferenceManager(this);
        mProgressView = findViewById(R.id.login_progress);
        donorsList = new ArrayList<HashMap<String, String>>();
        //ListView lv = getListView();

        checkLoginStatus();

        populateListView();
        registerClickCallback();
    }

    private void checkLoginStatus() {
        userPrefs.checkLogin();
    }

    private void populateListView() {
        showProgress(true);
        getDonorsTask = new GetDonorsTask();
        Log.d("Action:", "Call to Async task");
        String url = "http://medi.orgfree.com/donors.php";
        String response = null;
        try {
            response = getDonorsTask.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Logger: Donors- ", response);
        String[] donors = {};
        // Create list items
        //String[] donors = {"Raja", "Nag", "Sudhakar", "Karthik", "Prathap", "Chandra", "Namrata", "Vinay", "Ganesh", "Paramesh", "Dileepan", "Mohith", "Sharma", "Shrikanth", "Ramesh", "Ramakrishna", "Raja", "Nag", "Sudhakar", "Karthik", "Prathap", "Chandra", "Namrata", "Vinay", "Ganesh", "Paramesh", "Dileepan", "Mohith", "Sharma", "Shrikanth", "Ramesh", "Ramakrishna"};

        // Build adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_donor, R.id.tv_donor, donors);

        // Configure listview
        ListView donorsList = (ListView) findViewById(R.id.lv_donors);
        donorsList.setAdapter(adapter);
    }

    private void registerClickCallback() {
        ListView donorsList = (ListView) findViewById(R.id.lv_donors);
        donorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View clickedItem, int position, long id) {
                TextView dnrTv = (TextView) clickedItem.findViewById(R.id.tv_donor);
                String donorName = dnrTv.getText().toString();
                Crouton.makeText(MainActivity.this, "#" + position + " " + donorName, Style.INFO).show();
            }
        });
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

    public class GetDonorsTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... url) {
            // TODO: attempt authentication against a network service.
            String resp = getJson("http://medi.orgfree.com/donors.php");
            return resp;
        }

        @Override
        protected void onPostExecute(String success) {
            showProgress(false);

            if (!success.equals("")) {
                Crouton.makeText(MainActivity.this,"Server response success.",Style.CONFIRM).show();
            } else {
                Crouton.makeText(MainActivity.this,"Server response failed.",Style.ALERT).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    private String getJson(String url) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);

        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;

        try {
            HttpResponse response = httpClient.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            String lineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + lineSeparator);
            }
            bufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }

        //Log.d("Logger:", "Sever Resposne: " + stringBuffer.toString());

        return stringBuffer.toString();

    }

    private String parseJson(String response) {
        try {
            JSONObject resp = new JSONObject(response);
            //Log.d("Logger: Status-", resp.getString("response"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


}
