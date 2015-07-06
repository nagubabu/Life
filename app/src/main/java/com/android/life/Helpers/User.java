package com.android.life.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nag on 6/30/15.
 */
public class User {

    public String name;

    public String email;

    public String blood_group;

    public String address;

    public String phone;

    public int id;

    public User(){

    }


    public User(JSONObject object) {

        try {
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

            if (object.has("userId"))
                this.id = object.getInt("userId");


        } catch (JSONException e) {

            e.printStackTrace();

        }

    }

    public User(int id, String name, String email, String blood_group, String address, String phone){
        this.id = id;
        this.name = name;
        this.email = email;
        this.blood_group = blood_group;
        this.address = address;
        this.phone = phone;
    }

    public void setID(int id){
        this.id = id;
    }

    public int getID(){
        return this.id;
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


}
