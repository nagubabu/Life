package com.android.life.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.life.Helpers.ServiceHandler;
import com.android.life.models.User;
import com.android.life.Helpers.UserPreferenceManager;
import com.android.life.R;
import com.android.life.utils.NetworkUtil;
import com.android.life.utils.ValidationUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Nag on 23/07/15.
 */
public class ProfileFragment extends Fragment {
    
    private Listener listener;
    UserPreferenceManager userPrefs;
    private ProfileUpdateTask updateTask = null;

    // UI references.
    private EditText oldPass, uName, newPass, uAddress, uPhone;
    private String blood_group;
    private ProgressDialog progressDialog;
    private Button update;
    private Spinner bloodGroupSelector;

    // JSON Node names
    private static final String TAG_RESPONSE = "response";
    private static final String TAG_STATUS = "status";
    private static String url = "http://medi.orgfree.com/updateUser.php";

    ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();

    public interface Listener {
        //public void gotoLoginFrag();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ProfileFragment.Listener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater
                .inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        userPrefs = new UserPreferenceManager(getActivity());
        // Set up the login form.
        uName = (EditText) getView().findViewById(R.id.et_name);
        oldPass = (EditText) getView().findViewById(R.id.et_old_password);
        newPass = (EditText) getView().findViewById(R.id.et_new_password);
        uAddress = (EditText) getView().findViewById(R.id.et_address);
        uPhone = (EditText) getView().findViewById(R.id.et_phone);
        update = (Button) getView().findViewById(R.id.btn_update);

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
                //Toast.makeText(getActivity(), "You selected " + bloodGroupSelector.getSelectedItem(), Toast.LENGTH_LONG).show();
                blood_group = bloodGroupSelector.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> spinner) {
                Toast.makeText(getActivity(), "Nothing selected.", Toast.LENGTH_LONG).show();
            }
        });

        User userDetails = userPrefs.getUserDetails();
        uName.setText(userDetails.getName());
        uAddress.setText(userDetails.getAddress());
        uPhone.setText(userDetails.getPhone());
        bloodGroupSelector.setSelection(adapter.getPosition(userDetails.getBloodGroup()));

        //mySpinner.setSelection(arrayAdapter.getPosition("Category 2"));

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptUpdateProfile();
            }
        });
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    public void attemptUpdateProfile() {
        if (updateTask != null) {
            return;
        }

        // Reset errors.
        uName.setError(null);
        uAddress.setError(null);
        uPhone.setError(null);
        oldPass.setError(null);
        newPass.setError(null);

        // Store values at the time of the login attempt.
        String name = uName.getText().toString();
        String cPassword = oldPass.getText().toString();
        String nPassword = newPass.getText().toString();
        String address = uAddress.getText().toString();
        String phone = uPhone.getText().toString();


        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(name)) {
            uName.setError(getString(R.string.error_field_required));
            focusView = uName;
            cancel = true;
        } else if (name.length() < 3) {
            uName.setError(getString(R.string.error_name_length));
            focusView = uName;
            cancel = true;
        } else if (TextUtils.isEmpty(address)) {
            uAddress.setError(getString(R.string.error_field_required));
            focusView = uAddress;
            cancel = true;
        } else if (TextUtils.isEmpty(phone)) {
            uPhone.setError(getString(R.string.error_field_required));
            focusView = uPhone;
            cancel = true;
        } else if (!ValidationUtil.isValid(ValidationUtil.Type.PHONE, phone)) {
            uPhone.setError(getString(R.string.error_invalid_phone));
            focusView = uPhone;
            cancel = true;
        }else if (!TextUtils.isEmpty(cPassword) && !TextUtils.isEmpty(nPassword) && !isPasswordValid(nPassword)) {
            newPass.setError(getString(R.string.error_invalid_password));
            focusView = newPass;
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

                postParams.add(new BasicNameValuePair("id", String.valueOf(userPrefs.getUserId())));
                postParams.add(new BasicNameValuePair("name", name));
                postParams.add(new BasicNameValuePair("cpasscode", cPassword));
                postParams.add(new BasicNameValuePair("npasscode", nPassword));
                postParams.add(new BasicNameValuePair("blood_group", blood_group));
                postParams.add(new BasicNameValuePair("contact", phone));
                postParams.add(new BasicNameValuePair("address", address));

                updateTask = new ProfileUpdateTask();
                updateTask.execute((Void) null);
            } else {
                Crouton.makeText(getActivity(), getResources().getString(R.string.no_internet), Style.ALERT).show();
            }
        }
    }

    /**
     * Shows the progress
     */
    public void showProgress(final boolean show) {
        if (show) {
            if (progressDialog != null) {
                progressDialog = null;
            }
            progressDialog = ProgressDialog.show(getActivity(), "", "Updating...");
        }else
            progressDialog.dismiss();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class ProfileUpdateTask extends AsyncTask<Void, Void, String> {

        String $error;

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            JSONObject registerResponse = null;
            String responeStatus = null;
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(ProfileFragment.url, ServiceHandler.POST, postParams);
            Log.d("Login Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    registerResponse = jsonObj.getJSONObject(TAG_RESPONSE);
                    responeStatus = registerResponse.getString(TAG_STATUS);
                    if(responeStatus.equals("fail"))
                        $error = registerResponse.getString("message");

                } catch (JSONException e) {
                    e.printStackTrace();
                    $error = e.getMessage();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            if ($error != null && !$error.isEmpty()) {
                return $error;
            } else if (responeStatus.equals("success")) {
                return getResources().getString(R.string.success);
            } else
                return getResources().getString(R.string.fail);
        }

        @Override
        protected void onPostExecute(final String resp) {
            updateTask = null;

            if($error != null) {
                Crouton.makeText(getActivity(), $error, Style.ALERT).show();
            }else if (resp.equals(getResources().getString(R.string.success))) {
                Crouton.makeText(getActivity(), getString(R.string.update_success), Style.CONFIRM).show();
            } else {
                Crouton.makeText(getActivity(), getString(R.string.update_failed), Style.ALERT).show();
            }
            oldPass.setText("");
            newPass.setText("");
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            updateTask = null;
            showProgress(false);
        }
    }

}
