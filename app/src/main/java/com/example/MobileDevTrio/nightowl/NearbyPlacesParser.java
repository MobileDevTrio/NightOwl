package com.example.MobileDevTrio.nightowl;

import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NearbyPlacesParser extends Parser {
    private static final String RESULTS = "results";
    private static final String NEXT_PAGE_TOKEN = "next_page_token";
    private static final String NAME = "name";
    private static final String PLACE_ID = "place_id";
    private static final String TYPES = "types";
    private static final String GEOMETRY = "geometry";
    private static final String LOCATION = "location";
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String RATING = "rating";

    private PlaceType placeType;

    public NearbyPlacesParser(String placesUrl, PlaceType type) {
        super(placesUrl);
        placeType = type;
    }

    @Override
    public Pair<String, List<Place>> parseNearbyPlaces() {
        final String result = convertStreamToString(this.getInputStream());

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray(RESULTS);
            List<Place> nearbyPlacesList = new ArrayList<>();

            String nextPageToken = null;

            if (jsonObject.has(NEXT_PAGE_TOKEN)) {
                nextPageToken = jsonObject.getString(NEXT_PAGE_TOKEN);
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                nearbyPlacesList.add(buildNearbyPlace(jsonArray.getJSONObject(i)));
            }

            return new Pair<>(nextPageToken, nearbyPlacesList);

        } catch (JSONException e) {
            System.out.println("NearbyPlaces: EXCEPTION");
            e.printStackTrace();
        }

        return null;
    }

    private Place buildNearbyPlace(JSONObject jsonPlace) throws JSONException {
        Place place = new Place();

        place.setPlaceId(jsonPlace.getString(PLACE_ID));

        JSONObject locationJson = jsonPlace.getJSONObject(GEOMETRY).getJSONObject(LOCATION);
        LatLng location = new LatLng(locationJson.getDouble(LAT), locationJson.getDouble(LNG));
        place.setLocation(location);

        place.setName(jsonPlace.getString(NAME));

        if (jsonPlace.has(RATING))
            place.setRating(jsonPlace.getDouble(RATING));

        JSONArray typesArray = jsonPlace.getJSONArray(TYPES);
        /*
        String[] types = new String[typesArray.length()];
        for (int i = 0; i < typesArray.length(); i++) {
            types[i] = typesArray.getString(i);
        }
        place.setTypes(types); */

        switch(placeType) {
            case RESTAURANT: place.setSingleType("Restaurant");
                break;
            case BAR: place.setSingleType("Bar");
                break;
            case CLUB: place.setSingleType("Club");
                break;
        }

        return place;
    }

    /**
     * Convert an inputstream to a string.
     *
     * @param input inputstream to convert.
     * @return a String of the inputstream.
     */
    private String convertStreamToString(final InputStream input) {
        if (input == null) return null;

        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
        final StringBuilder stringBuilder = new StringBuilder();

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            Log.e("Places Error", e.getMessage());
        } finally {
            try {
                input.close();
                bufferedReader.close();
            } catch (IOException e) {
                Log.e("Places Error", e.getMessage());
            }
        }

        return stringBuilder.toString();
    }

    public void parsePlace(Place place) {
    }
}
