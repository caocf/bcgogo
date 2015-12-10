package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
import com.tonggou.andclient.parse.AddBindCarParser;
import com.tonggou.andclient.parse.CommonParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.OBDDevice;
import com.tonggou.andclient.vo.VehicleInfo;

public class AddBindCarActivity extends BaseActivity {
	private static final int NETWORK_FAILD = -1;
	private static final int ADD_SUCCEED = 0x001;
	private static final int NETWORK_SUCCEED = 0x0010;
	private static final int ADD_FAILD = 0x002;

	private String mVehicleNoStr, nextMaintainTimeStr, nextMaintainMileageStr, bindcarnexttimeStr,
			bindcarnexttime2Str, vehicleModel, vehicleBrand, engineNoStr,
			registNoStr;
	private TextView bindcar_submit, nextInsuranceTime, nextExamineTime, bindcarnum1, bindcarnum2;
	private View back;
	private VehicleInfo mVehicleInfo;

	private Handler handler;
	private EditText mVehicleNoEdit, bindcarnextmile, currentMileET, bindcarvehiclevin,
			bindcarengineno, bindcarregistno;

	public static String shop2DCodeStr = null; // 店铺二维码
	private String shop2DCodeId = ""; // 店铺id
	private String carBrandId; // 品牌id
	private String carMoldId; // 车型id
	private String userNo, ok = "no";
	private String obdSNStr; // 当前车辆所安装的obd的唯一标识号
	// private String obdVin = "NULL";
	private String vehicleVin;
	private boolean meterOk = true, meterOk2 = true, timeOk = true, timeOk2 = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		receiveData();

		setContentView(R.layout.addbindcar);

		findViewById(R.id.back).setFocusable(true);
		findViewById(R.id.back).setFocusableInTouchMode(true);

		userNo = getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME,
				null);

		currentMileET = (EditText) findViewById(R.id.bindcarmilenow); // 当前里程
		bindcarnextmile = (EditText) findViewById(R.id.bindcarnextmile);
		nextInsuranceTime = (TextView) findViewById(R.id.bindcarnexttime);
		nextExamineTime = (TextView) findViewById(R.id.bindcarnexttime2);
		mVehicleNoEdit = (EditText) findViewById(R.id.bindcar_num); // 车牌号
		bindcarvehiclevin = (EditText) findViewById(R.id.bindcarvehiclevin);// 车架号
		if (vehicleVin != null) {
			bindcarvehiclevin.setText(vehicleVin);
			bindcarvehiclevin.setEnabled(false);
		}
		bindcarengineno = (EditText) findViewById(R.id.bindcarengineno); // 发动机号
		bindcarregistno = (EditText) findViewById(R.id.bindcarregistno); // 登记证书号
		bindcar_submit = (TextView) findViewById(R.id.bindcar_submit);

		mVehicleNoEdit.setOnKeyListener(new OnKeyListener() {
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
					currentMileET.requestFocus();
				}
				return false;
			}
		});
		currentMileET.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66 && event.getAction() == KeyEvent.ACTION_UP) {
					bindcarnextmile.requestFocus();
				}
				return false;
			}
		});

		mVehicleNoEdit.clearFocus();
		currentMileET.clearFocus();

		bindcar_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mVehicleNoStr = mVehicleNoEdit.getText().toString().toUpperCase();

				vehicleVin = bindcarvehiclevin.getText().toString();
				engineNoStr = bindcarengineno.getText().toString();
				registNoStr = bindcarregistno.getText().toString();

				nextMaintainTimeStr = currentMileET.getText().toString();
				nextMaintainMileageStr = bindcarnextmile.getText().toString();

				bindcarnexttimeStr = nextInsuranceTime.getText().toString();
				bindcarnexttime2Str = nextExamineTime.getText().toString();
				vehicleBrand = bindcarnum2.getText().toString();
				vehicleModel = bindcarnum1.getText().toString();
				Time localTime = new Time("Asia/Hong_Kong");
				localTime.setToNow();
				long nextInsuranceTimeLong = SomeUtil.StringDateToLong(bindcarnexttimeStr); // 保险时间
				long nowTime = SomeUtil.StringDateToLong(localTime.format("%Y-%m-%d %H:%M")
						.toString()); // 保险时间
				long nextExamineTimeLong = SomeUtil.StringDateToLong(bindcarnexttime2Str); // 验车时间
				if (mVehicleNoStr == null || "".equals(mVehicleNoStr)) {
					Toast.makeText(AddBindCarActivity.this, "请输入车牌号码", Toast.LENGTH_SHORT).show();
					return;
				} else {
					mVehicleNoStr = mVehicleNoStr.trim().replace(" ", "");
				}
				if (carBrandId == null || "".equals(carBrandId)) {
					Toast.makeText(AddBindCarActivity.this, "请选择车辆品牌", Toast.LENGTH_SHORT).show();
					return;
				}
				if (carMoldId == null || "".equals(carMoldId)) {
					Toast.makeText(AddBindCarActivity.this, "请选择车型", Toast.LENGTH_SHORT).show();
					return;
				}
				if (vehicleVin != null && !"".equals(vehicleVin)) {
					try {
						Integer.parseInt(vehicleVin);
						meterOk = true;
					} catch (Exception e) {
						meterOk = false;
					}
				} else {
					meterOk = true;
				}
				if (engineNoStr != null && !"".equals(engineNoStr)) {
					try {
						Integer.parseInt(engineNoStr);
						meterOk = true;
					} catch (Exception e) {
						meterOk = false;
					}
				} else {
					meterOk = true;
				}
				if (registNoStr != null && !"".equals(registNoStr)) {
					try {
						Integer.parseInt(registNoStr);
						meterOk = true;
					} catch (Exception e) {
						meterOk = false;
					}
				} else {
					meterOk = true;
				}
				if (nextMaintainTimeStr != null && !"".equals(nextMaintainTimeStr)) {
					try {
						Integer.parseInt(nextMaintainTimeStr);
						meterOk = true;
					} catch (Exception e) {
						meterOk = false;
						Toast.makeText(AddBindCarActivity.this, "当前里程输入不正确", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					meterOk = true;
				}
				if (meterOk) {
					if (nextMaintainMileageStr != null && !"".equals(nextMaintainMileageStr)) {
						try {
							Integer.parseInt(nextMaintainMileageStr);
							meterOk2 = true;
						} catch (Exception e) {
							meterOk2 = false;
							Toast.makeText(AddBindCarActivity.this, "下次保养里程输入不正确",
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
							Toast.makeText(AddBindCarActivity.this, "下次保险的时间不能早于当前时间",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						timeOk = true;
					}
				}
				if (meterOk && meterOk2 && timeOk) {

					if (bindcarnexttime2Str != null && !"".equals(bindcarnexttime2Str)) {
						if (nextExamineTimeLong >= nowTime) {
							timeOk2 = true;
						} else {
							timeOk2 = false;
							Toast.makeText(AddBindCarActivity.this, "下次验车的时间不能早于当前时间",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						timeOk2 = true;
					}
				}

				if (meterOk && meterOk2 && timeOk && timeOk2) {
					
					// 判断用户是否登录
					if( !TongGouApplication.getInstance().isLogin() ) {
						
						VehicleInfo vehicle = new VehicleInfo();
						vehicle.setVehicleVin(vehicleVin);
						vehicle.setVehicleNo( mVehicleNoStr );
						vehicle.setVehicleBrand(vehicleBrand);
						vehicle.setVehicleModel(vehicleModel);
						vehicle.setRegistNo( registNoStr );
						vehicle.setNextInsuranceTime( "" + SomeUtil.StringDateToLong(nextInsuranceTime.getText().toString()));
						vehicle.setNextExamineTime( "" + SomeUtil.StringDateToLong( nextExamineTime.getText().toString()));
						vehicle.setNextMaintainMileage(nextMaintainMileageStr);
						vehicle.setNextMaintainTime( "" + SomeUtil.StringDateToLong( nextMaintainTimeStr));
						vehicle.setEngineNo(engineNoStr);
						vehicle.setRegistNo(registNoStr);
						GuestVehicleManager manager = new GuestVehicleManager();
						manager.add(vehicle);
						Intent intent = new Intent();
						intent.putExtra("tonggou.isOk", "yes");
						setResult(Activity.RESULT_OK, intent);
						finish();
						return;
					}
					
					
					showLoadingDialog("保存中...");
					if (obdSNStr == null || "".equals(obdSNStr)) {
						saveVehicleInfo(null, vehicleVin, mVehicleNoStr, vehicleModel,
								carMoldId, vehicleBrand, carBrandId, userNo);// bindcarenginenoStr,bindcarregistnoStr
					} else {
						new Thread() {
							public void run() {
								// if(obdVin==null||"".equals(obdVin)||"NULL".equals(obdVin)){
								bindObd(vehicleVin, mVehicleNoStr, vehicleModel, carMoldId,
										vehicleBrand, carBrandId, obdSNStr, userNo,
										nextMaintainMileageStr);// ,bindcarenginenoStr,bindcarregistnoStr
							}
						}.start();
					}
				}

			}
		});

		nextInsuranceTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(AddBindCarActivity.this);
				View view = LayoutInflater.from(AddBindCarActivity.this).inflate(
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

						nextInsuranceTime.setText(sb);
						// etEndTime.requestFocus();

						dialog.cancel();
					}
				});
				Dialog dialog = builder.create();
				dialog.show();
			}
		});
		nextExamineTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(AddBindCarActivity.this);
				View view = LayoutInflater.from(AddBindCarActivity.this).inflate(
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

						nextExamineTime.setText(sb);

						dialog.cancel();
					}
				});
				Dialog dialog = builder.create();
				dialog.show();
			}
		});
		// setDateTime();
		bindcarnum2 = (TextView) findViewById(R.id.addbindcar_bindcarnum2);
		bindcarnum2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AddBindCarActivity.this, AppointmentNetWorkSearch.class);
				intent.putExtra("tonggou.from", "pinpai");
				intent.putExtra("tonggou.pinpai", "");
				startActivityForResult(intent, 1010);
			}
		});
		bindcarnum1 = (TextView) findViewById(R.id.addbindcar_bindcarnum1);
		bindcarnum1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (carBrandId != null && !"".equals(carBrandId)) {
					Intent intent = new Intent(AddBindCarActivity.this,
							AppointmentNetWorkSearch.class);
					intent.putExtra("tonggou.from", "chexing");
					intent.putExtra("tonggou.pinpai", carBrandId);
					startActivityForResult(intent, 2020);
				} else {
					Toast.makeText(AddBindCarActivity.this, getString(R.string.brand_first),
							Toast.LENGTH_SHORT).show();

				}
			}
		});
		back = findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddBindCarActivity.this.finish();

				TongGouApplication.getInstance().queryVehicleList();
				BaseConnectOBDService.addingCar = false;
			}
		});

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				dismissLoadingDialog();
				switch (msg.what) {
				case NETWORK_FAILD:
					Toast.makeText(AddBindCarActivity.this, (String) msg.obj, Toast.LENGTH_SHORT)
							.show();
					break;
				case NETWORK_SUCCEED:
					carBrandId = mVehicleInfo.getVehicleBrandId();
					carMoldId = mVehicleInfo.getVehicleModelId();
					if (mVehicleInfo.getObdSN() != null && !"null".equals(mVehicleInfo.getObdSN())) {
						bindcarvehiclevin.setText(mVehicleInfo.getVehicleVin() + "");
						// obdVin = mVehicleInfo.getVehicleVin();
						// veId = mVehicleInfo.getVehicleId();
					}
					// obdSNStr = mVehicleInfo.getObdSN();
					// obdVin = mVehicleInfo.getVehicleVin();
					// bindcarvehiclevin.setText(mVehicleInfo.getVehicleVin()+"");
					break;
				case ADD_SUCCEED:
					ok = "yes";
					String haveCurrenMile = currentMileET.getText().toString();
					BaseConnectOBDService.cmile = haveCurrenMile;
					Log.i("Bluetooth thinks", "公里cmile" + BaseConnectOBDService.cmile);

					Toast.makeText(AddBindCarActivity.this, (String) msg.obj, Toast.LENGTH_SHORT)
							.show();
					Intent dataIntent = new Intent();
					dataIntent.putExtra("tonggou.isOk", ok);
					setResult(3030, dataIntent);
					AddBindCarActivity.this.finish();

					TongGouApplication.getInstance().queryVehicleList();
					BaseConnectOBDService.addingCar = false;

					break;
				case ADD_FAILD:
					Toast.makeText(AddBindCarActivity.this, (String) msg.obj, Toast.LENGTH_SHORT)
							.show();

					break;
				}
			}
		};

		// 停掉连接obd
		BaseConnectOBDService.addingCar = true;
		Intent intentS = new Intent();
		intentS.setAction(TongGouService.TONGGOU_ACTION_START);
		intentS.putExtra("com.tonggou.server", "STOP");
		sendBroadcast(intentS);

	}

	private void receiveData() {
		Intent intent = getIntent();
		if (intent != null) {
			vehicleVin = intent.getStringExtra(ConnectOBDDialogActivity.EXTRA_VEHICLE_VIN);
			OBDDevice obdDevice = (OBDDevice) intent
					.getSerializableExtra(ConnectOBDDialogActivity.EXTRA_OBD_DEVICE);
			if (obdDevice != null) {
				obdSNStr = obdDevice.getDeviceAddress();
			}
		}
	}

	public void onResume() {
		super.onResume();
		// if(readFromObdAddress!=null){
		// obdVinTV.setText(readFromObdAddress);
		// }
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
		AddBindCarActivity.this.finish();
		// 连接obd
		Intent intent = new Intent();// 创建Intent对象
		intent.setAction(TongGouService.TONGGOU_ACTION_START);
		intent.putExtra("com.tonggou.server", "SCAN_OBD");
		sendBroadcast(intent);// 发送广播
		BaseConnectOBDService.addingCar = false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1010) {
			if (data.getStringExtra("pinpai") != null && !"".equals(data.getStringExtra("pinpai"))) {
				if (bindcarnum2.getText().toString() != null
						&& !"".equals(bindcarnum2.getText().toString())) {
					if (!bindcarnum2.getText().toString().equals(data.getStringExtra("pinpai"))) {

						bindcarnum1.setText("");
						carMoldId = null;
					}
				}
				bindcarnum2.setText(data.getStringExtra("pinpai"));
			}
			if (data.getStringExtra("pinpaiId") != null) {
				carBrandId = data.getStringExtra("pinpaiId");
			}

		}
		if (resultCode == 2020) {
			if (data.getStringExtra("chexing") != null) {
				bindcarnum1.setText(data.getStringExtra("chexing"));
			}
			if (data.getStringExtra("chexingId") != null) {
				carMoldId = data.getStringExtra("chexingId");
			}
		}
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

	private void bindObd(String vehicleVin, String vehicleNo, String vehicleModel,
			String vehicleModelId, String vehicleBrand, String vehicleBrandId, String obdSN,
			String userNo, String nextMaintainMileage) {

		long nextInsuranceTimeLong = SomeUtil
				.StringDateToLong(nextInsuranceTime.getText().toString()); // 保险时间
		long nextExamineTimeLong = SomeUtil.StringDateToLong(nextExamineTime.getText().toString()); // 验车时间
		String currentMileStr = currentMileET.getText().toString();

		String url = INFO.HTTP_HEAD + INFO.HOST_IP + "/obd/binding";
		CommonParser commonParser = new CommonParser();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userNo", sharedPreferences.getString(
				BaseActivity.NAME, "")));
		nameValuePairs.add(new BasicNameValuePair("obdSN", obdSN));
		nameValuePairs.add(new BasicNameValuePair("vehicleVin", vehicleVin));

		// nameValuePairs.add(new BasicNameValuePair("vehicleId","NULL"));

		nameValuePairs.add(new BasicNameValuePair("vehicleNo", vehicleNo.trim()));

		nameValuePairs.add(new BasicNameValuePair("vehicleModelId", vehicleModelId));
		nameValuePairs.add(new BasicNameValuePair("vehicleModel", vehicleModel));

		nameValuePairs.add(new BasicNameValuePair("vehicleBrandId", vehicleBrandId));
		nameValuePairs.add(new BasicNameValuePair("vehicleBrand", vehicleBrand));
		// 可选

		if (vehicleVin != null && !"".equals(vehicleVin)) {
			nameValuePairs.contains(new BasicNameValuePair("vehicleVin", vehicleVin));
		}
		if (engineNoStr != null && !"".equals(engineNoStr)) {
			nameValuePairs.add(new BasicNameValuePair("engineNo", engineNoStr));
		}

		if (registNoStr != null && !"".equals(registNoStr)) {
			nameValuePairs.add(new BasicNameValuePair("registNo", registNoStr));
		}

		if (shop2DCodeId != null && !"".equals(shop2DCodeId)) {
			nameValuePairs.add(new BasicNameValuePair("sellShopId", shop2DCodeId));
		}

		if (currentMileStr != null && !"".equals(currentMileStr)) {
			nameValuePairs.add(new BasicNameValuePair("currentMileage", currentMileStr));
		}

		if (nextMaintainMileage != null && !"".equals(nextMaintainMileage)) {
			nameValuePairs.add(new BasicNameValuePair("nextMaintainMileage", nextMaintainMileage));
		}
		if (nextInsuranceTimeLong != 0) {
			nameValuePairs.add(new BasicNameValuePair("nextInsuranceTime", nextInsuranceTimeLong
					+ ""));
		}
		if (nextExamineTimeLong != 0) {
			nameValuePairs.add(new BasicNameValuePair("nextExamineTime", nextExamineTimeLong + ""));
		}

		final NetworkState ns = Network.getNetwork(this).httpPostUpdateString(url, nameValuePairs,
				commonParser);
		if (ns.isNetworkSuccess()) {
			if (commonParser.isSuccessfull()) {

				// 绑定完成后请求重新车辆列表
				TongGouApplication.getInstance().queryVehicleList();
				
				// 更新字典
				UpdateFaultDic.getUpdateFaultDic(AddBindCarActivity.this).updateFaultDic(
						vehicleModelId);
				String mes = commonParser.getCommonResponse().getMessage();
				sendMessage(ADD_SUCCEED, mes);
			} else {
				// 提示用户错误
				String errorAlert = commonParser.getErrorMessage();
				sendMessage(ADD_FAILD, errorAlert);
			}
		} else {
			// 网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());

		}
	}

	// 保存车辆信息
	private void saveVehicleInfo(String vehicleId, String vehicleVin, String vehicleNo,
			String vehicleModel, final String vehicleModelId, String vehicleBrand, String vehicleBrandId,
			String userNo) {
		showLoadingDialog("保存中...");
		VehicleInfo vehicle = new VehicleInfo();
		vehicle.setVehicleVin(vehicleVin);
		vehicle.setNextInsuranceTime(nextInsuranceTime.getText().toString());
		vehicle.setNextExamineTime(nextExamineTime.getText().toString());
		vehicle.setNextMaintainMileage(nextMaintainMileageStr);
		vehicle.setNextMaintainTime(nextMaintainTimeStr);
		vehicle.setEngineNo(engineNoStr);
		vehicle.setRegistNo(registNoStr);
		
		StoreVehicleInfoRequest request = new StoreVehicleInfoRequest();
		request.setRequestParams(userNo, vehicleId, vehicleNo, vehicleBrand, vehicleModel, vehicle, null);
		request.doRequest(this, new AsyncJSONResponseParseHandler<AddBindCarResponse>() {

			@Override
			public void onParseSuccess(AddBindCarResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				
				updateFaultDic(vehicleModelId);
				String haveCurrenMile = currentMileET.getText().toString();
				BaseConnectOBDService.cmile = haveCurrenMile;
				Log.i("Bluetooth thinks", "公里cmile" + BaseConnectOBDService.cmile);

				TongGouApplication.showToast( result.getMessage());
				Intent dataIntent = new Intent();
				dataIntent.putExtra("tonggou.isOk", "yes");
				setResult(Activity.RESULT_OK, dataIntent);
				AddBindCarActivity.this.finish();
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
	
	/**
	 * 更新字典
	 * @param vehicleModelId
	 */
	public void updateFaultDic(final String vehicleModelId) {
		new Thread() {
			@Override
			public void run() {
				UpdateFaultDic.getUpdateFaultDic(AddBindCarActivity.this).updateFaultDic( vehicleModelId );
			}
		}.start();
	}

}
