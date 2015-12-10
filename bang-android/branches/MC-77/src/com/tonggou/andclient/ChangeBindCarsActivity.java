package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tonggou.andclient.app.BaseConnectOBDService;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.app.UpdateFaultDic;
import com.tonggou.andclient.guest.GuestVehicleManager;
import com.tonggou.andclient.jsonresponse.AddBindCarResponse;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.network.parser.AsyncJSONResponseParseHandler;
import com.tonggou.andclient.network.request.StoreVehicleInfoRequest;
import com.tonggou.andclient.parse.CommonParser;
import com.tonggou.andclient.parse.VehicleParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.VehicleInfo;

public class ChangeBindCarsActivity extends BaseActivity {

	private static final int NETWORK_FAILD = -1;
	private static final int NETWORK_SUCCEED = 0x001;
	private static final int NETWORK_NODATA = 0x002;
	private static final int CHANGE_SUCCEED = 0x003;
	private static final int CHANGE_NODATA = 0x004;
	private static final int BIND_SUCCEED = 0x005;

	private String vehicleNoStr, bindcarnextmileStr, bindcarcurrentmileStr, bindcarnexttimeStr,
			bindcarnexttime2Str, vehicleModelStr, vehicleBrandStr, vehicleEnginNoStr,
			vehicleRegistNoStr;
	private TextView bindcar_submit, bindcarnexttime, bindcarnexttime2, bindcarnum1, bindcarnum2,
			mTxtOBDBinded, mTxtShopBinded;
	private View back;

	private VehicleInfo mVehicleInfo;
	private Handler handler;
	private EditText bindcarnum, bindcarmilenow, bindcarnextmile;
	private EditText bindcarvehiclevin, bindcarengineno, bindcarregistno;
	private ProgressBar progress;

	// private String shop2DCodeStr=""; //店铺二维码
	private String shop2DCodeId = ""; // 店铺id
	private String vehicleBrandIdStr; // 品牌id
	private String vehicleModelIdStr; // 车型id
	private String vehicleIdStr;
	private String ok = "no";
	private String obdSNStr; // 当前车辆所安装的obd的唯一标识号
	// private String obdVin = "NULL";
	private String vehicleVinStr;

	private boolean meterOk = true, meterOk2 = true, timeOk = true, timeOk2 = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changebindcars);

		// ifAddInterface = false;
		findViewById(R.id.back).setFocusableInTouchMode(true);
		bindcarvehiclevin = (EditText) findViewById(R.id.bindcarvehiclevin);// 车架号
		bindcarengineno = (EditText) findViewById(R.id.bindcarengineno); // 发动机号
		bindcarregistno = (EditText) findViewById(R.id.bindcarregistno); // 登记证书号
		progress = (ProgressBar) findViewById(R.id.shopdetilmappro);
		progress.setVisibility(View.GONE);
		vehicleIdStr = getIntent().getStringExtra("tonggou.veId");
		bindcarnum = (EditText) findViewById(R.id.bindcar_num);
		bindcarmilenow = (EditText) findViewById(R.id.bindcarmilenow);
		bindcarnextmile = (EditText) findViewById(R.id.bindcarnextmile);
		bindcarnexttime = (TextView) findViewById(R.id.bindcarnexttime);
		bindcarnexttime2 = (TextView) findViewById(R.id.bindcarnexttime2);
		mTxtOBDBinded = (TextView) findViewById(R.id.txt_Change_bindcars_OBDBinded);
		mTxtShopBinded = (TextView) findViewById(R.id.txt_Change_bindcars_ShopBinded);

		bindcarnum.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66 && event.getAction() == KeyEvent.ACTION_UP) {
					bindcarvehiclevin.requestFocus();
				}
				return false;
			}
		});

		bindcarvehiclevin.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66 && event.getAction() == KeyEvent.ACTION_UP) {
					bindcarengineno.requestFocus();
				}
				return false;
			}
		});
		bindcarengineno.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66 && event.getAction() == KeyEvent.ACTION_UP) {
					bindcarregistno.requestFocus();
				}
				return false;
			}
		});
		bindcarregistno.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66 && event.getAction() == KeyEvent.ACTION_UP) {
					bindcarmilenow.requestFocus();
				}
				return false;
			}
		});

		bindcarmilenow.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66 && event.getAction() == KeyEvent.ACTION_UP) {
					bindcarnextmile.requestFocus();
				}
				return false;
			}
		});

		bindcarnum.clearFocus();
		bindcarvehiclevin.clearFocus();
		bindcarengineno.clearFocus();
		bindcarregistno.clearFocus();
		bindcarmilenow.clearFocus();

		bindcar_submit = (TextView) findViewById(R.id.bindcar_submit);
		bindcar_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vehicleNoStr = bindcarnum.getText().toString().toUpperCase();
				vehicleModelStr = bindcarnum1.getText().toString();
				vehicleBrandStr = bindcarnum2.getText().toString();

				bindcarnextmileStr = bindcarnextmile.getText().toString();
				bindcarcurrentmileStr = bindcarmilenow.getText().toString();
				bindcarnexttimeStr = bindcarnexttime.getText().toString();
				bindcarnexttime2Str = bindcarnexttime2.getText().toString();

				vehicleVinStr = bindcarvehiclevin.getText().toString();
				vehicleEnginNoStr = bindcarengineno.getText().toString();
				vehicleRegistNoStr = bindcarregistno.getText().toString();

				Time localTime = new Time("Asia/Hong_Kong");
				localTime.setToNow();
				long nextInsuranceTimeLong = SomeUtil.StringDateToLong(bindcarnexttimeStr); // 保险时间
				long nowTime = SomeUtil.StringDateToLong(localTime.format("%Y-%m-%d %H:%M")
						.toString()); // 保险时间
				long nextExamineTimeLong = SomeUtil.StringDateToLong(bindcarnexttime2Str); // 验车时间

				if (vehicleModelStr == null || "".equals(vehicleModelStr)) {
					Toast.makeText(ChangeBindCarsActivity.this, "请选择车辆品牌", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (vehicleBrandStr == null || "".equals(vehicleBrandStr)) {
					Toast.makeText(ChangeBindCarsActivity.this, "请选择车型", Toast.LENGTH_SHORT).show();
					return;
				}

				if (vehicleVinStr != null && !"".equals(vehicleVinStr)) {
					try {
						Integer.parseInt(vehicleVinStr);
						meterOk = true;
					} catch (Exception e) {
						meterOk = false;
					}
				} else {
					meterOk = true;
				}
				if (!TextUtils.isEmpty(vehicleEnginNoStr)) {
					try {
						Integer.parseInt(vehicleEnginNoStr);
						meterOk = true;
					} catch (Exception e) {
						meterOk = false;
					}
				} else {
					meterOk = true;
				}
				if (vehicleRegistNoStr != null && !"".equals(vehicleRegistNoStr)) {
					try {
						Integer.parseInt(vehicleRegistNoStr);
						meterOk = true;
					} catch (Exception e) {
						meterOk = false;
					}
				} else {
					meterOk = true;
				}

				if (bindcarcurrentmileStr != null && !"".equals(bindcarcurrentmileStr)) {
					try {
						Integer.parseInt(bindcarcurrentmileStr);
						meterOk = true;
					} catch (Exception e) {
						meterOk = false;
						Toast.makeText(ChangeBindCarsActivity.this, "当前里程输入不正确", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					meterOk = true;
				}

				if (meterOk) {
					if (bindcarnextmileStr != null && !"".equals(bindcarnextmileStr)) {
						try {
							Integer.parseInt(bindcarnextmileStr);
							meterOk2 = true;
						} catch (Exception e) {
							meterOk2 = false;
							Toast.makeText(ChangeBindCarsActivity.this, "下次保养里程输入不正确",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						meterOk2 = true;
					}
				}
				if (meterOk && meterOk2) {
					if (bindcarnexttimeStr != null && !"".equals(bindcarnexttimeStr)) {
						if (nextInsuranceTimeLong >= nowTime) {
							timeOk = true;
						} else {
							timeOk = false;
							Toast.makeText(ChangeBindCarsActivity.this, "下次保险的时间不能早于当前时间",
									Toast.LENGTH_SHORT).show();
						}

					} else {
						timeOk = true;
					}

				}
				if (timeOk && meterOk && meterOk2) {

					if (bindcarnexttime2Str != null && !"".equals(bindcarnexttime2Str)) {
						if (nextExamineTimeLong >= nowTime) {
							timeOk2 = true;
						} else {
							timeOk2 = false;
							Toast.makeText(ChangeBindCarsActivity.this, "下次验车的时间不能早于当前时间",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						timeOk2 = true;
					}

				}

				if (meterOk && meterOk2 && timeOk && timeOk2) {
					
					// 判断用户是否登录
					if( !TongGouApplication.getInstance().isLogin() ) {
						
						mVehicleInfo.setVehicleVin(vehicleVinStr);
						mVehicleInfo.setNextInsuranceTime( "" + SomeUtil.StringDateToLong(bindcarnexttime.getText().toString()));
						mVehicleInfo.setNextExamineTime( "" + SomeUtil.StringDateToLong( bindcarnexttime2.getText().toString()));
						mVehicleInfo.setNextMaintainMileage(bindcarnextmileStr);
						mVehicleInfo.setNextMaintainTime("" + SomeUtil.StringDateToLong( bindcarcurrentmileStr));
						mVehicleInfo.setEngineNo(vehicleEnginNoStr);
						mVehicleInfo.setRegistNo(vehicleRegistNoStr);
						mVehicleInfo.setVehicleBrand(vehicleBrandStr);
						mVehicleInfo.setVehicleModel(vehicleModelStr);
						mVehicleInfo.setVehicleNo( vehicleNoStr );
						GuestVehicleManager manager = new GuestVehicleManager();
						manager.update(mVehicleInfo);
						Intent intent = new Intent();
						intent.putExtra("tonggou.isOk", "yes");
						setResult(Activity.RESULT_OK, intent);
						finish();
						return;
					}
					
					
					if (obdSNStr != null && !"".equals(obdSNStr)
							&& !"null".equals(obdSNStr)) {
						showLoadingDialog("保存中...");
						new Thread() {
							public void run() {
								
									// 停掉连接obd
									Intent intentS = new Intent();
									intentS.setAction(TongGouService.TONGGOU_ACTION_START);
									intentS.putExtra("com.tonggou.server", "STOP");
									sendBroadcast(intentS);
	
									bindObd(vehicleIdStr, vehicleVinStr, vehicleNoStr, vehicleModelStr, vehicleModelIdStr,
											vehicleBrandStr, vehicleBrandIdStr, obdSNStr,
											sharedPreferences.getString(BaseActivity.NAME, ""));
							}
						}.start();
					} else {
							updateVehicleInfo(vehicleIdStr, mVehicleInfo.getVehicleVin(), vehicleNoStr,
									vehicleModelStr, vehicleModelIdStr, vehicleBrandStr, vehicleBrandIdStr,
									obdSNStr, currentUsername);
					}
				}
			}
		});
		back = findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ChangeBindCarsActivity.this.finish();

				// 连接obd
				BaseConnectOBDService.addingCar = false;
				Intent intent = new Intent();// 创建Intent对象
				intent.setAction(TongGouService.TONGGOU_ACTION_START);
				intent.putExtra("com.tonggou.server", "SCAN_OBD");
				sendBroadcast(intent);// 发送广播

			}
		});
		bindcarnexttime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ChangeBindCarsActivity.this);
				View view = LayoutInflater.from(ChangeBindCarsActivity.this).inflate(
						R.layout.date_time_dialog, null);
				final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
				final TimePicker timePicker = (android.widget.TimePicker) view
						.findViewById(R.id.time_picker);

				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
						cal.get(Calendar.DAY_OF_MONTH), null);

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
						sb.append(String.format("%d-%02d-%02d", datePicker.getYear(),
								datePicker.getMonth() + 1, datePicker.getDayOfMonth()));
						sb.append("  ");
						if (timePicker.getCurrentHour() < 10) {
							sb.append("0");
						}
						sb.append(timePicker.getCurrentHour()).append(":");// .append(timePicker.getCurrentMinute());
						if (timePicker.getCurrentMinute() < 10) {
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
				AlertDialog.Builder builder = new AlertDialog.Builder(ChangeBindCarsActivity.this);
				View view = LayoutInflater.from(ChangeBindCarsActivity.this).inflate(
						R.layout.date_time_dialog, null);
				final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
				final TimePicker timePicker = (android.widget.TimePicker) view
						.findViewById(R.id.time_picker);

				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
						cal.get(Calendar.DAY_OF_MONTH), null);

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
						sb.append(String.format("%d-%02d-%02d", datePicker.getYear(),
								datePicker.getMonth() + 1, datePicker.getDayOfMonth()));
						sb.append("  ");
						if (timePicker.getCurrentHour() < 10) {
							sb.append("0");
						}
						sb.append(timePicker.getCurrentHour()).append(":");
						if (timePicker.getCurrentMinute() < 10) {
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
		bindcarnum2 = (TextView) findViewById(R.id.changebindcars_bindcarnum2);
		bindcarnum2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ChangeBindCarsActivity.this,
						AppointmentNetWorkSearch.class);
				intent.putExtra("tonggou.from", "pinpai");
				intent.putExtra("tonggou.pinpai", "");
				startActivityForResult(intent, 1010);
			}
		});
		bindcarnum1 = (TextView) findViewById(R.id.changebindcars_bindcarnum1);
		bindcarnum1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (vehicleBrandIdStr != null && !"".equals(vehicleBrandIdStr)) {
					Intent intent = new Intent(ChangeBindCarsActivity.this,
							AppointmentNetWorkSearch.class);
					intent.putExtra("tonggou.from", "chexing");
					intent.putExtra("tonggou.pinpai", vehicleBrandIdStr);
					startActivityForResult(intent, 2020);
				} else {
					Toast.makeText(ChangeBindCarsActivity.this, getString(R.string.brand_first),
							Toast.LENGTH_SHORT).show();

				}
			}
		});

		handler = new Handler() {
			@SuppressLint("HandlerLeak")
			@Override
			public void handleMessage(Message msg) {
				TongGouApplication.showLog("  #### -- " + msg.obj + "@@@@@@@@  " + msg.what);
				dismissLoadingDialog();
				switch (msg.what) {
				case NETWORK_FAILD:
					progress.setVisibility(View.GONE);
					Toast.makeText(ChangeBindCarsActivity.this, (String) msg.obj,
							Toast.LENGTH_SHORT).show();
					break;
				case NETWORK_SUCCEED:
					setVehicleInfoValue();
					break;
				case NETWORK_NODATA:
					progress.setVisibility(View.GONE);
					Toast.makeText(ChangeBindCarsActivity.this, (String) msg.obj,
							Toast.LENGTH_SHORT).show();

					break;
				case CHANGE_SUCCEED:
					ok = "yes";
					Toast.makeText(ChangeBindCarsActivity.this, (String) msg.obj,
							Toast.LENGTH_SHORT).show();

					Intent dataIntent = new Intent();
					dataIntent.putExtra("tonggou.isOk", ok);
					setResult(5050, dataIntent);
					ChangeBindCarsActivity.this.finish();
					break;
				case BIND_SUCCEED:
					ok = "yes";
					BaseConnectOBDService.cmile = bindcarmilenow.getText().toString();
					Toast.makeText(ChangeBindCarsActivity.this, "保存车辆信息成功！", Toast.LENGTH_SHORT)
							.show();
					Intent backIntent = new Intent();
					backIntent.putExtra("tonggou.isOk", ok);
					setResult(5050, backIntent);
					ChangeBindCarsActivity.this.finish();

					TongGouApplication.getInstance().queryVehicleList();
					BaseConnectOBDService.addingCar = false;
					break;

				case CHANGE_NODATA:
					Toast.makeText(ChangeBindCarsActivity.this, (String) msg.obj,
							Toast.LENGTH_SHORT).show();

					break;
				}
			}

		};

		// if( mVehicleInfo != null ) {
		// setVehicleInfoValue();
		// } else {
		
		if( TongGouApplication.getInstance().isLogin() ) {
			new Thread() {
				public void run() {
					networking(vehicleIdStr);
				}
			}.start();
			// }

			
		} else { //若是游客，则从数据看中查询车辆
			GuestVehicleManager manager = new GuestVehicleManager();
			mVehicleInfo = manager.getVehicle(vehicleIdStr);
			setVehicleInfoValue();
		}
		
		
		// 停掉连接obd
		BaseConnectOBDService.addingCar = true;
		Intent intentS = new Intent();
		intentS.setAction(TongGouService.TONGGOU_ACTION_START);
		intentS.putExtra("com.tonggou.server", "STOP");
		sendBroadcast(intentS);
		
		

	}

	private void setVehicleInfoValue() {
		progress.setVisibility(View.GONE);
		vehicleBrandIdStr = mVehicleInfo.getVehicleBrandId();
		vehicleModelIdStr = mVehicleInfo.getVehicleModelId();
		if (mVehicleInfo.getObdSN() != null && !"null".equals(mVehicleInfo.getObdSN())) {
			mTxtOBDBinded.setVisibility(View.VISIBLE);
			bindcarvehiclevin.setText(mVehicleInfo.getVehicleVin() + "");// 车架号
			vehicleVinStr = mVehicleInfo.getVehicleVin();
			vehicleIdStr = mVehicleInfo.getVehicleId();
			// ifAddInterface = true;
		}
		obdSNStr = mVehicleInfo.getObdSN();
		bindcarnum.setText(mVehicleInfo.getVehicleNo() + "");

		if (mVehicleInfo.getVehicleBrand() != null && !"".equals(mVehicleInfo.getVehicleBrand())
				&& !"null".equals(mVehicleInfo.getVehicleBrand())) {
			bindcarnum2.setText(mVehicleInfo.getVehicleBrand() + "");
		}
		if (mVehicleInfo.getVehicleModel() != null && !"".equals(mVehicleInfo.getVehicleModel())
				&& !"null".equals(mVehicleInfo.getVehicleModel())) {
			bindcarnum1.setText(mVehicleInfo.getVehicleModel() + "");
		}
		if (mVehicleInfo.getCurrentMileage() != null
				&& !"".equals(mVehicleInfo.getCurrentMileage())
				&& !"null".equals(mVehicleInfo.getCurrentMileage())) {

			if (mVehicleInfo.getCurrentMileage().indexOf(".") != -1) {
				String cm = mVehicleInfo.getCurrentMileage().substring(0,
						mVehicleInfo.getCurrentMileage().indexOf("."));
				bindcarmilenow.setText(cm);
			} else {
				bindcarmilenow.setText(mVehicleInfo.getCurrentMileage() + "");
			}

		}
		if (mVehicleInfo.getNextMaintainMileage() != null
				&& !"".equals(mVehicleInfo.getNextMaintainMileage())
				&& !"null".equals(mVehicleInfo.getNextMaintainMileage())) {
			if (mVehicleInfo.getNextMaintainMileage().indexOf(".") != -1) {
				String nm = mVehicleInfo.getNextMaintainMileage().substring(0,
						mVehicleInfo.getNextMaintainMileage().indexOf("."));
				bindcarnextmile.setText(nm);
			} else {
				bindcarnextmile.setText(mVehicleInfo.getNextMaintainMileage() + "");
			}

		}
		if (mVehicleInfo.getNextExamineTime() != null
				&& !"".equals(mVehicleInfo.getNextExamineTime())
				&& !"null".equals(mVehicleInfo.getNextExamineTime())
				&& !"0".equals(mVehicleInfo.getNextExamineTime())) {
			bindcarnexttime2.setText(SomeUtil.longToStringDate(mVehicleInfo.getNextExamineTime()
					+ ""));
		}
		if (mVehicleInfo.getNextInsuranceTime() != null
				&& !"".equals(mVehicleInfo.getNextInsuranceTime())
				&& !"null".equals(mVehicleInfo.getNextInsuranceTime())
				&& !"0".equals(mVehicleInfo.getNextInsuranceTime())) {
			bindcarnexttime.setText(SomeUtil.longToStringDate(mVehicleInfo.getNextInsuranceTime()
					+ ""));
		}
		if (mVehicleInfo.getRecommendShopName() != null) {
			mTxtShopBinded.setVisibility(View.VISIBLE);
			mTxtShopBinded.setText(String.format(
					getResources().getString(R.string.change_bindcars_shop_binded),
					mVehicleInfo.getRecommendShopName()));
		}

		if (mVehicleInfo.getVehicleVin() != null && !"".equals(mVehicleInfo.getVehicleVin())
				&& !"null".equals(mVehicleInfo.getVehicleVin())) {
			bindcarvehiclevin.setText(mVehicleInfo.getVehicleVin() + "");
		}
		if (mVehicleInfo.getEngineNo() != null && !"".equals(mVehicleInfo.getEngineNo())
				&& !"null".equals(mVehicleInfo.getEngineNo())) {
			TongGouApplication.showLog("mVehicleInfo.getEngineNo()  +  "
					+ mVehicleInfo.getEngineNo() + "");
			bindcarengineno.setText(mVehicleInfo.getEngineNo() + "");

		}
		if (mVehicleInfo.getRegistNo() != null && !"".equals(mVehicleInfo.getRegistNo())
				&& !"null".equals(mVehicleInfo.getRegistNo())) {
			bindcarregistno.setText(mVehicleInfo.getRegistNo() + "");
		}

		if (mTxtOBDBinded.getVisibility() == View.VISIBLE) {
			setViewMargins(mTxtOBDBinded, 0, SomeUtil.Dp2Px(this, 5), 0, 0);
		}

		if (mTxtShopBinded.getVisibility() == View.VISIBLE) {
			if (mTxtOBDBinded.getVisibility() != View.VISIBLE) {
				setViewMargins(mTxtShopBinded, 0, SomeUtil.Dp2Px(this, 5), 0, 0);
			}
		}

		/*
		 * mEngineNo = mVehicleInfo.getEngineNo(); if( TextUtils.isEmpty(
		 * mEngineNo ) && !"null".equals(mEngineNo) ) {
		 * bindcarvehiclevin.setText(mEngineNo); }
		 * 
		 * obdVin = mVehicleInfo.getVehicleVin(); if( TextUtils.isEmpty( obdVin
		 * ) && !"null".equalsIgnoreCase(obdVin) ) {
		 * bindcarengineno.setText(obdVin); }
		 * 
		 * mRegistNo = mVehicleInfo.getRegistNo(); if( TextUtils.isEmpty(
		 * mRegistNo ) && !"null".equals(mRegistNo) ) {
		 * bindcarregistno.setText(mRegistNo); }
		 */
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void onBackPressed() {
		ChangeBindCarsActivity.this.finish();
		if( TongGouApplication.getInstance().isLogin() ) {
			// 连接obd
			BaseConnectOBDService.addingCar = false;
			Intent intent = new Intent();// 创建Intent对象
			intent.setAction(TongGouService.TONGGOU_ACTION_START);
			intent.putExtra("com.tonggou.server", "SCAN_OBD");
			sendBroadcast(intent);// 发送广播
		}

	}

	private void setViewMargins(View view, int left, int top, int right, int bottom) {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(left, top, right, bottom);
		view.setLayoutParams(params);
	}

	private void networking(String vehicleId) {
		String url = INFO.HTTP_HEAD + INFO.HOST_IP + "/vehicle/singleVehicle/vehicleId/"
				+ vehicleId;
		VehicleParser vehicleParser = new VehicleParser();
		NetworkState ns = Network.getNetwork(ChangeBindCarsActivity.this).httpGetUpdateString(url,
				vehicleParser);

		if (ns.isNetworkSuccess()) {
			if (vehicleParser.isSuccessfull()) {
				mVehicleInfo = vehicleParser.getVehicleResponse().getVehicleInfo();
				if (mVehicleInfo != null) {
					sendMessage(NETWORK_SUCCEED, vehicleParser.getVehicleResponse().getMessage());
				} else {
					sendMessage(NETWORK_NODATA, vehicleParser.getVehicleResponse().getMessage());
				}

			} else {
				// 解析出错
				sendMessage(NETWORK_NODATA, vehicleParser.getErrorMessage());
			}
		} else {
			// 网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
		}
	}
	
	
		/**
		 * 更新字典
		 * @param vehicleModelId
		 */
		public void updateFaultDic(final String vehicleModelId) {
			new Thread() {
				@Override
				public void run() {
					UpdateFaultDic.getUpdateFaultDic(ChangeBindCarsActivity.this).updateFaultDic( vehicleModelId );
				}
			}.start();
		}

	private void updateVehicleInfo(String vehicleId, String vehicleVin, String vehicleNo,
			String vehicleModel, final String vehicleModelId, String vehicleBrand, String vehicleBrandId,
			String obdSN, String userNo) {

		showLoadingDialog("保存中...");
		VehicleInfo vehicle = new VehicleInfo();
		vehicle.setVehicleVin(vehicleVin);
		vehicle.setNextInsuranceTime(bindcarnexttime.getText().toString());
		vehicle.setNextExamineTime(bindcarnexttime2.getText().toString());
		vehicle.setNextMaintainMileage(bindcarnextmileStr);
		vehicle.setNextMaintainTime(bindcarcurrentmileStr);
		vehicle.setEngineNo(vehicleEnginNoStr);
		vehicle.setRegistNo(vehicleRegistNoStr);
		
		StoreVehicleInfoRequest request = new StoreVehicleInfoRequest();
		request.setRequestParams(userNo, vehicleId, vehicleNo, vehicleBrand, vehicleModel, vehicle, null);
		request.doRequest(this, new AsyncJSONResponseParseHandler<AddBindCarResponse>() {

			@Override
			public void onParseSuccess(AddBindCarResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				
				updateFaultDic(vehicleModelId);
				String currenMile = bindcarmilenow.getText().toString();
				BaseConnectOBDService.cmile = currenMile;
				Log.i("Bluetooth thinks", "公里cmile" + BaseConnectOBDService.cmile);

				TongGouApplication.showToast( result.getMessage());
				Intent dataIntent = new Intent();
				dataIntent.putExtra("tonggou.isOk", "yes");
				setResult(Activity.RESULT_OK, dataIntent);
				finish();
				BaseConnectOBDService.addingCar = false;
				TongGouApplication.getInstance().queryVehicleList();
				
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
				super.onParseFailure(errorCode, errorMsg);
				showErrorMessageDialog(errorMsg);
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
			}
			
			@Override
			public Class<AddBindCarResponse> getTypeClass() {
				return AddBindCarResponse.class;
			}
			
		});
		
		
	}

	protected void sendMessage(int what, String content) {
		if (what < 0) {
			what = BaseActivity.SEND_MESSAGE;
		}
		Message msg = Message.obtain(handler, what, content);
		if (msg != null) {
			msg.sendToTarget();
		}
	}

	private void setDateTime() {
		Time localTime = new Time("Asia/Hong_Kong");
		localTime.setToNow();
		bindcarnexttime.setText(localTime.format("%Y-%m-%d %H:%M"));
	}

	private void setDateTime2() {
		Time localTime = new Time("Asia/Hong_Kong");
		localTime.setToNow();
		bindcarnexttime2.setText(localTime.format("%Y-%m-%d %H:%M"));
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1010) {
			if (bindcarnum2.getText().toString() != null
					&& !"".equals(bindcarnum2.getText().toString())) {
				if (!data.getStringExtra("pinpai").equals(bindcarnum2.getText().toString())) {

					bindcarnum1.setText("");
					vehicleModelIdStr = null;
				}
			}
			bindcarnum2.setText(data.getStringExtra("pinpai"));
			vehicleBrandIdStr = data.getStringExtra("pinpaiId");

		}
		if (resultCode == 2020) {
			bindcarnum1.setText(data.getStringExtra("chexing"));
			vehicleModelIdStr = data.getStringExtra("chexingId");

		}
	}

	private void bindObd(String vehicleId, String vehicleVin, String vehicleNo,
			String vehicleModel, String vehicleModelId, String vehicleBrand, String vehicleBrandId,
			String obdSN, String userNo) {
		long nextInsuranceTimeLong = SomeUtil
				.StringDateToLong(bindcarnexttime.getText().toString()); // 保险时间
		long nextExamineTimeLong = SomeUtil.StringDateToLong(bindcarnexttime2.getText().toString()); // 验车时间

		String url = INFO.HTTP_HEAD + INFO.HOST_IP + "/obd/binding";
		CommonParser commonParser = new CommonParser();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userNo", sharedPreferences.getString(
				BaseActivity.NAME, "")));
		nameValuePairs.add(new BasicNameValuePair("obdSN", obdSN));
		nameValuePairs.add(new BasicNameValuePair("vehicleVin", vehicleVin));

		nameValuePairs.add(new BasicNameValuePair("vehicleId", vehicleId));

		nameValuePairs.add(new BasicNameValuePair("vehicleNo", vehicleNo.trim()));

		nameValuePairs.add(new BasicNameValuePair("vehicleModelId", vehicleModelId));
		nameValuePairs.add(new BasicNameValuePair("vehicleModel", vehicleModel));

		nameValuePairs.add(new BasicNameValuePair("vehicleBrandId", vehicleBrandId));
		nameValuePairs.add(new BasicNameValuePair("vehicleBrand", vehicleBrand));
		// //可选

		if (shop2DCodeId != null && !"".equals(shop2DCodeId)) {
			nameValuePairs.add(new BasicNameValuePair("sellShopId", shop2DCodeId));
		}
		if (vehicleVin != null && !"".equals(vehicleVin)) {
			// nameValuePairs.add(new BasicNameValuePair("vehicleVin",obdVin));
			nameValuePairs.contains(new BasicNameValuePair("vehicleVin", vehicleVin));
		}
		if (vehicleEnginNoStr != null && !"".equals(vehicleEnginNoStr)) {
			nameValuePairs.add(new BasicNameValuePair("engineNo", vehicleEnginNoStr));
		}

		if (vehicleRegistNoStr != null && !"".equals(vehicleRegistNoStr)) {
			nameValuePairs.add(new BasicNameValuePair("registNo", vehicleRegistNoStr));
		}

		if (bindcarcurrentmileStr != null && !"".equals(bindcarcurrentmileStr)) {
			nameValuePairs.add(new BasicNameValuePair("currentMileage", bindcarcurrentmileStr));
		}

		if (bindcarnextmileStr != null && !"".equals(bindcarnextmileStr)) {
			nameValuePairs.add(new BasicNameValuePair("nextMaintainMileage", bindcarnextmileStr));
		}
		if (nextInsuranceTimeLong != 0) {
			nameValuePairs.add(new BasicNameValuePair("nextInsuranceTime", nextInsuranceTimeLong
					+ ""));
		}
		if (nextExamineTimeLong != 0) {
			nameValuePairs.add(new BasicNameValuePair("nextExamineTime", nextExamineTimeLong + ""));
		}

		for (NameValuePair p : nameValuePairs) {
			TongGouApplication.showLog(p.getValue() + "  " + p.getName());
		}

		final NetworkState ns = Network.getNetwork(this).httpPostUpdateString(url, nameValuePairs,
				commonParser);
		if (ns.isNetworkSuccess()) {
			if (commonParser.isSuccessfull()) {
				// 正确的处理逻辑
				getCarList();
				UpdateFaultDic.getUpdateFaultDic(ChangeBindCarsActivity.this).updateFaultDic(
						vehicleModelId);
				String mes = commonParser.getCommonResponse().getMessage();
				sendMessage(BIND_SUCCEED, mes);
			} else {
				// 提示用户错误
				String errorAlert = commonParser.getErrorMessage();
				sendMessage(CHANGE_NODATA, errorAlert);
			}
		} else {
			// 网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());

		}
	}

	/**
	 * 更新车辆列表
	 */
	private void getCarList() {
//		if (TongGouApplication.imageVersion == null || "".equals(TongGouApplication.imageVersion)) {
//			TongGouApplication.imageVersion = sharedPreferences.getString(BaseActivity.SCREEN,
//					"480X800");
//		}
//		String url = INFO.HTTP_HEAD + INFO.HOST_IP + "/login";
//		LoginParser loginParser = new LoginParser();
//		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//		nameValuePairs.add(new BasicNameValuePair("userNo",currentUsername));	
//		nameValuePairs.add(new BasicNameValuePair("password",currentPwd));
//		nameValuePairs.add(new BasicNameValuePair("platform",INFO.MOBILE_PLATFORM));
//		nameValuePairs.add(new BasicNameValuePair("appVersion",INFO.VERSION));
//		//可选
//		nameValuePairs.add(new BasicNameValuePair("platformVersion",INFO.MOBILE_PLATFORM_VERSION));
//		nameValuePairs.add(new BasicNameValuePair("mobileModel",INFO.MOBILE_MODEL));
//		nameValuePairs.add(new BasicNameValuePair("imageVersion",INFO.IMAGE_VERSION));
//
//		NetworkState ns = Network.getNetwork(this).httpPostUpdateString(url, nameValuePairs,
//				loginParser);
//		if (ns.isNetworkSuccess()) {
//			if (loginParser.isSuccessfull()) {
//				// 保存数据
//				TongGouApplication.obdLists = loginParser.getLoginResponse().getObdList();
//
//			}
//		}
		
		TongGouApplication.getInstance().queryVehicleList();
	}
}
