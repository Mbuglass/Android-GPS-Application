package com.example.michael.firstapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends AppCompatActivity implements OnMapLongClickListener,
        OnMarkerClickListener, OnMapReadyCallback {

    //widgets
    private EditText mSearch;

    //vars
    private static final int REQUEST_LOCATION_PERMISSION = 123;
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    /**
     * On create method initialises views, progress bar, FirebaseAuth and sets focus on input field
     *
     * @param savedInstanceState allows activity to restore to a previous state using data stored
     *                           in this Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Assign the input field to variable
        mSearch = (EditText) findViewById(R.id.input_search);

        init();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // Read autocomplete suggestions from text file and add to autocomplete suggestions
        // in search text box
        getAutocomplete();
    }

    /**
     * Creates option menu in top bar using values from xml file
     *
     * @param menu contains menu options
     * @return boolean indicating successful addition of menu to toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    /**
     * onClick for options menu items
     * Will find origin of onclick to determine which action must be taken
     * Will take users to Account, Settings, and Filter screens or change map view
     *
     * @param item contains the source of the onClick call
     * @return true to indicate action has been completed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            // Go to Filter screen
            case R.id.menu_filter:
                Log.d(TAG, "onOptionsItemSelected:menu_filter");
                Intent intentFilter = new Intent(this, FilterActivity.class);
                startActivity(intentFilter);
                return true;
            // Go to Filter screen
            case R.id.menu_settings:
                Log.d(TAG, "onOptionsItemSelected:menu_filter");
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                return true;
            // Go to Account screen
            case R.id.menu_account:
                Log.d(TAG, "onOptionsItemSelected:menu_account");
                Intent intentAccount = new Intent(this, AccountActivity.class);
                startActivity(intentAccount);
                return true;
            // Change map to normal view
            case R.id.menu_normal_map:
                Log.d(TAG, "onOptionsItemSelected:menu_normal_map");
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            // Change map to hybrid view
            case R.id.menu_hybrid_map:
                Log.d(TAG, "onOptionsItemSelected:menu_hybrid_map");
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            // Change map to satellite view
            case R.id.menu_satellite_map:
                Log.d(TAG, "onOptionsItemSelected:menu_satellite_map");
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            // Change map to terrain view
            case R.id.menu_terrain_map:
                Log.d(TAG, "onOptionsItemSelected:menu_terrain_map");
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *
     * @param googleMap contains the instance of the map, allowing for manipulation of the map and
     *                  its settings
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setPadding(0, 200, 0, 0);

        // Adds zoom buttons to map
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Set on long click listener, for adding new marker
        mMap.setOnMapLongClickListener(this);

        // Set on marker click listener - used to look at marker info and to add info
        // when adding a new marker
        mMap.setOnMarkerClickListener(this);

        // Get locations from cloud database and add to map
        getLocations();

        // Enable location services, so that the device position can be located
        enableMyLocation();

        // Check if dark mode is enabled in settings, and change map to dark colour
        // scheme if so
        // Check if labels are enabled or disabled and change map accordingly
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        boolean mapDarkMode = sharedPref.getBoolean
                (SettingsActivity.KEY_SETTING_DARK_MODE, false);
        boolean mapLabels = sharedPref.getBoolean
                (SettingsActivity.KEY_SETTINGS_LABELS, false);
        // Darkmode with labels
        if (mapDarkMode&&mapLabels) {
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.darkmode_json));
            // Darkmode without labels
        } else if (mapDarkMode&&!mapLabels){
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.darkmodenolabels_json));
            // Normal no labels
        } else if (!mapDarkMode&&!mapLabels){
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.normalnolabels_json));
            // Normal mode
        } else {
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.normal_json));
        }
        // Inform user on how to add marker to map
        Toast.makeText(this, "Press and hold to add new marker",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Check if permission for location services has been granted,
     * and if so enable the map to focus on user location and display
     * their location of the map
     *
     * @param requestCode  requestCode
     * @param permissions  permissions
     * @param grantResults grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
        }
    }

    /**
     * Request location permissions from user
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            // Change map camera to current location
            getCurrentLocation();

        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    /**
     * Get lat and lng of user location and move camera to this location
     */
    private void getCurrentLocation() {
        LocationManager locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        // Suppress warning as method will only be called when permission is granted
        @SuppressLint("MissingPermission")
        android.location.Location lastKnownLocation =
                locationManager.getLastKnownLocation(locationProvider);
        double userLat = lastKnownLocation.getLatitude();
        double userLong = lastKnownLocation.getLongitude();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom
                (new LatLng(userLat, (userLong)), 15));
    }

    /**
     * Initialise search bar view, and setup onEditorAction to allow for search results
     * to be located on the map
     */
    private void init() {
        Log.d(TAG, "init:initializing");
        mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_SEARCH)
                        || (actionId == EditorInfo.IME_ACTION_DONE)
                        || (event.getAction() == KeyEvent.ACTION_DOWN)
                        || (event.getAction() == KeyEvent.KEYCODE_ENTER)) {
                    // Move camera to location of search result
                    geoLocate();
                }
                return false;
            }
        });
    }

    /**
     * Get input from search bar abd move camera to location of search result where possible
     * If the location can not be found, or finds conflicts, the camera may remain stationary
     */
    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");
        String searchString = mSearch.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);

        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location:" + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom
                    (new LatLng(address.getLatitude(), (address.getLongitude())), 12));
        }
    }

    /**
     * Get autocomplete suggestions for search input from text document resource
     * and add values to search bar auto complete suggestions
     *
     * Data used in the autocomplete is licensed under the Creative Common Attribution License
     * and is original data courtesy of geonames (http://www.geonames.org/)
     */
    private void getAutocomplete() {
        // List to store autocomplete suggestions
        List<String> places = new ArrayList<String>();

        // Populates List with ~23000 major cities form the file cities.txt
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getAssets().open("cities.txt")))) {

            // Loop through all lines until end of file
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                // Add line (location) to list
                places.add(mLine);
            }
        } catch (IOException e) {
            //log the exception
        }
        //log the exception
        // Create autocomplete adapter out of places List
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, places);
        // Add adapter to text view to allow autocomplete
        AutoCompleteTextView textView = (AutoCompleteTextView) mSearch;
        textView.setAdapter(adapter);
    }

    /**
     * Gets locations from database and adds to map
     * Will check category of marker and cross reference with filter preferences to determine if
     * marker should be added to the map or not. Marker colour also changes with category.
     */
    private void getLocations() {
        // Clear all existing markers from app
        mMap.clear();

        // Get filter preferences
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        final boolean filterPrefMemorial = sharedPref.getBoolean
                (FilterActivity.KEY_FILTER_PREF_MEMORIAl, false);
        final boolean filterPrefStatue = sharedPref.getBoolean
                (FilterActivity.KEY_FILTER_PREF_STATUE, false);
        final boolean filterPrefBuilding = sharedPref.getBoolean
                (FilterActivity.KEY_FILTER_PREF_BUILDING, false);
        final boolean filterPrefPark = sharedPref.getBoolean
                (FilterActivity.KEY_FILTER_PREF_PARK, false);
        final boolean filterPrefPlayArea = sharedPref.getBoolean
                (FilterActivity.KEY_FILTER_PREF_PLAY_AREA, false);
        final boolean filterPrefStreetArt = sharedPref.getBoolean
                (FilterActivity.KEY_FILTER_PREF_STREET_ART, false);
        final boolean filterPrefToilet = sharedPref.getBoolean
                (FilterActivity.KEY_FILTER_PREF_TOILET, false);
        final boolean filterPrefBin = sharedPref.getBoolean
                (FilterActivity.KEY_FILTER_PREF_BIN, false);
        final boolean filterPrefMisc = sharedPref.getBoolean
                (FilterActivity.KEY_FILTER_PREF_MISC, false);

        // Get marker details from collection
        mDb.collection("Location")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // For each document in the collection
                            for (QueryDocumentSnapshot document :
                                    Objects.requireNonNull(task.getResult())) {
                                // Declare variables and fill with data from Firestore document
                                GeoPoint geo = document.getGeoPoint("coordinates");
                                String id = document.getId();
                                String name = document.getString("locationName");
                                String cat = document.getString("category");
                                assert geo != null;
                                // Get lat and lng from GeoPoint to make new LatLng
                                double lat = geo.getLatitude();
                                double lng = geo.getLongitude();
                                LatLng latLng = new LatLng(lat, lng);
                                // Marker used to decide whether to add marker to map
                                boolean addMarker = true;
                                // Float to store colour of maker to be placed - Default is red
                                // Must be float as defaultMarker method expects float
                                float color = 0;
                                // Switch case to change marker colour depending on which category
                                // the marker belongs to and decide whether to add to map
                                // by comparing to filter preferences
                                switch (cat) {
                                    case "Memorial":
                                        color = 0;
                                        if (!filterPrefMemorial) {
                                            addMarker = false;
                                        }
                                        break;
                                    case "Statue":
                                        color = 30;
                                        if (!filterPrefStatue) {
                                            addMarker = false;
                                        }
                                        break;
                                    case "Building":
                                        color = 150;
                                        if (!filterPrefBuilding) {
                                            addMarker = false;
                                        }
                                        break;
                                    case "Park":
                                        color = 90;
                                        if (!filterPrefPark) {
                                            addMarker = false;
                                        }
                                        break;
                                    case "Play Area":
                                        color = 120;
                                        if (!filterPrefPlayArea) {
                                            addMarker = false;
                                        }
                                        break;
                                    case "Street Art":
                                        color = 60;
                                        if (!filterPrefStreetArt) {
                                            addMarker = false;
                                        }
                                        break;
                                    case "Toilet":
                                        color = 190;
                                        if (!filterPrefToilet) {
                                            addMarker = false;
                                        }
                                        break;
                                    case "Bin":
                                        color = 220;
                                        if (!filterPrefBin) {
                                            addMarker = false;
                                        }
                                        break;
                                    case "Misc":
                                        color = 210;
                                        if (!filterPrefMisc) {
                                            addMarker = false;
                                        }
                                        break;
                                }
                                // Checks if marker should be added to map, if so adds marker
                                if (addMarker) {
                                    // Adds marker to map
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title(name)
                                            .icon(BitmapDescriptorFactory
                                                    .defaultMarker(color)));
                                    marker.setTag(id);
                                }
                            } // END FOR LOOP
                        }
                    }
                });
    }

    /**
     * Detects long click of map, and adds a marker at selected location
     *
     * @param point contains coordinates of location pressed by the user
     */
    @Override
    public void onMapLongClick(LatLng point) {
        // Used to clear the map, so the user cannot place more than one "New Marker" marker
        getLocations();
        // Places new marker on the map
        LatLng latLng = new LatLng(point.latitude, point.longitude);
        Marker newMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                .title("Click to Add")
                .draggable(true)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        // Sets marker tag - used for identifying if the marker was placed by the user
        // when pressed (see OnMarkerClick())
        newMarker.setTag("NEW");

        Toast.makeText(this, "Click Marker to Share", Toast.LENGTH_SHORT).show();
    }

    /**
     * Detects click of a marker
     *
     * @param marker instance of marker that was clicked by user
     * @return boolean indicating the event has been consumed to prevent default behaviour occurring
     * (camera move such that the marker is centere and for the marker's info window to open,
     * if it has one)
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        // If marker was placed by user, take them to a window allowing them to add
        // the marker to the database
        if (Objects.equals(marker.getTag(), "NEW")) {
            // Get position of the pressed marker
            LatLng latLng = marker.getPosition();
            // Create new intent, to open the new marker activity
            Intent intent = new Intent(this, NewMarkerActivity.class);
            // Pass the marker position to the new window via the intent
            // Used for adding the new marker coordinates to the database
            intent.putExtra("MARKER_LAT", latLng.latitude);
            intent.putExtra("MARKER_LNG", latLng.longitude);
            // Start the new marker activity
            startActivity(intent);
            getLocations();
        }
        // If the marker came from the database, take the user to an info window,
        // which shows information about the marker
        else {
            Intent intent = new Intent(this, ViewMarkerActivity.class);
            // Pass the marker ID (document ID in Firestore) to the new window
            // used to query database in new window
            String markerID = (String) marker.getTag();
            intent.putExtra("MARKER_ID", markerID);
            startActivity(intent);
            getLocations();
        }
        // Returns true to indicate that we have consumed the event and that we do not wish
        // for the default behavior to occur (camera move such that the marker is centered
        // and for the marker's info window to open, if it has one).
        return true;
    }
}