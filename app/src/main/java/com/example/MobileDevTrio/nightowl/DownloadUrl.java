package com.example.MobileDevTrio.nightowl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *  Class to retrieve data from Google Places database through http connection.
 *  The data returned is in JSON format.
 */
public class DownloadUrl {

    /**
     *  This creates an http connection using the passed URL
     * @param myUrl
     * @return
     * @throws IOException
     */
    public String readUrl(String myUrl) throws IOException {
        System.out.println(myUrl);
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(myUrl);

            // An http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            // Read data from the url
            inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            data = stringBuffer.toString();
            bufferedReader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
            urlConnection.disconnect();
        }

        return data;
    }
}
