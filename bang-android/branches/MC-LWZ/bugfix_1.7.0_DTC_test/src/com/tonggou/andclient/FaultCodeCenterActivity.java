package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.fragment.CurrentFaultFragment;
import com.tonggou.andclient.fragment.HistoryFaultFragment;
import com.tonggou.andclient.myview.SimpleTitleBar;
import com.tonggou.andclient.myview.SubtitleTabView;
import com.tonggou.andclient.vo.CarCondition;

/**
 * 故障查询界面
 * @author lwz
 *
 */
public class FaultCodeCenterActivity extends AbsBackableActivity {

	private SubtitleTabView mSubtitleTabView;
	private HashMap<String, ArrayList<CarCondition>> mCacheData;
	
	@Override
	protected int getContentLayout() {
		return R.layout.activity_fault_code_center;
	}

	@Override
	protected void afterTitleBarCreated(SimpleTitleBar titleBar, Bundle savedInstanceState) {
		super.afterTitleBarCreated(titleBar, savedInstanceState);
		titleBar.setTitle(R.string.title_fault_code_center);
	}
	
	private void showTitleBarRightButton() {
		getSimpleTitle().setRightButton(getString(R.string.titlebar_right_btn_txt_appointment), R.drawable.ic_titlebar_btn_bg);
		getSimpleTitle().setOnRightButtonClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FaultCodeCenterActivity.this, StoreQueryActivity.class);
				intent.putExtra("tonggou.shop.categoryname", getString(R.string.shopslist_service));
				startActivity(intent);
			}
		});
	}
	
	private void hideTitleBarRightButton() {
		getSimpleTitle().setOnRightButtonClickListener(null);
	}
	
	@Override
	protected void findViews(Bundle savedInstanceState) {
		super.findViews(savedInstanceState);
		mSubtitleTabView = (SubtitleTabView) findViewById(R.id.subtitle_tab);
		mSubtitleTabView.setTabText(R.string.tab_current_fault, R.string.tab_record_fault);
	}
	
	@Override
	protected void afterViews() {
		mCacheData = new HashMap<String, ArrayList<CarCondition>>(2);
		showTitleBarRightButton();
		setListener();
		// 默认第一次显示当前故障
		replaceFragment(CurrentFaultFragment.newInstance(), CurrentFaultFragment.TAG);
	}

	private void setListener() {
		mSubtitleTabView.setOnFirstTabClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showTitleBarRightButton();
				replaceFragment(CurrentFaultFragment.newInstance(), CurrentFaultFragment.TAG);
			}
		});
		mSubtitleTabView.setOnSecondTabClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideTitleBarRightButton();
				replaceFragment(HistoryFaultFragment.newInstance(), HistoryFaultFragment.TAG);
			}
		});
	}
	
	private void replaceFragment(Fragment fragment, String tag) {
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.fragment_container, fragment, tag)
		.commit();
	}
	
	public ArrayList<CarCondition> getCacheData(String key) {
		return mCacheData.get(key);
	}
	
	public boolean hasCacheData( String key ) {
		return getCacheData(key) != null;
	}
	
	public void cacheData(String key, List<CarCondition> data) {
		mCacheData.put(key, (ArrayList<CarCondition>)data);
	}
	
	@Override
	protected void onDestroy() {
		mCacheData = null;
		super.onDestroy();
	}
	
}
