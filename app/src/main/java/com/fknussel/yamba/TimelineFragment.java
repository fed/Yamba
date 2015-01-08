package com.fknussel.yamba;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

// We implement LoaderCallbacks<Cursor>, which is the set of callbacks
// that will be called when the data is available. CursorLoader consists
// of an interface and couple of callbacks that are called by the system
// when the data is ready for us, thus allowing for asynchronous loading
public class TimelineFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = TimelineFragment.class.getSimpleName();
    
    // This is the list of column names that map to the database tables, which provide our data
    private static final String[] FROM = {
            StatusContract.Column.USER,
            StatusContract.Column.MESSAGE,
            StatusContract.Column.CREATED_AT
    };

    // These are the view IDs to which we’ll bind the data.
    // The IDs are from a custom view: R.layout.list_item
    private static final int[] TO = {
            R.id.list_item_text_user,
            R.id.list_item_text_message,
            R.id.list_item_text_created_at
    };

    // This is an arbitrary ID that will help us make sure
    // that the loader calling back is the one we initiated
    private static final int LOADER_ID = 42;
    
    // Our adapter, to which we’ll connect both the data and the view
    private SimpleCursorAdapter mAdapter;

    // onActivityCreated() gets called when the activity hosting this fragment has been created
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Here, we create the adapter that glues together the data to the
        // custom view R.layout.list_item. It does that by binding the database
        // columns defined by the FROM array to view IDs identified by the TO array.
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, null, FROM, TO, 0);

        // We finally attach this adapter to the ListView that is already
        // embedded in the ListFragment, of which TimelineFragment is a subclass.
        setListAdapter(mAdapter);

        // When the fragment is created, we initiate the loading of the data.
        // This is now done on a separate thread, not blocking the rest of this method,
        // which as everything else, runs on the UI thread.
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    // --- Loader Callbacks ---
    // Executed on a non-UI thread
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // onCreateLoader() is where the data is actually loaded.
        // This runs on a worker thread and may take a long time to complete.
        if (id != LOADER_ID) {
            return null;
        }
        
        Log.d(TAG, "onCreateLoader");

        // A CursorLoader loads the data from the content provider
        return new CursorLoader(getActivity(), StatusContract.CONTENT_URI, null, null, null, StatusContract.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Once the data is loaded, the system will call back our code
        // via onLoadFinish ed(), passing in the data
        Log.d(TAG, "onLoadFinished with cursor: " + data.getCount());
        
        // We update the data that the adapter is using to update the list view.
        // The user finally gets the fresh timeline.
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // In case the data is stale or unavailable, we remove it from the view
        mAdapter.swapCursor(null);
    }
}