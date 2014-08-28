package com.gotu.android.unused_files;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.gotu.android.linkin.R;
import com.gotu.android.linkin.ShareActivity;
import com.gotu.android.linkin.SocialActivity;
import com.gotu.android.linkin.util.Constants;

public class Facebooks {
	private final static String SHARING = "com.gotu.android.linkin FACEBOOK_SHARING";
	private final static String ERROR = "com.gotu.android.linkin FACEBOOK_LOGIN_ERROR";
	private boolean isSharing;
	private Context mContext;
	private SocialAuthAdapter mAdapter = null;
	private Profile mUserProfile = null;
	private ProgressDialog mDialog;
	private TextView textName;
	private String mProviderName;

	public Facebooks(Context context, TextView text) {
		isSharing = false;
		textName = text;

		mAdapter = new SocialAuthAdapter(new ResponseListener());
		init();

		mContext = context;
	}

	private void init() {
		mAdapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);

		try {
			mAdapter.addConfig(Provider.FACEBOOK, Constants.FACEBOOK_KEY, Constants.FACEBOOK_SEC, Constants.PERMISSIONS);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void updateStatus(final String text) {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				mAdapter.updateStatus(text, new MessageListener(), true);
			}
		};

		new Thread(runnable).start();
	}

	public void authorize() {
		mAdapter.authorize(mContext, Provider.FACEBOOK);

	}

	public void isSharing(boolean sharing) {
		isSharing = sharing;
	}

	public void logout() {
		mAdapter.signOut(mContext, Provider.FACEBOOK.toString());
		SocialActivity.facebookAuthorized = false;
	}

	public String getUserName() {
		if (mUserProfile != null) {
			return mUserProfile.getFirstName() + " " + mUserProfile.getLastName();
		}

		return mContext.getString(R.string.facebook_name);
	}

	public boolean isAuthorized() {
		String token = null;
		try {
			token = mAdapter.getCurrentProvider().getAccessGrant().getKey();
		} catch (Exception e) {

		}

		if (token == null) {
			return false;
		} else {
			return true;
		}
	}


	private final class ResponseListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {
			// Get the provider
			mProviderName = values.getString(SocialAuthAdapter.PROVIDER);

			//Toast.makeText(mContext, R.string.fbLogin, Toast.LENGTH_SHORT).show();
			mDialog = new ProgressDialog(mContext);
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mDialog.setMessage("Loading...");

			SocialActivity.facebookAuthorized = true;
			if (!isSharing) {
				Log.d(SHARING, "Attempting to get Facebook profile and change view");
				SocialActivity.updateFacebookUi_SignIn();
				mAdapter.getUserProfileAsync(new ProfileDataListener());
			}
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
			Log.e(ERROR, arg0.toString());
		}
	}

	// To get status of message after authentication
	private final class MessageListener implements SocialAuthListener<Integer> {
		@Override
		public void onExecute(String provider, Integer t) {
			Integer status = t;
			if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204) {
				Toast.makeText(mContext, "Message posted on " + provider, Toast.LENGTH_LONG).show();
				ShareActivity.resetText();
			} else
				Toast.makeText(mContext, "Message not posted " + provider, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}

	// To receive the profile response after authentication
	private final class ProfileDataListener implements SocialAuthListener<Profile> {

		@Override
		public void onExecute(String provider, Profile t) {

			Log.d("Custom-UI", "Receiving Data");
			mDialog.dismiss();
			mUserProfile = t;

			if (textName != null) {
				textName.setText(mContext.getString(R.string.facebook_name_logged, getUserName()));
			}
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}
}
