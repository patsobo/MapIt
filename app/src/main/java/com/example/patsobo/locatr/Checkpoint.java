package com.example.patsobo.locatr;

import java.util.Date;
import java.util.UUID;

/**
 * Created by patsobo on 4/9/2017.
 */

public class Checkpoint {

    private UUID mId;
    private Date mDate;
    private double mLat;
    private double mLong;

    public Checkpoint() {
        this(UUID.randomUUID());
    }

    public Checkpoint(UUID id) {
        mId = id;
        mDate = new Date();
    }
    public UUID getId() {
        return mId;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLong() {
        return mLong;
    }

    public void setLong(double longitude) {
        mLong = longitude;
    }
}
