package com.example.gmpillatt.upsanddowns;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import static android.R.attr.id;

/**
 * Created by gmpillatt on 01/01/2017.
 */

public class ConfirmUpsDowns extends AppCompatActivity {

    private TextView textConfirm;
    private TextView textWidebeam;
    public CheckBox checkBox;
    public boolean wideBeam;
    public static final String EXTRA_USERCHOICE= "";

    DBHelperClass dBHelper = new DBHelperClass(this);
    ContentValues values = new ContentValues();

    @Override
    protected void onCreate (Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.confirm_ups_downs);

        Log.w("Confirm","started");

        //private Integer userChoice;

        //bind TextView
        textConfirm = (TextView) findViewById(R.id.tvChoice);
        textWidebeam = (TextView) findViewById(R.id.tvWidebeam);


        //bind checkbox
        checkBox = (CheckBox) findViewById(R.id.cbWidebeam);

        //initialise wideBeam
        wideBeam = false;

        //set the flight
        values.put(DBContractClass.DBSchema.COLUMN_NAME_FLIGHT, "S3L");



        int userChoice = (Integer) getIntent().getExtras().get(EXTRA_USERCHOICE);

        switch (userChoice) {

            case R.id.btn1Up: {
                textConfirm.setTextColor(getResources().getColor(R.color.colorUp));
                textConfirm.setText(getString(R.string.str1Up));
                textWidebeam.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);
                values.put(DBContractClass.DBSchema.COLUMN_NAME_NUMBERBOATS, 1);
                values.put(DBContractClass.DBSchema.COLUMN_NAME_UPDOWN, "U");
                break;
            }

            case R.id.btn2Up: {
                textConfirm.setTextColor(getResources().getColor(R.color.colorUp));
                textConfirm.setText(getString(R.string.str2Up));
                textWidebeam.setVisibility(View.INVISIBLE);
                checkBox.setVisibility(View.INVISIBLE);
                values.put(DBContractClass.DBSchema.COLUMN_NAME_NUMBERBOATS, 2);
                values.put(DBContractClass.DBSchema.COLUMN_NAME_UPDOWN, "U");
                break;
                }

            case R.id.btn1Down: {

                textConfirm.setTextColor(getResources().getColor(R.color.colorDown));
                textConfirm.setText(getString(R.string.str1Down));
                textWidebeam.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);
                values.put(DBContractClass.DBSchema.COLUMN_NAME_NUMBERBOATS, 1);
                values.put(DBContractClass.DBSchema.COLUMN_NAME_UPDOWN, "D");
                break;
                }

            case R.id.btn2Down: {
                textConfirm.setTextColor(getResources().getColor(R.color.colorDown));
                textConfirm.setText(getString(R.string.str2Down));
                textWidebeam.setVisibility(View.INVISIBLE);
                checkBox.setVisibility(View.INVISIBLE);
                values.put(DBContractClass.DBSchema.COLUMN_NAME_NUMBERBOATS, 2);
                values.put(DBContractClass.DBSchema.COLUMN_NAME_UPDOWN, "D");
                break;
                }
        }



    }

public void confirmLock(View view) {

    if (checkBox.isChecked())
    {
        values.put(DBContractClass.DBSchema.COLUMN_NAME_WIDEBEAM, "W");
    }
    else
    {
        values.put(DBContractClass.DBSchema.COLUMN_NAME_WIDEBEAM, "N");
    }

    try {
        SQLiteDatabase db = dBHelper.getWritableDatabase();
        long newRowId = db.insert(DBContractClass.DBSchema.TABLE_NAME, null, values);
        Log.w("Confirm","Row inserted");
    }
    catch (SQLiteException e)
    {
        Log.w("Confirm", "Exception");
    }

    setResult(RESULT_OK);
    finish();

    return;
}



}


