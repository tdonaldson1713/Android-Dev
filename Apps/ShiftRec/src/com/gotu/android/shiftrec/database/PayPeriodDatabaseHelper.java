package com.gotu.android.shiftrec.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gotu.android.shiftrec.hours.PayPeriod;

public class PayPeriodDatabaseHelper  extends SQLiteOpenHelper {
	public static final String TAG = "HoursDatabaseHelper";
	public static final String DB_NAME = "shiftrec.db";
	private static final String COLUMN_ID = "_id";
	public static final int VERSION = 1;
	
	public PayPeriodDatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_PAY_PERIOD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {}
	
	private static final String TABLE_PAY_PERIOD = "pay_period";
	private static final String COLUMN_WEEK = "week"; // The pay period - 7/7/2014 to 7/20/2014
	private static final String COLUMN_PAY = "pay_rate";
	private static final String COLUMN_MON_IN = "day_mon_in";
	private static final String COLUMN_TUE_IN = "day_tue_in";
	private static final String COLUMN_WED_IN = "day_wed_in";
	private static final String COLUMN_THU_IN = "day_thu_in";
	private static final String COLUMN_FRI_IN = "day_fri_in";
	private static final String COLUMN_SAT_IN = "day_sat_in";
	private static final String COLUMN_SUN_IN = "day_sun_in";
	private static final String COLUMN_MON_OUT = "day_mon_out";
	private static final String COLUMN_TUE_OUT = "day_tue_out";
	private static final String COLUMN_WED_OUT = "day_wed_out";
	private static final String COLUMN_THU_OUT = "day_thu_out";
	private static final String COLUMN_FRI_OUT = "day_fri_out";
	private static final String COLUMN_SAT_OUT = "day_sat_out";
	private static final String COLUMN_SUN_OUT = "day_sun_out";
	
	private static final String CREATE_TABLE_PAY_PERIOD = "create table " + TABLE_PAY_PERIOD +
			"(" + COLUMN_ID + " integer primary key autoincrement, " +
			COLUMN_WEEK + " varchar(100), " +
			COLUMN_PAY + " real, " +
			COLUMN_MON_IN + " real, " +
			COLUMN_TUE_IN + " real, " +
			COLUMN_WED_IN + " real, " +
			COLUMN_THU_IN + " real, " +
			COLUMN_FRI_IN + " real, " +
			COLUMN_SAT_IN + " real, " +
			COLUMN_SUN_IN + " real, " + 
			COLUMN_MON_OUT + " real, " +
			COLUMN_TUE_OUT + " real, " +
			COLUMN_WED_OUT + " real, " +
			COLUMN_THU_OUT + " real, " +
			COLUMN_FRI_OUT + " real, " +
			COLUMN_SAT_OUT + " real, " +
			COLUMN_SUN_OUT + " real);";
	private static final String UPDATE_WEEK = "UPDATE " + TABLE_PAY_PERIOD + 
			" SET " + COLUMN_WEEK + " = ? " +
			"WHERE _id = ?";
	private static final String UPDATE_PAY_TABLE = "UPDATE " + TABLE_PAY_PERIOD + 
			" SET " + 
			COLUMN_WEEK + " = ?, " +
			COLUMN_PAY + " = ?, " +
			COLUMN_MON_IN + " = ?, " +
			COLUMN_TUE_IN + " = ?, " +
			COLUMN_WED_IN + " = ?, " +
			COLUMN_THU_IN + " = ?, " +
			COLUMN_FRI_IN + " = ?, " +
			COLUMN_SAT_IN + " = ?, " +
			COLUMN_SUN_IN + " = ?, " +
			COLUMN_MON_OUT + " = ?, " +
			COLUMN_TUE_OUT + " = ?, " +
			COLUMN_WED_OUT + " = ?, " +
			COLUMN_THU_OUT + " = ?, " +
			COLUMN_FRI_OUT + " = ?, " +
			COLUMN_SAT_OUT + " = ?, " +
			COLUMN_SUN_OUT + " = ? " +
			"WHERE _id = ?";
	private static final String DELETE_PAY_PERIOD = "DELETE FROM " + TABLE_PAY_PERIOD + 
			" WHERE _id = ? AND " +
			COLUMN_WEEK + " = ?";
	
	public static class PayPeriodCursor extends CursorWrapper {

		public PayPeriodCursor(Cursor cursor) {
			super(cursor);
		}
		
		public PayPeriod getPayPeriodInfo() {
			if (isBeforeFirst() || isAfterLast()) {
				return null;
			}
			
			PayPeriod period = new PayPeriod(false);
			
			period.setId(getLong(getColumnIndex(COLUMN_ID)));
			period.setPayPeriod(getString(getColumnIndex(COLUMN_WEEK)));
			period.setPayRate(getDouble(getColumnIndex(COLUMN_PAY)));
			
			period.setMondayIn(getDouble(getColumnIndex(COLUMN_MON_IN)));
			period.setTuesdayIn(getDouble(getColumnIndex(COLUMN_TUE_IN)));
			period.setWednesdayIn(getDouble(getColumnIndex(COLUMN_WED_IN)));
			period.setThursdayIn(getDouble(getColumnIndex(COLUMN_THU_IN)));
			period.setFridayIn(getDouble(getColumnIndex(COLUMN_FRI_IN)));
			period.setSaturdayIn(getDouble(getColumnIndex(COLUMN_SAT_IN)));
			period.setSundayIn(getDouble(getColumnIndex(COLUMN_SUN_IN)));
			
			period.setMondayOut(getDouble(getColumnIndex(COLUMN_MON_OUT)));
			period.setTuesdayOut(getDouble(getColumnIndex(COLUMN_TUE_OUT)));
			period.setWednesdayOut(getDouble(getColumnIndex(COLUMN_WED_OUT)));
			period.setThursdayOut(getDouble(getColumnIndex(COLUMN_THU_OUT)));
			period.setFridayOut(getDouble(getColumnIndex(COLUMN_FRI_OUT)));
			period.setSaturdayOut(getDouble(getColumnIndex(COLUMN_SAT_OUT)));
			period.setSundayOut(getDouble(getColumnIndex(COLUMN_SUN_OUT)));
			
			return period;
		}
	}
	
	public long insertPayPeriod(PayPeriod period) {
		ContentValues cv = new ContentValues();
		
		cv.put(COLUMN_WEEK, period.getPayPeriod());
		cv.put(COLUMN_PAY, period.getPayRate());
		
		cv.put(COLUMN_MON_IN, period.getMondayIn());
		cv.put(COLUMN_TUE_IN, period.getTuesdayIn());
		cv.put(COLUMN_WED_IN, period.getWednesdayIn());
		cv.put(COLUMN_THU_IN, period.getThursdayIn());
		cv.put(COLUMN_FRI_IN, period.getFridayIn());
		cv.put(COLUMN_SAT_IN, period.getSaturdayIn());
		cv.put(COLUMN_SUN_IN, period.getSundayIn());
		
		cv.put(COLUMN_MON_OUT, period.getMondayOut());
		cv.put(COLUMN_TUE_OUT, period.getTuesdayOut());
		cv.put(COLUMN_WED_OUT, period.getWednesdayOut());
		cv.put(COLUMN_THU_OUT, period.getThursdayOut());
		cv.put(COLUMN_FRI_OUT, period.getFridayOut());
		cv.put(COLUMN_SAT_OUT, period.getSaturdayOut());
		cv.put(COLUMN_SUN_OUT, period.getSundayOut());
		
		
		return getWritableDatabase().insert(TABLE_PAY_PERIOD, null, cv);
	}
	
	// Updates only the days of the week.
	public PayPeriodCursor updateWeek(PayPeriod period) {
		String[] periodInfo = {period.getPayPeriod(), String.valueOf(period.getId())};
		Cursor cursor = getWritableDatabase().rawQuery(UPDATE_WEEK, periodInfo);
		return new PayPeriodCursor(cursor);
	}
	
	// Updates the number of hours and the week, if necessary. 
	public PayPeriodCursor updatePayTable(PayPeriod period) {
		String[] periodInfo = {period.getPayPeriod(), String.valueOf(period.getPayRate()),
				String.valueOf(period.getMondayIn()), String.valueOf(period.getTuesdayIn()), 
				String.valueOf(period.getWednesdayIn()), String.valueOf(period.getThursdayIn()), 
				String.valueOf(period.getFridayIn()), String.valueOf(period.getSaturdayIn()), 
				String.valueOf(period.getSundayIn()), String.valueOf(period.getMondayOut()), 
				String.valueOf(period.getTuesdayOut()), String.valueOf(period.getWednesdayOut()),
				String.valueOf(period.getThursdayOut()), String.valueOf(period.getFridayOut()), 
				String.valueOf(period.getSaturdayOut()), String.valueOf(period.getSundayOut()), 
				String.valueOf(period.getId())};
		Cursor cursor = getWritableDatabase().rawQuery(UPDATE_PAY_TABLE, periodInfo);
		return new PayPeriodCursor(cursor);
	}
	
	public PayPeriodCursor deletePayPeriod(PayPeriod period) {
		String[] periodInfo = {String.valueOf(period.getId()), period.getPayPeriod()};
		Cursor cursor = getWritableDatabase().rawQuery(DELETE_PAY_PERIOD, periodInfo);
		return new PayPeriodCursor(cursor);
	}
	
	public PayPeriodCursor getPayPeriod(long id) {
		Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + 
				TABLE_PAY_PERIOD + " WHERE _id = ?", new String[]{String.valueOf(id)});
		return new PayPeriodCursor(cursor);
	}
	
	public PayPeriodCursor getPayPeriodTable() {
		Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_PAY_PERIOD, null);
		return new PayPeriodCursor(cursor);
	}
}
