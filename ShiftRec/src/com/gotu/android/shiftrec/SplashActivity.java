package com.gotu.android.shiftrec;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;

import com.gotu.android.util.TinyDB;

public class SplashActivity extends Activity {
	private final int SPLASH_DISPLAY_LENGTH = 3000;
	private TinyDB shared_db;
	private ProgressBar loadSpinner;
	private int time = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		shared_db = new TinyDB(this);
		loadSpinner = (ProgressBar) findViewById(R.id.load_progress);
		loadSpinner.setIndeterminate(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		// Obtain the sharedPreference, default to true if not available
		boolean isSplashEnabled = sp.getBoolean("isSplashEnabled", true);

		if (isSplashEnabled) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// Finish the splash activity so it can't be returned to.
					SplashActivity.this.finish();
					time += 1;
					loadSpinner.setProgress(time);
					
					Intent i = new Intent(getApplicationContext(), HourGridActivity.class);
					startActivity(i);
				}
			}, SPLASH_DISPLAY_LENGTH);
		} else {
			// if the splash is not enabled, then finish the activity
			// immediately and go to main.
			finish();
			
			Intent i = new Intent(getApplicationContext(), HourGridActivity.class);
			startActivity(i);
		}

	}
}