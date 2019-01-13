package com.example.wyatttowne.freezetrack;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class WatchBoard extends AppCompatActivity {

    RecyclerView rv;
    LinearLayoutManager llm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_board);

        rv = (RecyclerView) findViewById(R.id.recycler);
        llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        fillCards();
    }


    private void fillCards(){

        SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(this);
        ArrayList<Leftover> tempList = new ArrayList<Leftover>();

        



    }
}
