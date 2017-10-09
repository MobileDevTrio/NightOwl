package com.example.MobileDevTrio.nightowl;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    // Entry points for Google Places API
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;

    // Google API object used to obtain device location using GPS, Wi-Fi, and network
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // Default location (KSU Marietta campus) and default zoom to use when location permission
    // is not granted
    // TODO: Allow/Prompt user to set the default location
    private LatLng mDefaultLocation = new LatLng(33.9397, -84.5197);
    private static final int DEFAULT_ZOOM = 15;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. Which is equivalent to the
    // last-known location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    protected FrameLayout mapLayout;
    private LinearLayout tabNearYouLayout, tabFavoritesLayout, tabTopLayout;
    private TextView tabNearYouTV, tabFavoritesTV, tabTopTV;
    private View tabNearYouSec, tabFavoritesSec, tabTopSec;

    // Venue listItem
    private List<Venue> venueList;

    // BottomSheet Behavior
    BottomSheetBehavior bottomSheetBehavior1, bottomSheetBehavior2;

    // Filter Image Buttons
    boolean filterBtnIsPressed;
    protected ImageView filterImageBtn, filterRestaurantsBtn, filterClubBtn, filterBarBtn;

    // SelectedVenueBottomSheet Views
    protected ImageView goBackBtn;
    protected TextView  svNameTV, svRatingTV, svTypeTV, svDescriptionTV, svAddressTV, svOpenClosedTV,
                        svPhoneNumTV, svURLTV;



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

        // Prompt the user for location permission
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

//        mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(mDefaultLocation.latitude, mDefaultLocation.longitude))
//                .title("Hello world"));
//
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                mMap.addMarker(new MarkerOptions()
//                        .position(latLng)
//                        .title("New markerr"));
//            }
//        });
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
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
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
     * initializes all variables and methods
     */
    private void initialize() {
        // Variables
        mapLayout = findViewById(R.id.mapLayout);

        venueList = new ArrayList<>();

        bottomSheetBehavior1 = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        bottomSheetBehavior2 = BottomSheetBehavior.from(findViewById(R.id.selectedVenueBottomSheetLayout));

        tabNearYouLayout = findViewById(R.id.tabNearYou);
        tabFavoritesLayout = findViewById(R.id.tabFavorites);
        tabTopLayout = findViewById(R.id.tabTop);

        tabNearYouTV = findViewById(R.id.tabNearYouTextView);
        tabFavoritesTV = findViewById(R.id.tabFavoritesTextView);
        tabTopTV = findViewById(R.id.tabTopTextView);

        tabNearYouSec = findViewById(R.id.tabNearYouSecondary);
        tabFavoritesSec = findViewById(R.id.tabFavoritesSecondary);
        tabTopSec = findViewById(R.id.tabTopSecondary);

        filterImageBtn = findViewById(R.id.filterImage);
        filterRestaurantsBtn = findViewById(R.id.filterRestaurants);
        filterClubBtn = findViewById(R.id.filterClub);
        filterBarBtn = findViewById(R.id.filterBars);
        filterBtnIsPressed = false;

        goBackBtn = findViewById(R.id.goBackImage);
        svNameTV = findViewById(R.id.svName);
        svRatingTV = findViewById(R.id.svRating);
        svTypeTV = findViewById(R.id.svType);
        svDescriptionTV = findViewById(R.id.svDescription);
        svAddressTV = findViewById(R.id.svAddress);
        svOpenClosedTV = findViewById(R.id.svOpenClosed);
        svPhoneNumTV = findViewById(R.id.svPhoneNumber);
        svURLTV = findViewById(R.id.svURL);


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

        initializeListData();
        createRecyclerList();

        darkenMap(0);
    }

    /**
     * adds data to venueList
     */
    private void initializeListData() {
        for (int i = 0; i < 5; i++) {
            Venue v = new Venue("Hangovers", 5, "Bar", "123 Piedmont Dr.", "Very nice place.", 3, 7);

            venueList.add(v);
        }
    }

    /**
     * TODO: set all venue details...
     * loads selected venue data onto selectedVenueBottomSheet
     * @param sv user-selected venue
     */
    private void setSelectedVenueBottomSheetData(Venue sv) {
        svNameTV.setText(sv.getName());
        svRatingTV.setText(Double.toString(sv.getRating()));
        svTypeTV.setText(sv.getType());
        svAddressTV.setText(sv.getAddress());
        svDescriptionTV.setText(sv.getDescription());
    }

    /**
     * creates RecyclerView
     */
    private void createRecyclerList() {
        RecyclerView rv = findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        MyAdapter adapter = new MyAdapter(venueList, this);
        rv.setAdapter(adapter);

    }

    /**
     * @param selectedVenue user-selected venue
     */
    public void cardViewClicked(Venue selectedVenue) {
        setSelectedVenueBottomSheetData(selectedVenue);

        bottomSheetBehavior1.setState(STATE_COLLAPSED); // collapses venue List BottomSheet

        bottomSheetBehavior2.setState(STATE_EXPANDED); // expands selected Venue BottomSheet
    }

    private void goBackBtnListener() {
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior2.setState(STATE_COLLAPSED); // collapses selected Venue BottomSheet

                bottomSheetBehavior1.setState(STATE_EXPANDED); // expands Venue List BottomSheet
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
     * BottomSheet Listener 2 - SELECTED VENUE BOTTOM SHEET
     */
    private void bottomSheetListener2() {
        bottomSheetBehavior2.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == STATE_EXPANDED) {
                    goBackBtn.setVisibility(View.VISIBLE);
                    goBackBtn.setScaleX(1);
                    goBackBtn.setScaleY(1);

                } else if(newState == STATE_COLLAPSED) {
                    //filterBtnIsPressed = false;
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
                bottomSheetBehavior1.setState(STATE_EXPANDED); // expands Venue List BottomSheet

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
                bottomSheetBehavior1.setState(STATE_EXPANDED); // expands Venue List BottomSheet

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
                bottomSheetBehavior1.setState(STATE_EXPANDED); // expands Venue List BottomSheet

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
