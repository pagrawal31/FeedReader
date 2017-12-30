package com.patech.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.rssfeed.model.feed.Feed;
import com.patech.feedreader.R;

import java.util.List;

/**
 * Created by pagrawal on 21-11-2017.
 */

public class FeedSearchDisplayAdapter extends ArrayAdapter {

    private List<Feed> feeds;
    private Context context;
    public FeedSearchDisplayAdapter(@NonNull Context context, @NonNull List<Feed> feeds) {
        super(context, -1, feeds);
        this.context = context;
    }

    public void updateList(List<Feed> feeds) {
        this.feeds = feeds;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (this.feeds == null)
            return 0;
        return this.feeds.size();
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.subscribe_search_row, parent, false);
        ImageView iconView = rowView.findViewById(R.id.icon);
        TextView nameView = rowView.findViewById(R.id.name);
        TextView urlView = rowView.findViewById(R.id.url);

        nameView.setText(feeds.get(position).getName());
        urlView.setText(feeds.get(position).getLink());
        return rowView;
    }
}
