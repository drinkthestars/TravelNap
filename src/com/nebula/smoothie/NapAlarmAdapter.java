package com.nebula.smoothie;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.activeandroid.query.Select;

public class NapAlarmAdapter extends ArrayAdapter<NapAlarm>{

	public NapAlarmAdapter(Context context, List<NapAlarm> NapAlarms) {
		super(context, 0, NapAlarms);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//take the list of NapAlarms and then put the object stuff in
		//the NapAlarm_item layout
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.napalarm_item, parent, false);
		}
		
		//Get NapAlarm from position
		//NapAlarm alarm = getItem(position);

		
		List<NapAlarm> alarm = new Select().all().from(com.nebula.smoothie.NapAlarm.class).execute();
		if (alarm != null) {
			Log.d("DEBUG", "Alarm QUERY: " + alarm);
		}
		String name = "Home";
		//user name
		TextView nameView = (TextView) convertView.findViewById(R.id.tvName);
		String formattedName = "<b>" + name + "</b>";
        nameView.setText(Html.fromHtml(formattedName));
        
        //Address of NapAlarm
        TextView bodyView = (TextView) convertView.findViewById(R.id.tvBody);
        bodyView.setText(Html.fromHtml("185 Estancia Drive"));
		
		return convertView;
	}
}
