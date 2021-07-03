package com.example.drp.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.example.drp.R;
import com.example.drp.activity.PaymentResultPage;
import com.example.drp.activity.SplashScreen;

public class NotificationHelper {

    private final Context context;
    public static final String NOTIFICATION_CHANNEL_ID = "14001";
    public static final String NOTIFICATION_CHANNEL_NAME = "Notification for ration collection";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Shows reminder for ration collection";
    public NotificationHelper(Context context) {
        this.context = context;
    }

    public void createNotification(String title, String content, String big) {

        Intent intentStartApp = new Intent(context, SplashScreen.class);
        intentStartApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent intentBill = new Intent(context, PaymentResultPage.class);
        intentBill.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent;
        if (UtilHelper.isRunning(context)) {
            pendingIntent = PendingIntent.getActivity(context, 0, intentBill, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getActivity(context, 0, intentStartApp, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(big))
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationChannel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
            assert mNotificationManager != null;
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, notificationBuilder.build());
    }

}
