package com.example.gmpillatt.upsanddowns;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.id;

/**
 * Created by gmpillatt on 01/01/2017.
 */

public class ConfirmUpsDowns extends AppCompatActivity {

    private TextView textConfirm;
    public CheckBox checkBox;
    public boolean wideBeam;
    public static final String EXTRA_USERCHOICE= "UserChoice";

    DBHelperClass dBHelper = new DBHelperClass(this);
    ContentValues values = new ContentValues();

    Intent intent = new Intent();

    @Override
    protected void onCreate (Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.confirm_ups_downs);

        //if (BuildConfig.DEBUG) Log.w("Confirm","started");

        //bind TextView
        textConfirm = (TextView) findViewById(R.id.tvChoice);

        //bind checkbox
        checkBox = (CheckBox) findViewById(R.id.cbWidebeam);

        //initialise wideBeam
        wideBeam = false;

        //Tell the parent activity that we've been into confirmUpsDowns
        //Set the result as Cancelled. This will be reset to successful if the user confirms
        intent.putExtra(MainActivity.EXTRA_USERSELECTION,3);
        setResult(RESULT_CANCELED,intent);

        int userChoice = (Integer) getIntent().getExtras().get(EXTRA_USERCHOICE);

        switch (userChoice) {

            case R.id.btn1Up: {
                textConfirm.setTextColor(getResources().getColor(R.color.colorUp));
                textConfirm.setText(getString(R.string.str1Up));
                checkBox.setVisibility(View.VISIBLE);
                values.put(DBContractClass.LSSchema.COLUMN_NAME_NUMBERBOATS, 1);
                values.put(DBContractClass.LSSchema.COLUMN_NAME_UPDOWN, "U");
                break;
            }

            case R.id.btn2Up: {
                textConfirm.setTextColor(getResources().getColor(R.color.colorUp));
                textConfirm.setText(getString(R.string.str2Up));
                checkBox.setVisibility(View.INVISIBLE);
                values.put(DBContractClass.LSSchema.COLUMN_NAME_NUMBERBOATS, 2);
                values.put(DBContractClass.LSSchema.COLUMN_NAME_UPDOWN, "U");
                break;
                }

            case R.id.btn1Down: {

                textConfirm.setTextColor(getResources().getColor(R.color.colorDown));
                textConfirm.setText(getString(R.string.str1Down));
                checkBox.setVisibility(View.VISIBLE);
                values.put(DBContractClass.LSSchema.COLUMN_NAME_NUMBERBOATS, 1);
                values.put(DBContractClass.LSSchema.COLUMN_NAME_UPDOWN, "D");
                break;
                }

            case R.id.btn2Down: {
                textConfirm.setTextColor(getResources().getColor(R.color.colorDown));
                textConfirm.setText(getString(R.string.str2Down));
                checkBox.setVisibility(View.INVISIBLE);
                values.put(DBContractClass.LSSchema.COLUMN_NAME_NUMBERBOATS, 2);
                values.put(DBContractClass.LSSchema.COLUMN_NAME_UPDOWN, "D");
                break;
                }
        }

    }

    String dateTimeNow() {

        SimpleDateFormat dateFormatOutput = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
        Date justNow = new Date();

        String returnString = dateFormatOutput.format(justNow);

        //if (BuildConfig.DEBUG) Log.w("Confirm lock","Today's date: "+returnString);

        return returnString;
    }

public void confirmLock(View view) {

    //Set the widebeam flag "W" or "N"
    if (checkBox.isChecked())
    {
        values.put(DBContractClass.LSSchema.COLUMN_NAME_WIDEBEAM, "W");
    }
    else
    {
        values.put(DBContractClass.LSSchema.COLUMN_NAME_WIDEBEAM, "N");
    }

    //Set the flight and current time
    values.put(DBContractClass.LSSchema.COLUMN_NAME_FLIGHT, "S3L");
    values.put(DBContractClass.LSSchema.COLUMN_NAME_DATETIME,dateTimeNow());

    try {
        SQLiteDatabase db = dBHelper.getWritableDatabase();
        long newRowId = db.insert(DBContractClass.LSSchema.TABLE_NAME, null, values);
        //if (BuildConfig.DEBUG) Log.w("Confirm","Row inserted");
        db.close();
    }
    catch (SQLiteException e)
    {
        //if (BuildConfig.DEBUG) Log.w("Confirm", "Exception");
    }

    setResult(RESULT_OK,intent);
    finish();

}



}



