package com.example.wyatttowne.freezetrack;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationService extends Service {

    Thread notificationThread;

    static String notifyTime = "";
    static boolean warningOn = true;
    static boolean expiredOn = true;
    static boolean timeToUpdate = true;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast toast = Toast.makeText(getApplicationContext(), String.valueOf(isMyServiceRunning(NotificationService.class)), Toast.LENGTH_SHORT);
        toast.show();

        if (!isMyServiceRunning(NotificationService.class)) {
            startNotificationService();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        notificationThread.interrupt();
        Toast toast = Toast.makeText(getApplicationContext(), "Service stopped!", Toast.LENGTH_SHORT);
        toast.show();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //FOR SERVICE NEED WAY TO NOTIFY UPON SUDDEN SERVICE ENABLED OR DISABLED!!!!!
    private void startNotificationService() {

        Toast toast = Toast.makeText(getApplicationContext(), "Service is running!", Toast.LENGTH_SHORT);
        toast.show();

        updateChecks(); //Start here next time... need to finish service implementation

        Date d1 = new Date();
        final Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);

        notificationThread = new Thread() {

            @Override
            public void run() {

                boolean newDay = false;

                while (true) {

                    if ((c1.get(Calendar.HOUR_OF_DAY) == 1 && c1.get(Calendar.MINUTE) == 1 && c1.get(Calendar.SECOND) >= 1 && c1.get(Calendar.SECOND) <= 10)) {
                        newDay = true;

                        try {
                            notificationThread.wait(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else {
                        newDay = false;
                    }

                    if (warningOn && newDay) {
                        warnItems();
                    }

                    if (expiredOn && newDay) {
                        expireItems();
                    }

                    if (timeToUpdate) {
                        updateChecks();
                        timeToUpdate = false;
                    }
                }
            }

        };

        notificationThread.start();

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        Toast toast = Toast.makeText(getApplicationContext(), "In start command!", Toast.LENGTH_SHORT);
        toast.show();
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Toast toast2 = Toast.makeText(getApplicationContext(), "Is true!", Toast.LENGTH_SHORT);
                toast2.show();
                return true;
            }
        }
        return false;
    }

    private void updateChecks() {

        SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(getApplicationContext());
        SQLiteDatabase db;

        try {
            db = freezeDatabaseHelper.getReadableDatabase();
            Cursor c = db.query("SETTINGS", new String[]{"_id, WARNING_STATUS, EXPIRED_STATUS, NOTIFY_TIME"}, null, null, null, null, null);

            if (c.moveToFirst()) {

                notifyTime = c.getString(c.getColumnIndex("NOTIFY_TIME"));
                warningOn = (c.getInt(c.getColumnIndex("WARNING_STATUS")) == 1 ? true : false);
                expiredOn = (c.getInt(c.getColumnIndex("EXPIRED_STATUS")) == 1 ? true : false);

            }

            c.close();
            db.close();

        } catch (SQLiteException ex) {
            Toast toast = Toast.makeText(getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void warnItems() {
        SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(getApplicationContext());
        SQLiteDatabase db;

        int addTime = 0;
        int countWarning = 0;

        try {
            db = freezeDatabaseHelper.getReadableDatabase();
            Cursor c = db.query("ITEM", new String[]{"_id, NAME, START_DATE, END_DATE, IMAGE_NAME"}, null, null, null, null, null);

            if (c.moveToFirst()) {

                SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();

                if (!(c.getString(c.getColumnIndex("END_DATE")).equals("None"))) {

                    try {
                        cal1.setTime(new Date());
                        cal2.setTime(formatter.parse(c.getString(c.getColumnIndex("END_DATE"))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    addTime = (notifyTime.equals("1 Day") ? 1 : addTime);
                    addTime = (notifyTime.equals("2 Days") ? 2 : addTime);
                    addTime = (notifyTime.equals("3 Days") ? 3 : addTime);
                    addTime = (notifyTime.equals("4 Days") ? 4 : addTime);
                    addTime = (notifyTime.equals("5 Days") ? 5 : addTime);
                    addTime = (notifyTime.equals("6 Days") ? 6 : addTime);
                    addTime = (notifyTime.equals("1 Week") ? 7 : addTime);

                    cal1.add(Calendar.DATE, addTime);

                    if (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) ||
                            (cal1.get(android.icu.util.Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) >= cal2.get(Calendar.YEAR))) {

                        countWarning++;

                        long diff = cal2.getTime().getTime() - cal1.getTime().getTime();
                        float days = (diff / (1000 * 60 * 60 * 24));
                        int intDays = (int) days;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel warningChannel = new NotificationChannel(
                                    "channel" + c.getString(c.getColumnIndex("NAME")),
                                    "Channel " + c.getString(c.getColumnIndex("NAME")),
                                    NotificationManager.IMPORTANCE_HIGH
                            );

                            warningChannel.setDescription("Channel for " + c.getString(c.getColumnIndex("NAME")));

                            NotificationManager manager = getSystemService(NotificationManager.class);
                            manager.createNotificationChannel(warningChannel);
                        }

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "channel" + c.getString(c.getColumnIndex("NAME")))
                                .setSmallIcon(R.drawable.ic_food)
                                .setContentTitle("EXPIRATION WARNING!")
                                .setContentText("Heads up! It looks like your item " + c.getString(c.getColumnIndex("NAME")) + " is about to expire in " + intDays + " days.")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();

                        notificationManagerCompat.notify(countWarning, notification);

                        //Send notification to phone
                    }
                }

                while (c.moveToNext()) {

                    formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

                    cal1 = Calendar.getInstance();
                    cal2 = Calendar.getInstance();

                    if (!(c.getString(c.getColumnIndex("END_DATE")).equals("None"))) {

                        try {
                            cal1.setTime(new Date());
                            cal2.setTime(formatter.parse(c.getString(c.getColumnIndex("END_DATE"))));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (!(cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) ||
                                (cal1.get(android.icu.util.Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) >= cal2.get(Calendar.YEAR))) {

                            countWarning++;

                            long diff = cal2.getTime().getTime() - cal1.getTime().getTime();
                            float days = (diff / (1000 * 60 * 60 * 24));
                            int intDays = (int) days;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel warningChannel = new NotificationChannel(
                                        "channel" + c.getString(c.getColumnIndex("NAME")),
                                        "Channel " + c.getString(c.getColumnIndex("NAME")),
                                        NotificationManager.IMPORTANCE_HIGH
                                );

                                warningChannel.setDescription("Channel for " + c.getString(c.getColumnIndex("NAME")));

                                NotificationManager manager = getSystemService(NotificationManager.class);
                                manager.createNotificationChannel(warningChannel);
                            }

                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

                            Notification notification = new NotificationCompat.Builder(getApplicationContext(), "channel" + c.getString(c.getColumnIndex("NAME")))
                                    .setSmallIcon(R.drawable.ic_food)
                                    .setContentTitle("EXPIRATION WARNING!")
                                    .setContentText("Heads up! It looks like your item " + c.getString(c.getColumnIndex("NAME")) + " is about to expire in " + intDays + " days.")
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                    .build();

                            notificationManagerCompat.notify(countWarning, notification);

                            //Send notification to phone
                        }
                    }

                }

            }

            c.close();
            db.close();

        } catch (SQLiteException ex) {
            Toast toast = Toast.makeText(getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void expireItems() { //Need to clean up notification section for this method
        SQLiteOpenHelper freezeDatabaseHelper = new FreezeDatabaseHelper(getApplicationContext());
        SQLiteDatabase db;

        int countExpire = 0;

        try {
            db = freezeDatabaseHelper.getReadableDatabase();
            Cursor c = db.query("ITEM", new String[]{"_id, NAME, START_DATE, END_DATE, IMAGE_NAME"}, null, null, null, null, null);

            if (c.moveToFirst()) {

                SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();

                if (!(c.getString(c.getColumnIndex("END_DATE")).equals("None"))) {

                    try {
                        cal1.setTime(new Date());
                        cal2.setTime(formatter.parse(c.getString(c.getColumnIndex("END_DATE"))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (!(cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) ||
                            (cal1.get(android.icu.util.Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) >= cal2.get(Calendar.YEAR))) {

                        countExpire++;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel warningChannel = new NotificationChannel(
                                    "channel" + c.getString(c.getColumnIndex("NAME")),
                                    "Channel " + c.getString(c.getColumnIndex("NAME")),
                                    NotificationManager.IMPORTANCE_HIGH
                            );

                            warningChannel.setDescription("Channel for " + c.getString(c.getColumnIndex("NAME")));

                            NotificationManager manager = getSystemService(NotificationManager.class);
                            manager.createNotificationChannel(warningChannel);
                        }

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "channel" + c.getString(c.getColumnIndex("NAME")))
                                .setSmallIcon(R.drawable.ic_food)
                                .setContentTitle("EXPIRATION WARNING!")
                                .setContentText("Uh oh! It looks like your item " + c.getString(c.getColumnIndex("NAME")) + " has expired!")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();

                        notificationManagerCompat.notify(countExpire, notification);

                        //Send notification to phone
                    }
                }

                while (c.moveToNext()) {

                    formatter = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

                    cal1 = Calendar.getInstance();
                    cal2 = Calendar.getInstance();

                    if (!(c.getString(c.getColumnIndex("END_DATE")).equals("None"))) {

                        try {
                            cal1.setTime(new Date());
                            cal2.setTime(formatter.parse(c.getString(c.getColumnIndex("END_DATE"))));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (!(cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) ||
                                (cal1.get(android.icu.util.Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR) && cal1.get(Calendar.YEAR) >= cal2.get(Calendar.YEAR))) {

                            countExpire++;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel warningChannel = new NotificationChannel(
                                        "channel" + c.getString(c.getColumnIndex("NAME")),
                                        "Channel " + c.getString(c.getColumnIndex("NAME")),
                                        NotificationManager.IMPORTANCE_HIGH
                                );

                                warningChannel.setDescription("Channel for " + c.getString(c.getColumnIndex("NAME")));

                                NotificationManager manager = getSystemService(NotificationManager.class);
                                manager.createNotificationChannel(warningChannel);
                            }

                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

                            Notification notification = new NotificationCompat.Builder(getApplicationContext(), "channel" + c.getString(c.getColumnIndex("NAME")))
                                    .setSmallIcon(R.drawable.ic_food)
                                    .setContentTitle("EXPIRATION WARNING!")
                                    .setContentText("Uh oh! It looks like your item " + c.getString(c.getColumnIndex("NAME")) + " has expired!")
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                    .build();

                            notificationManagerCompat.notify(countExpire, notification);

                            //Send notification to phone
                        }
                    }

                }

            }

            c.close();
            db.close();

        } catch (SQLiteException ex) {
            Toast toast = Toast.makeText(getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    public static void notifyService() {
        timeToUpdate = true;
    }

}
