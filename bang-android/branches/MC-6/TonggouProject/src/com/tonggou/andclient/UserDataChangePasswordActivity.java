package com.tonggou.andclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.UserDataChangePasswordParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SomeUtil;

public class UserDataChangePasswordActivity extends BaseActivity{

	private static final int  NETWORK_FAILD=-1;
	private static final int  LOGIN_SUCCEED=0x001;
	private static final int  LOGIN_FAILD=0x002;

	private Handler handler;
	private String userNo; 
	private EditText oldpass,newpass,pass;
	private View ok,back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userNo=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME, null);
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
						if(newpasswordStr.length()>=6){
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
									new Thread(){
										public void run(){
											setInfo(oldpasswordStr,passwordStr);
										}
									}.start();
								}else{
									Toast.makeText(UserDataChangePasswordActivity.this,getString(R.string.login_password_same), Toast.LENGTH_SHORT).show();
								}
							}else{
								Toast.makeText(UserDataChangePasswordActivity.this,getString(R.string.login_password_again_null), Toast.LENGTH_SHORT).show();								
							}
						}else{
							Toast.makeText(UserDataChangePasswordActivity.this,"密码不能少于6位数", Toast.LENGTH_SHORT).show();								
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
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				switch(msg.what){
				case NETWORK_FAILD: 
					Toast.makeText(UserDataChangePasswordActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case LOGIN_SUCCEED: 
					Toast.makeText(UserDataChangePasswordActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					UserDataChangePasswordActivity.this.finish();
					break;
				case LOGIN_FAILD: 							
					Toast.makeText(UserDataChangePasswordActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
	}
	private void setInfo(String oldPassword,String newPassword){

		UserDataChangePasswordParser userDataChangePasswordParser = new UserDataChangePasswordParser();
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/user/password";	
		String valuePairs = "{\"userNo\":\""+userNo+"\", " +" \"oldPassword\":\""+oldPassword+"\", " +
				" \"newPassword\":\""+newPassword+"\"}";
		NetworkState ns = Network.getNetwork(UserDataChangePasswordActivity.this).httpPutUpdateString(url,valuePairs.getBytes(),userDataChangePasswordParser);
		if(ns.isNetworkSuccess()){
			if(userDataChangePasswordParser.isSuccessfull()){
				//正确的处理逻辑 
				String mes = userDataChangePasswordParser.getUserDataChangePasswordResponse().getMessage();
				sendMessage(LOGIN_SUCCEED, mes);
			}else{
				//提示用户错误
				String errorAlert = userDataChangePasswordParser.getErrorMessage();
				sendMessage(LOGIN_FAILD, errorAlert);
			}
		}else{
			//网络错误
			sendMessage(LOGIN_FAILD, ns.getErrorMessage());
		}

	}
	protected void sendMessage(int what, String content) {
		if (what < 0) {
			what = BaseActivity.SEND_MESSAGE;
		}
		Message msg = Message.obtain(handler, what, content);
		if(msg!=null){
			msg.sendToTarget();
		}
	}
}
