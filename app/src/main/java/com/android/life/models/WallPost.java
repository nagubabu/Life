package com.android.life.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mokriya on 18/08/15.
 */
public class WallPost {

    public int user_id;
    public String name;
    public String message;
    public String profile_pic;
    public String status;
    public String posted_on;

    public WallPost(){}

    public WallPost(JSONObject object){

            try {

                if (object.has("user_id"))
                    this.user_id = object.getInt("user_id");

                if (object.has("name"))
                    this.name = object.getString("name");

                if (object.has("message"))
                    this.message = object.getString("message");

                if (object.has("profile_pic"))
                    this.profile_pic = object.getString("profile_pic");

                if (object.has("status"))
                    this.status = object.getString("status");

                if (object.has("posted_on"))
                    this.posted_on = object.getString("posted_on");

            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void setUserID(int user_id){
        this.user_id = user_id;
    }

    public int getUserID(){
        return this.user_id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }

    public String getProfile_pic(){
        return this.profile_pic;
    }

    public void setProfile_pic(String profile_pic){
        this.profile_pic = profile_pic;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return this.status;
    }

    public void setPostedOn(String posted_on){
        this.posted_on = posted_on;
    }

    public String getPostedOn(){
        return this.posted_on;
    }
}
