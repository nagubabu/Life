package com.android.life.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
                + KEY_PHONE + " TEXT" + ")";
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
        values.put(KEY_USER_ID, user.user_id);
        values.put(KEY_NAME, user.name);
        values.put(KEY_EMAIL, user.email);
        values.put(KEY_BLOOD_GROUP, user.blood_group);
        values.put(KEY_ADDRESS, user.address);
        values.put(KEY_PHONE, user.phone);

        // Inserting Row
        db.insert(TABLE_USERS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    User getUser(int user_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_USER_ID,
                        KEY_NAME, KEY_EMAIL, KEY_BLOOD_GROUP, KEY_ADDRESS, KEY_PHONE}, KEY_USER_ID + "=?",
                new String[]{String.valueOf(user_id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        User user = new User(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        // return contact
        return user;
    }

    // Getting All Users
    public List<User> getAllUsers() {
        ArrayList<User> userList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;

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
                // Adding user to list
                userList.add(user);
            } while (cursor.moveToNext());
        }

        // return user list
        return userList;
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


    // Getting users Count
    public int getUsersCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // deletes all records
    public void deleteRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_USERS);
        db.close();
    }

    // insert data using transaction and prepared statement
    public void insertOrReplaceUser(JSONArray users) {

        // you can use INSERT only
        String sql = "INSERT OR REPLACE INTO " + TABLE_USERS + " ( " + KEY_USER_ID + ", " + KEY_NAME + ", " + KEY_EMAIL + ", " + KEY_BLOOD_GROUP + ", " + KEY_ADDRESS + ", " + KEY_PHONE + " ) " +
                "VALUES ( ?, ?, ?, ?, ?, ? )";

        SQLiteDatabase db = this.getWritableDatabase();
        /*
         * According to the docs http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html
         * Writers should use beginTransactionNonExclusive() or beginTransactionWithListenerNonExclusive(SQLiteTransactionListener)
         * to start a transaction. Non-exclusive mode allows database file to be in readable by other threads executing queries.
         */
        db.beginTransactionNonExclusive();
        // db.beginTransaction();

        SQLiteStatement stmt = db.compileStatement(sql);

        for (int i = 0; i < users.length(); i++) {
            JSONObject c = null;

            try {
                c = users.getJSONObject(i);
                User newUser = new User(c);
                int user_id = newUser.getUserID();

                stmt.bindString(1, String.valueOf(newUser.getUserID()));
                stmt.bindString(2, newUser.getName());
                stmt.bindString(3, newUser.getEmail());
                stmt.bindString(4, newUser.getBloodGroup());
                stmt.bindString(5, newUser.getAddress());
                stmt.bindString(6, newUser.getPhone());

                stmt.execute();
                stmt.clearBindings();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();
    }
}
