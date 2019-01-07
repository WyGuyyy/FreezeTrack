package com.example.wyatttowne.freezetrack;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Thread.sleep;

public class ItemActivity extends AppCompatActivity {

    public static final String I_NO = "I_NO";

    private SQLiteDatabase db;
    private Cursor cursor;

    TextView name;
    TextView description;
    TextView time;
    TextView date;

    int iNo;

    //Background thread timer
    Thread timeUpdate;
    Runnable timerTask = new Runnable() {
        @Override
        public void run() {

            while (true) {

                cursor = db.query("ITEM", new String[] {"DAYS", "HOURS", "MINUTES", "SECONDS", "START_DATE", "END_DATE"}, "_id = ?", new String[] {Integer.toString(iNo)}, null, null, null);
                int days = cursor.getInt(1);
                int hours = cursor.getInt(2);
                int minutes = cursor.getInt(3);
                int seconds = cursor.getInt(4);
                String startText = cursor.getString(5);
                String endText = cursor.getString(6);

                time.setText("Days: " + days + " Hours: " + hours + " minutes: " + minutes + " seconds: " + seconds);

                date.setText(startText + " -> " + endText);

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        iNo = (Integer) getIntent().getExtras().get(I_NO);
        //Horoscope horoscope = Horoscope.horoscopes[hsNo];

        try {

            SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(this);
            db = freezeDatabaseHelper.getReadableDatabase();
            cursor = db.query("ITEM", new String[] {"NAME", "DESCRIPTION", "DAYS", "HOURS", "MINUTES", "SECONDS", "START_DATE", "END_DATE"}, "_id = ?", new String[] {Integer.toString(iNo)}, null, null, null);

            if(cursor.moveToFirst()) {

                //Get the item details from the server
                String nameText = cursor.getString(0);
                String descriptionText = cursor.getString(1);
                int days = cursor.getInt(2);
                int hours = cursor.getInt(3);
                int minutes = cursor.getInt(4);
                int seconds = cursor.getInt(5);
                String startText = cursor.getString(6);
                String endText = cursor.getString(7);

                name = (TextView) findViewById(R.id.tvName);
                name.setText(nameText);

                description = (TextView) findViewById(R.id.tvDescription);
                description.setText(descriptionText);

                time = (TextView) findViewById(R.id.tvTime);
                time.setText("Days: " + days + " Hours: " + hours + " minutes: " + minutes + " seconds: " + seconds);

                date = (TextView) findViewById(R.id.tvDate);
                date.setText(startText + " -> " + endText);

            }

        }catch(SQLiteException ex){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        timeUpdate = new Thread(timerTask);
        timeUpdate.start();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
        timeUpdate.interrupt();
    }
}
