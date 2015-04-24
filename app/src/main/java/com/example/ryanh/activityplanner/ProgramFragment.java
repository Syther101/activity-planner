//package com.example.ryanh.activityplanner;
//
///**
// * Created by RyanH on 24/04/15.
// */
//
//import android.database.Cursor;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.LoaderManager;
//
//import com.example.ryanh.activityplanner.data.ProgrammeContract.ProgrammeEntry;
//
//public class ProgramFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
//
//    private static final String LOG_TAG = ProgramFragment.class.getSimpleName();
//
//    private static final int PROGRAM_LOADER = 0;
//
//    //not sure if needmSchool
//    //Will need to pass school data in through intent for school detail section?
//
//    private static final String[] PROGRAM_COLUMNS = {
//            ProgrammeEntry.TABLE_NAME + "." + ProgrammeEntry._ID,
//            ProgrammeEntry.COLUMN_PROG_NAME
//
//            //Might also be able to get here thou due to joined tables like in app.
//
//    };
//
//    static final int COL_PROGRAM_ID = 0;
//    static final int COL_PROGRAM_NAME = 1;
//
//
//
//}
