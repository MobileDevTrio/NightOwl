package com.example.MobileDevTrio.nightowl;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object, Void, List<Place>> {
    private final String PARAM_KEY = "key=";
    private final String PARAM_PLACE_ID = "placeid=";
    private final String PARAM_LOCATION = "location=";
    private final String PARAM_RADIUS = "radius=";
    private final String PARAM_TYPE = "type=";
    private final String PARAM_OPENNOW = "opennow";
    private final String PARAM_PAGETOKEN = "pagetoken=";
    private final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private final String PLACE_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";

    private List<Place> nearbyPlacesList = new ArrayList<>();

    private GoogleMap mMap;

    private Context context;

    private PlaceType placeType;

    public GetNearbyPlaces(Context context) {
        this.context = context;
    }

    @Override
    protected List<Place> doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[7];

        Log.d("NightOwl:", " doInBackground() is running...");


        switch((String)objects[2]) {
            case "restaurant": placeType = PlaceType.RESTAURANT;
                break;
            case "bar": placeType = PlaceType.BAR;
                break;
            case "night_club": placeType = PlaceType.CLUB;
                break;
            default: placeType =  PlaceType.RESTAURANT;
                break;
        }

        /*
           removing this try block caused some kind of FileNotFoundError.
         */
        try {
            // first 20 results
            Pair<String, List<Place>> pair = new NearbyPlacesParser(buildURL(objects), placeType).parseNearbyPlaces();
            nearbyPlacesList.addAll(pair.second);

            Thread.sleep(1);

            /* check for 20-40 results
            if (pair.first != null) {
                objects[5] = pair.first;
                Thread.sleep(2000); // There is a short delay between when a next_page_token is issued, and when it will become valid
                pair = new NearbyPlacesParser(buildURL(objects)).parseNearbyPlaces();
                nearbyPlacesList.addAll(pair.second);

                /* check for 40-60 results
                if (pair.first != null) {
                    objects[5] = pair.first;
                    Thread.sleep(2000);
                    pair = new NearbyPlacesParser(buildURL(objects)).parseNearbyPlaces();
                    nearbyPlacesList.addAll(pair.second);
                }
            } */

            // Further updates place details
            for (Place place:nearbyPlacesList) {
                objects[6] = place.getPlaceId();
                new PlaceParser(buildURL(objects)).parsePlace(place);
            }

            return nearbyPlacesList;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Place> places) {

        //showNearbyPlaces(places);
        //((MapsActivity) context).onPostExecute();

        switch (placeType) {
            case RESTAURANT: ((MapsActivity) context).setRestaurantsList(places);
                //((MapsActivity) context).checkIfReady();
                break;
            case BAR: ((MapsActivity) context).setBarsList(places);
                //((MapsActivity) context).checkIfReady();
                break;
            case CLUB: ((MapsActivity) context).setClubsList(places);
                ((MapsActivity) context).checkIfReady();
                break;
        }

    }

    public List<Place> getNearbyPlacesList() {
        return nearbyPlacesList;
    }

    private String buildURL(Object... params) {
        String latitude      = (String) params[0];
        String longitude     = (String) params[1];
        String type          = (String) params[2];
        int radius           =    (int) params[3];
        String key           = (String) params[4];
        String nextPageToken = (String) params[5];
        String placeId       = (String) params[6];

        StringBuilder stringBuilder = new StringBuilder();

        if (placeId != null) {
            stringBuilder.append(PLACE_DETAILS_URL);
            stringBuilder.append(PARAM_KEY).append(key);
            stringBuilder.append("&").append(PARAM_PLACE_ID).append(placeId);

            return stringBuilder.toString();
        }

        stringBuilder.append(PLACES_SEARCH_URL);
        stringBuilder.append(PARAM_KEY).append(key);

        if (nextPageToken != null) {
            stringBuilder.append("&").append(PARAM_PAGETOKEN).append(nextPageToken);

            return stringBuilder.toString();
        }

        stringBuilder.append("&").append(PARAM_LOCATION).append(latitude).append(",").append(longitude);
        stringBuilder.append("&").append(PARAM_TYPE).append(type);
        stringBuilder.append("&").append(PARAM_OPENNOW);
        stringBuilder.append("&").append(PARAM_RADIUS).append(radius);

        //System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }
}
