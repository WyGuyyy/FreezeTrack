package com.example.wyatttowne.freezetrack;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.DialogFragment;
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

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Add_Item_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__item_);

        Spinner templates = (Spinner) findViewById(R.id.spTemplate);
        ArrayList<String> templateList = new ArrayList<String>();
        templateList.add("None");

        //Add database connectivity

        String[] templateArr = templateList.toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, templateArr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        templates.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition("None");
        templates.setSelection(spinnerPosition);

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

       String strName = name.getText().toString();
       String strDesc = desc.getText().toString();
       String strStart = start.getText().toString();
       String strFinish = start.getText().toString();
       String image = "";

       boolean exists = false;

       SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd/MM/yyyy");
       Date parsedStartDate = null;
       Date parsedFinishDate = null;

       try {
           parsedStartDate = formatter.parse(strStart);
           parsedFinishDate = formatter.parse(strFinish);
       }catch(Exception ex){
           ex.printStackTrace();
       }

       if(parsedFinishDate.before(parsedStartDate)) {

           try {

               SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(this);
               SQLiteDatabase db = freezeDatabaseHelper.getWritableDatabase();

               if ((exists = ((FreezeDatabaseHelper) freezeDatabaseHelper).addAnItem(db, strName, strDesc, strStart, strFinish, image))) {
                   Toast toast = Toast.makeText(this, "Item name already exists. Please try a new name.", Toast.LENGTH_SHORT);
                   toast.show();
               }

               db.close();

           } catch (SQLiteException ex) {
               Toast toast = Toast.makeText(this, "Database unavailable.", Toast.LENGTH_SHORT);
               toast.show();
           }

           AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setTitle("Add a Photo?");

           builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   startCamera();
               }
           });

           builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {

               }
           });

           builder.show();


           if(!exists) {
               Spinner templates = (Spinner) findViewById(R.id.spTemplate);
               EditText etName = (EditText) findViewById(R.id.etName);
               EditText etDesc = (EditText) findViewById(R.id.etDescription);
               TextView tvStart = (TextView) findViewById(R.id.tvStart);
               TextView tvFinish = (TextView) findViewById(R.id.tvFinish);

               templates.setSelection(0);
               etName.setText("");
               etDesc.setText("");
               tvStart.setText("");
               tvFinish.setText("");

               Toast toast = Toast.makeText(this, "Item saved!", Toast.LENGTH_LONG);
               toast.show();
           }

       }else{
           Toast toast = Toast.makeText(this, "Expiration date must be the same as or later than the start date. Item not saved.", Toast.LENGTH_SHORT);
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
               c = db.query("TEMPLATE", new String[]{"_id, NAME, DESCRIPTION, START, END"}, "NAME = ?", new String[]{selectedTemplate}, null, null, null);

               if (c.moveToFirst()) {
                   name = c.getString(c.getColumnIndex("NAME"));
                   desc = c.getString(c.getColumnIndex("DESCRIPTION"));
                   start = c.getString(c.getColumnIndex("START"));
                   end = c.getString(c.getColumnIndex("END"));
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
           }

           db.close();

       }catch(SQLiteException ex){
           Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
           toast.show();
       }


   }

   private void startCamera(){
       Intent intent = new Intent(this, CameraActivity.class);
       startActivity(intent);
   }


}
