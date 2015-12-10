package com.tonggou.yf.andclient.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.tonggou.lib.net.response.BaseResponse;
import com.tonggou.yf.andclient.App;
import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.bean.AppointOrderDTO;
import com.tonggou.yf.andclient.net.TonggouResponseParseHandler;
import com.tonggou.yf.andclient.net.request.ChangeAppointTimeRequest;
import com.tonggou.yf.andclient.net.request.QueryAppointListRequest;
import com.tonggou.yf.andclient.net.response.QueryAppointListResponse;
import com.tonggou.yf.andclient.ui.ChangeServiceTimeActivity;
import com.tonggou.yf.andclient.util.ContextUtil;
import com.tonggou.yf.andclient.widget.AbsTodoAdapter;

public class AppointTodoFragment extends AbsTodoFragment<AppointOrderDTO> {

	private final int REQUEST_CODE_DATETIME = 0x234;
	
	private int mChangeServiceTimePosition;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		registerActivityResultObserver();
	}
	
	void registerActivityResultObserver() {
		getMainActivity().registerActivityResultObserver(REQUEST_CODE_DATETIME, this);
	}
	
	@Override
	AbsTodoAdapter<AppointOrderDTO> createAdapter() {
		return new DTCAdapter(getActivity(), R.layout.item_list_appointment);
	}

	class DTCAdapter extends AbsTodoAdapter<AppointOrderDTO> {

		public DTCAdapter(Context context, int layoutRes) {
			super(context, layoutRes);
		}

		@Override
		public int getHandledBtnId() {
			return 0;
		}

		@Override
		public int getHandledLeftBtnId() {
			return R.id.handled_btn_accept;
		}

		@Override
		public int getHandledRightBtnId() {
			return R.id.handled_btn_change_time;
		}
		
		@Override
		public void bindData(int pos, AppointOrderDTO itemData) {
			super.bindData(pos, itemData);
			
			bindText(R.id.appointment_time_indicator_text, itemData.getAppointTimeStr());
			bindText(R.id.service_type_indicator_text, itemData.getAppointServiceType());
			bindText(R.id.vehicle_no_indicator_text, itemData.getVehicleNo());
			bindText(R.id.vehicle_owner_indicator_text, itemData.getCustomer());
		}

		@Override
		public void onHandledBtnClick(final int pos) {
		}

		@Override
		public void onHandledLeftBtnClick(final int pos) {
			doAcceptAppoint(getData().get(pos).getId(), new Runnable() {
				
				@Override
				public void run() {
					animateDismiss(pos);
				}
			});
		}
		
		@Override
		public void onHandledRightBtnClick(final int pos) {
			doChangeAppointTime(pos);
		}
		
	}

	@Override
	void requestData(final int pageNo, final boolean isRefresh) {
		QueryAppointListRequest request = new QueryAppointListRequest();
		request.setApiParams(pageNo);
		request.doRequest(getActivity(), new TonggouResponseParseHandler<QueryAppointListResponse>() {

			@Override
			public void onParseSuccess(QueryAppointListResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				updatePager(result.getPager());
				if( isRefresh ) { 
					mAdapter.update(result.getAppointOrderDTOList());
				} else {
					mAdapter.append(result.getAppointOrderDTOList());
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				mRefreshProxy.loadDataActionComplete(isRefresh);
			}

			@Override
			public Class<QueryAppointListResponse> getTypeClass() {
				return QueryAppointListResponse.class;
			}
		});
	}

	public void doChangeAppointTime(final int pos) {
		mChangeServiceTimePosition = pos;
		ContextUtil.startActivityForResult(
				getActivity(), ChangeServiceTimeActivity.class, REQUEST_CODE_DATETIME);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_DATETIME ) {
			long datetime = data.getLongExtra(ChangeServiceTimeActivity.EXTRA_DATETIME, System.currentTimeMillis());
			String idStr = mAdapter.getData().get(mChangeServiceTimePosition).getId();
			changeAppointTime(idStr, datetime, new Runnable() {
				
				@Override
				public void run() {
					animateDismiss(mChangeServiceTimePosition);
				}
			});
			return;
		}
		releaseHandleBtnlock();
	}
	
	public void changeAppointTime(final String idStr, long timeMillis, final Runnable handleSuccessCallback) {
		showLoadingDialog(R.string.info_loading_handle);
		ChangeAppointTimeRequest request = new ChangeAppointTimeRequest();
		request.setRequestParams(idStr, timeMillis);
		request.doRequest(getActivity(), new TonggouResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				App.showLongToast(getString(R.string.info_change_time_success));
				if( handleSuccessCallback != null ) {
					handleSuccessCallback.run();
				}
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
				super.onParseFailure(errorCode, errorMsg);
				App.showShortToast(getString(R.string.info_change_time_failure));
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				onHandleFininsh();
			}
			
			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
		});
	}
}
