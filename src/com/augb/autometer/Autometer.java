package com.augb.autometer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.augb.autometer.activity.Settings;
import com.augb.autometer.util.Util;

public class Autometer extends Activity implements GpsNotificationListener {
	double distanceValue, fareValue;
	long waitingtimeValue;

	Handler handler = new Handler();

	private TextView waitingTimeView, DistanceView, fareView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Typeface myTypeface = Typeface.createFromAsset(this.getAssets(),
				"DS-DIGIB.TTF");
		waitingTimeView = (TextView) findViewById(R.id.WaitingTime);
		waitingTimeView.setTypeface(myTypeface);
		DistanceView = (TextView) findViewById(R.id.Distance);
		DistanceView.setTypeface(myTypeface);
		fareView = (TextView) findViewById(R.id.Fare);
		fareView.setTypeface(myTypeface);
		ToggleButton VacantButton = (ToggleButton) findViewById(R.id.VacantHiredButton);
		VacantButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {// do the toggle thing
				if (v instanceof ToggleButton) {
					ToggleButton tb = (ToggleButton) v;
					if (tb.isChecked()) {
						// its in hiring mode, change to vacant
						handler.post(new Runnable() {
							@Override
							public void run() {
								stopMeter();
								init();
							}
						});
					} else {
						GpsLocationManager locManager = GpsLocationManager
								.getGPSLocationManger();
						locManager.start(Autometer.this, Autometer.this);
						Log.d("test", "start");
					}
				}
			}
		});
		Button stopButton = (Button) findViewById(R.id.StopButton);
		stopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						stopMeter();
					}
				});
			}
		});
		init();
	}

	private void stopMeter() {
		ToggleButton VacantButton = (ToggleButton) findViewById(R.id.VacantHiredButton);
		VacantButton.setChecked(false);
		// TODO Stop capturing data.
		GpsLocationManager locManager = GpsLocationManager.getGPSLocationManger();
		locManager.stop();
		Log.d("test", "stop");
	}

	public void init() {
		distanceValue = fareValue = 0.0;
		waitingtimeValue = 0;
		waitingTimeView.setText("--.--");
		DistanceView.setText("---.-");
		fareView.setText("---.--");

	}
	
	@Override
	public void onUpdate(final GpsLocation loc) {
		Log.d("test", "got update: " + loc.toString());
		handler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				distanceValue = loc.getTotalDistance() / 1000;
				waitingtimeValue = loc.getTotalWaitingDT();// in millisecs
				waitingTimeView.setText(String.format("%d:%d", waitingtimeValue
						/ (1000 * 60), waitingtimeValue / (1000)));
				DistanceView.setText(String.format("%.2f", distanceValue));
				fareValue = Util.getFareFromDistance(distanceValue,
						Autometer.this);
				fareView.setText(String.format("%.2f", fareValue));

			}
		});

	}

	/**
	 * Display the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inFlater = getMenuInflater();
		inFlater.inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * When the menu item is clicked
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, Settings.class));

		}
		return false;
	}
}