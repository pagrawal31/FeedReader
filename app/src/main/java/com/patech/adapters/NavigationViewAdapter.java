package com.patech.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.java.rssfeed.ReadTest;
import com.java.rssfeed.filterimpl.ExcludeFeedFilter;
import com.java.rssfeed.filterimpl.IncludeFeedFilter;
import com.java.rssfeed.interfaces.IFeedFilter;
import com.patech.dbhelper.DatabaseUtils;
import com.patech.dbhelper.FeedContract.*;
import com.patech.feedreader.MainActivity;
import com.patech.feedreader.R;
import com.java.rssfeed.FeedInfoStore;
import com.java.rssfeed.model.feed.Feed;
import com.patech.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pagrawal on 29-10-2017.
 */

public class NavigationViewAdapter extends BaseAdapter {

    private List<Feed> menus;
    private Context context;
    private SQLiteDatabase mWriterFeedDB = null;
    private SQLiteDatabase mReaderFeedDB = null;
    MainActivity mainActivity;

    public NavigationViewAdapter(Context context) {
        this.context = context;
        mainActivity = (MainActivity) this.context;
        mWriterFeedDB = mainActivity.getWritableDatabase();
        mReaderFeedDB = mainActivity.getReadableDatabase();
        menus = new ArrayList<>();
        if (FeedInfoStore.getInstance().getFeedSize() == 0) {
            updateFromDatabase();
        }

        updateList();
    }

    private void updateFromDatabase() {
        Cursor feedCursor = fetchFeedFromDatabase();

        List<String> itemIds = new ArrayList<>();
        while (feedCursor.moveToNext()) {
            String desc = feedCursor.getString(feedCursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DESC));
            String title = feedCursor.getString(feedCursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
            String url = feedCursor.getString(feedCursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_URL));
            Feed newFeed = new Feed.FeedBuilder().setName(title).setDescription(desc).build(url);

            FeedInfoStore.getInstance().addFeedIntoList(newFeed);

            Cursor filterIdCursor = DatabaseUtils.getFilters(mReaderFeedDB, newFeed);

            while(filterIdCursor.moveToNext()) {
                String filterId = filterIdCursor.getString(filterIdCursor.getColumnIndexOrThrow(FeedFilterEntry.COLUMN_NAME_FILTER_ID));
                Cursor filterCursor = DatabaseUtils.fetchFiltersFromFilterDb(mReaderFeedDB, filterId);
                while(filterCursor.moveToNext()) {
                    String filterDesc = filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_NAME_DESC));
                    String filterName = filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_NAME_NAME));
                    String filterText = filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_NAME_TEXT));
                    String filterType = filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_NAME_TYPE));
                    boolean isGlobal = AppUtils.getBooleanFromInt(filterCursor.getInt((filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_NAME_GLOBAL))));
                    IFeedFilter filter;
                    if (filterType.equals(IncludeFeedFilter.FILTERTYPE)) {
                        filter = new IncludeFeedFilter(filterText, filterName, filterDesc, isGlobal);
                    } else {
                        filter = new ExcludeFeedFilter(filterText, filterName, filterDesc, isGlobal);
                    }
                    if (isGlobal) {
                        FeedInfoStore.getInstance().addGlobalFilter(filter);
                    }
                    ReadTest.addFilterToFeed(newFeed, filter);
                }
                filterCursor.close();
            }
            filterIdCursor.close();
        }
        feedCursor.close();
    }

    public Cursor fetchFeedFromDatabase() {
        return fetchFeedFromDatabase(null);
    }

    public Cursor fetchFeedFromDatabase(Feed feed) {
        return DatabaseUtils.fetchFeedFromDatabase(mReaderFeedDB, feed);
    }

    public void remove(int position) {
        Feed currFeed = FeedInfoStore.getInstance().getFeedInfoList().get(position);
        String url = currFeed.getLink();
        // Define 'where' part of query.
        String selection = FeedEntry.COLUMN_NAME_URL + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { url };

        // TODO: It should remove only one.
        // Issue SQL statement.
        int count = mReaderFeedDB.delete(
                FeedEntry.TABLE_NAME,
                selection,
                selectionArgs
        );
        if (count > 0) {
            FeedInfoStore.getInstance().removeFeedFromList(position);
            updateList();
        }
    }
    public void updateList() {
        menus = FeedInfoStore.getInstance().getFeedInfoList();
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return menus.size();
    }

    @Override
    public Object getItem(int i) {
        return menus.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.drawer_list_item, parent, false);
        } else {
            row = convertView;
        }

        TextView titleView = (TextView) row.findViewById(R.id.title);
        TextView lastUpdatedView = (TextView) row.findViewById(R.id.lastUpdatedFeedVal);
        TextView lastUpdatedScanView = (TextView) row.findViewById(R.id.lastUpdatedScanVal);
        //

        Feed feed = menus.get(position);
        String title = feed.getName();
        String summary = feed.getDescription();
        String lastUpdatedVal = feed.getLastUpdated();
        String lastScannedDate = feed.getLastScanned();

        if (title == null || title.isEmpty()) {
            title = "Menu:" + position;
        }

        lastUpdatedView.setText(lastUpdatedVal);
        lastUpdatedScanView.setText(lastScannedDate);


        titleView.setText(title);
        return row;
    }
}
