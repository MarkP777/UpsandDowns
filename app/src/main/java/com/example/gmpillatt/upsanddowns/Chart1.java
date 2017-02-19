package com.example.gmpillatt.upsanddowns;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Chart1 extends AppCompatActivity {

    String TAG="Chart1";
    DBHelperClass dBHelper = new DBHelperClass(this);
    Cursor c;
    int upCount,downCount;
    String[] xAxisValues = new String[11];
    BarChart chart;
    TextView chartTitle;
    Button prevButton;
    Button nextButton;
    List<BarEntry> upEntries;
    List<BarEntry> downEntries;
    final SimpleDateFormat dateFormatTitle = new SimpleDateFormat("EEEE\ndd MMMM yyyy");
    final SimpleDateFormat dateFormatButton = new SimpleDateFormat("dd/MM");
    Integer upBarColor;
    Integer downBarColor;
    Calendar todaysDate;
    Calendar prevDate;
    Calendar nextDate;

    // set custom bar width - just a a little bit of space between each up/down pair of bars
    final float groupSpace = 0.06f;
    final float barSpace = 0.00f; // x2 dataset
    final float barWidth = 0.47f; // x2 dataset
    // (0.00 + 0.47) * 2 + 0.06 = 1.00 -> interval per "group"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tell the parent activity that we're doing a Chart1. Always return OK
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_USERSELECTION,5);
        setResult(RESULT_OK,intent);

        setContentView(R.layout.chart1);

        //Find the title and buttons
        chartTitle = (TextView) findViewById(R.id.chart1Title);
        prevButton = (Button) findViewById(R.id.chart1Prev);
        nextButton = (Button) findViewById(R.id.chart1Next);

        //Find the chart
        chart = (BarChart) findViewById(R.id.chart1);

        //Initialise lists to hold our data
        upEntries = new ArrayList<>();
        downEntries = new ArrayList<>();

        //initialise dates
        todaysDate = Calendar.getInstance();
        prevDate = Calendar.getInstance();
        prevDate.setTime(todaysDate.getTime());
        nextDate = Calendar.getInstance();
        nextDate.setTime(todaysDate.getTime());

        //Set the title
        chartTitle.setText(dateFormatTitle.format(todaysDate.getTime()));

        //Disable and hide the next button, cos you can't go past today, but set up the date anyway
        nextButton.setEnabled(false);
        nextButton.setVisibility(View.INVISIBLE);
        nextDate.add(Calendar.DATE,1);

        //Set the date for the previous button as yesterday
        prevDate.add(Calendar.DATE, -1);
        prevButton.setText(dateFormatButton.format(prevDate.getTime()));

        //Set up stuff for the chart that won't change with the data

        upBarColor=getResources().getColor(R.color.colorUp);
        downBarColor=getResources().getColor(R.color.colorDown);

        chart.setTouchEnabled(false);

        chart.setDescription(null);


        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        leftAxis.setGranularity(1f);
        rightAxis.setGranularity(1f);
        leftAxis.setSpaceTop(0f);
        rightAxis.setSpaceTop(0f);
        leftAxis.setSpaceBottom(0f);
        rightAxis.setSpaceBottom(0f);
        leftAxis.setAxisMinimum(0f);
        rightAxis.setAxisMinimum(0f);


        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(11f);
        xAxis.setLabelCount(11);
        xAxis.setValueFormatter(new Chart1XAxisValueFormatter(xAxisValues));
        xAxis.setDrawGridLines(false);
        xAxis.setCenterAxisLabels(true);

        //Set up the X Axis labels
        int xAxisIndex = 0;
        for (int hour = 8; hour <= 18; hour++)
        {

            if (hour <= 12) {
                xAxisValues[xAxisIndex] = String.format("%1$d",hour);
            }
            else {
                xAxisValues[xAxisIndex] = String.format("%1$d",hour-12);
            }

            xAxisIndex++;
        }


        //Get initial chart data
        fillChartData(todaysDate);

        //Display the chart
        chart.invalidate(); // refresh

    }

    protected void fillChartData(Calendar date) {

        int xAxisIndex = 0;

        upEntries.clear();
        downEntries.clear();

        for (int hour = 8; hour <= 18; hour++)

        {

            getUpandDownsByHour(date, hour);

            upEntries.add(new BarEntry(xAxisIndex, upCount));
            downEntries.add(new BarEntry(xAxisIndex, downCount));

            //We'd need to set the xAxisValues here if they weren't static: xAxisValues[xAxisIndex] = blah

            xAxisIndex++;

        }

        //TODO review whether this new is correct
        BarDataSet upSet = new BarDataSet(upEntries, "Ups"); // add entries to dataset
        BarDataSet downSet = new BarDataSet(downEntries, "Downs"); // add entries to dataset

        upSet.setColor(upBarColor);
        downSet.setColor(downBarColor);

        upSet.setDrawValues(false);
        downSet.setDrawValues(false);

        BarData data = new BarData(upSet, downSet);
        data.setBarWidth(barWidth); // set the width of each bar
        chart.setData(data);

        chart.groupBars(0f, groupSpace, barSpace);

    }


    protected void getUpandDownsByHour(Calendar date, int hour) {

        int count;

        try {

            if (BuildConfig.DEBUG) Log.w("WriteUpsandDowns", "getUpandDownsByHour");

            SQLiteDatabase db = dBHelper.getWritableDatabase();

            //Construct the date part of the query
            SimpleDateFormat dateFormatOutput = new SimpleDateFormat("yyyy-MM-dd");


            //Find some test data
            //String dateString="2017-02-12";

            String dateString = dateFormatOutput.format(date.getTime());

            //Construct the where clause
            String lowerBound;
            String upperBound;

            if (hour == 8) {
                lowerBound = "datetime('"
                        + dateString
                        + "','start of day')";

            } else {
                lowerBound = "datetime('"
                        + dateString
                        + "','start of day','+"
                        + String.format("%1$d", hour)
                        + " hours')";
            }
            if (hour == 18) {
                upperBound = "datetime('"
                        + dateString
                        + "','+1 day','start of day')";
            } else {
                upperBound = "datetime('"
                        + dateString
                        + "','start of day','+"
                        + String.format("%1$d", (hour + 1))
                        + " hours')";
            }


            String queryString = "SELECT upDown,SUM(numberBoats) FROM lockStats WHERE (date_Time >="
                    + lowerBound
                    + ") AND (date_Time <"
                    + upperBound
                    + ") GROUP BY upDown;";


            c = db.rawQuery(queryString, null);


            //Set counts. We won't get anything back from the query if there are no records
            upCount = 0;
            downCount = 0;

            while (c.moveToNext()) {


                String upDown = c.getString(0);
                count = c.getInt(1);

                if (upDown.equals("U")) {
                    upCount = count;
                    if (BuildConfig.DEBUG)
                        Log.w("getUpandDownsByHour", String.format("%1$d", count) + " Up");
                } else if (upDown.equals("D")) {
                    downCount = count;
                    if (BuildConfig.DEBUG)
                        Log.w("getUpandDownsByHour", String.format("%1$d", count) + " Down");
                }

            }
            c.close();


        } catch (SQLiteException e) {

            if (BuildConfig.DEBUG) Log.w("getUpandDownsByHour", "Exception");

        }


    }

    public void clickPrev(View view)
    {

        //Disable the button until we've sorted out the chart
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

        //Get the data and draw the chart
        fillChartData(prevDate);
        chart.notifyDataSetChanged();
        chart.invalidate();

        //set the title
        chartTitle.setText(dateFormatTitle.format(prevDate.getTime()));

        //Change the date on this button and the Next. No validation. User can go back as far as they wish
        prevDate.add(Calendar.DATE, -1);
        prevButton.setText(dateFormatButton.format(prevDate.getTime()));

        nextDate.add(Calendar.DATE, -1);
        nextButton.setText(dateFormatButton.format(nextDate.getTime()));

        //Enable Next and Prev buttons
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);
        nextButton.setVisibility(View.VISIBLE);
    }

    public void clickNext(View view) {
        //Disable the button until we've sorted out the chart
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

        //Get the data and draw the chart
        fillChartData(nextDate);
        chart.notifyDataSetChanged();
        chart.invalidate();

        //set the title
        chartTitle.setText(dateFormatTitle.format(nextDate.getTime()));

        //Change the date on the previous button
        prevDate.add(Calendar.DATE, +1);
        prevButton.setText(dateFormatButton.format(prevDate.getTime()));
        prevButton.setEnabled(true);

        //Sort out the next button. Blank, disabled and invisible if it's today
        nextDate.add(Calendar.DATE, +1);
        if (!nextDate.after(todaysDate)) {
            nextButton.setText(dateFormatButton.format(nextDate.getTime()));
            nextButton.setEnabled(true);
        } else {
            nextButton.setText("");
            nextButton.setVisibility(View.INVISIBLE);
        }

    }
}
