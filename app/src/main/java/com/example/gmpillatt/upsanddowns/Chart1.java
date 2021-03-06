package com.example.gmpillatt.upsanddowns;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Float.NaN;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.YEAR;

public class Chart1 extends AppCompatActivity {

    String TAG = "Chart1";
    DBHelperClass dBHelper = new DBHelperClass(this);
    Cursor c;
    int upCount, downCount;
    String[] xAxisValues = new String[11];
    BarChart chart;
    TextView chartTitle;
    Button prevButton;
    Button nextButton;
    List<BarEntry> totalEntries;
    final SimpleDateFormat dateFormatTitle = new SimpleDateFormat("EEEE\ndd MMMM yyyy");
    final SimpleDateFormat dateFormatButton = new SimpleDateFormat("dd/MM");
    Integer upBarColor;
    Integer downBarColor;
    Calendar todaysDate;
    Calendar prevDate;
    Calendar nextDate;
    Calendar currentDate;
    Boolean noMoreRecords;
    LegendEntry downLegendEntry;
    LegendEntry upLegendEntry;
    List<LegendEntry> legendEntries;


    // set custom bar width - just a a little bit of space between each bar
    //final float groupSpace = 0.20f;
    final float groupSpace = 0.00f;
    final float barSpace = 0.05f;
    // final float barWidth = 0.40f; // x2 dataset
    final float barWidth = 0.95f;
    // (0.00 + 0.50) * 2 + 0.00 = 1.00 -> interval per "group" (if grouped)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tell the parent activity that we're doing a Chart1. Always return OK
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_USERSELECTION, 5);
        setResult(RESULT_OK, intent);

        setContentView(R.layout.chart1);

        //Find the title and buttons
        chartTitle = (TextView) findViewById(R.id.chart1Title);
        prevButton = (Button) findViewById(R.id.chart1Prev);
        nextButton = (Button) findViewById(R.id.chart1Next);

        //Find the chart
        chart = (BarChart) findViewById(R.id.chart1);

        //Initialise lists to hold our data
        totalEntries = new ArrayList<>();

        //initialise dates
        todaysDate = Calendar.getInstance();
        prevDate = Calendar.getInstance();
        prevDate.setTime(todaysDate.getTime());
        nextDate = Calendar.getInstance();
        nextDate.setTime(todaysDate.getTime());
        currentDate = Calendar.getInstance();
        currentDate.setTime(todaysDate.getTime());
        noMoreRecords = false;

        //Set the title
        chartTitle.setText(dateFormatTitle.format(todaysDate.getTime()));

        //Get the current day's totals in readiness for initialising the chart legend
        getUpAndDownsByDay(currentDate);

        //Disable and hide the next button, cos you can't go past today
        nextButton.setEnabled(false);
        nextButton.setVisibility(View.INVISIBLE);

        //Find the date for the Previous button
        getPrevDate();
        if (!noMoreRecords) {
            prevButton.setText(dateFormatButton.format(prevDate.getTime()));
            prevButton.setEnabled(true);
            prevButton.setVisibility(View.VISIBLE);
        } else {
            prevButton.setText("");
            prevButton.setVisibility(View.INVISIBLE);
        }

        //Set up stuff for the chart that won't change with the data

        upBarColor = getResources().getColor(R.color.colorUp);
        downBarColor = getResources().getColor(R.color.colorDown);

        chart.setTouchEnabled(false);
        chart.setDescription(null);

        //Set up custom legend
        downLegendEntry = new LegendEntry(("Downs (" + String.format("%1$d",downCount) + ")"), Legend.LegendForm.DEFAULT, Float.NaN, Float.NaN, null, downBarColor);
        upLegendEntry = new LegendEntry(("Ups (" + String.format("%1$d",upCount) + ")"), Legend.LegendForm.DEFAULT, Float.NaN, Float.NaN, null, upBarColor);
        legendEntries = new ArrayList<>();
        legendEntries.add(0, downLegendEntry);
        legendEntries.add(1, upLegendEntry);

        //Display custom legend
        Legend legend = chart.getLegend();
        legend.setCustom(legendEntries);
        legend.setEnabled(true);

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
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(10.5f);
        xAxis.setLabelCount(11);
        xAxis.setValueFormatter(new Chart1XAxisValueFormatter(xAxisValues));
        xAxis.setDrawGridLines(false);
        xAxis.setCenterAxisLabels(false);

        //Set up the X Axis labels
        int xAxisIndex = 0;
        for (int hour = 8; hour <= 18; hour++) {

            if (hour <= 12) {
                xAxisValues[xAxisIndex] = String.format("%1$d", hour);
            } else {
                xAxisValues[xAxisIndex] = String.format("%1$d", hour - 12);
            }

            xAxisIndex++;
        }

        //Get initial chart data
        fillChartData(currentDate);

        //Display the chart
        chart.invalidate(); // refresh

    }

    protected void fillChartData(Calendar date) {

        int xAxisIndex = 0;

        totalEntries.clear();

        for (int hour = 8; hour <= 18; hour++)

        {

            getUpAndDownsByHour(date, hour);

            totalEntries.add(new BarEntry(xAxisIndex, new float[]{downCount, upCount}));

            //We'd need to set the xAxisValues here if they weren't static: xAxisValues[xAxisIndex] = blah

            xAxisIndex++;

        }

        //TODO review whether this new is correct
        BarDataSet totalSet = new BarDataSet(totalEntries, "Totals"); // add entries to dataset

        totalSet.setColors(new int[]{downBarColor, upBarColor});

        totalSet.setDrawValues(false);

        BarData data = new BarData(totalSet);
        data.setBarWidth(barWidth); // set the width of each bar
        chart.setData(data);

    }


    protected void getUpAndDownsByHour(Calendar date, int hour) {

        int count;

        try {

            //if (BuildConfig.DEBUG) Log.w("WriteUpsandDowns", "getUpandDownsByHour");

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
                    //if (BuildConfig.DEBUG)
                    //    Log.w("getUpandDownsByHour", String.format("%1$d", count) + " Up");
                } else if (upDown.equals("D")) {
                    downCount = count;
                    //if (BuildConfig.DEBUG)
                    //    Log.w("getUpandDownsByHour", String.format("%1$d", count) + " Down");
                }

            }
            c.close();
            db.close();


        } catch (SQLiteException e) {

            //if (BuildConfig.DEBUG) Log.w("getUpandDownsByHour", "Exception");

        }


    }


    protected void getUpAndDownsByDay(Calendar date) {

        int count;

        try {

            //if (BuildConfig.DEBUG) Log.w("WriteUpsandDowns", "getUpandDownsByHour");

            SQLiteDatabase db = dBHelper.getWritableDatabase();

            //Construct the date part of the query
            SimpleDateFormat dateFormatOutput = new SimpleDateFormat("yyyy-MM-dd");


            //Find some test data
            //String dateString="2017-02-12";

            String dateString = dateFormatOutput.format(date.getTime());

            //Construct the where clause
            String lowerBound;
            String upperBound;

            lowerBound = "datetime('"
                    + dateString
                    + "','start of day')";

            upperBound = "datetime('"
                    + dateString
                    + "','+1 day','start of day')";

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
                    //if (BuildConfig.DEBUG)
                    //    Log.w("getUpandDownsByHour", String.format("%1$d", count) + " Up");
                } else if (upDown.equals("D")) {
                    downCount = count;
                    //if (BuildConfig.DEBUG)
                    //    Log.w("getUpandDownsByHour", String.format("%1$d", count) + " Down");
                }

            }
            c.close();
            db.close();


        } catch (SQLiteException e) {

            //if (BuildConfig.DEBUG) Log.w("getUpandDownsByHour", "Exception");

        }


    }



    private void getPrevDate() {

        Date parsedDate = new Date();
        SimpleDateFormat dateFormatToParse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            //if (BuildConfig.DEBUG) Log.w("Chart1", "getPrevDate");

            SQLiteDatabase db = dBHelper.getWritableDatabase();

            //Construct the date part of the query
            SimpleDateFormat dateFormatOutput = new SimpleDateFormat("yyyy-MM-dd");


            //Find some test data
            //String dateString="2016-02-12";

            String dateString = dateFormatOutput.format(currentDate.getTime());

            String queryString = "SELECT date_Time FROM lockStats WHERE (date_Time < datetime('" + dateString + "','start of day')) ORDER BY date_Time DESC LIMIT 1;";

            c = db.rawQuery(queryString, null);

            String prevDateString = "";

            noMoreRecords = true;

            while (c.moveToNext()) {

                prevDateString = c.getString(0);
                noMoreRecords = false;

            }
            c.close();
            db.close();

            if (!noMoreRecords) {
                try {
                    parsedDate = dateFormatToParse.parse(prevDateString);
                } catch (Exception parseException) {
                }
                prevDate.setTime(parsedDate);
            }

        } catch (SQLiteException e) {

            //if (BuildConfig.DEBUG) Log.w("getPrevDate", "Exception");

        }

    }

    private void getNextDate() {

        Date parsedDate = new Date();
        SimpleDateFormat dateFormatToParse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            //if (BuildConfig.DEBUG) Log.w("Chart1", "getNextDate");

            SQLiteDatabase db = dBHelper.getWritableDatabase();

            //Construct the date part of the query
            SimpleDateFormat dateFormatOutput = new SimpleDateFormat("yyyy-MM-dd");


            //Find some test data
            //String dateString="2016-02-12";

            String dateString = dateFormatOutput.format(currentDate.getTime());

            String queryString = "SELECT date_Time FROM lockStats WHERE (date_Time >= datetime('" + dateString + "','start of day','+1 day')) ORDER BY date_Time ASC LIMIT 1;";

            c = db.rawQuery(queryString, null);

            String nextDateString = "";

            noMoreRecords = true;

            while (c.moveToNext()) {

                nextDateString = c.getString(0);
                noMoreRecords = false;

            }
            c.close();
            db.close();

            if (!noMoreRecords) {
                try {
                    parsedDate = dateFormatToParse.parse(nextDateString);
                } catch (Exception parseException) {
                }
                nextDate.setTime(parsedDate);
            }

        } catch (SQLiteException e) {

            //if (BuildConfig.DEBUG) Log.w("getPrevDate", "Exception");

        }

    }


    public void clickPrev(View view) {

        //Disable the button until we've sorted out the chart
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

        //Get the data and draw the chart
        fillChartData(prevDate);

        //Set the total number of boats in the legend
        getUpAndDownsByDay(prevDate);
        upLegendEntry.label = "Ups (" + String.format("%1$d",upCount) + ")";
        legendEntries.set(0,upLegendEntry);
        downLegendEntry.label = "Downs (" + String.format("%1$d",downCount) + ")";
        legendEntries.set(0,upLegendEntry);

        //Redraw the chart
        chart.notifyDataSetChanged();
        chart.invalidate();

        //set the title
        chartTitle.setText(dateFormatTitle.format(prevDate.getTime()));

        //next date becomes the former current date
        nextDate.setTime(currentDate.getTime());
        nextButton.setText(dateFormatButton.format(currentDate.getTime()));
        nextButton.setEnabled(true);
        nextButton.setVisibility(View.VISIBLE);

        //current date becomes the former previous date
        currentDate.setTime(prevDate.getTime());

        //get the next oldest date for the new previous date
        getPrevDate();
        if (!noMoreRecords) {
            prevButton.setText(dateFormatButton.format(prevDate.getTime()));
            prevButton.setEnabled(true);
            prevButton.setVisibility(View.VISIBLE);
        } else {
            prevButton.setText("");
            prevButton.setVisibility(View.INVISIBLE);
        }

    }

    public void clickNext(View view) {
        //Disable the button until we've sorted out the chart
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

        //Get the data
        fillChartData(nextDate);

        //Set the total number of boats in the legend
        getUpAndDownsByDay(nextDate);
        upLegendEntry.label = "Ups (" + String.format("%1$d",upCount) + ")";
        legendEntries.set(0,upLegendEntry);
        downLegendEntry.label = "Downs (" + String.format("%1$d",downCount) + ")";
        legendEntries.set(0,upLegendEntry);

        //Redraw the chart
        chart.notifyDataSetChanged();
        chart.invalidate();

        //set the title
        chartTitle.setText(dateFormatTitle.format(nextDate.getTime()));

        //previous date becomes the former current date
        prevDate.setTime(currentDate.getTime());
        prevButton.setText(dateFormatButton.format(currentDate.getTime()));
        prevButton.setEnabled(true);
        prevButton.setVisibility(View.VISIBLE);

        //set the Current date to the former Next date
        currentDate.setTime(nextDate.getTime());

        //get the next newest date for the new next date. If there are no later records but the current date is before today, set it to today
        getNextDate();
        if (!noMoreRecords) {
            nextButton.setText(dateFormatButton.format(nextDate.getTime()));
            nextButton.setEnabled(true);
            nextButton.setVisibility(View.VISIBLE);
        } else if (dateIsBefore(currentDate,todaysDate)) {
            nextDate.setTime(todaysDate.getTime());
            nextButton.setText(dateFormatButton.format(nextDate.getTime()));
            nextButton.setEnabled(true);
            nextButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setText("");
            nextButton.setVisibility(View.INVISIBLE);
        }

    }

    private boolean dateIsBefore(Calendar date1, Calendar date2) {

        String date1String = String.format("%1$04d", date1.get(Calendar.YEAR))
                + String.format("%1$02d", date1.get(Calendar.MONTH))
                + String.format("%1$02d", date1.get(Calendar.DAY_OF_MONTH));

        String date2String = String.format("%1$04d", date2.get(Calendar.YEAR))
                + String.format("%1$02d", date2.get(Calendar.MONTH))
                + String.format("%1$02d", date2.get(Calendar.DAY_OF_MONTH));

        if (date1String.compareTo(date2String) < 0) {
            return true;
        }
        else {
            return false;
        }
    }

}
