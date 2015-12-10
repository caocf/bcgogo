package com.tonggou.yf.andclient.ui.fragment;

import org.androidannotations.annotations.EFragment;

import android.content.Context;

import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.bean.ServiceJobDTO;
import com.tonggou.yf.andclient.bean.type.TodoType;
import com.tonggou.yf.andclient.net.TonggouResponseParseHandler;
import com.tonggou.yf.andclient.net.request.QueryMaintainListRequest;
import com.tonggou.yf.andclient.net.response.QueryMaintainListResponse;
import com.tonggou.yf.andclient.widget.AbsTodoAdapter;

@EFragment(R.layout.fragment_refresh_list)
public class MaintainTodoFragment extends AbsTodoFragment<ServiceJobDTO> {

	@Override
	AbsTodoAdapter<ServiceJobDTO> createAdapter() {
		return new DTCAdapter(getActivity(), R.layout.item_list_maintain);
	}

	class DTCAdapter extends AbsTodoAdapter<ServiceJobDTO> {

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
		public void bindData(int pos, ServiceJobDTO itemData) {
			super.bindData(pos, itemData);
			float maintainLeft = itemData.getMaintainLeftMileage();
			String indicatorMileage = maintainLeft > 0 
					? getString(R.string.format_maintain_mileage_beyond, Math.abs(maintainLeft))
					: getString(R.string.format_maintain_mileage_left, Math.abs(maintainLeft));
			bindText(R.id.maintain_mileage_indicator_text, indicatorMileage);
			
			bindText(R.id.vehicle_owner_indicator_text, itemData.getCustomerName());
			bindText(R.id.vehicle_no_indicator_text, itemData.getVehicleNo());
		}

		@Override
		public void onHandledBtnClick(final int pos) {
			doRemindHandle(TodoType.MAINTAIN, getData().get(pos).getId(), new Runnable() {
				
				@Override
				public void run() {
					animateDismiss(pos);
				}
			});
		}

		@Override
		public void onHandledLeftBtnClick(int pos) {
			doLoadSmsTemplet(TodoType.MAINTAIN, getData().get(pos).getId());
		}
		
		@Override
		public void onHandledRightBtnClick(int pos) {
			doPhoneCall(getData().get(pos).getMobile());
		}
		
	}

	@Override
	void requestData(int pageNo, final boolean isRefresh) {
		QueryMaintainListRequest request = new QueryMaintainListRequest();
		request.setApiParams(pageNo);
		request.doRequest(getActivity(), new TonggouResponseParseHandler<QueryMaintainListResponse>() {

			@Override
			public void onParseSuccess(QueryMaintainListResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				updatePager(result.getPager());
				if( isRefresh ) { 
					mAdapter.update(result.getServiceJobDTOList());
				} else {
					mAdapter.append(result.getServiceJobDTOList());
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				mRefreshProxy.loadDataActionComplete(isRefresh);
			}
			
			@Override
			public Class<QueryMaintainListResponse> getTypeClass() {
				return QueryMaintainListResponse.class;
			}
		});
	}

}
