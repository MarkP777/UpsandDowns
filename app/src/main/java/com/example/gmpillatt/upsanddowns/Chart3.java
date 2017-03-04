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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Chart3 extends AppCompatActivity {

    String TAG="Chart3";
    DBHelperClass dBHelper = new DBHelperClass(this);
    Cursor c;
    int totalCount;
    String[] xAxisValues = new String[31];
    BarChart chart;
    TextView chartTitle;
    Button prevButton;
    Button nextButton;
    List<BarEntry> totalEntries;
    final SimpleDateFormat dateFormatTitle = new SimpleDateFormat("dd MMMM yyyy");
    Integer totalBarColor;
    Calendar todaysDate;
    Calendar chartDate;

    // set custom bar width - just a a little bit of space between each up/down pair of bars
    //final float groupSpace = 0.06f;
    final float barWidth = 0.95f; // x2 dataset

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tell the parent activity that we're doing a Chart3. Always return OK
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_USERSELECTION,7);
        setResult(RESULT_OK,intent);

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
        chartDate = Calendar.getInstance();
        chartDate.setTime(todaysDate.getTime());

        //Set the title
        chartTitle.setText("Month to \n" + dateFormatTitle.format(chartDate.getTime()));

        //Title the next button and disable and hide it because we can't go beyond today
        nextButton.setText("Next");
        nextButton.setEnabled(false);
        nextButton.setVisibility(View.INVISIBLE);

        //Title the previous button
        prevButton.setText("Prev");

        //Set up stuff for the chart that won't change with the data

        totalBarColor=getResources().getColor(R.color.colorTotal);

        chart.setTouchEnabled(false);
        chart.setFitBars(true);
        chart.setDescription(null);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);


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
        xAxis.setAxisMaximum(30.5f);
        xAxis.setValueFormatter(new Chart3XAxisValueFormatter(xAxisValues));
        xAxis.setDrawGridLines(false);
        xAxis.setCenterAxisLabels(true);

        //Get initial chart data
        fillChartData(chartDate);

        //Display the chart
        chart.invalidate(); // refresh

    }

    protected void fillChartData(Calendar day) {

        Calendar workingDate = Calendar.getInstance();
        workingDate.setTime(day.getTime());

        totalEntries.clear();


        // Get 31 days of data working backwards
        for (int i = 30; i >= 0; i--)

        {

            getTotalsByDay(workingDate);

            totalEntries.add(new BarEntry(i, totalCount));

            //Set up the X Axis label
            xAxisValues[i] = String.format("%1$02d", workingDate.get(Calendar.DAY_OF_MONTH))
                    + "/"
                    + String.format("%1$02d", (workingDate.get(Calendar.MONTH)+1));

            workingDate.add(Calendar.DATE,-1);

        }


        //TODO review whether this new is correct
        BarDataSet totalSet = new BarDataSet(totalEntries, "Totals"); // add entries to dataset


        totalSet.setColor(totalBarColor);

        totalSet.setDrawValues(false);

        BarData data = new BarData(totalSet);
        data.setBarWidth(barWidth); // set the width of each bar
        chart.setData(data);

    }


    protected void getTotalsByDay(Calendar date) {

        int count;

        try {

            if (BuildConfig.DEBUG) Log.w(TAG, "getTotalsByDay started");

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


            String queryString = "SELECT SUM(numberBoats) FROM lockStats WHERE (date_Time >="
                    + lowerBound
                    + ") AND (date_Time <"
                    + upperBound
                    + ");";


            c = db.rawQuery(queryString, null);


            //Set count. We won't get anything back from the query if there are no record
            totalCount = 0;

            while (c.moveToNext()) {

                totalCount = c.getInt(0);

            }
            c.close();


        } catch (SQLiteException e) {

            if (BuildConfig.DEBUG) Log.w(TAG, " getTotalsByDay Exception");

        }


    }

    public void clickPrev(View view)
    {

        //Disable the button until we've sorted out the chart
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

        //Get the data and draw the chart
        chartDate.add(Calendar.DATE,-31);
        fillChartData(chartDate);
        chart.notifyDataSetChanged();
        chart.invalidate();

        //set the title
        chartTitle.setText("Month to \n" + dateFormatTitle.format(chartDate.getTime()));

        //Enable Next and Prev buttons. Show next button
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);
        nextButton.setVisibility(View.VISIBLE);
    }

    public void clickNext(View view) {

        //Disable the button until we've sorted out the chart
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

        //Get the data and draw the chart
        chartDate.add(Calendar.DATE,+31);
        fillChartData(chartDate);
        chart.notifyDataSetChanged();
        chart.invalidate();

        //set the title
        chartTitle.setText("Month to \n" + dateFormatTitle.format(chartDate.getTime()));

        //Enable Prev button and Next provided that we're not already at today's date
        prevButton.setEnabled(true);
        if (!chartDate.equals(todaysDate))
        { nextButton.setEnabled(true);}
        else
        { nextButton.setVisibility(View.INVISIBLE); }
    }
}
