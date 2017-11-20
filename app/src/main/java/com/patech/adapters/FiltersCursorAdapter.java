package com.patech.adapters;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.patech.dbhelper.FeedContract.*;
import com.patech.feedreader.R;

/**
 * Created by pagrawal on 16-11-2017.
 */

public class FiltersCursorAdapter extends CursorAdapter {

    LayoutInflater inflater;
    public FiltersCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.filter_view, parent, false);
    }

    @Override
    public void bindView(View rowView, Context context, Cursor cursor) {

        TextView nameView = (TextView) rowView.findViewById(R.id.name);
        TextView descView = (TextView) rowView.findViewById(R.id.description);
        TextView filterTextView = (TextView) rowView.findViewById(R.id.filterText);
        TextView filterTypeView = (TextView) rowView.findViewById(R.id.filterType);

        // ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        String filterId = cursor.getString(cursor.getColumnIndexOrThrow(FeedFilterEntry.COLUMN_NAME_FILTER_ID));
//        String title = cursor.getString(cursor.getColumnIndexOrThrow(FeedFilterEntry.COLUMN_NAME_TITLE));
//        String url = cursor.getString(cursor.getColumnIndexOrThrow(FeedFilterEntry.COLUMN_NAME_URL));

        nameView.setText(filterId);
//        descView.setText(filters.get(position).getFilterDesc());
//        filterTextView.setText(filters.get(position).getFilterText());
//        filterTypeView.setText(filters.get(position).getFilterType());

    }
}
