package com.tonggou.gsm.andclient.ui;

import java.util.ArrayList;

import android.R.color;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.bean.AppVehicleDTO;
import com.tonggou.gsm.andclient.bean.DTCInfo;
import com.tonggou.gsm.andclient.bean.ServiceCategory;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.AppointmentRequest;
import com.tonggou.gsm.andclient.net.response.BaseResponse;
import com.tonggou.gsm.andclient.ui.view.AbsViewHolderAdapter;
import com.tonggou.gsm.andclient.ui.view.IndicatorEditText;
import com.tonggou.gsm.andclient.ui.view.IndicatorSpinner;
import com.tonggou.gsm.andclient.ui.view.IndicatorTextView;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;

/**
 * 预约服务页面
 * @author lwz
 *
 */
public class AppointmentActivity extends DateTimePickerBackableActivity {
	
	public static final String EXTRA_DTC_DATA = "extra_dtc_data";
	
	private IndicatorTextView mVehicleNoIndicatorText;
	private IndicatorEditText mContactIndicatorEdit;
	private IndicatorTextView mShopIndicatorText;
	private IndicatorTextView mTimeIndicatorText;
	private IndicatorEditText mContactInfoIndicatorEdit;
	private IndicatorEditText mExtraIndicatorEdit;
	private IndicatorSpinner mServieCategoryIndicatorSpinner;
	private ArrayList<DTCInfo> mDTCData;
	private String mServiceTypeId = "10000010001000001";	// 维修
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appointment);
		
		mDTCData = new ArrayList<DTCInfo>();
		
		mServieCategoryIndicatorSpinner = (IndicatorSpinner) findViewById(R.id.service_type_indicator_spinner);
		mVehicleNoIndicatorText = (IndicatorTextView) findViewById(R.id.vehicle_no_indicator_edit);
		mContactIndicatorEdit = (IndicatorEditText) findViewById(R.id.contact_indicator_edit);
		mShopIndicatorText = (IndicatorTextView) findViewById(R.id.shop_indicator_edit);
		mTimeIndicatorText = (IndicatorTextView) findViewById(R.id.appointment_time_indicator_edit);
		mContactInfoIndicatorEdit = (IndicatorEditText) findViewById(R.id.contact_info_indicator_edit);
		mExtraIndicatorEdit = (IndicatorEditText) findViewById(R.id.extra_indicator_edit);
		
		mShopIndicatorText.setTextValue(UserBaseInfo.getShopInfo().getName());
		mShopIndicatorText.setClickableIndicator(
				View.inflate(this, R.layout.widget_shop_detail_btn, null), new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toShopDetailActitiy();
			}
		});
		mVehicleNoIndicatorText.setTextValue(UserBaseInfo.getVehicleInfo().getVehicleNo());
		mContactInfoIndicatorEdit.setEditTextValue(UserBaseInfo.getUserInfo().getMobile());
		mContactIndicatorEdit.getEditText().requestFocus();
		mTimeIndicatorText.setImageIndicator(R.drawable.ic_calendar, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDatePickerDialog(mTimeIndicatorText);
			}
		});
		
		if( !restoreExtras(getIntent()) ) {
			restoreExtras(savedInstanceState);
		}
		initServieCategorySpinner();
	}
	
	private void initServieCategorySpinner() {
		Spinner spinner = mServieCategoryIndicatorSpinner.getSpinner();
		final ArrayList<ServiceCategory> data = ServiceCategory.createServiceType(
				getResources().getStringArray(R.array.shop_service_category), 
				getResources().getStringArray(R.array.shop_service_category_id));
		spinner.setAdapter(new ServiceCategoryAdapter(this, data, R.layout.item_shop_service_type));
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mServiceTypeId = data.get(position).id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				mServiceTypeId = data.get(0).id;
			}
		});
	}
	
	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra != null && extra.containsKey(EXTRA_DTC_DATA) ) {
			ArrayList<DTCInfo> data = extra.getParcelableArrayList(EXTRA_DTC_DATA);
			mDTCData.addAll(data);
			return true;
		}
		return false;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(EXTRA_DTC_DATA, mDTCData);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_appointment);
		titleBar.setRightButton(getString(R.string.btn_confirm), color.transparent);
		titleBar.setOnRightButtonClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( invalidateForm() ) {
					return;
				}
				doAppointment();
			}
		});
	}
	
	public void toShopDetailActitiy() {
		Intent intent = new Intent(this, ShopDetailActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 验证提交表单数据正确性
	 * @return true 数据校验失败，false 数据校验通过
	 */
	private boolean invalidateForm() {
		return invalidateText(mTimeIndicatorText.getTextView(), R.string.appointment_date_donot_empty)
				|| invalidateText(mContactIndicatorEdit.getEditText(), R.string.appointment_contact_donot_empty)
				|| invalidateText(mContactInfoIndicatorEdit.getEditText(), R.string.appointment_contact_info_donot_empty);
	}
	
	/**
	 * 发送预约请求
	 */
	private void doAppointment() {
		showLoadingDialog();
		AppointmentRequest request = new AppointmentRequest();
		AppVehicleDTO vehicleInfo = UserBaseInfo.getVehicleInfo();
		request.setRequestParams(UserBaseInfo.getShopInfo().getId(),
				mServiceTypeId, getText(mContactInfoIndicatorEdit.getEditText()), 
				getText(mContactIndicatorEdit.getEditText()), 
				UserBaseInfo.getUserInfo().getUserNo(),
				formatDateTimeYYYYHHddHHmm(getText(mTimeIndicatorText.getTextView())).getMillis(),
				vehicleInfo.getVehicleNo(), null,
				vehicleInfo.getVehicleBrand(), null,
				vehicleInfo.getVehicleModel(), null,
				getText(mExtraIndicatorEdit.getEditText()),
				mDTCData);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				App.showShortToast(result.getMessage());
				finish();
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
			}
			
			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
		});
		
	}
	
	class ServiceCategoryAdapter extends AbsViewHolderAdapter<ServiceCategory> {

		ServiceCategoryAdapter(Context context, ArrayList<ServiceCategory> data,
				int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void bindData(int pos, ServiceCategory itemData) {
			TextView nameText = getViewFromHolder(android.R.id.text1);
			nameText.setText(itemData.name);
		}
		
	}
}
