package com.example.gustav.recipefinder;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by gustav on 9/12/18.
 */

public class recipeHandler extends AsyncTask<String, String, JSONObject> {

    private VolleyCallback c;

    public JSONObject search(String q, VolleyCallback callback) {
        c = callback;
        this.execute("q", q);
        return new JSONObject();
    }

    public JSONObject searchByURI(String q, VolleyCallback callback) {
        c = callback;
        this.execute("URI", q);
        return new JSONObject();
    }

    public interface VolleyCallback{
        JSONObject onSuccess(JSONObject result);
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            String query = params[1];
            HttpURLConnection urlConnection = null;
            URL url;
            switch(params[0]) {
                case "q":
                    url = new URL("https://api.edamam.com/search?q=" + java.net.URLEncoder.encode(query, "UTF-8") + "&app_id=333fe5c4&app_key=0f97bc389287ab1c563cbb9413daed8e&from=0&to=3");
                    break;
                case "URI":
                    url = new URL("https://api.edamam.com/search?r=" + java.net.URLEncoder.encode(query, "UTF-8") + "&app_id=333fe5c4&app_key=0f97bc389287ab1c563cbb9413daed8e");
                    break;
                default:
                    return new JSONObject();
            }
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            String jsonString = sb.toString();
            if(jsonString.charAt(0) == '[') { // Ifall den returnerar det som en array, ta bort det
                jsonString = jsonString.substring(1, jsonString.length()-1);
            }
            return new JSONObject(jsonString);
        }
        catch (Exception ex) {
            System.out.println("Search crash " + ex);
            return new JSONObject();
        }
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        c.onSuccess(result);
    }
}
