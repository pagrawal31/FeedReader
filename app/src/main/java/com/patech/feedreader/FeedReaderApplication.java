package com.patech.feedreader;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.patech.dbhelper.FeedDatabaseOpenHelper;

/**
 * Created by pagrawal on 17-11-2017.
 */

public class FeedReaderApplication extends Application {

    private FeedDatabaseOpenHelper mFeedDbHelper;
    private SQLiteDatabase mWriterFeedDB = null;
    private SQLiteDatabase mReaderFeedDB = null;
    private long frequencyInMillis = 30000;

    @Override
    public void onCreate() {
        super.onCreate();
        mFeedDbHelper = new FeedDatabaseOpenHelper(this);
        mWriterFeedDB = mFeedDbHelper.getWritableDatabase();
        mReaderFeedDB = mFeedDbHelper.getReadableDatabase();
    }

    public SQLiteDatabase getReadableDatabase() {
        return mReaderFeedDB;
    }
    public long getFrequencyInMillis() {
        return frequencyInMillis;
    }

    public void setFrequencyInMillis(long frequencyInMillis) {
        this.frequencyInMillis = frequencyInMillis;
    }

    public SQLiteDatabase getWritableDatabase() {
        return mWriterFeedDB;
    }


}