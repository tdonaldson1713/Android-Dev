package com.gotu.android.shiftrec;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.gotu.android.shiftrec.dialogs.EditDialog;
import com.gotu.android.shiftrec.hours.PayPeriod;

public class GridItemAdapter extends ArrayAdapter<PayPeriod> {
	private Context context;
	private static ArrayList<PayPeriod> hours;

	public GridItemAdapter(ArrayList<PayPeriod> periods, Context mContext) { 
		super(mContext, 0, periods);
		context = mContext;

		hours = new ArrayList<PayPeriod>();
		hours.addAll(periods);
	}

	public static void addPayPeriod(PayPeriod period) {
		hours.add(period);
	}
	
	public void addAllPayPeriods(ArrayList<PayPeriod> periods) {
		if (hours.size() > 1) {
			hours.clear();
		}
		
		hours.addAll(periods);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return hours.size();
	}

	@Override
	public PayPeriod getItem(int position) {
		// TODO Auto-generated method stub
		return hours.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup container) {
		View main_layout = HourGridActivity.flipCard(context, container, position, hours.get(position));
		
		return main_layout; 
	} 
}
