package com.fknussel.yamba;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

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

        setEmptyText("Loading data...");

        // Here, we create the adapter that glues together the data to the
        // custom view R.layout.list_item. It does that by binding the database
        // columns defined by the FROM array to view IDs identified by the TO array.
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, null, FROM, TO, 0);

        // We attach a custom ViewBinder instance to our stock adapter
        mAdapter.setViewBinder(new TimelineViewBinder());
        
        // We finally attach this adapter to the ListView that is already
        // embedded in the ListFragment, of which TimelineFragment is a subclass.
        setListAdapter(mAdapter);

        // When the fragment is created, we initiate the loading of the data.
        // This is now done on a separate thread, not blocking the rest of this method,
        // which as everything else, runs on the UI thread.
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    // onListItemClick() is called when an item in the list is clicked on
    public void onListItemClick(ListView l, View v, int position, long id) {
        // We ask the fragment manager for DetailsFragment
        DetailsFragment fragment = (DetailsFragment) getFragmentManager().findFragmentById(R.id.fragment_details);
        
        // Is details fragment visible? It is quite possible that DetailsFragment is not visible,
        // such as with the portrait orientation of the small phone screen.
        // In that case, DetailsFragment will be null. If DetailsFragment is not null, it’s visible.
        // In that case, we simply call our method updateView() to have the fragment fetch
        // the data from the content provider and update its view.
        // Otherwise, we launch the details activity, which will do the same once
        // it creates and attaches this fragment to it.
        if (fragment != null && fragment.isVisible()) {
            fragment.updateView(id);
        } else {
            startActivity(new Intent(getActivity(), DetailsActivity.class) .putExtra(StatusContract.Column.ID, id));
        }
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


    /** The actual implementation of a ViewBinder instance that handles custom
     * binding of data to view. Notice that we are implementing it as an inner class.
     * There’s no reason for any other class to use it, and thus it shouldn’t
     * be exposed to the outside world. Also notice that it is static final,
     * meaning that it’s a constant.
     * */
    class TimelineViewBinder implements SimpleCursorAdapter.ViewBinder {

        // This method gets called for each data element that
        // needs to be bound to a particular view
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            
            // First we check whether this view is the view we care about, i.e.,
            // our TextView representing when the status was created.
            // If not, we return false, which causes the adapter to handle the bind itself
            // in the standard manner. If it is our view, we move on and do the custom bind.
            if (view.getId() != R.id.list_item_text_created_at) {
                return false;
            }

            // Convert timestamp to relative time
            // We get the raw timestamp value from the cursor data
            long timestamp = cursor.getLong(columnIndex);
            
            // Using the same Android helper method we used in our previous example,
            // DateUtils.getRelativeTimeSpanString(), we convert the timestamp to a
            // human-readable format. This is that business logic that we are injecting.
            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(timestamp);
            
            // Update the text on the actual view
            ((TextView) view).setText(relativeTime);
            
            // Return true so that SimpleCursorAdapter does not process bindView()
            // on this element in its standard way
            return true;
        }
    }
}
