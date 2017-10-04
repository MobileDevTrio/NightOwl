package com.example.khoapham.nightowl;

// illya balakin was here... 

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LinearLayout tabNearYouLayout, tabFavoritesLayout, tabTopLayout;
    private TextView tabNearYouTV, tabFavoritesTV, tabTopTV;
    private View tabNearYouSec, tabFavoritesSec, tabTopSec;

    // Venue listItem
    private List<Venue> venueList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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

        tabNearYouLayout = findViewById(R.id.tabNearYou);
        tabFavoritesLayout = findViewById(R.id.tabFavorites);
        tabTopLayout = findViewById(R.id.tabTop);

        tabNearYouTV = findViewById(R.id.tabNearYouTextView);
        tabFavoritesTV = findViewById(R.id.tabFavoritesTextView);
        tabTopTV = findViewById(R.id.tabTopTextView);

        tabNearYouSec = findViewById(R.id.tabNearYouSecondary);
        tabFavoritesSec = findViewById(R.id.tabFavoritesSecondary);
        tabTopSec = findViewById(R.id.tabTopSecondary);

        venueList = new ArrayList<>();

        // Methods & Listeners

        tabNearYouListener();
        tabFavoritesListener();
        tabTopListener();

        initializeListData();
        createRecyclerList();
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
        RecyclerView rv = (RecyclerView)findViewById(R.id.recyclerView);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        MyAdapter adapter = new MyAdapter(venueList, this);
        rv.setAdapter(adapter);

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





}
