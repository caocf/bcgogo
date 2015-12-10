package com.tonggou.andclient;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.app.UpdateFaultDic;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.RegistrationParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SomeUtil;

public class RegisterActivity extends BaseActivity {
	private TextView  next;
	private ImageView back;
	private EditText  name,pass,password,phonenumber,carname;//,nickname
	private String carNum="";
	private AlertDialog registerAlert1;

	private String  nameStr,passwordStr,phonenumberStr,phonenumberStr1;
	//nameStr passwordStr phonenumberStr carNum
	private Handler handler;

	private static final int  LOGIN_SUCCEED=0x001;
	private static final int  LOGIN_FAILD=0x002;

	public static String registercarnameStr;    //车牌号

	android.widget.RelativeLayout relat1,relat2;
	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register1);
		name=(EditText) findViewById(R.id.registername);
		pass=(EditText) findViewById(R.id.registerpass);
		password=(EditText) findViewById(R.id.registerpassword);
		//nickname=(EditText) findViewById(R.id.registernickname);
		phonenumber=(EditText) findViewById(R.id.registerphonenum);
		carname = (EditText) findViewById(R.id.registercarname);
		//relat1 = findViewById(R.id.relat1);

		relat1 = (android.widget.RelativeLayout)findViewById(R.id.relat1);
		//relat2 = (android.widget.RelativeLayout)findViewById(R.id.relat2);

		name.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					pass.requestFocus(); 
				} 
				return false; 
			} 
		});
		/*carname.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					pass.requestFocus(); 
				} 
				return false; 
			} 
		});*/

		name.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
			@Override 
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					nameStr = name.getText().toString().trim();
					if(nameStr!=null&&!"".equals(nameStr)){	
						if(SomeUtil.isNumeric(nameStr)){  //isMobileNO  isNumeric
							phonenumber.setText(nameStr);
							//relat2.setVisibility(View.VISIBLE);
							relat1.setVisibility(View.GONE);
							carNum="";
						}else{
							if(SomeUtil.isCarNum(nameStr.trim().toUpperCase())){
								carNum = nameStr.trim().toUpperCase();
								relat1.setVisibility(View.VISIBLE);
								//relat2.setVisibility(View.GONE);
								//carname.setText(nameStr);

							}else{
								carNum="";
								//phonenumber.setText("");
							}
						}
					}else{
						carNum="";
						//phonenumber.setText("");
					}
				}
			}
		});

		pass.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					password.requestFocus(); 
				} 
				return false; 
			} 
		}); 


		password.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66 && event.getAction() == KeyEvent.ACTION_UP) { 
					phonenumber.requestFocus(); 
				} 
				return false; 
			} 
		}); 
		phonenumber.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66 && event.getAction() == KeyEvent.ACTION_UP) { 
					toNext();
				} 
				return false; 
			} 
		});

		name.clearFocus();
		pass.clearFocus();
		password.clearFocus();
		phonenumber.clearFocus();
		//carname.clearFocus();
		next=(TextView) findViewById(R.id.register_next);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				nameStr = name.getText().toString().trim();
				//String carnameStr = carname.getText().toString().trim();
				/*
				if(carnameStr != null && "".equals(carnameStr)){
					if(SomeUtil.isCarNum(carnameStr.trim().toUpperCase())){
						carNum = carnameStr.trim().toUpperCase();
					}else{
						carNum = "";
					}
				}else{
					carNum = "";
				}*/



				if(nameStr != null && !"".equals(nameStr)){	
					if(SomeUtil.isNumeric(nameStr)){
						
						phonenumberStr1 = nameStr.trim();
						/*
						if(carnameStr != null && !"".equals(carnameStr)){
							if(SomeUtil.isCarNum(carnameStr.trim().toUpperCase())){
								carNum = carnameStr.trim().toUpperCase();
							}else{
								carNum = "";
							}
						}else{
							carNum = "";
						}
					*/
						}else{
							phonenumberStr1 = phonenumberStr;
						if(SomeUtil.isCarNum(nameStr.trim().toUpperCase())){
							carNum = nameStr.trim().toUpperCase();
						}else{
							carNum="";
							//phonenumber.setText("");
						}
					}
				}else{
					carNum="";
					//phonenumber.setText("");
				}

				toNext();
			}
		});
		back=(ImageView) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RegisterActivityNext.registercarnameStr=null;
				RegisterActivityNext.carBrand=null;
				RegisterActivityNext.carMold=null;
				RegisterActivityNext.carBrandId=null;
				RegisterActivityNext.carMoldId=null;
				RegisterActivityNext.shop2DCodeStr=null;
				RegisterActivityNext.shop2DCodeId = null;
				RegisterActivityNext.registernextmileStr=null;
				RegisterActivityNext.registernexttimeStr=null;
				RegisterActivityNext.registernexttime2Str=null;
				RegisterActivityNext.currentMileStr=null;
				RegisterActivity.this.finish();
			}
		});

		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				switch(msg.what){
				case LOGIN_SUCCEED: 
					registerAlert1.cancel();
					registerAlert1.dismiss();
					Toast.makeText(RegisterActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case LOGIN_FAILD: 	
					registerAlert1.cancel();
					registerAlert1.dismiss();
					Toast.makeText(RegisterActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			return true;
		}else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void onBackPressed() {
		RegisterActivityNext.registercarnameStr=null;
		RegisterActivityNext.carBrand=null;
		RegisterActivityNext.carMold=null;
		RegisterActivityNext.carBrandId=null;
		RegisterActivityNext.carMoldId=null;
		RegisterActivityNext.shop2DCodeStr=null;
		RegisterActivityNext.shop2DCodeId = null;
		RegisterActivityNext.registernextmileStr=null;
		RegisterActivityNext.registernexttimeStr=null;
		RegisterActivityNext.registernexttime2Str=null;
		RegisterActivityNext.currentMileStr=null;
		RegisterActivity.this.finish();
	}


	public void toNext(){
		nameStr=name.getText().toString().trim().toUpperCase();
		String passStr=pass.getText().toString();
		passwordStr=password.getText().toString();
		phonenumberStr=phonenumber.getText().toString();
		//String registecarnameStr = carname.getText().toString().trim().toUpperCase();

		if(nameStr !=null && !"".equals(nameStr) && nameStr.length()>4){
			if(SomeUtil.isRegisterName(nameStr)){
				if(passStr != null && !"".equals(passStr)){
					if(passStr.length()>=6){
						if(passStr.indexOf(" ")!=-1){
							Toast.makeText(RegisterActivity.this,"密码不能含有空格", Toast.LENGTH_SHORT).show();
							return;
						}
						if(SomeUtil.justIfChineseInput(passStr)){
							Toast.makeText(RegisterActivity.this,"密码不能含有中文字符", Toast.LENGTH_SHORT).show();
							return;
						}

						if(passwordStr!=null&&!"".equals(passwordStr)){
							//if(registecarnameStr != null && !"".equals(registecarnameStr)){
								//if(SomeUtil.isCarNum(registecarnameStr.trim().toUpperCase())){
									if(passwordStr.equals(passStr)){
										if(phonenumberStr != null && !"".equals(phonenumberStr)){
											if(SomeUtil.isPhoneNumberValid(phonenumberStr)){
												/*Intent intent = new Intent(RegisterActivity.this,RegisterActivityNext.class);
												intent.putExtra("tonggou.name",nameStr);
												intent.putExtra("tonggou.password",passwordStr);
												intent.putExtra("tonggou.carNum",carNum);
												intent.putExtra("tonggou.phonenumber",phonenumberStr);
												startActivity(intent);*/

												registerAlert1= new AlertDialog.Builder(RegisterActivity.this).create();
												registerAlert1.show();	
												registerAlert1.setCanceledOnTouchOutside(false);
												Window window = registerAlert1.getWindow();
												window.setContentView(R.layout.logining);
												TextView waiting_message =(TextView) window.findViewById(R.id.loging_alerttext);
												waiting_message.setText(R.string.register_waiting);
												new Thread(){
													public void run(){
														//name,password,nickname,phonenumber,
														register(nameStr,passwordStr,phonenumberStr1, carNum);
													}
												}.start();
											}else{
												Toast.makeText(RegisterActivity.this,getString(R.string.wrongnumber), Toast.LENGTH_SHORT).show();
											}
										}else{
											Toast.makeText(RegisterActivity.this,getString(R.string.login_phonenumberStr_ull), Toast.LENGTH_SHORT).show();
										}

									}else{
										Toast.makeText(RegisterActivity.this,getString(R.string.login_password_same), Toast.LENGTH_SHORT).show();
									}
							//}else {
								//	Toast.makeText(RegisterActivity.this, "车牌号格式不正确", Toast.LENGTH_LONG).show();
								//}
							//}else {
							//	Toast.makeText(RegisterActivity.this, "车牌号不能为空", Toast.LENGTH_LONG).show();
						//	}
						}else{
							Toast.makeText(RegisterActivity.this,getString(R.string.login_password_again_null), Toast.LENGTH_SHORT).show();
						}

					}else{
						Toast.makeText(RegisterActivity.this,"密码不能少于6位数", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(RegisterActivity.this,getString(R.string.login_password_null), Toast.LENGTH_SHORT).show();
				}	
			}else{
				Toast.makeText(RegisterActivity.this,getString(R.string.login_name_wrong), Toast.LENGTH_SHORT).show();
			}
		}else{
		Toast.makeText(RegisterActivity.this,getString(R.string.login_name_null)+"或者不能少于5个字", Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 
	 * @param name                       //用户名  必填
	 * @param password                   //密码       必填
	 * @param phonenumber                //手机号  必填
	 * @param chePaiHao        //车牌号
	
	 */
	@SuppressLint("DefaultLocale")
	private void register(String name,String password,String phonenumber,
			String chePaiHao){

		if(TongGouApplication.imageVersion==null||"".equals(TongGouApplication.imageVersion)){
			TongGouApplication.imageVersion = sharedPreferences.getString(BaseActivity.SCREEN, "480X800");
		}	


		RegistrationParser registrationParser = new RegistrationParser();
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/user/registration";	

		StringBuffer sb1 = new StringBuffer();
		//email={email}&name={name}&shopEmployee={shopEmployee}
		//必填的 用户名 ，密码，手机号
		String value1 = "{\"userNo\":\""+name+"\", " +" \"password\":\""+password+"\", " +
				" \"mobile\":\""+phonenumber+"\"" ;
		//" \"mobile\":\""+phonenumber+"\",\"name\":\""+nickname+"\"" ;

		sb1.append(value1);

		if(chePaiHao!=null&&!"".equals(chePaiHao)){//车牌号
			String valueVB = ",\"vehicleNo\":\""+chePaiHao.toUpperCase()+"\"" ;
			sb1.append(valueVB);
		}
		sb1.append(",\"loginInfo\":{");

		String platformStr = "\"platform\":\""+INFO.CLIENT_PLATFORM+"\""  ;
		sb1.append(platformStr);

		if(INFO.VERSION!=null){
			String appVersionStr = ",\"appVersion\":\""+INFO.VERSION+"\""  ;
			sb1.append(appVersionStr);
		}
		if(TongGouApplication.platformVersion!=null){
			String platformVersionStr = ",\"platformVersion\":\""+TongGouApplication.platformVersion+"\""  ;
			sb1.append(platformVersionStr);
		}
		if(TongGouApplication.mobileModel!=null){
			String mobileModelStr = ",\"mobileModel\":\""+TongGouApplication.mobileModel+"\""  ;
			sb1.append(mobileModelStr);
		}
		if(TongGouApplication.imageVersion!=null){
			String imageVersionStr = ",\"imageVersion\":\""+TongGouApplication.imageVersion+"\""  ;
			sb1.append(imageVersionStr);
		}

		String valueMK = "}}";
		sb1.append(valueMK);

		String resultStr1 = sb1.toString();

		NetworkState ns = Network.getNetwork(RegisterActivity.this).httpPutUpdateString(url,resultStr1.getBytes(),registrationParser);
		if(ns.isNetworkSuccess()){
			if(registrationParser.isSuccessfull()){
				//正确的处理逻辑 
				String mess = registrationParser.getRegistrationReponse().getMessage();

				sendMessage(LOGIN_SUCCEED, mess);
				
				SharedPreferences setSharedpre1 = getSharedPreferences("Register", MODE_PRIVATE);
				SharedPreferences.Editor editor1 = setSharedpre1.edit();
				editor1.putString("tonggou.carNum", carNum);
				editor1.putString("tonggou.name", nameStr);
				editor1.putString("tonggou.password",passwordStr );
				editor1.putString("tonggou.phonenumber", phonenumberStr);
				editor1.commit();
				
			//	Intent intent = new Intent(RegisterActivity.this,RegisterActivityNext.class);
			//	intent.putExtra("tonggou.carNum",carNum);
				/*intent.putExtra("tonggou.name",nameStr);
				intent.putExtra("tonggou.password",passwordStr);
				
				intent.putExtra("tonggou.phonenumber",phonenumberStr);*/
			//	startActivity(intent);
				
				Intent intent = new Intent(RegisterActivity.this,CaptureActivity.class);
				intent.putExtra("fromshopUI", "register");
				startActivity(intent);
				
			}else{
				//提示用户错误
				String errorAlert = registrationParser.getErrorMessage();
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
