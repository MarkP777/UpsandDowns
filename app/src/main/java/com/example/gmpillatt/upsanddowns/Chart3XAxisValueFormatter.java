package com.example.gmpillatt.upsanddowns;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by gmpillatt on 12/02/2017.
 */

public class Chart3XAxisValueFormatter implements IAxisValueFormatter {

    private String[] mValues;

    public Chart3XAxisValueFormatter(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        if (BuildConfig.DEBUG)
            Log.w("formatter", String.format("%1$.1f", value));

        //MP Hack. Added the next two lines to stop trying to access array out of bounds
        if (value < 0) {
            value = 0;
        }
        if (value > 30) {
            value = 30;
        }

        return mValues[(int) value];
    }

    /** this is only needed if numbers are returned, else return 0 */

 /*
    MP Commented out because the compiler complains

    @Override
    public int getDecimalDigits() { return 0; }
*/

}
