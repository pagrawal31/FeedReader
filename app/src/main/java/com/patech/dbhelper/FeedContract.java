package com.patech.dbhelper;

import android.net.Uri;
import android.provider.BaseColumns;

import com.patech.utils.AppConstants;
import com.patech.utils.AppUtils;

/**
 * Created by pagrawal on 21-10-2017.
 */

public class FeedContract {

    public static final String CONTENT = "content://";
    public static final String AUTHORITY = "com.patech.dbhelper.provider.FeedData";
    public static final String CONTENT_AUTHORITY = CONTENT + AUTHORITY;

    static final String TYPE_PRIMARY_KEY = "INTEGER PRIMARY KEY AUTOINCREMENT";
    static final String TYPE_EXTERNAL_ID = "INTEGER(7)";
    static final String TYPE_TEXT = "TEXT";
    static final String TYPE_TEXT_UNIQUE = "TEXT UNIQUE";
    static final String TYPE_DATE_TIME = "DATETIME";
    static final String TYPE_INT = "INT";
    static final String TYPE_BOOLEAN = "INTEGER(1)";

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
        public final static String COLUMN_NAME_GLOBAL = "global";
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
            FilterEntry.COLUMN_NAME_GLOBAL
    };

    public static final String[] feedFilterTableAllProjection = {
            FeedFilterEntry._ID,
            FeedFilterEntry.COLUMN_NAME_FILTER_ID
    };

    public static class EntryColumns implements BaseColumns {
        public static final String TABLE_NAME = "entries";

        public static final String FEED_ID = "feedid";
        public static final String TITLE = "title";
        public static final String ABSTRACT = "abstract";
        public static final String MOBILIZED_HTML = "mobilized";
        public static final String DATE = "date";
        public static final String FETCH_DATE = "fetch_date";
        public static final String IS_READ = "isread";
        public static final String LINK = "link";
        public static final String IS_FAVORITE = "favorite";
        public static final String ENCLOSURE = "enclosure";
        public static final String GUID = "guid";
        public static final String AUTHOR = "author";
        public static final String IMAGE_URL = "image_url";
        public static final String[] PROJECTION_ID = new String[]{EntryColumns._ID};
        public static final String WHERE_READ = EntryColumns.IS_READ + AppConstants.DB_IS_TRUE;
        public static final String WHERE_UNREAD = "(" + EntryColumns.IS_READ + AppConstants.DB_IS_NULL + AppConstants.DB_OR + EntryColumns.IS_READ + AppConstants.DB_IS_FALSE + ')';
        public static final String WHERE_NOT_FAVORITE = "(" + EntryColumns.IS_FAVORITE + AppConstants.DB_IS_NULL + AppConstants.DB_OR + EntryColumns.IS_FAVORITE + AppConstants.DB_IS_FALSE + ')';

        public static Uri ENTRIES_FOR_FEED_CONTENT_URI(String feedId) {
            return Uri.parse(CONTENT_AUTHORITY + "/feeds/" + feedId + "/entries");
        }

        public static final String[][] COLUMNS = new String[][]{{_ID, TYPE_PRIMARY_KEY}, {FEED_ID, TYPE_EXTERNAL_ID}, {TITLE, TYPE_TEXT},
                {ABSTRACT, TYPE_TEXT}, {MOBILIZED_HTML, TYPE_TEXT}, {DATE, TYPE_DATE_TIME}, {FETCH_DATE, TYPE_DATE_TIME}, {IS_READ, TYPE_BOOLEAN}, {LINK, TYPE_TEXT},
                {IS_FAVORITE, TYPE_BOOLEAN}, {ENCLOSURE, TYPE_TEXT}, {GUID, TYPE_TEXT}, {AUTHOR, TYPE_TEXT}, {IMAGE_URL, TYPE_TEXT}};

        public static Uri ENTRIES_FOR_FEED_CONTENT_URI(long feedId) {
            return Uri.parse(CONTENT_AUTHORITY + "/feeds/" + feedId + "/entries");
        }

        public static Uri ENTRIES_FOR_GROUP_CONTENT_URI(String groupId) {
            return Uri.parse(CONTENT_AUTHORITY + "/groups/" + groupId + "/entries");
        }

        public static Uri ENTRIES_FOR_GROUP_CONTENT_URI(long groupId) {
            return Uri.parse(CONTENT_AUTHORITY + "/groups/" + groupId + "/entries");
        }

        public static Uri ALL_ENTRIES_CONTENT_URI(String entryId) {
            return Uri.parse(CONTENT_AUTHORITY + "/all_entries/" + entryId);
        }

        public static Uri CONTENT_URI(String entryId) {
            return Uri.parse(CONTENT_AUTHORITY + "/entries/" + entryId);
        }

        public static final Uri CONTENT_URI = Uri.parse(CONTENT_AUTHORITY + "/entries");

        public static Uri CONTENT_URI(long entryId) {
            return Uri.parse(CONTENT_AUTHORITY + "/entries/" + entryId);
        }

        public static Uri PARENT_URI(String path) {
            return Uri.parse(CONTENT_AUTHORITY + path.substring(0, path.lastIndexOf('/')));
        }

        public static Uri SEARCH_URI(String search) {
            return Uri.parse(CONTENT_AUTHORITY + "/entries/search/" + (AppUtils.isEmpty(search) ? " " : Uri.encode(search))); // The space is mandatory here with empty search
        }


        public static final Uri ALL_ENTRIES_CONTENT_URI = Uri.parse(CONTENT_AUTHORITY + "/all_entries");


        public static final Uri FAVORITES_CONTENT_URI = Uri.parse(CONTENT_AUTHORITY + "/favorites");


    }

}
