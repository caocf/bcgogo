package com.tonggou.andclient;

import android.os.Bundle;
import android.view.View;

import com.tonggou.andclient.myview.SimpleTitleBar;

public abstract class AbsBackableActivity extends AbsTitleBarActivity {
	
	@Override
	protected void afterTitleBarCreated(SimpleTitleBar titleBar, Bundle savedInstanceState) {
		super.afterTitleBarCreated(titleBar, savedInstanceState);
		titleBar.setLeftButton(R.drawable.back)
		.setOnLeftButtonClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
}
