package com.android.life.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.life.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nag on 7/6/15.
 */
public class UserDbManager extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "db_lifeSaver";

    // Contacts table name
    private static final String TABLE_USERS = "users";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_BLOOD_GROUP = "blood_group";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PROFILE_PIC = "profile_pic";
    private static final String KEY_STATUS = "status";
    private static final String KEY_CREATED = "created";
    private static final String KEY_UPDATED = "updated";

    public UserDbManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_BLOOD_GROUP + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_PROFILE_PIC + " TEXT,"
                + KEY_STATUS + " TEXT,"
                + KEY_CREATED + " DATETIME,"
                + KEY_UPDATED + " DATETIME )";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getUserID());
        values.put(KEY_NAME, user.getName());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_BLOOD_GROUP, user.getBloodGroup());
        values.put(KEY_ADDRESS, user.getAddress());
        values.put(KEY_PHONE, user.getPhone());
        values.put(KEY_PROFILE_PIC, user.getProfile_pic());
        values.put(KEY_STATUS, user.getStatus());
        values.put(KEY_CREATED, user.getCreated());
        values.put(KEY_UPDATED, user.getUpdated());

        // Inserting Row
        db.insert(TABLE_USERS, null, values);
        db.close(); // Closing database connection
    }

    // Updating single user
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_BLOOD_GROUP, user.getBloodGroup());
        values.put(KEY_ADDRESS, user.getAddress());
        values.put(KEY_PHONE, user.getPhone());
        values.put(KEY_STATUS, user.getStatus());
        values.put(KEY_CREATED, user.getCreated());
        values.put(KEY_UPDATED, user.getUpdated());
        // updating row
        return db.update(TABLE_USERS, values, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.getUserID())});
    }

    // Deleting single contact
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.getUserID())});
        db.close();
    }

    // Getting single user
    public User getUser(int user_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        //String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE " + KEY_USER_ID + " = " + user_id;

        //Cursor cursor = db.rawQuery(selectQuery, null);

        Cursor cursor = db.query(TABLE_USERS, null, KEY_USER_ID + "=?", new String[]{String.valueOf(user_id)}, null, null, null, null);

        User user = new User();

        if (cursor != null) {
            cursor.moveToFirst();
            user.setUserID(Integer.parseInt(cursor.getString(1)));
            user.setName(cursor.getString(2));
            user.setEmail(cursor.getString(3));
            user.setBloodGroup(cursor.getString(4));
            user.setAddress(cursor.getString(5));
            user.setPhone(cursor.getString(6));
            user.setProfile_pic(cursor.getString(7));
            user.setStatus(cursor.getString(8));
            user.setCreated(cursor.getString(9));
            user.setUpdated(cursor.getString(10));
        }
        db.close();
        // return user object
        return user;
    }

    // Get latest created date
    public String getLatestCreatedDate(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT created FROM " + TABLE_USERS + " ORDER BY " + KEY_CREATED + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        String created = null;
        if (cursor != null && cursor.moveToFirst()) {
            created = cursor.getString(0);
            Log.d("Created:", cursor.getString(0));
        }else{
            created = "0000-00-00 00:00:00";
        }
        return created;
    }

    // Get latest created date
    public String getLatestUpdatedDate(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT updated FROM " + TABLE_USERS + " ORDER BY " + KEY_UPDATED + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        String updated = null;
        if (cursor != null && cursor.moveToFirst()) {
            updated = cursor.getString(0);
        }
        return updated;
    }

    // Getting All Users
    public ArrayList<User> getAllUsers() {
        ArrayList<User> userList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " ORDER BY name ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setUserID(Integer.parseInt(cursor.getString(1)));
                user.setName(cursor.getString(2));
                user.setEmail(cursor.getString(3));
                user.setBloodGroup(cursor.getString(4));
                user.setAddress(cursor.getString(5));
                user.setPhone(cursor.getString(6));
                user.setProfile_pic(cursor.getString(7));
                user.setStatus(cursor.getString(8));
                user.setCreated(cursor.getString(9));
                user.setUpdated(cursor.getString(10));
                // Adding user to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        db.close();
        // return user list
        return userList;
    }

    // Getting users Count
    public int getUsersCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // deletes all records
    public void deleteRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_USERS);
        db.close();
    }


    // insert data using transaction and prepared statement
    public void insertUsers(JSONArray users) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        for (int i = 0; i < users.length(); i++) {
            JSONObject c = null;

            try {
                c = users.getJSONObject(i);
                User newUser = new User(c);

                String insertSql = "INSERT INTO " + TABLE_USERS + " ( " + KEY_USER_ID + ", " + KEY_NAME + ", " + KEY_EMAIL + ", " + KEY_BLOOD_GROUP + ", " + KEY_ADDRESS + ", " + KEY_PHONE + ", " + KEY_PROFILE_PIC + ", " + KEY_STATUS + ", " + KEY_CREATED + ", " + KEY_UPDATED + " ) " +
                        "VALUES ( " +
                        "'" + newUser.getUserID() + "'," +
                        "'" + newUser.getName() + "'," +
                        "'" + newUser.getEmail() + "'," +
                        "'" + newUser.getBloodGroup() + "'," +
                        "'" + newUser.getAddress() + "'," +
                        "'" + newUser.getPhone() + "'," +
                        "'" + newUser.getProfile_pic() + "'," +
                        "'" + newUser.getStatus() + "'," +
                        "'" + newUser.getCreated() + "'," +
                        "'" + newUser.getUpdated() + "'" +
                        ")";
                Log.d("Action-Inser-Query: ", insertSql);
                db.execSQL(insertSql);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    // insert data using transaction and prepared statement
    public void updateUsers(JSONArray users) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        for (int i = 0; i < users.length(); i++) {
            JSONObject c = null;

            try {
                c = users.getJSONObject(i);
                User newUser = new User(c);

                String updateSql = "UPDATE " + TABLE_USERS + " SET " +
                        KEY_NAME + "='"+ newUser.getName() +"', " +
                        KEY_EMAIL + "='"+ newUser.getEmail() +"', " +
                        KEY_BLOOD_GROUP + "='"+ newUser.getBloodGroup() +"', " +
                        KEY_ADDRESS + "='"+ newUser.getAddress() +"', " +
                        KEY_PHONE + "='"+ newUser.getPhone() +"', " +
                        KEY_PROFILE_PIC + "='"+ newUser.getProfile_pic() +"', " +
                        KEY_STATUS + "='"+ newUser.getStatus() +"', " +
                        KEY_UPDATED + "='"+ newUser.getUpdated() +"' " +
                        "WHERE " + KEY_USER_ID + "='"+ newUser.getUserID() +"'";

                Log.d("Action-Update-Query: ", updateSql);
                db.execSQL(updateSql);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
}