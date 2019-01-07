package com.example.wyatttowne.freezetrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Create thread here to update times of all items in database -> run on create database!!!

public class FreezeDatabaseHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "freeze";
    private static final int DB_VERSION = 1;

    FreezeDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    public void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        if(oldVersion <= 1) {
            db.execSQL("CREATE TABLE ITEM (_id INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, DESCRIPTION TEXT, START_DATE TEXT, END_DATE TEXT, IMAGE BLOB);");
            db.execSQL("CREATE TABLE TEMPLATE (_id INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, DESCRIPTION TEXT, START_DATE TEXT, END_DATE TEXT, IMAGE BLOB);");
        }
    }

    //Insert an item into database
    public boolean addAnItem(SQLiteDatabase db, String name, String description, String startDate, String endDate, String image){ //Image insertion to be determined

        boolean doesExist = false;
        Cursor cursor = db.query("TEMPLATE", new String[]{"_id, NAME"}, null, null, null, null, null);

        if(cursor.moveToFirst()){
            if(cursor.getString(cursor.getColumnIndex("NAME")).equals(name)){
                doesExist = true;
            }

            while(cursor.moveToNext()){
                if(cursor.getString(cursor.getColumnIndex("NAME")).equals(name)){
                    doesExist = true;
                }
            }
        }

        if(!doesExist) {
            ContentValues freezeValues = new ContentValues();
            freezeValues.put("NAME", name);
            freezeValues.put("DESCRIPTION", description);
            freezeValues.put("START_DATE", startDate);
            freezeValues.put("END_DATE", endDate);
            freezeValues.put("IMAGE", image);
            db.insert("ITEM", null, freezeValues);
            cursor.close();
            return doesExist;
        }else{
            cursor.close();
            return doesExist;
        }
    }

    //Insert a template into the database
    public boolean addATemplate(SQLiteDatabase db, String name, String description, String startDate, String endDate, String image){

        boolean doesExist = false;
        Cursor cursor = db.query("TEMPLATE", new String[]{"_id, NAME"}, null, null, null, null, null);

        if(cursor.moveToFirst()){
            if(cursor.getString(cursor.getColumnIndex("NAME")).equals(name)){
                doesExist = true;
            }

            while(cursor.moveToNext()){
                if(cursor.getString(cursor.getColumnIndex("NAME")).equals(name)){
                    doesExist = true;
                }
            }
        }

        if(!doesExist) {
            ContentValues freezeValues = new ContentValues();
            freezeValues.put("NAME", name);
            freezeValues.put("DESCRIPTION", description);
            freezeValues.put("START_DATE", startDate);
            freezeValues.put("END_DATE", endDate);
            freezeValues.put("IMAGE", image);
            db.insert("TEMPLATE", null, freezeValues);
            cursor.close();
            return doesExist;
        }else{
            cursor.close();
            return doesExist;
        }
    }

}
