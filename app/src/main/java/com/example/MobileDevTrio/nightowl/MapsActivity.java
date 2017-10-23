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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.List;
import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;

/**
 *  TODO: add MarkerOptions.Listeners to bring up location details of marker touched
 *  TODO: add call functionality
 *  TODO: show the hours of a place in location details view
 *  TODO: add method to check if location services is turned on
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final String TAG = MapsActivity.class.getSimpleName();

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PROXIMITY_RADIUS = 1600 * 4;  // 4 mile radius
    private static final double LATITUDE_OFFSET = 0.05;
    private static final float DEFAULT_ZOOM = 11.8f;

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
    protected boolean filterBtnIsPressed;
    protected ImageView filterImageBtn, filterRestaurantsBtn, filterClubBtn, filterBarBtn, clearFilterBtn;

    // SelectedPlaceBottomSheet Views
    protected ImageView goBackBtn;
    protected TextView  spNameTV, spRatingTV, spTypeTV, spAddressTV, spOpenClosedTV, spClosingTimeTV,
                        spPhoneNumTV, spURLTV;

    protected boolean appWasPaused;

    // Loading Screen
    protected RelativeLayout loadingScreenLayout;
    protected ProgressBar pbRestaurants, pbBars, pbClubs;
    protected ImageView  checkRestaurants, checkBars, checkClubs;

    //Ratings stars
    ImageView spStarEmpty1, spStarEmpty2, spStarEmpty3, spStarEmpty4, spStarEmpty5,
            spStarFull1, spStarFull2, spStarFull3, spStarFull4, spStarFull5,
            spStarHalf1, spStarHalf2, spStarHalf3, spStarHalf4, spStarHalf5;


    private List<Place> placeList, restaurantList, barList, clubList;
    private List<MarkerOptions> restaurantMarkers, barMarkers, clubMarkers;
    boolean restaurantListReady, barListReady, clubListReady;

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
                //mMap.getUiSettings().setZoomControlsEnabled(true);
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
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude() - LATITUDE_OFFSET,
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                                /*******************************************************************
                                 * GET PLACES HERE
                                 */

                                startGettingPlaces();



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

    /**********************************************************************************************************************************************************
     **********************************************************************************************************************************************************
     *                                     GET NEARBY PLACES
     */

    private void startGettingPlaces() {
        showLoadingScreen();
        getRestaurants();
        getBars();
        getClubs();
    }

    private void getRestaurants() {
        Log.d("NightOwl-d", "initial - getPlaces() - Restaurant");

        Object[] urlParams = new Object[7];
        urlParams[0] = Double.toString(mLastKnownLocation.getLatitude());
        urlParams[1] = Double.toString(mLastKnownLocation.getLongitude());
        //urlParams[0] = Double.toString(33.9397);    // for emulator
        //urlParams[1] = Double.toString(-84.5197);   // for emulator
        urlParams[2] = "restaurant";
        urlParams[3] = PROXIMITY_RADIUS;
        urlParams[4] = getResources().getString(R.string.google_places_web_service_key);
        urlParams[5] = null;
        urlParams[6] = null;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces(MapsActivity.this);
        getNearbyPlaces.execute(urlParams);
    }

    public void setRestaurantsList(List<Place> restaurantList) {
        this.restaurantList = restaurantList;
        restaurantListReady = true;
        setRestaurantsCheckMark();
    }

    public void getBars() {
        Log.d("NightOwl-d", "initial - getPlaces() - Bars");

        Object[] urlParams = new Object[7];
        urlParams[0] = Double.toString(mLastKnownLocation.getLatitude());
        urlParams[1] = Double.toString(mLastKnownLocation.getLongitude());
        //urlParams[0] = Double.toString(33.9397);    // for emulator
        //urlParams[1] = Double.toString(-84.5197);   // for emulator
        urlParams[2] = "bar";
        urlParams[3] = PROXIMITY_RADIUS;
        urlParams[4] = getResources().getString(R.string.google_places_web_service_key);
        urlParams[5] = null;
        urlParams[6] = null;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces(MapsActivity.this);
        getNearbyPlaces.execute(urlParams);
    }

    public void setBarsList(List<Place> barList) {
        this.barList = barList;
        barListReady = true;
        setBarsCheckMark();
    }

    public void getClubs() {
        Log.d("NightOwl-d", "initial - getPlaces() - Clubs");

        Object[] urlParams = new Object[7];
        urlParams[0] = Double.toString(mLastKnownLocation.getLatitude());
        urlParams[1] = Double.toString(mLastKnownLocation.getLongitude());
        //urlParams[0] = Double.toString(33.9397);    // for emulator
        //urlParams[1] = Double.toString(-84.5197);   // for emulator
        urlParams[2] = "night_club";
        urlParams[3] = PROXIMITY_RADIUS;
        urlParams[4] = getResources().getString(R.string.google_places_web_service_key);
        urlParams[5] = null;
        urlParams[6] = null;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces(MapsActivity.this);
        getNearbyPlaces.execute(urlParams);
    }

    public void setClubsList(List<Place> clubList) {
        this.clubList = clubList;
        clubListReady = true;
        showClubsCheckMark();
    }

    /**
     * Checks if all lists are ready
     */
    public void checkIfReady() {
        if(restaurantListReady && barListReady && clubListReady) {
            setPlaces();
        }
    }

    private void setPlaces() {
        Log.d("NightOwl-d", "initial - setting places...");

        setNearbyPlacesList();

        createRecyclerList(placeList);

        // Show the markers on the map
        showNearbyPlaces(restaurantMarkers);
        showNearbyPlaces(barMarkers);
        showNearbyPlaces(clubMarkers);
    }

    /**
     * Merges three types lists into one lists - placesList
     */
    private void setNearbyPlacesList() {
        int addRest;
        int addBar;
        int addClub;

        if (restaurantList.size() < 10) {
            addRest = restaurantList.size();
        } else {
            addRest = 10;
        }

        ///////////////////////////////////////////////////////////////////

        if (barList.size() < 10) {
            addBar = barList.size();
        } else {
            addBar = 10;
        }

        ////////////////////////////////////////////////////////////////////

        if (clubList.size() < 10) {
            addClub = clubList.size();
        } else {
            addClub = 10;
        }

        ///////////////////////////////////////////////////////////////////

        //TODO: change algorithm to a more random algo...
        for (int i = 0; i < addRest; i++) {
            placeList.add(restaurantList.get(i));
        }

        for (int i = 0; i < addBar; i++) {
            placeList.add(barList.get(i));
        }

        for (int i = 0; i < addClub; i++) {
            placeList.add(clubList.get(i));
        }


        // Initialize the place markers list
        restaurantMarkers = new ArrayList<>();
        barMarkers = new ArrayList<>();
        clubMarkers = new ArrayList<>();

        sortPlaceMarkersIntoList(restaurantList);
        sortPlaceMarkersIntoList(barList);
        sortPlaceMarkersIntoList(clubList);

    }

    /**
     *  Creates a MarkerOption for a place and stores it in a List, according to the place type
     * @param places List of nearby places
     */
    private void sortPlaceMarkersIntoList(List<Place> places) {

        if (places != null) {
            for (int i = 0; i < places.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                Place googlePlace = places.get(i);

                String placeName = googlePlace.getName();

                double latitude = googlePlace.getLatitude();
                double longitude = googlePlace.getLongitude();

                LatLng latLng = new LatLng(latitude, longitude);
                markerOptions.position(latLng).title(placeName);

                switch (googlePlace.getSingleType()) {
                    case "restaurant":
                        restaurantMarkers.add(markerOptions);
                        break;
                    case "bar":
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        barMarkers.add(markerOptions);
                        break;
                    case "night_club":
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                        clubMarkers.add(markerOptions);
                        break;
                }
            }
        }
    }

    private void showNearbyPlaces(List<MarkerOptions> placeMarkers) {

        if (placeMarkers != null) {
            for (int i = 0; i < placeMarkers.size(); i++) {
                mMap.addMarker(placeMarkers.get(i));
            }
        }
    }


    /**********************************************************************************************************************************************************
     **********************************************************************************************************************************************************
     *                                     LOADING SCREEN
     */

    private void showLoadingScreen() {
        setLoadingScreenVisibility();
    }

    /**
     * TODO: make animation
     */
    private void hideLoadingScreen() {
        Log.d("NightOwl", "Hiding loading screen...");
        loadingScreenSlideUp();
        loadingScreenLayout.setVisibility(View.GONE);
    }

    private void setLoadingScreenVisibility() {
        loadingScreenLayout.setVisibility(View.VISIBLE);

        pbRestaurants.setVisibility(View.VISIBLE);
        pbBars.setVisibility(View.VISIBLE);
        pbClubs.setVisibility(View.VISIBLE);

        checkRestaurants.setVisibility(View.GONE);
        checkBars.setVisibility(View.GONE);
        checkClubs.setVisibility(View.GONE);
    }

    /**
     * Sets restaurants check mark to VISIBLE
     */
    private void setRestaurantsCheckMark() {
        pbRestaurants.setVisibility(View.GONE);
        checkRestaurants.setVisibility(View.VISIBLE);

        Animation scaleAnimation = new ScaleAnimation(0.1f, 1f, 0.2f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);

        checkRestaurants.startAnimation(scaleAnimation);
    }

    /**
     * Sets bars check mark to VISIBLE
     */
    private void setBarsCheckMark() {
        pbBars.setVisibility(View.GONE);
        checkBars.setVisibility(View.VISIBLE);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0.1f, 1f, 0.2f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(400);

        checkBars.startAnimation(scaleAnimation);
    }

    /**
     * Sets clubs check mark to VISIBLE
     */
    private void showClubsCheckMark() {
        pbClubs.setVisibility(View.GONE);
        checkClubs.setVisibility(View.VISIBLE);

        Animation scaleAnimation = new ScaleAnimation(0.1f, 1f, 0.2f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(400);

        checkClubs.startAnimation(scaleAnimation);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hideLoadingScreen();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //hideLoadingScreen();
    }

    private void loadingScreenSlideUp() {
        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        loadingScreenLayout.setAnimation(slide_up);
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

        // Variables
        mapLayout = findViewById(R.id.mapLayout);

        //venueList = new ArrayList<>();
        placeList = new ArrayList<>();
        restaurantList = new ArrayList<>();
        barList = new ArrayList<>();
        clubList = new ArrayList<>();

        restaurantListReady = false;
        barListReady = false;
        clubListReady = false;

        // Loading Screen
        loadingScreenLayout = findViewById(R.id.loadingScreenLayout);
        pbRestaurants = findViewById(R.id.loadingRestaurantsProgressBar);
        pbBars = findViewById(R.id.loadingBarsProgressBar);
        pbClubs = findViewById(R.id.loadingClubsProgressBar);
        checkRestaurants = findViewById(R.id.check_restaurants);
        checkBars = findViewById(R.id.check_bars);
        checkClubs = findViewById(R.id.check_clubs);


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
        clearFilterBtn = findViewById(R.id.clearFilterBtn);
        filterBtnIsPressed = false;

        goBackBtn = findViewById(R.id.goBackImage);
        spNameTV = findViewById(R.id.spName);
        spRatingTV = findViewById(R.id.spRating);
        spTypeTV = findViewById(R.id.spType);
        spAddressTV = findViewById(R.id.spAddress);
        spOpenClosedTV = findViewById(R.id.spOpenClosed);
        spClosingTimeTV = findViewById(R.id.spClosingTime);
        spPhoneNumTV = findViewById(R.id.spPhoneNumber);
        spURLTV = findViewById(R.id.spURL);

        // Rating Stars
        spStarFull1 = findViewById(R.id.spRatingStarFull1);
        spStarFull2 = findViewById(R.id.spRatingStarFull2);
        spStarFull3 = findViewById(R.id.spRatingStarFull3);
        spStarFull4 = findViewById(R.id.spRatingStarFull4);
        spStarFull5 = findViewById(R.id.spRatingStarFull5);

        spStarHalf1 = findViewById(R.id.spRatingStarHalf1);
        spStarHalf2 = findViewById(R.id.spRatingStarHalf2);
        spStarHalf3 = findViewById(R.id.spRatingStarHalf3);
        spStarHalf4 = findViewById(R.id.spRatingStarHalf4);
        spStarHalf5 = findViewById(R.id.spRatingStarHalf5);

        spStarEmpty1 = findViewById(R.id.spRatingStarEmpty1);
        spStarEmpty2 = findViewById(R.id.spRatingStarEmpty2);
        spStarEmpty3 = findViewById(R.id.spRatingStarEmpty3);
        spStarEmpty4 = findViewById(R.id.spRatingStarEmpty4);
        spStarEmpty5 = findViewById(R.id.spRatingStarEmpty5);

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
        clearFilterBtnListener();
        goBackBtnListener();

        //initializeListData();
        //createRecyclerList();

        darkenMap(0);

        showLoadingScreen();

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

//    protected void onPostExecute() {
//        placeList = getNearbyPlaces.getNearbyPlacesList();
//        // movePlacesToVenueList(getNearbyPlaces.getNearbyPlacesList());
//        createRecyclerList();
//    }

    /*
     * adds data to venueList

    private void initializeListData() {
        for (int i = 0; i < 5; i++) {
            Venue v = new Venue("Hangovers", 5, "Bar", "123 Piedmont Dr.", "Very nice place.", 3, 7);

            venueList.add(v);
        }
    } */

    /**11
     * TODO: set all place details...
     * loads selected place data onto selectedPlaceBottomSheet
     * @param sp user-selected place
     */
    private void setSelectedPlaceBottomSheetData(Place sp) {
        resetRatingStarVisibility();
        spNameTV.setText(sp.getName());
        spRatingTV.setText(Double.toString(sp.getRating()));
        setSelectedPlaceRatingStars(Double.toString(sp.getRating()));
        spTypeTV.setText(sp.getSimplifiedType());
        spAddressTV.setText(sp.getAddress());
        spClosingTimeTV.setText(getClosingTimeString(sp));
        spPhoneNumTV.setText(sp.getPhone());
        spURLTV.setText(getSimplifiedPlaceWebsite(sp.getWebsite()));
    }

    private String getClosingTimeString(Place place) {
        String closingTimeString = "";
        String closingTime = place.getClosingHours();
        boolean isOpen247 = place.isOpen247();

        if (isOpen247) {
            closingTimeString = "Open 24 Hours";
        }
        else if (closingTime != null) {
            if (!closingTime.isEmpty()) {
                String[] hours = closingTime.split(":");
                String[] minAMPM = hours[1].split(" ");
                String part1 = hours[0];
                String part2 = minAMPM[0];
                String part3 = minAMPM[1];

                // checks if beginning character is '0' and removes it.
                if (part1.charAt(0) == '0') {
                    part1 = part1.charAt(1) + "";
                }

                //
                if (part2.charAt(0) == '0' && part2.charAt(1) == '0') {
                    closingTimeString = "Close " + part1 + " " + part3;
                } else {
                    closingTimeString = "Close " + part1 + ":" + part2 + " " + part3;
                }


            }
        }

        return closingTimeString;
    }

    private void setSelectedPlaceRatingStars(String rating) {
        int part1 = 0, part2 = 0;
        if(!rating.isEmpty()) {
            String[] part = rating.split("\\.");
            part1 = Integer.parseInt(part[0]);
            part2 = Integer.parseInt(part[1]);
        }

        // Set Full Stars
        if (part1 >= 1) {
            spStarFull1.setVisibility(View.VISIBLE);
        }
        if (part1 >= 2) {
            spStarFull2.setVisibility(View.VISIBLE);
        }
        if (part1 >= 3) {
            spStarFull3.setVisibility(View.VISIBLE);
        }
        if (part1 >= 4) {
            spStarFull4.setVisibility(View.VISIBLE);
        }
        if (part1 == 5) {
            spStarFull5.setVisibility(View.VISIBLE);
        }


        // Set Initial Empty/Half/Full Stars
        if(part1 == 0) {
            if(part2 < 3) {
                spStarEmpty1.setVisibility(View.VISIBLE);       // set first empty star
            } else if (part2 >= 3 && part2 <= 7) {
                spStarHalf1.setVisibility(View.VISIBLE);        // set half star
            } else if (part2 > 7) {
                spStarFull1.setVisibility(View.VISIBLE);        // set next full star
            }
        }
        if (part1 == 1) {
            spStarFull1.setVisibility(View.VISIBLE);
            if(part2 < 3) {
                spStarEmpty2.setVisibility(View.VISIBLE);       // set first empty star
            } else if (part2 >= 3 && part2 <= 7) {
                spStarHalf2.setVisibility(View.VISIBLE);        // set half star
            } else if (part2 > 7) {
                spStarFull2.setVisibility(View.VISIBLE);        // set next full star
            }
        }
        if (part1 == 2) {
            spStarFull2.setVisibility(View.VISIBLE);
            if(part2 < 3) {
                spStarEmpty3.setVisibility(View.VISIBLE);       // set first empty star
            } else if (part2 >= 3 && part2 <= 7) {
                spStarHalf3.setVisibility(View.VISIBLE);        // set half star
            } else if (part2 > 7) {
                spStarFull3.setVisibility(View.VISIBLE);        // set next full star
            }
        }
        if (part1 == 3) {
            spStarFull3.setVisibility(View.VISIBLE);
            if(part2 < 3) {
                spStarEmpty4.setVisibility(View.VISIBLE);       // set first empty star
            } else if (part2 >= 3 && part2 <= 7) {
                spStarHalf4.setVisibility(View.VISIBLE);        // set half star
            } else if (part2 > 7) {
                spStarFull4.setVisibility(View.VISIBLE);        // set next full star
            }
        }
        if (part1 == 4) {
            spStarFull4.setVisibility(View.VISIBLE);
            if(part2 < 3) {
                spStarEmpty5.setVisibility(View.VISIBLE);       // set first empty star
            } else if (part2 >= 3 && part2 <= 7) {
                spStarHalf5.setVisibility(View.VISIBLE);        // set half star
            } else if (part2 > 7) {
                spStarFull5.setVisibility(View.VISIBLE);        // set next full star
            }
        }
        if (part1 == 5) {
            spStarFull5.setVisibility(View.VISIBLE);
        }

        // Set Remaining Empty Stars
        if(part1 <= 3) {
            spStarEmpty5.setVisibility(View.VISIBLE);
        }
        if(part1 <= 2) {
            spStarEmpty4.setVisibility(View.VISIBLE);
        }
        if(part1 <= 1) {
            spStarEmpty3.setVisibility(View.VISIBLE);
        }
        if(part1 == 0) {
            spStarEmpty2.setVisibility(View.VISIBLE);
        }

    }

    private void resetRatingStarVisibility() {
        spStarFull1.setVisibility(View.INVISIBLE);
        spStarFull2.setVisibility(View.INVISIBLE);
        spStarFull3.setVisibility(View.INVISIBLE);
        spStarFull4.setVisibility(View.INVISIBLE);
        spStarFull5.setVisibility(View.INVISIBLE);

        spStarHalf1.setVisibility(View.INVISIBLE);
        spStarHalf2.setVisibility(View.INVISIBLE);
        spStarHalf3.setVisibility(View.INVISIBLE);
        spStarHalf4.setVisibility(View.INVISIBLE);
        spStarHalf5.setVisibility(View.INVISIBLE);

        spStarEmpty1.setVisibility(View.INVISIBLE);
        spStarEmpty2.setVisibility(View.INVISIBLE);
        spStarEmpty3.setVisibility(View.INVISIBLE);
        spStarEmpty4.setVisibility(View.INVISIBLE);
        spStarEmpty5.setVisibility(View.INVISIBLE);
    }


    /**
     * @param URL - full url
     * @return simplified url
     */
    private String getSimplifiedPlaceWebsite(String URL) {
        String simplifiedURL = "No Website Available.";

        if(URL != null) {
            if(!URL.isEmpty()) {
                String[] URLpart = URL.split("/");

                simplifiedURL = URLpart[0] + "//" + URLpart[1] + URLpart[2] + "/";
            }
        }

        return simplifiedURL;
    }

    /**
     * creates RecyclerView
     */
    private void createRecyclerList(List<Place> placeList) {
        String a = "size: " + placeList.size() + "";

        Toast.makeText(MapsActivity.this, a , Toast.LENGTH_LONG).show();
        Log.d("NightOwl:", " creating recyclerList");

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
                    clearFilterBtn.setTranslationX(1);

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

                        filterRestaurantsBtn.setTranslationX(2200 * ((slideOffset - 2) * (-1)) - 2200);
                        filterBarBtn.setTranslationX(2000 * ((slideOffset - 2) * (-1)) - 2000);
                        filterClubBtn.setTranslationX(1800 * ((slideOffset - 2) * (-1)) - 1800);
                        clearFilterBtn.setTranslationX(-2000 * ((slideOffset - 2) * (-1)) + 2000);

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
        clearFilterBtn.setVisibility(View.VISIBLE);
    }

    private void hideFilterButtons() {
        filterRestaurantsBtn.setVisibility(View.INVISIBLE);
        filterClubBtn.setVisibility(View.INVISIBLE);
        filterBarBtn.setVisibility(View.INVISIBLE);
        clearFilterBtn.setVisibility(View.INVISIBLE);
    }

    /**
     *  Shows only restaurant locations on the map
     */
    private void filterRestaurantsBtnListener() {
        filterRestaurantsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("NightOwl", "filter_button RESTAURANT clicked");

                mMap.clear();
                showNearbyPlaces(restaurantMarkers);
                createRecyclerList(restaurantList);
            }
        });
    }

    /**
     *
     */
    private void filterBarBtnListener() {
        filterBarBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d("NightOwl", "filter_button BAR clicked");

                mMap.clear();
                showNearbyPlaces(barMarkers);
                createRecyclerList(barList);
            }
        });
    }

    /**
     *
     */
    private void filterClubBtnListener() {
        filterClubBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d("NightOwl", "filter_button CLUB clicked");

                mMap.clear();
                showNearbyPlaces(clubMarkers);
                createRecyclerList(clubList);
            }
        });
    }

    /**
     *
     */
    private void clearFilterBtnListener() {
        clearFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                showNearbyPlaces(restaurantMarkers);
                showNearbyPlaces(barMarkers);
                showNearbyPlaces(clubMarkers);
                createRecyclerList(placeList);
            }
        });
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
