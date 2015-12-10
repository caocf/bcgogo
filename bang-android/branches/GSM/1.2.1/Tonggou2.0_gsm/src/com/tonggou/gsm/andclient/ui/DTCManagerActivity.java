package com.tonggou.gsm.andclient.ui;

import java.util.ArrayList;

import android.R.color;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.DTCInfo;
import com.tonggou.gsm.andclient.ui.fragment.AbsPullToRefreshLazyLoadFragment;
import com.tonggou.gsm.andclient.ui.fragment.CurrentDTCListFragment;
import com.tonggou.gsm.andclient.ui.fragment.DTCListFragment;
import com.tonggou.gsm.andclient.ui.fragment.HistoryDTCListFragment;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.ContextUtil;

/**
 * 故障查询界面
 * @author lwz
 *
 */
public class DTCManagerActivity extends AbsTabBackablePullToRefreshActivity implements OnPageChangeListener {
	
	@Override
	int getLayoutRes() {
		return R.layout.activity_dtc_manager;
	}

	@Override
	int getTabViewId() {
		return R.id.tab_view;
	}

	@Override
	int getViewPagerId() {
		return R.id.viewpager;
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_dtc_manager);
		titleBar.setRightButton(getString(R.string.btn_titlebar), color.transparent);
		titleBar.setOnRightButtonClickListener( null );
	}
	
	public void showAppointmentButton() {
		getTitleBar().setOnRightButtonClickListener(mRightTitleBtnClickListener);
	}
	
	public void hideAppointmentButton() {
		getTitleBar().setOnRightButtonClickListener(null);
	}
	
	private OnClickListener mRightTitleBtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			toAppointmentActivity();
		}
	};
	
	private void toAppointmentActivity() {
		ArrayList<DTCInfo> data = ((DTCListFragment)mFragmentMap.get(0)).getData();
		if( data.isEmpty() ) {
			return;
		}
		Bundle args = new Bundle();
		args.putParcelableArrayList(AppointmentActivity.EXTRA_DTC_DATA, data);
		ContextUtil.startActivity(this, AppointmentActivity.class, args);
	}
	
	@Override
	AbsPullToRefreshLazyLoadFragment getViewPagerFragmentItem(int pos) {
		boolean flag = (pos == 0 );
		return DTCListFragment.newInstance(
				flag ? CurrentDTCListFragment.class : HistoryDTCListFragment.class, flag);
	}

}
