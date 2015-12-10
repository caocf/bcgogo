package com.tonggou.andclient;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.network.DefaultUpdateCheck;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.LoginParser;
import com.tonggou.andclient.parse.SearchPasswordParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.vo.Version;

public class LoginActivity extends BaseActivity {

	private static final int  NETWORK_FAILD=-1;
	private static final int  LOGIN_SUCCEED=0x001;
	private static final int  LOGIN_FAILD=0x002;
	private static final int  SEARCHPASSWORD_FAILD=0x003;
	private static final int  SEARCHPASSWORD_SUCCEED=0x004;
	private static final int  ALERT_NEW_VERSION = 5;

	EditText name,password;
	TextView  register,login;
	View forgetpassword;
	AlertDialog searchPW,loginingAlert,loginwrongAlert;
	String userID,userPassword;
	Handler handler;
    private String loginExpire ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);
		final String userSave = sharedPreferences.getString(BaseActivity.NAME, "");
		final String passSave = sharedPreferences.getString(BaseActivity.PASSWORD, "");
		
		loginExpire = getIntent().getStringExtra("tonggou.loginExpire");
		
		name=(EditText) findViewById(R.id.loginname);
		if(userSave!=null&&!"".equals(userSave)){
			name.setText(userSave);
		}		
		password=(EditText) findViewById(R.id.loginpassword);
		if(passSave!=null&&!"".equals(passSave)){
			password.setText(passSave);
		}
		name.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66) { 
					password.requestFocus(); 
				} 
				return false; 
			} 
		}); 
		password.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				userID=name.getText().toString();
				userPassword=password.getText().toString();
				if(userID!=null&&!"".equals(userID)){
					if(userPassword!=null&&!"".equals(userPassword)){	
						loginingAlert= new AlertDialog.Builder(LoginActivity.this).create();
						loginingAlert.show();			
						Window window = loginingAlert.getWindow();
						window.setContentView(R.layout.logining);
						new Thread(){
							public void run(){
								login(userID,userPassword);
							}
						}.start();
					}else{
						Toast.makeText(LoginActivity.this,getString(R.string.login_password_null), Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(LoginActivity.this,getString(R.string.login_name_null), Toast.LENGTH_SHORT).show();
				}


				return false;
			}
		});

		name.clearFocus();
		password.clearFocus();
		register=(TextView) findViewById(R.id.register);
		login=(TextView) findViewById(R.id.login);

		forgetpassword=findViewById(R.id.forgetpassword);



		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent toHome = new Intent(LoginActivity.this,RegisterActivity.class);
				startActivity(toHome);
			}
		});
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userID=name.getText().toString();
				userID = userID.trim();
				userPassword=password.getText().toString();
				userPassword = userPassword.trim();
				if(userID!=null&&!"".equals(userID)){
					if(userPassword!=null&&!"".equals(userPassword)){
						if(userPassword.indexOf(" ")!=-1){
							Toast.makeText(LoginActivity.this,"密码不能含有空格", Toast.LENGTH_SHORT).show();
							return;
						}
						loginingAlert= new AlertDialog.Builder(LoginActivity.this).create();
						loginingAlert.show();			
						Window window = loginingAlert.getWindow();
						window.setContentView(R.layout.logining);
						new Thread(){
							public void run(){
								login(userID,userPassword);
							}
						}.start();
					}else{
						Toast.makeText(LoginActivity.this,getString(R.string.login_password_null), Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(LoginActivity.this,getString(R.string.login_name_null), Toast.LENGTH_SHORT).show();
				}

			}
		});
		forgetpassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchPW= new AlertDialog.Builder(LoginActivity.this).create();
				searchPW.setView((LoginActivity.this).getLayoutInflater().inflate(R.layout.searchpasswordback, null));
				searchPW.show();			
				LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
				View textEntryView = inflater.inflate(R.layout.searchpasswordback, null);
				Window window = searchPW.getWindow();
				window.setContentView(textEntryView);

				final EditText searchpasswordEdit=(EditText) window.findViewById(R.id.searchpasswordname);
				TextView searchpassword=(TextView) window.findViewById(R.id.searchpasswordok);
				searchpassword.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						final String searchpassword_name=searchpasswordEdit.getText().toString();
						if(searchpassword_name!=null&&!"".equals(searchpassword_name)){
							new Thread(){
								public void run(){
									searchPassword(searchpassword_name);
								}
							}.start();
							searchPW.cancel();
							searchPW.dismiss();
						}else{
							Toast.makeText(LoginActivity.this,getString(R.string.searchpassword_name), Toast.LENGTH_SHORT).show();
						}

					}
				});
			}
		});
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				switch(msg.what){
				case NETWORK_FAILD: 
					if(loginingAlert!=null){					
						loginingAlert.cancel();
						loginingAlert.dismiss();
					}
					Toast.makeText(LoginActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case LOGIN_SUCCEED: 
					if(loginingAlert!=null){					
						loginingAlert.cancel();
						loginingAlert.dismiss();
					}
					goToHomePage(userID,userPassword);
					break;
				case LOGIN_FAILD: 		
					if(loginingAlert!=null){					
						loginingAlert.cancel();
						loginingAlert.dismiss();
					}
					loginwrongAlert= new AlertDialog.Builder(LoginActivity.this).create();
					loginwrongAlert.show();			
					Window window = loginwrongAlert.getWindow();
					window.setContentView(R.layout.loginwrong);
					TextView loginwrong_message =(TextView) window.findViewById(R.id.loginwrong_message);
					loginwrong_message.setText((String)msg.obj);
					TextView loginwrong_ok=(TextView) window.findViewById(R.id.loginwrongok);
					loginwrong_ok.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							loginwrongAlert.cancel();
							loginwrongAlert.dismiss();
						}
					});
					break;
				case SEARCHPASSWORD_FAILD: 
					Toast.makeText(LoginActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case SEARCHPASSWORD_SUCCEED: 
					Toast.makeText(LoginActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case ALERT_NEW_VERSION:					
					if(DefaultUpdateCheck.versionAction!=null&&DefaultUpdateCheck.versionAction.getAction()==Version.UPDATE_ACTION_FORCE){  //强制升级
						new AlertDialog.Builder(LoginActivity.this)
						.setTitle("强制升级提示")
						.setMessage(DefaultUpdateCheck.versionAction.getMessage()==null?"":DefaultUpdateCheck.versionAction.getMessage())
						.setPositiveButton("是", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {										
								//forceUpdateNewVersion = true;

								////////////////////////////////////////////////////////////////
								String downUrl = DefaultUpdateCheck.versionAction.getUrl();
								if(downUrl==null||"".equals(downUrl)){
									//url错误提示
									Toast.makeText(LoginActivity.this, "url为空", Toast.LENGTH_LONG).show();
								}else{
									Uri uri = Uri.parse(downUrl);  
									Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
									startActivity(intent); 
								}
								////////////////////////////////////////////////////////////////
							}
						}).setNeutralButton("否", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {  //退出
								//NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
								//notiManager.cancel(0x7f030000);
								exit();
							}
						}).show();
					}else if(DefaultUpdateCheck.versionAction!=null&&DefaultUpdateCheck.versionAction.getAction()==Version.UPDAATE_ACTION_ALERT){     //提示升级
						new AlertDialog.Builder(LoginActivity.this)
						.setTitle("升级提示")
						.setMessage(DefaultUpdateCheck.versionAction.getMessage()==null?"":DefaultUpdateCheck.versionAction.getMessage())
						.setPositiveButton("是", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

								////////////////////////////////////////////////////////////////
								String downUrl = DefaultUpdateCheck.versionAction.getUrl();
								if(downUrl==null||"".equals(downUrl)){
									//url错误提示
									Toast.makeText(LoginActivity.this, "url为空", Toast.LENGTH_LONG).show();
								}else{
									Uri uri = Uri.parse(downUrl);  
									Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
									startActivity(intent); 
								}
								/////////////////////////////////////////////////////////////////
							
							}
						}).setNeutralButton("否", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

							}
						}).show();
					}							
					break;

				}
			}
		};

		sendMessage(ALERT_NEW_VERSION, null);  //提示版本情况
		
        if(loginExpire!=null&&!"".equals(loginExpire)){
        	//登录过期提示
        	new AlertDialog.Builder(LoginActivity.this) 		
	        .setTitle(getString(R.string.exit_title)) 
	        .setMessage(loginExpire) 
	        .setNeutralButton("关闭", new DialogInterface.OnClickListener(){ 
	        	public void onClick(DialogInterface dialog, int whichButton){ 
	        	} 
	        }).show();
        }
	}

	public void onDestroy() {	      
	    super.onDestroy();
		if(searchPW!=null){					
			searchPW =null;
		}
		if(loginingAlert!=null){					
			loginingAlert =null;
		}
		if(loginwrongAlert!=null){					
			loginwrongAlert =null;
		}
	}
	
	private void login(String tid,String access_token){
		if(TongGouApplication.imageVersion==null||"".equals(TongGouApplication.imageVersion)){
			TongGouApplication.imageVersion = sharedPreferences.getString(BaseActivity.SCREEN, "480X800");
		}		
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/login";
		LoginParser loginParser = new LoginParser();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userNo",tid));	
		nameValuePairs.add(new BasicNameValuePair("password",access_token));
		nameValuePairs.add(new BasicNameValuePair("platform",INFO.CLIENT_PLATFORM));
		nameValuePairs.add(new BasicNameValuePair("appVersion",INFO.VERSION));
		//可选
		nameValuePairs.add(new BasicNameValuePair("platformVersion",TongGouApplication.platformVersion));
		nameValuePairs.add(new BasicNameValuePair("mobileModel",TongGouApplication.mobileModel));
		nameValuePairs.add(new BasicNameValuePair("imageVersion",TongGouApplication.imageVersion));


		NetworkState ns = Network.getNetwork(LoginActivity.this).httpPostUpdateString(url,nameValuePairs,loginParser);	
		if(ns.isNetworkSuccess()){
			if(loginParser.isSuccessfull()){
				TongGouApplication app = (TongGouApplication)this.getApplication();
				app.saveSomeInformation(loginParser,sharedPreferences,tid,access_token);

				sendMessage(LOGIN_SUCCEED, null);
			}else{
				//解析出错
				sendMessage(LOGIN_FAILD, loginParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
		}
	}



	private void goToHomePage(final String userID, final String password) {	
		//跳转主页
		Intent toHome = new Intent(LoginActivity.this,HomePageActivity.class);
		startActivity(toHome);
	}


	private void searchPassword(final String userName) {

		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/user/password/userNo/"+userName;
		SearchPasswordParser searchPasswordParser = new SearchPasswordParser();		
		NetworkState ns = Network.getNetwork(LoginActivity.this).httpGetUpdateString(url,searchPasswordParser);	
		if(ns.isNetworkSuccess()){
			if(searchPasswordParser.isSuccessfull()){
				sendMessage(SEARCHPASSWORD_SUCCEED, searchPasswordParser.getSearchPasswordResponse().getMessage());
			}else{
				//解析出错
				sendMessage(SEARCHPASSWORD_FAILD, searchPasswordParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
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

