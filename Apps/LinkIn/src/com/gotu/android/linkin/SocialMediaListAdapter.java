package com.gotu.android.linkin;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Session;
import com.gotu.android.linkin.util.Constants;

public class SocialMediaListAdapter extends ArrayAdapter<String>{
	private Activity calling_activity;
	private Context mContext;
	private ArrayList<String> networks;
	private ArrayList<Boolean> authorized_networks;
	private Session session;
	private boolean xlarge = false, large = false, medium = false, small = false;

	public SocialMediaListAdapter(Context context, int resource, ArrayList<String> objects, ArrayList<Boolean> objs, Activity activity) {
		super(context, resource, objects);
		calling_activity = activity;
		mContext = context;

		networks = new ArrayList<String>();
		networks.addAll(objects);

		authorized_networks = new ArrayList<Boolean>();
		authorized_networks.addAll(objs);

		session = Session.getActiveSession();

		if ((mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {     
			xlarge = true;
		} else if ((mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {     
			large = true;
		} else if ((mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {     
			medium = true;
		} else if ((mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {     
			small = true;
		} 
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.list_item, null);
		
		ImageView socialImage = (ImageView) view.findViewById(R.id.img_social);
		final TextView socialMediaName = (TextView) view.findViewById(R.id.social_name);
		final TextView socialNetworkName = (TextView) view.findViewById(R.id.social_name_title);

		String name = networks.get(position);

		if (Character.isWhitespace(name.charAt(0))) {
			name = name.substring(1);
		}

		socialMediaName.setText(name);
		final ImageButton socialMediaSharing = (ImageButton) view.findViewById(R.id.social_share);

		if (!authorized_networks.get(position)) {
			socialMediaSharing.setImageResource(android.R.color.transparent);
		}

		switch(position) {
		case 0: // Facebook
			if (xlarge) {
				socialImage.setImageResource(R.drawable.facebook_icon_xlarge);
			} else if (large) {
				socialImage.setImageResource(R.drawable.facebook_icon_large);
			} else {
				socialImage.setImageResource(R.drawable.facebook_icon);
			}
			
			socialNetworkName.setText(R.string.Facebook);
			break;
		case 1: // Twitter
			if (xlarge) {
				socialImage.setImageResource(R.drawable.twitter_icon_xlarge);
			} else if (large) {
				socialImage.setImageResource(R.drawable.twitter_icon_large);
			} else {
				socialImage.setImageResource(R.drawable.twitter_icon);
			}
			
			socialNetworkName.setText(R.string.Twitter);
			break;
		case 2: // Google+
			if (xlarge) {
				socialImage.setImageResource(R.drawable.google_plus_icon_xlarge);
			} else if (large) {
				socialImage.setImageResource(R.drawable.google_plus_icon_large);
			} else {
				socialImage.setImageResource(R.drawable.google_plus_icon);
			}
			
			socialNetworkName.setText(R.string.GooglePlus);
			break;
		case 3: // SMS
			if (xlarge) {
				socialImage.setImageResource(R.drawable.sms_icon_xlarge);
			} else if (large) {
				socialImage.setImageResource(R.drawable.sms_icon_large);
			} else {
				socialImage.setImageResource(R.drawable.sms_icon);
			}
			
			socialNetworkName.setText(R.string.SMS);
			break;
		}

		socialMediaSharing.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (socialMediaSharing.getContentDescription().equals(mContext.getResources().getString(R.string.sharing_content))) {
					if (xlarge) {
						socialMediaSharing.setImageResource(R.drawable.connected_xlarge);
					} else if (large) {
						socialMediaSharing.setImageResource(R.drawable.connected_large);
					} else {
						socialMediaSharing.setImageResource(R.drawable.connected);
					}
					
					socialMediaSharing.setContentDescription(mContext.getResources().getString(R.string.sharing_content_yes));
				} else {
					if (xlarge) {
						socialMediaSharing.setImageResource(R.drawable.disconnected_xlarge);
					} else if (large) {
						socialMediaSharing.setImageResource(R.drawable.disconnected_large);
					} else {
						socialMediaSharing.setImageResource(R.drawable.disconnected);
					}
					
					socialMediaSharing.setContentDescription(mContext.getResources().getString(R.string.sharing_content));
				}

				if (position == 0 && socialMediaSharing.getContentDescription().equals(mContext.getResources().getString(R.string.sharing_content_yes))) {
					try {

						if (session != null && session.isOpened()) {
							if (!session.getPermissions().contains(Constants.PERMISSIONS)) {
								session.requestNewPublishPermissions(new Session.NewPermissionsRequest(calling_activity, Constants.PERMISSIONS));
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						// +		Session.NewPermissionsRequest request = new Session.NewPermissionsRequest(mActivity, mConfiguration.getPublishPermissions());

					}
				}
			}
		});

		return view;
	}

}
