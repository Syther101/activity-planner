package com.example.ryanh.activityplanner.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by RyanH on 21/04/15.
 */

/**
 * Defines table and column names for the programme database.
 */
public class ProgrammeContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website. A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.ryanh.activityplanner";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.ryanh.activityplanner/school/programme is a valid path for
    // looking at a schools programmes.
    // Not sure if need to include URI for say programmes as as well as programme?
    public static final String PATH_SCHOOL = "school";
    public static final String PATH_SCHOOL_PROGRAMME = "school/programme";
    public static final String PATH_PROGRAMME_ACTIVITY = "school/programme/activity";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the Julian day at UTC.
    // date format: 2015-04-21 00:00:00
    public static long normalizeDate(long startDate) {
        // Normalize the start date to the beginning of the (UTC) day

        // Not sure if need this yet.
        return startDate;
    }

    /*
        Inner class that defines the table contents of the school table
        REMOVE: This is where you add the strings. (Similar to what has been done for WeatherEntry
     */
    public static final class SchoolEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCHOOL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCHOOL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCHOOL;

        // Table name
        public static final String TABLE_NAME = "school";

        // FireBaseQuery is what wil be sent to FireBase as the school query. E.g. CMP
        // Need to chnage to array in FireBase instead of CMP name?
        public static final String COLUMN_FIREBASE_QUERY = "school_code";

        // Human readable school string, provided by API. Because for styling,
        // Computing Science is more recognizable than CMP.
        public static final String COLUMN_SCHOOL_NAME = "school_name";

        //School description
        public static final String COLUMN_SCHOOL_DESC = "school_desc";

        // Date of last update retrieved from server, stored as long mlilliseconds
        public static final String COLUMN_SCHOOL_UPDATE = "last_update_date";

        // If school is active within the app
        // Not sure if needed
        public static final String COLUMN_SCHOOL_ACTIVE = "school_active";

        //School picture?

        // Find description for this.
        public static Uri buildSchoolUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildSchool(String school) {
            return CONTENT_URI.buildUpon().appendPath(school).build();
        }

        public static String getSchoolFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }

    public static final class ProgrammeEntry implements BaseColumns {

        //Think this is /school/programme
        //Not sure where this is used.
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCHOOL_PROGRAMME).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCHOOL_PROGRAMME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCHOOL_PROGRAMME;

        public static final String TABLE_NAME = "programme";

        // Name of program e.g. 2015 Summer Open Day
        public static final String COLUMN_PROG_NAME = "programme_name";

        // Description of program to go in detailed view
        public static final String COLUMN_PROG_DESC = "programme_description";

        // Column with the foreign key into the school table
        public static final String COLUMN_SCHOOL_KEY = "school_key";
        // Date, stored as long milliseconds since the epoch
        public static final String COLUMN_PROG_DATE = "date";

        //Find description for this
        public static Uri buildProgrammeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final Uri buildProgramme(String programme) {
            return CONTENT_URI.buildUpon().appendPath(programme).build();
        }

        public static final String getProgramme(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class ActivityEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROGRAMME_ACTIVITY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROGRAMME_ACTIVITY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROGRAMME_ACTIVITY;

        // Table name
        public static final String TABLE_NAME = "activity";

        // Name of Activity
        public static final String COLUMN_ACTIVITY_NAME = "activity_name";

        // Description of activity to go in detailed view
        public static final String COLUMN_ACTIVITY_DESC = "activity_desc";

        // Column with the foreign key into the programme table
        public static final String COLUMN_PROG_KEY = "programme_key";
        // Datetime, stored as long milliseconds since epoch
        public static final String COLUMN_ACTIVITY_DATE = "activity_datetime";

        public static final String COLUMN_ACTIVITY_LOCATION = "activity_location_name";

        // In order to uniquely pinpoint the location on the map when we launce the map intent
        // we store the latitude and longitude as returned by api.
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

        //Find description for this
        public static Uri buildActivityUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String getActivity(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }


    //Build school

    //Build programme

    //Build programme with school

    //Build activity

    //Build activity with programme
}
