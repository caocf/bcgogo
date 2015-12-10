package com.tonggou.yf.andclient.ui.fragment;

import android.content.Context;
import android.text.TextUtils;

import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.bean.FaultInfoToShopDTO;
import com.tonggou.yf.andclient.bean.type.TodoType;
import com.tonggou.yf.andclient.net.TonggouResponseParseHandler;
import com.tonggou.yf.andclient.net.request.QueryDtcListRequest;
import com.tonggou.yf.andclient.net.response.QueryDtcListResponse;
import com.tonggou.yf.andclient.widget.AbsTodoAdapter;

public class DTCTodoFragment extends AbsTodoFragment<FaultInfoToShopDTO> {

	@Override
	AbsTodoAdapter<FaultInfoToShopDTO> createAdapter() {
		return new DTCAdapter(getActivity(), R.layout.item_list_dtc);
	}

	class DTCAdapter extends AbsTodoAdapter<FaultInfoToShopDTO> {

		public DTCAdapter(Context context, int layoutRes) {
			super(context, layoutRes);
		}

		@Override
		public int getHandledBtnId() {
			return R.id.handled_btn;
		}

		@Override
		public int getHandledLeftBtnId() {
			return R.id.handled_btn_send_sms;
		}

		@Override
		public int getHandledRightBtnId() {
			return R.id.handled_btn_phone_call;
		}
		
		@Override
		public void bindData(int pos, FaultInfoToShopDTO itemData) {
			super.bindData(pos, itemData);
			
			bindText(R.id.dtc_type_indicator_text, itemData.getFaultAlertTypeValue());
			bindText(R.id.timestamp_text, itemData.getFaultCodeReportTimeStr());
			bindText(R.id.vehicle_no_indicator_text, itemData.getVehicleNo());
			bindText(R.id.vehicle_owner_indicator_text, itemData.getCustomerName());
			bindText(R.id.dtc_desc_indicator_text,  
					getDtcDescriptionFormatStr(itemData.getFaultCode(), itemData.getFaultCodeDescription()));
		}
		
		private String getDtcDescriptionFormatStr(String faultCode, String desctiption) {
			if( !TextUtils.isEmpty(faultCode) ) {
				return faultCode +  (TextUtils.isEmpty(desctiption) ? "" : " " + desctiption);
			} else {
				return desctiption;
			}
		}
		@Override
		public void onHandledBtnClick(final int pos) {
			doRemindHandle(TodoType.DTC, getData().get(pos).getId(), new Runnable() {
				
				@Override
				public void run() {
					animateDismiss(pos);
				}
			});
		}

		@Override
		public void onHandledLeftBtnClick(int pos) {
			doLoadSmsTemplet(TodoType.DTC, getData().get(pos).getId());
		}
		
		@Override
		public void onHandledRightBtnClick(int pos) {
			doPhoneCall(getData().get(pos).getMobile());
		}
		
	}

	@Override
	void requestData(final int pageNo, final boolean isRefresh) {
		QueryDtcListRequest request = new QueryDtcListRequest();
		request.setApiParams(pageNo);
		request.doRequest(getActivity(), new TonggouResponseParseHandler<QueryDtcListResponse>() {

			@Override
			public void onParseSuccess(QueryDtcListResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				updatePager(result.getPager());
				if( isRefresh ) { 
					mAdapter.update(result.getFaultInfoToShopDTOList());
				} else {
					mAdapter.append(result.getFaultInfoToShopDTOList());
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				mRefreshProxy.loadDataActionComplete(isRefresh);
			}

			@Override
			public Class<QueryDtcListResponse> getTypeClass() {
				return QueryDtcListResponse.class;
			}
		});
	}
	
}
