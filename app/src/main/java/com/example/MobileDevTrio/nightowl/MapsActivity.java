package com.example.MobileDevTrio.nightowl;


import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // Entry points for Google Places API
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;

    protected FrameLayout mapLayout;
    private LinearLayout tabNearYouLayout, tabFavoritesLayout, tabTopLayout;
    private TextView tabNearYouTV, tabFavoritesTV, tabTopTV;
    private View tabNearYouSec, tabFavoritesSec, tabTopSec;

    // Venue listItem
    private List<Venue> venueList;

    // Filter Image Buttons
    boolean filterBtnIsPressed;
    ImageView filterImageBtn;
    ImageView filterRestaurantsBtn;
    ImageView filterClubBtn;
    ImageView filterBarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

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
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }


    /**
     * initializes all variables and methods
     */
    private void initialize() {
        // Variables
        mapLayout = findViewById(R.id.mapLayout);
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

        venueList = new ArrayList<>();

        // Methods & Listeners

        tabNearYouListener();
        tabFavoritesListener();
        tabTopListener();
        bottomSheetListener();
        filterButtonListener();
        filterRestaurantsBtnListener();
        filterBarBtnListener();
        filterClubBtnListener();

        initializeListData();
        createRecyclerList();

        darkenMap(0);
    }

    /**
     * adds data to venueList
     */
    private void initializeListData() {
        for(int i = 0; i < 5; i++) {
            Venue v = new Venue("Hangovers", 5, "Bar", "123 Piedmont Dr.", "Very nice place.", 3, 7);

            venueList.add(v);
        }
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
     * TODO: change parameter to a better identifier than venue name.
     * @param venueName name of clicked venue.
     */
    public void cardViewClicked(String venueName) {

    }


    /**
     * BottomSheet Listener
     */
    private void bottomSheetListener() {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));

        bottomSheetBehavior.setState(STATE_EXPANDED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == STATE_EXPANDED) {
                    filterImageBtn.setVisibility(View.VISIBLE);
                    filterImageBtn.setScaleX(1);
                    filterImageBtn.setScaleY(1);


                    filterRestaurantsBtn.setTranslationX(1);
                    filterBarBtn.setTranslationX(1);
                    filterClubBtn.setTranslationX(1);

                } else if(newState == STATE_COLLAPSED) {
                    filterBtnIsPressed = false;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if((slideOffset) >= 0.75) {
                    // FILTER BUTTON VISIBILITY & SCALE
                    filterImageBtn.setVisibility(View.VISIBLE);
                    filterImageBtn.setScaleX(4 * (slideOffset) - 3);
                    filterImageBtn.setScaleY(4 * (slideOffset) - 3);

                    // FILTER OPTION VISIBILITY & TRANSLATION
                    if(filterBtnIsPressed) {
                        showFilterButtons();

                        filterRestaurantsBtn.setTranslationX(1700 * ((slideOffset - 2) * (-1)) - 1700);
                        filterBarBtn.setTranslationX(1700 * ((slideOffset - 2) * (-1)) - 1700);
                        filterClubBtn.setTranslationX(1700 * ((slideOffset - 2) * (-1)) - 1700);

                        // Darken Map
                        darkenMap((int)(400 * (slideOffset + 1) - 700));
                    }
                } else if((slideOffset) < 0.75) {
                    filterImageBtn.setVisibility(View.INVISIBLE);

                    // FILTER OPTION VISIBILITY
                    if(filterBtnIsPressed) {
                        hideFilterButtons();
                    }
                } else {
                    filterImageBtn.setVisibility(View.VISIBLE);

                    // FILTER OPTION VISIBILITY
                    if(filterBtnIsPressed) {
                        showFilterButtons();
                    }
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

                if(!filterBtnIsPressed) {
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
     * @param value >= 0, <=255
     */
    private void darkenMap(int value) {
        if(value > 255) {
            mapLayout.getForeground().setAlpha(255);
        }
        else if(value < 0) {
            mapLayout.getForeground().setAlpha(0);
        }
        else {
            mapLayout.getForeground().setAlpha(value);
        }
    }



}
