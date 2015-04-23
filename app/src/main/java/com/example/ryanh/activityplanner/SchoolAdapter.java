package com.example.ryanh.activityplanner;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by RyanH on 23/04/15.
 */
public class SchoolAdapter extends CursorAdapter {


    // Cache of the children views for a school list item.
    public static class ViewHolder {
        public final TextView schoolView;

        public ViewHolder(View view) {
            schoolView = (TextView) view.findViewById(R.id.list_item_school_textview);
        }
    }

    public SchoolAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = -1;

        layoutId = R.layout.list_item_school;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    //This is where we fill-in the views with the contents of the cursor.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Read school from cursor
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read school name from cursor
        String schoolName = cursor.getString(SchoolFragment.COL_SCHOOL_NAME);
        viewHolder.schoolView.setText(schoolName);
    }


}
