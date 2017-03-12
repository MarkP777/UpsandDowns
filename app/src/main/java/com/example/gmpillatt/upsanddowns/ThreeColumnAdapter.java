package com.example.gmpillatt.upsanddowns;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gmpillatt on 14/01/2017.
 */

public class ThreeColumnAdapter extends ArrayAdapter<ItemData> {

    /*
    Custom adapter cribbed from http://stackoverflow.com/questions/8166497/custom-adapter-for-list-view
    Siddarth Kanted's answer
     */

    private int layoutResource;

    public ThreeColumnAdapter(Context context, int layoutResource, List<ItemData> threeStringsList) {
        super(context, layoutResource, threeStringsList);
        this.layoutResource = layoutResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }

        ItemData itemData = getItem(position);

        if (itemData != null) {
            TextView col1TextView = (TextView) view.findViewById(R.id.col1TextView);
            TextView col2TextView = (TextView) view.findViewById(R.id.col2TextView);
            TextView col3TextView = (TextView) view.findViewById(R.id.col3TextView);

            if (col1TextView != null) {
                col1TextView.setText(itemData.getLeft());
            }


            if (col2TextView != null) {
                col2TextView.setTextColor(itemData.getTextColor());
                col2TextView.setText(itemData.getCentre());
            }

            if (col3TextView != null) {
                col3TextView.setText(itemData.getRight());
            }
        }

        return view;

    }


}
