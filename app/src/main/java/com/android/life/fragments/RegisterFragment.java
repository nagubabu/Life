package com.android.life.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.life.R;
import com.android.life.Helpers.NetworkUtil;
import com.android.life.Helpers.ServiceHandler;
import com.android.life.Helpers.UserPreferenceManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Nag on 7/13/15.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {
    private Listener listener;
    UserPreferenceManager userPrefs;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;
    private static String url = "http://medi.orgfree.com/signin.php";

    // UI references.
    private AutoCompleteTextView uEmail;
    private EditText uPass, uName, ucPass, uAddress, uPhone;
    private ProgressDialog progressDialog;
    private Button signup;
    private Spinner bloodGroupSelector;

    JSONObject loginResponse = null;
    // JSON Node names
    private static final String TAG_RESPONSE = "response";
    private static final String TAG_STATUS = "status";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_USERNAME = "name";

    public interface Listener {
        //public void gotoLoginFrag();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement RegisterFragment.Listener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater
                .inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {

        userPrefs = new UserPreferenceManager(getActivity());
        // Set up the login form.
        uName = (EditText) getView().findViewById(R.id.et_name);
        uEmail = (AutoCompleteTextView) getView().findViewById(R.id.email);
        uPass = (EditText) getView().findViewById(R.id.et_password);
        ucPass = (EditText) getView().findViewById(R.id.et_password_confirm);
        uAddress = (EditText) getView().findViewById(R.id.et_address);
        uPhone = (EditText) getView().findViewById(R.id.et_phone);


        uPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.btn_login || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        bloodGroupSelector = (Spinner) getView().findViewById(R.id.sp_blood_group);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.blood_groups, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        bloodGroupSelector.setAdapter(adapter);
        bloodGroupSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View arg1,
                                       int pos, long id) {
                Toast.makeText(getActivity(), "You selected " + bloodGroupSelector.getSelectedItem(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> spinner) {
                Toast.makeText(getActivity(), "Nothing selected.", Toast.LENGTH_LONG).show();
            }
        });
        signup = (Button) getView().findViewById(R.id.btn_register);
        signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == signup) {
            attemptRegister();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        uEmail.setError(null);
        uPass.setError(null);

        // Store values at the time of the login attempt.
        String email = uEmail.getText().toString();
        String password = uPass.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            uEmail.setError(getString(R.string.error_field_required));
            focusView = uEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            uEmail.setError(getString(R.string.error_invalid_email));
            focusView = uEmail;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            uPass.setError(getString(R.string.error_field_required));
            focusView = uPass;
            cancel = true;
        } else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            uPass.setError(getString(R.string.error_invalid_password));
            focusView = uPass;
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
                showProgress(true);

                mAuthTask = new UserRegisterTask(email, password);
                mAuthTask.execute((Void) null);
            } else {
                Crouton.makeText(getActivity(), getResources().getString(R.string.no_internet), Style.ALERT).show();
            }
        }
    }

    /**
     * Shows the progress
     */
    public void showProgress(final boolean show) {
        if (show)
            progressDialog = ProgressDialog.show(getActivity(), "", "Authenticating...");
        else
            progressDialog.dismiss();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserRegisterTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("email", mEmail));
            nameValuePair.add(new BasicNameValuePair("passcode", mPassword));
            String responeStatus = null;
            String userName = null;
            String userEmail = null;
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(RegisterFragment.url, ServiceHandler.POST, nameValuePair);
            //Log.d("Login Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    loginResponse = jsonObj.getJSONObject(TAG_RESPONSE);
                    responeStatus = loginResponse.getString(TAG_STATUS);
                    userName = loginResponse.getString(TAG_USERNAME);
                    userEmail = loginResponse.getString(TAG_EMAIL);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            if (responeStatus.equals("success")) {
                userPrefs.createUserSession(userName, userEmail);
                return true;
            } else
                return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                gotoLogin();
                getActivity().finish();
            } else {
                uPass.setError(getString(R.string.error_incorrect_password));
                uPass.requestFocus();
            }
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void gotoLogin() {
    }

}
