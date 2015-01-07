package com.fknussel.yamba;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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

        Log.d(TAG, "onStarted");
        
        // ContentValues is a simple data structure consisting of name-value pairs
        // that maps database table names to their respective values
        ContentValues values = new ContentValues();

        YambaClient cloud = new YambaClient(username, password);

        try {
            int count = 0;
            
            List<YambaClient.Status> timeline = cloud.getTimeline(20);
            
            for (YambaClient.Status status : timeline) {
                // For each record, we create a content value. We are reusing 
                // the same Java object, clearing it each time we start the loop and
                // populating appropriate values for the status data.
                values.clear();
                values.put(StatusContract.Column.ID, status.getId());
                values.put(StatusContract.Column.USER, status.getUser());
                values.put(StatusContract.Column.MESSAGE, status.getMessage());
                values.put(StatusContract.Column.CREATED_AT, status.getCreatedAt().getTime());

                // Here we use getContentResolver() from the current context to get the access
                // to content provider’s insert(). The actual provider to use is resolved via
                // the URI that we pass: StatusContract.CONTENT_URI, which is registered with
                // the system via the application AndroidManifest.xml file.
                // This is how the content resolver knows that it’s StatusProvider
                // on the receiving end of this insert() call.
                Uri uri = getContentResolver().insert(StatusContract.CONTENT_URI, values);

                if (uri != null) {
                    // Count how many successful inserts we actually had
                    count++;
                    Log.d(TAG, String.format("%s: %s", status.getUser(), status.getMessage()));
                }
            }
        } catch(YambaClientException e) {
            Log.e(TAG, "Failed to fetch timeline", e);
            e.printStackTrace();
        }
    }
}
