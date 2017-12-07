package com.patech.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.patech.dbhelper.FeedContract.*;

/**
 * Created by pagrawal on 21-10-2017.
 */

public class FeedDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Feeds.db";

    Context context;

    final private static String CREATE_TABLE_FILTER =
            "CREATE TABLE "+ FilterEntry.TABLE_NAME + " ( " +
                    FilterEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FilterEntry.COLUMN_NAME_NAME + " TEXT," +
                    FilterEntry.COLUMN_NAME_TYPE + " INTEGER," +
                    FilterEntry.COLUMN_NAME_GLOBAL + " INTEGER, " +
                    FilterEntry.COLUMN_NAME_TEXT + " TEXT," +
                    FilterEntry.COLUMN_NAME_DESC + " TEXT)";

    final private static String CREATE_TABLE_FEED =
            "CREATE TABLE "+ FeedEntry.TABLE_NAME + " ( " +
                    FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FeedEntry.COLUMN_NAME_URL + " TEXT," +
                    FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedEntry.COLUMN_NAME_DESC + " TEXT)";

    final private static String CREATE_TABLE_FEED_FILTER = "CREATE TABLE " +
            FeedFilterEntry.TABLE_NAME + " ( " +
            FeedFilterEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FeedFilterEntry.COLUMN_NAME_FILTER_ID + " INTEGER, " +
            FeedFilterEntry.COLUMN_NAME_FEED_URL + " TEXT)";

    // FeedFilterEntry

    private static final String DELETE_TABLE_FEED =
            "DROP TABLE IF EXISTS " + FeedContract.FeedEntry.TABLE_NAME;
    private static final String DELETE_TABLE_FILTER =
            "DROP TABLE IF EXISTS " + FeedContract.FilterEntry.TABLE_NAME;
    private static final String DELETE_TABLE_FEED_FILTER =
            "DROP TABLE IF EXISTS " + FeedFilterEntry.TABLE_NAME;

    public FeedDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FEED);
        db.execSQL(CREATE_TABLE_FILTER);
        db.execSQL(CREATE_TABLE_FEED_FILTER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE_FEED);
        db.execSQL(DELETE_TABLE_FILTER);
        db.execSQL(DELETE_TABLE_FEED_FILTER);
    }

}
