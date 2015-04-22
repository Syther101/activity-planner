package com.example.ryanh.activityplanner;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by RyanH on 21/04/15.
 */
public class FetchProgramTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchProgramTask.class.getSimpleName();

    private Context context;

    private FetchProgramTask(Context context) {
        this.context = context;
    }

    private boolean DEBIG = true;

    /**
     * Helper method to handle insertion of a new activity in the programme activity database
     */
    // First check, if the activity with this name exists in the db
    // If it exists, return the current ID
    // Otherwise, insert it using the content resolver and the base URi

}
