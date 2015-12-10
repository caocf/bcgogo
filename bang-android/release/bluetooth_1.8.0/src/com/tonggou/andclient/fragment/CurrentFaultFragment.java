package com.tonggou.andclient.fragment;

import java.util.List;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.tonggou.andclient.BaseActivity;
import com.tonggou.andclient.R;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.util.PreferenceUtil;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.util.RefreshViewLoadMoreProxy.LOAD_MODE;
import com.tonggou.andclient.vo.CarCondition;
import com.tonggou.andclient.vo.VehicleInfo;
import com.tonggou.andclient.vo.type.FaultCodeStatusType;

/**
 * µ±«∞π ’œ
 * @author lwz
 *
 */
public class CurrentFaultFragment extends AbsFaultListFragment {
	
	public static final String TAG = "CurrentFaultFragment"; 

	public static Fragment newInstance() {
		return new CurrentFaultFragment();
	}
	
	@Override
	protected void afterViews() {
		super.afterViews();
		if( mPullToRefreshActionSlideListView.getMode() != Mode.DISABLED ) {
			mPullToRefreshActionSlideListView.setMode(Mode.PULL_FROM_START);
		}
	}

	@Override
	protected void updateData(int page, LOAD_MODE mode )  {
		mPostLoadHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				VehicleInfo vehicleInfo = TongGouApplication.getInstance().getDefaultVehicle();
				if( vehicleInfo == null ) {
					return;
				}
				String vehicleId = vehicleInfo.getVehicleId();
				String userNo = PreferenceUtil.getString(TongGouApplication.getInstance(), BaseActivity.SETTING_INFOS, BaseActivity.NAME);
				List<CarCondition> data = SaveDB.getSaveDB(TongGouApplication.getInstance()).getAllCarConditons(userNo, vehicleId);
				mAdapter.update( data );
				mLoadMoreProxy.loadDataActionComplete();
			}
		}, 500);
	}

	@Override
	int getHandledButtonImageResource() {
		return R.drawable.handled;
	}

	@Override
	void onHandledAction(CarCondition itemData) {
		modifyFaultCodeStatusRequest(null, 
				itemData.getFaultCode(), 
				FaultCodeStatusType.UNTREATED,
				FaultCodeStatusType.FIXED,
				itemData.getVehicleId());
	}

	@Override
	void onHandleSuccess(final String faultCodeId, final String faultCode, final String vehicleId) {
		String userNo = PreferenceUtil.getString(getActivity(), BaseActivity.SETTING_INFOS, BaseActivity.NAME);
		SaveDB.getSaveDB(getActivity()).deleteOneAlarm(userNo, faultCode, vehicleId);
		onRefresh(1, 1);
	}

	@Override
	public void onRefresh(int page, int dataCount) {
//		Intent intent = new Intent();
//        intent.setAction(TongGouService.TONGGOU_ACTION_READ_CURRENT_DTC_CONDITION);
//        getActivity().sendBroadcast(intent);
		updateData(1, null);
	}

	@Override
	public void onLoadMore(int page, int dataCount) {
		
	}
	
	
}
