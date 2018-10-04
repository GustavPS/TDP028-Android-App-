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
import java.util.Arrays;


/**
 * Created by gustav on 9/12/18.
 */

public class recipeHandler extends AsyncTask<String, String, JSONObject> {

    private VolleyCallback c;

    public JSONObject search(String query, VolleyCallback callback) {
        c = callback;
        try {
            String url = "https://api.edamam.com/search?q=" + java.net.URLEncoder.encode(query, "UTF-8") + "&app_id=333fe5c4&app_key=0f97bc389287ab1c563cbb9413daed8e&from=0&to=3";
            this.execute(url);
        } catch(Exception ex) {
            System.out.println("URL crash ( search)");
        }
        return new JSONObject();
    }

    public void search_with_calories(String query, String cal_start, String cal_end, String from, String to, VolleyCallback callback) {
        c = callback;
        try {
            String url = "https://api.edamam.com/search?q=" + java.net.URLEncoder.encode(query, "UTF-8") + "&app_id=333fe5c4&app_key=0f97bc389287ab1c563cbb9413daed8e&from=" + from + "&to=" + to + "&calories=" + cal_start + "-" + cal_end;
            this.execute(url);
        } catch(Exception ex) {
            System.out.println("URL crash ( search)");
            ex.printStackTrace();
        }
    }

    public JSONObject searchByURI(String query, VolleyCallback callback) {
        c = callback;
        try {
            String url = "https://api.edamam.com/search?r=" + java.net.URLEncoder.encode(query, "UTF-8") + "&app_id=333fe5c4&app_key=0f97bc389287ab1c563cbb9413daed8e";
            this.execute(url);
        } catch(Exception ex) {
            System.out.println("URL crash ( search)");
        }
        return new JSONObject();
    }

    public interface VolleyCallback{
        JSONObject onSuccess(JSONObject result);
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            HttpURLConnection urlConnection = null;
            URL url = new URL(params[0]);

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
            ex.printStackTrace();
            return new JSONObject();
        }
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        c.onSuccess(result);
    }
}
