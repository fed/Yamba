package com.fknussel.yamba;

import android.net.Uri;
import android.provider.BaseColumns;

public class StatusContract {

    // DB specific constants
    // ---------------------------
    // This is the actual SQLite file that will contain the database
    public static final String DB_NAME = "timeline.db";
    
    // Database schemas are versioned.
    public static final int DB_VERSION = 1;

    // Sort order
    public static final String DEFAULT_SORT = Column.CREATED_AT + " DESC";
    
    // Table name
    public static final String TABLE = "status";

    // Table definition
    public class Column {
        // Although the ID could be any name, there’s a convention in Android
        // to name it _id, for which it provides an API-level contract as well.
        // You should try to use this whenever you define an ID field.
        public static final String ID = BaseColumns._ID;
        public static final String USER = "user";
        public static final String MESSAGE = "message";
        public static final String CREATED_AT = "created_at";
    }
    
    // Provider specific constants
    // ---------------------------
    // content://com.fknussel.yamba.StatusProvider/status
    public static final String AUTHORITY = "com.fknussel.yamba.StatusProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE);
    public static final int STATUS_ITEM = 1;
    public static final int STATUS_DIR = 2;
    public static final String STATUS_TYPE_ITEM = "vnd.android.cursor.item/vnd.com.fknussel.yamba.provider.status";
    public static final String STATUS_TYPE_DIR = "vnd.android.cursor.dir/vnd.com.fknussel.yamba.provider.status";
    
}
