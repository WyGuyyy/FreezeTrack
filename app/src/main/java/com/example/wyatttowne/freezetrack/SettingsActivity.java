package com.example.wyatttowne.freezetrack;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    Spinner spTime;
    Switch swWarning;
    Switch swExpired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spTime = (Spinner) findViewById(R.id.spTime);
        swWarning = (Switch) findViewById(R.id.swWarning);
        swExpired = (Switch) findViewById(R.id.swExpired);

        initialize();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initialize(){

        SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(this);
        String[] timeValues = {"1 Hour", "2 Hours", "4 hours", "8 Hours", "1 Day"};

        try{

            SQLiteDatabase db = freezeDatabaseHelper.getReadableDatabase();
            Cursor c = db.query("SETTINGS", new String[]{"_id, WARNING_STATUS, EXPIRED_STATUS, NOTIFY_TIME"}, null, null, null, null, null);

            if(c.moveToFirst()){

                if(c.getInt(c.getColumnIndex("WARNING_STATUS")) == 1){
                    swWarning.setChecked(true);
                }else{
                    swWarning.setChecked(false);
                }

                if(c.getInt(c.getColumnIndex("EXPIRED_STATUS")) == 1){
                    swExpired.setChecked(true);
                }else{
                    swExpired.setChecked(false);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeValues);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spTime.setAdapter(adapter);

                int spinnerPosition = adapter.getPosition(c.getString(c.getColumnIndex("NOTIFY_TIME")));
                spTime.setSelection(spinnerPosition);

            }

            c.close();
            db.close();

        }catch(SQLiteException ex){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }



}
