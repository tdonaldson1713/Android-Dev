package com.gotu.android.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class DisableControls {
	public static void disableKeyboardOnClick(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
	}
}
