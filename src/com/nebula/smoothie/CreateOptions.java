package com.nebula.smoothie;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class CreateOptions extends Fragment {
	OnAlarmAddedListener mCallback;
	OnLocationSubmitListener mLocCallback;
	Button btAddAlarm;
	EditText etLocation;
	Button btSubmit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.create_alarm_options, container, false);
		btAddAlarm = (Button) view.findViewById(R.id.btAddLoc);
		etLocation = (EditText) view.findViewById(R.id.etLocation);
		btSubmit = (Button) view.findViewById(R.id.btSubmit);
		
		btAddAlarm.setVisibility(View.INVISIBLE);
		btAddAlarm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onAlarmAddedFrag(v);
			}
		});
		
		btSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSubmitLocation(v);
				btAddAlarm.setVisibility(View.VISIBLE);
			}
		});
		
		return view;
	}

	// Container Activity must implement this interface
	public interface OnAlarmAddedListener {
		public void onAlarmAdded();
	}

	// Container Activity must implement this interface
	public interface OnLocationSubmitListener {
		public void onLocationSubmitted(String location);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnAlarmAddedListener) activity;
			mLocCallback = (OnLocationSubmitListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnAlarmAddedListener and OnLocationSubmitListener");
		}
	}

	public void onAlarmAddedFrag(View v) {
		// Send the event to the host activity
		mCallback.onAlarmAdded();
	}

	public void onSubmitLocation(View v) {
		String location = etLocation.getText().toString();
		mLocCallback.onLocationSubmitted(location);
		btAddAlarm.setVisibility(1);
	}
}
