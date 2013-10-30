package com.nebula.smoothie;

import com.google.android.gms.maps.model.LatLng;

/*
 * Model for an alarm
 */
public class NapAlarm {
	private LatLng source;
	private LatLng dest;
	private String transMode;
	private double distance;
	private double thresh;
	
	public NapAlarm() {
		
	}
	
	public NapAlarm(LatLng source, LatLng dest) {
		this.source = source;
		this.dest = dest;
	}
	
	public void setSource(LatLng source) {
		this.source = source;
	}
	
	public void setDest(LatLng dest) {
		this.dest = dest;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public void setTransMode(String transMode) {
		this.transMode = transMode;
	}
	
	public void setThresh(double thresh) {
		this.thresh = thresh;
	}
	
	public LatLng getSource() {
		return source;
	}
	
	public LatLng getDest() {
		return dest;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public String getTransMode() {
		return transMode;
	}
	
	public double getThresh() {
		return thresh;
	}
}
