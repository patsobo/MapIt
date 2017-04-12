package com.example.patsobo.locatr;

/**
 * Created by patsobo on 3/11/2017.
 */

public class WeatherItem {
    private String mId;
    private double mLat;
    private double mLon;
    private String mWeather;
    private double mTemperature;


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public double getLat() {
        return mLat;
    }
    public void setLat(double lat) {
        mLat = lat;
    }
    public double getLon() {
        return mLon;
    }
    public void setLon(double lon) {
        mLon = lon;
    }

    public String getWeather() {
        return mWeather;
    }
    public void setWeather(String weather) {
        mWeather = weather;
    }
    public double getTemperature() {
        return mTemperature;
    }
    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    @Override
    public String toString() {
        return mWeather;
    }
}
