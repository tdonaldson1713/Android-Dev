package com.gotu.android.shiftrec;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.gotu.android.shiftrec.database.PayPeriodDatabaseHelper.PayPeriodCursor;
import com.gotu.android.shiftrec.dialogs.AddPayWeekDialog;
import com.gotu.android.shiftrec.dialogs.EditDialog;
import com.gotu.android.shiftrec.hours.PayPeriod;
import com.gotu.android.shiftrec.hours.PayPeriodLab;
import com.gotu.android.shitrec.util.ControlAnimations;
 // Adding a pay week: AddPayWeekDialog.createAddPayWeekDialog(activity, adapter).show();
public class HourGridActivity extends Activity {
	private static Activity activity;
	private static Resources r;
	private GridView gridView;
	private static GridItemAdapter adapter;
	public static ArrayList<PayPeriod> payPeriods;
	public static Typeface unispace;
	private static DecimalFormat df;

	private void setupDatabase() {
		PayPeriod period = new PayPeriod(false);
		period.setPayPeriod("7/7/2014 - 7/13/2014");
		PayPeriodLab.get(this).insert(period);

		PayPeriod period1 = new PayPeriod(false);
		period1.setPayPeriod("7/14/2014 - 7/20/2014");
		PayPeriodLab.get(this).insert(period1);

		PayPeriod period2 = new PayPeriod(false);
		period2.setPayPeriod("7/21/2014 - 7/27/2014");
		PayPeriodLab.get(this).insert(period2);

		PayPeriod period3 = new PayPeriod(false);
		period3.setPayPeriod("7/28/2014 - 8/3/2014");
		PayPeriodLab.get(this).insert(period3); 	
	}

	public static void getDatabase() {
		if (payPeriods.size() > 0) {
			payPeriods.clear();
		}
		
		PayPeriodCursor cursor = PayPeriodLab.get(activity).getAll();
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			PayPeriod period = cursor.getPayPeriodInfo();
			payPeriods.add(period);
			cursor.moveToNext();
		}

		cursor.close();
	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.add_pay_week:
			AddPayWeekDialog.createAddPayWeekDialog(activity, adapter).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hour_grid);
		payPeriods = new ArrayList<PayPeriod>();

		setupAds();
		resourceSetup();

		//setupDatabase();
		getDatabase();

		gridView = (GridView) findViewById(R.id.gv_Grid);
		adapter = new GridItemAdapter(payPeriods, getApplicationContext());
		gridView.setAdapter(adapter);
	}

	private void resourceSetup() {
		activity = HourGridActivity.this;
		r = getResources();
		df = new DecimalFormat("0.00");
		unispace = Typeface.createFromAsset(getAssets(), "fonts/unispace.ttf");
	}

	private void setupAds() {
		AdView adView = (AdView)this.findViewById(R.id.adHour);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	private static View flipBackCard(final Context context, final ViewGroup container, final int pos, final PayPeriod hour) {
		final View back_view = LayoutInflater.from(context).inflate(R.layout.grid_item_card_back, container, false);
		backCardTable(back_view, hour);

		final Button btnEdit = (Button) back_view.findViewById(R.id.btnEdit);
		btnEdit.setTypeface(unispace);
		btnEdit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditDialog.createEditDialog(activity, adapter, hour).show();
			}
		});

		final Button btnOverview = (Button) back_view.findViewById(R.id.btnOverview);
		btnOverview.setTypeface(unispace);
		btnOverview.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final LinearLayout linear = (LinearLayout) back_view.findViewById(R.id.linearlayout_backcard);
				final RelativeLayout linDate = (RelativeLayout) back_view.findViewById(R.id.linearlayout_back);
				ControlAnimations.startAnimation(linear, hour, adapter);
				ControlAnimations.startAnimation(btnOverview, hour, adapter);
				ControlAnimations.startAnimation(btnEdit, hour, adapter);
				ControlAnimations.startAnimation(linDate, hour, adapter);
			}
		});

		return back_view;
	}

	public static View flipCard(final Context context, final ViewGroup container, final int pos, final PayPeriod hour) {
		final View front_view;

		if (hour.isShowingBack()) {
			front_view = flipBackCard(context, container, pos, hour);
		} else {
			front_view = LayoutInflater.from(context).inflate(R.layout.grid_item_card_front, container, false);
			frontCardTable(front_view, hour);

			final Button btnEdit = (Button) front_view.findViewById(R.id.btnEdit);
			btnEdit.setTypeface(unispace);
			btnEdit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					EditDialog.createEditDialog(activity, adapter, hour).show();
				}
			});

			final Button btnDetails = (Button) front_view.findViewById(R.id.btnDetails);
			btnDetails.setTypeface(unispace);
			btnDetails.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					final LinearLayout linear = (LinearLayout) front_view.findViewById(R.id.linearlayout_frontcard);
					final RelativeLayout linDate = (RelativeLayout) front_view.findViewById(R.id.linearlayout_front);
					ControlAnimations.startAnimation(linear, hour, adapter);
					ControlAnimations.startAnimation(btnDetails, hour, adapter);
					ControlAnimations.startAnimation(btnEdit, hour, adapter);
					ControlAnimations.startAnimation(linDate, hour, adapter);
				}
			});
		}

		TextView text = (TextView) front_view.findViewById(R.id.text_date);
		text.setText(manipulateString(hour.getPayPeriod()));
		text.setTypeface(unispace);

		return front_view;
	}

	private static void frontCardTable(View front_view, final PayPeriod hour) {
		TextView textHoursHeader = (TextView) front_view.findViewById(R.id.textHoursHeader);
		textHoursHeader.setTypeface(unispace);

		TextView textPayRateHeader = (TextView) front_view.findViewById(R.id.textPayRateHeader);
		textPayRateHeader.setTypeface(unispace);

		TextView textNetPayHeader = (TextView) front_view.findViewById(R.id.textNetPayHeader);
		textNetPayHeader.setTypeface(unispace);

		TextView textHoursWeekly = (TextView) front_view.findViewById(R.id.textHoursWeekly);
		textHoursWeekly.setTypeface(unispace);
		textHoursWeekly.setText(r.getString(R.string.weekly_hours, df.format(hour.getHours())));

		TextView textPayRate = (TextView) front_view.findViewById(R.id.textPayRate);
		textPayRate.setTypeface(unispace);
		textPayRate.setText(r.getString(R.string.pay_rate, df.format(hour.getPayRate())));

		TextView textNetPay = (TextView) front_view.findViewById(R.id.textNetPay);
		textNetPay.setTypeface(unispace);
		textNetPay.setText(r.getString(R.string.net_pay, df.format(hour.getNetPay())));
	}

	private static void backCardTable(View back_view, final PayPeriod hour) {

		// Days Column
		TextView textMon = (TextView) back_view.findViewById(R.id.textMon);
		textMon.setTypeface(unispace);

		TextView textTue = (TextView) back_view.findViewById(R.id.textTues);
		textTue.setTypeface(unispace);

		TextView textWed = (TextView) back_view.findViewById(R.id.textWed);
		textWed.setTypeface(unispace);

		TextView textThurs = (TextView) back_view.findViewById(R.id.textThurs);
		textThurs.setTypeface(unispace);

		TextView textFri = (TextView) back_view.findViewById(R.id.textFri);
		textFri.setTypeface(unispace);

		TextView textSat = (TextView) back_view.findViewById(R.id.textSat);
		textSat.setTypeface(unispace);

		TextView textSun = (TextView) back_view.findViewById(R.id.textSun);
		textSun.setTypeface(unispace);

		// Hours Column
		TextView textMonHours = (TextView) back_view.findViewById(R.id.textMonHours);
		textMonHours.setTypeface(unispace);
		textMonHours.setText(r.getString(R.string.daily_hours, df.format(hour.getMonday())));

		TextView textTueHours = (TextView) back_view.findViewById(R.id.textTuesHours);
		textTueHours.setTypeface(unispace);
		textTueHours.setText(r.getString(R.string.daily_hours, df.format(hour.getTuesday())));

		TextView textWedHours = (TextView) back_view.findViewById(R.id.textWedHours);
		textWedHours.setTypeface(unispace);
		textWedHours.setText(r.getString(R.string.daily_hours, df.format(hour.getWednesday())));

		TextView textThursHours = (TextView) back_view.findViewById(R.id.textThursHours);
		textThursHours.setTypeface(unispace);
		textThursHours.setText(r.getString(R.string.daily_hours, df.format(hour.getThursday())));

		TextView textFriHours = (TextView) back_view.findViewById(R.id.textFriHours);
		textFriHours.setTypeface(unispace);
		textFriHours.setText(r.getString(R.string.daily_hours, df.format(hour.getFriday())));

		TextView textSatHours = (TextView) back_view.findViewById(R.id.textSatHours);
		textSatHours.setTypeface(unispace);
		textSatHours.setText(r.getString(R.string.daily_hours, df.format(hour.getSaturday())));

		TextView textSunHours = (TextView) back_view.findViewById(R.id.textSunHours);
		textSunHours.setTypeface(unispace);
		textSunHours.setText(r.getString(R.string.daily_hours, df.format(hour.getSunday())));
	}

	private static String manipulateString(String text) {
		return text;
	}
}
