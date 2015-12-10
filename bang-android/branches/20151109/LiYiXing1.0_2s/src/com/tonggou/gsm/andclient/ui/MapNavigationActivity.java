package com.tonggou.gsm.andclient.ui;

import android.graphics.Color;
import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.service.LocationServcice.OnLocationReceiveListener;

/**
 * 地图导航 Activity
 * @author lwz
 *
 */
public class MapNavigationActivity extends BackableTitleBarActivity implements OnLocationReceiveListener {

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_map_navigation);
		getTitleBar().setTitle("导航", Color.BLACK);
		
		if( !restoreExtras(getIntent()) ) {
			if( !restoreExtras(savedInstance) ) {
				App.showLongToast("定位数据获取失败");
				finish();
				return;
			}
		}
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
	}
}