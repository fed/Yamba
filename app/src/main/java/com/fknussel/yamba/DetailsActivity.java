package com.fknussel.yamba;

import android.app.Activity;
import android.os.Bundle;

public class DetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if this activity was created before
        // We only create the new fragment when onCreate() is called first time around
        if (savedInstanceState == null) {
            // Create a new instance of the fragment
            DetailsFragment fragment = new DetailsFragment();
            
            // Get the fragment transaction from the manager, and add this fragment to this activity
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, fragment.getClass().getSimpleName())
                    .commit();
        }
    }
}
