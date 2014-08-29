package com.gotu.android.shiftrec.dialogs;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gotu.android.shiftrec.GridItemAdapter;
import com.gotu.android.shiftrec.HourGridActivity;
import com.gotu.android.shiftrec.R;
import com.gotu.android.shiftrec.hours.PayPeriod;
import com.gotu.android.shiftrec.hours.PayPeriodLab;
import com.gotu.android.util.DisableControls;

public class AddPayWeekDialog {
	private static ArrayList<Integer> lengthOfMonths;
	private static Context mContext;
	public static Dialog dialog;
	public static EditText editEnd;
	public static EditText editBegin;
	public static int beginningMonth;
	public static int beginningDay;
	public static int beginningYear;
	public static int endingMonth;
	public static int endingDay;
	public static int endingYear;
	private static View view;	

	public static Dialog createAddPayWeekDialog(final Context context, final GridItemAdapter adapter) {
		mContext = context;
		setupLengthOfMonths();

		dialog = new Dialog(context, R.style.ThemeDialogCustom);
		dialog.setCancelable(false);
		view = LayoutInflater.from(mContext).inflate(R.layout.dialog_add_pay_week, null);
		dialog.setContentView(view);

		TextView textHeader = (TextView) view.findViewById(R.id.editTitle_AddPayWeek);
		textHeader.setTypeface(HourGridActivity.unispace);

		TextView textBegin = (TextView) view.findViewById(R.id.textDialogWeek_Beginning);
		textBegin.setTypeface(HourGridActivity.unispace);

		TextView textEnd = (TextView) view.findViewById(R.id.textDialogWeek_Ending);
		textEnd.setTypeface(HourGridActivity.unispace);

		editBegin = (EditText) view.findViewById(R.id.editDialogWeek_Beginning);
		editBegin.setTypeface(HourGridActivity.unispace);
		editBegin.setRawInputType(InputType.TYPE_NULL);

		editEnd = (EditText) view.findViewById(R.id.editDialogWeek_Ending);
		editEnd.setTypeface(HourGridActivity.unispace);
		editEnd.setRawInputType(InputType.TYPE_NULL);

		Button btnSave = (Button) view.findViewById(R.id.btnSave_PayPeriod);
		btnSave.setTypeface(HourGridActivity.unispace);

		Button btnCancel = (Button) view.findViewById(R.id.btnCancel_PayPeriod);
		btnCancel.setTypeface(HourGridActivity.unispace);

		editBegin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DisableControls.disableKeyboardOnClick(v);
				DatePickerDialog.createDatePickerDialog(context, true).show();
			}
		});

		editBegin.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
					DatePickerDialog.createDatePickerDialog(context, true).show();
				}
			}
		});

		editEnd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DatePickerDialog.createDatePickerDialog(context, false).show();
			}
		});

		editEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					DatePickerDialog.createDatePickerDialog(context, false).show();
				}
			}
		});

		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (editBegin.getText().toString().equals("") || editEnd.getText().toString().equals("")) {
					Toast.makeText(mContext, R.string.pay_week_error, Toast.LENGTH_SHORT).show();
				} else {
					int beginningMonthDays = lengthOfMonths.get(beginningMonth - 1);
					
					// We first need to check if the month's are equal. If they are, we need to check 
					// that the days are exactly 7 days apart. 
					if (beginningYear == endingYear) {
						if (beginningMonth == endingMonth) {
							if ((endingDay - beginningDay) == 6) {
								save();
							} else {
								Toast.makeText(mContext, R.string.six_days_error, Toast.LENGTH_SHORT).show();
							}
						} else if (endingMonth - beginningMonth == 1) { // Checking to see if our months are different.
							if (((beginningMonthDays - beginningDay) + endingDay) == 6) {
								save();
							} else {
								Toast.makeText(mContext, R.string.six_days_error, Toast.LENGTH_SHORT).show();
							}
						} 
					} else if (beginningYear != endingYear) {
						if (endingYear - beginningYear == 1) { // This means beginningMonth = 12 and endingMonth == 1
							if (endingMonth == 1 && beginningMonth == 12) {
								if (((beginningMonthDays - beginningDay) + endingDay) == 6) {
									save();
								} else {
									Toast.makeText(mContext, R.string.six_days_error, Toast.LENGTH_SHORT).show();
								}
							} else {
								Toast.makeText(mContext, R.string.new_year_error, Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(mContext, R.string.one_year_error, Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(mContext, R.string.pay_week_unknown_error, Toast.LENGTH_SHORT).show();
					}
				}
			}
			public void save() {
				PayPeriod period = new PayPeriod(false);
				period.setPayPeriod(DatePickerDialog.createDayString(beginningMonth, beginningDay, beginningYear) + " - " + 
						DatePickerDialog.createDayString(endingMonth, endingDay, endingYear));
				long id = PayPeriodLab.get(mContext).insert(period);
				period.setId(id);
				
				HourGridActivity.getDatabase();
				
				adapter.addAllPayPeriods(HourGridActivity.payPeriods);
				adapter.notifyDataSetChanged();
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


	private static void setupLengthOfMonths() {
		lengthOfMonths = new ArrayList<Integer>();
		lengthOfMonths.add(31); // Jan

		// This accounts for leap years. Leap years only appear on years that are mod 4.
		if (Calendar.getInstance().get(Calendar.YEAR) % 4 == 0) { 
			lengthOfMonths.add(29); // Feb
		} else {
			lengthOfMonths.add(28); // Feb
		}

		lengthOfMonths.add(31); // Mar
		lengthOfMonths.add(30); // Apr
		lengthOfMonths.add(31); // May
		lengthOfMonths.add(30); // Jun
		lengthOfMonths.add(31); // Jul
		lengthOfMonths.add(31); // Aug
		lengthOfMonths.add(30); // Sept
		lengthOfMonths.add(31); // Oct
		lengthOfMonths.add(30); // Nov
		lengthOfMonths.add(31); // Dec
	}
}
