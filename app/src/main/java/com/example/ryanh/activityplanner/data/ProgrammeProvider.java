package com.example.ryanh.activityplanner.data;


import android.content.ContentProvider;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.ryanh.activityplanner.data.ProgrammeContract.SchoolEntry;
import com.example.ryanh.activityplanner.data.ProgrammeContract.ProgrammeEntry;
import com.example.ryanh.activityplanner.data.ProgrammeContract.ActivityEntry;

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
    private static final String sSchoolSelection =
            SchoolEntry.TABLE_NAME +
                    "." + SchoolEntry.COLUMN_FIREBASE_QUERY + " = ? ";

    private static final String sProgrammeSelection =
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
        String selection = sSchoolSelection;

        return sSchoolProgrammeQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection, //Columns
                selection, //Where school = CMP
                selectionArgs, // Values for where
                null, // Group by
                null, // Filter row
                sortOrder); //Sort

    }

    private Cursor getProgrammeActivities(Uri uri, String[] projection, String sortOrder) {
        String programmeId = ProgrammeEntry.getProgramme(uri);

        String[] selectionArgs = new String[]{programmeId};

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
            case SCHOOL:
                return null;
            case SCHOOL_WITH_PROGRAMME:
                return null;
            case PROGRAMME_WITH_ACTIVITY:
                return null;
            case PROGRAMME:
                return null;
            case ACTIVITY:
                return null;
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
            case PROGRAMME_WITH_ACTIVITY: {
                retCursor =
            }
        }
    }

    //getProgrammesBySchool

    //getActivitiesByProgramme
}
