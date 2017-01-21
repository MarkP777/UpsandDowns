package com.example.gmpillatt.upsanddowns;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public TextView textUps;
    public TextView textDowns;
    private Integer userChoice;

    public static final String EXTRA_USERSELECTION="UserSelection";

    String upDown=" ";
    Integer count=0;
    Cursor c;
    String timestamp=" ";
    DBHelperClass dBHelper = new DBHelperClass(this);



    //final Intent intent = new Intent(MainActivity.this, ConfirmUpsDowns.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "Started");
        setContentView(R.layout.activity_main);

        //Initialise TextView
        textUps = (TextView) findViewById(R.id.textView2);
        textDowns = (TextView) findViewById(R.id.textView3);
        writeUpsAndDowns();
    }

    public void onClick(View view) {
        Log.w(TAG, "onClick Button pressed " + view.getId());
        Intent intent = new Intent(MainActivity.this, ConfirmUpsDowns.class);
        intent.putExtra(ConfirmUpsDowns.EXTRA_USERCHOICE, (int)view.getId());
        //setUserChoice(view.getId());
        startActivityForResult(intent,3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, "onActivityresult started");

        //Find out where we're coming from
        int userAction = data.getIntExtra(EXTRA_USERSELECTION, 0);
        Log.w(TAG,"onActivityResult user selection"+userAction);

        switch (userAction) {

            case (3): { //Added some ups or downs

                switch (resultCode) {
                    case RESULT_OK: {
                        Log.w("MainActivity", "OK Count " + String.format("%1$d", count));
                        writeUpsAndDowns();
                        break;
                    }
                    case RESULT_CANCELED: {
                        //Don't need to update the totals if the user cancelled, but keeping it in for the time being
                        Log.w("MainActivity", "Cancelled Count " + String.format("%1$d", count));
                        writeUpsAndDowns();
                        break;
                    }

                }
                break;
            }

            case (4): { //List All

                //Don't check the result code - always OK
                writeUpsAndDowns();
                Log.w(TAG, "Return from ListAll");
                break;

            }
        }

    }



    protected void writeUpsAndDowns() {

        try {

            Log.w("WriteUpsandDowns","Started");

            SQLiteDatabase db = dBHelper.getWritableDatabase();

            c=db.rawQuery("SELECT upDown,SUM(numberBoats) FROM lockStats WHERE (date_Time >= date('now','start of day')) GROUP BY upDown;",null);

            while (c.moveToNext()) {

                upDown=c.getString(0);
                count=c.getInt(1);

                if (upDown.equals("U"))
                {
                    textUps.setText(String.format("%1$d",count) + " Up");
                    Log.w("MainActivity",String.format("%1$d",count) + " Up");
                }
                else if (upDown.equals("D"))
                {
                    textDowns.setText(String.format("%1$d",count) + " Down");
                    Log.w("MainActivity",String.format("%1$d",count) + " Down");
                }

            }

        }
        catch (SQLiteException e) {

            Log.w("MainActivity", "Exception");

        }


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

        switch (item.getItemId()) {
            case R.id.Menu1:
                Intent intent1=new Intent(MainActivity.this,ListAll.class);
                startActivityForResult(intent1,4);
                return true;
            case R.id.Menu2:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
