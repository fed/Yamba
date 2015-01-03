package com.fknussel.yamba;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


public class RefreshService extends IntentService {
    
    private static final String TAG = "RefreshService";
    
    public RefreshService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service stopped");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Executes on a worker thread
        Log.d(TAG, "Service started, here's where we do the main work");
    }
}
