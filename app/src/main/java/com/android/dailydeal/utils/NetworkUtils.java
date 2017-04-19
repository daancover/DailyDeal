package com.android.dailydeal.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Daniel on 18/04/2017.
 */

public class NetworkUtils {
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static URL buildUrl(double lat, double lon) {
        // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-19.9078549,-43.9218217&radius=2000&type=grocery_or_supermarket&key=AIzaSyCo2hWaTEZKh5NkrtMtZ0PFsvTfdRW-tR8
        Uri builtUri = Uri.parse("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lon + "&radius=2000&type=grocery_or_supermarket&key=AIzaSyCo2hWaTEZKh5NkrtMtZ0PFsvTfdRW-tR8").buildUpon().build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
}
