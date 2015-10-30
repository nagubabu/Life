package com.android.life.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nag on 6/30/15.
 */
public class User {

    public int user_id;
    public String name;
    public String email;
    public String blood_group;
    public String address;
    public String phone;
    public String image_url;
    public String status;
    public String created;
    public String updated;



    public User(){

    }


    public User(JSONObject object) {

        try {
            if (object.has("userId"))
                this.user_id = object.getInt("userId");

            if (object.has("name"))
                this.name = object.getString("name");

            if (object.has("email"))
                this.email = object.getString("email");

            if (object.has("bloodGroup"))
                this.blood_group = object.getString("bloodGroup");

            if (object.has("address"))
                this.address = object.getString("address");

            if (object.has("contact"))
                this.phone = object.getString("contact");

            if (object.has("status"))
                this.status = object.getString("status");

            if (object.has("created"))
                this.created = object.getString("created");

            if (object.has("updated"))
                this.updated = object.getString("updated");

        } catch (JSONException e) {

            e.printStackTrace();

        }

    }

    public User(int user_id, String name, String email, String blood_group, String address, String phone, String status, String created, String updated){
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.blood_group = blood_group;
        this.address = address;
        this.phone = phone;
        this.status = status;
        this.created = created;
        this.updated = updated;
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

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return this.email;
    }

    public void setBloodGroup(String blood_group){
        this.blood_group = blood_group;
    }

    public String getBloodGroup(){
        return this.blood_group;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return this.address;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getPhone(){
        return this.phone;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return this.status;
    }

    public void setCreated(String created){
        this.created = created;
    }

    public String getCreated(){
        return this.created;
    }

    public void setUpdated(String updated){
        this.updated = updated;
    }

    public String getUpdated(){
        return this.updated;
    }


}
