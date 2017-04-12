package com.example.patsobo.locatr.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.patsobo.locatr.Checkpoint;

import java.util.Date;
import java.util.UUID;

import com.example.patsobo.locatr.database.MapDbSchema.MapTable;

/**
 * Created by patsobo on 4/9/2017.
 */

public class MapCursorWrapper extends CursorWrapper {
    public MapCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Gets the Checkpoint located at the cursor this class is wrapping.
     * @return the Checkpoint corresponding to the location of the cursor.
     */
    public Checkpoint getCheckpoint() {
        String uuidString = getString(getColumnIndex(MapTable.Cols.UUID));
        long date = getLong(getColumnIndex(MapTable.Cols.DATE));
        double lat = getDouble(getColumnIndex(MapTable.Cols.LAT));
        double longitude = getDouble(getColumnIndex(MapTable.Cols.LONG));

        Checkpoint checkpoint = new Checkpoint(UUID.fromString(uuidString));
        checkpoint.setDate(new Date(date));
        checkpoint.setLat(lat);
        checkpoint.setLong(longitude);

        return checkpoint;
    }
}
