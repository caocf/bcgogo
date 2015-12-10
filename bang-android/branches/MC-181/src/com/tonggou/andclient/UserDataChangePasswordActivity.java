package com.tonggou.andclient;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.BaseResponse;
import com.tonggou.andclient.network.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.ModifyPasswordRequest;
import com.tonggou.andclient.util.SomeUtil;

public class UserDataChangePasswordActivity extends BaseActivity{

	private EditText oldpass,newpass,pass;
	private View ok,back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userdatachangepassword);
		oldpass=(EditText) findViewById(R.id.set_useroldpassword);
		newpass=(EditText) findViewById(R.id.set_usernewpassword);
		pass=(EditText) findViewById(R.id.set_userpassword);

		ok=findViewById(R.id.changepassword_ok);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String oldpasswordStr=oldpass.getText().toString();
				String newpasswordStr=newpass.getText().toString();
				final String passwordStr=pass.getText().toString();

				if(oldpasswordStr!=null&&!"".equals(oldpasswordStr)){
					if(newpasswordStr!=null&&!"".equals(newpasswordStr)){
						if(newpasswordStr.indexOf(" ")!=-1){
							Toast.makeText(UserDataChangePasswordActivity.this,"密码不能含有空格", Toast.LENGTH_SHORT).show();
							return;
						}
						if(SomeUtil.justIfChineseInput(newpasswordStr)){
							Toast.makeText(UserDataChangePasswordActivity.this,"密码不能含有中文字符", Toast.LENGTH_SHORT).show();
							return;
						}
						
						if(passwordStr!=null&&!"".equals(passwordStr)){
							if(newpasswordStr.equals(passwordStr)){
								doModify(oldpasswordStr,passwordStr);
							}else{
								Toast.makeText(UserDataChangePasswordActivity.this,getString(R.string.login_password_same), Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(UserDataChangePasswordActivity.this,getString(R.string.login_password_again_null), Toast.LENGTH_SHORT).show();								
						}
					}else{
						Toast.makeText(UserDataChangePasswordActivity.this,getString(R.string.login_password_null), Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(UserDataChangePasswordActivity.this,getString(R.string.login_password_null), Toast.LENGTH_SHORT).show();
				}

			}
		});
		back=findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UserDataChangePasswordActivity.this.finish();
			}
		});
	}
	private void doModify(String oldPassword,String newPassword){
		showLoadingDialog("提交中...");
		ModifyPasswordRequest request = new ModifyPasswordRequest();
		String userNo = sharedPreferences.getString(NAME, "");
		request.setRequestParams(userNo, oldPassword, newPassword);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				TongGouApplication.showToast(result.getMessage());
				UserDataChangePasswordActivity.this.finish();
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
