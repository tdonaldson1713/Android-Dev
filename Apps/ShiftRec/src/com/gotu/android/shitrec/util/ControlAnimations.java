package com.gotu.android.shitrec.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gotu.android.shiftrec.GridItemAdapter;
import com.gotu.android.shiftrec.hours.PayPeriod;

public class ControlAnimations {
	public static void startAnimation(final LinearLayout linear, final PayPeriod hour, final GridItemAdapter adapter) {
		hour.setShowing(!hour.isShowingBack());

		ObjectAnimator anim = ObjectAnimator.ofFloat(linear, View.ALPHA, 0);
		anim.setDuration(300);

		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				adapter.notifyDataSetChanged();
				linear.setAlpha(1);
			}
		});
		anim.start();
	}

	public static void startAnimation(final Button linear, final PayPeriod hour, final GridItemAdapter adapter) {	
		hour.setShowing(!hour.isShowingBack());
		
		ObjectAnimator anim = ObjectAnimator.ofFloat(linear, View.ALPHA, 0);
		anim.setDuration(300);

		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				adapter.notifyDataSetChanged();
				linear.setAlpha(1);
			}
		});
		anim.start();
	}

	public static void startAnimation(final RelativeLayout realtive, final PayPeriod hour, final GridItemAdapter adapter) {						
		ObjectAnimator anim = ObjectAnimator.ofFloat(realtive, View.ALPHA, 0);
		anim.setDuration(300);

		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				adapter.notifyDataSetChanged();
				realtive.setAlpha(1);
			}
		});
		anim.start();
	}
}
