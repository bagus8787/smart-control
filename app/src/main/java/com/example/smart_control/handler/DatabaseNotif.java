package com.example.smart_control.handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smart_control.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class DatabaseNotif extends SQLiteOpenHelper {
    // static variable
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "db_notif";

    // table name
    private static final String TABLE_TALL = "talls";

    // column tables
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";

    private static DatabaseNotif sInstance;

    public static synchronized DatabaseNotif getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseNotif(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseNotif(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TALL + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_BODY + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // on Upgrade database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TALL);
        onCreate(db);
    }

    //    addData
    public void addRecord(Notification notification){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, notification.getTitle());
        values.put(KEY_BODY, notification.getBody());

        db.insert(TABLE_TALL, null, values);
        db.close();
    }

    public Notification getContact (int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TALL, new String[]{
                        KEY_ID,
                        KEY_TITLE,
                        KEY_BODY },
                KEY_ID + "=?",
                new String[] {
                        String.valueOf(id)},
                null,
                null,
                null,
                null);
        if (cursor != null)
            cursor.moveToFirst();

        Notification contact = new Notification(Integer.parseInt(
                cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2));
        //return contact
        return contact;
    }

    //      //get All Record
    public ArrayList<Notification> getAllRecord(){
        ArrayList<Notification> modelList = new ArrayList<Notification>();
        //Select All Query
        String selectQuery  = "SELECT * FROM " + TABLE_TALL;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do {
                Notification notification = new Notification();

                notification.setId(Integer.parseInt(cursor.getString(0)));
                notification.setTitle(cursor.getString(1));
                notification.setBody(cursor.getString(2));

                modelList.add(notification);
            } while (cursor.moveToNext());
        }
        return modelList;
    }

    //get Total
    public int getDbModelCount(){
        String countQuery = "SELECT * FROM " + TABLE_TALL;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    //Update Data
    public int updateContact (Notification notification){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, notification.getTitle());
        values.put(KEY_BODY, notification.getBody());

        //Update
        return db.update(TABLE_TALL, values, KEY_ID + " = ? ",
                new String[]{
                        String.valueOf(notification.getId())
                });
    }

    //Delete Data
    public void deleteModel(Notification notification){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TALL, KEY_ID + " = ? ",
                new String[]{
                        String.valueOf(notification.getId())
                });

        db.close();
    }
}
