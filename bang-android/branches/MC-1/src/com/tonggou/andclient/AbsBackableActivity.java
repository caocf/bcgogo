package com.tonggou.andclient;

import android.view.View;

import com.tonggou.andclient.myview.SimpleTitleBar;

public abstract class AbsBackableActivity extends AbsTitleBarActivity {

	@Override
	protected void afterTitleBarCreated(SimpleTitleBar titleBar) {
		super.afterTitleBarCreated(titleBar);
		titleBar.setLeftButton(R.drawable.back)
		.setOnLeftButtonClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
}
