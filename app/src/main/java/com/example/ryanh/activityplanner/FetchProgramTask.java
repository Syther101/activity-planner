package com.example.ryanh.activityplanner;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ryanh.activityplanner.data.ProgrammeContract.ActivityEntry;
import com.example.ryanh.activityplanner.data.ProgrammeContract.ProgrammeEntry;
import com.example.ryanh.activityplanner.data.ProgrammeContract.SchoolEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by RyanH on 21/04/15.
 */
public class FetchProgramTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchProgramTask.class.getSimpleName();

    private Context context;

    public FetchProgramTask(Context context) {
        this.context = context;
    }

    private boolean DEBIG = true;

    /**
     * Helper method to handle insertion of a new activity in the programme activity database
     */
    // First check, if the activity with this name exists in the db
    // If it exists, return the current ID
    // Otherwise, insert it using the content resolver and the base URi

    // TODO: Need to check if I will need 2 or more of these or can I fit all in one?
    long addSchool(String schoolCode, String schoolName, String schoolDesc) {
        long schoolId;

        // First, check if the school with this school exists in the db
        Cursor schoolCursor = context.getContentResolver().query(
                SchoolEntry.CONTENT_URI,
                new String[]{SchoolEntry._ID},
                SchoolEntry.COLUMN_FIREBASE_QUERY + " = ?",
                new String[]{schoolName},
                null);

        if (schoolCursor.moveToFirst()) {
            int schoolIdIndex = schoolCursor.getColumnIndex(SchoolEntry._ID);
            schoolId = schoolCursor.getLong(schoolIdIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty
            // simple. First create a ContentValues object to hold the data you want to
            // insert.
            ContentValues schoolValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            schoolValues.put(SchoolEntry.COLUMN_FIREBASE_QUERY, schoolCode);
            schoolValues.put(SchoolEntry.COLUMN_SCHOOL_NAME, schoolName);
            schoolValues.put(SchoolEntry.COLUMN_SCHOOL_DESC, schoolDesc);
            schoolValues.put(SchoolEntry.COLUMN_SCHOOL_UPDATE, System.currentTimeMillis());

            //Calendar c = Calendar.getInstance();
            long normalisedTime;

            // Finally, insert school data into the database.
            //TODO: Might be where we hit an error where URI stuff is incorrect.
            Uri insertedUri = context.getContentResolver().insert(
                    SchoolEntry.CONTENT_URI,
                    schoolValues
            );

            // The resulting URI contains the ID for the row. Extract the schoolId from the Uri.
            schoolId = ContentUris.parseId(insertedUri);
        }
        schoolCursor.close();
        return schoolId;
    }

    long addProgram(String programName, String programDesc, long programDate, long schoolKey) {
        long programId;

        // First, check if the program with this program exists in the db
        // uri, projection, selection, selectionArgs, sortOrder
        Cursor programCursor = context.getContentResolver().query(
                ProgrammeEntry.CONTENT_URI,
                new String[]{ProgrammeEntry._ID},
                ProgrammeEntry.COLUMN_PROG_NAME + " = ?",
                new String[]{programName},
                null);

        if (programCursor.moveToFirst()) {
            int programIdIndex = programCursor.getColumnIndex(ProgrammeEntry._ID);
            programId = programCursor.getLong(programIdIndex);
        } else {
            // Create a ContentValues object to hold the data you want to insert.
            ContentValues programValues = new ContentValues();

            ContentValues programmeValues = new ContentValues();

            //Insert current school, school data into programmeValues
            programmeValues.put(ProgrammeEntry.COLUMN_SCHOOL_KEY, schoolKey);
            programmeValues.put(ProgrammeEntry.COLUMN_PROG_NAME, programName);
            programmeValues.put(ProgrammeEntry.COLUMN_PROG_DESC, programDesc);
            programmeValues.put(ProgrammeEntry.COLUMN_PROG_DATE, programDate);

            // Insert program data into the database.
            Uri insertedUri = context.getContentResolver().insert(
                    ProgrammeEntry.CONTENT_URI,
                    programmeValues
            );

            // The resulting URI contains the ID for the row. Extract the programId from the URI
            programId = ContentUris.parseId(insertedUri);
        }
        programCursor.close();
        return programId;
    }

    // Think need to get all data from FireBase/JSON and then code written earlier will seperate
    // it.

    // TODO: Again Need to check if I will need 2 or more of these or can I fit all in one? with
    // relation to schoolCode, ProgrammeCode, etc.?
    private void getProgrammeDataFromJson(String programmeJsonStr) throws JSONException {
        // Now we have a String representing the complete programme data in JSON format.
        // Fortunately parsing is easy: constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the name of the JSON object that need to be extracted.

        // School information
        final String UEA_SCHOOL_LIST = "Schools";

        //final String UEA_SCHOOL_CODE = "" <-- This is where it = whatever is in array
        final String UEA_SCHOOL_CODE = "schoolCode";
        final String UEA_SCHOOL_NAME = "schoolName";
        final String UEA_SCHOOL_DESC = "schoolDesc";
        // Not sure if need last updated here
        //School location & Image

        // Programme information. Each programme info is an element of Programs array.
        final String UEA_PROGRAMME_LIST = "Programs";

        final String UEA_PROGRAMME_NAME = "programName";
        final String UEA_PROGRAMME_DESC = "programDesc"; //<-- Not in FB yet
        final String UEA_PROGRAMME_DATE = "programDate"; //<-- Change this to DT?

        // Activity information. Each activity is an element of activity array.
        final String UEA_ACTIVITY_LIST = "Events";

        final String UEA_ACTIVITY_NAME = "eventName";
        final String UEA_ACTIVITY_DESC = "eventDesc";
        final String UEA_ACTIVITY_DATETIME = "dt"; //<-- need to convert from epoch
        final String UEA_ACTIVITY_LOCATION = "eventLocation";
        // Might need lat long here?

        try {
            JSONObject programmeJson = new JSONObject(programmeJsonStr);
            //Not sure if need CMP school list here?

            //Schools[]
            JSONArray schoolsArray = programmeJson.getJSONArray(UEA_SCHOOL_LIST);

            //Vector for inserting data in the database
            //Should be fine as schoolsArray.length as other data will come from arrays within?
            Vector<ContentValues> cVVector = new Vector<ContentValues>(schoolsArray.length());

            // For every school
            for (int i = 0; i < schoolsArray.length(); i++) {
                String schoolCode;
                String schoolName;
                String schoolDesc;

                // Schools[i]
                JSONObject schoolJson = schoolsArray.getJSONObject(i);

                // Schools[i].schoolCode
                schoolCode = schoolJson.getString(UEA_SCHOOL_CODE);
                schoolName = schoolJson.getString(UEA_SCHOOL_NAME);
                schoolDesc = schoolJson.getString(UEA_SCHOOL_DESC);


                // School[i].Programs[]
                JSONArray programmeArray = schoolJson.getJSONArray(UEA_PROGRAMME_LIST);

                long schoolId = addSchool(schoolCode, schoolName, schoolDesc);

                // For every program. School[i].Program[i]
                for (int ii = 0; ii < programmeArray.length(); ii++) {
                    String programmeName;
                    String programmeDesc;
                    long programmeDate;

                    // Schools[i].Programs[i]
                    JSONObject programJson = programmeArray.getJSONObject(ii);

                    // Schools[i].Programs[i].programName
                    programmeName = programJson.getString(UEA_PROGRAMME_NAME);
                    programmeDesc = programJson.getString(UEA_PROGRAMME_DESC);
                    programmeDate = programmeJson.getLong(UEA_PROGRAMME_DATE);

                    // School[i].Programs[i].Events[]
                    JSONArray activityArray = programJson.getJSONArray(UEA_ACTIVITY_LIST);

                    long programId = addProgram(programmeName, programmeDesc, programmeDate,
                            schoolId);

                    for (int iii = 0; iii < activityArray.length(); iii++) {
                        String activityName;
                        String activityDesc;
                        long activityDT;
                        String activityLocation;
                        // Not sure if going to do lat long

                        // Schools[i].Programs[i].Events[i]
                        JSONObject activityJson = activityArray.getJSONObject(iii);

                        // Schools[i].Programs[i].Events[i].eventName
                        activityName = activityJson.getString(UEA_ACTIVITY_NAME);
                        activityDesc = activityJson.getString(UEA_ACTIVITY_DESC);
                        activityDT = activityJson.getLong(UEA_ACTIVITY_DATETIME);
                        activityLocation = activityJson.getString(UEA_ACTIVITY_LOCATION);

                        ContentValues activityValues = new ContentValues();

                        activityValues.put(ActivityEntry.COLUMN_PROG_KEY, programId);
                        activityValues.put(ActivityEntry.COLUMN_ACTIVITY_NAME, activityName);
                        activityValues.put(ActivityEntry.COLUMN_ACTIVITY_DESC, activityDesc);
                        activityValues.put(ActivityEntry.COLUMN_ACTIVITY_DATE, activityDT);

                        cVVector.add(activityValues);
                    }
                }
            }

            int inserted = 0;
            // Add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = context.getContentResolver().bulkInsert(ActivityEntry.CONTENT_URI,
                        cvArray);
            }

            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        // Declared out here so can be closed in finally.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string
        String programmeJsonStr = null;

        try {

            final String UEA_PLANNER_BASE_URL =
                    "https://uea-openday-program.firebaseio.com/.json";

            URL url = new URL(UEA_PLANNER_BASE_URL);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input steam into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary.
                // Does make debugging easier!
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Steam was empty
                return null;
            }
            programmeJsonStr = buffer.toString();
            getProgrammeDataFromJson(programmeJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }
}
