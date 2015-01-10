package com.fknussel.yamba;

import android.app.Fragment;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
    
    private TextView textUser, textMessage, textCreatedAt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // We’re inflating the R.layout.list_item view that we also use for the list
        View view = inflater.inflate(R.layout.list_item, null, false);
        
        textUser = (TextView) view.findViewById(R.id.list_item_text_user);
        textMessage = (TextView) view.findViewById(R.id.list_item_text_message);
        textCreatedAt = (TextView) view.findViewById(R.id.list_item_text_created_at);
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        
        // In onResume() we know that this fragment just got redisplayed on the screen.
        // So, we need to update it. To do so, we need to extract the ID for the tweet that
        // we are updating for. We’ll assume that whoever requested this fragment to be
        // displayed has passed on that ID to us via the intent that started the activity
        // this fragment is part of. This is similar to a web page for an ecommerce website
        // that you pass in the SKU ID in order to pick the right product to display.
        long id = getActivity().getIntent().getLongExtra( StatusContract.Column.ID, -1);
        updateView(id);
    }

    // This custom function goes out and pulls the data for the given ID
    // from the content provider, and updates the view of this fragment.
    public void updateView(long id) {
        if (id == -1) {
            textUser.setText("");
            textMessage.setText("");
            textCreatedAt.setText("");
            return;
        }

        Uri uri = ContentUris.withAppendedId(StatusContract.CONTENT_URI, id);
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        
        if (!cursor.moveToFirst())
            return;
        
        String user = cursor.getString(cursor.getColumnIndex(StatusContract.Column.USER));
        String message = cursor.getString(cursor.getColumnIndex(StatusContract.Column.MESSAGE));
        long createdAt = cursor.getLong(cursor .getColumnIndex(StatusContract.Column.CREATED_AT));
        
        textUser.setText(user);
        textMessage.setText(message);
        textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(createdAt));
    }
}
