package com.gotu.android.shiftrec.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.gotu.android.shiftrec.HourGridActivity;
import com.gotu.android.shiftrec.R;

public class DatePickerDialog {
	public static Dialog createDatePickerDialog(final Context context, final boolean isBeginning) {
		final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
		dialog.setCancelable(false);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_day_picker, null);
		dialog.setContentView(view);

		TextView textHeader = (TextView) view.findViewById(R.id.editDayPicker);
		textHeader.setTypeface(HourGridActivity.unispace);

		if (isBeginning) {
			textHeader.setText(context.getString(R.string.day_header, R.string.beginning));
		} else {
			textHeader.setText(context.getString(R.string.day_header, R.string.ending));
		}

		Button btnSave = (Button) view.findViewById(R.id.btnSave_DayPicker);
		btnSave.setTypeface(HourGridActivity.unispace);
		Button btnCancel = (Button) view.findViewById(R.id.btnCancel_DayPicker);
		btnCancel.setTypeface(HourGridActivity.unispace);

		final DatePicker dayPicker = (DatePicker) view.findViewById(R.id.datePickerDay);

		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isBeginning) {
					AddPayWeekDialog.beginningMonth = dayPicker.getMonth() + 1;
					AddPayWeekDialog.beginningDay = dayPicker.getDayOfMonth();
					AddPayWeekDialog.beginningYear = dayPicker.getYear();
					AddPayWeekDialog.editBegin.setText(createDayString(AddPayWeekDialog.beginningMonth, 
							AddPayWeekDialog.beginningDay, AddPayWeekDialog.beginningYear));
				} else {
					AddPayWeekDialog.endingMonth = dayPicker.getMonth() + 1;
					AddPayWeekDialog.endingDay = dayPicker.getDayOfMonth();
					AddPayWeekDialog.endingYear = dayPicker.getYear();
					AddPayWeekDialog.editEnd.setText(createDayString(AddPayWeekDialog.endingMonth, 
							AddPayWeekDialog.endingDay, AddPayWeekDialog.endingYear));
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
	
	public static String createDayString(int month, int day, int year) {
		return month + "/" + day + "/" + year;
	}
}
