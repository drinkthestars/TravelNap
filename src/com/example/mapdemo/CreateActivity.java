package com.example.mapdemo;

import android.content.IntentSender;
import android.location.Location;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.nebula.smoothie.R;

public class CreateActivity extends FragmentActivity implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener {
	
	// Global constants
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
	LocationClient mLocationClient;
    EditText etLocation;
    Button btSubmit;
    
	private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setupViews();
		
		GoogleMap map = ((SupportMapFragment)
				getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		mLocationClient = new LocationClient(this, this, this);
        
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.setMyLocationEnabled(true);
		map.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
			
			@Override
			public boolean onMyLocationButtonClick() {
				Location mCurrentLocation;
				if (mLocationClient.isConnected()) {
					mCurrentLocation = mLocationClient.getLastLocation();
					Log.d("DEBUG", "CURRENT LOCATION: " + mCurrentLocation.toString());
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
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
	
	 /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
//    	if (mLocationClient.isConnected()) {
//    		if (mLocationClient.getLastLocation() != null) {
//    			Location mCurrentLocation = mLocationClient.getLastLocation()
//    			Log.d("DEBUG", "CURRENT LOCATION: " + mCurrentLocation.toString());
//    		}
//		} else {
//			Log.d("DEBUG", "Location Client NOT CONNECTED");
//		}
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
//        
//        LocationManager manager = (LocationManager) this
//                .getSystemService(Context.LOCATION_SERVICE);
//        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
//                new LocationListener() {
//                    @Override
//                    public void onStatusChanged(String provider, int status,
//                            Bundle extras) {
//                    }
//
//                    @Override
//                    public void onProviderEnabled(String provider) {
//                    }
//
//                    @Override
//                    public void onProviderDisabled(String provider) {
//                    }
//
//                    @Override
//                    public void onLocationChanged(final Location location) {
//                    }
//                });
//        mCurrentLocation = mLocationClient.getLastLocation();
//
//        TextView latTextView = (TextView) findViewById(R.id.latitude_text);
//        latTextView.setText(Double.toString(mCurrentLocation.getLatitude()));
//
//        TextView longTextView = (TextView) findViewById(R.id.longitude_text);
//        longTextView.setText(Double.toString(mCurrentLocation.getLongitude()));
    }
    
    public void onSubmitLocation(View v) {
    	String location = etLocation.getText().toString();
    	Log.d("DEBUG", "NEW LOC: " + location);
    	
    }
}
