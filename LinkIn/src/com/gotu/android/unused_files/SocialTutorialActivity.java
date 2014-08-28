package com.gotu.android.unused_files;

import com.gotu.android.linkin.R;
import com.gotu.android.linkin.SocialActivity;
import com.gotu.android.linkin.R.id;
import com.gotu.android.linkin.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SocialTutorialActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social_tutorial);
		
		SocialActivity.canLoadApi = true;
		
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
