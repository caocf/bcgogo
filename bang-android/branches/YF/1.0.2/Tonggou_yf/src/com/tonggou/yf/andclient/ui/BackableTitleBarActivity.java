package com.tonggou.yf.andclient.ui;

import android.view.View;

import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.widget.view.SimpleTitleBar;

/**
 * 带返回键的标题栏 Activity
 * @author lwz
 *
 */
public class BackableTitleBarActivity extends SimpleTitleBarActivity {
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		titleBar.setLeftButton(R.drawable.ic_titlebar_back)
				.setOnLeftButtonClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});
	}
	
}
