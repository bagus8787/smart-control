package com.example.smart_control.handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smart_control.model.AlarmModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    // static variable
    private static final int DATABASE_VERSION = 2;

    // Database name
    private static final String DATABASE_NAME = "db_coba";

    // table name
    private static final String TABLE_TALL = "talls";

    // column tables
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "user";
    private static final String KEY_TALL = "tall";
    private static final String KEY_TIME = "time";
    private static final String KEY_COUNT = "count";
    private static final String KEY_OLD_TIME = "oldtime";
    private static final String KEY_DAY = "day";
    private static final String KEY_STATUS = "status";

    private static DatabaseHandler sInstance;

    public static synchronized DatabaseHandler getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TALL + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_TALL + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_COUNT + " TEXT,"
                + KEY_OLD_TIME + " TEXT,"
                + KEY_DAY + " TEXT,"
                + KEY_STATUS + " TEXT"
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
    public void addRecord(AlarmModel alarmModel){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, alarmModel.getName());
        values.put(KEY_TALL, alarmModel.getTall());
        values.put(KEY_TIME, alarmModel.getTime());
        values.put(KEY_COUNT, alarmModel.getCount());
        values.put(KEY_OLD_TIME, alarmModel.getOld_time());
        values.put(KEY_DAY, alarmModel.getOld_time());
        values.put(KEY_STATUS, "On");

        db.insert(TABLE_TALL, null, values);
        db.close();
    }

    public AlarmModel getContact (int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TALL, new String[]{
                KEY_ID,
                KEY_NAME,
                KEY_TALL ,
                KEY_TIME ,
                KEY_COUNT ,
                KEY_OLD_TIME,
                KEY_DAY,
                KEY_STATUS},
                KEY_ID + "=?",
                new String[] {
                        String.valueOf(id)},
                null,
                null,
                null,
                null);
        if (cursor != null)
            cursor.moveToFirst();

        AlarmModel contact = new AlarmModel(Integer.parseInt(
                cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7));
        //return contact
        return contact;
        }

//      //get All Record
    public ArrayList<AlarmModel> getAllRecord(){
        ArrayList<AlarmModel> modelList = new ArrayList<AlarmModel>();
        //Select All Query
        String selectQuery  = "SELECT * FROM " + TABLE_TALL;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do {
                AlarmModel alarmModel = new AlarmModel();

                alarmModel.setId(Integer.parseInt(cursor.getString(0)));
                alarmModel.setName(cursor.getString(1));
                alarmModel.setTall(cursor.getString(2));
                alarmModel.setTime(cursor.getString(3));
                alarmModel.setCount(cursor.getString(4));
                alarmModel.setOld_time(cursor.getString(5));
                alarmModel.setDay(cursor.getString(6));
                alarmModel.setStatus(cursor.getString(7));

                modelList.add(alarmModel);
            } while (cursor.moveToNext());
        }

//        if(cursor!=null && cursor.getCount() > 0)
//        {
//            if (cursor.moveToFirst()){
//                do {
//                    DbModel dbModel = new DbModel();
//
//                    dbModel.setId(Integer.parseInt(cursor.getString(0)));
//                    dbModel.setName(cursor.getString(1));
//                    dbModel.setTall(cursor.getString(2));
//                    dbModel.setTime(cursor.getString(3));
////                    dbModel.setCount(cursor.getString(4));
////                    dbModel.setOld_time(cursor.getString(5));
//
//                    modelList.add(dbModel);
//                } while (cursor.moveToNext());
//            }
//        }

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
    public int updateContact (AlarmModel alarmModel){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, alarmModel.getName());
        values.put(KEY_TALL, alarmModel.getTall());
        values.put(KEY_TIME, alarmModel.getTime());
        values.put(KEY_COUNT, alarmModel.getCount());
        values.put(KEY_OLD_TIME, alarmModel.getOld_time());

        //Update
        return db.update(TABLE_TALL, values, KEY_ID + " = ? ",
                new String[]{
                        String.valueOf(alarmModel.getId())
                });
    }

    //Delete Data
    public void deleteModel(AlarmModel alarmModel){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TALL, KEY_ID + " = ? ",
                new String[]{
                        String.valueOf(alarmModel.getId())
                });

        db.close();
    }
}
