package com.example.patsobo.locatr;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

public class MapItFragment extends SupportMapFragment {
    private static final String TAG = "MapItFragment";

    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private WeatherItem mMapItem;
    private Location mCurrentLocation;

    // map for tracking checkpoints by marker id
    private HashMap<String, Checkpoint> mCheckpoints = new HashMap<String, Checkpoint>();


    // database of checkpoints
    private CheckpointJourney mJourney;

    public static MapItFragment newInstance() {
        return new MapItFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // open the database
        mJourney = new CheckpointJourney(getContext());

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        setHasOptionsMenu(true);
                        getActivity().invalidateOptionsMenu();
                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .build();

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                /**
                 * Called when marker gets clicked.  Shows title in header and weather data and date
                 * in snackbar below.
                 */
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {
                        //marker.showInfoWindow();

                        // TODO: query for checkpoint object in database to get date
                        LatLng p = marker.getPosition();
                        Checkpoint c = mCheckpoints.get(marker.getId());

                        // request weather data and display snackbar on completion
                        new WeatherTask().execute(c);

                        // false so that default behavior also occurs.
                        // i.e., the action buttons in the bottom right,
                        // centering the camera, title appears on click
                        return false;
                    }
                });

                // draw previous markers
                List<Checkpoint> checkpoints = mJourney.getCheckpoints();
                for (Checkpoint c : checkpoints) {
                    LatLng p = new LatLng(c.getLat(), c.getLong());
                    MarkerOptions myMarker = new MarkerOptions()
                            .position(p)
                            .title("lat/lon: (" + p.latitude + "," + p.longitude + ")");

                    Marker marker = mMap.addMarker(myMarker);
                    mCheckpoints.put(marker.getId(), c);
                }
            }
        });

        // Add listener to the floating button for checking in.
        FloatingActionButton locationFab = (FloatingActionButton) getActivity().findViewById(R.id.action_locate);
        locationFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addLocation();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
        getActivity().invalidateOptionsMenu();
    }
    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }


    /**
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_locatr, menu);

        MenuItem deleteItem = menu.findItem(R.id.clear_checkpoints);
        deleteItem.setEnabled(mClient.isConnected());
    }

    /**
     * Called when a menu option on the top of the screen is selected.
     * @param item The menu item that was selected
     * @return whether an action occurred from the touch
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_checkpoints:
                mJourney.deleteCheckpoints();
                mMap.clear();
                mMapItem = null;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Updates the mCurrentLocation variable for a new checkpoint to be created
     */
    private void addLocation() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i("MapItFragment", "Got a fix: " + location);
                        new SearchTask().execute(location);
                    }
                });
    }

    /**
     * Based on the mCurrentLocation, creates and adds a checkpoint to the database, and displays a
     * a new marker on the map corresponding to that checkpoint.
     */
    private void updateUI() {
        if (mMap == null || mMapItem == null) {
            return;
        }

        //LatLng itemPoint = new LatLng(mMapItem.getLat(), mMapItem.getLon());
        LatLng myPoint = new LatLng(
                mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        // add marker to the database
        Checkpoint c = new Checkpoint();
        c.setLat(myPoint.latitude);
        c.setLong(myPoint.longitude);
        mJourney.addCheckpoint(c);

        MarkerOptions myMarker = new MarkerOptions()
                .position(myPoint)
                .title("lat/lon: (" + myPoint.latitude + "," + myPoint.longitude + ")");

        Marker marker = mMap.addMarker(myMarker);
        mCheckpoints.put(marker.getId(), c);

        LatLngBounds bounds = new LatLngBounds.Builder()
                //.include(itemPoint)
                .include(myPoint)
                .build();
        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
        mMap.animateCamera(update);


    }

    /**
     * Displays the snackbar after the weather data has been queried
     * @param checkpoint The checkpoint whose information is being displayed.
     * @param weatherItem The weather item with the relevant weather data from the async task.
     */
    private void displaySnackbar(Checkpoint checkpoint, WeatherItem weatherItem) {
        String snackText = "Visited: " + checkpoint.getDate() + "\n";
        snackText += "Weather: " + weatherItem.getWeather() + " | ";
        snackText += "Temperature: " + weatherItem.getTemperature() + " K";
        Snackbar snackbar = Snackbar.make(getView(), snackText, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    /**
     * Asynchronous task that fetches the current weather from openweathermap.org
     */
    private class WeatherTask extends AsyncTask<Checkpoint,Void,WeatherItem> {

        Checkpoint checkpoint;

        @Override
        protected WeatherItem doInBackground(Checkpoint... params) {
            this.checkpoint = params[0];

            return new WeatherFetcher().fetchWeather(this.checkpoint);
        }

        @Override
        protected void onPostExecute(WeatherItem weatherItem) {
            displaySnackbar(this.checkpoint, weatherItem);
        }
    }

    /**
     * Asynchrounous task that searches and gets current location
     */
    private class SearchTask extends AsyncTask<Location,Void,Void> {
        private WeatherItem mWeatherItem;
        private Location mLocation;

        @Override
        protected Void doInBackground(Location... params) {
            mLocation = params[0];
            WeatherFetcher fetcher = new WeatherFetcher();
            LatLng pos = new LatLng(params[0].getLatitude(), params[0].getLongitude());
            mWeatherItem = fetcher.fetchWeather(pos);

            if (mWeatherItem == null) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mMapItem = mWeatherItem;
            mCurrentLocation = mLocation;

            updateUI();
        }
    }

}
