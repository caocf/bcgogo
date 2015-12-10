package com.tonggou.gsm.andclient.ui;

import android.os.Bundle;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;

/**
 * 电子商城页面
 * @author peter
 *
 */
public class MyWalletActivity extends BackableTitleBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_wallet_frame);
	}

	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_my_wallet);
	}
}