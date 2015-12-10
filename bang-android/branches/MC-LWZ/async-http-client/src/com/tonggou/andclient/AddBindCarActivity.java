package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.test.MoreAsserts;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tonggou.andclient.app.BaseConnectOBDService;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouApplication.OnBindOBDListener;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.app.UpdateFaultDic;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.AddBindCarParser;
import com.tonggou.andclient.parse.CommonParser;
import com.tonggou.andclient.parse.GetFaultDicParser;
import com.tonggou.andclient.parse.LoginParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.OBDDevice;
import com.tonggou.andclient.vo.VehicleInfo;

public class AddBindCarActivity extends BaseActivity implements OnBindOBDListener {
	private static final int NETWORK_FAILD = -1;
	private static final int ADD_SUCCEED = 0x001;
	private static final int NETWORK_SUCCEED = 0x0010;
	private static final int ADD_FAILD = 0x002;

	private int flag=0;//用于隐藏与更多的切换
	private String bindcarnumberStr, bindcarcurrentmileStr, bindcarnextmileStr, bindcarnexttimeStr,
	bindcarnexttime2Str, bindcarnum1Str, bindcarnum2Str, bindcarvehiclevinStr,
	bindcarenginenoStr, bindcarregistnoStr;
	private TextView bindcar_submit, bindcarnexttime, bindcarnexttime2, bindcarnum1, bindcarnum2;
	private View back,more,moremessage;
	private View bindnewcar;
	private TextView scanTextView;
	private VehicleInfo mVehicleInfo;

	private Handler handler;
	private EditText bindcarnumber, bindcarnextmile, currentMileET, bindcarvehiclevin,
	bindcarengineno, bindcarregistno;

	public static String shop2DCodeStr = null; // 店铺二维码
	private String shop2DCodeId = ""; // 店铺id
	private String carBrandId; // 品牌id
	private String carMoldId; // 车型id
	private String userNo, ok = "no";
	private View progressBar;
	private String obdSNStr; // 当前车辆所安装的obd的唯一标识号
	// private String obdVin = "NULL";
	private String obdVin;
	private TextView obdVinTV,more1;
	private Button more2;
	private boolean meterOk = true, meterOk2 = true, timeOk = true, timeOk2 = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TongGouApplication.getInstance().registerBindOBDListener(this);
		receiveData();

		setContentView(R.layout.addbindcar);

		findViewById(R.id.back).setFocusable(true);
		findViewById(R.id.back).setFocusableInTouchMode(true);

		userNo = getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME,
				null);

		obdVinTV = (TextView) findViewById(R.id.bindcar_number_tv);
		progressBar = findViewById(R.id.progressBar);
		bindnewcar = findViewById(R.id.add_bindcar);
		if (obdSNStr != null) {
			obdVinTV.setText(obdSNStr);
			bindnewcar.setEnabled(false);
		}
		currentMileET = (EditText) findViewById(R.id.bindcarmilenow); // 当前里程
		bindcarnextmile = (EditText) findViewById(R.id.bindcarnextmile);
		bindcarnexttime = (TextView) findViewById(R.id.bindcarnexttime);
		bindcarnexttime2 = (TextView) findViewById(R.id.bindcarnexttime2);
		bindcarnumber = (EditText) findViewById(R.id.bindcar_num); // 车牌号
		bindcarvehiclevin = (EditText) findViewById(R.id.bindcarvehiclevin);// 车架号
		if (obdVin != null) {
			bindcarvehiclevin.setText(obdVin);
			bindcarvehiclevin.setEnabled(false);
		}
		bindcarengineno = (EditText) findViewById(R.id.bindcarengineno); // 发动机号
		bindcarregistno = (EditText) findViewById(R.id.bindcarregistno); // 登记证书号
		bindcar_submit = (TextView) findViewById(R.id.bindcar_submit);

		more = findViewById(R.id.more);
		moremessage = findViewById(R.id.moremessage);
		more2 = (Button) findViewById(R.id.more2);

		bindcarnumber.setOnKeyListener(new OnKeyListener() {
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

		bindcarnumber.clearFocus();
		currentMileET.clearFocus();

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

		bindcar_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bindcarnumberStr = bindcarnumber.getText().toString().toUpperCase();

				obdVin = bindcarvehiclevin.getText().toString();
				bindcarenginenoStr = bindcarengineno.getText().toString();
				bindcarregistnoStr = bindcarregistno.getText().toString();

				bindcarcurrentmileStr = currentMileET.getText().toString();
				bindcarnextmileStr = bindcarnextmile.getText().toString();

				bindcarnexttimeStr = bindcarnexttime.getText().toString();
				bindcarnexttime2Str = bindcarnexttime2.getText().toString();
				bindcarnum2Str = bindcarnum2.getText().toString();
				bindcarnum1Str = bindcarnum1.getText().toString();
				Time localTime = new Time("Asia/Hong_Kong");
				localTime.setToNow();
				long nextInsuranceTimeLong = SomeUtil.StringDateToLong(bindcarnexttimeStr); // 保险时间
				long nowTime = SomeUtil.StringDateToLong(localTime.format("%Y-%m-%d %H:%M")
						.toString()); // 保险时间
				long nextExamineTimeLong = SomeUtil.StringDateToLong(bindcarnexttime2Str); // 验车时间
				if (bindcarnumberStr == null || "".equals(bindcarnumberStr)) {
					Toast.makeText(AddBindCarActivity.this, "请输入车牌号码", Toast.LENGTH_SHORT).show();
					return;
				} else {
					bindcarnumberStr = bindcarnumberStr.trim().replace(" ", "");
				}
				if (carBrandId == null || "".equals(carBrandId)) {
					Toast.makeText(AddBindCarActivity.this, "请选择车辆品牌", Toast.LENGTH_SHORT).show();
					return;
				}
				if (carMoldId == null || "".equals(carMoldId)) {
					Toast.makeText(AddBindCarActivity.this, "请选择车型", Toast.LENGTH_SHORT).show();
					return;
				}
				if (obdVin != null && !"".equals(obdVin)) {
					try {
						Integer.parseInt(obdVin);
						meterOk = true;
					} catch (Exception e) {
						meterOk = false;
					}
				} else {
					meterOk = true;
				}
				if (bindcarenginenoStr != null && !"".equals(bindcarenginenoStr)) {
					try {
						Integer.parseInt(bindcarenginenoStr);
						meterOk = true;
					} catch (Exception e) {
						meterOk = false;
					}
				} else {
					meterOk = true;
				}
				if (bindcarregistnoStr != null && !"".equals(bindcarregistnoStr)) {
					try {
						Integer.parseInt(bindcarregistnoStr);
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
						Toast.makeText(AddBindCarActivity.this, "当前里程输入不正确", Toast.LENGTH_SHORT)
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
					progressBar.setVisibility(View.VISIBLE);
					new Thread() {
						public void run() {
							// if(obdVin==null||"".equals(obdVin)||"NULL".equals(obdVin)){
							if (obdSNStr == null || "".equals(obdSNStr)) {
								saveCarInfor("", obdVin, bindcarnumberStr, bindcarnum1Str,
										carMoldId, bindcarnum2Str, carBrandId, userNo);// ,bindcarvehiclevinStr,bindcarenginenoStr,bindcarregistnoStr
							} else {
								bindObd(obdVin, bindcarnumberStr, bindcarnum1Str, carMoldId,
										bindcarnum2Str, carBrandId, obdSNStr, userNo,
										bindcarnextmileStr);// ,bindcarenginenoStr,bindcarregistnoStr
							}
						}
					}.start();
				}

			}
		});

		bindcarnexttime.setOnClickListener(new View.OnClickListener() {

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

						bindcarnexttime2.setText(sb);

						dialog.cancel();
					}
				});
				Dialog dialog = builder.create();
				dialog.show();
			}
		});
		// setDateTime();
		bindcarnum2 = (TextView) findViewById(R.id.bindcarnum2);
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
		bindcarnum1 = (TextView) findViewById(R.id.bindcarnum1);
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
		scanTextView = (TextView) findViewById(R.id.registerscanshop);
		scanTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AddBindCarActivity.this, CaptureActivity.class);
				startActivityForResult(intent, 4040);
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
		bindnewcar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AddBindCarActivity.this, ConnectOBDDialogActivity.class);
				intent.putExtra("tonggou.connectobd.from", "addcar");
				startActivityForResult(intent, 3031);
			}
		});

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressBar.setVisibility(View.GONE);
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
			obdVin = intent.getStringExtra(ConnectOBDDialogActivity.EXTRA_VEHICLE_VIN);
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

		if (requestCode == 3031 && resultCode == RESULT_OK) {

			if (data == null) {
				return;
			}
			String vin = data.getStringExtra(ConnectOBDDialogActivity.EXTRA_VEHICLE_VIN);
			OBDDevice obdDevice = (OBDDevice) data
					.getSerializableExtra(ConnectOBDDialogActivity.EXTRA_OBD_DEVICE);
			String obd_SN = obdDevice.getDeviceAddress();
			if (vin != null && !"".equals(vin)) {
				obdVin = vin;
				bindcarvehiclevin.setText(obdVin);
			}
			if (!TextUtils.isEmpty(obd_SN)) {
				obdSNStr = obd_SN;
				obdVinTV.setText(obdSNStr);
			}
		}
		if (resultCode == 4040) {
			String s2dCode = data.getStringExtra("iconNames");
			if (s2dCode != null && !"".equals(s2dCode)) {
				scanTextView.setText(s2dCode);
				shop2DCodeStr = s2dCode;
			}
			String s2dCodeId = data.getStringExtra("iconID");
			if (s2dCodeId != null && !"".equals(s2dCodeId)) {
				shop2DCodeId = s2dCodeId;
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
				.StringDateToLong(bindcarnexttime.getText().toString()); // 保险时间
		long nextExamineTimeLong = SomeUtil.StringDateToLong(bindcarnexttime2.getText().toString()); // 验车时间
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

		if (obdVin != null && !"".equals(obdVin)) {
			nameValuePairs.contains(new BasicNameValuePair("vehicleVin", obdVin));
		}
		if (bindcarenginenoStr != null && !"".equals(bindcarenginenoStr)) {
			nameValuePairs.add(new BasicNameValuePair("engineNo", bindcarenginenoStr));
		}

		if (bindcarregistnoStr != null && !"".equals(bindcarregistnoStr)) {
			nameValuePairs.add(new BasicNameValuePair("registNo", bindcarregistnoStr));
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
				// 正确的处理逻辑
				getCarList();
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
	private void saveCarInfor(String vehicleId, String vehicleVin, String vehicleNo,
			String vehicleModel, String vehicleModelId, String vehicleBrand, String vehicleBrandId,
			String userNo) {

		AddBindCarParser addBindCarParser = new AddBindCarParser();
		String url = INFO.HTTP_HEAD + INFO.HOST_IP + "/vehicle/vehicleInfo";
		String current = "", nextmile = "", vehiclevin = "", engineno = "", registno = "";
		if (obdVin != null && !"".equals(obdVin)) {
			vehiclevin = ",\"vehicleVin\":\"" + obdVin + "\"";
		}
		if (bindcarenginenoStr != null && !"".equals(bindcarenginenoStr)) {
			engineno = ",\"engineNo\":\"" + bindcarenginenoStr + "\"";
		}
		if (bindcarregistnoStr != null && !"".equals(bindcarregistnoStr)) {
			registno = ",\"registNo\":\"" + bindcarregistnoStr + "\"";
		}
		if (bindcarcurrentmileStr != null && !"".equals(bindcarcurrentmileStr)) {
			current = ",\"currentMileage\":\"" + bindcarcurrentmileStr + "\"";
		}
		if (bindcarnextmileStr != null && !"".equals(bindcarnextmileStr)) {
			nextmile = ",\"nextMaintainMileage\":\"" + bindcarnextmileStr + "\"";
		}

		// String valuePairs ="{\"vehicleId\":\""+vehicleId+"\", "
		// +"\"vehicleNo\":\""+vehicleNo+"\"," +
		String valuePairs = 
				"{\"vehicleNo\":\"" + vehicleNo.trim() + "\","
				+ "\"vehicleModel\":\"" + vehicleModel + "\","
				+ "\"vehicleBrand\":\"" + vehicleBrand + "\"";
				if( !TextUtils.isEmpty( vehicleModelId ) ) {
					valuePairs += ",\"vehicleModelId\":" + vehicleModelId;
				}
				if( !TextUtils.isEmpty(vehicleBrandId) ) {
					valuePairs += ",\"vehicleBrandId\":" + vehicleBrandId;
				}
				if( !TextUtils.isEmpty(bindcarnexttime.getText().toString()) ) {
					long nextInsuranceTimeLong = SomeUtil
							.StringDateToLong(bindcarnexttime.getText().toString()); // 保险时间
					valuePairs += ",\"nextInsuranceTime\":" + nextInsuranceTimeLong;
				}
				if( !TextUtils.isEmpty(bindcarnexttime2.getText().toString()) ) {
					long nextExamineTimeLong = SomeUtil.StringDateToLong(bindcarnexttime2.getText().toString()); // 验车时间
					valuePairs += ",\"nextExamineTime\":" + nextExamineTimeLong; 
				}
				valuePairs += current + nextmile + vehiclevin + engineno + registno 
						+ ",\"userNo\":\"" + userNo + "\"}";
		NetworkState ns = Network.getNetwork(AddBindCarActivity.this).httpPutUpdateString(url,
				valuePairs.getBytes(), addBindCarParser);
		if (ns.isNetworkSuccess()) {
			if (addBindCarParser.isSuccessfull()) {
				// 正确的处理逻辑
				String mes = addBindCarParser.getAddBindCarResponse().getMessage();
				// 更新字典
				UpdateFaultDic.getUpdateFaultDic(AddBindCarActivity.this).updateFaultDic(
						vehicleModelId);
				sendMessage(ADD_SUCCEED, mes);
			} else {
				// 提示用户错误
				String errorAlert = addBindCarParser.getErrorMessage();
				sendMessage(ADD_FAILD, errorAlert);
			}
		} else {
			// 网络错误
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
		}
	}

	/**
	 * 更新车辆列表
	 */
	private void getCarList() {
		String url = INFO.HTTP_HEAD + INFO.HOST_IP + "/login";
		LoginParser loginParser = new LoginParser();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userNo", currentUserId));
		nameValuePairs.add(new BasicNameValuePair("password", currentPassWd));
		nameValuePairs.add(new BasicNameValuePair("platform", INFO.MOBILE_PLATFORM));
		nameValuePairs.add(new BasicNameValuePair("appVersion", INFO.VERSION));
		// 可选
		nameValuePairs.add(new BasicNameValuePair("platformVersion",
				INFO.MOBILE_PLATFORM_VERSION));
		nameValuePairs.add(new BasicNameValuePair("mobileModel", INFO.MOBILE_MODEL));
		nameValuePairs.add(new BasicNameValuePair("imageVersion", INFO.IMAGE_VERSION));

		NetworkState ns = Network.getNetwork(AddBindCarActivity.this).httpPostUpdateString(url,
				nameValuePairs, loginParser);
		if (ns.isNetworkSuccess()) {
			if (loginParser.isSuccessfull()) {
				// 保存数据
				TongGouApplication.obdLists = loginParser.getLoginResponse().getObdList();

			}
		}
	}
	
	

	@Override
	protected void onDestroy() {
		TongGouApplication.getInstance().unregisterBindOBDListener(this);
		super.onDestroy();
	}

	@Override
	public void onBindOBDSuccess(OBDDevice device, String vin) {
		if( device != null ) {
			obdSNStr = device.getDeviceAddress();
			obdVinTV.setText(obdSNStr);
		}
		if( !TextUtils.isEmpty(vin) ) {
			bindcarvehiclevin.setText(vin);
		}
	}

	@Override
	public void onBindOBDCancle() {
		
	}

	// /**
	// * 更新故障码
	// */
	// private void updateFaultDic(String vModelId){
	// if(vModelId==null||"NULL".equals(vModelId)||"".equals(vModelId)){
	// return;
	// }
	// String url =
	// INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/faultDic/dicVersion/NULL/vehicleModelId/"+vModelId;
	// GetFaultDicParser getFaultDicParserParser = new GetFaultDicParser();
	// NetworkState ns =
	// Network.getNetwork(AddBindCarActivity.this).httpGetUpdateString(url,getFaultDicParserParser);
	// if(ns.isNetworkSuccess()){
	// if(getFaultDicParserParser.isSuccessfull()){
	// String nowVersion =
	// getFaultDicParserParser.getFaultDicResponse().getDictionaryVersion();
	// if(getFaultDicParserParser.getFaultDicResponse().getFaultCodeList()!=null&&getFaultDicParserParser.getFaultDicResponse().getFaultCodeList().size()>0){
	// //更新数据库
	// SaveDB.getSaveDB(AddBindCarActivity.this).deleteModleFaultCodes(currentUserId+vModelId);
	// SaveDB.getSaveDB(AddBindCarActivity.this).saveFaultCodeInfo(getFaultDicParserParser.getFaultDicResponse().getFaultCodeList(),currentUserId+vModelId);
	// sharedPreferences.edit().putString(BaseActivity.VEHICLE_MODE_ID,
	// vModelId).putString(BaseActivity.FAULT_DIC_VERSON, nowVersion).commit();
	// //保存版本号
	// }
	//
	//
	// }else{
	// //解析出错
	// //sendMessage(PARSE_FAILD, getFaultDicParserParser.getErrorMessage());
	// }
	// }else{
	// //网络出错
	// sendMessage(NETWORK_FAILD, ns.getErrorMessage());
	// }
	// }

}
