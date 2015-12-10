package com.tonggou.gsm.andclient.ui;

import java.util.List;

import android.os.Bundle;
import com.baidu.location.BDLocation;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.GasStation;
import com.tonggou.gsm.andclient.service.LocationServcice;
import com.tonggou.gsm.andclient.service.LocationServcice.OnLocationReceiveListener;
import com.tonggou.gsm.andclient.ui.fragment.AbsGasStationLoadDataActivity;

/**
 * 加油站地图 Activity
 * @author lwz
 *
 */
public class GasStationMapActivity extends AbsGasStationLoadDataActivity implements OnLocationReceiveListener {
	@Override
	public void onCreate(Bundle arg0) {
		LocationServcice.registerListener(this);
		super.onCreate(arg0);
		setContentView(R.layout.activity_gas_station_map);
	}

	@Override
	public void onReceiveLocation(BDLocation location) {

	}

	@Override
	public void onUpdateData(List<GasStation> data, boolean isRefresh) {

	}

	@Override
	public void onRequestDataFinish(boolean isSuccess, boolean isRefresh) {

	}
}
