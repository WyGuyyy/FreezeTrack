package com.example.wyatttowne.freezetrack;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class WatchboardActivity extends ListActivity {

    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listItems = getListView();

        try{
            SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(this);
            db = freezeDatabaseHelper.getReadableDatabase();

            cursor = db.query("ITEMS", new String[]{"_id", "NAME"}, null, null, null, null, null);
            CursorAdapter listAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, new String[]{"NAME"}, new int[]{android.R.id.text1}, 0);

            listItems.setAdapter(listAdapter);

        }catch(SQLiteException ex){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); <- need to figure out how to implement with ListActivity

    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        cursor.close();
        db.close();
    }

    @Override
    public void onListItemClick(ListView listView, View itemView, int position, long id){
        Intent intent = new Intent(WatchboardActivity.this, ItemActivity.class);
        intent.putExtra(ItemActivity.I_NO, (int)id);
        startActivity(intent);
    }

}
