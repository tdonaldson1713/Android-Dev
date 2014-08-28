package com.gotu.android.shiftrec.dialogs;

import java.text.DecimalFormat;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.gotu.android.shiftrec.HourGridActivity;
import com.gotu.android.shiftrec.R;

public class TimePickerDialog {
	public static Dialog createTimePickerDialog(final Context context, final boolean isClockIn) {
		final DecimalFormat df = new DecimalFormat("0");
		final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
		dialog.setCancelable(false);		View view = LayoutInflater.from(context).inflate(R.layout.dialog_time_picker, null);
		dialog.setContentView(view);

		TextView textHeader = (TextView) view.findViewById(R.id.editTitle_TimePicker);
		textHeader.setTypeface(HourGridActivity.unispace);
		Button btnSave = (Button) view.findViewById(R.id.btnSave_Time);
		btnSave.setTypeface(HourGridActivity.unispace);
		Button btnCancel = (Button) view.findViewById(R.id.btnCancel_Time);
		btnCancel.setTypeface(HourGridActivity.unispace);

		final TimePicker timePicker = (TimePicker) view.findViewById(R.id.hoursTimePicker);
		
		btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String hour = df.format(timePicker.getCurrentHour());
				String minute = df.format(timePicker.getCurrentMinute());
			
				int min = Integer.valueOf(minute);
				
				if (isClockIn) {
					if (min < 10) {
						EditDialog.editHours_ClockIn.setText(hour + ":0" + minute);
					} else {
						EditDialog.editHours_ClockIn.setText(hour + ":" + minute);
					}
					
				} else {
					if (min < 10) {
						EditDialog.editHours_ClockOut.setText(hour + ":0" + minute);
					} else {
						EditDialog.editHours_ClockOut.setText(hour + ":" + minute);
					}
				}
				
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
