package com.tonggou.gsm.andclient.ui.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.VehicleDataStatistic;
import com.tonggou.gsm.andclient.net.response.QueryVehicleDataStatisticResponse;

public class VehicleDataChartPagerFragment extends AbsDataLoadedCallbackFragment {
	
	final int CHART_COUNT = 4;
	ViewPager mViewPager;
	ChartPagerAdapter mPagerAadpter;
	RadioGroup mChartRadioGroup;
	SparseIntArray mChartRadioMapping;
	ArrayList<float[]> mChartData;
	String[] mXLabels;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_vehicle_data_chart_pager, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mChartRadioGroup = findViewById(R.id.line_chart_radioGroup);
		mChartRadioGroup.setVisibility(View.INVISIBLE);
		mViewPager = findViewById(R.id.chart_viewpager);
		mViewPager.setOffscreenPageLimit(4);
		createChartRadioMapping();
		
	}
	
	private void createChartRadioMapping() {
		mChartRadioMapping = new SparseIntArray(4);
		mChartRadioMapping.put(R.id.mileage_radioBtn, 0);
		mChartRadioMapping.put(R.id.oilwear_radioBtn, 1);
		mChartRadioMapping.put(R.id.avg_oilwear_radioBtn, 2);
		mChartRadioMapping.put(R.id.money_radioBtn, 3);
	}
	
	class ChartPagerAdapter extends FragmentStatePagerAdapter {

		String[] chartUnits;
		
		
		public ChartPagerAdapter() {
			super(getChildFragmentManager());
			chartUnits = getResources().getStringArray(R.array.chart_units);
		}

		@Override
		public Fragment getItem(int pos) {
			return VehicleDataChartFragment.newInstance(mChartData.get(pos), mXLabels, chartUnits[pos]);
		}

		@Override
		public int getCount() {
			return CHART_COUNT;
		}
		
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	@Override
	public void onDataLoadedCallback(QueryVehicleDataStatisticResponse response) {
		conventToChartData(response);
		mChartRadioGroup.setVisibility(View.VISIBLE);
		mPagerAadpter = new ChartPagerAdapter();
		mViewPager.setAdapter(mPagerAadpter);
		mChartRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				mViewPager.setCurrentItem(mChartRadioMapping.get(checkedId, 0), true);
			}
		});
		mViewPager.setCurrentItem(0, true);
	}
	
	private void conventToChartData(QueryVehicleDataStatisticResponse response) {
		mChartData = new ArrayList<float[]>();
		ArrayList<VehicleDataStatistic> data = response.getMonthStats();
		final int SIZE = data.size();
		mXLabels = new String[SIZE];
		float[] mileages = new float[SIZE];
		float[] oilwears = new float[SIZE];
		float[] avgOilwears = new float[SIZE];
		float[] moneys = new float[SIZE];
		for( int i=0; i<SIZE; i++) {
			VehicleDataStatistic item = data.get(i);
			mileages[i] = item.getDistance();
			oilwears[i] = item.getOilCost();
			avgOilwears[i] = item.getOilWear();
			moneys[i] = item.getOilMoney();
			mXLabels[i] = getString(R.string.format_MM, String.valueOf(item.getStatMonth()));
		}
		
		mChartData.add(mileages);
		mChartData.add(oilwears);
		mChartData.add(avgOilwears);
		mChartData.add(moneys);
	}
	
}
