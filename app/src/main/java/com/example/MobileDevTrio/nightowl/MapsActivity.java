package com.example.MobileDevTrio.nightowl;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.List;
import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final String TAG = MapsActivity.class.getSimpleName();

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PROXIMITY_RADIUS = 1600 * 4;  // 4 mile radius
    private static final float DEFAULT_ZOOM = 11.5f;

    // Entry points for Google Places API
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;

    // Google API object used to obtain device location using GPS, Wi-Fi, and network
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // Default location (KSU Marietta campus) and default zoom to use when location permission
    // is not granted
    // TODO: Allow/Prompt user to set the default location
    private LatLng mDefaultLocation = new LatLng(33.9397, -84.5197);
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. Which is equivalent to the
    // last-known location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    protected FrameLayout mapLayout;
    private LinearLayout tabNearYouLayout, tabFavoritesLayout, tabTopLayout;
    private View tabNearYouSec, tabFavoritesSec, tabTopSec;

    // Venue listItem
    //private List<Venue> venueList;

    // BottomSheet Behavior
    BottomSheetBehavior bottomSheetBehavior1, bottomSheetBehavior2;

    // Filter Image Buttons
    boolean filterBtnIsPressed;
    protected ImageView filterImageBtn, filterRestaurantsBtn, filterClubBtn, filterBarBtn;

    // SelectedPlaceBottomSheet Views
    protected ImageView goBackBtn;
    protected TextView  spNameTV, spRatingTV, spTypeTV, spDescriptionTV, spAddressTV, spOpenClosedTV,
                        spPhoneNumTV, spURLTV;

    protected boolean appWasPaused;

    // TODO: Determine where to initialize this nearbyPlaces
    private List<Place> placeList;
    private GetNearbyPlaces getNearbyPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // initializes all variables
        initialize();

        // TODO: Revise logic to filter places, https://stackoverflow.com/questions/33006206/how-to-hide-show-groups-of-markers-by-category-with-google-maps-in-android?noredirect=1&lq=1
        filterRestaurantsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNearbyPlaces = new GetNearbyPlaces(MapsActivity.this);

                mMap.clear();
                Object[] urlParams = new Object[8];
                urlParams[0] = Double.toString(mLastKnownLocation.getLatitude());
                urlParams[1] = Double.toString(mLastKnownLocation.getLongitude());
                urlParams[2] = "restaurant";
                urlParams[3] = PROXIMITY_RADIUS;
                urlParams[4] = getResources().getString(R.string.google_places_web_service_key);
                urlParams[5] = null;
                urlParams[6] = null;
                urlParams[7] = mMap;

                getNearbyPlaces.execute(urlParams);
            }
        });
        
        //Bar filter button functionality
        filterBarBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getNearbyPlaces = new GetNearbyPlaces(MapsActivity.this);

                mMap.clear();
                Object[] urlParams = new Object[8];
                urlParams[0] = Double.toString(mLastKnownLocation.getLatitude());
                urlParams[1] = Double.toString(mLastKnownLocation.getLongitude());
                urlParams[2] = "bar";
                urlParams[3] = PROXIMITY_RADIUS;
                urlParams[4] = getResources().getString(R.string.google_places_web_service_key);
                urlParams[5] = null;
                urlParams[6] = null;
                urlParams[7] = mMap;

                getNearbyPlaces.execute(urlParams);

            }
        });

        //Nightclub filter button functionality
        filterClubBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getNearbyPlaces = new GetNearbyPlaces(MapsActivity.this);

                mMap.clear();
                Object[] urlParams = new Object[8];
                urlParams[0] = Double.toString(mLastKnownLocation.getLatitude());
                urlParams[1] = Double.toString(mLastKnownLocation.getLongitude());
                urlParams[2] = "night_club";
                urlParams[3] = PROXIMITY_RADIUS;
                urlParams[4] = getResources().getString(R.string.google_places_web_service_key);
                urlParams[5] = null;
                urlParams[6] = null;
                urlParams[7] = mMap;

                getNearbyPlaces.execute(urlParams);
            }
        });
    }

    /**
     * if application was paused, when resumed, the current thread will sleep for 2 seconds
     * and then attempt to get the device location.
     * This is for when the user is prompted to turn on GPS, or comes back to the app in general.
     * The reason we check if app was previously paused is because onResume gets called when app is
     * first started (after onStart()... )
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(appWasPaused) {
            Toast.makeText(getApplicationContext(), "onResume()", Toast.LENGTH_LONG).show();
            /*try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            } */

            getDeviceLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        appWasPaused = true;
        Toast.makeText(getApplicationContext(), "onPause()", Toast.LENGTH_LONG).show();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Applies style JSON to map
        styleMap();

        // Prompt the user for location permission
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    private void showNearbyPlaces(List<Place> nearbyPlacesList) {

        if (nearbyPlacesList != null) {
            for (int i = 0; i < nearbyPlacesList.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                Place googlePlace = nearbyPlacesList.get(i);

                String placeName = googlePlace.getName();
                String phone = googlePlace.getPhone();
                String address = googlePlace.getAddress();
                String website = googlePlace.getWebsite();

                double latitude = googlePlace.getLatitude();
                double longitude = googlePlace.getLongitude();

                LatLng latLng = new LatLng(latitude, longitude);
                markerOptions
                        .position(latLng)
                        .title(placeName + " : " + phone);

                mMap.addMarker(markerOptions);

            }
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                final Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // TODO: When device location is turned off, the task.getResult() returns a NULL. This causes an error when moving the camera, and causes app to crash.
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                Toast.makeText(getApplicationContext(), "Showing Alert", Toast.LENGTH_SHORT).show();

                                AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
                                alert.setMessage("Location needs to be turned on.");
                                alert.setPositiveButton("TURN ON", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        MapsActivity.this.startActivity(myIntent);
                                    }
                                });


                                alert.show();
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Adds style JSON to map
     */
    public void styleMap() {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }


    /**
     * initializes all variables and methods
     */
    private void initialize() {
        placeList = new ArrayList<>();

        // Variables
        mapLayout = findViewById(R.id.mapLayout);

        //venueList = new ArrayList<>();

        bottomSheetBehavior1 = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        bottomSheetBehavior2 = BottomSheetBehavior.from(findViewById(R.id.selectedPlaceBottomSheetLayout));

        tabNearYouLayout = findViewById(R.id.tabNearYou);
        tabFavoritesLayout = findViewById(R.id.tabFavorites);
        tabTopLayout = findViewById(R.id.tabTop);

        tabNearYouSec = findViewById(R.id.tabNearYouSecondary);
        tabFavoritesSec = findViewById(R.id.tabFavoritesSecondary);
        tabTopSec = findViewById(R.id.tabTopSecondary);

        filterImageBtn = findViewById(R.id.filterImage);
        filterRestaurantsBtn = findViewById(R.id.filterRestaurants);
        filterClubBtn = findViewById(R.id.filterClub);
        filterBarBtn = findViewById(R.id.filterBars);
        filterBtnIsPressed = false;

        goBackBtn = findViewById(R.id.goBackImage);
        spNameTV = findViewById(R.id.spName);
        spRatingTV = findViewById(R.id.spRating);
        spTypeTV = findViewById(R.id.spType);
        spDescriptionTV = findViewById(R.id.spDescription);
        spAddressTV = findViewById(R.id.spAddress);
        spOpenClosedTV = findViewById(R.id.spOpenClosed);
        spPhoneNumTV = findViewById(R.id.spPhoneNumber);
        spURLTV = findViewById(R.id.spURL);

        appWasPaused = false;


        // Methods & Listeners
        tabNearYouListener();
        tabFavoritesListener();
        tabTopListener();
        bottomSheetListener();
        bottomSheetListener2();
        filterButtonListener();
        filterRestaurantsBtnListener();
        filterBarBtnListener();
        filterClubBtnListener();
        goBackBtnListener();

        //initializeListData();
        //createRecyclerList();

        darkenMap(0);

    }

    /*
    private void movePlacesToVenueList(List<Place> placesList) {
        venueList.clear();
        
        for (int i = 0; i < placesList.size(); i++) {
            String name = placesList.get(i).getName();
            String type = placesList.get(i).getSingleType();

            Venue venue = new Venue(name, 5, type, "124 dacula rd.", "description, description", 5.0, 7.0);
            venueList.add(venue);
        }
    } */

    protected void onPostExecute() {
        placeList = getNearbyPlaces.getNearbyPlacesList();
        // movePlacesToVenueList(getNearbyPlaces.getNearbyPlacesList());
        createRecyclerList();
    }

    /*
     * adds data to venueList

    private void initializeListData() {
        for (int i = 0; i < 5; i++) {
            Venue v = new Venue("Hangovers", 5, "Bar", "123 Piedmont Dr.", "Very nice place.", 3, 7);

            venueList.add(v);
        }
    } */

    /**
     * TODO: set all place details...
     * loads selected place data onto selectedPlaceBottomSheet
     * @param sp user-selected place
     */
    private void setSelectedPlaceBottomSheetData(Place sp) {
        spNameTV.setText(sp.getName());
        spRatingTV.setText(Double.toString(sp.getRating()));
        spTypeTV.setText(sp.getSingleType());
        spAddressTV.setText(sp.getAddress());
        spPhoneNumTV.setText(sp.getPhone());
        //svDescriptionTV.setText(sp.getDescription());
    }

    /**
     * creates RecyclerView
     */
    private void createRecyclerList() {
        String a = "size: " + placeList.size() + "";

        Toast.makeText(MapsActivity.this, a , Toast.LENGTH_LONG).show();


        RecyclerView rv = findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        MyAdapter adapter = new MyAdapter(placeList, this);
        rv.setAdapter(adapter);

    }

    /**
     * @param selectedPlace user-selected place
     */
    public void cardViewClicked(Place selectedPlace) {
        setSelectedPlaceBottomSheetData(selectedPlace);

        bottomSheetBehavior1.setState(STATE_COLLAPSED); // collapses Place List BottomSheet

        bottomSheetBehavior2.setState(STATE_EXPANDED); // expands selected Place BottomSheet
    }

    private void goBackBtnListener() {
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior2.setState(STATE_COLLAPSED); // collapses selected Place BottomSheet

                bottomSheetBehavior1.setState(STATE_EXPANDED); // expands Place List BottomSheet
            }
        });
    }




    /**
     * BottomSheet Listener
     */
    private void bottomSheetListener() {
        bottomSheetBehavior1.setState(STATE_EXPANDED);

        bottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == STATE_EXPANDED) {
                    filterImageBtn.setVisibility(View.VISIBLE);
                    filterImageBtn.setScaleX(1);
                    filterImageBtn.setScaleY(1);


                    filterRestaurantsBtn.setTranslationX(1);
                    filterBarBtn.setTranslationX(1);
                    filterClubBtn.setTranslationX(1);

                } else if (newState == STATE_COLLAPSED) {
                    filterBtnIsPressed = false;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if ((slideOffset) >= 0.75) {
                    // FILTER BUTTON VISIBILITY & SCALE
                    filterImageBtn.setVisibility(View.VISIBLE);
                    filterImageBtn.setScaleX(4 * (slideOffset) - 3);
                    filterImageBtn.setScaleY(4 * (slideOffset) - 3);

                    // FILTER OPTION VISIBILITY & TRANSLATION
                    if (filterBtnIsPressed) {
                        showFilterButtons();

                        filterRestaurantsBtn.setTranslationX(1700 * ((slideOffset - 2) * (-1)) - 1700);
                        filterBarBtn.setTranslationX(1700 * ((slideOffset - 2) * (-1)) - 1700);
                        filterClubBtn.setTranslationX(1700 * ((slideOffset - 2) * (-1)) - 1700);

                        // Darken Map
                        darkenMap((int) (400 * (slideOffset + 1) - 700));
                    }
                } else if ((slideOffset) < 0.75) {
                    filterImageBtn.setVisibility(View.INVISIBLE);

                    // FILTER OPTION VISIBILITY
                    if (filterBtnIsPressed) {
                        hideFilterButtons();
                    }
                } else {
                    filterImageBtn.setVisibility(View.VISIBLE);

                    // FILTER OPTION VISIBILITY
                    if (filterBtnIsPressed) {
                        showFilterButtons();
                    }
                }
            }
        });
    }



    /**
     * BottomSheet Listener 2 - SELECTED PLACE BOTTOM SHEET
     */
    private void bottomSheetListener2() {
        bottomSheetBehavior2.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == STATE_EXPANDED) {
                    goBackBtn.setVisibility(View.VISIBLE);
                    goBackBtn.setScaleX(1);
                    goBackBtn.setScaleY(1);

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if((slideOffset) >= 0.75) {
                    // FILTER BUTTON VISIBILITY & SCALE
                    goBackBtn.setVisibility(View.VISIBLE);
                    goBackBtn.setScaleX(4 * (slideOffset) - 3);
                    goBackBtn.setScaleY(4 * (slideOffset) - 3);

                } else if((slideOffset) < 0.75) {
                    goBackBtn.setVisibility(View.INVISIBLE);

                } else {
                    goBackBtn.setVisibility(View.VISIBLE);

                }
            }
        });
    }


    /**
     *
     */
    private void filterButtonListener() {
        filterImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!filterBtnIsPressed) {
                    filterBtnIsPressed = true;
                    showFilterButtons();
                    darkenMap(100);
                } else {
                    filterBtnIsPressed = false;
                    hideFilterButtons();
                    darkenMap(0);
                }
            }
        });
    }

    private void showFilterButtons() {
        filterRestaurantsBtn.setVisibility(View.VISIBLE);
        filterClubBtn.setVisibility(View.VISIBLE);
        filterBarBtn.setVisibility(View.VISIBLE);
    }

    private void hideFilterButtons() {
        filterRestaurantsBtn.setVisibility(View.INVISIBLE);
        filterClubBtn.setVisibility(View.INVISIBLE);
        filterBarBtn.setVisibility(View.INVISIBLE);
    }

    private void filterRestaurantsBtnListener() {

    }

    private void filterBarBtnListener() {

    }

    private void filterClubBtnListener() {

    }

    private void tabNearYouListener() {
        tabNearYouLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // expand BottomSheet
                bottomSheetBehavior1.setState(STATE_EXPANDED); // expands Place List BottomSheet

                // set new selected colors
                tabNearYouLayout.setBackgroundColor(getResources().getColor(R.color.tabSelected));
                tabNearYouSec.setBackgroundColor(getResources().getColor(R.color.tabSelectedSecondary));

                // set other tab colors to unselected
                tabFavoritesLayout.setBackgroundColor(getResources().getColor(R.color.tabUnselected));
                tabFavoritesSec.setBackgroundColor(getResources().getColor(R.color.tabUnselectedSecondary));

                tabTopLayout.setBackgroundColor(getResources().getColor(R.color.tabUnselected));
                tabTopSec.setBackgroundColor(getResources().getColor(R.color.tabUnselectedSecondary));
            }
        });
    }

    private void tabFavoritesListener() {
        tabFavoritesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // expand BottomSheet
                bottomSheetBehavior1.setState(STATE_EXPANDED); // expands Place List BottomSheet

                // set new selected colors
                tabFavoritesLayout.setBackgroundColor(getResources().getColor(R.color.tabSelected));
                tabFavoritesSec.setBackgroundColor(getResources().getColor(R.color.tabSelectedSecondary));

                // set other tab colors to unselected
                tabNearYouLayout.setBackgroundColor(getResources().getColor(R.color.tabUnselected));
                tabNearYouSec.setBackgroundColor(getResources().getColor(R.color.tabUnselectedSecondary));

                tabTopLayout.setBackgroundColor(getResources().getColor(R.color.tabUnselected));
                tabTopSec.setBackgroundColor(getResources().getColor(R.color.tabUnselectedSecondary));
            }
        });
    }

    private void tabTopListener() {
        tabTopLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // expand BottomSheet
                bottomSheetBehavior1.setState(STATE_EXPANDED); // expands Place List BottomSheet

                // set new selected colors
                tabTopLayout.setBackgroundColor(getResources().getColor(R.color.tabSelected));
                tabTopSec.setBackgroundColor(getResources().getColor(R.color.tabSelectedSecondary));

                // set other tab colors to unselected
                tabNearYouLayout.setBackgroundColor(getResources().getColor(R.color.tabUnselected));
                tabNearYouSec.setBackgroundColor(getResources().getColor(R.color.tabUnselectedSecondary));

                tabFavoritesLayout.setBackgroundColor(getResources().getColor(R.color.tabUnselected));
                tabFavoritesSec.setBackgroundColor(getResources().getColor(R.color.tabUnselectedSecondary));
            }
        });
    }

    /**
     * Darkens the map
     *
     * @param value >= 0, <=255
     */
    private void darkenMap(int value) {
        if (value > 255) {
            mapLayout.getForeground().setAlpha(255);
        } else if (value < 0) {
            mapLayout.getForeground().setAlpha(0);
        } else {
            mapLayout.getForeground().setAlpha(value);
        }
    }


}
