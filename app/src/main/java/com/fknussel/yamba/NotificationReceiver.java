package com.fknussel.yamba;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 42;

    @Override
    public void onReceive(Context context, Intent intent) {
        
        // Similarly to the Alarm service, we get the notification service by calling getSystemService() from the passed in context
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        // The intent that triggered this receiver was posted in RefreshService,
        // and in there we attached a primitive integer representing the count of new tweets.
        // Here we extract it from that receiving intent.
        int count = intent.getIntExtra("count", 0);

        // We create a pending operation—in other words, what will happen once a user
        // clicks this specific notification. In this case, we launch MainActivity so
        // the user can quickly read new tweets.
        PendingIntent operation = PendingIntent.getActivity(context, -1, new Intent(context, MainActivity.class), PendingIntent.FLAG_ONE_SHOT);

        // To post a notification, first we need to build it. This code uses the
        // Notification.Builder class to help build a notification with the minimal
        // set of bells and whistles.
        Notification notification = new Notification.Builder(context)
                .setContentTitle("New tweets!")
                .setContentText("You've got " + count + " new tweets")
                .setSmallIcon(android.R.drawable.sym_action_email)
                .setContentIntent(operation)
                .setAutoCancel(true)
                .getNotification();

        // Finally, we post this notification to the notification manager
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
