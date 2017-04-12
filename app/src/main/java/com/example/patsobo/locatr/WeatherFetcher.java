package com.example.patsobo.locatr;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by patsobo on 4/10/2017.
 */

public class WeatherFetcher {
    private static final String TAG = "WeatherFetcher";

    private static final String API_KEY = "1cdc48c4c575c9420c9ef886e2384267";
    private static final String CURRENT_WEATHER_METHOD = "weather";
    private static final Uri ENDPOINT = Uri
            .parse("http://api.openweathermap.org/data/2.5/" + CURRENT_WEATHER_METHOD)
            .buildUpon()
            .appendQueryParameter("appid", API_KEY)
            .build();

    /**
     * Gets the raw bytes from a REST API request.
     * @param urlSpec The URL to make a request to.
     * @return The raw data from the request
     * @throws IOException Thrown if something happens to the connection
     */
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Wraps the raw data into a more manageable string.
     * @param urlSpec The url to make a request to.
     * @return A string representing the raw data json returned from the request.
     * @throws IOException Thrown if something happens to the connection while downloading.
     */
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    /**
     * Gets the weather at the specified Checkpoint.
     * It is overloaded to also accept a simple LatLng.
     * @param c The Checkpoint to check the weather for.
     * @return A WeatherIterm representing the weather at the given Checkpoint.
     */
    public WeatherItem fetchWeather(Checkpoint c) {
        LatLng pos = new LatLng(c.getLat(), c.getLong());
        String url = buildUrl(pos);
        return downloadWeatherItem(url);
    }
    public WeatherItem fetchWeather(LatLng pos) {
        String url = buildUrl(pos);
        return downloadWeatherItem(url);
    }

    /**
     * Downloads the weather item, given a built URL.
     * @param url A URL containing the location information toward the api request.
     * @return A WeatherItem representing the weather observed from the request made at the url.
     */
    private WeatherItem downloadWeatherItem(String url) {
        //List<WeatherItem> items = new ArrayList<>();
        WeatherItem item = new WeatherItem();

        try {
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(item, jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return item;
    }

    /**
     * Creates the url from the given position.
     * @param pos The position coordinates to add to the URL.
     * @return The longer URL with the position information.
     */
    private String buildUrl(LatLng pos) {
        return ENDPOINT.buildUpon()
                .appendQueryParameter("lat", "" + pos.latitude)
                .appendQueryParameter("lon", "" + pos.longitude)
                .build().toString();
    }

    /**
     * Parses the JSON returned from the REST request and turns it into a WeatherItem object.
     * @param item The WeatherItem object to fill in with data.
     * @param jsonBody The JSON returned from some REST request.
     * @throws IOException Thrown if the connection drops while downloading the JSON.
     * @throws JSONException Thrown if the JSON is incorrectly formatted.
     */
    private void parseItems(WeatherItem item, JSONObject jsonBody)
            throws IOException, JSONException {

        // don't know why the weather object is an array, but w/e
        JSONArray weatherJsonObject = jsonBody.getJSONArray("weather");
        JSONObject mainJsonObject = jsonBody.getJSONObject("main");
        JSONObject coordJsonObject = jsonBody.getJSONObject("coord");

        item.setWeather(weatherJsonObject.getJSONObject(0).getString("description"));
        item.setTemperature(mainJsonObject.getDouble("temp"));
        item.setLat(coordJsonObject.getDouble("lat"));
        item.setLon(coordJsonObject.getDouble("lon"));
    }

}
