package com.nebula.smoothie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static JSONObject jResponse = null;
    static String json = "";
    // constructor
    public JSONParser() {
    }
    public String getStringJSONFromUrl(String url) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();           

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            json = sb.toString();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;

    }
    
    public void getJSONFromUrl(String url) {
    	AsyncHttpClient client = new AsyncHttpClient();
    	Log.d("DEBUG", "BEFORE CLIENT.GET()");
		client.get(url, new JsonHttpResponseHandler() {
			@Override
			public void onFailure(Throwable arg0) {
				Log.d("DEBUG", "Call to api for directions failed! - " + arg0.toString());
			}
			
			@Override
			public void onSuccess(JSONObject jsonObj) {
				jResponse = jsonObj;
			}
		});
    }
    
    public static double getDistance(JSONObject jsonObj) {
    	Log.d("DEBUG", "======== INSIDE GET DISTANCE! ========");
    	double dist = -1;
    	JSONObject jsonData;
		try {
			jsonData = new JSONObject(json);
			JSONArray jsonArray = jsonData.getJSONArray("routes");
			JSONArray jsonLeg = jsonArray.getJSONObject(0).getJSONArray("legs");
			String distance = jsonLeg.getJSONObject(0).getJSONObject("distance").getString("text");
			Log.d("DEBUG", "STRING DISTANCE: " + distance.replaceAll("[^\\d.]", ""));
			dist = Double.parseDouble(distance.replaceAll("[^\\d.]", ""));
			Log.d("DEBUG", "Distance: " + dist);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dist;
    }
    
    public static LatLng getLatLng(JSONObject jsonObj) {
    	LatLng latlng = null;
    	JSONArray locInfo;
		try {
			locInfo = jsonObj.getJSONArray("results");
			JSONObject objLocInfo = locInfo.getJSONObject(0);
			double lat = objLocInfo.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
			double lng = objLocInfo.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
			latlng = new LatLng(lat, lng);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return latlng;
    }
}