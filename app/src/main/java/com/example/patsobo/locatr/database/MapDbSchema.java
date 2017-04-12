package com.example.patsobo.locatr.database;

/**
 * Created by patsobo on 4/9/2017.
 */

public class MapDbSchema {
    /**
     * A schema for the database we'll be creating and managing.
     */
    public static final class MapTable {
        public static final String NAME = "checkpoints";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String DATE = "date";
            public static final String LAT = "latitude";
            public static final String LONG = "longitude";
        }
    }
}
