package com.patech.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.java.rssfeed.filterimpl.FeedFilter;
import com.java.rssfeed.interfaces.IFeedFilter;
import com.patech.feedreader.R;

import java.util.List;

/**
 * Created by pagrawal on 16-11-2017.
 */

public class FiltersDisplayAdapter extends ArrayAdapter {

    private final Context context;
    private final List<? extends IFeedFilter> filters;

    public FiltersDisplayAdapter(Context context, List<? extends IFeedFilter> filters) {
        super(context, -1, filters);
        this.context = context;
        this.filters = filters;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.filter_view, parent, false);

        TextView nameView = (TextView) rowView.findViewById(R.id.name);
//        TextView descView = (TextView) rowView.findViewById(R.id.description);
        TextView filterTextView = (TextView) rowView.findViewById(R.id.filterText);
        TextView filterTypeView = (TextView) rowView.findViewById(R.id.filterType);

        // ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        nameView.setText(filters.get(position).getFilterName());
//        descView.setText(filters.get(position).getFilterDesc());
        filterTextView.setText(filters.get(position).getFilterText());
        filterTypeView.setText(filters.get(position).getFilterType());

        return rowView;
    }
}
