package com.tonggou.gsm.andclient.ui;

import android.os.Bundle;
import android.text.TextUtils;

import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.ServiceDetail;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.QueryServiceHistoryDetailRequest;
import com.tonggou.gsm.andclient.net.response.ServiceHistoryDetailResponse;
import com.tonggou.gsm.andclient.ui.view.IndicatorTextView;
import com.tonggou.gsm.andclient.ui.view.ScrollViewRefreshProxy;
import com.tonggou.gsm.andclient.ui.view.ScrollViewRefreshProxy.OnRefreshActionListener;
import com.tonggou.gsm.andclient.ui.view.ServiceHistoryItemContainer;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.StringUtil;

/**
 * 账单详情页面
 * @author lwz
 *
 */
public class PaymentDetailActivity extends BackableTitleBarActivity implements OnRefreshActionListener {
	
	public static final String EXTRA_SERVICE_HISTORY_ID = "extra_service_history_id";
	
	PullToRefreshScrollView mPtrScrollView;
	ServiceHistoryItemContainer mPaymentDetailItemContaienr;
	ScrollViewRefreshProxy mScrollViewRefreshProxy;
	String mServiceHistoryId = null;
	
	IndicatorTextView mReceiptNoIndicatorText;
	IndicatorTextView mServiceTypeIndicatorText;
	IndicatorTextView mPaidMoneyIndicatorText;
	IndicatorTextView mVehicleNoIndicatorText;
	IndicatorTextView mVehicleBrandIndicatorText;
	IndicatorTextView mTimestampIndicatorText;
	IndicatorTextView mContactIndicatorText;
	IndicatorTextView mPhoneNoIndicatorText;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_paid_detail);
		
		mPtrScrollView = (PullToRefreshScrollView) findViewById(R.id.ptr_view);
		mScrollViewRefreshProxy = new ScrollViewRefreshProxy(mPtrScrollView);
		mScrollViewRefreshProxy.setOnRefreshActionListener(this);
		mPaymentDetailItemContaienr = (ServiceHistoryItemContainer) findViewById(R.id.payment_detail_container);
		
		mReceiptNoIndicatorText = (IndicatorTextView) findViewById(R.id.receipt_no_indicator_text);
		mServiceTypeIndicatorText = (IndicatorTextView) findViewById(R.id.service_type_indicator_text);
		mPaidMoneyIndicatorText = (IndicatorTextView) findViewById(R.id.paid_money_indicator_text);
		mVehicleNoIndicatorText = (IndicatorTextView) findViewById(R.id.vehicle_no_indicator_text);
		mVehicleBrandIndicatorText = (IndicatorTextView) findViewById(R.id.vehicle_brand_indicator_text);
		mTimestampIndicatorText = (IndicatorTextView) findViewById(R.id.service_timestamp_indicator_text);
		mContactIndicatorText = (IndicatorTextView) findViewById(R.id.contact_indicator_text);
		mPhoneNoIndicatorText = (IndicatorTextView) findViewById(R.id.phone_no_indicator_text);
		
		
		if( !restoreExtras(getIntent()) ) {
			restoreExtras(savedInstance);
		}
		
		mScrollViewRefreshProxy.refreshing();
	}
	
	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra != null && extra.containsKey(EXTRA_SERVICE_HISTORY_ID) ) {
			mServiceHistoryId = extra.getString(EXTRA_SERVICE_HISTORY_ID);
			return true;
		}
		return false;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(EXTRA_SERVICE_HISTORY_ID, mServiceHistoryId);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_paid_detail);
	}

	@Override
	public void onRefresh() {
		doHistoryDetailRequest();
	}
	
	private void doHistoryDetailRequest() {
		QueryServiceHistoryDetailRequest request = new QueryServiceHistoryDetailRequest();
		request.setApiParams(mServiceHistoryId);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<ServiceHistoryDetailResponse>() {

			@Override
			public void onParseSuccess(ServiceHistoryDetailResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				updateUI(result.getServiceDetail());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				mScrollViewRefreshProxy.loadRefreshActionComplete();
			}
			
			@Override
			public Class<ServiceHistoryDetailResponse> getTypeClass() {
				return ServiceHistoryDetailResponse.class;
			}
		});
	}
	
	private void updateUI(ServiceDetail detail) {
		mReceiptNoIndicatorText.setTextValue(detail.getReceiptNo());
		mServiceTypeIndicatorText.setTextValue(detail.getServiceType());
		mPaidMoneyIndicatorText.setTextValue(getString(
				R.string.format_paid_total_money, detail.getSettleAccounts().getTotalAmount()));
		mVehicleNoIndicatorText.setTextValue(detail.getVehicleNo());
		mVehicleBrandIndicatorText.setTextValue(detail.getVehicleBrandModelStr());
		mTimestampIndicatorText.setTextValue( 
				StringUtil.formatDateYYYYMMdd( detail.getOrderTime() ) );
		mContactIndicatorText.setTextValue( detail.getCustomerName() );
		final String phoneNo = detail.getVehicleMobile();
		mPhoneNoIndicatorText.setTextValue( TextUtils.isEmpty(phoneNo) ? "--" : phoneNo );
		mPaymentDetailItemContaienr.setItemsValue(detail.getOrderItems(), null);
	}
}
