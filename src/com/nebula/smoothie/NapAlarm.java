package com.nebula.smoothie;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;

/*
 * Model for an alarm
 */
@Table(name = "NapAlarms")
public class NapAlarm extends Model{
	@Column(name = "Name")
	public String name;
	
	@Column(name = "Dest")
	public LatLng dest;
	
//	@Column(name = "Name")
//	private String transMode;
	
//	@Column(name = "Name")
//	private double thresh;
//	
	public NapAlarm() {
		super();
	}
	
	public NapAlarm(String name, LatLng dest) {
		super();
		this.name = name;
		this.dest = dest;
	}
	
	public void setDest(LatLng dest) {
		this.dest = dest;
	}
	
//	public void setTransMode(String transMode) {
//		this.transMode = transMode;
//	}
//	
//	public void setThresh(double thresh) {
//		this.thresh = thresh;
//	}

	public LatLng getDest() {
		return dest;
	}
	
	public static NapAlarm getAlarm() {
		return new Select().from(NapAlarm.class).executeSingle();
	}
	
//	public String getTransMode() {
//		return transMode;
//	}
//	
//	public double getThresh() {
//		return thresh;
//	}
}
