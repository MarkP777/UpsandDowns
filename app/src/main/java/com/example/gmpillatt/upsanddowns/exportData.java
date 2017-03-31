package com.example.gmpillatt.upsanddowns;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by gmpillatt on 04/03/2017.
 */

public class exportData extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String TAG = "exportData";

    private Spinner daySpinner;
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private TextView dayOfWeekTextView;
    private int[] max_days_in_month;
    private Date todaysDate;
    private String exportFileName;
    private File exportFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.export);

        //Tell the parent activity that we've come from exportData
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_USERSELECTION, 9);

        //No checks on success
        setResult(RESULT_OK, intent);

        //Get the start date for the export
        getExportFromDate();

    }


    protected void getExportFromDate() {

        int counter;

        String[] dayValues = new String[31];
        String[] monthValues = new String[12];
        String[] yearValues = new String[5];


        for (counter = 0; counter < 31; counter++) {
            dayValues[counter] = String.format("%1$02d", counter + 1);
        }

        for (counter = 0; counter < 12; counter++) {
            monthValues[counter] = String.format("%1$02d", counter + 1);
        }

        for (counter = 0; counter < 5; counter++) {
            yearValues[counter] = String.format("%1$d", counter + 2017);
        }

        Resources res = getResources();
        max_days_in_month = res.getIntArray(R.array.max_days_in_month);

        daySpinner = (Spinner) findViewById(R.id.spinner);
        monthSpinner = (Spinner) findViewById(R.id.spinner2);
        yearSpinner = (Spinner) findViewById(R.id.spinner3);
        dayOfWeekTextView = (TextView) findViewById(R.id.textView9);

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dayValues);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, monthValues);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, yearValues);

        // Specify the layout to use when the list of choices appears
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapters to the spinner
        daySpinner.setAdapter(dayAdapter);
        monthSpinner.setAdapter(monthAdapter);
        yearSpinner.setAdapter(yearAdapter);

        Calendar calendar = Calendar.getInstance();
        todaysDate = calendar.getTime();

        setSpinnersToToday();

        // Set listeners for the date spinners so that we can validate on the fly
        daySpinner.setOnItemSelectedListener(this);
        monthSpinner.setOnItemSelectedListener(this);
        yearSpinner.setOnItemSelectedListener(this);

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
            if ((daySpinner.getSelectedItemPosition() + 1) > max_days_in_month[monthSpinner.getSelectedItemPosition()]) {
                daySpinner.setSelection(max_days_in_month[monthSpinner.getSelectedItemPosition()] - 1);
            }
        } else if (((yearSpinner.getSelectedItemPosition() + 2017) % 4) == 0) // Leap year
        {
            if ((daySpinner.getSelectedItemPosition() + 1) > max_days_in_month[monthSpinner.getSelectedItemPosition()]) {
                daySpinner.setSelection(max_days_in_month[monthSpinner.getSelectedItemPosition()] - 1);
            }
        } else // Not a leap year
        {
            if ((daySpinner.getSelectedItemPosition() + 1) > (max_days_in_month[monthSpinner.getSelectedItemPosition()] - 1)) {
                daySpinner.setSelection(max_days_in_month[monthSpinner.getSelectedItemPosition()] - 2);
            }
        }

        //Reconstruct the date and then get and display the day of the week. Note that months start at 0 as per the spinner positions
        Calendar calendar = new GregorianCalendar(yearSpinner.getSelectedItemPosition() + 2017, monthSpinner.getSelectedItemPosition(), daySpinner.getSelectedItemPosition() + 1);
        Date exportFromDate = calendar.getTime();
        dayOfWeekTextView.setText((String) DateFormat.format("EEE", exportFromDate));

        if (exportFromDate.after(todaysDate)) { //Selected date after today
            setSpinnersToToday(); //reset the spinners to today
            dayOfWeekTextView.setText((String) DateFormat.format("EEE", todaysDate));
        }
    }

    protected void setSpinnersToToday() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todaysDate);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        String dayOfTheWeek = (String) DateFormat.format("EEE", todaysDate);

        daySpinner.setSelection(day - 1);
        monthSpinner.setSelection(month);
        yearSpinner.setSelection(year - 2017);
        dayOfWeekTextView.setText(dayOfTheWeek);
    }

    protected void export() {

        //Get the export date from the spinners

        String exportFromDate = yearSpinner.getSelectedItem() + "-"
                + monthSpinner.getSelectedItem() + "-"
                + daySpinner.getSelectedItem() + " ";

        String lowerBound = "datetime('"
                + exportFromDate
                + "','start of day')";

        String upperBound = "datetime('now',"
                + "'+1 day',"
                + "'start of day')";

        String whereClause = "(date_Time >="
                + lowerBound
                + ") AND (date_Time <"
                + upperBound
                + ")";

        String sortClause = "date_Time ASC, _ID ASC";


        DBHelperClass dBHelper = new DBHelperClass(this);
        SQLiteDatabase db = dBHelper.getWritableDatabase();
        Cursor c;

        //Define projection for DB query
        String[] projection = {
                DBContractClass.LSSchema._ID,
                DBContractClass.LSSchema.COLUMN_NAME_FLIGHT,
                DBContractClass.LSSchema.COLUMN_NAME_DATETIME,
                DBContractClass.LSSchema.COLUMN_NAME_NUMBERBOATS,
                DBContractClass.LSSchema.COLUMN_NAME_UPDOWN,
                DBContractClass.LSSchema.COLUMN_NAME_WIDEBEAM
        };

        //Wrap DB and file work in a try/catch
        try {

            //Query the db. This will reset the cursor position to the start
            c = db.query(
                    DBContractClass.LSSchema.TABLE_NAME,                     // The table to query
                    projection,                               // The columns to return
                    whereClause,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortClause                                 // The sort order
            );


            exportFileName = (String) DateFormat.format("UDyyyyMMddHHmmss", todaysDate) + ".csv";
            String endOfRecord = "\n";
            String fieldSeparator = ",";
            String outputString = "";

            Context context = getApplicationContext();
            exportFile = new File(context.getExternalCacheDir(), exportFileName);

            Boolean noRecordsToExport = true;

            //if (BuildConfig.DEBUG) Log.w(TAG, "Output file name is " + exportFile.getAbsolutePath());

            // Create the output file. No fancy checks on existence, etc. In theory, we should've cleared
            // up any old debris
            FileOutputStream outputStream = new FileOutputStream(exportFile);

            while (c.moveToNext()) {

                outputString = c.getString(c.getColumnIndex(DBContractClass.LSSchema.COLUMN_NAME_FLIGHT)) + fieldSeparator
                        + c.getString(c.getColumnIndex(DBContractClass.LSSchema.COLUMN_NAME_DATETIME)) + fieldSeparator
                        + c.getString(c.getColumnIndex(DBContractClass.LSSchema.COLUMN_NAME_UPDOWN)) + fieldSeparator
                        + String.format("%1$d", c.getInt(c.getColumnIndex(DBContractClass.LSSchema.COLUMN_NAME_NUMBERBOATS))) + fieldSeparator
                        + c.getString(c.getColumnIndex(DBContractClass.LSSchema.COLUMN_NAME_WIDEBEAM)) + endOfRecord;

                outputStream.write(outputString.getBytes());

                noRecordsToExport = false;
            }

            if (noRecordsToExport) {
                outputString = "No records found" + endOfRecord;
                outputStream.write(outputString.getBytes());
            }

            //close file and cursor
            outputStream.close();
            c.close();

            //Set the output file to readable by all
            exportFile.setReadable(true, false);

        } catch (Exception e) {
            //if (BuildConfig.DEBUG) Log.w(TAG, "Error in file output");
        }

    }

    protected void clearOldCSVs() {

        // Gets rid of any old CSV files that might lying around
        // gets the file directory
        Context context = getApplicationContext();
        File fileDirectory = new File(context.getExternalCacheDir().getAbsolutePath());

        // lists all the files into an array
        File[] dirFiles = fileDirectory.listFiles();

        if (dirFiles.length != 0) {
            // loops through the array of files, deleting files UD20*.csv
            // would use .matches and a regex to select, but my regex isn't that strong!
            for (int index = 0; index < dirFiles.length; index++) {
                if (dirFiles[index].getName().startsWith("UD20") && dirFiles[index].getName().endsWith(".csv")) {
                    //if (BuildConfig.DEBUG) Log.w(TAG, "Delete " + dirFiles[index].getName());
                    dirFiles[index].delete();
                }
            }

        }
    }


    public void confirmExportData(View view) {

        //Put all the processing in an asynchronous task
        new processExport().execute();
    }

    private class processExport extends AsyncTask<Void, Void, String> {

        LinearLayout exportDataLayout;
        RelativeLayout progressBarLayout;

        protected void onPreExecute() {

            //Disable the button to prevent multiple clicks
            Button clickedButton = (Button) findViewById(R.id.button4);
            clickedButton.setEnabled(false);

            //Hide the parameters and replace by a spinner
            exportDataLayout = (LinearLayout) findViewById(R.id.exportDataLayout);
            exportDataLayout.setVisibility(View.GONE);
            progressBarLayout = (RelativeLayout) findViewById(R.id.progressBarLayout);
            progressBarLayout.setVisibility(View.VISIBLE);
        }


        protected String doInBackground(Void... params) {

            //Clear up any old CSVs that have been left lying around
            clearOldCSVs();

            //export the data to the file. We may want to run this in a separate thread
            export();

            //don't use the return type but couldn't get it to work without it
            return "processExport completed";
        }

        protected void onPostExecute(String backgroundResult) {

            Context context = getApplicationContext();

            //Create an explicit intent
            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            // The intent does not have a URI, so declare the "text/plain" MIME type
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, exportFileName);
            emailIntent.putExtra(Intent.EXTRA_TEXT, "See attached");
            String filePath = exportFile.getAbsolutePath();
            //if (BuildConfig.DEBUG) Log.w(TAG, "Output file path is" + filePath);
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:" + filePath));

            //Redisplay the selection screen. Hide the spinner
            progressBarLayout.setVisibility(View.GONE);
            exportDataLayout.setVisibility(View.VISIBLE);

            //Start the intent
            startActivityForResult(emailIntent, 8);
            //startActivity(emailIntent);

            //Get out of here
            //finish();
        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //This is included to keep the user in the activity until the intent is completed
        //and then quit so that they go back to the home screen

        //if (BuildConfig.DEBUG) Log.w(TAG, "onActivityresult started");

        //Just get out of here
        finish();

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}
