package com.example.MobileDevTrio.nightowl;

import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlaceParser extends Parser {
    private static final String RESULT = "result";
    private static final String PHONE = "formatted_phone_number";
    private static final String ADDRESS = "formatted_address";
    private static final String WEBSITE = "website";
    private static final String OPENINGHOURS = "opening_hours";
    private static final String PERIODS = "periods";
    private static final String CLOSE = "close";
    private static final String OPEN = "open";
    private static final String TIME = "time";

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

        // Places that are open 24/7 do not have "close"
        if (placeDetails.getJSONObject(OPENINGHOURS).getJSONArray(PERIODS).getJSONObject(0).has(CLOSE)) {
            place.setOpen247(false);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Calendar.DAY_OF_WEEK is 1,2,...,7
            String openingHours = placeDetails.getJSONObject(OPENINGHOURS).getJSONArray(PERIODS).getJSONObject(dayOfWeek).getJSONObject(OPEN).getString(TIME);
            String closingHours, formattedClosingHours;

            if (isCorrectClosingDay(openingHours)) {
                closingHours = placeDetails.getJSONObject(OPENINGHOURS).getJSONArray(PERIODS).getJSONObject(dayOfWeek).getJSONObject(CLOSE).getString(TIME);
                formattedClosingHours = timeFormatTo12Hour(closingHours);
            } else {
                int prevDay = (dayOfWeek + 6) % 7;
                closingHours = placeDetails.getJSONObject(OPENINGHOURS).getJSONArray(PERIODS).getJSONObject(prevDay).getJSONObject(CLOSE).getString(TIME);
                formattedClosingHours = timeFormatTo12Hour(closingHours);
            }

            place.setClosingHours(formattedClosingHours);

        } else {
            place.setOpen247(true);
        }
    }

    private boolean isCorrectClosingDay(String openingHours) {
        String sCurrentTime = new SimpleDateFormat("HHmm").format(Calendar.getInstance().getTime());     // Current Time in HHmm format
        double dCurrentTime = Double.parseDouble(sCurrentTime);
        double dOpeningHours = Double.parseDouble(openingHours);

        return (dCurrentTime > dOpeningHours);      //

    }

    private String timeFormatTo12Hour(String time) {

        SimpleDateFormat twentyFourHourFormat = new SimpleDateFormat("HHmm");
        SimpleDateFormat twelveHourFormat = new SimpleDateFormat("hh:mm a");

        try {
            Date twentyFourHourDate = twentyFourHourFormat.parse(time);
            return twelveHourFormat.format(twentyFourHourDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

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
