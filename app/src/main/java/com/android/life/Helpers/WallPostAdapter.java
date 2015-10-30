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
import com.android.life.models.WallPost;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nag on 18/08/15.
 */
public class WallPostAdapter extends ArrayAdapter<WallPost> {

    UserDbManager userDbManager;

    public WallPostAdapter(Context context, ArrayList<WallPost> wallPosts) {
        super(context, 0, wallPosts);
        userDbManager = new UserDbManager(getContext());
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        WallPost wallPost = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_post, parent, false);
        }

        // Lookup view for data population
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tv_userName);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_date);
        TextView tvMessage = (TextView) convertView.findViewById(R.id.tv_message);

        User user = userDbManager.getUser(wallPost.getUserID());


        // Populate the data into the template view using the data object
        tvUserName.setText(user.getName());
        tvUserName.setTextColor(Color.BLACK);

        tvDate.setText(parseDateToddMMyyyy(wallPost.getPostedOn()));
        tvDate.setTextColor(Color.BLACK);

        tvMessage.setText(wallPost.getMessage());
        tvMessage.setTextColor(Color.BLACK);

        // Return the completed view to render on screen
        return convertView;

    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

}
