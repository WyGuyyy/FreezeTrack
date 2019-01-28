package com.example.wyatttowne.freezetrack;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class NotificationService extends Service {

    Thread notificationThread;

    static String notifyTime = "";
    static boolean warningOn = true;
    static boolean expiredOn = true;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!isMyServiceRunning(NotificationService.class)) {
            startNotificationService();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void startNotificationService() {

        updateChecks(); //Start here next time... need to finish service implementation

        notificationThread = new Thread(){

            @Override
            public void run(){



            }

        };

        notificationThread.start();

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void updateChecks(){

        SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(getApplicationContext());
        SQLiteDatabase db;

        try{
            db = freezeDatabaseHelper.getReadableDatabase();
            Cursor c = db.query("SETTINGS", new String[] {"_id, WARNING_STATUS, EXPIRED_STATUS, NOTIFY_TIME"}, null, null, null, null, null);

            if(c.moveToFirst()){

                notifyTime = c.getString(c.getColumnIndex("NOTIFY_TIME"));
                warningOn = (c.getInt(c.getColumnIndex("WARNING_STATUS")) == 1 ? true : false);
                expiredOn = (c.getInt(c.getColumnIndex("EXPIRED_STATUS")) == 1 ? true : false);

            }

            db.close();

        }catch(SQLiteException ex){
            Toast toast = Toast.makeText(getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

}
