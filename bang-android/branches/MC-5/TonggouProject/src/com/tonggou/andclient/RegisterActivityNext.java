package com.tonggou.andclient;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.UpdateFaultDic;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.RegistrationParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SomeUtil;
//import com.tonggou.andclient.vo.Logininfo;

public class RegisterActivityNext  extends BaseActivity {

	private static final int  LOGIN_SUCCEED=0x001;
	private static final int  LOGIN_FAILD=0x002;


	private String name,password,phonenumber,carnum,scanshop;//,nickname
	private TextView  register,registernexttime,registernexttime2,registercarnum1,registercarnum2;
	private View back;
	private TextView scanTextView;
	public  String carNum;
	public static String registercarnameStr;    //车牌号
	public static String carBrand;              //品牌
	public static String carMold;               //车型
	public static String carBrandId;              //品牌id
	public static String carMoldId;               //车型id
	public static String shop2DCodeStr="";       //店铺二维码    用于显示
	public static String shop2DCodeId="";              //店铺id  
	public static String registernextmileStr;   //下次保养里程
	public static String registernexttimeStr;   //下次保险时间
	public static String registernexttime2Str;  //下次验车时间
	public static String currentMileStr;        //当前里程

	private Handler handler;
	private EditText  registercarname,registernextmile,registerman;

	private AlertDialog registerAlert;
	private Dialog dialog ;
	private View view;
	private boolean meterOk=true,meterOk2=true,timeOk=true,timeOk2=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		registercarnameStr=null;
//		carBrand=null;
//		carMold=null;
//		carBrandId=null;
//		carMoldId=null;
//		shop2DCodeStr=null;
//		shop2DCodeId = null;
//		registernextmileStr=null;
//		registernexttimeStr=null;
//		registernexttime2Str=null;
//		registermanStr=null;

		setContentView(R.layout.register2);
		name = getIntent().getStringExtra("tonggou.name");
		password =  getIntent().getStringExtra("tonggou.password");
		carNum =  getIntent().getStringExtra("tonggou.carNum");
		phonenumber =  getIntent().getStringExtra("tonggou.phonenumber");

		if(carNum!=null && !"".equals(carNum)){
			registercarnameStr=carNum;
		}
		registercarname=(EditText) findViewById(R.id.registercarname);            //车牌号
		if(registercarnameStr!=null){
			registercarname.setText(registercarnameStr);
		}
		registerman=(EditText) findViewById(R.id.registerman);                    //当前里程
		if(currentMileStr!=null){
			registerman.setText(currentMileStr);
		}
		registernextmile=(EditText) findViewById(R.id.registernextmile);          //下次保养里程
		if(registernextmileStr!=null){
			registernextmile.setText(registernextmileStr);
		}
		registernexttime=(TextView) findViewById(R.id.registernexttime);          //下次保险时间
		if(registernexttimeStr!=null){
			registernexttime.setText(registernexttimeStr);
		}
		registernexttime2=(TextView) findViewById(R.id.registernexttime2);        //下次验车时间
		if(registernexttime2Str!=null){
			registernexttime2.setText(registernexttime2Str);
		}
		registercarname.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					registerman.requestFocus(); 
				} 
				return false; 
			} 
		});
		registerman.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					registernextmile.requestFocus(); 
				} 
				return false; 
			} 
		}); 

		registercarname.clearFocus();
		registerman.clearFocus();
		registernextmile.clearFocus();
		registernexttime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder  	builder = new AlertDialog.Builder(RegisterActivityNext.this);
				View view = LayoutInflater.from(RegisterActivityNext.this).inflate(R.layout.date_time_dialog, null);
				final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
				final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker);

				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

				timePicker.setIs24HourView(true);
				timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
				timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
				builder.setView(view);

				builder.setTitle("选取时间");
				builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						timePicker.clearFocus();
						StringBuffer sb = new StringBuffer();
						sb.append(String.format("%d-%02d-%02d", 
								datePicker.getYear(), 
								datePicker.getMonth() + 1,
								datePicker.getDayOfMonth()));
						sb.append("  ");
						if(timePicker.getCurrentHour()<10){
							sb.append("0");
						}
						sb.append(timePicker.getCurrentHour())
						.append(":");
						if(timePicker.getCurrentMinute()<10){
							sb.append("0");
						}
						sb.append(timePicker.getCurrentMinute());



						registernexttime.setText(sb);
						// etEndTime.requestFocus();

						dialog.cancel();
					}
				});
				Dialog dialog = builder.create();
				dialog.show();
			}
		});
		registernexttime2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder  	builder = new AlertDialog.Builder(RegisterActivityNext.this);
				View view = LayoutInflater.from(RegisterActivityNext.this).inflate(R.layout.date_time_dialog, null);
				final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
				final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker);

				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

				timePicker.setIs24HourView(true);
				timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
				timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
				builder.setView(view);


				builder.setTitle("选取时间");
				builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						timePicker.clearFocus();
						StringBuffer sb = new StringBuffer();
						sb.append(String.format("%d-%02d-%02d", 
								datePicker.getYear(), 
								datePicker.getMonth() + 1, 
								datePicker.getDayOfMonth()));
						sb.append("  ");
						if(timePicker.getCurrentHour()<10){
							sb.append("0");
						}
						sb.append(timePicker.getCurrentHour())
						.append(":");
						if(timePicker.getCurrentMinute()<10){
							sb.append("0");
						}
						sb.append(timePicker.getCurrentMinute());


						registernexttime2.setText(sb);

						dialog.cancel();
					}
				});
				Dialog dialog = builder.create();
				dialog.show();
			}
		});
		//setDateTime(); 


		register=(TextView) findViewById(R.id.register);
		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				registercarnameStr=registercarname.getText().toString().toUpperCase();//车牌号
				registernextmileStr=registernextmile.getText().toString();  //下次保养里程
				registernexttimeStr=registernexttime.getText().toString();  //下次保险时间
				registernexttime2Str=registernexttime2.getText().toString(); //下次保险时间
				currentMileStr=registerman.getText().toString();             //下次保险时间

				carBrand = registercarnum1.getText().toString();      //品牌
				carMold = registercarnum2.getText().toString();       //车型

				Time localTime = new Time("Asia/Hong_Kong");  
				localTime.setToNow();  
				long nextInsuranceTimeLong = SomeUtil.StringDateToLong(registernexttimeStr);  //保险时间
				long nowTime = SomeUtil.StringDateToLong(localTime.format("%Y-%m-%d %H:%M").toString());  //保险时间
				long nextExamineTimeLong = SomeUtil.StringDateToLong(registernexttime2Str);    //验车时间
				if(currentMileStr !=null && !"".equals(currentMileStr)){					
					try{
						Integer.parseInt(currentMileStr);
						meterOk=true;
					}catch(Exception  e){
						meterOk=false;
						Toast.makeText(RegisterActivityNext.this, "当前里程输入不正确", Toast.LENGTH_SHORT).show();
					}
				}
				 if(registernextmileStr!=null&&!"".equals(registernextmileStr)){
					try{
						Integer.parseInt(registernextmileStr);
						meterOk2=true;
					}catch(Exception  e){
						meterOk2=false;
						Toast.makeText(RegisterActivityNext.this, "下次保养里程输入不正确", Toast.LENGTH_SHORT).show();
					}
				}
				if(meterOk&&meterOk2){
					if(registernexttimeStr!=null&&!"".equals(registernexttimeStr)){
						if(nextInsuranceTimeLong>=nowTime){	
							timeOk=true;
						}else{
							timeOk=false;
							Toast.makeText(RegisterActivityNext.this, "下次保险的时间不能早于当前时间", Toast.LENGTH_SHORT).show();
						}
					}else{
						timeOk=true;
					}
				}
				if(meterOk&&meterOk2&&timeOk){

					if(registernexttime2Str != null && !"".equals(registernexttime2Str)){
						if(nextExamineTimeLong >= nowTime){	
							timeOk2 = true;
						}else{
							timeOk2 = false;
							Toast.makeText(RegisterActivityNext.this, "下次验车的时间不能早于当前时间", Toast.LENGTH_SHORT).show();
						}
					}else{
						timeOk2 = true;
					}
				}

				if(meterOk&&meterOk2&&timeOk&&timeOk2){	
					registerAlert= new AlertDialog.Builder(RegisterActivityNext.this).create();
					registerAlert.show();	
					registerAlert.setCanceledOnTouchOutside(false);
					Window window = registerAlert.getWindow();
					window.setContentView(R.layout.logining);
					TextView waiting_message =(TextView) window.findViewById(R.id.loging_alerttext);
					waiting_message.setText(R.string.register_waiting);
					new Thread(){
						public void run(){
							//name,password,nickname,phonenumber,
							register(name,password,phonenumber,
									registercarnameStr,carBrand,carBrandId,carMold,carMoldId,shop2DCodeId,
									registernextmileStr,registernexttimeStr, registernexttime2Str,currentMileStr);
						}
					}.start();
				}
				/*if(registernexttimeStr==null||"".equals(registernexttimeStr)){
					if(registernexttime2Str==null||"".equals(registernexttime2Str)){

					}else{
						if(registernexttime2Str!=null&&nextExamineTimeLong>=nowTime){	
							registerAlert= new AlertDialog.Builder(RegisterActivityNext.this).create();
							registerAlert.show();	
							registerAlert.setCanceledOnTouchOutside(false);
							Window window = registerAlert.getWindow();
							window.setContentView(R.layout.logining);
							TextView waiting_message =(TextView) window.findViewById(R.id.loging_alerttext);
							waiting_message.setText(R.string.register_waiting);
							new Thread(){
								public void run(){
									//name,password,nickname,phonenumber,
									register(name,password,phonenumber,
											registercarnameStr,carBrand,carBrandId,carMold,carMoldId,shop2DCodeId,
											registernextmileStr,registernexttimeStr, registernexttime2Str,registermanStr);
								}
							}.start();
						}else{
							Toast.makeText(RegisterActivityNext.this, "下次验车的时间不能早于当前时间", Toast.LENGTH_SHORT).show();
						}
					}
				}else{
					if(registernexttimeStr!=null&&nextInsuranceTimeLong>=nowTime){	
						registerAlert= new AlertDialog.Builder(RegisterActivityNext.this).create();
						registerAlert.show();	
						registerAlert.setCanceledOnTouchOutside(false);
						Window window = registerAlert.getWindow();
						window.setContentView(R.layout.logining);
						TextView waiting_message =(TextView) window.findViewById(R.id.loging_alerttext);
						waiting_message.setText(R.string.register_waiting);
						new Thread(){
							public void run(){
								//name,password,nickname,phonenumber,
								register(name,password,phonenumber,
										registercarnameStr,carBrand,carBrandId,carMold,carMoldId,shop2DCodeId,
										registernextmileStr,registernexttimeStr, registernexttime2Str,registermanStr);
							}
						}.start();
					}else{

						Toast.makeText(RegisterActivityNext.this, "下次保险的时间不能早于当前时间", Toast.LENGTH_SHORT).show();
					}
				}
				 */








			}
		});
		back=findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				registercarnameStr=registercarname.getText().toString().toUpperCase();
				registernextmileStr=registernextmile.getText().toString();
				registernexttimeStr=registernexttime.getText().toString();
				registernexttime2Str=registernexttime2.getText().toString();
				currentMileStr=registerman.getText().toString();

				carBrand = registercarnum1.getText().toString();
				carMold = registercarnum2.getText().toString();
				
				RegisterActivityNext.this.finish();
			}
		});
		scanTextView = (TextView)findViewById(R.id.registerscanshop);
		if(shop2DCodeStr!=null){
			scanTextView.setText(shop2DCodeStr);
		}
		scanTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RegisterActivityNext.this,CaptureActivity.class);
				startActivityForResult(intent, 4040);
			}
		});
		registercarnum1=(TextView) findViewById(R.id.registercarnum1);                                      //品牌
		if(carBrand!=null){
			registercarnum1.setText(carBrand);
		}
		registercarnum1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RegisterActivityNext.this,AppointmentNetWorkSearch.class);
				intent.putExtra("tonggou.from", "pinpai");
				intent.putExtra("tonggou.pinpai", "");//选择品牌
				startActivityForResult(intent, 1010);
			}
		});
		registercarnum2=(TextView) findViewById(R.id.registercarnum2);                                     //车型
		if(carMold!=null){
			registercarnum2.setText(carMold);
		}
		registercarnum2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(carBrandId!=null&&!"".equals(carBrandId)){
					Intent intent = new Intent(RegisterActivityNext.this,AppointmentNetWorkSearch.class);
					intent.putExtra("tonggou.from", "chexing");
					intent.putExtra("tonggou.pinpai",carBrandId);//选择车型
					startActivityForResult(intent, 2020);
				}else{
					Toast.makeText(RegisterActivityNext.this,getString(R.string.brand_first),Toast.LENGTH_SHORT).show();

				}
			}
		});




		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				switch(msg.what){
				case LOGIN_SUCCEED: 
					registerAlert.cancel();
					registerAlert.dismiss();
					Toast.makeText(RegisterActivityNext.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					goConnectOBDPage(name,password);
					break;
				case LOGIN_FAILD: 	
					registerAlert.cancel();
					registerAlert.dismiss();
					Toast.makeText(RegisterActivityNext.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
	}
	private void setDateTime(){
		Time localTime = new Time("Asia/Hong_Kong");  
		localTime.setToNow();  
		registernexttime.setText(localTime.format("%Y-%m-%d %H:%M")); 
		registernexttime2.setText(localTime.format("%Y-%m-%d %H:%M")); 
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
		registercarnameStr = registercarname.getText().toString();
		registernextmileStr = registernextmile.getText().toString();
		registernexttimeStr = registernexttime.getText().toString();
		registernexttime2Str = registernexttime2.getText().toString();
		currentMileStr = registerman.getText().toString();

		carBrand = registercarnum1.getText().toString();
		carMold = registercarnum2.getText().toString();
		
		RegisterActivityNext.this.finish();
	}
	
	

	/**
	 * 
	 * @param name                       //用户名  必填
	 * @param password                   //密码       必填
	 * @param nickname                   //客户名  必填
	 * @param phonenumber                //手机号  必填
	 * @param chePaiHao        //车牌号
	 * @param pinPai            //品牌
	 * @param cheXing           //车型
	 * @param erWeiMa         //店铺二维码
	 * @param baoYangLiCheng   //下次保养里程
	 * @param baoXianShiJian    //下次保险时间
	 * @param yanCheShiJian     //下次验车时间
	 * @param currentMileage       //当前里程
	 */
	private void register(String name,String password,String phonenumber,
			String chePaiHao,String pinPai,String pinPaiId,String cheXing,String cheXingId,String erWeiMa,String baoYangLiCheng,
			String baoXianShiJian,String yanCheShiJian,String currentMileage){

		if(TongGouApplication.imageVersion==null||"".equals(TongGouApplication.imageVersion)){
			TongGouApplication.imageVersion = sharedPreferences.getString(BaseActivity.SCREEN, "480X800");
		}	
		double xCBYLC = 0;
		if(baoYangLiCheng != null && !"".equals(baoYangLiCheng)){
			xCBYLC = Double.valueOf(baoYangLiCheng);
		}

		long bXSJ = SomeUtil.StringDateToLong(baoXianShiJian);
		long yCSJ = SomeUtil.StringDateToLong(yanCheShiJian);
		//long shop2dCode = SomeUtil.parseShop2DCode(erWeiMa);

		RegistrationParser registrationParser = new RegistrationParser();
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/user/registration";	

		StringBuffer sb = new StringBuffer();
		//email={email}&name={name}&shopEmployee={shopEmployee}
		//必填的 用户名 ，密码，手机号
		String value1 = "{\"userNo\":\""+name+"\", " +" \"password\":\""+password+"\", " +
				" \"mobile\":\""+phonenumber+"\"" ;
		//" \"mobile\":\""+phonenumber+"\",\"name\":\""+nickname+"\"" ;

		sb.append(value1);

		if(chePaiHao!=null&&!"".equals(chePaiHao)){//车牌号
			String valueVB = ",\"vehicleNo\":\""+chePaiHao.toUpperCase()+"\"" ;
			sb.append(valueVB);
		}
		if(pinPai!=null&&!"".equals(pinPai)){   //品牌
			String valueVBID = ",\"vehicleBrand\":\""+pinPai+"\""  ;
			sb.append(valueVBID);
		}
		if(pinPaiId!=null&&!"".equals(pinPaiId)){  //品牌ID
			String valueVM = ",\"vehicleBrandId\":\""+pinPaiId+"\""   ;
			sb.append(valueVM);
		}
		if(cheXing!=null&&!"".equals(cheXing)){   //车型
			String valueVMID = ",\"vehicleModel\":\""+cheXing+"\""  ;
			sb.append(valueVMID);
		}
		if(cheXingId!=null&&!"".equals(cheXingId)){ //车型ID
			String valueVIN = ",\"vehicleModelId\":\""+cheXingId+"\""  ;
			sb.append(valueVIN);
		}

		if(xCBYLC!=0){  //保养里程
			String valueVIN = ",\"nextMaintainMileage\":\""+xCBYLC+"\"" ;
			sb.append(valueVIN);
		}

		if(erWeiMa!=null&&!"".equals(erWeiMa)){ //二维码
			String valueVIN = ",\"shopId\":\""+erWeiMa+"\""  ;
			sb.append(valueVIN);
		}	
		if(currentMileage!=null&&!"".equals(currentMileage)){ //当前里程
			String valueVIN = ",\"currentMileage\":\""+currentMileage+"\""  ;
			sb.append(valueVIN);
		}

		if(bXSJ!=0){ //保险时间
			String insurance = ",\"nextInsuranceTime\":\""+bXSJ+"\""  ;
			sb.append(insurance);
		}

		if(yCSJ!=0){  //验车时间
			String exam = ",\"nextExamineTime\":\""+yCSJ+"\""  ;
			sb.append(exam);
		}
		sb.append(",\"loginInfo\":{");
	
			String platformStr = "\"platform\":\""+INFO.CLIENT_PLATFORM+"\""  ;
			sb.append(platformStr);
		
		if(INFO.VERSION!=null){
			String appVersionStr = ",\"appVersion\":\""+INFO.VERSION+"\""  ;
			sb.append(appVersionStr);
		}
		if(TongGouApplication.platformVersion!=null){
			String platformVersionStr = ",\"platformVersion\":\""+TongGouApplication.platformVersion+"\""  ;
			sb.append(platformVersionStr);
		}
		if(TongGouApplication.mobileModel!=null){
			String mobileModelStr = ",\"mobileModel\":\""+TongGouApplication.mobileModel+"\""  ;
			sb.append(mobileModelStr);
		}
		if(TongGouApplication.imageVersion!=null){
			String imageVersionStr = ",\"imageVersion\":\""+TongGouApplication.imageVersion+"\""  ;
			sb.append(imageVersionStr);
		}
		
		String valueMK = "}}";
		sb.append(valueMK);

		String resultStr = sb.toString();

       NetworkState ns = Network.getNetwork(RegisterActivityNext.this).httpPutUpdateString(url,resultStr.getBytes(),registrationParser);
		if(ns.isNetworkSuccess()){
			if(registrationParser.isSuccessfull()){
				//正确的处理逻辑 
				String mes = registrationParser.getRegistrationReponse().getMessage();
				if(cheXingId!=null&&!"".equals(cheXingId)){
					UpdateFaultDic.getUpdateFaultDic(RegisterActivityNext.this).updateFaultDic(cheXingId);		
				}
				sendMessage(LOGIN_SUCCEED, mes);
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
	private void goConnectOBDPage(final String userID, final String password) {
		getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
		.putString(BaseActivity.NAME, userID)
		.putString(BaseActivity.PASSWORD, password)
		.putString(BaseActivity.PHONE, phonenumber)
		.putString(BaseActivity.BRAND, carBrand)
		.putString(BaseActivity.MODEL, carMold)
		.putString(BaseActivity.VEHICLENUM, registercarnameStr).commit();

		getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
		.putBoolean(BaseActivity.LOGINED, true).commit();
	
		//跳转主页
		MainActivity.ifAutoLogin = true;
		Intent toHome = new Intent(RegisterActivityNext.this,ConnectOBDActivity.class);
		toHome.putExtra("tonggou.connectobd.from","register");
		if(shop2DCodeId!=null&&!"".equals(shop2DCodeId)){
			toHome.putExtra("tonggou.connectobd.shopscanid",shop2DCodeId);
		}
		
		startActivity(toHome);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==1010){	
			if(registercarnum1.getText().toString()!=null&&!"".equals(registercarnum1.getText().toString())){
            	if(!data.getStringExtra("pinpai").equals(registercarnum1.getText().toString())){

            		registercarnum2.setText("");	
        			carMoldId=null;
            	}
            }
			registercarnum1.setText(data.getStringExtra("pinpai"));
			carBrandId=data.getStringExtra("pinpaiId");
		}
		if(resultCode==2020){	
			registercarnum2.setText(data.getStringExtra("chexing"));	//车型
			carMoldId=data.getStringExtra("chexingId");   
		}
		if(resultCode==3030){		
			Toast.makeText(RegisterActivityNext.this,data.getStringExtra("ERROR_INFO") , Toast.LENGTH_LONG).show();

		}
		if(resultCode==4040){
			String s2dCode = data.getStringExtra("iconNames");			
			if(s2dCode!=null&&!"".equals(s2dCode)){
				scanTextView.setText(s2dCode);
				shop2DCodeStr = s2dCode;
			}
			String s2dCodeId = data.getStringExtra("iconID");
			if(s2dCodeId!=null&&!"".equals(s2dCodeId)){
				shop2DCodeId = s2dCodeId;
			}
		}
	}
}
