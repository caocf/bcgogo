package com.tonggou.gsm.andclient.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.ui.view.IndicatorEditText;
import com.tonggou.gsm.andclient.ui.view.IndicatorTextView;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.StringUtil;
import com.tonggou.gsm.andclient.util.TelephonyUtil;

/**
 * 店铺介绍
 * @author lwz
 *
 */
public class MyDeviceActivity extends BackableTitleBarActivity {

	IndicatorTextView mDeviceNoIndicatorText;
	IndicatorEditText mPrimaryPhoneNoIndicatorEdit;
	
	// NOTE : 需求原因去掉设置救援号码
//	IndicatorEditText mSosPhoneNo1IndicatorEdit;
//	IndicatorEditText mSosPhoneNo2IndicatorEdit;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		setContentView(R.layout.activity_my_device);
		
		mDeviceNoIndicatorText = (IndicatorTextView) findViewById(R.id.device_no_indicator_view);
		mPrimaryPhoneNoIndicatorEdit = (IndicatorEditText) findViewById(R.id.primary_phone_no_indicator_view);
//		mSosPhoneNo1IndicatorEdit = (IndicatorEditText) findViewById(R.id.sos_phone_no_1_indicator_view);
//		mSosPhoneNo2IndicatorEdit = (IndicatorEditText) findViewById(R.id.sos_phone_no_2_indicator_view);
		
		mDeviceNoIndicatorText.setTextValue(UserBaseInfo.getUserInfo().getImei());
		mPrimaryPhoneNoIndicatorEdit.setEditTextValue(UserBaseInfo.getUserInfo().getMobile());
		EditText editText = mPrimaryPhoneNoIndicatorEdit.getEditText();
		editText.setSelection(editText.getText().toString().length());
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_my_device);
	}
	
	public void onSendPrimaryPhoneNoBtnClick(View view) {
		if( !TelephonyUtil.isSIMCardEnable(this) ) {
			App.showShortToast(getString(R.string.info_sim_card_absent));
			return;
		}
		if( invalidateText(mPrimaryPhoneNoIndicatorEdit.getEditText(), R.string.info_primary_phone_no_donot_empty) ) {
			return;
		}
		if(invalidatePhoneNo(mPrimaryPhoneNoIndicatorEdit) ) {
			return;
		}
//		SendSMSService.send(this, UserBaseInfo.getUserInfo().getGsmObdImeiMoblie(), 
//				getPrimaryOrder(mPrimaryPhoneNoIndicatorEdit.getEditTextValue()));
		sendSms(UserBaseInfo.getUserInfo().getGsmObdImeiMoblie(), 
				getPrimaryOrder(mPrimaryPhoneNoIndicatorEdit.getEditTextValue()));
	}
	
//	public void onSendSosPhoneNoBtnClick(View view) {
//		if( !TelephonyUtil.isSIMCardEnable(this) ) {
//			App.showShortToast(getString(R.string.info_sim_card_absent));
//			return;
//		}
//		// 有一个手机号不为空就可以发送
//		if( !invalidateText(mSosPhoneNo1IndicatorEdit.getEditText())
//				|| !invalidateText(mSosPhoneNo2IndicatorEdit.getEditText()) ) {
//			
//			if( !TextUtils.isEmpty(mSosPhoneNo1IndicatorEdit.getEditTextValue())
//					&& invalidatePhoneNo(mSosPhoneNo1IndicatorEdit)) {
//				return;
//			}
//			
//			if( !TextUtils.isEmpty(mSosPhoneNo2IndicatorEdit.getEditTextValue())
//					&& invalidatePhoneNo(mSosPhoneNo2IndicatorEdit)) {
//				return;
//			}
//			
////			SendSMSService.send(this, UserBaseInfo.getUserInfo().getGsmObdImeiMoblie(), 
////					getSosOrder(mSosPhoneNo1IndicatorEdit.getEditTextValue(), mSosPhoneNo2IndicatorEdit.getEditTextValue()));
//			sendSms( UserBaseInfo.getUserInfo().getGsmObdImeiMoblie(), 
//					getSosOrder(mSosPhoneNo1IndicatorEdit.getEditTextValue(), mSosPhoneNo2IndicatorEdit.getEditTextValue()));
//		} else {
//			App.showShortToast(getString(R.string.info_sos_phone_no_donot_empty));
//		}
//	}
	
	private String getPrimaryOrder(String primaryPhoneNo) {
		return Constants.DEVICE_PHONE_NO_SET.PRIMARY_PHONE_NO_SET_ORDER_PREFIX 
				+ Constants.DEVICE_PHONE_NO_SET.ORDER_SEPARATOR + primaryPhoneNo;
	}
	
//	private String getSosOrder(String sosPhoneNo1, String sosPhoneNo2) {
//		String sosStr = TextUtils.isEmpty(sosPhoneNo1) ? "" : Constants.DEVICE_PHONE_NO_SET.ORDER_SEPARATOR + sosPhoneNo1;
//		sosStr += TextUtils.isEmpty(sosPhoneNo2) ? "" : Constants.DEVICE_PHONE_NO_SET.ORDER_SEPARATOR + sosPhoneNo2;
//		return Constants.DEVICE_PHONE_NO_SET.SOS_PHONE_SET_ORDER_PREFIX + sosStr;
//	}
	
	private boolean invalidatePhoneNo(IndicatorEditText editText) {
		if( !StringUtil.validatePhoneNo(editText.getEditText()) ) {
			App.showShortToast(getString(R.string.txt_info_phone_invalidate));
			return true;
		}
		return false;
	}
	
	private void sendSms(String smsTo, String smsBody) {
		Uri smsToUri = Uri.parse("smsto:" + smsTo);
	    Intent mIntent = new Intent( Intent.ACTION_SENDTO, smsToUri );  
	    mIntent.putExtra("sms_body", smsBody);
	    startActivity( mIntent);
	}
}
