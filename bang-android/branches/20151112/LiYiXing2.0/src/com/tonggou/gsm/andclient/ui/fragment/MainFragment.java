package com.tonggou.gsm.andclient.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.bean.AppVehicleDTO;
import com.tonggou.gsm.andclient.service.PollingVehicleLocationService;
import com.tonggou.gsm.andclient.ui.DTCManagerActivity;
import com.tonggou.gsm.andclient.ui.DriveTrackAcivity;
import com.tonggou.gsm.andclient.ui.MessageManagerActivity;
import com.tonggou.gsm.andclient.ui.ViolationQueryActivity;
import com.tonggou.gsm.andclient.ui.view.PersonCarDistanceStatisticsInfoBoardItem.Value;
import com.tonggou.gsm.andclient.ui.view.PersonCarDistanceStatisticsInfoBoard;
import com.tonggou.gsm.andclient.util.BMapUtil;
import com.tonggou.gsm.andclient.util.ContextUtil;

/**
 * 首页人车Fragment
 * @author peter
 *
 */
public class MainFragment extends BaseFragment implements OnClickListener{

	private FrameLayout mMapView;
	private FrameLayout mTextView;
	private static PersonCarDistanceStatisticsInfoBoard mBoardHeader;
	private MainVehicleAndUserMapFragment mMapFragment;
	public AppVehicleDTO vehicleInfo;
	private Button mBtnStartDrivingRecord;
	private Button mBtnVehicleCheck;
	private Button mBtnIllegalQuery;
	private Button mBtnMessageCenter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main, container, true);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mMapView = findViewById(R.id.home_map_view);
		mTextView = findViewById(R.id.home_text_view);

		initBroadHeader();

		mTextView.addView(mBoardHeader);
		mMapView.addView(getBMapHeader());

		mMapFragment = (MainVehicleAndUserMapFragment) getFragmentManager().findFragmentById(R.id.first_driving_track_map_fragment);

		PollingVehicleLocationService.startPolling(mActivity);

		mBtnStartDrivingRecord = findViewById(R.id.btn_drive_track);
		mBtnVehicleCheck = findViewById(R.id.btn_veilcle_check);
		mBtnIllegalQuery = findViewById(R.id.btn_illegal_query);
		mBtnMessageCenter = findViewById(R.id.btn_message_center);
		mBtnStartDrivingRecord.setOnClickListener(this);
		mBtnVehicleCheck.setOnClickListener(this);
		mBtnIllegalQuery.setOnClickListener(this);
		mBtnMessageCenter.setOnClickListener(this);
	}

	public MainVehicleAndUserMapFragment getTrackMap() {
		return mMapFragment;
	}

	private void initBroadHeader() {
		mBoardHeader = new PersonCarDistanceStatisticsInfoBoard(mActivity);
		mBoardHeader.setBackgroundResource(R.color.holo_blue);
		Value[] values = new Value[PersonCarDistanceStatisticsInfoBoard.ITEM_SIZE];
		String[] titles = getResources().getStringArray(R.array.home_driving_track_statistics_info_board_titles);
		for( int i = 0; i < values.length; i++ ) {
			values[i] = new Value(titles[i], "");
		}
		mBoardHeader.setItemsValue(values);
	}

	/**
	 * 得到百度地图
	 * @return
	 */
	private View getBMapHeader() {
		View view = View.inflate(mActivity, R.layout.widget_main_driving_track_fragment, null);
		return view;
	}

	public static void requestData() {
		updateStatisticsBoard(UserBaseInfo.getVehicleInfo());
	}

	/**
	 * 更新统计栏信息
	 * @param result
	 */
	public static void updateStatisticsBoard(AppVehicleDTO vehicleInfo) {

		if ( vehicleInfo == null) {
			String unknowIndicator = "--";
			mBoardHeader.updateItemsValue(new String[]{
					unknowIndicator, unknowIndicator
			});
		} else {
			String distance = BMapUtil.distanceWithUserAndVehicle;
			String currentMileage = vehicleInfo.getCurrentMileage() + "";
			mBoardHeader.updateItemsValue(new String[] {
				currentMileage, distance
			});
		}
	}

	@Override
	public void onDestroyView() {
		// 停止轮询车辆位置
		PollingVehicleLocationService.stopPolling(mActivity);
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_drive_track:
			ContextUtil.startActivity(mActivity, DriveTrackAcivity.class);
			break;
		case R.id.btn_veilcle_check:
			ContextUtil.startActivity(mActivity,DTCManagerActivity.class);
			break;
		case R.id.btn_illegal_query:
			ContextUtil.startActivity(mActivity, ViolationQueryActivity.class);
			break;
		case R.id.btn_message_center:
			ContextUtil.startActivity(mActivity, MessageManagerActivity.class);
			break;
		case R.id.first_map_thumb_img:
			break;
		}
	}
}