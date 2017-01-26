package com.example.android.imgurtestapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.example.android.imgurtestapp.ImgurActivity.LOG_TAG;


public class QueryUtils {

    private QueryUtils() {}  //если в классе все методы статические, конструктор не нужен,
    // создавать объект точно не надо никогда

    public static ArrayList<ImgurImage> fetchImages(String query, String accessToken) {
        URL url = createUrl(query);
        String jsonResponse = makeHttpRequest(url, accessToken);
        return extractImages(jsonResponse);
    }

    public static URL createUrl(String queryUrl) {
        URL url = null;
        try {
            url = new URL(queryUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url, String accessToken) {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try{
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.addRequestProperty("Authorization", "Bearer " + accessToken);
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);

            } else {
                Log.e(LOG_TAG, "Error response code: " + httpURLConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<ImgurImage> extractImages(String jsonR) {
        ArrayList<ImgurImage> resultArray = new ArrayList<ImgurImage>();

        try{
            JSONObject reader = new JSONObject(jsonR);
            JSONArray images = reader.getJSONArray("data");

            for (int i = 0; i< images.length(); ++i) {
                String currentImageUrl = images.getJSONObject(i).getString("link");
                if (images.getJSONObject(i).get("title") != null) {
                    String title = images.getJSONObject(i).getString("title");
                    resultArray.add(new ImgurImage(currentImageUrl, title));
                } else {
                    resultArray.add(new ImgurImage(currentImageUrl, ""));
                }
            }
        } catch (JSONException j) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", j);
        }

        return resultArray;
    }
    
    public static Bitmap imageFromUrl(String url) {
        Bitmap image = null;

        try {

            InputStream in = new java.net.URL(url).openStream();
            image = BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            String err = (e.getMessage()==null)?"Image from URL failed":e.getMessage();
            Log.e("Error", err);
            e.printStackTrace();
        }
        return image;
    }



}
