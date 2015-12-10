package com.tonggou.gsm.andclient.ui.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.bean.Pager;
import com.tonggou.gsm.andclient.bean.ServiceHistory;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.QueryServiecHistoryRequest;
import com.tonggou.gsm.andclient.net.response.ServiceHistoryResponse;
import com.tonggou.gsm.andclient.ui.view.AbsEmptyViewAdapter;
import com.tonggou.gsm.andclient.ui.view.IndicatorTextView;
import com.tonggou.gsm.andclient.util.StringUtil;

public abstract class AbsQueryServiceListFragment extends AbsPullToRefreshLazyLoadFragment {

	ServiceHistoryAdapter mAdapter;
	Pager mPager;
	final String EMPTY_MONEY_INDICATOR = "--"; 
	IndicatorTextView mTotalMoneyIndicatorText;
	ViewGroup mTotalMoneyIndicatorContaienr; 
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mPager = new Pager();
		mPager.setCurrentPage(1);
		super.onViewCreated(view, savedInstanceState);
		
		mTotalMoneyIndicatorContaienr = findViewById(R.id.total_money_container);
		mTotalMoneyIndicatorText = findViewById(R.id.total_money_indicator_text);
		mTotalMoneyIndicatorText.getTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		mTotalMoneyIndicatorText.setTextValue(EMPTY_MONEY_INDICATOR);

	}
	
	@Override
	int getLayoutRes() {
		return R.layout.fragment_paid_list;
	}
	
	@Override
	int getRefreshViewId() {
		return R.id.ptr_view;
	}

	@Override
	AbsEmptyViewAdapter<?> createAdapter() {
		return mAdapter = new ServiceHistoryAdapter(mActivity, R.layout.item_list_paid );
	}
	
	@Override
	public void onRefresh(int page) {
		mPager.setCurrentPage(1);
		doQueryServiceHistory(1, true);
	}

	@Override
	public void onLoadMore(int page) {
		doQueryServiceHistory(mPager.getCurrentPage() + 1, false);
	}
	
	public ArrayList<ServiceHistory> getData() {
		return (ArrayList<ServiceHistory>) mAdapter.getData();
	}
	
	public void doQueryServiceHistory(final int pageNo, final boolean isRefresh) {
		QueryServiecHistoryRequest request = new QueryServiecHistoryRequest();
		request.setApiParams(isQueryFinishedService(), UserBaseInfo.getUserInfo().getUserNo(), pageNo);
		request.doRequest(mActivity, new AsyncJsonBaseResponseParseHandler<ServiceHistoryResponse>() {

			@Override
			public void onParseSuccess(ServiceHistoryResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				ArrayList<ServiceHistory> data = isQueryFinishedService() ? 
						result.getFinishedServiceList() : result.getUnFinishedServiceList();
				if( isQueryFinishedService() ) {
					onFinishedServiceTotalMoney( result.getFinishedServiceTotal() );
				}
				if( isRefresh ) {
					mAdapter.update(data);
				} else {
					if( !data.isEmpty() ) {
						mAdapter.append(data);
					} else {
						App.showShortToast(getString(R.string.info_no_more_data));
					}
				}
				mPager.setCurrentPage(pageNo);
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				mLoadMoreProxy.loadDataActionComplete(isRefresh);
			}
			
			@Override
			public Class<ServiceHistoryResponse> getTypeClass() {
				return ServiceHistoryResponse.class;
			}
		});
	}
	
	abstract boolean isQueryFinishedService();

	protected void onFinishedServiceTotalMoney(float totalMoney) {
		
	} 
	
	class ServiceHistoryAdapter extends AbsEmptyViewAdapter<ServiceHistory> {

		public ServiceHistoryAdapter(Context context, int layoutRes) {
			super(context, layoutRes);
		}

		@Override
		protected void bindData(int pos, final ServiceHistory itemData) {
			View itemClickableIndicator = getViewFromHolder(R.id.item_clickable_indicator);			
			IndicatorTextView serviceType = getViewFromHolder(R.id.service_type_indicator_text);
			IndicatorTextView serviceSecondIndicatorText = getViewFromHolder(R.id.service_second_indicator_text);
			TextView timestamp = getViewFromHolder(R.id.timestamp_text);
			
			timestamp.setText( StringUtil.formatDateTimeYYYYMMddHHmm(itemData.getOrderTime()) );
			serviceType.setTextValue(itemData.getOrderType());
			if(  isQueryFinishedService() ) {
				itemClickableIndicator.setVisibility(View.VISIBLE);
				serviceSecondIndicatorText.getTextView().setTextColor(Color.RED);
				serviceSecondIndicatorText.getLeftIndicator().setText(R.string.item_list_paid_money);
				serviceSecondIndicatorText.setTextValue(
						getString(R.string.format_paid_total_money, itemData.getOrderTotal()));
			} else {
				itemClickableIndicator.setVisibility(View.GONE);
				serviceSecondIndicatorText.getTextView().setTextColor(getResources().getColor(R.color.holo_blue));
				serviceSecondIndicatorText.getLeftIndicator().setText(R.string.item_list_pay_type);
				serviceSecondIndicatorText.setTextValue(itemData.getStatus());
			}
		}
	}
}
