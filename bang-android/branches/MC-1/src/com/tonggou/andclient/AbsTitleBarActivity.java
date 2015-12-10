package com.tonggou.andclient;

import com.tonggou.andclient.myview.SimpleTitleBar;

import android.os.Bundle;
import android.view.Window;

public abstract class AbsTitleBarActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getContentLayout());
		findViews();
	}
	
	protected void findViews() {
		SimpleTitleBar titleBar = (SimpleTitleBar) findViewById(R.id.titlebar);
		afterTitleBarCreated(titleBar);
	}
	
	protected void afterTitleBarCreated(SimpleTitleBar titleBar ) {
		
	}

	protected abstract int getContentLayout();
}
