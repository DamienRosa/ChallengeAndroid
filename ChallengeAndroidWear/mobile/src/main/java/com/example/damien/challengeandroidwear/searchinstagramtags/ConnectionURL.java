package com.example.damien.challengeandroidwear.searchinstagramtags;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ConnectionURL {

    public static String RequestWithURL(String request_url) {
        String data = "";

        InputStream inputStream = null;
        HttpsURLConnection urlConnection = null;

        try {
            URL url = new URL(request_url);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer sb = new StringBuffer();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();
        } catch (MalformedURLException e) {
            Log.e("Exception while downloading url", e.toString());
        } catch (IOException e) {
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            urlConnection.disconnect();
        }
        return data;
    }
}
