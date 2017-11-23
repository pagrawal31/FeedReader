package com.patech.dbhelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.java.rssfeed.interfaces.IFeedFilter;
import com.patech.utils.CommonUtils;
import com.java.rssfeed.feed.Feed;
import com.patech.dbhelper.FeedContract.*;

/**
 * Created by pagrawal on 12-11-2017.
 */

public class DatabaseUtils {

    public static Cursor fetchFeedFromDatabase(SQLiteDatabase mReaderFeedDB, Feed feed) {
        String selection = null;
        String[] selectionArgs;
        if (feed != null) {
            selection = FeedContract.FeedEntry.COLUMN_NAME_URL + " LIKE ?";
        }

        Cursor cursor = mReaderFeedDB.query(
                FeedContract.FeedEntry.TABLE_NAME,                     // The table to query
                FeedContract.feedTableAllProjection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                feed != null ? new String[]{feed.getLink()} :
                        CommonUtils.EMPTY_SELECTION,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        return cursor;
    }

    public static Cursor fetchFiltersFromFilterDb(SQLiteDatabase mReaderFeedDB, IFeedFilter filter) {
        String selection = FilterEntry.COLUMN_NAME_TEXT+ " LIKE ?";
        String[] selectionArgs;

        Cursor cursor = mReaderFeedDB.query(
                FilterEntry.TABLE_NAME,                     // The table to query
                FeedContract.filterTableAllProjection ,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                new String[]{filter.getFilterText()},            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        return cursor;
    }

    public static Cursor fetchFiltersFromFeedFilterDb(SQLiteDatabase mReaderFeedDB, String filterId) {
        String selection = FeedFilterEntry.COLUMN_NAME_FILTER_ID + " LIKE ?";
        String[] selectionArgs;

        Cursor cursor = mReaderFeedDB.query(
                FeedFilterEntry.TABLE_NAME,                     // The table to query
                FeedContract.feedFilterTableAllProjection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                new String[]{filterId},            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        return cursor;
    }

    public static Cursor fetchFiltersFromFilterDb(SQLiteDatabase mReaderFeedDB, String filterId) {
        String selection = FilterEntry._ID + " LIKE ?";
        String[] selectionArgs;

        Cursor cursor = mReaderFeedDB.query(
                FilterEntry.TABLE_NAME,                     // The table to query
                FeedContract.filterTableAllProjection ,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                new String[]{filterId},            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        return cursor;
    }

    public static long insertFeedIntoDb(SQLiteDatabase db, Feed feed) {
        ContentValues values = new ContentValues();
        values.put(FeedContract.FeedEntry.COLUMN_NAME_TITLE, feed.getTitle());
        values.put(FeedContract.FeedEntry.COLUMN_NAME_DESC, feed.getDescription());
        values.put(FeedContract.FeedEntry.COLUMN_NAME_URL, feed.getLink());

        return db.insert(FeedContract.FeedEntry.TABLE_NAME, null, values);
    }

    public static long insertFilterIntoDb(SQLiteDatabase db, IFeedFilter filter) {
        ContentValues values = new ContentValues();
        values.put(FilterEntry.COLUMN_NAME_NAME, filter.getFilterName());
        values.put(FilterEntry.COLUMN_NAME_DESC, filter.getFilterDesc());
        values.put(FilterEntry.COLUMN_NAME_TEXT, filter.getFilterText());
        values.put(FilterEntry.COLUMN_NAME_TYPE, filter.getFilterType());
        return db.insert(FilterEntry.TABLE_NAME, null, values);
    }


//    public static long deleteFilterFromFilterDb(SQLiteDatabase db, IFeedFilter filter) {
//        String selection = FilterEntry.COLUMN_NAME_TEXT + " LIKE ?";
//        return db.delete(FilterEntry.TABLE_NAME, selection, new String[]{filter.getFilterText()});
//    }

    /*
        this function deletes filter from feedFilter table first
        then it checks if there is no other feed referring to same filter (by looking into feedFilter table)
        if yes, then delete the filter entry as well.

        param: isGlobal mean that filter is not specific to any feed.
        just remove all rows where filterId matches filter's id in filterTable
     */
    public static void deleteFilterFromFeedFilterDb(SQLiteDatabase db, IFeedFilter filter, Feed feed, boolean isGlobal) {
        // find filterId for filter first
        Cursor filterCursor = fetchFiltersFromFilterDb(db, filter);
        boolean removeFilter = false;
        while(filterCursor.moveToNext()) {
            String filterId = filterCursor.getString(filterCursor.getColumnIndexOrThrow(FilterEntry._ID));
            removeFilter = false;
            if (isGlobal) {
                String selection = FeedFilterEntry.COLUMN_NAME_FILTER_ID + " LIKE ?";
                db.delete(FeedFilterEntry.TABLE_NAME, selection, new String[]{filterId});
                removeFilter = true;
            } else {
                String selection = FeedFilterEntry.COLUMN_NAME_FILTER_ID + " LIKE ? AND "+
                        FeedFilterEntry.COLUMN_NAME_FEED_URL + " LIKE ?";
                db.delete(FeedFilterEntry.TABLE_NAME, selection, new String[]{filterId, feed.getLink()});
                Cursor feedFilterCursor = fetchFiltersFromFeedFilterDb(db, filterId);
                feedFilterCursor.moveToNext();
                if (feedFilterCursor.getCount() == 0) {
                    // 0 means there is no other filter in feedFilter table, so we can remove filter from filter table.
                    removeFilter = true;
                }
                feedFilterCursor.close();
            }
            if (removeFilter) {
                String filterSelection = FilterEntry._ID + " LIKE ?";
                db.delete(FilterEntry.TABLE_NAME, filterSelection, new String[]{filterId});
            }
        }
        filterCursor.close();
    }

    /*
        clears the filter table and feedFilter table.
     */
    public static void clearFiltersDb(SQLiteDatabase db) {
        db.delete(FilterEntry.TABLE_NAME, null, null);
        db.delete(FeedFilterEntry.TABLE_NAME, null, null);
    }

    public static long insertFilterFeedIntoDb(SQLiteDatabase db, long filterId, Feed feed) {
        ContentValues values = new ContentValues();
        values.put(FeedFilterEntry.COLUMN_NAME_FILTER_ID, filterId);
        values.put(FeedFilterEntry.COLUMN_NAME_FEED_URL, feed.getLink());
        return db.insert(FeedFilterEntry.TABLE_NAME, null, values);
    }

    public static Cursor getFilters(SQLiteDatabase db, Feed feed) {
        String selection = FeedFilterEntry.COLUMN_NAME_FEED_URL + " LIKE ?";

        Cursor cursor = db.query(
                FeedFilterEntry.TABLE_NAME,                     // The table to query
                FeedContract.feedFilterTableAllProjection,      // The columns to return
                selection,                                      // The columns for the WHERE clause
                new String[]{feed.getLink()},                   // The values for the WHERE clause
                null,                                           // don't group the rows
                null,                                           // don't filter by row groups
                null                                            // The sort order
        );
        return cursor;
    }
}