package com.android.life.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.life.Application;
import com.android.life.Helpers.ServiceHandler;
import com.android.life.Helpers.UserPreferenceManager;
import com.android.life.R;
import com.android.life.models.User;
import com.android.life.utils.NetworkUtil;
import com.android.life.utils.ValidationUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Nag on 23/07/15.
 */
public class ProfileFragment extends Fragment {

    private Listener listener;
    UserPreferenceManager userPrefs;
    private ProfileUpdateTask updateTask = null;
    ImageLoader imageLoader = Application.getInstance().getImageLoader();
    private static final String imagePath = "http://medi.orgfree.com/profile_pics/";

    // UI references.
    private EditText oldPass, uName, newPass, uAddress, uPhone;
    private String blood_group;
    private ProgressDialog progressDialog;
    private Button update;
    private Spinner bloodGroupSelector;
    private NetworkImageView profilePic;
    private Uri fileUri;
    String picturePath;
    Uri selectedImage;
    Bitmap photo;
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String KEY_USER_ID = "user_id";

    // JSON Node names
    private static final String TAG_RESPONSE = "response";
    private static final String TAG_STATUS = "status";
    private static String URL = "http://medi.orgfree.com/updateUser.php";
    private static String UPLOAD_URL = "http://medi.orgfree.com/upload.php";

    ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();

    public interface Listener {
        //public void gotoLoginFrag();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (Listener) activity;
            Log.d("Fragment: ", "onAttach");
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ProfileFragment.Listener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Fragment: ", "onCreateView");
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
        if (imageLoader == null)
            imageLoader = Application.getInstance().getImageLoader();
        // Set up the login form.
        uName = (EditText) getView().findViewById(R.id.et_name);
        oldPass = (EditText) getView().findViewById(R.id.et_old_password);
        newPass = (EditText) getView().findViewById(R.id.et_new_password);
        uAddress = (EditText) getView().findViewById(R.id.et_address);
        uPhone = (EditText) getView().findViewById(R.id.et_phone);
        update = (Button) getView().findViewById(R.id.btn_update);
        profilePic = (NetworkImageView) getView().findViewById(R.id.imgv_profile);

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
        profilePic.setImageUrl(imagePath.concat(userPrefs.getUserDetails().getProfile_pic()), imageLoader);
        profilePic.setDefaultImageResId(R.drawable.default_avatar);
        profilePic.setErrorImageResId(R.drawable.default_avatar);

        //mySpinner.setSelection(arrayAdapter.getPosition("Category 2"));

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptUpdateProfile();
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProfilePic();
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
        } else if (!TextUtils.isEmpty(cPassword) && !TextUtils.isEmpty(nPassword) && !isPasswordValid(nPassword)) {
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
        } else
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
            String jsonStr = sh.makeServiceCall(ProfileFragment.URL, ServiceHandler.POST, postParams);
            Log.d("Login Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    registerResponse = jsonObj.getJSONObject(TAG_RESPONSE);
                    responeStatus = registerResponse.getString(TAG_STATUS);
                    if (responeStatus.equals("fail"))
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

            if ($error != null) {
                Crouton.makeText(getActivity(), $error, Style.ALERT).show();
            } else if (resp.equals(getResources().getString(R.string.success))) {
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

    private void setProfilePic() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    takePhoto();
                } else if (items[item].equals("Choose from Library")) {
                    pickPhoto();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void takePhoto() {
        // Check Camera
        if (getActivity().getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, 100);
        } else {
            Toast.makeText(getActivity().getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }

    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), 200);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && data != null) {
            if (requestCode == 200)
                onSelectFromGalleryResult(data);
            else if (requestCode == 100)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        photo = (Bitmap) data.getExtras().get("data");
        uploadImage();
        //profilePic.setImageBitmap(photo);

        // Find the SD Card path
        //File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        //File dir = new File(filepath.getAbsolutePath());
        //dir.mkdirs();

        // Create a name for the saved image
        //File file = new File(dir, System.currentTimeMillis() + ".jpg");

        // Show a toast message on successful save
        Toast.makeText(getActivity(), "Photo captured!", Toast.LENGTH_LONG).show();

        //OutputStream output;
        try {
            //output = new FileOutputStream(file);
            // Compress into png format image from 0% - 100%
            //photo.compress(Bitmap.CompressFormat.PNG, 50, output);
            //output.flush();
            //output.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }

    }

    private void onSelectFromGalleryResult(Intent data) {
        selectedImage = data.getData();

        // Cursor to get image uri to display
        /*
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        picturePath = cursor.getString(columnIndex);
        cursor.close();

        photo = BitmapFactory.decodeFile(picturePath);

        profilePic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        profilePic.setImageBitmap(photo);
        */
        try {
            //Getting the Bitmap from Gallery
            photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        uploadImage();
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }


    private void uploadImage() {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Uploading...", "Please wait...", false, false);
        final long timeStamp = System.currentTimeMillis();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        updateProfilePic(timeStamp);
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                        Log.d("Success-Response: ", s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getActivity(), "Uploading failed. Please try again", Toast.LENGTH_LONG).show();
                        Log.d("Error-Response: ", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(photo);

                //Getting Image Name
                String name = timeStamp + ".png";

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_USER_ID, String.valueOf(userPrefs.getUserId()));
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, name);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Application.getRequestQueue();

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void updateProfilePic(long imageName){
        //Setting the Bitmap to ImageView
        String photoName = imageName + ".png";
        profilePic.setImageUrl(imagePath + photoName, imageLoader);
        userPrefs.updateProfilePic(photoName);
    }
}