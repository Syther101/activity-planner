package com.example.ryanh.activityplanner.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ryanh.activityplanner.data.ProgrammeContract.ActivityEntry;
import com.example.ryanh.activityplanner.data.ProgrammeContract.ProgrammeEntry;
import com.example.ryanh.activityplanner.data.ProgrammeContract.SchoolEntry;

/**
 * Created by RyanH on 21/04/15.
 */
public class ProgrammeDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "planner.db";

    public ProgrammeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Only thing not sure about is the firebase query. Whether it is needed?
        // Also use of autoincrement?

        final String SQL_CREATE_SCHOOL_TABLE = "CREATE TABLE " +
                SchoolEntry.TABLE_NAME + " (" +
                SchoolEntry._ID + " INTEGER PRIMARY KEY," +
                SchoolEntry.COLUMN_FIREBASE_QUERY + " TEXT UNIQUE NOT NULL, " +
                SchoolEntry.COLUMN_SCHOOL_NAME + " TEXT NOT NULL, " +
                SchoolEntry.COLUMN_SCHOOL_DESC + " TEXT NOT NULL, " +
                SchoolEntry.COLUMN_SCHOOL_UPDATE + " INTEGER NOT NULL " +
                " );";

        final String SQL_CREATE_PROGRAMME_TABLE = "CREATE TABLE " +
                ProgrammeEntry.TABLE_NAME + " (" +
                ProgrammeEntry._ID + " INTEGER PRIMARY KEY," +
                ProgrammeEntry.COLUMN_PROG_NAME + " TEXT NOT NULL, " +
                ProgrammeEntry.COLUMN_PROG_DESC + " TEXT NOT NULL, " +
                ProgrammeEntry.COLUMN_PROG_DATE + " INTEGER NOT NULL, " +
                ProgrammeEntry.COLUMN_SCHOOL_KEY + " INTEGER NOT NULL, " +

                // Set up the school column as a foreign key to school table
                " FOREIGN KEY (" + ProgrammeEntry.COLUMN_SCHOOL_KEY + ") REFERENCES " +
                SchoolEntry.TABLE_NAME + " (" + SchoolEntry._ID + ");";

        final String SQL_CREATE_ACTIVITY_TABLE = "CREATE TABLE " +
                ActivityEntry.TABLE_NAME + " (" +
                ActivityEntry._ID + " INTEGER PRIMARY KEY," +
                ActivityEntry.COLUMN_ACTIVITY_NAME + " TEXT NOT NULL, " +
                ActivityEntry.COLUMN_ACTIVITY_DESC + " TEXT NOT NULL, " +
                ActivityEntry.COLUMN_ACTIVITY_DATE + " INTEGER NOT NULL, " +
                ActivityEntry.COLUMN_ACTIVITY_LOCATION + " TEXT NOT NULL, " +
                ActivityEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                ActivityEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                ActivityEntry.COLUMN_PROG_KEY + " INTEGER NOT NULL, " +

                // Setup the programme column as a foreign key to programme table
                " FOREIGN KEY (" + ActivityEntry.COLUMN_PROG_KEY + ") REFERENCES " +
                ProgrammeEntry.TABLE_NAME + " (" + ProgrammeEntry._ID + ");";


        sqLiteDatabase.execSQL(SQL_CREATE_SCHOOL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PROGRAMME_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ACTIVITY_TABLE);

    }

}
