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
import java.util.List;

public class Chart2 extends AppCompatActivity {

    String TAG = "Chart2";
    DBHelperClass dBHelper = new DBHelperClass(this);
    Cursor c;
    int upCount, downCount;
    String[] xAxisValues = new String[26];
    BarChart chart;
    TextView chartTitle;
    Button prevButton;
    Button nextButton;
    List<BarEntry> totalEntries;
    final String[] daysOfWeekShort = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    final String[] daysOfWeekLong = {"Sundays", "Mondays", "Tuesdays", "Wednesdays", "Thursdays", "Fridays", "Saturdays"};
    final SimpleDateFormat dateFormatTitle = new SimpleDateFormat("EEE dd MMMM yy");
    final SimpleDateFormat dateFormatButton = new SimpleDateFormat("dd/MM");
    Integer upBarColor;
    Integer downBarColor;
    Calendar todaysDate;
    int todaysDay;
    int prevDay;
    int nextDay;

    // set custom bar width - just a a little bit of space between each up/down pair of bars
    //final float groupSpace = 0.06f;
    final float barWidth = 0.95f; // x2 dataset

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tell the parent activity that we're doing a Chart2. Always return OK
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_USERSELECTION, 6);
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
        todaysDay = todaysDate.get(Calendar.DAY_OF_WEEK);

        //Set the title
        chartTitle.setText(daysOfWeekLong[todaysDay - 1]);

        //Title the next button
        nextDay = (todaysDay + 1);
        if (nextDay > 7) {
            nextDay = 1;
        }
        nextButton.setText(daysOfWeekShort[nextDay - 1]);

        //Title the previous button
        prevDay = (todaysDay - 1);
        if (prevDay < 1) {
            prevDay = 7;
        }
        prevButton.setText(daysOfWeekShort[prevDay - 1]);

        //Set up stuff for the chart that won't change with the data

        downBarColor = getResources().getColor(R.color.colorDown);
        upBarColor = getResources().getColor(R.color.colorUp);

        chart.setTouchEnabled(false);
        chart.setFitBars(true);
        chart.setDescription(null);

        //Set up custom legend
        List <LegendEntry> legendEntries;
        legendEntries = new ArrayList<>();
        legendEntries.add(0,new LegendEntry("Downs",Legend.LegendForm.DEFAULT,Float.NaN,Float.NaN,null,downBarColor));
        legendEntries.add(1,new LegendEntry("Ups",Legend.LegendForm.DEFAULT,Float.NaN,Float.NaN,null,upBarColor));

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
        xAxis.setAxisMaximum(25.5f);
        //xAxis.setLabelCount(11);
        xAxis.setValueFormatter(new Chart2XAxisValueFormatter(xAxisValues));
        xAxis.setDrawGridLines(false);
        xAxis.setCenterAxisLabels(true);

        //Get initial chart data
        fillChartData(todaysDay);

        //Display the chart
        chart.invalidate(); // refresh

    }

    protected void fillChartData(int day) {

        Calendar workingDate = Calendar.getInstance();

        totalEntries.clear();

        // Work backwards from today until we find the first day where the day of the week is the one we want
        workingDate.setTime(todaysDate.getTime());
        while (workingDate.get(Calendar.DAY_OF_WEEK) != day) {
            workingDate.add(Calendar.DATE, -1);
        }

        // Get 26 weeks of data working backwards
        for (int i = 25; i >= 0; i--)

        {

            getTotalsByDay(workingDate);

            totalEntries.add(new BarEntry(i, new float [] {downCount, upCount}));

            //Set up the X Axis label
            xAxisValues[i] = String.format("%1$02d", workingDate.get(Calendar.DAY_OF_MONTH))
                    + "/"
                    + String.format("%1$02d", (workingDate.get(Calendar.MONTH) + 1));

            workingDate.add(Calendar.DATE, -7);

        }


        //TODO review whether this new is correct
        BarDataSet totalSet = new BarDataSet(totalEntries, "Totals"); // add entries to dataset


        totalSet.setColors(new int [] {downBarColor,upBarColor});

        totalSet.setDrawValues(false);

        BarData data = new BarData(totalSet);
        data.setBarWidth(barWidth); // set the width of each bar
        chart.setData(data);

    }


    protected void getTotalsByDay(Calendar date) {

        int count;

        try {

            //if (BuildConfig.DEBUG) Log.w(TAG, "getTotalsByDay started");

            SQLiteDatabase db = dBHelper.getWritableDatabase();

            //Construct the date part of the query
            SimpleDateFormat dateFormatOutput = new SimpleDateFormat("yyyy-MM-dd");

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


            String queryString = "SELECT upDown, SUM(numberBoats) FROM lockStats WHERE (date_Time >="
                    + lowerBound
                    + ") AND (date_Time <"
                    + upperBound
                    + ") GROUP BY upDown;";


            c = db.rawQuery(queryString, null);


            //Set count. We won't get anything back from the query if there are no records
            upCount = 0;
            downCount = 0;

            while (c.moveToNext()) {

                String upDown = c.getString(0);
                count = c.getInt(1);

                if (upDown.equals("U")) {
                    upCount = count;
                } else if (upDown.equals("D")) {
                    downCount = count;
                }

            }

            c.close();
            db.close();


        } catch (SQLiteException e) {

            //if (BuildConfig.DEBUG) Log.w(TAG, " getTotalsByDay Exception");

        }


    }

    public void clickPrev(View view) {

        //Disable the button until we've sorted out the chart
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

        //Get the data and draw the chart
        fillChartData(prevDay);
        chart.notifyDataSetChanged();
        chart.invalidate();

        //set the title
        chartTitle.setText(daysOfWeekLong[prevDay - 1]);

        //Change the date on this button and the Next. Cycle through the weekdays
        prevDay = prevDay - 1;
        if (prevDay < 1) {
            prevDay = 7;
        }
        prevButton.setText(daysOfWeekShort[prevDay - 1]);

        nextDay = nextDay - 1;
        if (nextDay < 1) {
            nextDay = 7;
        }
        nextButton.setText(daysOfWeekShort[nextDay - 1]);

        //Enable Next and Prev buttons
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);
    }

    public void clickNext(View view) {

        //Disable the button until we've sorted out the chart
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

        //Get the data and draw the chart
        fillChartData(nextDay);
        chart.notifyDataSetChanged();
        chart.invalidate();

        //set the title
        chartTitle.setText(daysOfWeekLong[nextDay - 1]);

        //Change the date on this button and the Previous. Cycle through the weekdays
        nextDay = nextDay + 1;
        if (nextDay > 7) {
            nextDay = 1;
        }
        nextButton.setText(daysOfWeekShort[nextDay - 1]);

        prevDay = prevDay + 1;
        if (prevDay > 7) {
            prevDay = 1;
        }
        prevButton.setText(daysOfWeekShort[prevDay - 1]);

        //Enable Next and Prev buttons
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);

    }
}
