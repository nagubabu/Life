package com.android.life;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.life.Helpers.UserPreferenceManager;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class MainActivity extends GlobalActivity {

    UserPreferenceManager userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userPrefs = new UserPreferenceManager(this);

        checkLoginStatus();

        populateListView();
        registerClickCallback();
    }

    private void checkLoginStatus() {
        userPrefs.checkLogin();
    }

    private void populateListView() {
        // Create list items
        String[] donors = {"Raja", "Nag", "Sudhakar", "Karthik", "Prathap", "Chandra", "Namrata", "Vinay", "Ganesh", "Paramesh", "Dileepan", "Mohith", "Sharma", "Shrikanth", "Ramesh", "Ramakrishna", "Raja", "Nag", "Sudhakar", "Karthik", "Prathap", "Chandra", "Namrata", "Vinay", "Ganesh", "Paramesh", "Dileepan", "Mohith", "Sharma", "Shrikanth", "Ramesh", "Ramakrishna"};

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


}
