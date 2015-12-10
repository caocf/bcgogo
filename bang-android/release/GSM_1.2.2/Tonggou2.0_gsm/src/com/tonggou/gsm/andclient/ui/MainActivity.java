package com.tonggou.gsm.andclient.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.service.NewVersionBroadcastReceiver;
import com.tonggou.gsm.andclient.service.NewVersionBroadcastReceiver.OnGotNewVersionListener;
import com.tonggou.gsm.andclient.service.PollingMessageService;
import com.tonggou.gsm.andclient.service.UpdateService;
import com.tonggou.gsm.andclient.service.UpdateVehicleLocationBroadcastReceiver;
import com.tonggou.gsm.andclient.ui.fragment.DrivingTrackFragment;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;

/**
 * 主 Activity, 侧滑
 * @author lwz
 *
 */
public class MainActivity extends AbsDoubleBackPressedExitActivity implements OnClickListener, OnGotNewVersionListener {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		App.getInstance().initBMapManager();
		super.onCreate(savedInstanceState);
		PollingMessageService.startPolling(this);
		NewVersionBroadcastReceiver.register(this, this);
		
		setContentView(R.layout.activity_main_content_frame);
		UpdateService.update(this, true, null);
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_driving_track, Color.WHITE)
				.setLeftButton(R.drawable.ic_titlebar_menu)
				.setRightImageButton(R.drawable.ic_titlebar_userinfo, android.R.color.transparent)
				.setOnLeftButtonClickListener(this)
				.setOnRightButtonClickListener(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		DrivingTrackFragment fragment = (DrivingTrackFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_driving_track);
		UpdateVehicleLocationBroadcastReceiver.register(this, fragment.getTrackMap());
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		UpdateVehicleLocationBroadcastReceiver.unregister(this);
	}
	
	@Override
	protected void onDestroy() {
		App.getInstance().releaseBMapManager();
		PollingMessageService.stopPolling(this);
		NewVersionBroadcastReceiver.unregister(this);
		super.onDestroy();
	}

	@Override
	public int getLeftMenuLayoutRes() {
		return R.layout.menu_left_frame;
	}
	
	@Override
	public int getRightMenuLayoutRes() {
		return R.layout.menu_right_frame;
	}

	@Override
	public void onClick(View v) {
		SlidingMenu slidingMenu = getSlidingMenu();
		if( slidingMenu.isMenuShowing() ) {
			slidingMenu.showContent(true);
			return;
		} 
		if( v.getId() == R.id.titlebar_left_btn ) {
			slidingMenu.showMenu(true);
		} else {
			slidingMenu.showSecondaryMenu(true);
		}
	}
	
	public void showCloseMenu() {
//		SlidingMenu slidingMenu = getSlidingMenu();
//		if( slidingMenu.isMenuShowing() || slidingMenu.isSecondaryMenuShowing()  ) {
//			slidingMenu.showContent(true);
//		}
	}

	@Override
	boolean isCanExit() {
		return getSlidingMenu().isMenuShowing();
	}

	@Override
	public void onGotNewVersion() {
		OnGotNewVersionListener l = (OnGotNewVersionListener) getSupportFragmentManager().findFragmentById(R.id.fragment_menu_right);
		l.onGotNewVersion();
		getTitleBar().showIndicator(false, true);
	}
	
}
