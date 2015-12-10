package com.tonggou.gsm.andclient.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.service.NewVersionBroadcastReceiver;
import com.tonggou.gsm.andclient.service.PollingMessageService;
import com.tonggou.gsm.andclient.service.UpdateVehicleLocationBroadcastReceiver;
import com.tonggou.gsm.andclient.ui.fragment.DrivingTrackFragment;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;

/**
 * @author lwz
 *
 */
public class DriveTrackAcivity extends BackableTitleBarActivity implements OnClickListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PollingMessageService.startPolling(this);
		setContentView(R.layout.activity_drive_record_content_frame);
	}

	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_driving_track, Color.WHITE)
				.setLeftButton(R.drawable.ic_titlebar_back);
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
		PollingMessageService.stopPolling(this);
		NewVersionBroadcastReceiver.unregister(this);
		super.onDestroy();
	}

	public void showCloseMenu() {
//		SlidingMenu slidingMenu = getSlidingMenu();
//		if( slidingMenu.isMenuShowing() || slidingMenu.isSecondaryMenuShowing()	 ) {
//			slidingMenu.showContent(true);
//		}
	}

	@Override
	public void onClick(View v) {
	}
}
