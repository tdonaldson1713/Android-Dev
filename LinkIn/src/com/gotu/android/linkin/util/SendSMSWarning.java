package com.gotu.android.linkin.util;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gotu.android.linkin.R;
import com.gotu.android.linkin.ShareActivity;

public class SendSMSWarning {
	private static Resources r;
	
	public static Dialog SMSWarning(final Context context, final String sms_text, 
			final String phoneNumber, final SmsManager sms) {
		r = context.getResources();
		
		final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_sms_warning, null);
		dialog.setContentView(view);
		
		TextView textTitle = (TextView) view.findViewById(R.id.textSMSTitle);
		TextView textMessage = (TextView) view.findViewById(R.id.textWarningSMSMessage);
		Button btnSend = (Button) view.findViewById(R.id.btn_sms_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_sms_cancel);
		
		textTitle.setText(r.getString(R.string.send_sms_title, phoneNumber));
		textMessage.setText(sms_text);
		btnSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// Send the SMS
				sms.sendTextMessage(phoneNumber, null, sms_text, null, null);
				Toast.makeText(context, r.getString(R.string.sent_sms, phoneNumber), Toast.LENGTH_SHORT).show();
				// Put the message into the database of messages. 
				ContentValues values = new ContentValues();
				values.put("body", sms_text);
				context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
				// Close the dialog
				
				boolean fb = ShareActivity.sharing_to_networks.get(0);
				boolean tw = ShareActivity.sharing_to_networks.get(1);
				
				if (fb || tw) {
					/*PostToSocialNetworks.ShareToSocialNetwork(ShareActivity.sharing_to_networks.get(0), ShareActivity.sharing_to_networks.get(1), context).show();
					*/
					if (tw) {
						ShareActivity.updateTwitter();
					}
					
					if (fb) {
						ShareActivity.updateFacebook();
					}
				} else {
					ShareActivity.resetText();
				}
				
				dialog.dismiss();
			}
		});
		
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Error sending SMS - cancelling all other sharing", Toast.LENGTH_SHORT).show();
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
