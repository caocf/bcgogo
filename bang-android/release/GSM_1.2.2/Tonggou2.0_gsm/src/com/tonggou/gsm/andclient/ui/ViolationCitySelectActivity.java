package com.tonggou.gsm.andclient.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.bean.Area;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;

/**
 * 违章城市查询
 * @author lwz
 *
 */
public class ViolationCitySelectActivity extends ViolationAreaSelectActivity {

	Area mParentArea;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if( !restoreExtras(getIntent()) ) {
			restoreExtras(savedInstanceState);
		}
		if( mParentArea == null ) {
			App.showShortToast("ERROR");
			finish();
			return;
		}
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra != null && extra.containsKey(EXTRA_AREA) ) {
			mParentArea = extra.getParcelable(EXTRA_AREA);
			return true;
		}
		return false;
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(mParentArea.getName());
	}
	
	@Override
	void initData() {
		mAdapter.update(mParentArea.getChildren());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		resultOK(mAdapter.getData().get(position));
	}
}
