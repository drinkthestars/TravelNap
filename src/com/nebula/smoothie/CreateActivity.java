package com.nebula.smoothie;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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
GooglePlayServicesClient.OnConnectionFailedListener,
CreateOptions.OnAlarmAddedListener,
CreateOptions.OnLocationSubmitListener,
LocationListener{

	// Global constants
	LocationClient mLocationClient;
	EditText etLocation;
	Button btSubmit;
	GoogleMap map;
	double currLat;
	double currLng;
	Location mCurrentLocation;
	LatLng dest = null;
	boolean mUpdatesRequested = true;
	NapAlarm newAlarm;
	MediaPlayer r;
	
	// Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    // Define an object that holds accuracy and frequency parameters
    LocationRequest mLocationRequest;
    SharedPreferences mPrefs;
    Editor mEditor;
	/*
	 * Define a request code to send to Google Play services
	 * This code is returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.replace(R.id.flFrame, new CreateOptions());
		// or ft.add(R.id.your_placeholder, new FooFragment());
		ft.commit();
		//setupViews();
		
		// Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        
        // Open the shared preferences
        mPrefs = getSharedPreferences("SharedPreferences",
                Context.MODE_PRIVATE);
        // Get a SharedPreferences editor
        mEditor = mPrefs.edit();
        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
        // Start with updates turned off
        mUpdatesRequested = true;
        
		map = ((SupportMapFragment)
				getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

		mLocationClient = new LocationClient(this, this, this);
		newAlarm = new NapAlarm();
		newAlarm.name = "MyFirstAlarm";
        newAlarm.save();
        
		NapAlarm na = new NapAlarm();
		na.name = "WHAT!!!!!!!";
		na.save();
		
		NapAlarm na2 = new NapAlarm();
		na2.name = "WHAT!!!!!!!12345";
		na2.save();
		
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.setMyLocationEnabled(true);
		map.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {

			@Override
			public boolean onMyLocationButtonClick() {
				return getCurrentLocation();
			}
		});
		
		final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
		if (mapView.getViewTreeObserver().isAlive()) {
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@SuppressWarnings("deprecation")
				// We use the new method when supported
				@SuppressLint("NewApi")	
				// We check which build version we are using.
				@Override
				public void onGlobalLayout() {
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					} else {
						mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					}
					// Send notification that map-loading has completed.
					getCurrentLocation();
				}
			});
		}
	}

	private boolean getCurrentLocation() {
		Log.d("DEBUG", "INSIDE GET CURRENT LOCATION");
		if (mLocationClient.isConnected()) {
			mCurrentLocation = mLocationClient.getLastLocation();

			//Set up current lat and lng
			currLat = mCurrentLocation.getLatitude();
			currLng = mCurrentLocation.getLongitude();
			
			//Updating alarm obj
			//newAlarm.setSource(new LatLng(currLat, currLng));
			Log.d("DEBUG", "Updated Alarm Object!");
			
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLat, currLng), 15));

		} else {
			Log.d("DEBUG", "Location Client NOT CONNECTED");
		}
		return false;
	}

	//Setup views
	public void setupViews() {
		CreateOptions myFrag = new CreateOptions();
		View view = myFrag.getView();
		etLocation = (EditText)view.findViewById(R.id.etLocation);
		btSubmit = (Button)view.findViewById(R.id.btSubmit);
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
		// If the client is connected
        if (mLocationClient.isConnected()) {
            /*
             * Remove location updates for a listener.
             * The current Activity is the listener, so
             * the argument is "this".
             */
       	mLocationClient.removeLocationUpdates(this);
        }
        /*
         * After disconnect() is called, the client is
         * considered "dead".
         */
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
		Log.d("DEBUG", "Connected");
		getCurrentLocation();
		
		// Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        // If already requested, start periodic updates
	}
	
	public void onAddAlarm() { 
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		Toast.makeText(this, "Alarm Started!", Toast.LENGTH_SHORT).show();
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.add(R.id.flFrame, new AlarmRunning());
		// or ft.add(R.id.your_placeholder, new FooFragment());
		ft.commit();
	}
	
    // Define the callback method that receives location updates
    @Override
    public void onLocationChanged(Location location) {
    	// Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d("DEBUG", msg);
        
        //need new lat and lng
        double newlat = location.getLatitude();
        double newLong = location.getLongitude();
        
        //Updating source in alarm
       // newAlarm.setSource(new LatLng(newlat, newLong));
        Log.d("DEBUG", "Updated source in alarm!");
        
        if (dest != null) {
	        String url = RouteDraw.makeURL(newlat, newLong, dest.latitude, dest.longitude);
	        JSONParser jParser = new JSONParser();
			String json = jParser.getStringJSONFromUrl(url);
			double dist;
			try {
				dist = JSONParser.getDistance(new JSONObject(json));
				//newAlarm.setDistance(dist);
				Log.d("DEBUG", "NEW DISTANCE: " + dist);
				if (dist <= 0.8) {
					//ring the alarm
					try {
						Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
						r = new MediaPlayer();
						r.setDataSource(this, alert);
						final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
						if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
							r.setAudioStreamType(AudioManager.STREAM_RING);
							r.setLooping(true);
							r.prepare();
							r.start();
						}
					} catch(Exception e) {
						e.printStackTrace();
					}   
					if (mLocationClient.isConnected()) {
						mLocationClient.removeLocationUpdates(this);
			        }
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage("You are " + dist + " miles from your destination!").setPositiveButton("I'm up!", dialogClickListener).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
    }
    
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
            case DialogInterface.BUTTON_POSITIVE:
            	r.stop();
                CreateActivity.this.finish();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                //No button clicked
                break;
            }
        }
    };
    
    @Override
    protected void onPause() {
        // Save the current setting for updates
        mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
        mEditor.commit();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
        /*
         * Get any previous setting for location updates
         * Gets "false" if an error occurs
         */
        if (mPrefs.contains("KEY_UPDATES_ON")) {
            mUpdatesRequested =
                    mPrefs.getBoolean("KEY_UPDATES_ON", false);

        // Otherwise, turn off location updates
        } else {
            mEditor.putBoolean("KEY_UPDATES_ON", false);
            mEditor.commit();
        }
    }
    
	public void loadAddressLatLon(String location){
		AsyncHttpClient client = new AsyncHttpClient();
		String apiReq = "http://maps.googleapis.com/maps/api/geocode/json?address=" + location.replace(" ", "+") + "&sensor=true";
		Log.d("DEBUG", "API REQ - " + apiReq);
		Log.d("DEBUG", "BEFORE CLIENT.GET()");
		client.get(apiReq, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject response) {
				Log.d("DEBUG", "Got address info!");
		        //Adding marker
				MarkerOptions mOpts = new MarkerOptions();
				dest = JSONParser.getLatLng(response);
				Log.d("DEBUG", "DEST: " + dest);
				mOpts.position(dest);
				map.addMarker(mOpts);

				//Updating destination in alarm
		        newAlarm.dest = dest;
		        newAlarm.save();

		        Log.d("DEBUG", "Updated destination in alarm!");
		        
				//Animating camera
				CameraUpdate newLoc = CameraUpdateFactory.newLatLngZoom(dest, 15);
				map.animateCamera(newLoc);
				
				//Drawing path
				new connectAsyncTask(RouteDraw.makeURL(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 
						dest.latitude, dest.longitude), map).execute();					

				Log.d("DEBUG", "CURRENT LAT: " + currLat + "   CURRENT LNG: " + currLng);
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				builder.include(dest);
				builder.include(new LatLng(currLat, currLng));
				LatLngBounds bounds = builder.build();
				map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
				Log.d("DEBUG", "CENTER IS: " + bounds.getCenter().toString());
			}
			@Override
			public void onFailure(Throwable arg0) {
				Log.d("DEBUG", "Api call failed! : " + arg0.toString());
			}
		});
	}

	public void onSubmitLocation(String location) {
		Log.d("DEBUG", "NEW LOC: " + location);
		loadAddressLatLon(location);
	}
	
	public void onSubmitLoc(View v) {
		EditText etLoc = (EditText) v.findViewById(R.id.etLocation);
		String location = etLoc.getText().toString();
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
			super.onPreExecute();
			progressDialog = new ProgressDialog(CreateActivity.this);
			progressDialog.setMessage("Fetching route, Please wait...");
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}
		@Override
		protected String  doInBackground(Void... params) {
			JSONParser jParser = new JSONParser();
			String json = jParser.getStringJSONFromUrl(url);
			try {
				double dist = JSONParser.getDistance(new JSONObject(json));
				//newAlarm.setDistance(dist);
				Log.d("DEBUG", "NEW DISTANCE: " + dist);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
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

	@Override
	public void onAlarmAdded() {
		onAddAlarm();
	}

	@Override
	public void onLocationSubmitted(String location) {
		onSubmitLocation(location);
	}
}
