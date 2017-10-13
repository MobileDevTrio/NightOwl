package com.example.MobileDevTrio.nightowl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *  Class to parse the JSON object retrieved from web request to Places database.
 *  Only the necessary details of a place are parsed and then returned as a List of HashMaps.
 */

// TODO: Need to make a second web request to retrieve more detailed information about a place
    // need to retrieve place phone, website
public class DataParser {

    /**
     *  This parses the JSON object and stores the necessary information of a place in a HashMap.
     *  Only the place name, vicinity, rating are retrievable.
     * @param googlePlaceJSON
     * @return
     */
    private HashMap<String, String> getPlace(JSONObject googlePlaceJSON) {

        //
        HashMap<String, String> googlePlacesMap = new HashMap<>();

        String placeId;
        String placeName = "-NA-";
        String phoneNumber = "-NA-";
        String vicinity = "-NA-";
        String rating = "-NA-";
        String website = "-NA-";
        String openNow;

        String latitude;
        String longitude;

        try {
            if (!googlePlaceJSON.isNull("name")) {
                placeName = googlePlaceJSON.getString("name");
            }

            if (!googlePlaceJSON.isNull("formatted_phone_number")) {
                phoneNumber = googlePlaceJSON.getString("formatted_phone_number");
            }

            if (!googlePlaceJSON.isNull("vicinity")) {
                vicinity = googlePlaceJSON.getString("vicinity");
            }

            if (!googlePlaceJSON.isNull("rating")) {
                rating = googlePlaceJSON.getString("rating");
            }

            if (!googlePlaceJSON.isNull("website")) {
                website = googlePlaceJSON.getString("website");
            }

            placeId = googlePlaceJSON.getString("place_id");
            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");

            // We only save places that are currently open at the moment the user uses the app
           // openNow = Boolean.toString(googlePlaceJSON.getJSONObject("opening_hours").getBoolean("open_now"));
           // if (Boolean.parseBoolean(openNow)) {
                googlePlacesMap.put("place_id", placeId);
                googlePlacesMap.put("place_name", placeName);
                googlePlacesMap.put("phone_number", phoneNumber);
                googlePlacesMap.put("vicinity", vicinity);
                googlePlacesMap.put("rating", rating);
                googlePlacesMap.put("website", website);
                googlePlacesMap.put("latitude", latitude);
                googlePlacesMap.put("longitude", longitude);
            //}

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlacesMap;
    }

    /**
     *  A JSONArray consists of JSONObject (which will be a place).
     *  We pass the JSONObject to getPlace(), which parses the necessary details of a place and then
     *  store a List of HashMap
     * @param jsonArray
     * @return
     */
    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {

        int count = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placeMap = null;

        for (int i = 0; i < count; i++) {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placesList;
    }

    /**
     *  The JSON data retrieved from the Places database is returned as an string.
     *  This turns the string into a JSONArray object to be eventually parsed with getPlace()
     * @param jsonData
     * @return
     */
    public List<HashMap<String, String>> parse(String jsonData) {

        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }
}
