package com.android.life;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.life.Helpers.ServiceHandler;
import com.android.life.Helpers.UserPreferenceManager;
import com.android.life.utils.NetworkUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Nag on 03/11/15.
 */
public class NewPostActivity extends GlobalActivity implements View.OnClickListener {

    Button btn_submitPost;
    EditText et_postContent;
    TextView tv_backToWall;
    private ProgressDialog progressDialog;
    private static String URL = "http://medi.orgfree.com/createPost.php";

    ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
    private CreatePost creatPostTask = null;
    UserPreferenceManager userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        userPrefs = new UserPreferenceManager(getApplicationContext());

        btn_submitPost = (Button) findViewById(R.id.btn_post);
        et_postContent = (EditText) findViewById(R.id.et_post_content);
        tv_backToWall = (TextView) findViewById(R.id.tv_back_to_wall);

        btn_submitPost.setOnClickListener(this);
        tv_backToWall.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.checkLoginStatus();
        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_new_post);
        item.setVisible(false);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == tv_backToWall) {
            Intent intent = new Intent(NewPostActivity.this, WallPostActivity.class);
            startActivity(intent);
        } else if (v == btn_submitPost) {
            savePost();
        }
    }

    private void savePost() {

        if(creatPostTask != null){
            return;
        }

        et_postContent.setError(null);
        String postContent = et_postContent.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(postContent)) {
            et_postContent.setError(getString(R.string.error_field_required));
            focusView = et_postContent;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            if (NetworkUtil.isConnected()) {
                progressDialog = ProgressDialog.show(this, "", "Creating post...");

                postParams.add(new BasicNameValuePair("user_id", String.valueOf(userPrefs.getUserId())));
                postParams.add(new BasicNameValuePair("message", postContent));

                creatPostTask = new CreatePost();
                creatPostTask.execute();
            } else {
                Crouton.makeText(this, getResources().getString(R.string.no_internet), Style.ALERT).show();
            }
        }

    }

    private class CreatePost extends AsyncTask<String, Void, String> {

        String $error;

        @Override
        protected String doInBackground(String... params) {
            if (NetworkUtil.isConnected()) {

                // Creating service handler class instance
                ServiceHandler sh = new ServiceHandler();
                JSONArray creatPost = null;

                // Making a request to url and getting response
                String createWallPostsResp = sh.makeServiceCall(NewPostActivity.URL, ServiceHandler.POST, postParams);
                Log.d("Response: ", createWallPostsResp);
                if(createWallPostsResp != null && createWallPostsResp.equals("fail")){
                    $error = "Couldn't post the message to wall. Please try agian in a moment!";
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

            if(resp.equals(getResources().getString(R.string.success))){
                gotoWallPostActivity();
            }else {
                Crouton.makeText(NewPostActivity.this, resp, Style.ALERT).show();
            }
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
        }
    }

    private void gotoWallPostActivity(){
        Intent intent = new Intent(NewPostActivity.this, WallPostActivity.class);
        startActivity(intent);
        finish();
    }
}
