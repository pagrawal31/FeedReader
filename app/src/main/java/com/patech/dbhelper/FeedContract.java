package com.patech.dbhelper;

import android.provider.BaseColumns;

/**
 * Created by pagrawal on 21-10-2017.
 */

public class FeedContract {
    public static class FeedEntry implements BaseColumns{
        public final static String TABLE_NAME = "table_feeds";

        public final static String COLUMN_NAME_URL = "url";
        public final static String COLUMN_NAME_TITLE = "title";
        public final static String COLUMN_NAME_DESC = "about";
    }

    public static class FilterEntry implements BaseColumns{
        public final static String TABLE_NAME = "table_filters";

        public final static String COLUMN_NAME_NAME = "name";
        public final static String COLUMN_NAME_TYPE = "type";
        public final static String COLUMN_NAME_DESC = "desc";
        public final static String COLUMN_NAME_TEXT = "txt";
    }

    public static class FeedFilterEntry implements BaseColumns {
        public final static String TABLE_NAME = "table_feed_filters";

        public final static String COLUMN_NAME_FILTER_ID = "FILTER_ID";
        public final static String COLUMN_NAME_FEED_URL = "FEED_URL";
    }

//

    public static final String[] feedTableAllProjection = {
            FeedEntry._ID,
            FeedEntry.COLUMN_NAME_URL,
            FeedEntry.COLUMN_NAME_TITLE,
            FeedEntry.COLUMN_NAME_DESC
    };

    public static final String[] filterTableAllProjection = {
            FilterEntry._ID,
            FilterEntry.COLUMN_NAME_NAME,
            FilterEntry.COLUMN_NAME_TYPE,
            FilterEntry.COLUMN_NAME_DESC,
            FilterEntry.COLUMN_NAME_TEXT,
    };

    public static final String[] feedFilterTableAllProjection = {
            FeedFilterEntry._ID,
            FeedFilterEntry.COLUMN_NAME_FILTER_ID
    };

}
