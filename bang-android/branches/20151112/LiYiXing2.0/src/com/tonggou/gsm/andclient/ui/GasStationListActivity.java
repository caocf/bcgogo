package com.tonggou.gsm.andclient.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.tonggou.gsm.andclient.R;

/**
 * 加油站列表
 * @author lwz
 *
 */
public class GasStationListActivity extends BackableTitleBarActivity implements OnItemClickListener {
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_gas_station_list);

		getTitleBar().setTitle(R.string.title_gas_station_list, R.color.black);

		if( !restoreExtras(getIntent()) ) {
			restoreExtras(savedInstance);
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}
}
