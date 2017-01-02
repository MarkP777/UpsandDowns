package com.example.gmpillatt.upsanddowns;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public TextView textUps;
    public TextView textDowns;
    private Integer userChoice;

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
        startActivity(intent);
    }

    protected void writeUpsAndDowns() {

        textUps.setText(Integer.toString(0) + " Up");
        textDowns.setText(Integer.toString(0) + " Up");


        return;

    }

    public Integer getUserChoice() {

        return userChoice;
    }

    public void setUserChoice(Integer integer) {

        userChoice = integer;
    }

}
