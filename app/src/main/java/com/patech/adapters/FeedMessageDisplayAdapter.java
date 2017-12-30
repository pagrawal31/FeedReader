package com.patech.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.patech.feedreader.R;
import com.java.rssfeed.model.feed.FeedMessage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pagrawal on 28-10-2017.
 */

public class FeedMessageDisplayAdapter extends ArrayAdapter<FeedMessage> {

    private final Context context;
    private List<FeedMessage> values;
    LayoutInflater inflater;

    public FeedMessageDisplayAdapter(Context context, List<FeedMessage> values) {
        super(context, -1, values);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.feed_message_row, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        // feedTitleTxt

        FeedMessage msg = values.get(position);
        String desc = msg.getDescription();

        holder.favoriteImg.setImageResource(msg.isFavorite() ? android.R.drawable.star_on : android.R.drawable.star_off);
        holder.titleView.setText(Html.fromHtml(msg.getTitle()));
        holder.msgView.setText(Html.fromHtml(desc));
        holder.dateView.setText(msg.getDate());
        return view;
    }

    @OnClick(R.id.favoriteImg)
    void changeFavoriteState(View view) {
        Toast.makeText(context, "ImageView clicked", Toast.LENGTH_SHORT).show();
    }

    public void setData(List<FeedMessage> messageList) {
        this.values = messageList;
    }

    static class ViewHolder {
        @BindView(R.id.title) TextView titleView;
        @BindView(R.id.description) TextView msgView;
        @BindView(R.id.date) TextView dateView;
        @BindView(R.id.favoriteImg) ImageView favoriteImg;
        @BindView(R.id.feedTitleTxt) TextView feedTitleTxt;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
