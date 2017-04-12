package com.example.patsobo.locatr.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.patsobo.locatr.database.MapDbSchema.MapTable;

/**
 * Created by patsobo on 4/9/2017.
 */

public class MapBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "MapBaseHelper";
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "mapBase.db";

    public MapBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * Creates the main database table.
     * @param db the empty database to create a table for.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + MapTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                MapTable.Cols.UUID + ", " +
                MapTable.Cols.DATE + ", " +
                MapTable.Cols.LAT + ", " +
                MapTable.Cols.LONG +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
