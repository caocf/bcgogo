package com.tonggou.gsm.andclient.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.ModifyPasswordRequest;
import com.tonggou.gsm.andclient.net.response.BaseResponse;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;

/**
 * 修改 密码
 * @author lwz
 *
 */
public class ModifyPwdActivity extends BackableTitleBarActivity {
	
	EditText mOldPwdEdit;
	EditText mNewPwdEdit;
	EditText mConfirmPwdEdit;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_modify_password);
		
		mOldPwdEdit = (EditText) findViewById(R.id.old_pwd_editText);
		mNewPwdEdit = (EditText) findViewById(R.id.new_pwd_editText);
		mConfirmPwdEdit = (EditText) findViewById(R.id.confirm_pwd_editText);
		
		setHintTextColor(mOldPwdEdit, getResources().getColor(R.color.holo_blue));
		setHintTextColor(mNewPwdEdit, getResources().getColor(R.color.holo_blue));
		setHintTextColor(mConfirmPwdEdit, getResources().getColor(R.color.holo_blue));
	}
	
	private void setHintTextColor( TextView textView, int color) {
		textView.setHintTextColor(color);
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_modify_pwd);
	}
	
	public void onSubmitBtnClickListener(View view) {
		if( invalidateText(mOldPwdEdit, R.string.info_old_pwd_donot_empty) 
				|| invalidateText(mNewPwdEdit, R.string.info_new_pwd_donot_empty)
				|| invalidateText(mConfirmPwdEdit, R.string.info_confirm_pwd_donot_empty) ) {
			return;
		}
		if( !getText(mNewPwdEdit).equals( getText(mConfirmPwdEdit)) ) {
			App.showShortToast( getString(R.string.info_new_confirm_pwd_donot_match) );
			return;
		}
		
		doModifyPwdRequest(getText(mOldPwdEdit), getText(mNewPwdEdit));
	}
	
	private void doModifyPwdRequest(String oldPwd, String newPwd) {
		showLoadingDialog();
		ModifyPasswordRequest request = new ModifyPasswordRequest();
		request.setRequestParams(UserBaseInfo.getUserInfo().getUserNo(), oldPwd, newPwd);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				App.showShortToast( result.getMessage() );
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
	
	
}
