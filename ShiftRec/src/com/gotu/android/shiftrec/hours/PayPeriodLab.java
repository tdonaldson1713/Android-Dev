package com.gotu.android.shiftrec.hours;

import android.content.Context;

import com.gotu.android.shiftrec.database.PayPeriodDatabaseHelper;
import com.gotu.android.shiftrec.database.PayPeriodDatabaseHelper.PayPeriodCursor;

public class PayPeriodLab {
	private static PayPeriodLab sPayPeriodLab;
	private Context mContext;
	private PayPeriodDatabaseHelper mHelper;
	
	private PayPeriodLab(Context appContext) {
		mContext = appContext;
		mHelper = new PayPeriodDatabaseHelper(appContext);
	}
	
	public static PayPeriodLab get(Context c) {
		if (sPayPeriodLab == null) {
			sPayPeriodLab = new PayPeriodLab(c.getApplicationContext());
		}
		
		return sPayPeriodLab;
	}
	
	public long insert(PayPeriod period) {
		return mHelper.insertPayPeriod(period);
	}

	public PayPeriodCursor update_week(PayPeriod period) {
		return mHelper.updateWeek(period);
	}
	
	public PayPeriodCursor update(PayPeriod period) {
		return mHelper.updatePayTable(period);
	}
	
	public PayPeriodCursor delete(PayPeriod period) {
		return mHelper.deletePayPeriod(period);
	}
	
	public PayPeriodCursor getAll() {
		return mHelper.getPayPeriodTable();
	}
	
	public PayPeriod find(long id) {
		PayPeriod period = new PayPeriod(false);
		PayPeriodCursor cursor = mHelper.getPayPeriod(id);
		cursor.moveToFirst();
		
		if (!cursor.isAfterLast()) {
			period = cursor.getPayPeriodInfo();
		}
		
		cursor.close();
		return period;
	}
}
