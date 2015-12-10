package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.tonggou.andclient.vo.OBDDevice;
import com.tonggou.andclient.vo.VehicleInfo;

public class ChangeBindCarsActivity extends BaseActivity{

	private static final int  NETWORK_FAILD=-1;
	private static final int  NETWORK_SUCCEED=0x001;
	private static final int  NETWORK_NODATA=0x002;
	private static final int  CHANGE_SUCCEED=0x003;
	private static final int  CHANGE_NODATA=0x004;
	private static final int  BIND_SUCCEED=0x005;

	private int flag=0;//用于隐藏与更多的切换
	private String    bindcarnumStr,bindcarnextmileStr,bindcarcurrentmileStr,bindcarnexttimeStr,bindcarnexttime2Str,
	bindcarnum1Str,bindcarnum2Str,bindcarvehiclevinStr,bindcarenginenoStr,bindcarregistnoStr;
	private TextView  bindcarnumber,bindcar_submit,bindcarnexttime,bindcarnexttime2,bindcarnum1,bindcarnum2;
	private View back,more,moremessage;

	private VehicleInfo mVehicleInfo;
	private Handler handler;
	private EditText  bindcarnum,bindcarmilenow,bindcarnextmile;
	private EditText bindcarvehiclevin,bindcarengineno,bindcarregistno;
	private TextView scanTextView,more1; 
	private Button more2;
	private ProgressBar  progress;
	private View progressBar;

	//private String shop2DCodeStr="";       //店铺二维码
	private String shop2DCodeId="";              //店铺id 
	private String carBrandId;              //品牌id
	private String carMoldId;               //车型id
	private String veId;
	private String ok="no";
	private String obdSNStr;            //当前车辆所安装的obd的唯一标识号
	//private String obdVin = "NULL";
	private String obdVin;
	//private boolean ifAddInterface = false;

	private boolean meterOk=true,meterOk2=true,timeOk=true,timeOk2=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changebindcars);

		//	ifAddInterface = false;
		findViewById(R.id.back).setFocusableInTouchMode(true);
		bindcarvehiclevin = (EditText) findViewById(R.id.bindcarvehiclevin);//车架号
		bindcarengineno = (EditText) findViewById(R.id.bindcarengineno);    //发动机号
		bindcarregistno = (EditText) findViewById(R.id.bindcarregistno);    //登记证书号
		progress=(ProgressBar) findViewById(R.id.shopdetilmappro);
		progressBar= findViewById(R.id.progressBar);
		veId = getIntent().getStringExtra("tonggou.veId");
		bindcarnumber=(TextView) findViewById(R.id.bindcar_number);
		bindcarnum=(EditText) findViewById(R.id.bindcar_num);
		bindcarmilenow=(EditText) findViewById(R.id.bindcarmilenow);
		bindcarnextmile=(EditText) findViewById(R.id.bindcarnextmile);
		bindcarnexttime=(TextView) findViewById(R.id.bindcarnexttime);
		bindcarnexttime2=(TextView) findViewById(R.id.bindcarnexttime2);
		
		more = findViewById(R.id.more);
		moremessage = findViewById(R.id.moremessage);
		more2 = (Button) findViewById(R.id.more2);
		
		bindcarnum.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					bindcarvehiclevin.requestFocus();	
				} 
				return false; 
			} 
		});

		bindcarvehiclevin.setOnKeyListener(new OnKeyListener() { 
			
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					bindcarengineno.requestFocus();	
				} 
				return false; 
			} 
		});
		bindcarengineno.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					bindcarregistno.requestFocus();	
				} 
				return false; 
			} 
		});
		bindcarregistno.setOnKeyListener(new OnKeyListener() { 
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

		more2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(flag == 0)  
				{  
					flag = flag+1;
					moremessage.setVisibility(View.VISIBLE);
					more2.setBackgroundResource(R.drawable.ic_hide);
					more.setBackgroundResource(R.drawable.registerback1);
					//more2.setText("收起");
				}else if(flag == 1)  
				{  
					flag = 0;  
					moremessage.setVisibility(View.GONE);
					more2.setBackgroundResource(R.drawable.ic_more);
					more.setBackgroundResource(R.color.bai);
					//more2.setText("更多");
				}
			}
		});
		
		bindcarnumber.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ChangeBindCarsActivity.this,ConnectOBDDialogActivity.class);
				intent.putExtra("tonggou.connectobd.from","changecar");
				startActivityForResult(intent, 3041);
			}
		});

		bindcarnum.clearFocus();
		bindcarvehiclevin.clearFocus();
		bindcarengineno.clearFocus();
		bindcarregistno.clearFocus();
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
				
				obdVin=bindcarvehiclevin.getText().toString();
				//bindcarvehiclevinStr=bindcarvehiclevin.getText().toString();
				bindcarenginenoStr=bindcarengineno.getText().toString();
				bindcarregistnoStr=bindcarregistno.getText().toString();

				Time localTime = new Time("Asia/Hong_Kong");  
				localTime.setToNow();  
				long nextInsuranceTimeLong = SomeUtil.StringDateToLong(bindcarnexttimeStr);  //保险时间
				long nowTime = SomeUtil.StringDateToLong(localTime.format("%Y-%m-%d %H:%M").toString());  //保险时间
				long nextExamineTimeLong = SomeUtil.StringDateToLong(bindcarnexttime2Str);    //验车时间

				if(bindcarnum1Str==null||"".equals(bindcarnum1Str)){
					Toast.makeText(ChangeBindCarsActivity.this, "请选择车辆品牌", Toast.LENGTH_SHORT).show();
					return;
				}
				if(bindcarnum2Str==null||"".equals(bindcarnum2Str)){
					Toast.makeText(ChangeBindCarsActivity.this, "请选择车型", Toast.LENGTH_SHORT).show();
					return;
				}

				if(obdVin!=null&&!"".equals(obdVin)){
					try {
						Integer.parseInt(obdVin);
						meterOk=true;
					} catch (Exception e) {
						meterOk=false;
					}
				}else {
					meterOk=true;
				}
				if( !TextUtils.isEmpty(bindcarenginenoStr)){
					try {
						Integer.parseInt(bindcarenginenoStr);
						meterOk=true;
					} catch (Exception e) {
						meterOk=false;
					}
				}else {
					meterOk=true;
				}
				if(bindcarregistnoStr!=null&&!"".equals(bindcarregistnoStr)){
					try {
						Integer.parseInt(bindcarregistnoStr);
						meterOk=true;
					} catch (Exception e) {
						meterOk=false;
					}
				}else {
					meterOk=true;
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
								networkingChange(veId,mVehicleInfo.getVehicleVin(),bindcarnumStr,bindcarnum1Str,carMoldId,bindcarnum2Str,carBrandId,obdSNStr,currentUserId);
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

		handler = new Handler(){
			@SuppressLint("HandlerLeak")
			@Override
			public void handleMessage(Message msg){		
				TongGouApplication.showLog( "  #### -- " + msg.obj + "@@@@@@@@  " + msg.what);
				switch(msg.what){
				case NETWORK_FAILD: 
					progress.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
					Toast.makeText(ChangeBindCarsActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case NETWORK_SUCCEED:
					setVehicleInfoValue();
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
						
					TongGouApplication.getInstance().queryVehicleList();
					BaseConnectOBDService.addingCar = false;
					break;

				case  CHANGE_NODATA: 	
					progressBar.setVisibility(View.GONE);							
					Toast.makeText(ChangeBindCarsActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

					break;
				}
			}


		};

	//	if( mVehicleInfo != null ) {
	//		setVehicleInfoValue();
	//	} else {
			new Thread(){
				public void run(){
					networking(veId);
				}
			}.start();
	//	}

		//停掉连接obd
		BaseConnectOBDService.addingCar = true;
		Intent intentS = new Intent();
		intentS.setAction(TongGouService.TONGGOU_ACTION_START);
		intentS.putExtra("com.tonggou.server","STOP");
		sendBroadcast(intentS);

	}

	private void setVehicleInfoValue() {
		progress.setVisibility(View.GONE);
		carBrandId=mVehicleInfo.getVehicleBrandId();
		carMoldId=mVehicleInfo.getVehicleModelId();
		if(mVehicleInfo.getObdSN()!=null&&!"null".equals(mVehicleInfo.getObdSN())){
			bindcarnumber.setText(mVehicleInfo.getObdSN()+""); //设备号 
		    bindcarvehiclevin.setText(mVehicleInfo.getVehicleVin()+"");//车架号
		 	obdVin = mVehicleInfo.getVehicleVin();
			veId = mVehicleInfo.getVehicleId();
			//ifAddInterface = true;
		}else{
			bindcarnumber.setHint("+ 添加设备"); 
		}
		obdSNStr = mVehicleInfo.getObdSN();
		bindcarnum.setText(mVehicleInfo.getVehicleNo()+"");
	
		if(mVehicleInfo.getVehicleBrand()!=null&&!"".equals(mVehicleInfo.getVehicleBrand())&&!"null".equals(mVehicleInfo.getVehicleBrand())){
			bindcarnum2.setText(mVehicleInfo.getVehicleBrand()+"");
		}
		if(mVehicleInfo.getVehicleModel()!=null&&!"".equals(mVehicleInfo.getVehicleModel())&&!"null".equals(mVehicleInfo.getVehicleModel())){
			bindcarnum1.setText(mVehicleInfo.getVehicleModel()+"");
		}
		if(mVehicleInfo.getCurrentMileage()!=null&&!"".equals(mVehicleInfo.getCurrentMileage())&&!"null".equals(mVehicleInfo.getCurrentMileage())){

			if(mVehicleInfo.getCurrentMileage().indexOf(".")!=-1){
				String cm = mVehicleInfo.getCurrentMileage().substring(0, mVehicleInfo.getCurrentMileage().indexOf("."));
				bindcarmilenow.setText(cm);
			}else{
				bindcarmilenow.setText(mVehicleInfo.getCurrentMileage()+"");
			}


		}if(mVehicleInfo.getNextMaintainMileage()!=null&&!"".equals(mVehicleInfo.getNextMaintainMileage())&&!"null".equals(mVehicleInfo.getNextMaintainMileage())){
			if(mVehicleInfo.getNextMaintainMileage().indexOf(".")!=-1){
				String nm = mVehicleInfo.getNextMaintainMileage().substring(0, mVehicleInfo.getNextMaintainMileage().indexOf("."));
				bindcarnextmile.setText(nm);
			}else{
				bindcarnextmile.setText(mVehicleInfo.getNextMaintainMileage()+"");
			}

		}if(mVehicleInfo.getNextExamineTime()!=null&&!"".equals(mVehicleInfo.getNextExamineTime())&&!"null".equals(mVehicleInfo.getNextExamineTime())&&!"0".equals(mVehicleInfo.getNextExamineTime())){
			bindcarnexttime2.setText(SomeUtil.longToStringDate(mVehicleInfo.getNextExamineTime()+""));
		}if(mVehicleInfo.getNextInsuranceTime()!=null&&!"".equals(mVehicleInfo.getNextInsuranceTime())&&!"null".equals(mVehicleInfo.getNextInsuranceTime())&&!"0".equals(mVehicleInfo.getNextInsuranceTime())){
			bindcarnexttime.setText(SomeUtil.longToStringDate(mVehicleInfo.getNextInsuranceTime()+""));
		}
		if(mVehicleInfo.getRecommendShopName()!=null){
			scanTextView.setText(mVehicleInfo.getRecommendShopName());
		}

		if(mVehicleInfo.getVehicleVin()!=null&&!"".equals(mVehicleInfo.getVehicleVin())&&!"null".equals(mVehicleInfo.getVehicleVin())){
			bindcarvehiclevin.setText(mVehicleInfo.getVehicleVin()+"");
		}
		if(mVehicleInfo.getEngineNo()!=null&&!"".equals(mVehicleInfo.getEngineNo())&&!"null".equals(mVehicleInfo.getEngineNo())){
			TongGouApplication.showLog("mVehicleInfo.getEngineNo()  +  " + mVehicleInfo.getEngineNo()+"");
			bindcarengineno.setText(mVehicleInfo.getEngineNo()+"");

		}if(mVehicleInfo.getRegistNo()!=null&&!"".equals(mVehicleInfo.getRegistNo())&&!"null".equals(mVehicleInfo.getRegistNo())){
			bindcarregistno.setText(mVehicleInfo.getRegistNo()+"");
		}	


/*			mEngineNo = mVehicleInfo.getEngineNo();
		if( TextUtils.isEmpty( mEngineNo ) && !"null".equals(mEngineNo)  ) {
			bindcarvehiclevin.setText(mEngineNo);
		}

				obdVin = mVehicleInfo.getVehicleVin();
		if( TextUtils.isEmpty( obdVin ) && !"null".equalsIgnoreCase(obdVin)  ) {
			bindcarengineno.setText(obdVin);
		}

		mRegistNo = mVehicleInfo.getRegistNo();
		if( TextUtils.isEmpty( mRegistNo ) && !"null".equals(mRegistNo)  ) {
			bindcarregistno.setText(mRegistNo);
		}*/
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
				mVehicleInfo=vehicleParser.getVehicleResponse().getVehicleInfo();
				if(mVehicleInfo!=null){
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
		String current="",nextmile="",vehiclevin="",engineno="",registno="";
		if(obdVin!=null&&!"".equals(obdVin)){
			vehiclevin=",\"vehicleVin\":\""+obdVin+"\"" ;
		}
		if(bindcarenginenoStr!=null&&!"".equals(bindcarenginenoStr)){
			engineno=",\"engineNo\":\""+bindcarenginenoStr+"\"" ;
		}
		if(bindcarregistnoStr!=null&&!"".equals(bindcarregistnoStr)){
			registno=",\"registNo\":\""+bindcarregistnoStr+"\"" ;
		}
		if(bindcarcurrentmileStr!=null&&!"".equals(bindcarcurrentmileStr)){
			current=",\"currentMileage\":\""+bindcarcurrentmileStr+"\"" ;
		}
		if(bindcarnextmileStr!=null&&!"".equals(bindcarnextmileStr)){
			nextmile=",\"nextMaintainMileage\":\""+bindcarnextmileStr+"\"" ;
		}

		String valuePairs ="{\"vehicleId\":\""+vehicleId+"\", " + "\"vehicleNo\":\""+vehicleNo.trim()+"\"," +
				" \"vehicleModel\":\""+vehicleModel + "\"";
				if( !TextUtils.isEmpty( vehicleBrandId )  ) {
					valuePairs += ",\"vehicleModelId\":"+vehicleModelId;
				}
				if( !TextUtils.isEmpty( vehicleBrandId  ) ) {
					valuePairs += ",\"vehicleBrandId\":"+vehicleBrandId;
				}
				valuePairs += ",\"vehicleBrand\":\""+vehicleBrand+"\"" +
				",\"nextInsuranceTime\":"+nextInsuranceTimeLong +
				",\"nextExamineTime\":"+nextExamineTimeLong
				+ current 
				+ nextmile
				+ vehiclevin
				+ engineno
				+ registno +
				",\"obdSN\":\""+obdSN+"\"" +
				",\"userNo\":\""+userNo+"\"}";
		
		TongGouApplication.showLog( valuePairs );
		
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
		if(requestCode == 3041 && resultCode == RESULT_OK){
			if( data == null ) {
				return;
			}
			String vin =data.getStringExtra(ConnectOBDDialogActivity.EXTRA_VEHICLE_VIN);
			OBDDevice obdDevice = (OBDDevice) data.getSerializableExtra(ConnectOBDDialogActivity.EXTRA_OBD_DEVICE);
			String obd_SN = obdDevice.getDeviceAddress();
			if(vin!=null&&!"".equals(vin)){
				obdVin = vin;
		        bindcarvehiclevin.setText(obdVin);
			}
			if(obd_SN!=null&&!"".equals(obd_SN)){
				obdSNStr = obd_SN;
				bindcarnumber.setText(obdSNStr);
				//	ifAddInterface = true;
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
		if(obdVin!=null&&!"".equals(obdVin)){
			//nameValuePairs.add(new BasicNameValuePair("vehicleVin",obdVin));
			nameValuePairs.contains(new BasicNameValuePair("vehicleVin",obdVin));
		}
		if(bindcarenginenoStr!=null&&!"".equals(bindcarenginenoStr)){
			nameValuePairs.add(new BasicNameValuePair("engineNo",bindcarenginenoStr));
		}

		if(bindcarregistnoStr!=null&&!"".equals(bindcarregistnoStr)){
			nameValuePairs.add(new BasicNameValuePair("registNo",bindcarregistnoStr));
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

		for( NameValuePair p : nameValuePairs ) {
			TongGouApplication.showLog( p.getValue() + "  " + p.getName() );
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
