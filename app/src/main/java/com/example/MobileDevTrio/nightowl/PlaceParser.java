package com.example.MobileDevTrio.nightowl;

import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class PlaceParser extends Parser {
    private static final String RESULT = "result";
    private static final String PHONE = "formatted_phone_number";
    private static final String ADDRESS = "formatted_address";
    private static final String WEBSITE = "website";

    public PlaceParser(String placeUrl) {
        super(placeUrl);
    }

    @Override
    public void parsePlace(Place place) {
        final String result = convertStreamToString(this.getInputStream());

        try {
            JSONObject jsonObject = new JSONObject(result);

            updatePlace((JSONObject) jsonObject.get(RESULT), place);

        } catch (JSONException e) {
            System.out.println("NearbyPlaces: EXCEPTION");
            e.printStackTrace();
        }
    }

    @Override
    public Pair<String, List<Place>> parseNearbyPlaces() {
        return null;
    }

    private void updatePlace(JSONObject placeDetails, Place place) throws JSONException {
        if (placeDetails.has(ADDRESS))
            place.setAddress(placeDetails.getString(ADDRESS));

        if (placeDetails.has(PHONE))
            place.setPhone(placeDetails.getString(PHONE));

        if (placeDetails.has(WEBSITE))
            place.setWebsite(placeDetails.getString(WEBSITE));
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

}
