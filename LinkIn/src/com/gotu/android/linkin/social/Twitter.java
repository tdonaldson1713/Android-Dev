package com.gotu.android.linkin.social;

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

public class Twitter {
	private boolean isSharing;
	private Context mContext;
	private SocialAuthAdapter mAdapter = null;
	private ProgressDialog mDialog;
	private String mProviderName;
	private Profile mUserProfile;
	private TextView textName;

	public Twitter(Context context, TextView text) {
		textName = text;
		isSharing = false;

		mAdapter = new SocialAuthAdapter(new ResponseListener());
		init();

		mContext = context;
	}

	// Initial configuration of the twitter adapter.
	private void init() {
		mAdapter.addProvider(Provider.TWITTER, R.drawable.twitter);
		mAdapter.addCallBack(Provider.TWITTER, Constants.TWITTER_CLB);

		try {
			mAdapter.addConfig(Provider.TWITTER, Constants.TWITTER_KEY, Constants.TWITTER_SEC, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateStatus(final String text) {
		/*Runnable runnable = new Runnable() {

			@Override
			public void run() {
				
			}

		};

		new Thread(runnable).start();*/
		mAdapter.updateStatus(text, new MessageListener(), true);
	}

	public void authorize() {
		mAdapter.authorize(mContext, Provider.TWITTER);
	}

	public void logout() {
		mAdapter.signOut(mContext, Provider.TWITTER.toString());
	}

	public void isSharing(boolean sharing) {
		isSharing = sharing;
	}

	public String getUserName() {
		if (mUserProfile != null) {
			return mUserProfile.getDisplayName();
		}

		return mContext.getString(R.string.twitter_name);
	}

	public boolean isAuthorized() {
		String token = null;
		try {
			token = mAdapter.getCurrentProvider().getAccessGrant().getKey();
		} catch (Exception e) {
			e.printStackTrace();
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

			mDialog = new ProgressDialog(mContext);
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mDialog.setMessage("Loading...");

			SocialActivity.twitterAuthorized = true;
			
			if (!isSharing) {
				Log.d("com.gotu.android.linkin TWITTER_SHARING", "Attempting to get Twitter profile and change view");
				SocialActivity.updateTwitterUi_SignIn();
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
			Log.e("com.gotu.android.linkin.social", arg0.toString());
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