package com.gotu.android.unused_files;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.gotu.android.linkin.R;
import com.gotu.android.linkin.SocialActivity;
import com.gotu.android.linkin.social.Twitter;

public class LogoutWarning {
	private static Resources r;
	
	public static void DialogLogoutWarning(final Context context, final String provider,  
			final Twitter twitter, final GoogleApiClient google) {
		r = context.getResources();
		
		final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_logout_warning, null);
		dialog.setContentView(view);
		
		TextView textMessage = (TextView) view.findViewById(R.id.textWarningLogoutMessage);
		Button btnLogout = (Button) view.findViewById(R.id.btn_logout_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_logout_cancel);
		
		if (provider.equals("twitter")) {
			textMessage.setText(r.getString(R.string.logout_warning, r.getString(R.string.Twitter)));
		} else if (provider.equals("google_plus")) {
			textMessage.setText(r.getString(R.string.logout_warning, r.getString(R.string.GooglePlus)));
		}
		
		btnLogout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (provider.equals("twitter") && twitter != null) {
					twitter.logout();
					SocialActivity.updateTwitterUi_SignOut();
					createLogoutToast(R.string.tLogout, context);
				} else if (provider.equals("google_plus") && google != null) {
					if (google.isConnected()) {
						Plus.AccountApi.clearDefaultAccount(google);
						google.disconnect();
						createLogoutToast(R.string.gpLogout, context);
						SocialActivity.updateGooglePlusUi_SignOut();
					}
				}
			}
		});
		
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lp.dimAmount = (float) 0.6;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
	}
	
	private static void createLogoutToast(int logout, Context context) {
		Toast.makeText(context, logout, Toast.LENGTH_SHORT).show();
	}
}
