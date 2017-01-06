package com.example.gmpillatt.upsanddowns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gmpillatt on 02/12/2016.
 */

public class DBHelperClass extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "LockStats.db";

    public DBHelperClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContractClass.DBSchema.TABLE_NAME + " (" +
                    DBContractClass.DBSchema._ID + " INTEGER PRIMARY KEY autoincrement," +
                    DBContractClass.DBSchema.COLUMN_NAME_FLIGHT + " TEXT," +
                    DBContractClass.DBSchema.COLUMN_NAME_DATETIME + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    DBContractClass.DBSchema.COLUMN_NAME_NUMBERBOATS + " INTEGER," +
                    DBContractClass.DBSchema.COLUMN_NAME_UPDOWN + " TEXT," +
                    DBContractClass.DBSchema.COLUMN_NAME_WIDEBEAM + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContractClass.DBSchema.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

