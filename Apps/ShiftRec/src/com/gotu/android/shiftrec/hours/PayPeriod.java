package com.gotu.android.shiftrec.hours;

public class PayPeriod {
	private long mId;
	private double mPayRate;
	private String mPayPeriod;
	private boolean mShowingBack;
	private double mWeeklyHours;
	
	private double mMondayIn;
	private double mTuesdayIn;
	private double mWednesdayIn;
	private double mThursdayIn;
	private double mFridayIn;
	private double mSaturdayIn;
	private double mSundayIn;
	
	private double mMondayOut;
	private double mTuesdayOut;
	private double mWednesdayOut;
	private double mThursdayOut;
	private double mFridayOut;
	private double mSaturdayOut;
	private double mSundayOut;

	public PayPeriod(boolean isShowing) {
		mShowingBack = isShowing;
		mMondayIn = mMondayOut = 0.00;
		mTuesdayIn = mTuesdayOut = 0.00;
		mWednesdayIn = mWednesdayOut = 0.00;
		mThursdayIn = mThursdayOut = 0.00;
		mFridayIn = mFridayOut = 0.00;
		mSaturdayIn = mSaturdayOut = 0.00;
		mSundayIn = mSundayOut = 0.00;
		mPayRate = 0.00;
	}

	public boolean isShowingBack() {
		return mShowingBack;
	}

	public void setShowing(boolean showing) {
		mShowingBack = showing;
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public void setPayRate(double payRate) {
		mPayRate = payRate;
	}

	public double getPayRate() {
		return mPayRate;
	}

	public double getHours() {
		mWeeklyHours = (mMondayOut - mMondayIn) + (mTuesdayOut - mTuesdayIn) + (mWednesdayOut - mWednesdayIn) + (mThursdayOut - mThursdayIn)
				+ (mFridayOut - mFridayIn) + (mSaturdayOut - mSaturdayIn) + (mSundayOut - mSundayIn);
		
		return mWeeklyHours;
	}

	public String getPayPeriod() {
		return mPayPeriod;
	}

	public void setPayPeriod(String payPeriod) {
		mPayPeriod = payPeriod;
	}

	public void setMondayIn(double day) {
		mMondayIn = day;
	}

	public void setTuesdayIn(double day) {
		mTuesdayIn = day;
	}

	public void setWednesdayIn(double day) {
		mWednesdayIn = day;
	}

	public void setThursdayIn(double day) {
		mThursdayIn = day;
	}

	public void setFridayIn(double day) {
		mFridayIn = day;
	}

	public void setSaturdayIn(double day) {
		mSaturdayIn = day;
	}

	public void setSundayIn(double day) {
		mSundayIn = day;
	}

	public void setMondayOut(double day) {
		mMondayOut = day;
	}

	public void setTuesdayOut(double day) {
		mTuesdayOut = day;
	}

	public void setWednesdayOut(double day) {
		mWednesdayOut = day;
	}

	public void setThursdayOut(double day) {
		mThursdayOut = day;
	}

	public void setFridayOut(double day) {
		mFridayOut = day;
	}

	public void setSaturdayOut(double day) {
		mSaturdayOut = day;
	}

	public void setSundayOut(double day) {
		mSundayOut = day;
	}
	
	public double getMonday() {
		return mMondayOut - mMondayIn;
	}

	public double getTuesday() {
		return mTuesdayOut - mTuesdayIn;
	}

	public double getWednesday() {
		return mWednesdayOut - mWednesdayIn;
	}

	public double getThursday() {
		return mThursdayOut - mThursdayIn;
	}

	public double getFriday() {
		return mFridayOut - mFridayIn;
	}

	public double getSaturday() {
		return mSaturdayOut - mSaturdayIn;
	}

	public double getSunday() {
		return mSundayOut - mSundayIn;
	}
	
	public double getMondayOut() {
		return mMondayOut;
	}

	public double getTuesdayOut() {
		return mTuesdayOut;
	}

	public double getWednesdayOut() {
		return mWednesdayOut;
	}

	public double getThursdayOut() {
		return mThursdayOut;
	}

	public double getFridayOut() {
		return mFridayOut;
	}

	public double getSaturdayOut() {
		return mSaturdayOut;
	}

	public double getSundayOut() {
		return mSundayOut;
	}
	
	public double getMondayIn() {
		return mMondayIn;
	}

	public double getTuesdayIn() {
		return mTuesdayIn;
	}

	public double getWednesdayIn() {
		return mWednesdayIn;
	}

	public double getThursdayIn() {
		return mThursdayIn;
	}

	public double getFridayIn() {
		return mFridayIn;
	}

	public double getSaturdayIn() {
		return mSaturdayIn;
	}

	public double getSundayIn() {
		return mSundayIn;
	}

	public double getNetPay() {
		return mPayRate * mWeeklyHours;
	}
}
