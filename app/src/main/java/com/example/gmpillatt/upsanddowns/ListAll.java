package com.example.gmpillatt.upsanddowns;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gmpillatt on 14/01/2017.
 */

public class ListAll extends ListActivity {

    private static final String TAG = "ListAll";
    private static Cursor cursor;

    List<ItemData> threeColumnList = new ArrayList<>();
    ThreeColumnAdapter adapter;

    int clickedRecordPosition=0;

    public static final String EXTRA_USERACTION="User action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        String tempCol1;
        String tempCol2;
        String tempCol3;
        Integer tempTextColor=0;
        Integer tempDBId=0;

        Integer upTextColor=getResources().getColor(R.color.colorUp);
        Integer downTextColor=getResources().getColor(R.color.colorDown);

        DBHelperClass dBHelper = new DBHelperClass(this);
        SQLiteDatabase db = dBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        Date parsedDate = new Date();
        SimpleDateFormat dateFormatToParse=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormatOutout=new SimpleDateFormat("HH:mm EEE dd/MM");

        String widebeamFlag="(W)";

        //Define projection for DB query
        String[] projection = {
                DBContractClass.DBSchema._ID,
                DBContractClass.DBSchema.COLUMN_NAME_FLIGHT,
                DBContractClass.DBSchema.COLUMN_NAME_DATETIME,
                DBContractClass.DBSchema.COLUMN_NAME_NUMBERBOATS,
                DBContractClass.DBSchema.COLUMN_NAME_UPDOWN,
                DBContractClass.DBSchema.COLUMN_NAME_WIDEBEAM
        };

        //Wrap DB work in a try/catch
        try {

            //Query the db. This will reset the cursor position to the start
            //Note that we should perhaps pass a string array for the sort order, but I doubt if it really matters
            cursor = db.query(
                    DBContractClass.DBSchema.TABLE_NAME,                     // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    "date_Time DESC, _ID DESC"                                 // The sort order
            );

        }
        catch (SQLiteException e) {}

            while (cursor.moveToNext()) {

                try {
                    parsedDate = dateFormatToParse.parse(cursor.getString(cursor.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_DATETIME)));
                } catch (Exception parseException) {
                }

                tempDBId = cursor.getInt(cursor.getColumnIndex(DBContractClass.DBSchema._ID));

                tempCol1 = String.format("%1$d",cursor.getInt(cursor.getColumnIndex(DBContractClass.DBSchema._ID)))
                         + " "
                         + dateFormatOutout.format(parsedDate);

                if (cursor.getString(cursor.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_UPDOWN)).equals("U")) {
                    tempCol2 = "Up";
                    tempTextColor = upTextColor;
                } else if (cursor.getString(cursor.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_UPDOWN)).equals("D")) {
                    tempCol2 = "Down";
                    tempTextColor = downTextColor;
                } else {
                    tempCol2 = " ";
                }

                tempCol3 = String.format("%1$d", cursor.getInt(cursor.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_NUMBERBOATS)));
                if (cursor.getString(cursor.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_WIDEBEAM)).equals("W")) {
                    tempCol3 = tempCol3 + widebeamFlag;
                }

                threeColumnList.add(new ItemData(tempCol1, tempCol2, tempCol3, tempTextColor, tempDBId));

            }

*/
        //Tell the parent activity that we're doing a ListAll. Always return OK
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_USERSELECTION,4);
        setResult(RESULT_OK,intent);

        //Fill the list of data that's going to be displayed
        fillList();

        // Seems to be integral to ListActivity and means that we don't setContentView
        ListView listView = getListView();

        //ThreeHorizontalTextViewsAdapter threeHorizontalTextViewsAdapter = new ThreeHorizontalTextViewsAdapter(this, R.layout.three_horizontal_text_views_layout, threeStringsList);
        adapter = new ThreeColumnAdapter(this, R.layout.three_column_listview, threeColumnList);

        listView.setAdapter(adapter);

            cursor.close();
    }

    @Override
    public void onListItemClick(ListView listView, View itemView, int position, long id) {


        if (BuildConfig.DEBUG) Log.w(TAG, "Clicked "+(Integer.toString(position)+" "+Long.toString(id)));

        clickedRecordPosition=position;

        Intent intent=new Intent(ListAll.this, EditLock.class);
        intent.putExtra(EditLock.EXTRA_DBID, String.format("%1$d",threeColumnList.get(position).getdBId()));
        startActivityForResult(intent,0);


        /*
        threeColumnList.set(1,new ItemData("g", "h", "i",0));
        adapter.notifyDataSetChanged();

        while (cursor.moveToNext()) {

            counter++;
            myList.add(Integer.toString(c.getInt(c.getColumnIndex(DBContractClass.DBSchema._ID)))
                    + "  " + c.getString(c.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_FLIGHT))
                    + "  " + String.format("%1$d",c.getInt(c.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_NUMBERBOATS)))
                    + "  " + c.getString(c.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_DATETIME)));
        */
        }


    void fillList() {

        String tempCol1;
        String tempCol2;
        String tempCol3;
        Integer tempTextColor=0;
        Integer tempDBId=0;

        Integer upTextColor=getResources().getColor(R.color.colorUp);
        Integer downTextColor=getResources().getColor(R.color.colorDown);

        DBHelperClass dBHelper = new DBHelperClass(this);
        SQLiteDatabase db = dBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        Date parsedDate = new Date();
        SimpleDateFormat dateFormatToParse=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormatOutout=new SimpleDateFormat("HH:mm EEE dd/MM");

        String widebeamFlag="(W)";

        //Make sure that the list is clear before we start adding to it
        threeColumnList.clear();

        //Define projection for DB query
        String[] projection = {
                DBContractClass.DBSchema._ID,
                DBContractClass.DBSchema.COLUMN_NAME_FLIGHT,
                DBContractClass.DBSchema.COLUMN_NAME_DATETIME,
                DBContractClass.DBSchema.COLUMN_NAME_NUMBERBOATS,
                DBContractClass.DBSchema.COLUMN_NAME_UPDOWN,
                DBContractClass.DBSchema.COLUMN_NAME_WIDEBEAM
        };

        //Wrap DB work in a try/catch
        try {

            //Query the db. This will reset the cursor position to the start
            //Note that we should perhaps pass a string array for the sort order, but I doubt if it really matters
            cursor = db.query(
                    DBContractClass.DBSchema.TABLE_NAME,                     // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    "date_Time DESC, _ID DESC"                                 // The sort order
            );

        }
        catch (SQLiteException e) {}


        //Work through the cursor, which will have been set to the beginning by the query
        while (cursor.moveToNext()) {

            try {
                parsedDate = dateFormatToParse.parse(cursor.getString(cursor.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_DATETIME)));
            } catch (Exception parseException) {
            }

            tempDBId = cursor.getInt(cursor.getColumnIndex(DBContractClass.DBSchema._ID));

            tempCol1 = String.format("%1$d",cursor.getInt(cursor.getColumnIndex(DBContractClass.DBSchema._ID)))
                    + " "
                    + dateFormatOutout.format(parsedDate);

            if (cursor.getString(cursor.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_UPDOWN)).equals("U")) {
                tempCol2 = "Up";
                tempTextColor = upTextColor;
            } else if (cursor.getString(cursor.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_UPDOWN)).equals("D")) {
                tempCol2 = "Down";
                tempTextColor = downTextColor;
            } else {
                tempCol2 = " ";
            }

            tempCol3 = String.format("%1$d", cursor.getInt(cursor.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_NUMBERBOATS)));
            if (cursor.getString(cursor.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_WIDEBEAM)).equals("W")) {
                tempCol3 = tempCol3 + widebeamFlag;
            }

            threeColumnList.add(new ItemData(tempCol1, tempCol2, tempCol3, tempTextColor, tempDBId));

        }

        //Close the cursor
        cursor.close();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (BuildConfig.DEBUG) Log.w(TAG,"onActivityresultstarted");

        switch (resultCode) {
            case RESULT_OK:
                //User changed the data
            {
                //Find out what the user did
                //TODO consider using shared preferences to pass what record is being worked on and user action
                int userAction = data.getIntExtra(EXTRA_USERACTION,0);

                switch (userAction) {
                case 1:
                {
                    //Update
                    if (BuildConfig.DEBUG) Log.w(TAG,"User action was(1): "+userAction);
                    //Need to refresh the whole of the dataset just in case the date/time has changed

                    // thereby changing the order of records
                    fillList();

                    break;
                }

                case 2:
                {
                    //Delete
                    if (BuildConfig.DEBUG) Log.w(TAG,"User action was(2): "+userAction);

                    //Need to remove the item from the list
                    threeColumnList.remove(clickedRecordPosition);



                    break;

                }


            }

                //Tell the adapter that the list has changed
                adapter.notifyDataSetChanged();

            }
            case RESULT_CANCELED:
                //User cancelled. Nothing to do.
            {
                break;
            }


        }
    }

















    }


