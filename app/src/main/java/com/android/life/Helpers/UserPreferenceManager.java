package com.android.life.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.life.LoginActivity;
import com.android.life.models.User;

/**
 * Created by Nag on 6/18/15.
 */
public class UserPreferenceManager {
    // Shared Preferences reference
    SharedPreferences pref;

    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREFER_NAME = "LifeAppPrefs";

    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    public static final String KEY_USER_ID = "userID";
    public static final String KEY_NAME = "userName";
    public static final String KEY_EMAIL = "userEmail";
    public static final String KEY_BLOOD_GROUP = "userBloodGroup";
    public static final String KEY_ADDRESS = "userAddress";
    public static final String KEY_PHONE = "userPhone";

    public UserPreferenceManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Create login session
    public void createUserSession(int id, String name, String email, String bloodGroup, String address, String phone){
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_BLOOD_GROUP, bloodGroup);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_PHONE, phone);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     * */
    public boolean checkLogin(){
        // Check login status
        if(!this.isUserLoggedIn()){

            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);

            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

            return true;
        }
        return false;
    }



    /**
     * Get stored session data
     * */
    public User getUserDetails(){

        //Use User class object to store user credentials
        User user = new User();
        user.setUserID(pref.getInt(KEY_USER_ID, 0));
        user.setName(pref.getString(KEY_NAME, null));
        user.setEmail(pref.getString(KEY_EMAIL, null));
        user.setBloodGroup(pref.getString(KEY_BLOOD_GROUP, null));
        user.setAddress(pref.getString(KEY_ADDRESS, null));
        user.setPhone(pref.getString(KEY_PHONE, null));

        // return user
        return user;
    }

    public int getUserId(){
        return pref.getInt(KEY_USER_ID, 0);
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }


    // Check for login
    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}
