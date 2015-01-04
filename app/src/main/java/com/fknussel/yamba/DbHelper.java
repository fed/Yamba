package com.fknussel.yamba;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();

    public DbHelper(Context context) {
        super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Called only once first time we create the database
        String sql = String.format(
                "create table %s (%s int primary key, %s text, %s text, %s int)",
                StatusContract.TABLE,
                StatusContract.Column.ID,
                StatusContract.Column.USER,
                StatusContract.Column.MESSAGE,
                StatusContract.Column.CREATED_AT
        );
        Log.d(TAG, "onCreate with SQL: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Gets called whenever existing version != new version, i.e. schema changed.
        // This typically happens when you change the schema and release the application
        // update to users who already have older version of your app.
        // Typically you do ALTER TABLE ... but we wanna keep it simple here.
        db.execSQL("drop table if exists " + StatusContract.TABLE);
        onCreate(db);
    }
}
