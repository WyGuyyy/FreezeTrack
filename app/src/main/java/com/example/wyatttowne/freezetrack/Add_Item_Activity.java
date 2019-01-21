package com.example.wyatttowne.freezetrack;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Add_Item_Activity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int WRITE_REQUEST = 2888;
    private static final int READ_REQUEST = 3888;
    private String sFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__item_);

        Spinner templates = (Spinner) findViewById(R.id.spTemplate);

        fillTemplates();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQUEST);
        }


        templates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                onChooseTemplate(parentView, selectedItemView, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    //Create the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the app bar.
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Handle click event for option bar items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_template:
                saveTemplate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onStartClick(View view){
        Bundle args = new Bundle();
        args.putBoolean("startSelected", true);
        args.putBoolean("endSelected", false);

        DialogFragment datePicker = new DatePickerFragment();
        datePicker.setArguments(args);
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    public void onEndClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Expire Date");

        builder.setPositiveButton("Select Date", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle args = new Bundle();
                args.putBoolean("startSelected", false);
                args.putBoolean("endSelected", true);

                DialogFragment datePicker = new DatePickerFragment();
                datePicker.setArguments(args);
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });

        builder.setNegativeButton("Set to none", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView end = (TextView) findViewById(R.id.tvFinish);
                end.setText("None");
            }
        });

        builder.show();
    }

    //NEXT STEP... CREATE DATE AND TIMER SELECTION SYSTEM!!!!!!!!!


    //TEMPORARILY SAVED FOR LATER USE OF TEMPLATE
   /* public void showTemplateBox(){

        String templateName = "";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Template Name");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(!input.getText().toString().equals("")) {
                    setTemplateName(input.getText().toString()); //Start here << finish tempalte input!!!!!!
                }else {
                    Toast toast = Toast.makeText(this, "Must enter a template name. Template not saved.", Toast.LENGTH_SHORT); //Fix this!!!!
                    toast.show();
                }

                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }
    */

   //Handle user event for saving an item button click
   public void onSave(View view){

       EditText name = (EditText)findViewById(R.id.etName);
       EditText desc = (EditText)findViewById(R.id.etDescription);
       TextView start = (TextView)findViewById(R.id.tvStart);
       TextView finish = (TextView)findViewById(R.id.tvFinish);

       final String strName = name.getText().toString();
       String strDesc = desc.getText().toString();
       String strStart = start.getText().toString();
       String strFinish = finish.getText().toString();
       String image = "";

       boolean exists = false;

       SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
       Date parsedStartDate = null;
       Date parsedFinishDate = null;

       if(!strName.equals("") && !strStart.equals("") && !strFinish.equals("")) {

           try {
               parsedStartDate = formatter.parse(strStart);

               if (!strFinish.equals("None")) {
                   parsedFinishDate = formatter.parse(strFinish);
               } else {
                   parsedFinishDate = parsedStartDate;
               }

           } catch (Exception ex) {
               ex.printStackTrace();
           }

           if (parsedFinishDate.equals("None") || !parsedFinishDate.before(parsedStartDate)) {

               try {

                   SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(this);
                   SQLiteDatabase db = freezeDatabaseHelper.getWritableDatabase();

                   if ((exists = ((FreezeDatabaseHelper) freezeDatabaseHelper).addAnItem(db, strName, strDesc, strStart, strFinish, image))) {
                       Toast toast = Toast.makeText(this, "Item name already exists. Please try a new name.", Toast.LENGTH_LONG);
                       toast.show();
                   }

                   db.close();

               } catch (SQLiteException ex) {
                   Toast toast = Toast.makeText(this, "Database unavailable.", Toast.LENGTH_SHORT);
                   toast.show();
               }

               if (!exists) {
                   AlertDialog.Builder builder = new AlertDialog.Builder(this);
                   builder.setTitle("Add a Photo?");

                   builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           startCamera(strName);
                       }
                   });

                   builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {

                       }
                   });

                   builder.show();

                   Spinner templates = (Spinner) findViewById(R.id.spTemplate);
                   EditText etName = (EditText) findViewById(R.id.etName);
                   EditText etDesc = (EditText) findViewById(R.id.etDescription);
                   TextView tvStart = (TextView) findViewById(R.id.tvStart);
                   TextView tvFinish = (TextView) findViewById(R.id.tvFinish);

                   sFileName = etName.getText().toString();

                   templates.setSelection(0);
                   etName.setText("");
                   etDesc.setText("");
                   tvStart.setText("");
                   tvFinish.setText("");

                   Toast toast = Toast.makeText(this, "Item saved!", Toast.LENGTH_LONG);
                   toast.show();
               }

           } else {
               Toast toast = Toast.makeText(this, "Expiration date must be the same as or later than the start date. Item not saved.", Toast.LENGTH_LONG);
               toast.show();
           }
       }else{
           Toast toast = Toast.makeText(this, "Some required fields are not filled", Toast.LENGTH_LONG);
           toast.show();
       }
   }

   public void onChooseTemplate(AdapterView<?> parentView, View selectedItemView, int position, long id){

       Spinner templates = (Spinner) findViewById(R.id.spTemplate);
       String selectedTemplate = templates.getSelectedItem().toString();

       String name = "";
       String desc = "";
       String start = "";
       String end = "";

       EditText etName = (EditText) findViewById(R.id.etName);
       EditText etDesc = (EditText) findViewById(R.id.etDescription);
       TextView tvStart = (TextView) findViewById(R.id.tvStart);
       TextView tvEnd = (TextView) findViewById(R.id.tvFinish);

       SQLiteOpenHelper sqLiteOpenHelper = new FreezeDatabaseHelper(this);
       SQLiteDatabase db;
       Cursor c;


       if(selectedTemplate.equals("None")){

           etName.setText("");
           etDesc.setText("");
           tvStart.setText("");
           tvEnd.setText("");

       }else {
           try {

               db = sqLiteOpenHelper.getReadableDatabase();
               c = db.query("TEMPLATE", new String[]{"_id, NAME, DESCRIPTION, START_DATE, END_DATE"}, "NAME = ?", new String[]{selectedTemplate}, null, null, null);

               if (c.moveToFirst()) {
                   name = c.getString(c.getColumnIndex("NAME"));
                   desc = c.getString(c.getColumnIndex("DESCRIPTION"));
                   start = c.getString(c.getColumnIndex("START_DATE"));
                   end = c.getString(c.getColumnIndex("END_DATE"));
               }

               db.close();

           } catch (SQLiteException ex) {
               Toast toast = Toast.makeText(this, "Database unavailable.", Toast.LENGTH_SHORT);
               toast.show();
           }

           etName.setText(name);
           etDesc.setText(desc);
           tvStart.setText(start);
           tvEnd.setText(end);

       }
   }

   //Helper method for selecting the save template option bar item
   public void saveTemplate(){
       EditText name = (EditText)findViewById(R.id.etName);
       EditText desc = (EditText)findViewById(R.id.etDescription);
       TextView start = (TextView)findViewById(R.id.tvStart);
       TextView finish = (TextView)findViewById(R.id.tvFinish);

       String strName = name.getText().toString();
       String strDesc = desc.getText().toString();
       String strStart = start.getText().toString();
       String strFinish = start.getText().toString();
       String image = "";

       try {

           SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(this);
           SQLiteDatabase db = freezeDatabaseHelper.getWritableDatabase();

           if(((FreezeDatabaseHelper) freezeDatabaseHelper).addATemplate(db, strName, strDesc, strStart, strFinish, image)){
               Toast toast = Toast.makeText(this, "Template name already exists. Please try a new name.", Toast.LENGTH_SHORT);
               toast.show();
           }else{
               Toast toast = Toast.makeText(this, "Template saved!", Toast.LENGTH_SHORT);
               toast.show();
           }

           db.close();

       }catch(SQLiteException ex){
           Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
           toast.show();
       }

       fillTemplates();

   }

   private void startCamera(String fileName) {
       /*Intent intent = new Intent(this, CameraActivity.class);
       intent.putExtra("fileName", fileName);
       startActivity(intent);*/

       if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
           Toast toast = Toast.makeText(this, "Camera permission was denied. Please close add item window and re-open to register permission. Item will be saved without a photo.", Toast.LENGTH_LONG);
           toast.show();
       } else {
           Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
           startActivityForResult(cameraIntent, CAMERA_REQUEST);
       }
   }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            String fileName = sFileName; //Use name field for file name (Entered in Add_Item_Activity class)
            File file = new File(Environment.getExternalStorageDirectory()+ File.separator + "FreezePics");//fileName.replace(" ","_") + ".jpg");

            if(!file.exists()) {
                file.mkdirs();
            }

            File pictureFile = new File(file, fileName.replace(" ", "_"));
            try {
                pictureFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try{

                FileOutputStream out = new FileOutputStream(pictureFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();

            }catch(Exception ex){
                ex.printStackTrace();
            }

            savePictureName(fileName);

        }

        sFileName = "";

   }

    private void savePictureName(String fileName){

        SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(this);

        ContentValues freezeValues = new ContentValues();
        freezeValues.put("IMAGE_NAME", fileName);

        try{

            SQLiteDatabase db = freezeDatabaseHelper.getWritableDatabase();
            db.update("ITEM", freezeValues, "NAME=?", new String[]{fileName});
            db.close();

        }catch (SQLiteException ex){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();

        }

        Toast toast = Toast.makeText(this, "Photo saved!", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void fillTemplates(){

        ArrayList<String> tempList = new ArrayList<String>();
        tempList.add("None");

        SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(this);

        try{

            SQLiteDatabase db = freezeDatabaseHelper.getReadableDatabase();
            Cursor c = db.query("TEMPLATE", new String[]{"_id, NAME"}, null, null, null, null, null);

            if(c.moveToFirst()){
                tempList.add(c.getString(c.getColumnIndex("NAME")));
                while(c.moveToNext()){
                    tempList.add(c.getString(c.getColumnIndex("NAME")));
                }
            }

            c.close();
            db.close();

        }catch(SQLiteException ex){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        String name = "";
        String desc = "";
        String start = "";
        String end = "";

        EditText etName = (EditText) findViewById(R.id.etName);
        EditText etDesc = (EditText) findViewById(R.id.etDescription);
        TextView tvStart = (TextView) findViewById(R.id.tvStart);
        TextView tvEnd = (TextView) findViewById(R.id.tvFinish);

        name = etName.getText().toString();
        desc = etDesc.getText().toString();
        start = tvStart.getText().toString();
        end = tvEnd.getText().toString();

        Spinner templates = (Spinner) findViewById(R.id.spTemplate);

        String[] templateArr = tempList.toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, templateArr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        templates.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(name);
        templates.setSelection(spinnerPosition);

    }


    }

