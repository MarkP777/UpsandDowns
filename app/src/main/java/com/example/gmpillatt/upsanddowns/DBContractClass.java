package com.example.gmpillatt.upsanddowns;

import android.provider.BaseColumns;

/**
 * Created by gmpillatt on 02/12/2016.
 */

public final class DBContractClass {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DBContractClass() {
    }

    /* Inner class that defines the table contents */
    public static class DBSchema implements BaseColumns {

        public static final String TABLE_NAME = "lockStats";
        public static final String COLUMN_NAME_FLIGHT = "flight";
        public static final String COLUMN_NAME_DATETIME = "date_Time";
        public static final String COLUMN_NAME_NUMBERBOATS = "numberBoats";
        public static final String COLUMN_NAME_UPDOWN = "upDown";
        public static final String COLUMN_NAME_WIDEBEAM = "wideBeam";

    }

}
