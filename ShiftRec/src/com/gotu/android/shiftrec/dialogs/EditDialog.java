package com.gotu.android.shiftrec.dialogs;

import java.text.DecimalFormat;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gotu.android.shiftrec.GridItemAdapter;
import com.gotu.android.shiftrec.HourGridActivity;
import com.gotu.android.shiftrec.R;
import com.gotu.android.shiftrec.database.PayPeriodDatabaseHelper.PayPeriodCursor;
import com.gotu.android.shiftrec.hours.PayPeriod;
import com.gotu.android.shiftrec.hours.PayPeriodLab;
import com.gotu.android.util.DisableControls;

public class EditDialog {	
	public static double selectedTime = 0.00;
	public static EditText editHours_ClockIn;
	public static EditText editHours_ClockOut;

	private static Context mContext;
	private static int selectedPos;
	private static boolean canClose_HoursIn = false;
	private static boolean canClose_HoursOut = false;
	private static boolean canClose_PayRate = false;
	private static DecimalFormat dfTwoDecimals;
	private static View view;

	public static Dialog createEditDialog(final Context context, final GridItemAdapter adapter, final PayPeriod period) {
		mContext = context;
		dfTwoDecimals = new DecimalFormat("0.00");

		final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
		dialog.setCancelable(false);
		view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_hours, null);
		dialog.setContentView(view);

		TextView textHoursClockIn = (TextView) view.findViewById(R.id.textDialogHours_ClockIn);
		textHoursClockIn.setTypeface(HourGridActivity.unispace);
		
		TextView textHoursClockOut = (TextView) view.findViewById(R.id.textDialogHours_ClockOut);
		textHoursClockOut.setTypeface(HourGridActivity.unispace);
		
		TextView textPayRate = (TextView) view.findViewById(R.id.textDialogPayRate);
		textPayRate.setTypeface(HourGridActivity.unispace);
		
		TextView textHeader = (TextView) view.findViewById(R.id.editTitle);
		textHeader.setTypeface(HourGridActivity.unispace);

		editHours_ClockIn = (EditText) view.findViewById(R.id.editDialogHours_ClockIn);
		editHours_ClockIn.setTypeface(HourGridActivity.unispace);
		editHours_ClockIn.setRawInputType(InputType.TYPE_NULL);
		
		editHours_ClockOut = (EditText) view.findViewById(R.id.editDialogHours_ClockOut);
		editHours_ClockOut.setTypeface(HourGridActivity.unispace);
		editHours_ClockOut.setRawInputType(InputType.TYPE_NULL);
		
		final EditText editPayRate = (EditText) view.findViewById(R.id.editDialogPayRate);
		editPayRate.setTypeface(HourGridActivity.unispace);
		editPayRate.requestFocus();

		Button btnSave = (Button) view.findViewById(R.id.btnSave);
		btnSave.setTypeface(HourGridActivity.unispace);
		
		Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
		btnCancel.setTypeface(HourGridActivity.unispace);


		String[] days = mContext.getResources().getStringArray(R.array.weekdays);
		Spinner daySpinner = (Spinner) view.findViewById(R.id.spinnerDate);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_weekday, days) {
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				((TextView) v).setTypeface(HourGridActivity.unispace);

				return v;
			}


			public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
				View v =super.getDropDownView(position, convertView, parent);

				((TextView) v).setTypeface(HourGridActivity.unispace);
				return v;
			}
		};

		editHours_ClockIn.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					TimePickerDialog.createTimePickerDialog(mContext, true).show();
				}
			}
		});
		editHours_ClockIn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DisableControls.disableKeyboardOnClick(v);
				TimePickerDialog.createTimePickerDialog(mContext, true).show();
			}
		});
	
		editHours_ClockOut.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (hasFocus) {
					TimePickerDialog.createTimePickerDialog(mContext, false).show();
				}
			}
		}); 
		editHours_ClockOut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TimePickerDialog.createTimePickerDialog(mContext, false).show();
			}
		});

		daySpinner.setAdapter(spinnerAdapter);
		daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				selectedPos = pos;

				switch(pos) {
				case 0:
					setupClockIn(period.getMondayIn());
					setupClockOut(period.getMondayOut());
					break;
				case 1:
					setupClockIn(period.getTuesdayIn());
					setupClockOut(period.getTuesdayOut());
					break;
				case 2:
					setupClockIn(period.getWednesdayIn());
					setupClockOut(period.getWednesdayOut());
					break;
				case 3:
					setupClockIn(period.getThursdayIn());
					setupClockOut(period.getThursdayOut());
					break;
				case 4:
					setupClockIn(period.getFridayIn());
					setupClockOut(period.getFridayOut());
					break;
				case 5:
					setupClockIn(period.getSaturdayIn());
					setupClockOut(period.getSaturdayOut());
					break;
				case 6:
					setupClockIn(period.getSundayIn());
					setupClockOut(period.getSundayOut());
					break;
				}

				editPayRate.setText(dfTwoDecimals.format(period.getPayRate()));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				double hour_in = 0.00;
				double minute_in;
				double hour_out = 0.00;
				double minute_out;
				double time_in = 0.00;
				double time_out = 0.00;

				if (!editHours_ClockIn.getText().toString().equals("")) {
					hour_in = Double.valueOf(editHours_ClockIn.getText().toString().substring(0, editHours_ClockIn.getText().toString().indexOf(":")));
					minute_in = Double.valueOf(editHours_ClockIn.getText().toString().substring(editHours_ClockIn.getText().toString().indexOf(":")+1));
					time_in = hour_in + (minute_in / 100);
					Log.d("TEST", "In: " + time_in);
					canClose_HoursIn = true;
				}

				if (!editHours_ClockOut.getText().toString().equals("")) {
					hour_out = Double.valueOf(editHours_ClockOut.getText().toString().substring(0, editHours_ClockOut.getText().toString().indexOf(":")));
					minute_out = Double.valueOf(editHours_ClockOut.getText().toString().substring(editHours_ClockOut.getText().toString().indexOf(":")+1));
					time_out = hour_out + (minute_out / 100);
					Log.d("TEST", "Out: " + time_out);
					canClose_HoursOut = true;
				}

				switch(selectedPos) {
				case 0:
					if (!editHours_ClockIn.getText().toString().equals("")) {
						period.setMondayIn(time_in); 
					}

					if (!editHours_ClockOut.getText().toString().equals("")) {
						period.setMondayOut(time_out); 
					}

					break;
				case 1:
					if (!editHours_ClockIn.getText().toString().equals("")) {
						period.setTuesdayIn(time_in); 
					}

					if (!editHours_ClockOut.getText().toString().equals("")) {
						period.setTuesdayOut(time_out); 
					}

					break;
				case 2:
					if (!editHours_ClockIn.getText().toString().equals("")) {
						period.setWednesdayIn(time_in); 
					}

					if (!editHours_ClockOut.getText().toString().equals("")) {
						period.setWednesdayOut(time_out); 
					}

					break;
				case 3:
					if (!editHours_ClockIn.getText().toString().equals("")) {
						period.setThursdayIn(time_in); 
					}

					if (!editHours_ClockOut.getText().toString().equals("")) {
						period.setThursdayOut(time_out); 
					}

					break;
				case 4:
					if (!editHours_ClockIn.getText().toString().equals("")) {
						period.setFridayIn(time_in); 
					}

					if (!editHours_ClockOut.getText().toString().equals("")) {
						period.setFridayOut(time_out); 
					}

					break;
				case 5:
					if (!editHours_ClockIn.getText().toString().equals("")) {
						period.setSaturdayIn(time_in); 
					}

					if (!editHours_ClockOut.getText().toString().equals("")) {
						period.setSaturdayOut(time_out); 
					}

					break;
				case 6:
					if (!editHours_ClockIn.getText().toString().equals("")) {
						period.setSundayIn(time_in); 
					}

					if (!editHours_ClockOut.getText().toString().equals("")) {
						period.setSundayOut(time_out); 
					}

					break;
				}

				if (!editPayRate.getText().toString().equals("") && !editPayRate.getText().toString().equals("0.00")) {
					period.setPayRate(Double.valueOf(editPayRate.getText().toString()));
					canClose_PayRate = true;
				}

				if (canClose_HoursIn && canClose_HoursOut && canClose_PayRate) {
					PayPeriodCursor id = PayPeriodLab.get(mContext).update(period);
					id.moveToFirst();
					PayPeriod p = id.getPayPeriodInfo();
					id.close();

					dialog.dismiss();
					adapter.notifyDataSetChanged();
				} else {
					Toast.makeText(mContext, R.string.edit_error, Toast.LENGTH_SHORT).show();
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

		return dialog;
	}	

	private static void setupClockIn(double hours) {
		int hour;
		String time;

		time = String.valueOf(hours);
		hour = Integer.valueOf(time.substring(0, time.indexOf(".")));
		String minute = time.substring(time.indexOf(".")+1);

		if (minute.equals("0")) {
			editHours_ClockIn.setText(hour + ":" + minute + "0");
		} else if (minute.startsWith("0") || !minute.contains("0")) {
			editHours_ClockIn.setText(hour + ":" + minute);
		} else {
			editHours_ClockIn.setText(hour + ":" + minute + "0");
		}
	}

	private static void setupClockOut(double hours) {
		int hour;
		String time;

		time = String.valueOf(hours);
		hour = Integer.valueOf(time.substring(0, time.indexOf(".")));
		String minute = time.substring(time.indexOf(".")+1);

		if (minute.equals("0")) {
			editHours_ClockOut.setText(hour + ":" + minute + "0");
		} else if (minute.startsWith("0") || !minute.contains("0")) {
			editHours_ClockOut.setText(hour + ":" + minute);
		} else {
			editHours_ClockOut.setText(hour + ":" + minute + "0");
		}
	}
}
