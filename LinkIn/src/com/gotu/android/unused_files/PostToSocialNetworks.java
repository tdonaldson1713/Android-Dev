package com.gotu.android.unused_files;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.gotu.android.linkin.R;
import com.gotu.android.linkin.ShareActivity;

public class PostToSocialNetworks {
	public static Resources r;
	
	public static Dialog ShareToSocialNetwork(final boolean face, final boolean twit, final Context context) {
		r = context.getResources();
		
		String builderMessage = r.getString(R.string.send_social_message) +
				(face ? "\t\t- " + r.getString(R.string.Facebook) + "\n": "") + 
				(twit ? "\t\t- " + r.getString(R.string.Twitter) : "" );
	
		final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_smn_warning, null);
		dialog.setContentView(view);
		
		TextView textMessage = (TextView) view.findViewById(R.id.textWarningMessage);
		Button btnSend = (Button) view.findViewById(R.id.btn_smn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_smn_cancel);
		
		textMessage.setText(builderMessage);
		btnSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (twit) {
					ShareActivity.updateTwitter();
				}
				
				if (face) {
					ShareActivity.updateFacebook();
				}
				
				ShareActivity.resetText();
				
				dialog.dismiss();
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
		return dialog;
	}
}
