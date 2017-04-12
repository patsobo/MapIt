package com.example.patsobo.locatr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.patsobo.locatr.database.MapBaseHelper;
import com.example.patsobo.locatr.database.MapCursorWrapper;
import com.example.patsobo.locatr.database.MapDbSchema;
import com.example.patsobo.locatr.database.MapDbSchema.MapTable;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.maps.model.LatLng;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by patsobo on 4/10/2017.
 */

public class CheckpointJourney {
    private static CheckpointJourney sCheckpointJourney;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CheckpointJourney get(Context context) {
        if (sCheckpointJourney == null) {
            sCheckpointJourney = new CheckpointJourney(context);
        }
        return sCheckpointJourney;
    }

    /**
     * Constructor for this class.
     * @param context the context of the app wanting this.
     */
    public CheckpointJourney(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new MapBaseHelper(mContext)
                .getWritableDatabase();
    }

    /**
     * Adds a Checkpoint to the database
     * @param c the Checkpoint to add to the database.
     */
    public void addCheckpoint(Checkpoint c) {
        ContentValues values = getContentValues(c);

        mDatabase.insert(MapTable.NAME, null, values);
    }

    /**
     * Gets a list of all the checkpoints.
     * @return a list of all the Checkpoing objects in the database.
     */
    public List<Checkpoint> getCheckpoints() {
        List<Checkpoint> checkpoints = new ArrayList<>();

        MapCursorWrapper cursor = queryCheckpoints(null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            checkpoints.add(cursor.getCheckpoint());
            cursor.moveToNext();
        }
        cursor.close();

        return checkpoints;
    }

    /**
     * Given a position, gets the Checkpoint corresponding to that position.
     * @param pos The position requested, acting as an ID.
     * @return
     */
    public Checkpoint getCheckpoint(LatLng pos) {
        MapCursorWrapper cursor = queryCheckpoints(
                MapTable.Cols.LAT + " = ? AND " + MapTable.Cols.LONG + " = ?",
                new String[]{ String.valueOf(pos.latitude), String.valueOf(pos.longitude) }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCheckpoint();
        } finally {
            cursor.close();
        }
    }

    /**
     * Clears the database.
     */
    public void deleteCheckpoints() {
        mDatabase.delete(MapTable.NAME, null, null);
    }

    private static ContentValues getContentValues(Checkpoint checkpoint) {
        ContentValues values = new ContentValues();
        values.put(MapTable.Cols.UUID, checkpoint.getId().toString());
        values.put(MapTable.Cols.DATE, checkpoint.getDate().getTime());
        values.put(MapTable.Cols.LAT, checkpoint.getLat());
        values.put(MapTable.Cols.LONG, checkpoint.getLong());

        return values;
    }

    /**
     * Executes query on database.
     * @param whereClause A list of conditions to query against.
     * @param whereArgs The arguments (replacing the '?'s in the whereClause) to query against.
     * @return a custom cursor wrapper that will return the requested Checkpoint.
     */
    private MapCursorWrapper queryCheckpoints(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                MapTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new MapCursorWrapper(cursor);
    }
}