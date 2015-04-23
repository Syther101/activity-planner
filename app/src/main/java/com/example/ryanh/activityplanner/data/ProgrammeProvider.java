package com.example.ryanh.activityplanner.data;


import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.ryanh.activityplanner.data.ProgrammeContract.ActivityEntry;
import com.example.ryanh.activityplanner.data.ProgrammeContract.ProgrammeEntry;
import com.example.ryanh.activityplanner.data.ProgrammeContract.SchoolEntry;

/**
 * Created by RyanH on 21/04/15.
 */
public class ProgrammeProvider extends ContentProvider {

    // The URI matcher used by this content provider
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ProgrammeDbHelper dbHelper;

    static final int SCHOOL = 100;
    static final int SCHOOL_WITH_PROGRAMME = 101;
    static final int PROGRAMME_WITH_ACTIVITY = 102;

    static final int PROGRAMME = 200;

    static final int ACTIVITY = 300;

    //Don't think need query builder
    private static final SQLiteQueryBuilder sSchoolProgrammeQueryBuilder;

    private static final SQLiteQueryBuilder sProgrammeActivityQueryBuilder;

    static {
        sSchoolProgrammeQueryBuilder = new SQLiteQueryBuilder();
        sProgrammeActivityQueryBuilder = new SQLiteQueryBuilder();

        sSchoolProgrammeQueryBuilder.setTables(
            ProgrammeEntry.TABLE_NAME + " INNER JOIN " +
                    SchoolEntry.TABLE_NAME +
                    " ON " + ProgrammeEntry.TABLE_NAME +
                    "." + ProgrammeEntry.COLUMN_SCHOOL_KEY +
                    " = " + SchoolEntry.TABLE_NAME +
                    "." + SchoolEntry._ID
        );

        sProgrammeActivityQueryBuilder.setTables(
                ActivityEntry.TABLE_NAME + " INNER JOIN " +
                        ProgrammeEntry.TABLE_NAME +
                        " ON " + ActivityEntry.TABLE_NAME +
                        "." + ActivityEntry.COLUMN_PROG_KEY +
                        " = " + ProgrammeEntry.TABLE_NAME +
                        "." + ProgrammeEntry._ID
        );
    }

    //school.CMP = ?
    //Not sure if this is needed. Would be needed for query but no data is going to be
    //queried in the app?
    private static final String sSchoolProgrammeSelection =
            SchoolEntry.TABLE_NAME +
                    "." + SchoolEntry.COLUMN_FIREBASE_QUERY + " = ? ";

    private static final String sProgrammeActivitySelection =
            ProgrammeEntry.TABLE_NAME +
                    "." + ProgrammeEntry._ID + " = ? ";


    // These are for getting combined tables. Not sure if need as will only be needing 1
    // results at any one time?

    // SCHOOL

    // PROGRAMME WITH SCHOOL. Mean find programme using school when appending query?

    // ACTIVITY WITH PROGRAMME.

    private Cursor getSchoolProgrammes(Uri uri, String[] projection, String sortOrder) {
        String schoolCode = SchoolEntry.getSchoolFromUri(uri);

        String[] selectionArgs = new String[]{schoolCode};
        String selection = sSchoolProgrammeSelection;

        return sSchoolProgrammeQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection, //Columns
                selection, //Where school
                selectionArgs, // Values for where = CMP
                null, // Group by
                null, // Filter row
                sortOrder); //Sort

    }

    private Cursor getProgrammeActivities(Uri uri, String[] projection, String sortOrder) {
        String programmeId = ProgrammeEntry.getProgramme(uri);

        String[] selectionArgs = new String[]{programmeId};
        String selection = sProgrammeActivitySelection;

        return sProgrammeActivityQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection, //Columns
                selection, //Where programme
                selectionArgs, // Values for where = 0
                null, // Group by
                null, // Filter row
                sortOrder); //Sort
    }

    /*
        Here is where we create the UriMatcher. The UriMatcher will match each URI
        to the SCHOOL, SCHOOL_PROGRAMME, PROGRAMME_ACTIVITY integer cinstants above.
        Can test this using the testUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        //All paths added to the UriMatcher have a corresponding code to return when a match
        // is found. The code passed into the constructor represents the code to return for
        // to return for the root URI. It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ProgrammeContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, ProgrammeContract.PATH_SCHOOL, SCHOOL);
        //matcher.addURI(authority, ProgrammeContract.PATH_SCHOOL + "/", SCHOOL);
        matcher.addURI(authority, ProgrammeContract.PATH_SCHOOL + "/*/#", SCHOOL_WITH_PROGRAMME);
        matcher.addURI(authority, ProgrammeContract.PATH_SCHOOL + "/#/#", PROGRAMME_WITH_ACTIVITY);

        matcher.addURI(authority, ProgrammeContract.PATH_SCHOOL, PROGRAMME);
        matcher.addURI(authority, ProgrammeContract.PATH_SCHOOL, ACTIVITY);
        return matcher;
    }

    /*
        Create dBHelper for later use.
     */
    @Override
    public boolean onCreate() {
        dbHelper = new ProgrammeDbHelper(getContext());
        return true;
    }

    /*
        getType function that uses the UriMatcher. You can
        test this by uncommenting testGetType in TestProvider.
     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SCHOOL: //Single school item?
                return SchoolEntry.CONTENT_TYPE;
            case SCHOOL_WITH_PROGRAMME: //Multiple school programmes
                return ProgrammeEntry.CONTENT_TYPE;
            case PROGRAMME_WITH_ACTIVITY: //Multiple programme activities
                return ActivityEntry.CONTENT_TYPE;
            case PROGRAMME: // Single programme item
                return ProgrammeEntry.CONTENT_ITEM_TYPE;
            case ACTIVITY: // Single activity item
                return ActivityEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;

        switch(sUriMatcher.match(uri)) {
            // "CMP/0"
            case SCHOOL_WITH_PROGRAMME: {
                retCursor = getSchoolProgrammes(uri, projection, sortOrder);
                break;
            }
            // "CMP/0/0" or something along these lines
            case PROGRAMME_WITH_ACTIVITY: {
                retCursor = getProgrammeActivities(uri, projection, sortOrder);
                break;
            }
            case SCHOOL: {
                retCursor = dbHelper.getReadableDatabase().query(
                        SchoolEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PROGRAMME: {
                retCursor = dbHelper.getReadableDatabase().query(
                        SchoolEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    //insert function
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case SCHOOL: {
                long _id = db.insert(SchoolEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = SchoolEntry.buildSchoolUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case PROGRAMME: {
                //TODO: Check to see if need to normalise date.
                long _id = db.insert(ProgrammeEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = ProgrammeEntry.buildProgrammeUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case ACTIVITY: {
                //TODO: Again check to see if activity needs normalised date
                long _id = db.insert(ActivityEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = ActivityEntry.buildActivityUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            }
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case SCHOOL:
                rowsDeleted = db.delete(
                        SchoolEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PROGRAMME:
                rowsDeleted = db.delete(
                        ProgrammeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ACTIVITY:
                rowsDeleted = db.delete(
                        ActivityEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null delete all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    //TODO: This is where normalise date would go, again check dis.

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case SCHOOL:
                rowsUpdated = db.update(SchoolEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PROGRAMME:
                rowsUpdated = db.update(ProgrammeEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case ACTIVITY:
                rowsUpdated = db.update(ActivityEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        // Could put this switch in the for loop if they end up all being the same.

        switch (match) {
            case PROGRAMME: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ProgrammeEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case ACTIVITY: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ActivityEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount ++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the
    // testing framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }

    //getProgrammesBySchool

    //getActivitiesByProgramme
}
