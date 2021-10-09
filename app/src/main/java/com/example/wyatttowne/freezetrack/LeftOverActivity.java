package com.example.wyatttowne.freezetrack;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class LeftOverActivity extends AppCompatActivity {

    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_over);

        fillInfo();

        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(LeftOverActivity.this);
                builder.setTitle("Are you sure you wish to delete item: " + getIntent().getStringExtra("Name") + "?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void fillInfo(){

        SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(this);

        Cursor c;
        SQLiteDatabase db;

        TextView txtName = (TextView) findViewById(R.id.txtName);
        TextView txtDesc = (TextView) findViewById(R.id.txtDescription);
        TextView txtStart = (TextView) findViewById(R.id.txtStart);
        TextView txtEnd = (TextView) findViewById(R.id.txtFinish);
        TextView txtStatus = (TextView) findViewById(R.id.txtStatus);

        ImageView imgLeftover = (ImageView) findViewById(R.id.leftover_image_inner);

        String name = getIntent().getStringExtra("Name");
        String desc = "";
        String startDate = "";
        String endDate = "";
        String imageName = "";

        try{

            db = freezeDatabaseHelper.getReadableDatabase();
            c = db.query("ITEM", new String[]{"_id, NAME, DESCRIPTION, START_DATE, END_DATE, IMAGE_NAME"}, "NAME=?", new String[]{name}, null, null, null);

            if(c.moveToFirst()){

                desc = c.getString(c.getColumnIndex("DESCRIPTION"));
                startDate = c.getString(c.getColumnIndex("START_DATE"));
                endDate = c.getString(c.getColumnIndex("END_DATE"));
                imageName = c.getString(c.getColumnIndex("IMAGE_NAME"));

            }

            c.close();
            db.close();

        }catch(SQLiteException ex){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        //Fill text view values
        txtName.setText(name);
        txtDesc.setText(desc);
        txtStart.setText(startDate);
        txtEnd.setText(endDate);

        SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        Date currentDate = new Date();
        Date parsedFinishDate = null;

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        try {

            if (!endDate.equals("None")) {
                parsedFinishDate = formatter.parse(endDate);

                cal1.setTime(currentDate);
                cal2.setTime(parsedFinishDate);

                //!(cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) ||

                if((cal1.get(Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) >= cal2.get(Calendar.YEAR))){
                    Log.d("test", cal1.get(Calendar.DAY_OF_YEAR) + "");
                    txtStatus.setText("EXPIRED");
                    txtStatus.setTextColor(Color.RED);
                    txtStatus.setTypeface(null, Typeface.BOLD);

                }else{
                    txtStatus.setText("GOOD");
                    txtStatus.setTextColor(Color.GREEN);
                    txtStatus.setTypeface(null, Typeface.BOLD);
                }

            }else {
                parsedFinishDate = currentDate;
                txtStatus.setText("No expiration date.");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        File dir = new File(Environment.getExternalStorageDirectory()+ "/FreezePics");
        File imgFile = new File(dir, name.replace(" ", "_"));

        if(imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgLeftover.setImageBitmap(bitmap);
        }else{
            imgLeftover.setImageResource(R.drawable.food_image);
        }

    }

    public void deleteItem(){

        SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(this);
        SQLiteDatabase db;

        try{

            db = freezeDatabaseHelper.getWritableDatabase();
            db.delete("ITEM", "NAME=?", new String[]{getIntent().getStringExtra("Name")});
            db.close();

        }catch(SQLiteException ex){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        Toast toast = Toast.makeText(this, "Item deleted!", Toast.LENGTH_SHORT);
        toast.show();

        finish();
    }
}
