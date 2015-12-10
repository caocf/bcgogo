package com.tonggou.andclient;

import com.tonggou.andclient.myview.SimpleTitleBar;

import android.os.Bundle;
import android.view.Window;

public abstract class AbsTitleBarActivity extends BaseActivity {

	private SimpleTitleBar mTitleBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getContentLayout());
		findViews(savedInstanceState);
	}
	
	protected void findViews(Bundle savedInstanceState) {
		mTitleBar = (SimpleTitleBar) findViewById(R.id.titlebar);
		afterTitleBarCreated(mTitleBar, savedInstanceState);
	}
	
	protected void afterTitleBarCreated(SimpleTitleBar titlebar, Bundle savedInstanceState) {
		
	}
	
	protected SimpleTitleBar getSimpleTitle() {
		return mTitleBar;
	}

	protected abstract int getContentLayout();
}
