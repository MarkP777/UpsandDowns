package com.example.gmpillatt.upsanddowns;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public TextView textUps;
    public TextView textDowns;
    private Integer userChoice;

    String upDown=" ";
    Integer count=0;
    Cursor c;
    String timestamp=" ";
    DBHelperClass dBHelper = new DBHelperClass(this);



    //final Intent intent = new Intent(MainActivity.this, ConfirmUpsDowns.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("MainActivity", "Started");
        setContentView(R.layout.activity_main);

        //Initialise TextView
        textUps = (TextView) findViewById(R.id.textView2);
        textDowns = (TextView) findViewById(R.id.textView3);
        writeUpsAndDowns();
    }

    public void onClick(View view) {
        Log.w("onClick", "Button pressed " + view.getId());
        Intent intent = new Intent(MainActivity.this, ConfirmUpsDowns.class);
        intent.putExtra(ConfirmUpsDowns.EXTRA_USERCHOICE, (int)view.getId());
        //setUserChoice(view.getId());
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.w("onActivityresult","started");
        TextView tV;
        tV=(TextView)findViewById(R.id.textView2);

        switch (resultCode) {
            case RESULT_OK:
            {
                writeUpsAndDowns();
                Log.w("MainActivity","OK Count "+String.format("%1$d",count));
                break;

            }
            case RESULT_CANCELED:
            {
                writeUpsAndDowns();
                Log.w("MainActivity","Cancelled Count "+String.format("%1$d",count));
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

}
