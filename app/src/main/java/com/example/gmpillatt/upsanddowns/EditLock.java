package com.example.gmpillatt.upsanddowns;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
//import androidx.core.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by gmpillatt on 17/01/2017.
 */

public class EditLock extends AppCompatActivity implements AdapterView.OnItemSelectedListener, NoticeDialogFragment.NoticeDialogListener {

    private static final String TAG = "EditLock";
    private static Cursor cursor;

    private int[] max_days_in_month;

    private Spinner minsSpinner;
    private Spinner hoursSpinner;
    private Spinner daySpinner;
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private Spinner upDownSpinner;
    private Spinner boatsSpinner;
    private CheckBox widebeamCheckbox;

    private TextView dayOfWeekTextView;

    private int dBIdInt;
    private String dBIdString;
    private String recordString;

    private String[] hoursValues = new String[24];
    private String[] minsValues = new String[60];
    private String[] dayValues = new String[31];
    private String[] monthValues = new String[12];
    private String[] yearValues = new String[15];
    private String[] boatValues = new String[9];

    private final int indexUp = 0;
    private final int indexDown = 1;

    public static final String EXTRA_DBID = "DBId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if (BuildConfig.DEBUG) Log.w(TAG, "Started");

        setContentView(R.layout.edit_lock);

        DBHelperClass dBHelper = new DBHelperClass(this);
        SQLiteDatabase db = dBHelper.getWritableDatabase();

        String[] upDownValues = new String[]{"Up", "Down"};

        Date parsedDate = new Date();
        SimpleDateFormat dateFormatToParse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int counter;

        for (counter = 0; counter < 24; counter++) {
            hoursValues[counter] = String.format("%1$02d", counter);
        }
        ;

        for (counter = 0; counter < 60; counter++) {
            minsValues[counter] = String.format("%1$02d", counter);
        }
        ;

        for (counter = 0; counter < 31; counter++) {
            dayValues[counter] = String.format("%1$02d", counter + 1);
        }
        ;

        for (counter = 0; counter < 12; counter++) {
            monthValues[counter] = String.format("%1$02d", counter + 1);
        }
        ;

        for (counter = 0; counter < 15; counter++) {
            yearValues[counter] = String.format("%1$d", counter + 2017);
        }
        ;

        for (counter = 0; counter < 9; counter++) {
            boatValues[counter] = String.format("%1$d", counter + 1);
        }

        Resources res = getResources();
        max_days_in_month = res.getIntArray(R.array.max_days_in_month);

        daySpinner = (Spinner) findViewById(R.id.spinner);
        monthSpinner = (Spinner) findViewById(R.id.spinner2);
        yearSpinner = (Spinner) findViewById(R.id.spinner3);
        minsSpinner = (Spinner) findViewById(R.id.spinner7);
        hoursSpinner = (Spinner) findViewById(R.id.spinner6);
        upDownSpinner = (Spinner) findViewById(R.id.spinner4);
        boatsSpinner = (Spinner) findViewById(R.id.spinner5);
        dayOfWeekTextView = (TextView) findViewById(R.id.textView9);
        widebeamCheckbox = (CheckBox) findViewById(R.id.cbWidebeam);

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dayValues);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, monthValues);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, yearValues);

        ArrayAdapter<String> minsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, minsValues);

        ArrayAdapter<String> hoursAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, hoursValues);

        ArrayAdapter<String> upDownAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, upDownValues);

        ArrayAdapter<String> boatsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, boatValues);

        // Specify the layout to use when the list of choices appears
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        upDownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapters to the spinner
        daySpinner.setAdapter(dayAdapter);
        monthSpinner.setAdapter(monthAdapter);
        yearSpinner.setAdapter(yearAdapter);
        minsSpinner.setAdapter(minsAdapter);
        hoursSpinner.setAdapter(hoursAdapter);
        upDownSpinner.setAdapter(upDownAdapter);
        boatsSpinner.setAdapter(boatsAdapter);

        widebeamCheckbox = (CheckBox) findViewById(R.id.cbELWidebeam);

        //Define projection for DB query
        dBIdString = (String) getIntent().getExtras().get(EXTRA_DBID);
        dBIdInt = Integer.valueOf(dBIdString);

        //if (BuildConfig.DEBUG) Log.w(TAG,"DB id: "+dBIdString);

        String[] projection = {
                DBContractClass.LSSchema._ID,
                DBContractClass.LSSchema.COLUMN_NAME_FLIGHT,
                DBContractClass.LSSchema.COLUMN_NAME_DATETIME,
                DBContractClass.LSSchema.COLUMN_NAME_NUMBERBOATS,
                DBContractClass.LSSchema.COLUMN_NAME_UPDOWN,
                DBContractClass.LSSchema.COLUMN_NAME_WIDEBEAM
        };

        //Wrap DB work in a try/catch
        try {

            cursor = db.query(DBContractClass.LSSchema.TABLE_NAME, projection, "_id= ?", new String[]{dBIdString}, null, null, null);

            cursor.moveToFirst();

        } catch (SQLiteException e) {
        }

        //Unwrap the data and display and save it

        //Date and time
        try {
            parsedDate = dateFormatToParse.parse(cursor.getString(cursor.getColumnIndex(DBContractClass.LSSchema.COLUMN_NAME_DATETIME)));
        } catch (Exception parseException) {
        }

        Calendar c = Calendar.getInstance();
        c.setTime(parsedDate);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String dayOfTheWeek = (String) DateFormat.format("EEE", parsedDate);

        recordString = cursor.getString(cursor.getColumnIndex(DBContractClass.LSSchema.COLUMN_NAME_FLIGHT)) + getString(R.string.separator);

        recordString = recordString + (String) DateFormat.format("yyyy-MM-dd HH:mm", parsedDate) + getString(R.string.separator);

        daySpinner.setSelection(day - 1);
        monthSpinner.setSelection(month);
        yearSpinner.setSelection(year - 2017);
        hoursSpinner.setSelection(hour);
        minsSpinner.setSelection(minute);
        dayOfWeekTextView.setText(dayOfTheWeek);

        // Direction

        String direction = cursor.getString(cursor.getColumnIndex(DBContractClass.LSSchema.COLUMN_NAME_UPDOWN));

        recordString = recordString + direction + getString(R.string.separator);

        if (direction.equals("U")) {
            upDownSpinner.setSelection(indexUp);
        } else {
            upDownSpinner.setSelection(indexDown);
        }

        //Number of boats
        int boats = cursor.getInt(cursor.getColumnIndex(DBContractClass.LSSchema.COLUMN_NAME_NUMBERBOATS));

        recordString = recordString + String.format("%1$d", boats) + getString(R.string.separator);

        boatsSpinner.setSelection(boats - 1);

        //Widebeam
        String widebeam = cursor.getString(cursor.getColumnIndex(DBContractClass.LSSchema.COLUMN_NAME_WIDEBEAM));

        recordString = recordString + widebeam;

        if (widebeam.equals("W")) {
            widebeamCheckbox.setChecked(true);
        } else {
            widebeamCheckbox.setChecked(false);
        }

        cursor.close();
        db.close();


        // Set listeners for the date spinners so that we can validate on the fly
        daySpinner.setOnItemSelectedListener(this);
        monthSpinner.setOnItemSelectedListener(this);
        yearSpinner.setOnItemSelectedListener(this);

    }

    public void confirmUpdate(View view) {
        try {
            DBHelperClass dBHelper = new DBHelperClass(this);
            SQLiteDatabase db = dBHelper.getWritableDatabase();

            //if (BuildConfig.DEBUG) Log.w(TAG, "confirmUpdate started");

            //User wants to update the record

            ContentValues values = new ContentValues();

            //
            values.put(DBContractClass.LSSchema._ID, dBIdInt);

            //set the flight
            values.put(DBContractClass.LSSchema.COLUMN_NAME_FLIGHT, "S3L");

            //set the date and time
            values.put(DBContractClass.LSSchema.COLUMN_NAME_DATETIME,
                    yearSpinner.getSelectedItem() + "-" +
                            monthSpinner.getSelectedItem() + "-" +
                            daySpinner.getSelectedItem() + " " +
                            hoursSpinner.getSelectedItem() + ":" +
                            minsSpinner.getSelectedItem() + ":00");

            //Set the direction
            if (upDownSpinner.getSelectedItemPosition() == indexUp) {
                values.put(DBContractClass.LSSchema.COLUMN_NAME_UPDOWN, "U");
            } else {
                values.put(DBContractClass.LSSchema.COLUMN_NAME_UPDOWN, "D");
            }

            //Set the number of boats
            values.put(DBContractClass.LSSchema.COLUMN_NAME_NUMBERBOATS, Integer.valueOf((String) boatsSpinner.getSelectedItem()));

            //Set the widebeam flag
            if (widebeamCheckbox.isChecked()) {
                values.put(DBContractClass.LSSchema.COLUMN_NAME_WIDEBEAM, "W");
            } else {
                values.put(DBContractClass.LSSchema.COLUMN_NAME_WIDEBEAM, "N");
            }

            //Update the database
            //TODO Look at the where clause
            db.update(DBContractClass.LSSchema.TABLE_NAME, values, "_id=" + dBIdString, null);

            db.close();

        } catch (SQLiteException e) {
        }


        //Tell the parent activity that the user updated a record
        Intent intent = new Intent();
        intent.putExtra(ListAll.EXTRA_USERACTION, 1);
        setResult(RESULT_OK, intent);

        finish();
    }

    public void deleteRecord(View view) {

        //if (BuildConfig.DEBUG) Log.w(TAG, "User ask for delete - confirmation required");

        //Ask user for confirmation. All other processing depends on a positive response
        showNoticeDialog();

    }

    public void showNoticeDialog() {

        // Create an instance of the dialog fragment and show it

        DialogFragment dialog = new NoticeDialogFragment();

        Bundle args = new Bundle();
        args.putString("recordDetails", recordString);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }


    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        //if (BuildConfig.DEBUG) Log.w(TAG,"Returned positive from alert dialog");

        // User touched the dialog's positive button

        try {
            DBHelperClass dBHelper = new DBHelperClass(this);
            SQLiteDatabase db = dBHelper.getWritableDatabase();

            //if (BuildConfig.DEBUG) Log.w(TAG, "deleteRecord started");

            db.delete(DBContractClass.LSSchema.TABLE_NAME, "_id= ?", new String[]{dBIdString});
            db.close();
        } catch (SQLiteException e) {
        }


        //Tell the parent activity that the user deleted a record
        Intent intent = new Intent();
        intent.putExtra(ListAll.EXTRA_USERACTION, 2);
        setResult(RESULT_OK, intent);

        finish();


    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

        // User touched the dialog's negative button - there isn't one so we really shouldn't get here

        //if (BuildConfig.DEBUG) Log.w(TAG,"In alert cancel dialog");
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        /*
        if (BuildConfig.DEBUG) Log.w(TAG, "Spinner " + parent.getId() + "Selected: "
                + daySpinner.getSelectedItemPosition() + " "
                + monthSpinner.getSelectedItemPosition() + " "
                + yearSpinner.getSelectedItemPosition());
        */

        // need to mess about with logic if days exceed max days in month and reset with daySpinner.setSelection(x);

        if (monthSpinner.getSelectedItemPosition() != 1) // Month is not February
        {
            if ((daySpinner.getSelectedItemPosition() + 1) > max_days_in_month[monthSpinner.getSelectedItemPosition()])

            {
                daySpinner.setSelection(max_days_in_month[monthSpinner.getSelectedItemPosition()] - 1);
            }
        } else if (((yearSpinner.getSelectedItemPosition() + 2017) % 4) == 0) // Leap year
        {
            if ((daySpinner.getSelectedItemPosition() + 1) > max_days_in_month[monthSpinner.getSelectedItemPosition()])

            {
                daySpinner.setSelection(max_days_in_month[monthSpinner.getSelectedItemPosition()] - 1);
            }
        } else // Not a leap year
        {
            if ((daySpinner.getSelectedItemPosition() + 1) > (max_days_in_month[monthSpinner.getSelectedItemPosition()] - 1))

            {
                daySpinner.setSelection(max_days_in_month[monthSpinner.getSelectedItemPosition()] - 2);
            }
        }

        //Reconstruct the date and then get and display the day of the week. Note that months start at 0 as per the spinner positions
        Calendar newCalendar = new GregorianCalendar(yearSpinner.getSelectedItemPosition() + 2017, monthSpinner.getSelectedItemPosition(), daySpinner.getSelectedItemPosition() + 1);
        Date newDate = newCalendar.getTime();
        dayOfWeekTextView.setText((String) DateFormat.format("EEE", newDate));
    }


    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}



