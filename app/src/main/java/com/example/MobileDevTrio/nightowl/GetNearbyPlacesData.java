package com.example.MobileDevTrio.nightowl;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    GoogleMap mMap;
    String googlePlacesData;    // A string of the JSON data returned from Places database

    String url;                 // A url containing the web service request to Places database
                                // we request the places that we want in this url

    List<HashMap<String, String>> nearbyPlacesList;

    public List<HashMap<String, String>> getNearbyPlacesList() {
        return nearbyPlacesList;
    }

    /**
     *  This requests and retrieves the the JSON data, then passes the data to onPostExecute()
     * @param objects
     * @return
     */
    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    /**
     *  The JSON data is a string.
     *  We pass this data to a DataParser object to parse the necessary details of a place and
     *  store those details in a HashMap.
     *  The HashMap is then passed to showNearbyPlaces()
     * @param s
     */
    // The JSON data (which is googlePlacesData) is a string.
    @Override
    protected void onPostExecute(String s) {

        nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(s);     // data is parsed and stored within nearbyPlacesList
        showNearbyPlaces(nearbyPlacesList);
    }

    /**
     *  This drops a marker on the map of each place that has been retrieved from the database.
     * @param nearbyPlacesList
     */
    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {

        if (nearbyPlacesList != null) {
            for (int i = 0; i < nearbyPlacesList.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = nearbyPlacesList.get(i);

                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                double latitude = Double.parseDouble(googlePlace.get("latitude"));
                double longitude = Double.parseDouble(googlePlace.get("longitude"));

                LatLng latLng = new LatLng(latitude, longitude);
                markerOptions
                        .position(latLng)
                        .title(placeName + " : " + vicinity);

                mMap.addMarker(markerOptions);

            }
        }


        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
    }
}
