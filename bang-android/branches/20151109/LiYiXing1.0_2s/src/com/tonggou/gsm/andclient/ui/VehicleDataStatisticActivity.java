package com.tonggou.gsm.andclient.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.QueryVehicleDataStatisticRequest;
import com.tonggou.gsm.andclient.net.response.QueryVehicleDataStatisticResponse;
import com.tonggou.gsm.andclient.ui.fragment.AbsDataLoadedCallbackFragment;
import com.tonggou.gsm.andclient.ui.fragment.VehicleDataChartPagerFragment;
import com.tonggou.gsm.andclient.ui.fragment.VehicleDataStatisticFragment;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;

public class VehicleDataStatisticActivity extends AbsTabBackableActivity {

	private ArrayList<AbsDataLoadedCallbackFragment> mDataLoadedCallbacks;
	private View mNoDataView;

	@Override
	int getLayoutRes() {
		return R.layout.activity_vehicle_data_statistic;
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
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);

		mDataLoadedCallbacks = new ArrayList<AbsDataLoadedCallbackFragment>();
		mDataLoadedCallbacks.add( new VehicleDataStatisticFragment() );
		mDataLoadedCallbacks.add( new VehicleDataChartPagerFragment() );

		mNoDataView = findViewById(R.id.no_data_view);
		mViewPager.setVisibility(View.INVISIBLE);
		requestData();
	}

	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_vehicle_data_statistic);
	}

	@Override
	public void onTabSelected(int index) {
		mViewPager.setCurrentItem(index, true);
	}

	@Override
	PagerAdapter createPagerAdapter() {
		return new TabAdapter();
	}

	private void requestData() {
		showLoadingDialog();
		QueryVehicleDataStatisticRequest request = new QueryVehicleDataStatisticRequest();
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<QueryVehicleDataStatisticResponse>() {

			@Override
			public void onParseSuccess(QueryVehicleDataStatisticResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				if( result.getYearStat() == null || result.getMonthStats() == null || result.getMonthStats().isEmpty() ) {
					return;
				}
				mNoDataView.setVisibility(View.GONE);
				mViewPager.setVisibility(View.VISIBLE);
				for( AbsDataLoadedCallbackFragment fragment : mDataLoadedCallbacks ) {
					fragment.onDataLoadedCallback(result);
				}
			}

			@Override
			public Class<QueryVehicleDataStatisticResponse> getTypeClass() {
				return QueryVehicleDataStatisticResponse.class;
			}

			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
			}
		});
	}

	class TabAdapter extends FragmentStatePagerAdapter {

		public TabAdapter() {
			super(getSupportFragmentManager());
		}

		@Override
		public Fragment getItem(int pos) {
			AbsDataLoadedCallbackFragment fragment = mDataLoadedCallbacks.get(pos);

			return fragment;
		}

		@Override
		public int getCount() {
			return FRAGMENT_COUNT;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}
}