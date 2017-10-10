package com.example.MobileDevTrio.nightowl;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by KhoaPham on 10/9/17.
 */

public class GetNeabyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;

    @Override
    protected String doInBackground(Object... objects) {
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
