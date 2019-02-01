package com.example.wyatttowne.freezetrack;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationHelper notificationHelper = new Notif

        Intent background = new Intent(context, NotificationService.class);
        context.startService(background);
    }
}
