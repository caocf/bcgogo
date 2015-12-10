package com.tonggou.gsm.andclient.ui.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.VehicleDataStatistic;
import com.tonggou.gsm.andclient.net.response.QueryVehicleDataStatisticResponse;
import com.tonggou.gsm.andclient.ui.view.AbsViewHolderAdapter;
import com.tonggou.gsm.andclient.ui.view.IndicatorTextView;

public class VehicleDataStatisticFragment extends AbsDataLoadedCallbackFragment {

	ListView listView;
	StatisticListAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_vehicle_data_statistic, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		listView = findViewById(R.id.listview);
		mAdapter = new StatisticListAdapter(view.getContext(), R.layout.item_list_vehicle_data_statistic);
		listView.setAdapter(mAdapter);
	}
	
	@Override
	public void onDataLoadedCallback(QueryVehicleDataStatisticResponse response) {
		ArrayList<VehicleDataStatistic> data = new ArrayList<VehicleDataStatistic>();
		data.add(response.getYearStat());
		
		ArrayList<VehicleDataStatistic> monthData = response.getMonthStats();
		ArrayList<VehicleDataStatistic> reverseMonthData = new ArrayList<VehicleDataStatistic>();
		for( VehicleDataStatistic item : monthData ) {
			reverseMonthData.add(0, item);
		}
		data.addAll(reverseMonthData);
		mAdapter.update(data);
	}
	
	class StatisticListAdapter extends AbsViewHolderAdapter<VehicleDataStatistic> {

		public StatisticListAdapter(Context context, int layoutRes) {
			super(context, layoutRes);
		}
		
		@Override
		protected void bindData(int pos, VehicleDataStatistic itemData) {
			TextView timestampText = getViewFromHolder(R.id.timestamp_text);
			if( pos == 0 ) {
				timestampText.setBackgroundResource(R.drawable.item_list_title_primary);
				timestampText.setTextColor(Color.WHITE);
				timestampText.setText(getString(R.string.format_YYYY_MM_to_now, itemData.getStatYear(), itemData.getStatMonth()));
			} else {
				timestampText.setBackgroundResource(R.drawable.item_list_title);
				timestampText.setTextColor(getResources().getColor(R.color.gray));
				timestampText.setText(getString(R.string.format_YYYY_MM, itemData.getStatYear(), itemData.getStatMonth()));
			}
			
			IndicatorTextView mileageIndicatorText = getViewFromHolder(R.id.mileage_indicator_text);
			IndicatorTextView oilwearIndicatorText = getViewFromHolder(R.id.oilwear_indicator_text);
			IndicatorTextView avgOilwearIndicatorText = getViewFromHolder(R.id.avg_oilwear_indicator_text);
			IndicatorTextView moneyIndicatorText = getViewFromHolder(R.id.money_indicator_text);
			
			mileageIndicatorText.setTextValue(String.format("%.1f", itemData.getDistance()));
			oilwearIndicatorText.setTextValue(String.format("%.2f", itemData.getOilCost()));
			avgOilwearIndicatorText.setTextValue(String.format("%.2f", itemData.getOilWear()));
			moneyIndicatorText.setTextValue(String.format("%.2f", itemData.getOilMoney()));
		}
		
	}

}
