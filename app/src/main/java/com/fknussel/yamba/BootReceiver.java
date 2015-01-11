package com.fknussel.yamba;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    
    private static final String TAG = BootReceiver.class.getSimpleName();

    // This is going to be our default interval, 15 minutes expressed in milliseconds
    private static final long DEFAULT_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    
    // This method gets called when an intent matches this receiver
    @Override
    public void onReceive(Context context, Intent intent) {

        // We may have added a property for interval to our settings for the application,
        // alongside the username and password. If not, we’ll just use the default value of 15 min.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long interval = Long.parseLong(prefs.getString("interval", Long.toString(DEFAULT_INTERVAL)));

        // This is where we create our pending intent to be sent by the alarm to trigger the service.
        // Think of the pending intent as an intent plus the action on it, such as start a service.
        PendingIntent operation = PendingIntent.getService(context, -1, new Intent(context, RefreshService.class), PendingIntent.FLAG_UPDATE_CURRENT);

        // This is how we get the reference to the system service from the current context.
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // In case the interval is set to zero, presumably the user doesn't want this service to ever run.
        // Otherwise, we use the alarm manager’s API to repeat this operation every interval, or so.
        if (interval == 0) {
            alarmManager.cancel(operation);
            Log.d(TAG, "cancelling repeat operation");
        } else {
            alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, operation);
            Log.d(TAG, "setting repeat operation for: " + interval);
        }

        Log.d(TAG, "onReceived");
    }
}
