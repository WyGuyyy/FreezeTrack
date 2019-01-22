package com.example.wyatttowne.freezetrack;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PersonalReminderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_reminder);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    public void onStartClick(View view){
        Bundle args = new Bundle();

        DialogFragment datePicker = new DatePickerFragment();
        datePicker.setArguments(args);
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }
}
