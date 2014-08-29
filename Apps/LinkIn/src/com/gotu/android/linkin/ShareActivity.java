package com.gotu.android.linkin;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.gotu.android.linkin.social.Twitter;
import com.gotu.android.linkin.util.Constants;
import com.gotu.android.linkin.util.PrivacyPolicyDialog;
import com.gotu.android.linkin.util.SendSMSWarning;
import com.gotu.android.linkin.util.TinyDB;
import com.gotu.android.unused_files.Facebooks;

public class ShareActivity extends Activity implements ConnectionCallbacks, 
OnConnectionFailedListener, OnClickListener {

	private ArrayList<String> networks;
	private ArrayList<Boolean> authorized_networks;
	public static ArrayList<Boolean> sharing_to_networks;
	private static final String TAG = "com.gotu.android.linkit ShareActivity";

	private static Activity activity;
	public static EditText editLink;
	public static EditText editMessage;
	private ImageButton btnShare;
	private int FACEBOOK_MESSAGE_LENGTH = 2000;
	private int TWITTER_MESSAGE_LENGTH = 140;
	private int message_limiter = 0;
	static ListView listSocialNetworks;	
	private static Resources r;
	/*private static SocialMediaListAdapter adapter;*/
	private static SocialMediaListAdapter adapter;
	private String browser_share;
	private static String message;
	private static String link;
	private TextView text_message_length_twitter;
	private TextView text_message_length_facebook;
	private TextView text_message_length_warning;
	private TinyDB sharedPrefDB = null;
	private static View desired_view = null;

	// Beginning of Google+ Variables
	private static final int RC_SIGN_IN = 0;
	private static final int GOOGLE_PLUS_SHARE = 2;
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private boolean mSignInClicked;
	private ConnectionResult mConnectionResult;
	private boolean googlePlusAuthorized = false;
	private String googlePlusName = "";
	// End of Google+ Variables

	// Twitter variables
	static Twitter twitter = null;
	private boolean twitterAuthorized = false;
	private String twitterName = "";
	// End of twitter variables

	// Facebook variables
	static Facebooks facebook = null;
	private boolean facebookAuthorized = false;
	private String facebookName = "";
	private static UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {

		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	// End of Facebook variables

	// SMS variables
	private boolean hasTelephony = false;
	private static final int CONTACT_SELECTED = 1;
	private SmsManager sms;
	private String mPhoneNumber;
	// End of SMS variables

	// Message Dialog Stuff
	private TextView text_message_length_twitter_dialog;
	private TextView text_message_length_facebook_dialog;
	private TextView text_message_length_warning_dialog;
	// End Message Dialog

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
			PrivacyPolicyDialog.PrivacyPolicy(ShareActivity.this).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);

		activity = ShareActivity.this;
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView)this.findViewById(R.id.adView2);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		r = getResources();
		sharedPrefDB = new TinyDB(this);
		getPreferences();

		hasTelephony = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
		browser_share = getIntent().getStringExtra(SplashActivity.SHARE);

		if (browser_share != null && browser_share.length() >= 22) {
			message_limiter = 26;
		} else if (browser_share != null && browser_share.length() < 22) {
			message_limiter = browser_share.length();
		}

		networks = new ArrayList<String>();
		networks.add(facebookName);
		networks.add(twitterName);
		networks.add(googlePlusName);

		if (hasTelephony) {			
			networks.add("SMS - " + ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number());
		} else {
			networks.add("SMS");
		}

		setUpFacebook();
		setUpTwitter();
		setUpGooglePlus();
		setUpSMS();

		authorized_networks = new ArrayList<Boolean>();
		authorized_networks.add(facebookAuthorized);		
		authorized_networks.add(twitterAuthorized);
		authorized_networks.add(googlePlusAuthorized);
		authorized_networks.add(hasTelephony);

		editMessage = (EditText) findViewById(R.id.editMessage);
		editLink = (EditText) findViewById(R.id.editLink);
		
		if (browser_share != null) {
			editLink.setText(browser_share);		
		}

		text_message_length_twitter = (TextView) findViewById(R.id.textMessageLength_Twitter);
		text_message_length_facebook = (TextView) findViewById(R.id.textMessageLength_Facebook);
		text_message_length_warning = (TextView) findViewById(R.id.textMessageLength_Warning);
		setInitialLengths();

		editMessage.addTextChangedListener(setupMessageLengthListener());
		editMessage.setOnClickListener(this);
		editLink.addTextChangedListener(setupMessageLengthListener());
		editLink.setOnClickListener(this);
		
		btnShare = (ImageButton) findViewById(R.id.imgShareToSocialNetworks);
		btnShare.setOnClickListener(this);

		listSocialNetworks = (ListView) findViewById(R.id.listSocialMedia);

		/*adapter = new SocialMediaListAdapter(this, R.layout.list_item, networks, authorized_networks);*/
		adapter = new SocialMediaListAdapter(this, R.layout.list_item, networks, authorized_networks, ShareActivity.this);
		listSocialNetworks.setAdapter(adapter);
	}

	private void getPreferences() {
		facebookAuthorized = sharedPrefDB.getBoolean(SocialActivity.FACEBOOK_PASS);
		facebookName = sharedPrefDB.getString(SocialActivity.FACEBOOK_NAME);
		twitterAuthorized = sharedPrefDB.getBoolean(SocialActivity.TWITTER_PASS);
		twitterName = sharedPrefDB.getString(SocialActivity.TWITTER_NAME);
		googlePlusAuthorized = sharedPrefDB.getBoolean(SocialActivity.GOOGLE_PLUS_PASS);
		googlePlusName = sharedPrefDB.getString(SocialActivity.GOOGLE_PLUS_NAME);
	}

	private void setInitialLengths() {
		text_message_length_twitter.setText(getString(R.string.message_length, TWITTER_MESSAGE_LENGTH - message_limiter));
		text_message_length_facebook.setText(getString(R.string.message_length, FACEBOOK_MESSAGE_LENGTH - message_limiter));
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

		if (requestCode == CONTACT_SELECTED) {

			if (intent != null) {
				Uri uri = intent.getData();

				if (uri != null) {
					Cursor c = null;
					try {
						c = getContentResolver()
								.query(uri,
										new String[] {
										ContactsContract.CommonDataKinds.Phone.NUMBER,
										ContactsContract.CommonDataKinds.Phone.TYPE },
										null, null, null);

						// If the user is entering the first contact that should be
						// sent the massive amount of text messages.
						if (c != null && c.moveToFirst()) {
							mPhoneNumber = c.getString(0);
							String text = message + "\n" + link;
							SendSMSWarning.SMSWarning(this, text, mPhoneNumber, sms).show();
						} else {
							Toast.makeText(this, R.string.unable_to_find_contact, Toast.LENGTH_SHORT).show();
						}

					} finally {
						if (c != null) {
							c.close();
						}
					}
				}
			}

		}

		if (requestCode == GOOGLE_PLUS_SHARE) {
			if (responseCode == RESULT_OK) {
				if (sharing_to_networks.get(3)) {
					updateSMS();
				} else if (sharing_to_networks.get(0) || sharing_to_networks.get(1)) {
					/*PostToSocialNetworks.ShareToSocialNetwork(sharing_to_networks.get(0), sharing_to_networks.get(1), this).show();*/
					if (sharing_to_networks.get(0)) {
						updateFacebook();
					}

					if (sharing_to_networks.get(1)) {
						updateTwitter();
					}
				} else {
					resetText();
				}
			} else {
				Toast.makeText(getApplication(), "Error updating Google+ - cancelling all sharing", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) { // Google+ Function
		mSignInClicked = false;
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
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	private void setUpGooglePlus() {
		if (googlePlusAuthorized) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(Plus.API)
			.addScope(Plus.SCOPE_PLUS_LOGIN)
			.build();

			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					// if the user is signed in, we'll have a boolean passed through activities
					// to connect to google+ api or not. This will be the case for the other 2 also
					mGoogleApiClient.connect();
				}
			};

			new Thread(runnable).start();
		}
	}

	private void setUpFacebook() {
		if (facebookAuthorized) {
			/*facebook = new Facebooks(this, null);
			facebook.isSharing(true);
			facebook.authorize();*/
			Session.openActiveSession(ShareActivity.this, true, new Session.StatusCallback() {

				@Override
				public void call(final Session session, SessionState state,
						Exception exception) {
					if (session.isOpened()) {
						// We need to get the permission when link is pressed if we don't have it. 
					}
				}

			});
		}
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.i("FB TEST", "Logged in...");
		} else if (state.isClosed()) {
			Log.i("FB TEST", "Logged out...");
		}
	}

	private void setUpTwitter() {
		if (twitterAuthorized) {
			twitter = new Twitter(this, null);
			twitter.isSharing(true);
			twitter.authorize();
		}
	}

	private TextWatcher setupMessageLengthListener() {
		final TextWatcher watcher = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// Doing nothing here.
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// Doing nothing here.
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int twitterLength = TWITTER_MESSAGE_LENGTH - message_limiter - s.length();
				int facebookLength = FACEBOOK_MESSAGE_LENGTH - message_limiter - s.length();

				if (facebookLength == 0) {
					text_message_length_facebook.setText("0");
				} else {
					text_message_length_facebook.setText(String.valueOf(facebookLength));
				}

				if (twitterLength == 0) {
					text_message_length_twitter.setText("0");
				} else {
					text_message_length_twitter.setText(String.valueOf(twitterLength));
				}	

				if (twitterLength <= 5) {
					text_message_length_warning.setText(R.string.is_shortening_message);
					text_message_length_twitter.setTextColor(getResources().getColor(R.color.red));
				} else if (twitterLength > 5) {
					text_message_length_twitter.setTextColor(getResources().getColor(R.color.black));
				}

				if (facebookLength <= 5) {
					text_message_length_warning.setText(R.string.is_shortening_message);
					text_message_length_facebook.setTextColor(getResources().getColor(R.color.red));
				} else if (facebookLength > 5) {
					text_message_length_facebook.setTextColor(getResources().getColor(R.color.black));
				}

				if (facebookLength > 5 && twitterLength > 5) {
					text_message_length_warning.setText("");
				}
			}

		};

		return watcher;
	}

	private TextWatcher setupMessageLengthListener_Dialog() {
		final TextWatcher watcher = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// Doing nothing here.
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// Doing nothing here.
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int twitterLength = TWITTER_MESSAGE_LENGTH - message_limiter - s.length();
				int facebookLength = FACEBOOK_MESSAGE_LENGTH - message_limiter - s.length();

				if (facebookLength <= 0) {
					text_message_length_facebook_dialog.setText("0");
				} else {
					text_message_length_facebook_dialog.setText(String.valueOf(facebookLength));
				}

				if (twitterLength <= 0) {
					text_message_length_twitter_dialog.setText("0");
				} else {
					text_message_length_twitter_dialog.setText(String.valueOf(twitterLength));
				}	

				if (twitterLength > 0 && twitterLength <= 5) {
					text_message_length_twitter_dialog.setTextColor(getResources().getColor(R.color.red));
				} else if (twitterLength > 5) {
					text_message_length_twitter_dialog.setTextColor(getResources().getColor(R.color.black));
				}

				if (facebookLength > 0 && facebookLength <= 5) {
					text_message_length_facebook_dialog.setTextColor(getResources().getColor(R.color.red));
				} else if (facebookLength > 5) {
					text_message_length_facebook_dialog.setTextColor(getResources().getColor(R.color.black));
				}
			}

		};

		return watcher;
	}

	private void setUpSMS() {
		if (hasTelephony) {
			sms = SmsManager.getDefault();
		}
	}

	private void editMessageDialog(final boolean link_clicked) {
		final Dialog dialog = new Dialog(this, R.style.ThemeDialogCustom);
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_message, null);
		dialog.setContentView(view);
		
		if (link_clicked) {
			TextView title = (TextView) view.findViewById(R.id.textTitle);
			title.setText(R.string.link_hint);
		}

		text_message_length_twitter_dialog = (TextView) view.findViewById(R.id.textMessageLengthInDialog_Twitter);
		text_message_length_facebook_dialog = (TextView) view.findViewById(R.id.textMessageLengthInDialog_Facebook);

		text_message_length_twitter_dialog.setText(getString(R.string.message_length, TWITTER_MESSAGE_LENGTH - message_limiter));
		text_message_length_facebook_dialog.setText(getString(R.string.message_length, FACEBOOK_MESSAGE_LENGTH - message_limiter)); 

		Button okButton = (Button) view.findViewById(R.id.btn_ok);
		Button cancelButton = (Button) view.findViewById(R.id.btn_cancel);

		final EditText input = (EditText) view.findViewById(R.id.editMessageInDialog);
		input.addTextChangedListener(setupMessageLengthListener_Dialog());
		input.setOnClickListener(this);

		if (!link_clicked) {
			input.setText(editMessage.getText().toString());
			input.setSelection(editMessage.length());
		} else {
			input.setText(editLink.getText().toString());
			input.setSelection(editLink.length());
		}

		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!link_clicked) {
					editMessage.setText(input.getText().toString());
					editMessage.setSelection(input.getText().toString().length());
				} else {
					editLink.setText(input.getText().toString());
					editLink.setSelection(input.getText().toString().length());
				}
				
				InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
				//msgDialog.dismiss();
				dialog.dismiss();
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
				//msgDialog.dismiss();
				dialog.dismiss();
			}
		});

		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lp.dimAmount = (float) 0.6;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.editMessage:
			// Create dialog for editing the message to the user. Will include the link here too.
			editMessageDialog(false);
			break;
		case R.id.editLink:
			editMessageDialog(true);
			break;
		case R.id.imgShareToSocialNetworks:
			sharing_to_networks = new ArrayList<Boolean>();

			for (int a = 0; a < adapter.getCount(); a++) {
				desired_view = listSocialNetworks.getChildAt(a);
				ImageButton b = (ImageButton) desired_view.findViewById(R.id.social_share);

				if (b.getContentDescription().equals(getResources().getString(R.string.sharing_content_yes))) {
					sharing_to_networks.add(true);
				} else {
					sharing_to_networks.add(false);
				}
			}

			boolean can_share = false;
			for (int a = 0; a < sharing_to_networks.size(); a++) {
				if (sharing_to_networks.get(a)) {
					can_share = true;
					continue;
				} 
			}

			if (!can_share) {
				Toast.makeText(getApplicationContext(), R.string.no_shareable_networks, Toast.LENGTH_SHORT).show();
			} else {
				message = editMessage.getText().toString();
				link = editLink.getText().toString();

				if (message.equals("") && link.equals("")) {
					Toast.makeText(this, "Please enter text into the text box", Toast.LENGTH_SHORT).show();
				} else {
					// We're going to put all the sharing on a separate thread than the main thread.
					if (sharing_to_networks.get(2)) {
						updateGooglePlus();
					} else if (sharing_to_networks.get(3)) {
						updateSMS();
					} else if (sharing_to_networks.get(0) || sharing_to_networks.get(1)) {
						/*PostToSocialNetworks.ShareToSocialNetwork(sharing_to_networks.get(0), sharing_to_networks.get(1), activity).show();*/
						if (sharing_to_networks.get(0)) {
							updateFacebook();
						}

						if (sharing_to_networks.get(1)) {
							updateTwitter();
						}
					}
				}
			}

			break;
		}
	}

	public static void updateFacebook() {
		Session session = Session.getActiveSession();
		if (session != null) {
			publishStory();
		}
	}

	private static void publishStory() {
		Session session = Session.getActiveSession();

		if (session != null) {
			Bundle postParams = new Bundle();

			if (!link.equals("")) {
				postParams.putString("link", link);
			}
			postParams.putString("message", message);

			// Optional parameters
			/*postParams.putString("name", "Facebook SDK for Android");
			postParams.putString("caption", "Build great social apps and get more installs.");
			postParams.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
			postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");*/

			Request.Callback callback= new Request.Callback() {
				public void onCompleted(Response response) {
					try {
						JSONObject graphResponse = response
								.getGraphObject()
								.getInnerJSONObject();
						String postId = null;
						try {
							postId = graphResponse.getString("id");
							Log.d(TAG, postId);
						} catch (JSONException e) {
							Log.i(TAG,
									"JSON error "+ e.getMessage());
						}
					} catch (Exception e) {
						Toast.makeText(activity, R.string.get_permissions, Toast.LENGTH_SHORT).show();
						e.printStackTrace();
						return;
					}

					FacebookRequestError error = response.getError();
					if (error != null) {
						Toast.makeText(activity,
								error.getErrorMessage(),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(activity,
								"Shared to Facebook",
								Toast.LENGTH_LONG).show();

						if (!sharing_to_networks.isEmpty() && !sharing_to_networks.get(1)) {
							resetText();
						}
					}
				}
			};

			Request request = new Request(session, "me/feed", postParams, 
					HttpMethod.POST, callback);

			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}
	}

	public static boolean hasFacebookPublishPermissions() {
		Session session = Session.getActiveSession();
		return session != null && session.getPermissions().contains(Constants.PERMISSIONS);
	}

	public static void updateTwitter() {
		String shortened_message = "";
		boolean shortened = false;

		// Here, we need to shorten the message that gets tweeted according 
		// to the length of the URL being shared.
		/*if (!editLink.getText().equals("") && message.length() > 85) {
			if (editLink.getText().length() >= 22) {
				Log.d("TEST", "here");
				shortened_message = message.subSequence(0, 60) + " ...";
				shortened = true;
			} else if (editLink.getText().length() < 22) {
				Log.d("TEST", "there");
				shortened_message = message.subSequence(0, 113) + " ...";
				shortened = true;
			}
		} else if (message.length() > 140) {
			shortened_message = message.subSequence(0, 134) + " ...";
			shortened = true;
		}*/

		if (!editLink.getText().equals("") && message.length() > 120) {
			shortened_message = message.substring(0, 105) + " ... ";
			shortened = true;
		} 

		if (!editLink.getText().toString().equals("")) {
			if (!shortened) {
				twitter.updateStatus(message  + " " + link);
			} else {
				twitter.updateStatus(shortened_message  + " " + link);
			}
		} else {
			if (!shortened) {
				twitter.updateStatus(message);
			} else {
				twitter.updateStatus(shortened_message);
			}
		}
	}

	private void updateGooglePlus() {
		if (editMessage.getText().toString().equals("")) {
			link += " ";
		}

		Intent shareIntent= new PlusShare.Builder(this)
		.setType("text/plain")
		.setText(message + "\n\n" + link)
		.getIntent();

		startActivityForResult(shareIntent, GOOGLE_PLUS_SHARE);
	}

	private void updateSMS() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
				startActivityForResult(intent, 1);
			}
		};

		new Thread(runnable).start();
	}

	public static void resetText() {
		editMessage.setText("");
		editLink.setText("");

		if (!sharing_to_networks.isEmpty()) {
			// Uncheck the checkboxes. (this is going to change to imageviews/imagebuttons.
			for (int a = 0; a < adapter.getCount(); a++) {
				desired_view = listSocialNetworks.getChildAt(a);
				ImageButton b = (ImageButton) desired_view.findViewById(R.id.social_share);

				if (sharing_to_networks.get(a)) {
					b.setImageResource(R.drawable.disconnected);
				}
				b.setContentDescription(r.getString(R.string.sharing_content));
			}

			sharing_to_networks.clear();
		}

		activity.finish();
	}
}
