package com.fknussel.yamba;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

import java.util.List;


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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");

        // Check that username and password are not empty.
        // If empty, Toast a message to set login info and bounce to SettingActivity.
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please set your login information", Toast.LENGTH_SHORT).show();
            return;
        }

        YambaClient yambaCloud = new YambaClient(username, password);

        try {
            List<YambaClient.Status> timeline = yambaCloud.getTimeline(20);
            for (YambaClient.Status status : timeline) {
                Log.d(TAG, String.format("%s: %s", status.getUser(),status.getMessage()));
            }
        } catch(YambaClientException e) {
            Log.e(TAG, "Failed to fetch timeline", e);
            e.printStackTrace();
        }
    }
}
