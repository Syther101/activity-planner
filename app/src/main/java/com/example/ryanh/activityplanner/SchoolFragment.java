package com.example.ryanh.activityplanner;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.ryanh.activityplanner.data.ProgrammeContract.SchoolEntry;

/**
 * Created by RyanH on 23/04/15.
 */
public class SchoolFragment extends Fragment implements  LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SCHOOL_LOADER = 0;

    // Data that is needed to be displayed
    // Should be all that is needed, Just need school name to display in list
    private static final String[] SCHOOL_COLUMNS = {
            SchoolEntry.TABLE_NAME + "." + SchoolEntry._ID,
            SchoolEntry.COLUMN_SCHOOL_NAME
    };

    // These are tied to SCHOOL_COLUMNS. If SCHOOL_COLUMNS changes, these
    // must change.
    static final int COL_SCHOOL_ID = 0;
    static final int COL_SCHOOL_NAME = 1;

    private SchoolAdapter mSchoolAdapter;

    public SchoolFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceSate) {
        super.onCreate(savedInstanceSate);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Don't think need logic here as won't have time to implement settings for use.

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we wil end
        // up with an empty list the first time we run.
        //http://developer.android.com/reference/android/widget/CursorAdapter.html#FLAG_AUTO_REQUERY
        mSchoolAdapter = new SchoolAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attatch this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_schools);
        listView.setAdapter(mSchoolAdapter);

        // We'll call our MainActivity
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                // CursorAdapter returns a cursor at the correct position for getItem(), or null
//                // if it cannot seek to that position
//                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
//                if (cursor != null) {
//                    Intent intent = new Intent(getActivity(), SchoolFragment.class);
//                    startActivity(intent);
//                }
//            }
//        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceSate) {
        getLoaderManager().initLoader(SCHOOL_LOADER, null, this);
        super.onActivityCreated(savedInstanceSate);
    }

    private void updateSchool() {
        FetchProgramTask programTask = new FetchProgramTask(getActivity());
        programTask.execute();
    }

    // Since we read the school when we create the loader, all we need to do is restart things
    void onSchoolChanged() {
        updateSchool();
        getLoaderManager().restartLoader(SCHOOL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Sort order: Ascending, by date updated
        String sortOrder = SchoolEntry.COLUMN_SCHOOL_UPDATE + " ASC";
        Uri school = SchoolEntry.buildSchools();

        return new CursorLoader(getActivity(),
                school,
                SCHOOL_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mSchoolAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mSchoolAdapter.swapCursor(null);
    }

}
