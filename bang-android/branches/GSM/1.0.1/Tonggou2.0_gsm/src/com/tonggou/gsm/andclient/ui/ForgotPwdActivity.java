package com.tonggou.gsm.andclient.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.ResetPasswordRequest;
import com.tonggou.gsm.andclient.net.response.BaseResponse;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.StringUtil;

/**
 * 忘记密码页面
 * @author lwz
 *
 */
public class ForgotPwdActivity extends BackableTitleBarActivity {
	
	public static final String EXTRA_PHONE_NO = "extra_phone_no";
	
	EditText mPhoneNoEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_pwd);
		
		mPhoneNoEdit = (EditText) findViewById(R.id.phoneNo_editText);
		if( !restoreExtras(getIntent()) ) {
			restoreExtras(savedInstanceState);
		}
	}
	
	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra != null && extra.containsKey(EXTRA_PHONE_NO) ) {
			String phoneNo = extra.getString(EXTRA_PHONE_NO);
			mPhoneNoEdit.setText( phoneNo );
			mPhoneNoEdit.setSelection(phoneNo.length());
			return true;
		}
		return false;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(EXTRA_PHONE_NO, getText(mPhoneNoEdit));
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_find_pwd);
	}
	
	public void onSendNewPwdBtnClick(View view) {
		if( invalidateText(mPhoneNoEdit, R.string.txt_info_please_input_phone_no) ) {
			return;
		}
		if( !StringUtil.isPhoneNo(getText(mPhoneNoEdit)) ) {
			App.showShortToast(getString(R.string.txt_info_phone_invalidate));
			return;
		}
		doFindPwd( getText(mPhoneNoEdit) );
	}
	
	private void doFindPwd(String phoneNo) {
		showLoadingDialog();
		ResetPasswordRequest request = new ResetPasswordRequest();
		request.setApiParams(phoneNo);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				App.showShortToast(result.getMessage());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
				finish();
			}
			
			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
		});
		
	}
}
