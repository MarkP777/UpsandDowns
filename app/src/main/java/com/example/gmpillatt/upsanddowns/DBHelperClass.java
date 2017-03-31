package com.example.gmpillatt.upsanddowns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gmpillatt on 02/12/2016.
 */

public class DBHelperClass extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "LockStats.db";

    public DBHelperClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_LS_ENTRIES =
            "CREATE TABLE " + DBContractClass.LSSchema.TABLE_NAME + " (" +
                    DBContractClass.LSSchema._ID + " INTEGER PRIMARY KEY autoincrement," +
                    DBContractClass.LSSchema.COLUMN_NAME_FLIGHT + " TEXT," +
                    DBContractClass.LSSchema.COLUMN_NAME_DATETIME + " DATETIME," +
                    DBContractClass.LSSchema.COLUMN_NAME_NUMBERBOATS + " INTEGER," +
                    DBContractClass.LSSchema.COLUMN_NAME_UPDOWN + " TEXT," +
                    DBContractClass.LSSchema.COLUMN_NAME_WIDEBEAM + " TEXT)";

    private static final String SQL_CREATE_PREF_ENTRIES =
            "CREATE TABLE " + DBContractClass.PrefSchema.TABLE_NAME + " (" +
                    DBContractClass.PrefSchema._ID + " INTEGER PRIMARY KEY autoincrement," +
                    DBContractClass.PrefSchema.COLUMN_NAME_HOMEFLIGHT + " TEXT);";

    private static final String SQL_ADD_HOMEFLIGHT =
            "INSERT INTO " + DBContractClass.PrefSchema.TABLE_NAME + " (" +
                    DBContractClass.PrefSchema.COLUMN_NAME_HOMEFLIGHT + ") " +
                    "VALUES ('S3L');";

    private static final String SQL_DELETE_LS_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContractClass.LSSchema.TABLE_NAME;

    private static final String SQL_DELETE_PREF_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContractClass.PrefSchema.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LS_ENTRIES);
        db.execSQL(SQL_CREATE_PREF_ENTRIES);
        db.execSQL(SQL_ADD_HOMEFLIGHT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Current upgrade policy is simply to discard the data and start over
        db.execSQL(SQL_DELETE_LS_ENTRIES);
        db.execSQL(SQL_DELETE_PREF_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

