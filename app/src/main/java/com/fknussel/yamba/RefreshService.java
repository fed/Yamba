package com.fknussel.yamba;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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

        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // ContentValues is a simple data structure consisting of name-value pairs
        // that maps database table names to their respective values
        ContentValues values = new ContentValues();

        YambaClient yambaCloud = new YambaClient(username, password);

        try {
            List<YambaClient.Status> timeline = yambaCloud.getTimeline(20);
            for (YambaClient.Status status : timeline) {
                // For each record, we create a content value. We are reusing 
                // the same Java object, clearing it each time we start the loop and
                // populating appropriate values for the status data.
                values.clear();
                values.put(StatusContract.Column.ID, status.getId());
                values.put(StatusContract.Column.USER, status.getUser());
                values.put(StatusContract.Column.MESSAGE, status.getMessage());
                values.put(StatusContract.Column.CREATED_AT, status.getCreatedAt().getTime());
                
                // Notice that we are not piecing together an SQL statement here, but
                // rather using a prepared statement approach to inserting into the database
                // We use insertWithOnConflict() instead of insert(), which allows us to specify
                // what to do in case of a constract violation (e.g., duplicate ID).
                // Our call passes the CONFLICT_IGNORE parameter to tell the database
                // to just silently ignore our attempt to update it.
                db.insertWithOnConflict(StatusContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            }
        } catch(YambaClientException e) {
            Log.e(TAG, "Failed to fetch timeline", e);
            e.printStackTrace();
        }
    }
}
