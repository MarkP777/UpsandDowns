package com.example.gmpillatt.upsanddowns;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.gmpillatt.upsanddowns.BuildConfig.DEBUG;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public TextView textUps;
    public TextView textDowns;
    private Integer userChoice;

    public static final String EXTRA_USERSELECTION = "UserSelection";

    String upDown = " ";
    Integer count = 0;
    Cursor c;
    String timestamp = " ";
    DBHelperClass dBHelper = new DBHelperClass(this);
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (BuildConfig.DEBUG) Log.w(TAG, "Started");

        //Home page. 4 large buttons and up/down counts
        setContentView(R.layout.activity_main);

        //Initialise TextView
        textUps = (TextView) findViewById(R.id.textView2);
        textDowns = (TextView) findViewById(R.id.textView3);
        writeUpsAndDowns();
    }

    public void onClick(View view) {

        //if (BuildConfig.DEBUG) Log.w(TAG, "onClick Button pressed " + view.getId());

        //Pass the id of the button pressed as EXTRA
        Intent intent = new Intent(MainActivity.this, ConfirmUpsDowns.class);
        intent.putExtra(ConfirmUpsDowns.EXTRA_USERCHOICE, (int) view.getId());
        startActivityForResult(intent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //if (BuildConfig.DEBUG) Log.w(TAG, "onActivityresult started");

        //Find out where we're coming from
        /*
        Actions are:
        1 Update record
        2 Delete record
        3 Add ups and downs
        4 List all
        5 Chart 1
        6 Chart 2
        7 Chart 3
        8 email exported data (started from exportData)
        9 export data
         */
        int userAction = data.getIntExtra(EXTRA_USERSELECTION, 0);

        //if (BuildConfig.DEBUG) Log.w(TAG, "onActivityResult user selection" + userAction);

        switch (userAction) {

            case (3): { //Added some ups or downs

                switch (resultCode) {
                    case RESULT_OK: {
                        //if (BuildConfig.DEBUG)
                        //    Log.w("MainActivity", "OK Count " + String.format("%1$d", count));
                        writeUpsAndDowns();
                        break;
                    }
                    case RESULT_CANCELED: {
                        //Don't need to update the totals if the user cancelled, but keeping it in for the time being
                        //if (BuildConfig.DEBUG)
                        //    Log.w("MainActivity", "Cancelled Count " + String.format("%1$d", count));
                        writeUpsAndDowns();
                        break;
                    }

                }
                break;
            }

            case (4): // List all
            case (10): // importData
            {

                //Don't check the result code - always OK
                writeUpsAndDowns();
                //if (BuildConfig.DEBUG) Log.w(TAG, "Return from ListAll");
                break;
            }

            case (5): // Chart 1
            case (6): // Chart 2
            case (7): // Chart 3
            case (9): //exportData
            {
                //Don't check the result code - always OK
                //if (BuildConfig.DEBUG) Log.w(TAG, "Return from Chart or Export");
                break;
            }
        }

    }


    protected void writeUpsAndDowns() {

        try {

            //if (BuildConfig.DEBUG)Log.w("WriteUpsandDowns","Started");

            db = dBHelper.getWritableDatabase();

            //Construct the where clause
            String lowerBound;
            String upperBound;

            lowerBound = "datetime('"
                    + dateTimeNow()
                    + "','start of day')";

            upperBound = "datetime('"
                    + dateTimeNow()
                    + "','+1 day','start of day')";

            String queryString = "SELECT upDown,SUM(numberBoats) FROM lockStats WHERE (date_Time >="
                    + lowerBound
                    + ") AND (date_Time <"
                    + upperBound
                    + ") GROUP BY upDown;";

            c = db.rawQuery(queryString, null);

            //Set counts. We won't get anything back from the query if there are no records
            int upCount = 0;
            int downCount = 0;

            while (c.moveToNext()) {

                upDown = c.getString(0);
                count = c.getInt(1);

                if (upDown.equals("U")) {
                    upCount = count;
                    //if (BuildConfig.DEBUG)
                    //    Log.w("MainActivity", String.format("%1$d", count) + " Up");
                } else if (upDown.equals("D")) {
                    downCount = count;
                    //if (BuildConfig.DEBUG)
                    //    Log.w("MainActivity", String.format("%1$d", count) + " Down");
                }

            }
            c.close();
            db.close();
            textUps.setText(String.format("%1$d", upCount) + " Up");
            textDowns.setText(String.format("%1$d", downCount) + " Down");

        } catch (SQLiteException e) {

            //if (BuildConfig.DEBUG) Log.w("MainActivity", "Exception");

        }

    }

    String dateTimeNow() {

        SimpleDateFormat dateFormatOutput = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
        Date justNow = new Date();

        String returnString = dateFormatOutput.format(justNow);

        //if (BuildConfig.DEBUG) Log.w("MainActivity","Today's date: "+returnString);

        return returnString;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection

        Intent intent1;

        switch (item.getItemId()) {
            case R.id.Menu1:
                intent1 = new Intent(MainActivity.this, ListAll.class);
                startActivityForResult(intent1, 4);
                return true;
            case R.id.Menu2:
                intent1 = new Intent(MainActivity.this, Chart1.class);
                startActivityForResult(intent1, 5);
                return true;
            case R.id.Menu3:
                intent1 = new Intent(MainActivity.this, Chart2.class);
                startActivityForResult(intent1, 6);
                return true;
            case R.id.Menu4:
                intent1 = new Intent(MainActivity.this, Chart3.class);
                startActivityForResult(intent1, 7);
                return true;
            case R.id.Menu5:
                intent1 = new Intent(MainActivity.this, exportData.class);
                startActivityForResult(intent1, 9);
                return true;
            case R.id.Menu6:
                intent1 = new Intent(MainActivity.this, importData.class);
                startActivityForResult(intent1, 10);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        db.close();
    }


}
