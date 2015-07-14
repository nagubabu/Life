package com.android.life;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.life.Helpers.UserPreferenceManager;
import com.android.life.fragments.LoginFragment;
import com.android.life.fragments.RegisterFragment;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoginFragment.Listener, RegisterFragment.Listener {

    UserPreferenceManager userPrefs;
    LoginFragment loginFragment;
    RegisterFragment registerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userPrefs = new UserPreferenceManager(this);
        if (userPrefs.isUserLoggedIn()) {
            Log.d("Login status: ", "User already logged in");
            gotoHome();
        }
        setContentView(R.layout.activity_login);

        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();

        // Add new fragment, loginFragment for the first time
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_holder, loginFragment);
        fragmentTransaction.commit();
    }

    private void gotoHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public void gotoRegisterFrag() {
        replaceFragment(registerFragment);
    }


    private void replaceFragment(Fragment fragment) {
        /*
        String backStateName = fragment.getClass().getName();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, fragment);
        fragmentTransaction.addToBackStack(backStateName);
        fragmentTransaction.commit();
        */
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_holder, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        } else {
            Toast.makeText(getApplicationContext(), fragmentTag + " is already in backstack", Toast.LENGTH_SHORT).show();
        }
    }
}

