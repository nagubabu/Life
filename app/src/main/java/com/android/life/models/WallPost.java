package com.android.life.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mokriya on 18/08/15.
 */
public class WallPost {

    public int user_id;
    public String message;
    public String status;
    public String posted_on;

    public WallPost(){}

    public WallPost(JSONObject object){

            try {

                if (object.has("userId"))
                    this.user_id = object.getInt("userId");

                if (object.has("message"))
                    this.message = object.getString("message");

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

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
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
