package com.patech.dbhelper;

/**
 * Created by pagrawal on 12-10-2017.
 */

public class RecordContract {
    public void RecordContract() {

    }
    public static class RecordEntry {
        public final static String TABLE_NAME = "feed_message_table";
        public final static String COLUMN_NAME_ID = "id";

        public final static String COLUMN_NAME_TITLE = "title";
        public final static String COLUMN_NAME_DESC = "desc";
        public final static String COLUMN_NAME_LINK = "link";
        public final static String COLUMN_NAME_AUTHOR = "author";
        public final static String COLUMN_NAME_GUID = "guid";
        public final static String COLUMN_NAME_DATE = "date";
        public final static String COLUMN_NAME_CRC32 = "crc32";

    }
}
