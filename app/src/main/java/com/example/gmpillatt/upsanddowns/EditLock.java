package com.example.gmpillatt.upsanddowns;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
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

public class EditLock extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "EditLock";
    private static Cursor cursor;

    private int[] max_days_in_month;

    private int daySpinnerId;
    private int monthSpinnerId;
    private int yearSpinnerId;

    private Spinner minsSpinner;
    private Spinner hoursSpinner;
    private Spinner daySpinner;
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private Spinner upDownSpinner;
    private Spinner boatsSpinner;
    private CheckBox widebeamCheckbox;

    private TextView dayOfWeekTextView;

    public static final String EXTRA_DBID= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("MainActivity", "Started");
        setContentView(R.layout.edit_lock);

        DBHelperClass dBHelper = new DBHelperClass(this);
        SQLiteDatabase db = dBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        String[] hoursValues = new String[24];
        String[] minsValues = new String[60];
        String[] dayValues = new String[31];
        String[] monthValues = new String[12];
        String[] yearValues = new String[5];
        String[] boatValues=new String[9];
        String[] upDownValues = new String[]{"Up","Down"};

        final int indexUp = 0;
        final int indexDown = 1;

        Date parsedDate = new Date();
        SimpleDateFormat dateFormatToParse=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int counter;

        for (counter = 0; counter < 24; counter++) {
            hoursValues[counter] = String.format("%1$02d",counter);
        } ;

        for (counter = 0; counter < 60; counter++) {
            minsValues[counter] = String.format("%1$02d",counter);
        } ;

        for (counter = 0; counter < 31; counter++) {
            dayValues[counter] = String.format("%1$02d",counter + 1);
        } ;

        for (counter = 0; counter < 12; counter++) {
            monthValues[counter] = String.format("%1$02d",counter + 1);
        } ;

        for (counter = 0; counter < 5; counter++) {
            yearValues[counter] = String.format("%1$d",counter + 2017);
        } ;

        for (counter = 0; counter < 9; counter++) {
            boatValues[counter] = String.format("%1$d",counter + 1);
        }

        Resources res=getResources();
        max_days_in_month = res.getIntArray(R.array.max_days_in_month);

        daySpinner = (Spinner) findViewById(R.id.spinner);
        monthSpinner = (Spinner) findViewById(R.id.spinner2);
        yearSpinner = (Spinner) findViewById(R.id.spinner3);
        minsSpinner = (Spinner) findViewById(R.id.spinner7);
        hoursSpinner = (Spinner) findViewById(R.id.spinner6);
        upDownSpinner = (Spinner) findViewById(R.id.spinner4);
        boatsSpinner  = (Spinner) findViewById(R.id.spinner5);
        dayOfWeekTextView = (TextView) findViewById(R.id.textView9);
        widebeamCheckbox = (CheckBox) findViewById(R.id.cbWidebeam);

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,dayValues);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,monthValues);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,yearValues);

        ArrayAdapter<String> minsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,minsValues);

        ArrayAdapter<String> hoursAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,hoursValues);

        ArrayAdapter<String> upDownAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,upDownValues);

        ArrayAdapter<String> boatsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,boatValues);

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

// Get the ids of the date spinners because we'll need these to validate the correct date
        daySpinnerId=daySpinner.getId();
        monthSpinnerId=monthSpinner.getId();
        yearSpinnerId=yearSpinner.getId();

//Define projection for DB query
        String dBId = (String) getIntent().getExtras().get(EXTRA_DBID);

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

            cursor = db.query(DBContractClass.DBSchema.TABLE_NAME, projection, "_id= ?", new String [] {dBId}, null, null, null);

            cursor.moveToFirst();

        }
        catch (SQLiteException e) {}

        //Unwrap the data and display it

        //Date and time
        try {
            parsedDate = dateFormatToParse.parse(cursor.getString(cursor.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_DATETIME)));
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

        daySpinner.setSelection(day-1);
        monthSpinner.setSelection(month-1);
        yearSpinner.setSelection(year-2017);
        hoursSpinner.setSelection(hour);
        minsSpinner.setSelection(minute);
        dayOfWeekTextView.setText(dayOfTheWeek);

        // Direction

        String direction = cursor.getString(cursor.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_UPDOWN));
        if (direction.equals("U"))
            {upDownSpinner.setSelection(indexUp);}
        else
            {upDownSpinner.setSelection(indexDown);}

        //Number of boats
        int boats = cursor.getInt(cursor.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_NUMBERBOATS));
        boatsSpinner.setSelection(boats-1);

        //Widebeam
        String widebeam = cursor.getString(cursor.getColumnIndex(DBContractClass.DBSchema.COLUMN_NAME_WIDEBEAM));
        if (widebeam.equals("W"))
        {widebeamCheckbox.setChecked(true);}
        else
        {widebeamCheckbox.setChecked(false);}


// Set listeners for the date spinners so that we can validate on the fly
        daySpinner.setOnItemSelectedListener(this);
        monthSpinner.setOnItemSelectedListener(this);
        yearSpinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Log.w("Spinner "+parent.getId(), "Selected: "
                + daySpinner.getSelectedItemPosition() + " "
                + monthSpinner.getSelectedItemPosition() + " "
                + yearSpinner.getSelectedItemPosition());

        // need to mess about with logic if days exceed max days in month and reset with daySpinner.setSelection(x);

        if (monthSpinner.getSelectedItemPosition() != 1) // Month is not February
        {
            if ((daySpinner.getSelectedItemPosition()+1) > max_days_in_month[monthSpinner.getSelectedItemPosition()])

            {daySpinner.setSelection(max_days_in_month[monthSpinner.getSelectedItemPosition()]-1);}
        }

        else
            if (((yearSpinner.getSelectedItemPosition()+2017)%4)==0) // Leap year
            {
                if ((daySpinner.getSelectedItemPosition() + 1) > max_days_in_month[monthSpinner.getSelectedItemPosition()])

                {
                    daySpinner.setSelection(max_days_in_month[monthSpinner.getSelectedItemPosition()] - 1);
                }
            }
            else // Not a leap year
            {
                if ((daySpinner.getSelectedItemPosition() + 1) > (max_days_in_month[monthSpinner.getSelectedItemPosition()]-1))

                {
                    daySpinner.setSelection(max_days_in_month[monthSpinner.getSelectedItemPosition()] - 2);
                }
            }

        //Reconstruct the date and then get and display the day of the week
        Calendar newCalendar = new GregorianCalendar(yearSpinner.getSelectedItemPosition()+2017, monthSpinner.getSelectedItemPosition()+1, daySpinner.getSelectedItemPosition()+1);
        Date newDate = newCalendar.getTime();
        dayOfWeekTextView.setText((String) DateFormat.format("EEE", newDate));




    }


    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }






}



