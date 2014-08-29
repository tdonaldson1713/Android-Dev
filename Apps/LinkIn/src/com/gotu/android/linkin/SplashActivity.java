package com.gotu.android.linkin;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gotu.android.linkin.util.TinyDB;
import com.gotu.android.linkin.util.URLShortener;

public class SplashActivity extends Activity {

	// Set the display time, in milliseconds (or extract it out as a
	// configurable parameter)
	private final int SPLASH_DISPLAY_LENGTH = 3000;
	static String address="https://www.googleapis.com/urlshortener/v1/url";
	private String browser_share = "";
	public static final String SHARE = "LINK";
	public static final String SOCIAL_ADAPTER = "ADAPTER";
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
		final boolean canShare = hasLogin();
		browser_share = getIntent().getStringExtra(Intent.EXTRA_TEXT);

		if (browser_share != null) {
			new URLShort().execute();
		}

		if (isSplashEnabled) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// Finish the splash activity so it can't be returned to.
					SplashActivity.this.finish();
					time += 1;
					loadSpinner.setProgress(time);
					if (browser_share != null && canShare) {
						loadShare();
					} else if (browser_share == null || !canShare) {
						loadSocial();
					}
				}
			}, SPLASH_DISPLAY_LENGTH);
		} else {
			// if the splash is not enabled, then finish the activity
			// immediately and go to main.
			finish();

			if (browser_share != null && canShare) {
				loadShare();
			} else if (browser_share == null || !canShare) {
				loadSocial();
			}
		}
	}

	private void loadSocial() {
		Intent mainIntent = new Intent(getApplicationContext(), SocialActivity.class);
		mainIntent.putExtra(SHARE, browser_share);
		startActivity(mainIntent);
	}

	private void loadShare() {
		Intent shareIntent = new Intent(getApplicationContext(), ShareActivity.class);
		shareIntent.putExtra(SHARE, browser_share);
		startActivity(shareIntent);
	}
	/*
	 * Checks to see if the user has logged into any social networks through Link It.
	 */
	private boolean hasLogin() {
		boolean facebookAuthorized = shared_db.getBoolean(SocialActivity.FACEBOOK_PASS);
		boolean twitterAuthorized = shared_db.getBoolean(SocialActivity.TWITTER_PASS);
		boolean googlePlusAuthorized = shared_db.getBoolean(SocialActivity.GOOGLE_PLUS_PASS);
		Log.d("com.gotu.android.linkin IS_AUTHORIZED", "splash Google+ " + String.valueOf(googlePlusAuthorized));
		Log.d("com.gotu.android.linkin IS_AUTHORIZED", "splash Facebook " + String.valueOf(facebookAuthorized));
		Log.d("com.gotu.android.linkin IS_AUTHORIZED", "splash Twitter " + String.valueOf(twitterAuthorized));

		if (facebookAuthorized || twitterAuthorized || googlePlusAuthorized) {
			return true;
		} else {
			return false;
		}
	}

	private class URLShort extends AsyncTask<String, String, JSONObject> {
		String longUrl;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			longUrl = browser_share;
		}
		@Override
		protected JSONObject doInBackground(String... args) {
			URLShortener jParser = new URLShortener();
			JSONObject json = jParser.getJSONFromUrl(address,longUrl);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if (json != null){
					browser_share = json.getString("id");
				}else{
					Toast.makeText(getApplicationContext(),R.string.unable_to_shorten, Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
