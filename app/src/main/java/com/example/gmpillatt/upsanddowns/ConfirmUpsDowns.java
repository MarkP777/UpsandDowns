package com.example.gmpillatt.upsanddowns;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    protected void onCreate (Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.confirm_ups_downs);

        //private Integer userChoice;

        //bind TextView
        textConfirm = (TextView) findViewById(R.id.tvChoice);
        textWidebeam = (TextView) findViewById(R.id.tvWidebeam);


        //bind checkbox
        checkBox = (CheckBox) findViewById(R.id.cbWidebeam);

        //initialise wideBeam
        wideBeam = false;

        int userChoice = (Integer) getIntent().getExtras().get(EXTRA_USERCHOICE);

        //userChoice=MainActivity.getUserChoice();

        switch (userChoice) {

            case R.id.btn1Up: {
                textConfirm.setBackgroundColor(getResources().getColor(R.color.colorUp));
                textConfirm.setText(getString(R.string.str1Up));
                textWidebeam.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);
                break;
            }

            case R.id.btn2Up: {
                textConfirm.setBackgroundColor(getResources().getColor(R.color.colorUp));
                textConfirm.setText(getString(R.string.str2Up));
                textWidebeam.setVisibility(View.INVISIBLE);
                checkBox.setVisibility(View.INVISIBLE);
                break;
                }

            case R.id.btn1Down: {

                textConfirm.setBackgroundColor(getResources().getColor(R.color.colorDown));
                textConfirm.setText(getString(R.string.str1Down));
                textWidebeam.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);
                break;
                }

            case R.id.btn2Down: {
                textConfirm.setBackgroundColor(getResources().getColor(R.color.colorDown));
                textConfirm.setText(getString(R.string.str2Down));
                textWidebeam.setVisibility(View.INVISIBLE);
                checkBox.setVisibility(View.INVISIBLE);
                break;
                }
        }



    }

public void confirmLock(View view) {

    super.onBackPressed();

    return;
}



}



