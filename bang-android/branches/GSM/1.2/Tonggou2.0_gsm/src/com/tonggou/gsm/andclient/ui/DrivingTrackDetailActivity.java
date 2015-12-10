package com.tonggou.gsm.andclient.ui;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.DriveLogDTO;
import com.tonggou.gsm.andclient.bean.GeoPointParcel;
import com.tonggou.gsm.andclient.net.response.DriveLogResponse;
import com.tonggou.gsm.andclient.ui.fragment.DrivingTrackDetailMapFragment;
import com.tonggou.gsm.andclient.ui.view.Chart;
import com.tonggou.gsm.andclient.ui.view.ChartItem.ChartItemValue;
import com.tonggou.gsm.andclient.ui.view.DrivingTrackDetailStatisticsContainer;
import com.tonggou.gsm.andclient.ui.view.DrivingTrackStatisticsItem;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.ContextUtil;
import com.tonggou.gsm.andclient.util.StringUtil;

/**
 * 行车轨迹详情界面
 * @author lwz
 *
 */
public class DrivingTrackDetailActivity extends BackableTitleBarActivity {
	
	public static final String EXTRA_DRIVE_LOG_RESPONSE = "extra_drive_log_response";
	
	private DrivingTrackDetailStatisticsContainer mTrackDetailStatisticsContainer;
	private Chart mChart;
	private DrivingTrackStatisticsItem mCurrentOilWear;
	private TextView mOilLevelStatus;
	private TextView mAverageOilWear;
	private DriveLogResponse mFackDriveLogResponse;
	private DriveLogDTO mLog;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.activity_driving_track_detail);
	
		if( !restoreExtras(getIntent()) ) {
			restoreExtras(savedInstance);
		}
		mLog = mFackDriveLogResponse.getDriveLogDTOs().get(0);
		mTrackDetailStatisticsContainer = (DrivingTrackDetailStatisticsContainer) findViewById(R.id.track_detail_statistics_container);
		mChart = (Chart) findViewById(R.id.track_detail_statistics_chart);
		mCurrentOilWear = (DrivingTrackStatisticsItem) findViewById(R.id.current_avg_oil_wear);
		mAverageOilWear = (TextView) findViewById(R.id.avg_oil_wear);
		mOilLevelStatus = (TextView) findViewById(R.id.oil_level_status_text);
		
		updateStatisticsData();
		initTrackMap();
	}
	
	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra != null && extra.containsKey(EXTRA_DRIVE_LOG_RESPONSE) ) {
			mFackDriveLogResponse = (DriveLogResponse) extra.getSerializable(EXTRA_DRIVE_LOG_RESPONSE);
			return true;
		}
		return false;
	}

	private void updateStatisticsData() {
		initDetailStatisticsValue( new String[]{
						String.valueOf(mLog.getDistance()), 
						StringUtil.formatMinute(mLog.getTravelTime()), 
						formatFloat1(mLog.getTotalOilMoney()),
						formatFloat1(mLog.getOilCost())});
		float averageOilWear = mFackDriveLogResponse.getTotalOilWear();
		float currentOilWear = mLog.getOilWear();
		mChart.setUnit(getString(R.string.unit_L_100KM));
		mChart.setValues(new ChartItemValue[]{
				new ChartItemValue(getString(R.string.oilwear_worst), formatFloat1(mFackDriveLogResponse.getWorstOilWear()), Color.parseColor("#535190"), mFackDriveLogResponse.getWorstOilWear()),
				new ChartItemValue(getString(R.string.oilwear_average), formatFloat1(averageOilWear), Color.parseColor("#3AA523"), averageOilWear),
				new ChartItemValue(getString(R.string.oilwear_current), formatFloat1(currentOilWear), Color.parseColor("#F25C4B"), currentOilWear),
				new ChartItemValue(getString(R.string.oilwear_best), formatFloat1(mFackDriveLogResponse.getBestOilWear()), Color.parseColor("#2876D8"), mFackDriveLogResponse.getBestOilWear())
		});
		mCurrentOilWear.setItemValue(
				R.drawable.ic_driving_track_averageoilwear, formatFloat1(currentOilWear), R.color.red, getString(R.string.unit_L_100KM));
		mOilLevelStatus.setText(compareResult( averageOilWear, currentOilWear ));
		mAverageOilWear.setText(getString(R.string.format_unit_L_100Km, formatFloat1(averageOilWear)));
	}
	
	private String compareResult(float average, float current) {
		int resultRes = R.string.compare_equals;
		if( average > current ) {
			resultRes = R.string.compare_less_then;
		} else if( average < current ) {
			resultRes = R.string.compare_great_then;
		}
		return getString(resultRes);
	}

	private void initTrackMap() {
		getSupportFragmentManager().beginTransaction()
		.add(R.id.track_detail_map_container, DrivingTrackDetailMapFragment.newFragment(mLog.getId()), DrivingTrackDetailMapFragment.TAG)
		.commit();
	}
	
	public void onMapClickCallback(GeoPointParcel[] points) {
		if( points == null || points.length == 0 ) {
			App.showShortToast(getString(R.string.info_driving_track_points_empty));
			return;
		}
		Bundle args = new Bundle();
		args.putParcelableArray(DrivingTrackPlaybackActivity.EXTRA_TRACK_POINTS, points);
		ContextUtil.startActivity(this, DrivingTrackPlaybackActivity.class, args);
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_driving_track_detail);
	}
	
	private void initDetailStatisticsValue(CharSequence[] values) {
		final String[] leftIndicatorValues = getResources().getStringArray(R.array.driving_track_detail_statistics_titles);
		final String[] units = getResources().getStringArray(R.array.driving_track_detail_statistics_units);
		TypedArray colorTypeArray = getResources().obtainTypedArray(R.array.driving_track_detail_statistics_colors);
		TypedArray iconTypeArray = getResources().obtainTypedArray(R.array.driving_track_detail_statistics_icons);
		final int SIZE = 4;
		int[] colors = new int[SIZE];
		Drawable[] icons = new Drawable[SIZE];
		for( int i=0; i<SIZE; i++ ) {
			colors[i] = colorTypeArray.getColor(i, Color.BLACK);
			icons[i] = iconTypeArray.getDrawable(i);
		}
		colorTypeArray.recycle();
		iconTypeArray.recycle();
		mTrackDetailStatisticsContainer.setItemsValue(leftIndicatorValues, icons, values, colors, units);
	}
}
