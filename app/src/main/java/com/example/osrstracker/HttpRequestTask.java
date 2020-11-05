package com.example.osrstracker;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class HttpRequestTask extends AsyncTask<String, Void, String> {

    private static final String reqeustURL = "https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=";
    private Exception exception = null;

    @Override
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL(reqeustURL + strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            Log.i("HTTP", "Attempting HTTP request...");
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                int responseCode = urlConnection.getResponseCode();
                Log.i("HTTP", "Response Code: " + responseCode);
                if (responseCode != 200)
                    return null;

                String response = null;
                try (Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
                    response =  scanner.useDelimiter("\\A").next();
                }
                in.close();
                return response;
            } finally {
                urlConnection.disconnect();
            }

        }catch (Exception e){
            exception = e;
            Log.e("HTTP", e.getMessage());
        }
        return null;
    }

    protected void onPostExecute(String response) {
        if (exception != null || response == null) {
            //handle exception
            return;
        }

        Log.i("HTTP", response);
        //handle response
        //maybe call Event????
    }
}
