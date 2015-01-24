package com.fknussel.yamba;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

public class YambaWidget extends AppWidgetProvider {
    
    private static final String TAG = YambaWidget.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        this.onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, YambaWidget.class)));
    }

    // This method is called whenever our widget is to be updated,
    // so it’s where we’ll implement the main functionality of the widget.
    // When the widget gets registered in the manifest file,
    // we specify the update frequency we’d like.
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(TAG, "onUpdate");
        
        // Get the latest tweet
        Cursor cursor = context.getContentResolver().query(StatusContract.CONTENT_URI, null, null, null, StatusContract.DEFAULT_SORT);
        
        // We care only about the very latest status update from the online service.
        // So we position the cursor to the first element.
        // If one exists, it’s our latest status update.
        if (!cursor.moveToFirst()) {
            return;
        }
        
        // In the next few of lines of code, we extract data from the Cursor object
        // and store it in local variables
        String user = cursor.getString(cursor.getColumnIndex(StatusContract.Column.USER));
        String message = cursor.getString(cursor.getColumnIndex(StatusContract.Column.MESSAGE));
        long createdAt = cursor.getLong(cursor.getColumnIndex(StatusContract.Column.CREATED_AT));
        
        PendingIntent operation = PendingIntent.getActivity(context, -1, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        // Loop through all the instances of YambaWidget
        // Because the user could have multiple Yamba widgets installed,
        // we need to loop through them and update them all.
        // We don’t particularly care about the specific appWidgetId because we’re
        // doing identical work to update every instance of the Yamba widget.
        // The appWidgetId becomes an opaque handle we use to access each widget in turn.
        for (int appWidgetId : appWidgetIds) {
            
            // Update the view:
            // The actual view representing our widget is in another process.
            // To be precise, our widget is running inside the Home application,
            // which acts as its host and is the process we are updating. 
            // Hence the RemoteViews constructor. The Remote Views framework is a special
            // shared memory system designed specifically for widgets.
            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget);
           
            // Update the remote view:
            // Once we have the reference to our widget views’ Java memory space in another
            // process, we can update those views. In this case, we’re setting the status
            // data in the row that represents our widget.
            view.setTextViewText(R.id.list_item_text_user, user);
            view.setTextViewText(R.id.list_item_text_message, message);
            view.setTextViewText(R.id.list_item_text_created_at, DateUtils.getRelativeTimeSpanString(createdAt));
            
            view.setOnClickPendingIntent(R.id.list_item_text_user, operation);
            view.setOnClickPendingIntent(R.id.list_item_text_message, operation);
            
            // Update the widget:
            // Once we update the remote views, the AppWidgetManager call to updateApp Widget()
            // actually posts a message telling the system to update our widget.
            // This will happen asynchronously, but shortly after onUpdate() completes.
            appWidgetManager.updateAppWidget(appWidgetId, view);
        }
    }
}
