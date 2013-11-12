package com.nebula.smoothie;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends Activity {
	public final int REQUEST_CODE = 7;
	NapAlarmAdapter adapter;
	ArrayList<NapAlarm> napAlarms;
	ListView lvAlarms;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		napAlarms = new ArrayList<NapAlarm>();
		adapter = new NapAlarmAdapter(getBaseContext(), napAlarms);
		lvAlarms = (ListView) findViewById(R.id.lvAlarms);
		
		
		adapter.addAll(NapAlarm.getAlarm());
		lvAlarms.setAdapter(adapter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onAddView(MenuItem m) {
		Intent i = new Intent(this, CreateActivity.class);
		startActivity(i);
	}
	
}
