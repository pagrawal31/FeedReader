package com.patech.feedreader;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.patech.dbhelper.FeedDatabaseOpenHelper;

/**
 * Created by pagrawal on 17-11-2017.
 */

public class FeedReaderApplication extends Application {

    private FeedDatabaseOpenHelper mFeedDbHelper;
    private SQLiteDatabase mWriterFeedDB = null;
    private SQLiteDatabase mReaderFeedDB = null;
    private long frequencyInMillis = 30000;
    private boolean updateOnWifiOnly = true;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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

    public boolean isUpdateOnWifiOnly() {
        return updateOnWifiOnly;
    }

    public void setUpdateOnWifiOnly(boolean updateOnWifiOnly) {
        this.updateOnWifiOnly = updateOnWifiOnly;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
