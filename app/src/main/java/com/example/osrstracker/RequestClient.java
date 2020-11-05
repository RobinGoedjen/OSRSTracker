package com.example.osrstracker;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestClient {

    private static final String reqeustURL = "https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=";
    public static void fetchPlayer(String playerName) {
        try {

        } catch (Exception e) {
            Log.e("HTTP", e.getMessage());
        }

    }

}
