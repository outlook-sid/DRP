package com.example.drp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.drp.helpers.NotificationHelper;


public class ReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");
        String big = intent.getStringExtra("big");
        Log.v("____Broadcast_____",title+"\n"+body+"\n"+big);

        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.createNotification(title, body, big);

    }
}
