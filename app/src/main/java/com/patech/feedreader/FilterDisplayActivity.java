package com.patech.feedreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.java.rssfeed.FeedInfoStore;
import com.java.rssfeed.ReadTest;
import com.java.rssfeed.feed.Feed;
import com.java.rssfeed.filterimpl.ExcludeFeedFilter;
import com.java.rssfeed.filterimpl.IncludeFeedFilter;
import com.java.rssfeed.interfaces.IFeedFilter;
import com.patech.adapters.FiltersDisplayAdapter;
import com.patech.dbhelper.DatabaseUtils;
import com.patech.dbhelper.FeedContract;
import com.patech.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import com.patech.dbhelper.FeedContract.*;
/**
 * Created by pagrawal on 17-11-2017.
 */

public class FilterDisplayActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // show filters related to the position of feed.
    // show delete option on filters
    // if filter is removed then remove it from database
    // 1) remove it from corresponding feed parser (if it was global take extra care)
    //

    protected static final String POSITION = "POSITION";
    private ListView listView;
    List<IFeedFilter> filters;
    private int feedIdx = -1;
    private Feed currFeed = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_display);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.filterList);

        Intent i = getIntent();
        feedIdx = i.getIntExtra(POSITION, 0);
        currFeed = FeedInfoStore.getInstance().getFeed(feedIdx);

        SQLiteDatabase db = ((FeedReaderApplication)getApplication()).getReadableDatabase();

        Cursor filtersCursor = DatabaseUtils.getFilters(db, currFeed);
        filters = new ArrayList<>();

        while(filtersCursor.moveToNext()) {
            String filterId = filtersCursor.getString(filtersCursor.getColumnIndexOrThrow(FeedContract.FeedFilterEntry.COLUMN_NAME_FILTER_ID));

            Cursor filterCursor = DatabaseUtils.fetchFiltersFromFilterDb(db, filterId);
            while(filterCursor.moveToNext()) {
                String filterDesc = filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_NAME_DESC));
                String filterName = filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_NAME_NAME));
                String filterText = filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_NAME_TEXT));
                String filterType = filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_NAME_TYPE));
                boolean isGlobal = CommonUtils.getBooleanFromInt(filterCursor.getInt((filterCursor.getColumnIndexOrThrow(FilterEntry.COLUMN_NAME_GLOBAL))));

                IFeedFilter filter;

                if (filterType.equals(IncludeFeedFilter.FILTERTYPE)) {
                    filter = new IncludeFeedFilter(filterText, filterName, filterDesc, isGlobal);
                } else {
                    filter = new ExcludeFeedFilter(filterText, filterName, filterDesc, isGlobal);
                }
                filters.add(filter);
            }
            filterCursor.close();
        }

        ArrayAdapter adapter = new FiltersDisplayAdapter(getApplicationContext(), filters);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle("Position : " + info.position);
        String[] menuItems = getResources().getStringArray(R.array.menu_filters);
        for (int i = 0; i<menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        switch (menuItemIndex) {
            case 0:
                // remove filter
                // a) delete filter from filter table
                // b) delete from feedfilter table
                // c)
                IFeedFilter currFilter = filters.get(info.position);
                boolean isGlobal = false;
                if (isGlobal) {
                    ReadTest.removeFilterFromFeed(currFeed, currFilter);
                } else {
                    ReadTest.removeFilterFromFeed(currFilter);
                }
                SQLiteDatabase db = ((FeedReaderApplication)getApplication()).getWritableDatabase();
                DatabaseUtils.deleteFilterFromFeedFilterDb(db, currFilter, currFeed, isGlobal);

                filters.remove(info.position);
                ((ArrayAdapter)listView.getAdapter()).notifyDataSetChanged();
                break;
            case 1:
                // edit feed
//                Feed feed = FeedInfoStore.getInstance().getFeed(info.position);
//
//                FeedDialog mDialog = FeedDialog.newInstance();
//                mDialog.setFeedToDisplay(feed);
//                mDialog.show(getFragmentManager(), "Add Feed pagrawal");
                break;
            default:
                break;
        }
        return true;
    }

}
