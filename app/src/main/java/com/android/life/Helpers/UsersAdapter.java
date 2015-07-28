package com.android.life.Helpers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.life.R;
import com.android.life.models.User;

import java.util.ArrayList;

/**
 * Created by Nag on 6/30/15.
 */
public class UsersAdapter extends ArrayAdapter<User> {

    public UsersAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        User user = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_donor, parent, false);

        }

        // Lookup view for data population

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_donor);

        TextView tvBloodGroup = (TextView) convertView.findViewById(R.id.tv_bgroup);

        TextView tvUserId = (TextView) convertView.findViewById(R.id.tv_userId);

        // Populate the data into the template view using the data object

        tvName.setText(user.getName());
        tvName.setTextColor(Color.BLACK);

        tvBloodGroup.setText(user.getBloodGroup());
        tvBloodGroup.setTextColor(Color.BLACK);

        tvUserId.setText(String.valueOf(user.getUserID()));
        tvUserId.setTextColor(Color.BLACK);

        // Return the completed view to render on screen

        return convertView;

    }
}
