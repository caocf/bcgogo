package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tonggou.andclient.app.BaseConnectOBDService;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.app.UpdateFaultDic;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.AddBindCarParser;
import com.tonggou.andclient.parse.CommonParser;
import com.tonggou.andclient.parse.LoginParser;
import com.tonggou.andclient.parse.VehicleParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.VehicleInfo;

public class ChangeBindCarsActivity extends BaseActivity{

	private static final int  NETWORK_FAILD=-1;
	private static final int  NETWORK_SUCCEED=0x001;
	private static final int  NETWORK_NODATA=0x002;
	private static final int  CHANGE_SUCCEED=0x003;
	private static final int  CHANGE_NODATA=0x004;
	private static final int  BIND_SUCCEED=0x005;


	private String    bindcarnumStr,bindcarnextmileStr,bindcarcurrentmileStr,bindcarnexttimeStr,bindcarnexttime2Str,bindcarnum1Str,bindcarnum2Str;
	private TextView  bindcarnumber,bindcar_submit,bindcarnexttime,bindcarnexttime2,bindcarnum1,bindcarnum2;
	private View back;

	private VehicleInfo vehicle;
	private Handler handler;
	private EditText  bindcarnum,bindcarmilenow,bindcarnextmile;
	private TextView scanTextView; 
	private ProgressBar  progress;
	private View progressBar;

	//private String shop2DCodeStr="";       //店铺二维码
	private String shop2DCodeId="";              //店铺id 
	private String carBrandId;              //品牌id
	private String carMoldId;               //车型id
	private String veId;
	private String ok="no";
	private String obdSNStr;            //当前车辆所安装的obd的唯一标识号
	private String obdVin = "NULL";
	//private boolean ifAddInterface = false;

	private boolean meterOk=true,meterOk2=true,timeOk=true,timeOk2=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changebindcars);
		//ifAddInterface = false;
		findViewById(R.id.back).setFocusableInTouchMode(true);
		progress=(ProgressBar) findViewById(R.id.shopdetilmappro);
		progressBar= findViewById(R.id.progressBar);
		veId = getIntent().getStringExtra("tonggou.veId");
		bindcarnumber=(TextView) findViewById(R.id.bindcar_number);
		bindcarnum=(EditText) findViewById(R.id.bindcar_num);
		bindcarmilenow=(EditText) findViewById(R.id.bindcarmilenow);
		bindcarnextmile=(EditText) findViewById(R.id.bindcarnextmile);
		bindcarnexttime=(TextView) findViewById(R.id.bindcarnexttime);
		bindcarnexttime2=(TextView) findViewById(R.id.bindcarnexttime2);
		bindcarnum.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					bindcarmilenow.requestFocus();	
				} 
				return false; 
			} 
		});
		bindcarmilenow.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					bindcarnextmile.requestFocus();	
				} 
				return false; 
			} 
		});

		bindcarnumber.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ChangeBindCarsActivity.this,ConnectOBDActivity.class);
				intent.putExtra("tonggou.connectobd.from","changecar");
				startActivityForResult(intent, 3041);
			}
		});

		bindcarnum.clearFocus();
		bindcarmilenow.clearFocus();



		bindcar_submit=(TextView) findViewById(R.id.bindcar_submit);
		bindcar_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bindcarnumStr=bindcarnum.getText().toString().toUpperCase();
				bindcarnum1Str=bindcarnum1.getText().toString();
				bindcarnum2Str=bindcarnum2.getText().toString();

				bindcarnextmileStr=bindcarnextmile.getText().toString();
				bindcarcurrentmileStr=bindcarmilenow.getText().toString();
				bindcarnexttimeStr=bindcarnexttime.getText().toString();
				bindcarnexttime2Str=bindcarnexttime2.getText().toString();
				Time localTime = new Time("Asia/Hong_Kong");  
				localTime.setToNow();  
				long nextInsuranceTimeLong = SomeUtil.StringDateToLong(bindcarnexttimeStr);  //保险时间
				long nowTime = SomeUtil.StringDateToLong(localTime.format("%Y-%m-%d %H:%M").toString());  //保险时间
				long nextExamineTimeLong = SomeUtil.StringDateToLong(bindcarnexttime2Str);    //验车时间

				if(carBrandId==null||"".equals(carBrandId)){
					Toast.makeText(ChangeBindCarsActivity.this, "请选择车辆品牌", Toast.LENGTH_SHORT).show();
					return;
				}
				if(carMoldId==null||"".equals(carMoldId)){
					Toast.makeText(ChangeBindCarsActivity.this, "请选择车型", Toast.LENGTH_SHORT).show();
					return;
				}



				if(bindcarcurrentmileStr!=null&&!"".equals(bindcarcurrentmileStr)){
					try{
						Integer.parseInt(bindcarcurrentmileStr);
						meterOk=true;
					}catch(Exception  e){
						meterOk=false;
						Toast.makeText(ChangeBindCarsActivity.this, "当前里程输入不正确", Toast.LENGTH_SHORT).show();
					}
				}else{
					meterOk=true;
				}

				if(meterOk){	
					if(bindcarnextmileStr!=null&&!"".equals(bindcarnextmileStr)){
						try{
							Integer.parseInt(bindcarnextmileStr);
							meterOk2=true;
						}catch(Exception  e){
							meterOk2=false;
							Toast.makeText(ChangeBindCarsActivity.this, "下次保养里程输入不正确", Toast.LENGTH_SHORT).show();
						}
					}else{
						meterOk2=true;
					}
				}
				if(meterOk&&meterOk2){
					if(bindcarnexttimeStr!=null&&!"".equals(bindcarnexttimeStr)){
						if(nextInsuranceTimeLong>=nowTime){	
							timeOk=true;
						}else{
							timeOk=false;
							Toast.makeText(ChangeBindCarsActivity.this, "下次保险的时间不能早于当前时间", Toast.LENGTH_SHORT).show();
						}

					}else{
						timeOk=true;
					}

				}
				if(timeOk&&meterOk&&meterOk2){

					if(bindcarnexttime2Str!=null&&!"".equals(bindcarnexttime2Str)){
						if(nextExamineTimeLong>=nowTime){	
							timeOk2=true;
						}else{
							timeOk2=false;
							Toast.makeText(ChangeBindCarsActivity.this, "下次验车的时间不能早于当前时间", Toast.LENGTH_SHORT).show();
						}
					}else{
						timeOk2=true;
					}

				}


				if(meterOk&&meterOk2&&timeOk&&timeOk2){					
					progressBar.setVisibility(View.VISIBLE);
					new Thread(){
						public void run(){
							if(obdSNStr!=null&&!"".equals(obdSNStr)&&!"null".equals(obdSNStr)){
								//停掉连接obd
								Intent intentS = new Intent();
								intentS.setAction(TongGouService.TONGGOU_ACTION_START);
								intentS.putExtra("com.tonggou.server","STOP");
								sendBroadcast(intentS);

								bindObd(veId,obdVin,bindcarnumStr,bindcarnum1Str,carMoldId,bindcarnum2Str,carBrandId,obdSNStr,sharedPreferences.getString(BaseActivity.NAME, ""));
							}else{
								networkingChange(veId,vehicle.getVehicleVin(),bindcarnumStr,bindcarnum1Str,carMoldId,bindcarnum2Str,carBrandId,obdSNStr,currentUserId);
							}
						}
					}.start();

				}
			}
		});
		back=findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ChangeBindCarsActivity.this.finish();

				//连接obd
				BaseConnectOBDService.addingCar = false;
				Intent intent = new Intent();//创建Intent对象
				intent.setAction(TongGouService.TONGGOU_ACTION_START);
				intent.putExtra("com.tonggou.server","SCAN_OBD");
				sendBroadcast(intent);//发送广播
				
			}
		});
		bindcarnexttime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder  	builder = new AlertDialog.Builder(ChangeBindCarsActivity.this);
				View view = LayoutInflater.from(ChangeBindCarsActivity.this).inflate(R.layout.date_time_dialog, null);
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
						.append(":");//.append(timePicker.getCurrentMinute());
						if(timePicker.getCurrentMinute()<10){
							sb.append("0");
						}
						sb.append(timePicker.getCurrentMinute());



						bindcarnexttime.setText(sb);
						// etEndTime.requestFocus();

						dialog.cancel();
					}
				});
				Dialog dialog = builder.create();
				dialog.show();
			}
		});
		bindcarnexttime2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder  	builder = new AlertDialog.Builder(ChangeBindCarsActivity.this);
				View view = LayoutInflater.from(ChangeBindCarsActivity.this).inflate(R.layout.date_time_dialog, null);
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


						bindcarnexttime2.setText(sb);

						dialog.cancel();
					}
				});
				Dialog dialog = builder.create();
				dialog.show();
			}
		});
		bindcarnum2=(TextView) findViewById(R.id.bindcarnum2);
		bindcarnum2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ChangeBindCarsActivity.this,AppointmentNetWorkSearch.class);
				intent.putExtra("tonggou.from", "pinpai");
				intent.putExtra("tonggou.pinpai", "");
				startActivityForResult(intent, 1010);
			}
		});
		bindcarnum1=(TextView) findViewById(R.id.bindcarnum1);
		bindcarnum1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(carBrandId!=null&&!"".equals(carBrandId)){
					Intent intent = new Intent(ChangeBindCarsActivity.this,AppointmentNetWorkSearch.class);
					intent.putExtra("tonggou.from", "chexing");
					intent.putExtra("tonggou.pinpai",carBrandId);
					startActivityForResult(intent, 2020);
				}else{
					Toast.makeText(ChangeBindCarsActivity.this,getString(R.string.brand_first),Toast.LENGTH_SHORT).show();

				}
			}
		});
		scanTextView = (TextView)findViewById(R.id.registerscanshop);
		scanTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ChangeBindCarsActivity.this,CaptureActivity.class);
				startActivityForResult(intent, 4040);
			}
		});
		new Thread(){
			public void run(){
				networking(veId);
			}
		}.start();
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				switch(msg.what){
				case NETWORK_FAILD: 
					progress.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
					Toast.makeText(ChangeBindCarsActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case NETWORK_SUCCEED:
					progress.setVisibility(View.GONE);
					carBrandId=vehicle.getVehicleBrandId();
					carMoldId=vehicle.getVehicleModelId();
					if(vehicle.getObdSN()!=null&&!"null".equals(vehicle.getObdSN())){
						bindcarnumber.setText(vehicle.getObdSN()+""); //设备号 
						obdVin = vehicle.getVehicleVin();
						veId = vehicle.getVehicleId();
						//ifAddInterface = true;
					}else{
						bindcarnumber.setText("+ 添加设备"); 

					}
					obdSNStr = vehicle.getObdSN();
					bindcarnum.setText(vehicle.getVehicleNo()+"");
					if(vehicle.getVehicleBrand()!=null&&!"".equals(vehicle.getVehicleBrand())&&!"null".equals(vehicle.getVehicleBrand())){
						bindcarnum2.setText(vehicle.getVehicleBrand()+"");
					}
					if(vehicle.getVehicleModel()!=null&&!"".equals(vehicle.getVehicleModel())&&!"null".equals(vehicle.getVehicleModel())){
						bindcarnum1.setText(vehicle.getVehicleModel()+"");
					}
					if(vehicle.getCurrentMileage()!=null&&!"".equals(vehicle.getCurrentMileage())&&!"null".equals(vehicle.getCurrentMileage())){

						if(vehicle.getCurrentMileage().indexOf(".")!=-1){
							String cm = vehicle.getCurrentMileage().substring(0, vehicle.getCurrentMileage().indexOf("."));
							bindcarmilenow.setText(cm);
						}else{
							bindcarmilenow.setText(vehicle.getCurrentMileage()+"");
						}


					}if(vehicle.getNextMaintainMileage()!=null&&!"".equals(vehicle.getNextMaintainMileage())&&!"null".equals(vehicle.getNextMaintainMileage())){
						if(vehicle.getNextMaintainMileage().indexOf(".")!=-1){
							String nm = vehicle.getNextMaintainMileage().substring(0, vehicle.getNextMaintainMileage().indexOf("."));
							bindcarnextmile.setText(nm);
						}else{
							bindcarnextmile.setText(vehicle.getNextMaintainMileage()+"");
						}

					}if(vehicle.getNextExamineTime()!=null&&!"".equals(vehicle.getNextExamineTime())&&!"null".equals(vehicle.getNextExamineTime())&&!"0".equals(vehicle.getNextExamineTime())){
						bindcarnexttime2.setText(SomeUtil.longToStringDate(vehicle.getNextExamineTime()+""));
					}if(vehicle.getNextInsuranceTime()!=null&&!"".equals(vehicle.getNextInsuranceTime())&&!"null".equals(vehicle.getNextInsuranceTime())&&!"0".equals(vehicle.getNextInsuranceTime())){
						bindcarnexttime.setText(SomeUtil.longToStringDate(vehicle.getNextInsuranceTime()+""));
					}
					if(vehicle.getRecommendShopName()!=null){
						scanTextView.setText(vehicle.getRecommendShopName());
					}
					break;
				case NETWORK_NODATA: 
					progress.setVisibility(View.GONE);							
					Toast.makeText(ChangeBindCarsActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

					break;
				case CHANGE_SUCCEED: 
					ok="yes";	
					progressBar.setVisibility(View.GONE);
					Toast.makeText(ChangeBindCarsActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();					

					Intent dataIntent = new Intent();
					dataIntent.putExtra("tonggou.isOk",ok);
					setResult(5050, dataIntent);
					ChangeBindCarsActivity.this.finish();
					break;
				case BIND_SUCCEED: 
					ok="yes";	
					BaseConnectOBDService.cmile = bindcarmilenow.getText().toString();
					progressBar.setVisibility(View.GONE);
					Toast.makeText(ChangeBindCarsActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();					
					Intent backIntent = new Intent();
					backIntent.putExtra("tonggou.isOk",ok);
					setResult(5050, backIntent);
					ChangeBindCarsActivity.this.finish();
					
					//连接obd
					Intent intent = new Intent();//创建Intent对象
					intent.setAction(TongGouService.TONGGOU_ACTION_START);
					intent.putExtra("com.tonggou.server","SCAN_OBD");
					sendBroadcast(intent);//发送广播
					BaseConnectOBDService.addingCar = false;
					break;

				case  CHANGE_NODATA: 	
					progressBar.setVisibility(View.GONE);							
					Toast.makeText(ChangeBindCarsActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

					break;
				}
			}
		};
		
		
		
		//停掉连接obd
		BaseConnectOBDService.addingCar = true;
		Intent intentS = new Intent();
		intentS.setAction(TongGouService.TONGGOU_ACTION_START);
		intentS.putExtra("com.tonggou.server","STOP");
		sendBroadcast(intentS);
		
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
		ChangeBindCarsActivity.this.finish();
		//连接obd
		BaseConnectOBDService.addingCar = false;
		Intent intent = new Intent();//创建Intent对象
		intent.setAction(TongGouService.TONGGOU_ACTION_START);
		intent.putExtra("com.tonggou.server","SCAN_OBD");
		sendBroadcast(intent);//发送广播
	
	}

	private  void networking(String vehicleId){
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/singleVehicle/vehicleId/"+vehicleId;
		VehicleParser vehicleParser = new VehicleParser();		
		NetworkState ns = Network.getNetwork(ChangeBindCarsActivity.this).httpGetUpdateString(url,vehicleParser);	

		if(ns.isNetworkSuccess()){
			if(vehicleParser.isSuccessfull()){
				vehicle=vehicleParser.getVehicleResponse().getVehicleInfo();
				if(vehicle!=null){
					sendMessage(NETWORK_SUCCEED, vehicleParser.getVehicleResponse().getMessage());
				}else{
					sendMessage(NETWORK_NODATA,vehicleParser.getVehicleResponse().getMessage());
				}

			}else{
				//解析出错
				sendMessage(NETWORK_NODATA, vehicleParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
		}
	}
	
	
	private void networkingChange(String vehicleId,String vehicleVin,String vehicleNo,String vehicleModel,
			String vehicleModelId,String vehicleBrand,String vehicleBrandId,String obdSN,String userNo){

		long nextInsuranceTimeLong = SomeUtil.StringDateToLong(bindcarnexttime.getText().toString());  //保险时间
		long nextExamineTimeLong = SomeUtil.StringDateToLong(bindcarnexttime2.getText().toString());    //验车时间

		AddBindCarParser addBindCarParser = new AddBindCarParser();
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/vehicleInfo";
		String current="",nextmile="";
		if(bindcarcurrentmileStr!=null&&!"".equals(bindcarcurrentmileStr)){
			current=",\"currentMileage\":\""+bindcarcurrentmileStr+"\"" ;
		}
		if(bindcarnextmileStr!=null&&!"".equals(bindcarnextmileStr)){
			nextmile=",\"nextMaintainMileage\":\""+bindcarnextmileStr+"\"" ;
		}

		String valuePairs ="{\"vehicleId\":\""+vehicleId+"\", " +" \"vehicleVin\":\""+vehicleVin+"\", \"vehicleNo\":\""+vehicleNo.trim()+"\"," +
				" \"vehicleModel\":\""+vehicleModel+"\",\"vehicleModelId\":\""+vehicleModelId+"\"" +
				",\"vehicleBrand\":\""+vehicleBrand+"\"" +
				",\"vehicleBrandId\":\""+vehicleBrandId+"\"" +
				",\"nextInsuranceTime\":\""+nextInsuranceTimeLong+"\"" +
				",\"nextExamineTime\":\""+nextExamineTimeLong+"\"" +current+nextmile+
				",\"obdSN\":\""+obdSN+"\"" +
				",\"userNo\":\""+userNo+"\"}";
		NetworkState ns = Network.getNetwork(ChangeBindCarsActivity.this).httpPutUpdateString(url,valuePairs.getBytes(),addBindCarParser);
		if(ns.isNetworkSuccess()){
			if(addBindCarParser.isSuccessfull()){
				//正确的处理逻辑 
				String mes = addBindCarParser.getAddBindCarResponse().getMessage();
				UpdateFaultDic.getUpdateFaultDic(ChangeBindCarsActivity.this).updateFaultDic(vehicleModelId);
				sendMessage(CHANGE_SUCCEED, mes);
			}else{
				//提示用户错误
				String errorAlert = addBindCarParser.getErrorMessage();
				sendMessage(CHANGE_NODATA, errorAlert);
			}
		}else{
			//网络错误
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
	private void setDateTime(){
		Time localTime = new Time("Asia/Hong_Kong");  
		localTime.setToNow();  
		bindcarnexttime.setText(localTime.format("%Y-%m-%d %H:%M")); 
	}
	private void setDateTime2(){
		Time localTime = new Time("Asia/Hong_Kong");  
		localTime.setToNow();  
		bindcarnexttime2.setText(localTime.format("%Y-%m-%d %H:%M")); 
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==1010){		
			if(bindcarnum2.getText().toString()!=null&&!"".equals(bindcarnum2.getText().toString())){
				if(!data.getStringExtra("pinpai").equals(bindcarnum2.getText().toString())){

					bindcarnum1.setText("");	
					carMoldId=null;
				}
			}
			bindcarnum2.setText(data.getStringExtra("pinpai"));
			carBrandId=data.getStringExtra("pinpaiId");

		}
		if(resultCode==2020){		
			bindcarnum1.setText(data.getStringExtra("chexing"));	
			carMoldId=data.getStringExtra("chexingId");

		}
		if(resultCode==4040){
			String s2dCode = data.getStringExtra("iconNames");			
			if(s2dCode!=null&&!"".equals(s2dCode)){
				scanTextView.setText(s2dCode);
				//shop2DCodeStr = s2dCode;
			}
			String s2dCodeId = data.getStringExtra("iconID");
			if(s2dCodeId!=null&&!"".equals(s2dCodeId)){
				shop2DCodeId = s2dCodeId;
			}
		}
		if(resultCode==3041){			
			String vin =data.getStringExtra("tonggou.get.vin");
			String obd_SN =data.getStringExtra("tonggou.get.obdsn");
			if(vin!=null&&!"".equals(vin)){
				obdVin = vin;

			}
			if(obd_SN!=null&&!"".equals(obd_SN)){
				obdSNStr = obd_SN;
				bindcarnumber.setText(obdSNStr);
				//ifAddInterface = true;
			}
		}
	}



	private void bindObd(String vehicleId,String vehicleVin,String vehicleNo,String vehicleModel,
			String vehicleModelId,String vehicleBrand,String vehicleBrandId,String obdSN,String userNo){
		long nextInsuranceTimeLong = SomeUtil.StringDateToLong(bindcarnexttime.getText().toString());  //保险时间
		long nextExamineTimeLong = SomeUtil.StringDateToLong(bindcarnexttime2.getText().toString());    //验车时间


		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/obd/binding";
		CommonParser commonParser = new CommonParser();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userNo",sharedPreferences.getString(BaseActivity.NAME, "")));	
		nameValuePairs.add(new BasicNameValuePair("obdSN",obdSN));
		nameValuePairs.add(new BasicNameValuePair("vehicleVin",vehicleVin));

		nameValuePairs.add(new BasicNameValuePair("vehicleId",vehicleId));

		nameValuePairs.add(new BasicNameValuePair("vehicleNo",vehicleNo.trim()));

		nameValuePairs.add(new BasicNameValuePair("vehicleModelId",vehicleModelId));
		nameValuePairs.add(new BasicNameValuePair("vehicleModel",vehicleModel));

		nameValuePairs.add(new BasicNameValuePair("vehicleBrandId",vehicleBrandId));
		nameValuePairs.add(new BasicNameValuePair("vehicleBrand",vehicleBrand));
		//		//可选

		if(shop2DCodeId!=null&&!"".equals(shop2DCodeId)){
			nameValuePairs.add(new BasicNameValuePair("sellShopId",shop2DCodeId));
		}


		if(bindcarcurrentmileStr!=null&&!"".equals(bindcarcurrentmileStr)){
			nameValuePairs.add(new BasicNameValuePair("currentMileage",bindcarcurrentmileStr));
		}

		if(bindcarnextmileStr!=null&&!"".equals(bindcarnextmileStr)){			
			nameValuePairs.add(new BasicNameValuePair("nextMaintainMileage",bindcarnextmileStr));
		}
		if(nextInsuranceTimeLong!=0){
			nameValuePairs.add(new BasicNameValuePair("nextInsuranceTime",nextInsuranceTimeLong+""));
		}
		if(nextExamineTimeLong!=0){
			nameValuePairs.add(new BasicNameValuePair("nextExamineTime",nextExamineTimeLong+""));
		}


		final NetworkState ns = Network.getNetwork(this).httpPostUpdateString(url,nameValuePairs,commonParser);	
		if(ns.isNetworkSuccess()){
			if(commonParser.isSuccessfull()){
				//正确的处理逻辑 
				getCarList();
				UpdateFaultDic.getUpdateFaultDic(ChangeBindCarsActivity.this).updateFaultDic(vehicleModelId);		
				String mes = commonParser.getCommonResponse().getMessage();
				sendMessage(BIND_SUCCEED, mes);
			}else{
				//提示用户错误
				String errorAlert = commonParser.getErrorMessage();
				sendMessage(CHANGE_NODATA, errorAlert);
			}
		}else{
			//网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());

		}
	}

	
	/**
	 * 更新车辆列表
	 */
	private void getCarList(){
		if(TongGouApplication.imageVersion==null||"".equals(TongGouApplication.imageVersion)){
			TongGouApplication.imageVersion = sharedPreferences.getString(BaseActivity.SCREEN, "480X800");
		}	
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/login";
		LoginParser loginParser = new LoginParser();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userNo",currentUserId));	
		nameValuePairs.add(new BasicNameValuePair("password",currentPassWd));
		nameValuePairs.add(new BasicNameValuePair("platform",INFO.CLIENT_PLATFORM));
		nameValuePairs.add(new BasicNameValuePair("appVersion",INFO.VERSION));
		//可选
		nameValuePairs.add(new BasicNameValuePair("platformVersion",TongGouApplication.platformVersion));
		nameValuePairs.add(new BasicNameValuePair("mobileModel",TongGouApplication.mobileModel));
		nameValuePairs.add(new BasicNameValuePair("imageVersion",TongGouApplication.imageVersion));


		NetworkState ns = Network.getNetwork(this).httpPostUpdateString(url,nameValuePairs,loginParser);	
		if(ns.isNetworkSuccess()){
			if(loginParser.isSuccessfull()){
				//保存数据
				TongGouApplication.obdLists = loginParser.getLoginResponse().getObdList();

			}
		}
	}
}
