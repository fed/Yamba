package com.fknussel.yamba;

import android.provider.BaseColumns;

public class StatusContract {

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
}
