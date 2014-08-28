package com.gotu.android.linkin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.PendingCall;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.gotu.android.linkin.social.Twitter;
import com.gotu.android.linkin.util.PrivacyPolicyDialog;
import com.gotu.android.linkin.util.RoundedImages;
import com.gotu.android.linkin.util.TinyDB;
import com.gotu.android.unused_files.Facebooks;

public class SocialActivity extends Activity implements ConnectionCallbacks, 
OnConnectionFailedListener, OnClickListener {
	private static final String TAG = "com.gotu.android.linkin.SocialActivity";
	public static final String GOOGLE_PLUS_PASS = "GooglePlusPass";
	public static final String GOOGLE_PLUS_NAME = "GooglePlusName";
	public static final String TWITTER_PASS = "TwitterPass";
	public static final String TWITTER_NAME = "TwitterName";
	public static final String FACEBOOK_PASS = "FacebookPass";
	public static final String FACEBOOK_NAME = "FacebookName";
	public static final String PRIVACY_POLICY = "PrivacyPolicyShown";
	public static final int LOAD_TUTORIAL = 1;


	// Non-specific variables
	ImageButton imgShare;
	private TinyDB sharedPrefDB = null;
	private boolean isConnected = true;
	private static boolean isPolicyShown = false;
	private String browser_share;

	// Beginning of Google+ Variables
	private static final int RC_SIGN_IN = 0;
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private boolean mSignInClicked;
	public static boolean canLoadApi = false;
	public static boolean googlePlusAuthorized;
	private ConnectionResult mConnectionResult = null;
	static ImageButton gDisconnect;
	static ImageButton gConnect;
	ImageButton gProfBtn;
	static TextView gName;
	static LinearLayout gBar;
	// End of Google+ Variables

	// Beginning of Twitter Variables
	private Twitter twitter;
	public static boolean twitterAuthorized = false;
	static ImageButton tConnect;
	static ImageButton tDisconnect;
	ImageButton tProfBtn;
	static TextView tName;
	static LinearLayout tBar;
	// End of Twitter Variables

	// Beginning of Facebook Variables
	private UiLifecycleHelper uiHelper;
	private Facebooks facebook;
	public static boolean facebookAuthorized = false;
	static ImageButton fConnect;
	static ImageButton fDisconnect;
	ImageButton fProfBtn;
	static TextView fName;
	static LinearLayout fBar;
	private static String facebook_name;
	private String facebookVersion;
	private Session.StatusCallback callback = new Session.StatusCallback() {

		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	// End of Facebook Variables

	@Override
	protected void onStart() {
		mGoogleApiClient.connect();

		super.onStart();
	}

	@Override
	protected void onStop() {
		savePreferences();

		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}

		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();

		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}


	@Override
	protected void onPause() {
		savePreferences();
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onBackPressed() {
		if (isConnected) {
			savePreferences();
		}
		super.onBackPressed();
	}
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.privacy_policy_menu:
			PrivacyPolicyDialog.PrivacyPolicy(SocialActivity.this).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}*/

	private void savePreferences() {
		if (facebookAuthorized) {
			sharedPrefDB.putBoolean(FACEBOOK_PASS, true);
			sharedPrefDB.putString(FACEBOOK_NAME, fName.getText().toString());
		} else {
			sharedPrefDB.putBoolean(FACEBOOK_PASS, false);
			sharedPrefDB.putString(FACEBOOK_NAME, "Not logged in");
		}

		if (twitter != null && twitter.isAuthorized()) {
			sharedPrefDB.putBoolean(TWITTER_PASS, true);
			sharedPrefDB.putString(TWITTER_NAME, tName.getText().toString());
		} else {
			sharedPrefDB.putBoolean(TWITTER_PASS, false);
			sharedPrefDB.putString(TWITTER_NAME, "Not logged in");
		}

		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			sharedPrefDB.putBoolean(GOOGLE_PLUS_PASS, true);
			sharedPrefDB.putString(GOOGLE_PLUS_NAME, gName.getText().toString());
		} else {
			sharedPrefDB.putBoolean(GOOGLE_PLUS_PASS, false);
			sharedPrefDB.putString(GOOGLE_PLUS_NAME, "Not logged in");
		}
	}

	private void getPreferences() {
		facebookAuthorized = sharedPrefDB.getBoolean(FACEBOOK_PASS);
		twitterAuthorized = sharedPrefDB.getBoolean(TWITTER_PASS);
		googlePlusAuthorized = sharedPrefDB.getBoolean(GOOGLE_PLUS_PASS);
		isPolicyShown = sharedPrefDB.getBoolean(PRIVACY_POLICY);
	}

	private void getHashKey() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.gotu.android.linkin", 
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("TEST", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social);

		sharedPrefDB = new TinyDB(this);
		getPreferences();

		//getHashKey();

		browser_share = getIntent().getStringExtra(SplashActivity.SHARE);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (mWifi != null) {
			Log.d(TAG, String.valueOf(mWifi.isConnected()));
		}
		if (mMobile != null) {
			Log.d(TAG, String.valueOf(mMobile.isConnected()));
		}
		
		if (!mWifi.isConnected() && !mMobile.isConnected()) {
			isConnected = false;
			Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
		}
		
		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView)this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		imgShare = (ImageButton) findViewById(R.id.imgShare);
		imgShare.setOnClickListener(this);		

		setUpFacebook();
		setUpTwitter();
		setUpGooglePlus();

		//roundLinearLayoutCorners();

		if (!isPolicyShown) {
			PrivacyPolicyDialog.PrivacyPolicy(this).show();
			sharedPrefDB.putBoolean(PRIVACY_POLICY, true);
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) { // Google+ Function
		mSignInClicked = false;
		/*Toast.makeText(this, "User is connected", Toast.LENGTH_SHORT).show();*/

		if (Plus.AccountApi.getAccountName(mGoogleApiClient) != null) {
			Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
			String personName = currentPerson.getDisplayName();
			gName.setText(getString(R.string.google_name_logged, personName));
			updateGooglePlusUi_SignIn();	
			googlePlusAuthorized = true;
		}

		sharedPrefDB.putBoolean(GOOGLE_PLUS_PASS, true);	
	}

	@Override
	public void onConnectionSuspended(int cause) { // Google+ Function
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) { // Google+ Function
		if (!mIntentInProgress) {
			mConnectionResult = result;

			if (mSignInClicked) {
				resolveSignInError();
			}
		}
	}

	private void resolveSignInError() { // Google+ Function
		if (mConnectionResult != null && mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}


	@Override
	public void onClick(View view) {


		switch(view.getId()) {

		case R.id.imgShare:
			if (facebookAuthorized || twitterAuthorized || googlePlusAuthorized) {
				savePreferences();

				Intent share = new Intent(getApplicationContext(), ShareActivity.class);
				share.putExtra(SplashActivity.SHARE, browser_share);
				startActivity(share);
			} else {
				Toast.makeText(getApplicationContext(), R.string.not_logged_in, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.facebookConnect:
			/*new Thread(new Runnable() {
				@Override
				public void run() {
					facebook.authorize();
				}
			}).start();*/

			loginToFacebook();
			break;
		case R.id.facebookDisconnect:
			break; 
		case R.id.twitterConnect:	
			new Thread(new Runnable() {
				@Override
				public void run() {
					twitter.authorize();
				}
			}).start();
			break;
		case R.id.twitterDisconnect:
			// LogoutWarning.DialogLogoutWarning(this, Provider.TWITTER.toString(), twitter, null);
			twitter.logout();
			updateTwitterUi_SignOut();
			break;
		case R.id.googlePlusConnect:
			if (!mGoogleApiClient.isConnecting()) {
				mSignInClicked = true;
				resolveSignInError();

			}

			Toast.makeText(this, R.string.gpLogin, Toast.LENGTH_SHORT).show();

			break;
		case R.id.googlePlusDisconnect:
			// LogoutWarning.DialogLogoutWarning(this, getString(R.string.google_plus),null, mGoogleApiClient);
			if (mGoogleApiClient.isConnected()) {
				Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
				mGoogleApiClient.disconnect();
				updateGooglePlusUi_SignOut();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {

		uiHelper.onActivityResult(requestCode, responseCode, intent, new FacebookDialog.Callback() {

			@Override
			public void onError(PendingCall pendingCall, Exception error, Bundle data) {
				Log.e("TEST", String.format("Error: %s", error.toString()));
			}

			@Override
			public void onComplete(PendingCall pendingCall, Bundle data) {
				Log.i("TEST", "Success!");
			}
		});

		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}

		if (requestCode == LOAD_TUTORIAL) {
			if (responseCode == RESULT_OK) {
				imgShare = (ImageButton) findViewById(R.id.imgShare);
				imgShare.setOnClickListener(this);

				sharedPrefDB = new TinyDB(this);
				getPreferences();

				setUpFacebook();
				setUpTwitter();
				setUpGooglePlus();
			}
		}
	}

	/*
	 * Helper functions for setting up the social networks
	 */
	// Setting up our initial connection to google+ api.
	// We're creating our connection client and finding the buttons and image
	// that has to do with Google+ (Signin, Signout, Profile).
	@SuppressWarnings("deprecation")
	private void setUpGooglePlus() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(Plus.API)
		.addScope(Plus.SCOPE_PLUS_LOGIN)
		.build();

		gBar = (LinearLayout) findViewById(R.id.google_plus_bar);		
		gDisconnect = (ImageButton) findViewById(R.id.googlePlusDisconnect);
		gConnect = (ImageButton) findViewById(R.id.googlePlusConnect);
		gProfBtn = (ImageButton) findViewById(R.id.imgGooglePlus);
		gName = (TextView) findViewById(R.id.google_login_title);

		gDisconnect.setOnClickListener(this);
		gConnect.setOnClickListener(this);
		gProfBtn.setOnClickListener(this);

		if (!isConnected) {
			gConnect.setVisibility(View.GONE);
			gDisconnect.setVisibility(View.GONE);
		}

		if (googlePlusAuthorized && isConnected) {
			mGoogleApiClient.connect();
		}
	}

	public static void updateGooglePlusUi_SignIn() {
		gDisconnect.setVisibility(View.VISIBLE);
		gConnect.setVisibility(View.GONE);
	}

	public static void updateGooglePlusUi_SignOut() {
		gName.setText(R.string.google_name);
		gDisconnect.setVisibility(View.GONE);
		gConnect.setVisibility(View.VISIBLE);
		googlePlusAuthorized = false;
	}

	// Setting up our initial connection to the facebook api and the device's controls
	@SuppressWarnings("deprecation")
	private void setUpFacebook() {
		fBar = (LinearLayout) findViewById(R.id.facebook_bar);
		fConnect = (ImageButton) findViewById(R.id.facebookConnect);
		fDisconnect = (ImageButton) findViewById(R.id.facebookDisconnect);
		fProfBtn = (ImageButton) findViewById(R.id.imgFacebook);
		fName = (TextView) findViewById(R.id.facebook_login_title);

		fConnect.setOnClickListener(this);
		fDisconnect.setOnClickListener(this);
		fProfBtn.setOnClickListener(this);

		if (!isConnected) {
			fConnect.setVisibility(View.GONE);
			fDisconnect.setVisibility(View.GONE);
		}

		if (facebookAuthorized) {
			updateUI(Session.getActiveSession(), true);
		}

	}

	private void loginToFacebook() {
		Session.openActiveSession(SocialActivity.this, true, new Session.StatusCallback() {

			@Override
			public void call(final Session session, SessionState state,
					Exception exception) {
				if (session.isOpened()) {
					facebookAuthorized = true;
					Request.newMeRequest(session, new Request.GraphUserCallback() {

						@Override
						public void onCompleted(GraphUser user, Response response) {
							facebookAuthorized = true;
						}
					}).executeAsync();
				}
			}

		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.i("FB TEST", "Logged in...");
			updateUI(session, true);
		} else if (state.isClosed()) {
			Log.i("FB TEST", "Logged out...");
			updateUI(session, false);
		}
	}

	private void updateUI(Session session, final boolean log) {
		Request.newMeRequest(session, new Request.GraphUserCallback() {

			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user != null) {
					if (log) {
						facebook_name = user.getFirstName() + " " + user.getLastName();
						updateFacebookUi_SignIn();
					} else {
						updateFacebookUi_SignOut();
					}
				}
			}
		}).executeAsync();
	}

	public static void updateFacebookUi_SignIn() {
		fName.setText(facebook_name);
		fConnect.setVisibility(View.GONE);
		fDisconnect.setVisibility(View.VISIBLE);
	}

	public static void updateFacebookUi_SignOut() {
		fName.setText(R.string.facebook_name);
		fConnect.setVisibility(View.VISIBLE);
		fDisconnect.setVisibility(View.GONE);
		facebookAuthorized = false;
	}


	// Setting up our initial connection to the twitter api and the device's controls

	private void setUpTwitter() {
		tBar = (LinearLayout) findViewById(R.id.twitter_bar);
		tConnect = (ImageButton) findViewById(R.id.twitterConnect);
		tDisconnect = (ImageButton) findViewById(R.id.twitterDisconnect);
		tProfBtn = (ImageButton) findViewById(R.id.imgTwitter);
		tName = (TextView) findViewById(R.id.twitter_login_title);

		tConnect.setOnClickListener(this);
		tDisconnect.setOnClickListener(this);
		tProfBtn.setOnClickListener(this);

		if (!isConnected) {
			tConnect.setVisibility(View.GONE);
			tDisconnect.setVisibility(View.GONE);
		}

		twitter = new Twitter(this, tName);

		if (twitterAuthorized && isConnected) {
			twitter.authorize();
		}
	}

	public static void updateTwitterUi_SignIn() {
		tConnect.setVisibility(View.GONE);
		tDisconnect.setVisibility(View.VISIBLE);
	}

	public static void updateTwitterUi_SignOut() {
		tName.setText(R.string.twitter_name);
		tConnect.setVisibility(View.VISIBLE);
		tDisconnect.setVisibility(View.GONE);
		twitterAuthorized = false;
	}

	@SuppressWarnings("deprecation")
	private void roundLinearLayoutCorners() {
		//imgShare.setImageBitmap(RoundedImages.getRoundedRectBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.share), 125));

		Bitmap bmBack = RoundedImages.getRoundedRectBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bar), 25);
		fBar.setBackgroundDrawable(new BitmapDrawable(getResources(), bmBack));
		tBar.setBackgroundDrawable(new BitmapDrawable(getResources(), bmBack));
		gBar.setBackgroundDrawable(new BitmapDrawable(getResources(), bmBack));
	}
}
