package com.fknussel.yamba;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;


public class StatusActivity extends ActionBarActivity {

    private final String TAG = this.getClass().getSimpleName();
    
    private Button buttonTweet;
    private EditText editStatus;
    private TextView textCount;
    private int defaultTextColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        
        buttonTweet = (Button) findViewById(R.id.buttonTweet);
        editStatus = (EditText) findViewById(R.id.editStatus);
        textCount = (TextView) findViewById(R.id.textCount);
        
        // Clicking on the Tweet button posts the status to the web service
        buttonTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = editStatus.getText().toString();
                Log.d(TAG, "You just clicked on the TWEET button...");
                Log.d(TAG, "Your status: " + status);
                new PostTask().execute(status);
            }
        });
        
        // Characters remaining
        defaultTextColor = textCount.getTextColors().getDefaultColor();
        
        editStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Determine how many characters are left
                int count = 140 - editStatus.length();
                
                // Update the value on screen
                textCount.setText(Integer.toString(count));
                
                // Update the text color accordingly
                if (count < 10) {
                    textCount.setTextColor(Color.RED);
                } else if (count > 10 && count < 50) {
                    textCount.setTextColor(Color.YELLOW);
                } else if (count >= 50 && count < 140) {
                    textCount.setTextColor(Color.GREEN);
                } else if (count == 140) {
                    textCount.setTextColor(defaultTextColor);
                }
            }
        });
    }
    
    private final class PostTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            YambaClient yambaCloud = new YambaClient("student", "password");
            
            try {
                yambaCloud.postStatus( params[0] );
                return "Successfully posted";
            } catch(YambaClientException e) {
                e.printStackTrace();
                return "Failed to post to yamba service";
            }
            
        }
        
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(StatusActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
