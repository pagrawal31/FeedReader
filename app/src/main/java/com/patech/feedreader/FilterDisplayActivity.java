package com.patech.feedreader;

import android.content.Intent;
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
import android.widget.ListView;

import com.java.rssfeed.FeedInfoStore;
import com.java.rssfeed.ReadTest;
import com.java.rssfeed.model.feed.Feed;
import com.java.rssfeed.interfaces.IFeedFilter;
import com.java.rssfeed.interfaces.IPageParser;
import com.patech.adapters.FiltersDisplayAdapter;
import com.patech.dbhelper.DatabaseUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private List<IFeedFilter> filters = Collections.EMPTY_LIST;
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
        IPageParser parser = null;

        try {
            parser = ReadTest.getFeedParser(feedIdx);
        } catch (Exception e) {
            parser = null;
        }

        if (parser != null) {
            filters = new ArrayList<>(parser.getFilters());
        }

        currFeed = FeedInfoStore.getInstance().getFeed(feedIdx);

        ArrayAdapter adapter = new FiltersDisplayAdapter(getApplicationContext(), new ArrayList<IFeedFilter>(filters));
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
                    ReadTest.removeFilterFromFeed(currFilter);
                } else {
                    ReadTest.removeFilterFromFeed(currFeed, currFilter);
                }
                SQLiteDatabase db = ((FeedReaderApplication)getApplication()).getWritableDatabase();
                DatabaseUtils.deleteFilterFromFeedFilterDb(db, currFilter, currFeed, isGlobal);

                filters.remove(info.position);
                ((FiltersDisplayAdapter)(listView.getAdapter())).udpateData(filters);
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
