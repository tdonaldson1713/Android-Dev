package com.gotu.android.testapplication;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

// Make sure you allow your class to implement these 3 classes - very important so 
// you can override the onConnection functions.
public class MainActivity extends ActionBarActivity implements ConnectionCallbacks, 
OnConnectionFailedListener, OnClickListener {
	public static final String GOOGLE_PLUS_PASS = "GooglePlusPass";
	public static final String GOOGLE_PLUS_NAME = "GooglePlusName";

	private static final int RC_SIGN_IN = 0;
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private boolean mSignInClicked;
	public static boolean canLoadApi = false;
	public static boolean googlePlusAuthorized;
	private Button googleLogin;
	private ConnectionResult mConnectionResult = null;

	@Override
	protected void onStart() {
		mGoogleApiClient.connect();

		super.onStart();
	}

	@Override
	protected void onStop() {
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}

		super.onStop();
	}

	@Override
	public void onConnected(Bundle connectionHint) { // Google+ Function
		mSignInClicked = false;
		/*Toast.makeText(this, "User is connected", Toast.LENGTH_SHORT).show();*/

		// This section of code gets the user's information - specifically for us, the name associated
		// the user's account. 
		if (Plus.AccountApi.getAccountName(mGoogleApiClient) != null) {
			Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
			String personName = currentPerson.getDisplayName();
			googleLogin.setText(personName);
			googlePlusAuthorized = true;
		}
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

	// This function helps us out if we can't connect to the Google+ servers - that way our application
	// won't crash.
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
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_main);

		setUpGooglePlus();
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	
	// This function sets up your button to login to your google account.
	private void setUpGooglePlus() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(Plus.API)
		.addScope(Plus.SCOPE_PLUS_LOGIN)
		.build();

		googleLogin = (Button) findViewById(R.id.button1);
		googleLogin.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.button1: 
			if (!mGoogleApiClient.isConnecting()) {
				mSignInClicked = true;
				resolveSignInError();
			}
			break;
			/*case R.id.button2: // Logout button
			if (mGoogleApiClient.isConnected()) {
				Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
				mGoogleApiClient.disconnect();
			}
			break;
		}*/
		}
	}
	
	/*private ProgressDialog mDialog;
	private String mProviderName;
	private SocialAuthAdapter adapter = null;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	private boolean hasPermission = false;
	private UiLifecycleHelper uiHelper;
	TextView welcome;
	private Session.StatusCallback callback = new Session.StatusCallback() {

		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState != null) {
			pendingPublishReauthorization = 
					savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
		}

		Button btn1 = (Button) findViewById(R.id.button1);

		// This will print out your HashKey. 
		// Run this during debug and it'll give you your debug key
		// To get the release key, export the apk, sign it and then install that version on your device.
		// Run the application while it's still connected to your computer so you can see logcat. 
		// The hashkey should be different and this is your release key. Both of these need to go into your app
		// settings on Facebook developer stuff. 

		// One other thing needs to be changed...where it says com.gotu.android.testapplication, put your package name there. 
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.gotu.android.testapplication", 
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("TEST", Base64.encodeToString(md.digest(), Base64.DEFAULT));
				btn1.setText(Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}


		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		welcome = (TextView) findViewById(R.id.textTitle);

		btn1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Starts Facebook Login.

				Session session = Session.getActiveSession();
				if (!session.isOpened() && !session.isClosed()) {
			        session.openForPublish(new Session.OpenRequest(MainActivity.this)
			            .setPermissions(Arrays.asList("publish_actions"))
			            .setCallback(callback));
			        Toast.makeText(getApplicationContext(), "New open", Toast.LENGTH_SHORT).show();
				} else {
			        Session.openActiveSession(MainActivity.this, true, callback);
			    }
				Session.openActiveSession(MainActivity.this, true, Arrays.asList("public_profile"), new Session.StatusCallback() {

					@Override
					public void call(final Session session, SessionState state,
							Exception exception) {
						if (session.isOpened()) {
							Request.newMeRequest(session, new Request.GraphUserCallback() {

								@Override
								public void onCompleted(GraphUser user, Response response) {									
									if (user != null) {
										welcome.setText("Hello " + user.getName() + "!");
									}
								}
							}).executeAsync();
						}
					}

				});

			}
		});

		Button btnPost = (Button) findViewById(R.id.btnpost);
		btnPost.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Session session = Session.getActiveSession();
				if (session != null) {
					publishStory();
					if (hasPublishPermissions()) {
						publishStory();
						if (FacebookDialog.canPresentShareDialog(getApplicationContext(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
							FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(MainActivity.this)
							.setLink("https://developers.facebook.com/android")
							.setDescription("I am testing the Facebook SDK for my new app, Link It.")
							.build();

							uiHelper.trackPendingDialogCall(shareDialog.present());
						} else {
							publishFeedDialog();
						}
						return;
					} else if (session.isOpened()) {
						session.requestNewPublishPermissions(new Session.NewPermissionsRequest(MainActivity.this, Constants.PERMISSIONS));
						return;
					}
				}
			}
		});

		//adapter = new SocialAuthAdapter(new ResponseListener());
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {

			@Override
			public void onError(PendingCall pendingCall, Exception error, Bundle data) {
				Log.e("TEST", String.format("Error: %s", error.toString()));
			}

			@Override
			public void onComplete(PendingCall pendingCall, Bundle data) {
				Log.i("TEST", "Success!");
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		uiHelper.onSaveInstanceState(outState);
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.i("FB TEST", "Logged in...");
			updateUI(session);
		} else if (state.isClosed()) {
			Log.i("FB TEST", "Logged out...");
		}

		if (pendingPublishReauthorization && 
		        state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
		    pendingPublishReauthorization = false;
		    publishStory();
		}
	}

	private void updateUI(Session session) {
		Request.newMeRequest(session, new Request.GraphUserCallback() {

			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user != null) {
					welcome.setText("Hello " + user.getName() + "!");
				}
			}
		}).executeAsync();
	}

	private void publishStory() {
		Session session = Session.getActiveSession();

		if (session != null){

			// Check for publish permissions    
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				pendingPublishReauthorization = true;
				Session.NewPermissionsRequest newPermissionsRequest = new Session
						.NewPermissionsRequest(this, Constants.PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}

			Bundle postParams = new Bundle();

			postParams.putString("message", "Testing that a message with an image works properly in Link It");
			postParams.putString("link", "https://developers.facebook.com/android");

			// Optional parameters
			postParams.putString("name", "Facebook SDK for Android");
			postParams.putString("caption", "Build great social apps and get more installs.");
			postParams.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
			postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

			Request.Callback callback= new Request.Callback() {
				public void onCompleted(Response response) {
					JSONObject graphResponse = response
							.getGraphObject()
							.getInnerJSONObject();
					String postId = null;
					try {
						postId = graphResponse.getString("id");
					} catch (JSONException e) {
						Log.i("TEST",
								"JSON error "+ e.getMessage());
					}
					FacebookRequestError error = response.getError();
					if (error != null) {
						Toast.makeText(getApplicationContext(),
								error.getErrorMessage(),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(),
								"Shared to Facebook",
								Toast.LENGTH_LONG).show();
					}
				}
			};

			Request request = new Request(session, "me/feed", postParams, 
					HttpMethod.POST, callback);

			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}
	}

	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}


	// TWITTER

	private final class ResponseListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {
			// Get the provider
			mProviderName = values.getString(SocialAuthAdapter.PROVIDER);

			//Toast.makeText(mContext, R.string.fbLogin, Toast.LENGTH_SHORT).show();
			mDialog = new ProgressDialog(getApplicationContext());
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
			mDialog.setMessage("Loading...");
		}

		@Override
		public void onBack() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(SocialAuthError arg0) {
			//Toast.makeText(mContext, "Cannot log into Facebook", Toast.LENGTH_LONG).show();
			Log.e("com.gotu.android.linkin.social", arg0.toString());
		}
	}*/
}
