package com.tonggou.gsm.andclient.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapPoi;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.bean.DriveLogDTO;
import com.tonggou.gsm.andclient.db.dao.TonggouDriveLogDao;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.QueryDriveLogRequest;
import com.tonggou.gsm.andclient.net.response.DriveLogResponse;
import com.tonggou.gsm.andclient.service.PollingVehicleLocationService;
import com.tonggou.gsm.andclient.ui.DrivingTrackDetailActivity;
import com.tonggou.gsm.andclient.ui.view.AbsEmptyViewAdapter;
import com.tonggou.gsm.andclient.ui.view.DrivingTrackDateSwitcher;
import com.tonggou.gsm.andclient.ui.view.DrivingTrackDateSwitcher.OnSwitchDateRangeListener;
import com.tonggou.gsm.andclient.ui.view.DrivingTrackStatisticsContainer;
import com.tonggou.gsm.andclient.ui.view.DrivingTrackStatisticsInfoBoard;
import com.tonggou.gsm.andclient.ui.view.DrivingTrackStatisticsInfoBoardItem.Value;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy.OnLoadDataActionListener;
import com.tonggou.gsm.andclient.util.ContextUtil;
import com.tonggou.gsm.andclient.util.PreferenceUtil;
import com.tonggou.gsm.andclient.util.StringUtil;

/**
 * 行车轨迹 Fragment
 * @author lwz
 *
 */
public class DrivingTrackFragment extends BaseFragment 
		implements OnItemClickListener, OnSwitchDateRangeListener, OnLoadDataActionListener {

	private PullToRefreshListView mDrivingTrackList;
	private RefreshViewLoadMoreProxy mLoadMoreProxy;
	private DriveLogAdapter mAdapter;
	private DrivingTrackStatisticsInfoBoard mBoardHeader;
	private DrivingTrackDateSwitcher mDateSwitcher;
	private DrivingTrackMapFragment mMapFragment;
	private ImageView mMapThumbImg;
	private DriveLogResponse mFakeDriveLogResponse;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_driving_track, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mDrivingTrackList = findViewById(R.id.driving_track_list);
		initBroadHeader();
		mDrivingTrackList.getRefreshableView().setDividerHeight(0);
		mDrivingTrackList.getRefreshableView().setSelector(new ColorDrawable(Color.TRANSPARENT));
		mDrivingTrackList.getRefreshableView().addHeaderView(mBoardHeader);
		mDrivingTrackList.getRefreshableView().addHeaderView(getBMapHeader());
		
		mAdapter = new DriveLogAdapter(mActivity, R.layout.item_list_driving_track);
		mDrivingTrackList.setAdapter(mAdapter);
		mDrivingTrackList.getRefreshableView().setOnItemClickListener(
				new AbsEmptyViewAdapter.OnItemClickListenerWrapper(this));
		mLoadMoreProxy = new RefreshViewLoadMoreProxy(mDrivingTrackList, Mode.PULL_FROM_START);
		mLoadMoreProxy.setOnLoadDataActionListener(this);
		mDateSwitcher = findViewById(R.id.driving_track_date_switcher);
		mDateSwitcher.setOnSwitchDateRangeListener(this);
		mLoadMoreProxy.refreshing();
		
		mMapFragment = (DrivingTrackMapFragment) getFragmentManager().findFragmentById(R.id.driving_track_map_fragment);
		mMapFragment.setMKMapViewListener(new MKMapViewListener() {
			
			@Override public void onMapMoveFinish() {}
			
			@Override 
			public void onMapLoadFinish() {
				// 地图加载好了就开始轮询车辆位置
				PollingVehicleLocationService.startPolling(mActivity);
				// 轮询车的位置成功返回后，在 DrivingTrackMapFragment 中进行相应的操作
			}
			
			@Override public void onMapAnimationFinish() {}
			
			@Override 
			public void onGetCurrentMap(Bitmap thumb) {
				mMapThumbImg.setVisibility(View.VISIBLE);
				mMapThumbImg.setImageBitmap(thumb);
			}
			
			@Override public void onClickMapPoi(MapPoi arg0) {}
		});
		
	}
	
	public DrivingTrackMapFragment getTrackMap() {
		return mMapFragment;
	}
	
	private void initBroadHeader() {
		mBoardHeader = new DrivingTrackStatisticsInfoBoard(mActivity);
		mBoardHeader.setBackgroundResource(R.color.holo_blue);
		Value[] values = new Value[DrivingTrackStatisticsInfoBoard.ITEM_SIZE];
		String[] titles = getResources().getStringArray(R.array.driving_track_statistics_info_board_titles);
		String[] untis = getResources().getStringArray(R.array.driving_track_statistics_info_board_units);
		for( int i=0; i<values.length; i++ ) {
			values[i] = new Value(titles[i], "", untis[i]);
		}
		mBoardHeader.setItemsValue(values);
	}
	
	/**
	 * 得到百度地图
	 * @return
	 */
	private View getBMapHeader() {
		View view = View.inflate(mActivity, R.layout.widget_driving_track_fragment, null);
		mMapThumbImg = (ImageView)view.findViewById(R.id.map_thumb_img);
		return view;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// 去掉 header 的位置
		final int headerViewCount = mDrivingTrackList.getRefreshableView().getHeaderViewsCount(); 
		if( position < headerViewCount ) return;
		final int pos = position - headerViewCount;
		
		// 跳转到详情界面
		Bundle args = new Bundle(1);
		mFakeDriveLogResponse.addSingleDriveLog(mAdapter.getItem(pos));
		args.putSerializable(DrivingTrackDetailActivity.EXTRA_DRIVE_LOG_RESPONSE, mFakeDriveLogResponse);
		ContextUtil.startActivity(mActivity, DrivingTrackDetailActivity.class, args);
	}
	
	@Override
	public void onSwitchDateRange(long minDate, long maxDate) {
		mDrivingTrackList.setRefreshing(true);
		queryFromDB(minDate, maxDate);
		doQueryDriveLogRequest(minDate, maxDate);
	}

	@Override
	public void onRefresh(int page) {
		mDateSwitcher.performSwitchDateRangeListener();
	}

	@Override
	public void onLoadMore(int page) {
		
	}
	
	private void queryFromDB(long startTime, long endTime) {
		// 从数据库中查询记录
		ArrayList<DriveLogDTO> logs = TonggouDriveLogDao.queryLogsByTimestamp(
				mActivity, UserBaseInfo.getUserInfo().getUserNo(), startTime, endTime);
		mAdapter.update(logs);
		mFakeDriveLogResponse = restoreStatisticsValue(startTime);
		mFakeDriveLogResponse.setDriveLogDTOs(logs);
		updateStatisticsBoard(mFakeDriveLogResponse);
	}
	
	public void doQueryDriveLogRequest(final long startTime, long endTime) {
		QueryDriveLogRequest request = new QueryDriveLogRequest();
		request.setApiParams(startTime, endTime);
		request.doRequest(mActivity, new AsyncJsonBaseResponseParseHandler<DriveLogResponse>() {

			@Override
			public void onParseSuccess(DriveLogResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				mFakeDriveLogResponse = result.copyWithoutLogs();
				storeStatisticsValue(startTime, mFakeDriveLogResponse);
				updateStatisticsBoard(result);
				mAdapter.update(result.getDriveLogDTOs());
				TonggouDriveLogDao.insertLogs(mActivity, result.getDriveLogDTOs());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				mLoadMoreProxy.loadDataActionComplete(false);
			}
			
			@Override
			public Class<DriveLogResponse> getTypeClass() {
				return DriveLogResponse.class;
			}
		});
	}
	
	/**
	 * 将统计的值存储在 preferences 中
	 * @param startTime	一周的起始时间。以一周的起始时间为 key (一年最多为 56 条，所以可以存储在 preference 中，暂不考虑数据库存储)
	 * @param response
	 */
	private void storeStatisticsValue(long startTime, final DriveLogResponse response) {
		String statisticsValues = new Gson().toJson(response.copyWithoutLogs()).toString();
		PreferenceUtil.putString(mActivity, Constants.PREF.PREF_NAME_OTHER_INFO, 
				createPersonalPrefKeyByStartTime(startTime), statisticsValues);
	}
	
	/**
	 * 将统计的值从 preferences 中取出
	 * @param startTime	一周的起始时间。以一周的起始时间为 key (一年最多为 56 条，所以可以存储在 preference 中，暂不考虑数据库存储)
	 */
	private DriveLogResponse restoreStatisticsValue(long startTime) {
		String statisticsValues = PreferenceUtil.getString(mActivity, 
				Constants.PREF.PREF_NAME_OTHER_INFO, createPersonalPrefKeyByStartTime(startTime));
		if( TextUtils.isEmpty(statisticsValues) ) {
			return new DriveLogResponse();
		}
		return new Gson().fromJson(statisticsValues, DriveLogResponse.class);
	}
	
	private String createPersonalPrefKeyByStartTime(long startTime) {
		return Constants.PREF.PREF_KEY_STATISTICS_VALUE_JSON_STR + "_" + UserBaseInfo.getUserInfo().getUserNo() + "_" + startTime;
	}
	
	/**
	 * 更新统计栏信息
	 * @param result
	 */
	public void updateStatisticsBoard(DriveLogResponse result) {
		List<DriveLogDTO> driveLogs = result.getDriveLogDTOs();
		if( driveLogs.isEmpty() ) {
			String unknowIndicator = "--";
			mBoardHeader.updateItemsValue(new String[]{
					unknowIndicator, unknowIndicator, unknowIndicator, unknowIndicator
				});
		} else {
			String distance = String.valueOf(result.getSubtotalDistance());
			String driveDuring = StringUtil.formatHour(result.getSubtotalTravelTime());
			String totalOilWare = formatFloat1(result.getSubtotalOilCost());
			String oilWare = formatFloat1(result.getSubtotalOilWear());
			mBoardHeader.updateItemsValue(new String[]{
					distance, driveDuring, totalOilWare, oilWare	
				});
		}
	}
	
	class DriveLogAdapter extends AbsEmptyViewAdapter<DriveLogDTO> {

		private final String[] units;
		private final int[] valuesColor = new int[]{
				R.color.holo_blue, R.color.holo_blue, R.color.orange, R.color.orange};
		private final int[] drawablesRes = new int[]{
				R.drawable.ic_driving_track_distance, R.drawable.ic_driving_track_traveltime, 
				R.drawable.ic_driving_track_averageoilwear, R.drawable.ic_driving_track_oilwear};
		
		public DriveLogAdapter(Context context, int layoutRes) {
			super(context, layoutRes);
			units = getResources().getStringArray(R.array.driving_track_item_units);
		}

		@Override
		protected void bindData(int pos, DriveLogDTO itemData) {
			DrivingTrackStatisticsContainer container = getViewFromHolder(R.id.driving_track_statistics_info);
			TextView startPlaceText = getViewFromHolder(R.id.track_start_address);
			TextView endPlaceText = getViewFromHolder(R.id.track_end_address);
			TextView startTimeText = getViewFromHolder(R.id.track_start_time);
			TextView startDateText = getViewFromHolder(R.id.track_start_date);
			TextView endTimeText = getViewFromHolder(R.id.track_end_time);
			TextView endDateText = getViewFromHolder(R.id.track_end_date);
			TextView startCityText = getViewFromHolder(R.id.track_start_city);
			TextView endCityText = getViewFromHolder(R.id.track_end_city);
			
			container.setItemsValue( drawablesRes,
					new String[]{formatFloat1(itemData.getDistance()), 
						StringUtil.formatMinute(itemData.getTravelTime()), 
						formatFloat1(itemData.getOilWear()),
						formatFloat1(itemData.getOilCost())},
					valuesColor, units);
			
			String[] startAddress = splitePlace(itemData.getStartPlace()); 
			startPlaceText.setText(startAddress[0]);
			startCityText.setText(startAddress[1]);
			
			String[] endAddress = splitePlace(itemData.getEndPlace()); 
			endPlaceText.setText(endAddress[0]);
			endCityText.setText(endAddress[1]);
			
			String startDateTime[] = getFrormatDateTime(itemData.getStartTime()).split(" ");
			startTimeText.setText(startDateTime[1]);
			startDateText.setText(startDateTime[0]);
			startDateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
			
			String endDateTime[] = getFrormatDateTime(itemData.getEndTime()).split(" ");
			endTimeText.setText(endDateTime[1]);
			endDateText.setText(endDateTime[0]);
			endDateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
		}
		
		final String SPLIT_FLAG = ",";
		String[] splitePlace(String place) {
			String[] address = new String[]{"", ""};
			if( !TextUtils.isEmpty(place) ) {
				String[] temp = place.split(SPLIT_FLAG, 2);
				for( int i=0; i<temp.length; i++ ) {
					address[i] = temp[i];
				}
			}
			return address;
		}
		
		String dateTimeFormat = "MM月dd日 HH:mm";
		protected String getFrormatDateTime(long millis) {
			return new DateTime(millis).toString(dateTimeFormat);
		}
	}
	
	@Override
	public void onDestroyView() {
		// 停止轮询车辆位置
		PollingVehicleLocationService.stopPolling(mActivity);
		super.onDestroyView();
	}
}
