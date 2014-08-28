package com.gotu.android.linkin.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.gotu.android.linkin.R;

public class PrivacyPolicyDialog {	
	public static Dialog PrivacyPolicy(final Context context) {
		final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_privacy_policy, null);
		dialog.setContentView(view);
		
		Button btnClose = (Button) view.findViewById(R.id.btn_privacy_close);
		btnClose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
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
