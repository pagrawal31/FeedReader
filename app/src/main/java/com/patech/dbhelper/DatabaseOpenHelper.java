package com.patech.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.patech.dbhelper.RecordContract.RecordEntry;

/**
 * Created by pagrawal on 12-10-2017.
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SudokuDatabase.db";

    Context context;

    final private static String SQL_CREATE_ENTRIES =
            "CREATE TABLE "+ RecordEntry.TABLE_NAME + " ( " +
                    RecordEntry.COLUMN_NAME_ID + " TEXT, " +
                    RecordEntry.COLUMN_NAME_TITLE + " TEXT," +
                    RecordEntry.COLUMN_NAME_DESC + " TEXT," +
                    RecordEntry.COLUMN_NAME_LINK + " TEXT," +
                    RecordEntry.COLUMN_NAME_AUTHOR + " TEXT," +
                    RecordEntry.COLUMN_NAME_GUID + " TEXT," +
                    RecordEntry.COLUMN_NAME_DATE + " TEXT," +
                    RecordEntry.COLUMN_NAME_CRC32 + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RecordEntry.TABLE_NAME;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
