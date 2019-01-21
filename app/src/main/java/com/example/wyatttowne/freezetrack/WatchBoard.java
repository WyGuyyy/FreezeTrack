package com.example.wyatttowne.freezetrack;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.GridLayout;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.example.wyatttowne.freezetrack.Leftover.leftovers;

public class WatchBoard extends AppCompatActivity {

    RecyclerView rv;
    GridLayoutManager glm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_board);

        rv = (RecyclerView) findViewById(R.id.recycler);
        glm = new GridLayoutManager(this, 2);
        rv.setLayoutManager(glm);
        fillCards();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void fillCards(){

        SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(this);
        ArrayList<Leftover> tempList = new ArrayList<Leftover>();

        Cursor c;
        SQLiteDatabase db;

        String name = "";
        String startDate = "";
        String imageName = "";

        int activeDays = 0;

        try{

            db = freezeDatabaseHelper.getReadableDatabase();
            c = db.query("ITEM", new String[]{"_id, NAME, START_DATE, IMAGE_NAME"}, null, null, null, null, null);

            if(c.moveToFirst()){

                name = c.getString(c.getColumnIndex("NAME"));
                startDate = c.getString(c.getColumnIndex("START_DATE"));
                imageName = c.getString(c.getColumnIndex("IMAGE_NAME"));

                activeDays = getDaysActive(startDate);

                Leftover lv = new Leftover(name, activeDays, imageName);
                tempList.add(lv);

                while(c.moveToNext()){

                    name = c.getString(c.getColumnIndex("NAME"));
                    startDate = c.getString(c.getColumnIndex("START_DATE"));
                    imageName = c.getString(c.getColumnIndex("IMAGE_NAME"));

                    activeDays = getDaysActive(startDate);

                    lv = new Leftover(name, activeDays, imageName);
                    tempList.add(lv);

                }
            }

            c.close();
            db.close();

        }catch(SQLiteException ex){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        CustomAdapter adapter = new CustomAdapter(this, tempList);
        rv.setAdapter(adapter);

    }

    public int getDaysActive(String startDate){

        int daysBetween = 0;

        Calendar c1 = new GregorianCalendar();
        Calendar c2 = new GregorianCalendar();

        SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

        try {
            Date date = formatter.parse(startDate);
            c1.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        daysBetween = (int) ((c2.getTime().getTime() - c1.getTime().getTime()) / (1000 * 60 * 60 * 24));

        return daysBetween;

    }
}
