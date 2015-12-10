package com.tonggou.andclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.UserDateParser;
import com.tonggou.andclient.parse.UserDateSaveParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.vo.UserInfo;

public class UserDataActivity extends BaseActivity{
	private static final int  NETWORK_FAILD=-1;
	private static final int  LOGIN_SUCCEED=0x001;
	private static final int  LOGIN_FAILD=0x002;
	private static final int  SAVE_SUCCEED=0x003;
	private static final int  SAVE_FAILD=0x004;
	
	private String userNo;
	private Handler handler;
	private EditText name,phonenumber;
	private View ok,back,changepassword;
	private TextView nickname;
	private UserInfo user;
	private ProgressBar progress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userNo=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME, null);

		setContentView(R.layout.userdata);
		name=(EditText) findViewById(R.id.set_name_tv);
		nickname=(TextView) findViewById(R.id.set_nickname_tv);
		phonenumber=(EditText) findViewById(R.id.set_phone_tv);

		progress=(ProgressBar) findViewById(R.id.shopdetilmappro);
		ok=findViewById(R.id.right_button);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				progress.setVisibility(View.INVISIBLE);
				new Thread(){
					public void run(){
						setInfo(nickname.getText().toString(),name.getText().toString(),phonenumber.getText().toString());
					}
				}.start();
			}
		});
		back=findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UserDataActivity.this.finish();
			}
		});
		changepassword=findViewById(R.id.set_password);
		changepassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserDataActivity.this,UserDataChangePasswordActivity.class);
				startActivity(intent);
			}
		});
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				switch(msg.what){
				case NETWORK_FAILD:
					progress.setVisibility(View.GONE);
					Toast.makeText(UserDataActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case LOGIN_SUCCEED: 
					progress.setVisibility(View.GONE);
					showMessge();
					break;
				case LOGIN_FAILD: 	
					progress.setVisibility(View.GONE);						
					Toast.makeText(UserDataActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

					break;
				case SAVE_SUCCEED: 
					progress.setVisibility(View.GONE);
					Toast.makeText(UserDataActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();
					break;
				case SAVE_FAILD: 
					progress.setVisibility(View.GONE);							
					Toast.makeText(UserDataActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

					break;

				}
			}
		};
		new Thread(){
			public void run(){
				getInfo();
			}
		}.start();
	}
	private void getInfo(){
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/user/information/userNo/"+userNo;
		UserDateParser userDateParser = new UserDateParser();		
		NetworkState ns = Network.getNetwork(UserDataActivity.this).httpGetUpdateString(url,userDateParser);	

		if(ns.isNetworkSuccess()){
			if(userDateParser.isSuccessfull()){
				user=userDateParser.getUserDateResponse().getUserInfo();
				if(user!=null){
					sendMessage(LOGIN_SUCCEED, null);
				}else{
					sendMessage(LOGIN_FAILD,"没有列表数据");
				}

			}else{
				//解析出错
				sendMessage(LOGIN_FAILD, userDateParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
		}
	}
	private void setInfo(String userNo,String name,String  mobile){
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/user/information";
		UserDateSaveParser userDateSaveParser = new UserDateSaveParser();		
		String valuePairs = "{\"userNo\":\""+userNo+"\", " +" \"mobile\":\""+mobile+"\", " +
				" \"name\":\""+name+"\"}";
		NetworkState ns = Network.getNetwork(UserDataActivity.this).httpPutUpdateString(url,valuePairs.getBytes(),userDateSaveParser);
		if(ns.isNetworkSuccess()){
			if(userDateSaveParser.isSuccessfull()){
				//正确的处理逻辑 
				String mes = userDateSaveParser.getUserDateSaveResponse().getMessage();
				sendMessage(SAVE_SUCCEED, mes);
			}else{
				//提示用户错误
				String errorAlert = userDateSaveParser.getErrorMessage();
				sendMessage(SAVE_FAILD, errorAlert);
			}
		}else{
			//网络错误
			sendMessage(SAVE_FAILD, ns.getErrorMessage());
		}
	}
	private void showMessge(){ 
		nickname.setText(user.getUserNo());
		name.setText(user.getName());
		phonenumber.setText(user.getMobile());

		getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
		.putString(BaseActivity.PHONENAME, user.getName())
		.putString(BaseActivity.PHONE, user.getMobile()).commit();
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
