package com.example.gmpillatt.upsanddowns;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Created by gmpillatt on 08/10/2017.
 */

public class importData extends AppCompatActivity {

    String TAG = "importData";

    private int numRecsImported = 0;

    private String importFilename;
    private String importFilePathName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the file name and folder for the input file
        importFilename = getResources().getString(R.string.importFilename);
        importFilePathName = getApplicationContext().getExternalFilesDir(null).getAbsolutePath();

        //Tell the parent activity that we've come from exportData
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_USERSELECTION, 10);

        //No checks on success
        setResult(RESULT_OK, intent);

        //Look for the import file
        Boolean inputFileFound = importFilePresent();

        /*
        if (BuildConfig.DEBUG) {
            if (inputFileFound) Log.w(TAG,"input file found");
            else Log.w(TAG,"input file not found");}
        */

        //Show the info screen
        setContentView(R.layout.importrecords);


        //Tell the user by displaying the error textview and disable the import button if import file not found
        //The user will have to Back out and try again
        if (!inputFileFound) {
            String errorText = getResources().getString(R.string.importNoFileFound) + "\nFile: " + importFilename + "\nFolder: " + importFilePathName;
            TextView errorTextView = (TextView) findViewById(R.id.textViewImportNoFile);
            errorTextView.setText(errorText);
            errorTextView.setVisibility(View.VISIBLE);
            Button clickedButton = (Button) findViewById(R.id.importConfirm);
            clickedButton.setEnabled(false);

        }

    }


    protected void importRecords() {

        try {
            DBHelperClass dBHelper = new DBHelperClass(this);
            SQLiteDatabase db = dBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();


            // Open the file. No fancy checks on existence, etc as we should already know this
            File myImportFile = new File(importFilePathName + File.separator + importFilename);

            //if (BuildConfig.DEBUG) Log.w(TAG, "Input file name is " + myImportFile.getAbsolutePath());

            // Set up the input stream
            FileInputStream inputStream = new FileInputStream(myImportFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            //Work through the file a line at a time. No validation
            String line;
            while ((line = reader.readLine()) != null) {
                String[] rowData = line.split(",");
                /*
                if (BuildConfig.DEBUG) Log.w(TAG, rowData[0] +
                        "\n" + rowData[1] +
                        "\n" + rowData[2] +
                        "\n" + rowData[3] +
                        "\n" + rowData[4] +
                        "\n" + "..............");
                */
                values.put(DBContractClass.LSSchema.COLUMN_NAME_FLIGHT, rowData[0]);
                values.put(DBContractClass.LSSchema.COLUMN_NAME_DATETIME,rowData[1]);
                values.put(DBContractClass.LSSchema.COLUMN_NAME_UPDOWN, rowData[2]);
                values.put(DBContractClass.LSSchema.COLUMN_NAME_NUMBERBOATS, Integer.parseInt(rowData[3]));
                values.put(DBContractClass.LSSchema.COLUMN_NAME_WIDEBEAM, rowData[4]);

                long newRowId = db.insert(DBContractClass.LSSchema.TABLE_NAME, null, values);

                numRecsImported ++;

            }

            //Tidy up
            inputStream.close();
            db.close();

        }

        catch (IOException eIO) {
            //if (BuildConfig.DEBUG) Log.w(TAG, "File handling exception");
        }

        catch (SQLiteException eSQL) {
            //if (BuildConfig.DEBUG) Log.w(TAG, "DB exception");
        }

        catch (NumberFormatException eNF) {
            //if (BuildConfig.DEBUG) Log.w(TAG, "Number conversion exception");
        }

    }


    protected void clearImportFile() {

        // Gets rid of the import file

        // Get the file directory
        File fileDirectory = new File(importFilePathName);

        // lists all the files into an array
        File[] dirFiles = fileDirectory.listFiles();

        if (dirFiles.length != 0) {
            // loops through the array of files, deleting files UDimport.csv
            for (int index = 0; index < dirFiles.length; index++) {
                if (dirFiles[index].getName().equals(importFilename)) {
                    //if (BuildConfig.DEBUG) Log.w(TAG, "Delete " + dirFiles[index].getName());
                    dirFiles[index].delete();
                }
            }

        }
    }

    protected boolean importFilePresent() {

        // Looks for an import file in the cache directory and returns true if it can find it
        // gets the file directory

        Boolean fileFound = false;

        File fileDirectory = new File(importFilePathName);

        //if (BuildConfig.DEBUG) Log.w(TAG, "Import folder is " + importFilePathName);

        // lists all the files into an array
        File[] dirFiles = fileDirectory.listFiles();


        if (dirFiles.length != 0) {
            // loops through the array of files, looking for a file UDimport.csv
            for (int index = 0; index < dirFiles.length; index++) {
                if (dirFiles[index].getName().equals(importFilename)) {
                    //if (BuildConfig.DEBUG) Log.w(TAG, "Found " + dirFiles[index].getName());
                    fileFound = true;
                }
            }

        }

        return fileFound;

    }


    public void confirmImportData(View view) {

        //Put all the processing in an asynchronous task
        new processImport().execute();
    }

    private class processImport extends AsyncTask<Void, Void, String> {

        LinearLayout importDataLayout;
        RelativeLayout progressBarLayout;

        protected void onPreExecute() {

            //Disable the button to prevent multiple clicks
            Button clickedButton = (Button) findViewById(R.id.importConfirm);
            clickedButton.setEnabled(false);

            //Hide the parameters and replace by a spinner
            importDataLayout = (LinearLayout) findViewById(R.id.importDataLayout);
            importDataLayout.setVisibility(View.GONE);
            progressBarLayout = (RelativeLayout) findViewById(R.id.importProgressBarLayout);
            progressBarLayout.setVisibility(View.VISIBLE);

        }


        protected String doInBackground(Void... params) {

            //import the data to the database
            importRecords();

            //don't use the return type but couldn't get it to work without it
            return "processImport completed";
        }

        protected void onPostExecute(String backgroundResult) {

            //Tell the user what happened
            Toast.makeText(getApplicationContext(), (String.format("%1$d",numRecsImported) + " records imported"),
                    Toast.LENGTH_LONG).show();

            //Get rid of the input file
            clearImportFile();

            //Get out of here
            finish();
        }


    }


    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}
