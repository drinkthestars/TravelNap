package com.nebula.smoothie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class CreateActivity extends FragmentActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {

	// Global constants
	LocationClient mLocationClient;
	EditText etLocation;
	Button btSubmit;
	GoogleMap map;
	double currLat;
	double currLng;
	Location mCurrentLocation;

	/*
	 * Define a request code to send to Google Play services
	 * This code is returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setupViews();

		map = ((SupportMapFragment)
				getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

		mLocationClient = new LocationClient(this, this, this);

		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.setMyLocationEnabled(true);
		map.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {

			@Override
			public boolean onMyLocationButtonClick() {
				if (mLocationClient.isConnected()) {
					mCurrentLocation = mLocationClient.getLastLocation();
					
					//Set up current lat and lng
					currLat = mCurrentLocation.getLatitude();
					currLng = mCurrentLocation.getLongitude();
					
				} else {
					Log.d("DEBUG", "Location Client NOT CONNECTED");
				}
				return false;
			}
		});
		
		
	}

	//Setup views
	public void setupViews() {
		etLocation = (EditText) findViewById(R.id.etLocation);
		btSubmit = (Button) findViewById(R.id.btSubmit);
	}

	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onStop();
	}

	/*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects.
		 * If the error has a resolution, try sending an Intent to
		 * start a Google Play services activity that can resolve
		 * error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the
			 * user with the error.
			 */
			Toast.makeText(this, connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * Called by Location Services if the connection to the
	 * location client drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	/*
	 * Called by Location Services when the request to connect the
	 * client finishes successfully. At this point, you can
	 * request the current location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle) {
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		
		if (mLocationClient.isConnected()) {
			mCurrentLocation = mLocationClient.getLastLocation();
			
			//Set up current lat and lng
			currLat = mCurrentLocation.getLatitude();
			currLng = mCurrentLocation.getLongitude();
			
		} else {
			Log.d("DEBUG", "Location Client NOT CONNECTED");
		}
	}

	public void loadAddressLatLon(String location){
		AsyncHttpClient client = new AsyncHttpClient();
		String apiReq = "http://maps.googleapis.com/maps/api/geocode/json?address=" + location.replace(" ", "+") + "&sensor=true";
		Log.d("DEBUG", "Api request URI: " + apiReq);


		Log.d("DEBUG", "BEFORE CLIENT.GET()");
		client.get(apiReq, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject response) {
				Log.d("DEBUG", "Got address info!");
				JSONArray locInfo = null;
				try {
					locInfo = response.getJSONArray("results");
					JSONObject objLocInfo = locInfo.getJSONObject(0);
					double lat = objLocInfo.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
					double lng = objLocInfo.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
					Log.d("DEBUG", "Lat: " + lat + "   Lng: " + lng);

					//Adding marker
					MarkerOptions mOpts = new MarkerOptions();
					mOpts.position(new LatLng(lat, lng));
					map.addMarker(mOpts);

					//Animating camera
					CameraUpdate newLoc = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15);
					map.animateCamera(newLoc);
					new connectAsyncTask(RouteDraw.makeURL(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), lat, lng), map).execute();					
					
					Log.d("DEBUG", "CURRENT LAT: " + currLat + "   CURRENT LNG: " + currLng);
					LatLngBounds.Builder builder = new LatLngBounds.Builder();
					builder.include(new LatLng(lat, lng));
					builder.include(new LatLng(currLat, currLng));
					LatLngBounds bounds = builder.build();
					map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
					Log.d("DEBUG", "CENTER IS: " + bounds.getCenter().toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		
			@Override
			public void onFailure(Throwable arg0) {
				Log.d("DEBUG", "Api call failed! : " + arg0.toString());
			}
		});
	}

	public void onSubmitLocation(View v) {
		String location = etLocation.getText().toString();
		Log.d("DEBUG", "NEW LOC: " + location);
		loadAddressLatLon(location);
	}

	private class connectAsyncTask extends AsyncTask<Void, Void, String>{
		private ProgressDialog progressDialog;
		String url;
		private RouteDraw rdraw;
		GoogleMap map;
		connectAsyncTask(String urlPass, GoogleMap map){
			url = urlPass;
			this.map = map;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(CreateActivity.this);
			progressDialog.setMessage("Fetching route, Please wait...");
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}
		@Override
		protected String doInBackground(Void... params) {
			JSONParser jParser = new JSONParser();
			String json = jParser.getJSONFromUrl(url);
			return json;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);   
			progressDialog.hide();        
			if(result!=null){
				rdraw = new RouteDraw(map);
				rdraw.drawPath(result);

			}
		}
	}
}
