package com.fknussel.yamba;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class StatusProvider extends ContentProvider {

    private static final UriMatcher sURIMatcher = new UriMatcher( UriMatcher.NO_MATCH);
    private static final String TAG = StatusProvider.class.getSimpleName();
    private DbHelper dbHelper;

    static {
        sURIMatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE, StatusContract.STATUS_DIR);
        sURIMatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE + "/#", StatusContract.STATUS_ITEM);
    }
    
    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        Log.d(TAG, "onCreated");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Here we use SQLiteQueryBuilder to make it easier to put
        // together a potentially complex query statement
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        
        // Don’t forget to specify what table you are working on
        qb.setTables( StatusContract.TABLE );
        
        // Again, we use the matcher to see what type of the URI we got
        switch (sURIMatcher.match(uri)) {
            case StatusContract.STATUS_DIR:
                break;
            case StatusContract.STATUS_ITEM:
                // If the URI contains the ID of the record to query, we need to extract that ID
                // and include it in the query. This is where SQLiteQueryBuilder makes it easier
                // than building a long string
                qb.appendWhere(StatusContract.Column.ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        // Specify the sort order for the returned data,
        // using default if sort order hasn’t been provided
        String orderBy = (TextUtils.isEmpty(sortOrder))
                ? StatusContract.DEFAULT_SORT
                : sortOrder;

        // We need to open the database, in this case just for reading
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Note that the database call has two additional parameters that correspond to
        // the GROUPING and HAVING components of a SELECT statement in SQL.
        // Because content providers do not support this feature, we simply pass in null.
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Register for uri changes. Tell this cursor that it depends on the data
        // as specified by this URI. In other words, when the insert(), update(), or delete()
        // notify the app that the data has changed, this cursor will know that it may
        // want to refresh its data.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d(TAG, "queried records: " + cursor.getCount());
        
        // Return the actual data in the form of a cursor
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        // getType() uses sURIMatcher, an instance of the UriMatcher API class, to determine
        // whether the URI has an ID part. Based on the type of URI we have, getType() returns
        // the appropriate MIME type that we’ve defined previously in StatusContract.
        switch (sURIMatcher.match(uri)) {
            case StatusContract.STATUS_DIR:
                Log.d(TAG, "gotType: " + StatusContract.STATUS_TYPE_DIR);
                return StatusContract.STATUS_TYPE_DIR;
            case StatusContract.STATUS_ITEM:
                Log.d(TAG, "gotType: " + StatusContract.STATUS_TYPE_ITEM);
                return StatusContract.STATUS_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = null;
        
        // Assert correct URI
        if (sURIMatcher.match(uri) != StatusContract.STATUS_DIR) {
            throw new IllegalArgumentException("Illegal uri: " + uri);
        }
        
        // Open the database for writing
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Attempt to insert the values into the database and, upon a successful insert,
        // receive the ID of the new record from the database.
        long rowId = db.insertWithOnConflict(StatusContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        // Was insert successful?
        if (rowId != -1) {
            long id = values.getAsLong(StatusContract.Column.ID);
            ret = ContentUris.withAppendedId(uri, id);
            Log.d(TAG, "inserted uri: " + ret);

            // Notify observers of this content provider that this particular data has changed
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ret;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String where;
        
        switch (sURIMatcher.match(uri)) {
            case StatusContract.STATUS_DIR:
                // so we count deleted rows
                where = (selection == null) ? "1" : selection;
                break;
            case StatusContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = StatusContract.Column.ID + "=" + id + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.delete(StatusContract.TABLE, where, selectionArgs);
        
        if (ret > 0) {
            // Notify that data for this uri has changed
            getContext().getContentResolver().notifyChange(uri, null);
        }
        
        Log.d(TAG, "deleted records: " + ret);
        
        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        /*
        To update the data via the Content Provider API, we need:
        
        1. The URI of the provider: This may or may not contain an ID.
            If it does, the ID indicates the specific record that needs to be updated,
            and we can ignore the selection. If the ID is not specified, it means that we
            are updating many records and need the selection to indicate which are to be changed.
            
        2. The values to be updated: The format of this parameter is a set of name-value pairs
            that represents column names and new values.
            
        3. Any selection and arguments that go with it: These together make up a WHERE clause
            in SQL, selecting the records that will change. The selection and its arguments are
            omitted when there is an ID, because the ID is enough to select the record that
            is being updated.
        */

        String where;

        // First, we check the type of URI that was passed in
        switch (sURIMatcher.match(uri)) {

            case StatusContract.STATUS_DIR:
                // If the URI doesn’t contain the ID, we don’t have much else to worry about
                // so we count updated rows
                where = selection;
                break;

            case StatusContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                // However, if the URI does have an ID as part of the path,
                // we need to extract it and update our WHERE statement to account for that ID
                where = StatusContract.Column.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( "
                        + selection + " )");
                break;
            
            default:
                // We shouldn’t be seeing any other type of URI
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        // Open the database for writing the updates and call update(), passing in this data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.update(StatusContract.TABLE, values, where, selectionArgs);

        if(ret>0) {
            // If the update was successful (i.e., the number of affected rows is more than zero),
            // we notify any interested parties that the data has changed
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d(TAG, "updated records: " + ret);
        
        return ret;
    }
}
