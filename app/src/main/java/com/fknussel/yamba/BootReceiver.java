package com.fknussel.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    
    private static final String TAG = BootReceiver.class.getSimpleName();
    
    // This method gets called when an intent matches this receiver
    @Override
    public void onReceive(Context context, Intent intent) {
        // We launch an intent to start our Updater service. The system passed us
        // a Context object when it invoked our onReceive() method, and we are
        //  expected to pass it on to the Updater service.
        context.startService(new Intent(context, RefreshService.class));
        Log.d(TAG, "onReceived");
    }
}
