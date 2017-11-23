package com.patech.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.patech.feedreader.R;
import com.java.rssfeed.feed.FeedMessage;

import java.util.List;

/**
 * Created by pagrawal on 28-10-2017.
 */

public class FeedMessageDisplayAdapter extends ArrayAdapter<FeedMessage> {

    private final Context context;
    private final List<FeedMessage> values;

    public FeedMessageDisplayAdapter(Context context, List<FeedMessage> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.feed_message_row, parent, false);

        TextView titleView = (TextView) rowView.findViewById(R.id.title);
        TextView msgView = (TextView) rowView.findViewById(R.id.description);
        TextView dateView = (TextView) rowView.findViewById(R.id.date);

        // ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        titleView.setText(values.get(position).getTitle());
        String desc = values.get(position).getDescription();
        msgView.setText(Html.fromHtml(desc));
        dateView.setText(values.get(position).getDate());

        return rowView;
    }

}
