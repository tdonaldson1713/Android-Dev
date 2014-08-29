package com.gotu.android.unused_files;

import java.util.ArrayList;

import com.gotu.android.linkin.R;
import com.gotu.android.linkin.R.drawable;
import com.gotu.android.linkin.R.id;
import com.gotu.android.linkin.R.layout;
import com.gotu.android.linkin.R.string;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SocialMediaListAdapter_old extends ArrayAdapter<String>{
	private Context mContext;
	private ArrayList<String> networks;
	private ArrayList<Boolean> authorized_networks;

	public SocialMediaListAdapter_old(Context context, int resource, ArrayList<String> objects, ArrayList<Boolean> objs) {
		super(context, resource, objects);

		mContext = context;

		networks = new ArrayList<String>();
		networks.addAll(objects);

		authorized_networks = new ArrayList<Boolean>();
		authorized_networks.addAll(objs);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.list_item_old, null);

		ImageView socialImage = (ImageView) view.findViewById(R.id.img_social);
		final TextView socialMediaName = (TextView) view.findViewById(R.id.social_name);
		
		String name = networks.get(position);
		
		if (Character.isWhitespace(name.charAt(0))) {
			name = name.substring(1);
		}

		socialMediaName.setText(name);
		final ImageButton socialMediaSharing = (ImageButton) view.findViewById(R.id.social_share);

		if (!authorized_networks.get(position)) {
			//socialMediaSharing.setEnabled(false);
			socialMediaSharing.setImageResource(android.R.color.transparent);
		}

		switch(position) {
		case 0: // Facebook
			socialImage.setImageResource(R.drawable.facebook_icon);
			break;
		case 1: // Twitter
			socialImage.setImageResource(R.drawable.twitter_icon);
			break;
		case 2: // Google+
			socialImage.setImageResource(R.drawable.google_plus_icon);
			break;
		case 3: // SMS
			socialImage.setImageResource(R.drawable.sms_icon);
			break;
		}

		socialMediaSharing.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (socialMediaSharing.getContentDescription().equals(mContext.getResources().getString(R.string.sharing_content))) {
					socialMediaSharing.setImageResource(R.drawable.connected);
					socialMediaSharing.setContentDescription(mContext.getResources().getString(R.string.sharing_content_yes));
				} else {
					socialMediaSharing.setImageResource(R.drawable.disconnected);
					socialMediaSharing.setContentDescription(mContext.getResources().getString(R.string.sharing_content));
				}
			}
		});

		return view;
	}

}
