package com.tonggou.gsm.andclient.test;

import android.os.Bundle;
import android.view.View;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.ui.SimpleTitleBarActivity;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;

public class TestSimpleTitleBarActivity extends SimpleTitleBarActivity {
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.test_activity_simple_titlebar);
		
		SimpleTitleBar titleBar = getTitleBar();
		titleBar.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
		titleBar.setTitle("行车一键通")
			.setLeftButton(android.R.color.black).setOnLeftButtonClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					App.showShortToast("left btn click");
				}
			})
			.setRightButton("REFRESH", android.R.color.black)
			.setOnRightButtonClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					App.showShortToast("REFRESH btn click");
				}
			});
	}
}
