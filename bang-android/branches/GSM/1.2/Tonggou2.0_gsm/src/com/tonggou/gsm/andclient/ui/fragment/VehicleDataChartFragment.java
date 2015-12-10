package com.tonggou.gsm.andclient.ui.fragment;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.ui.view.LineChartView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VehicleDataChartFragment extends BaseFragment {

	private final static String EXTRA_CHART_DATA = "extra_chart_data";
	private final static String EXTRA_CHART_DATA_UNIT = "extra_chart_data_unit";
	private final static String EXTRA_CHART_DATA_X_LABELS = "extra_chart_data_x_labels";
	
	public static Fragment newInstance(float[] data, String[] xLabels, String unit) {
		Fragment fragment = new VehicleDataChartFragment();
		Bundle args = new Bundle();
		args.putFloatArray(EXTRA_CHART_DATA, data);
		args.putStringArray(EXTRA_CHART_DATA_X_LABELS, xLabels);
		args.putString(EXTRA_CHART_DATA_UNIT, unit);
		fragment.setArguments(args);
		return fragment; 
	}
	
	LineChartView mLineChartView;
	float[] mChartData;
	String mUnit;
	String[] mXLabels;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_vehicle_data_chart, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mLineChartView = findViewById(R.id.line_chart);
		TextView unitText = findViewById(R.id.unit_text);
		
		if( !restoreExtras(getArguments()) ) {
			restoreExtras(savedInstanceState);
		}
		
		unitText.setText(getString(R.string.format_unit, mUnit));
		mLineChartView.setData(mChartData);
		mLineChartView.setAxisXLabels(mXLabels);
	}
	
	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra != null && extra.containsKey(EXTRA_CHART_DATA) ) {
			mChartData = extra.getFloatArray(EXTRA_CHART_DATA);
			mUnit = extra.getString(EXTRA_CHART_DATA_UNIT);
			mXLabels = extra.getStringArray(EXTRA_CHART_DATA_X_LABELS);
			return true;
		}
		return false;
	}
	
}
