package com.tonggou.gsm.andclient.ui.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.DriveLogDTO;
import com.tonggou.gsm.andclient.bean.LatLngParcel;
import com.tonggou.gsm.andclient.db.dao.TonggouDriveLogDao;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.QueryDrivingTrackRequest;
import com.tonggou.gsm.andclient.net.response.DrivingTrackResponse;
import com.tonggou.gsm.andclient.ui.DrivingTrackDetailActivity;
import com.tonggou.gsm.andclient.util.BMapUtil;

public class DrivingTrackDetailMapFragment extends AbsMapFragment  implements OnMapLoadedCallback {

	public static final String TAG = "DrivingTrackPlaybackFragment";
	private static final String EXTRA_DRIVE_LOG_ID = "extra_drive_log_id";
	private String mLogId;
	private LatLngParcel[] mPoints;
	private BaiduMap mBaiduMap;
	private OverlayOptions mOption;

	public static Fragment newFragment(String logId) {
		Fragment fragment = new DrivingTrackDetailMapFragment();
		Bundle args = new Bundle();
		args.putString(EXTRA_DRIVE_LOG_ID, logId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	int getLayoutRes() {
		return R.layout.fragment_support_map;
	}

	@Override
	int getMapFragmentId() {
		return R.id.support_map_fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if( !restoreExtras(getArguments()) ) {
			restoreExtras(savedInstanceState);
		}

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	void onMapCreated(final MapView mapView, Bundle savedInstance) {
		mBaiduMap = mapView.getMap();

		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {
				if( isDetached() || mActivity.isFinishing() ) {
					return;
				}

				if( mPoints == null ) {
					App.showShortToast(getString(R.string.info_driving_track_points_loading));
					return;
				}
				((DrivingTrackDetailActivity)mActivity).onMapClickCallback(mPoints);
			}

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra != null && extra.containsKey(EXTRA_DRIVE_LOG_ID) ) {
			mLogId = extra.getString(EXTRA_DRIVE_LOG_ID);
			return true;
		}
		return false;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(EXTRA_DRIVE_LOG_ID, mLogId);
		super.onSaveInstanceState(outState);
	}

	/**
	 * 从数据库中查询 轨迹
	 * @return true 数据库中已有 轨迹，false 没有轨迹记录
	 */
	private boolean queryDriveTrackFromDB() {
		DriveLogDTO log = TonggouDriveLogDao.queryLogById(mActivity, mLogId);
		if( log == null ) {
			return false;
		}
		String trackStr = log.getPlaceNotes();
		if( TextUtils.isEmpty(trackStr) ) {
			return false;
		}
		drawDrivingTrack(trackStr);
		return true;
	}

	private void queryDriveTrackRequest() {
		QueryDrivingTrackRequest request = new QueryDrivingTrackRequest();
		request.setApiParams(mLogId);
		request.doRequest(mActivity, new AsyncJsonBaseResponseParseHandler<DrivingTrackResponse>() {

			@Override
			public void onParseSuccess(DrivingTrackResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				drawDrivingTrack(result.getDetailDriveLogs());
			}

			@Override
			public Class<DrivingTrackResponse> getTypeClass() {
				return DrivingTrackResponse.class;
			}
		});
	}

	private void drawDrivingTrack(ArrayList<DriveLogDTO> logs) {
		if( logs == null || logs.isEmpty() ) {
			App.showShortToast(getString(R.string.info_driving_track_points_empty));
			return;
		}
		DriveLogDTO log = logs.get(0);
		drawDrivingTrack( log.getPlaceNotes() );
		TonggouDriveLogDao.updatePlaceNotes(mActivity, log.getId(), log.getPlaceNotes());
	}

	private void drawDrivingTrack(String trackStr) {
		drawDrivingTrack(parseData(trackStr));
	}

	private void drawDrivingTrack(LatLngParcel[] points) {
		mPoints = points;

		List<LatLngParcel> llpAL = Arrays.asList(mPoints);
		final LatLng[] span = new LatLng[2];
		final LatLngParcel centerPoint = new LatLngParcel(0, 0);
		BMapUtil.calculateSpanAndCenter(llpAL, span, centerPoint);

		mBaiduMap.animateMapStatus(BMapUtil.newMapStatusWithLatLngArray(span));

		mOption = BMapUtil.drawLine(Arrays.asList(BMapUtil.convertLatLngParcelArrayToLatLngArray(mPoints)));
		mBaiduMap.addOverlay(mOption);
	}

	private LatLngParcel[] parseData(String placeNotes) {
		String[] data = placeNotes.split("\\|");
		LatLngParcel[] points = new LatLngParcel[data.length];
		for( int i=0; i<data.length; i++ ) {
			points[i] = new LatLngParcel( convertWgs84ToBaidu((data[i])) );
		}

		return points;
	}

	private LatLng convertWgs84ToBaidu(String str) {
		String[] item = str.split(",");
		return BMapUtil.convertWgs84ToBaidu(Double.valueOf(item[0]), Double.valueOf(item[1]));
	}

    @Override
	public void onDestroyView() {
		super.onDestroyView();
		mPoints = null;
	}

	@Override
	public void onMapLoaded() {
		if( !queryDriveTrackFromDB() ) {
			queryDriveTrackRequest();
		}
	}
}