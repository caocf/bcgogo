package com.tonggou.andclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.app.BaseConnectOBDService;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouApplication.OnBindOBDListener;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.network.DefaultUpdateCheck;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.LogOutParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.vo.OBDDevice;
import com.tonggou.andclient.vo.Version;

public class SettingActivity extends BaseActivity{
	private static final int NETWORK_FAILD = -1;
	private static final int LOGIN_SUCCEED = 0x001;
	private static final int LOGIN_FAILD = 0x002;
	private static final int ALERT_NEW_VERSION = 0x003;
	private Handler handler;
	private String userNo, isCheck;
	private View data, voice, bindcar, feedback, version, aboutus, bindOBD, bindShop;
	private CheckBox ifvoice;
	private View logOut;
	private AlertDialog checkAlert;
	public static final int REQUEST_CODE_BIND_OBD = 1003;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userNo = getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME,
				null);
		isCheck = getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(
				BaseActivity.CHECKVOICE, null);
		setContentView(R.layout.set);

		data = findViewById(R.id.data);
		((TextView) data.findViewById(R.id.set_tv)).setText(getString(R.string.set_data));
		((ImageView) data.findViewById(R.id.set_iv)).setImageDrawable(getResources().getDrawable(
				R.drawable.setdata));
		data.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, UserDataActivity.class);
				startActivity(intent);
			}
		});

		voice = findViewById(R.id.voice);
		((TextView) voice.findViewById(R.id.set_tv)).setText(getString(R.string.set_voice));
		((ImageView) voice.findViewById(R.id.set_iv)).setImageDrawable(getResources().getDrawable(
				R.drawable.setvoice));
		voice.findViewById(R.id.set_more).setVisibility(View.GONE);
		ifvoice = (CheckBox) voice.findViewById(R.id.set_checkbox);
		if (!"NO".equals(isCheck)) {
			ifvoice.setChecked(true);
		}
		ifvoice.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					((CheckBox) v).setChecked(true);
					getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
							.putString(BaseActivity.CHECKVOICE, "YES").commit();

				} else {
					((CheckBox) v).setChecked(false);
					getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
							.putString(BaseActivity.CHECKVOICE, "NO").commit();
				}
			}
		});

		ifvoice.setVisibility(View.VISIBLE);

		bindcar = findViewById(R.id.bindcar);
		((TextView) bindcar.findViewById(R.id.set_tv)).setText("车辆管理");
		((ImageView) bindcar.findViewById(R.id.set_iv)).setImageDrawable(getResources()
				.getDrawable(R.drawable.setbindcar));
		bindcar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, BindCarsActivity.class);
				startActivity(intent);
			}
		});

		bindOBD = findViewById(R.id.bind_obd);
		((TextView) bindOBD.findViewById(R.id.set_tv)).setText("绑定OBD");
		((ImageView) bindOBD.findViewById(R.id.set_iv)).setImageDrawable(getResources()
				.getDrawable(R.drawable.bind_obd));
		bindOBD.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, ConnectOBDDialogActivity.class);
				intent.putExtra("tonggou.connectobd.from", "fromSettings");
				startActivity(intent);
			}
		});

		bindShop = findViewById(R.id.bind_shop);
		((TextView) bindShop.findViewById(R.id.set_tv)).setText("绑定店铺");
		((ImageView) bindShop.findViewById(R.id.set_iv)).setImageDrawable(getResources()
				.getDrawable(R.drawable.bind_shop));
		bindShop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, CaptureActivity.class);
				intent.putExtra("tonggou.connectobd.from", "fromSettings");
				startActivity(intent);
			}
		});

		feedback = findViewById(R.id.feedback);
		((TextView) feedback.findViewById(R.id.set_tv)).setText(getString(R.string.set_feedback));
		((ImageView) feedback.findViewById(R.id.set_iv)).setImageDrawable(getResources()
				.getDrawable(R.drawable.setfeedback));
		feedback.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, FeedbackActivity.class);
				startActivity(intent);
			}
		});

		version = findViewById(R.id.version);
		((TextView) version.findViewById(R.id.set_tv)).setText(getString(R.string.set_version));
		((ImageView) version.findViewById(R.id.set_iv)).setImageDrawable(getResources()
				.getDrawable(R.drawable.setversion));
		version.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				checkAlert = new AlertDialog.Builder(SettingActivity.this).create();
				checkAlert.show();
				Window window = checkAlert.getWindow();
				window.setContentView(R.layout.logining);
				TextView waiting_message = (TextView) window.findViewById(R.id.loging_alerttext);
				waiting_message.setText(R.string.register_waiting);
				new Thread() {
					public void run() {
						// //检查升级通讯
						new DefaultUpdateCheck(SettingActivity.this, false).checkUpgradeAction();
						sendMessage(ALERT_NEW_VERSION, null); // 提示版本情况
					}
				}.start();
			}
		});

		aboutus = findViewById(R.id.aboutus);
		((TextView) aboutus.findViewById(R.id.set_tv)).setText(getString(R.string.set_aboutus));
		((ImageView) aboutus.findViewById(R.id.set_iv)).setImageDrawable(getResources()
				.getDrawable(R.drawable.setaboutus));
		aboutus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this, AboutUsActivity.class);
				startActivity(intent);
			}
		});

		logOut = findViewById(R.id.register_next);
		logOut.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// ////////////////////////////////////
				exit();
				deInit();
				// /////////////////////////////////////
				new Thread() {
					public void run() {
						SaveDB.getSaveDB(SettingActivity.this).closeMyDb();
						// 停掉连接obd
						Intent intent = new Intent();
						intent.setAction(TongGouService.TONGGOU_ACTION_START);
						intent.putExtra("com.tonggou.server", "STOP");
						sendBroadcast(intent);

						logout();
					}
				}.start();

				Intent toLogin = new Intent(SettingActivity.this, LoginActivity.class);
				toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(toLogin);

			}
		});
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case NETWORK_FAILD:
					Toast.makeText(SettingActivity.this, (String) msg.obj, Toast.LENGTH_SHORT)
							.show();
					break;
				case LOGIN_SUCCEED:
					Toast.makeText(SettingActivity.this, (String) msg.obj, Toast.LENGTH_SHORT)
							.show();
					break;
				case LOGIN_FAILD:
					Toast.makeText(SettingActivity.this, (String) msg.obj, Toast.LENGTH_SHORT)
							.show();
					break;
				case ALERT_NEW_VERSION:
					if (checkAlert != null) {
						checkAlert.cancel();
						checkAlert.dismiss();
					}
					if (DefaultUpdateCheck.versionAction != null
							&& DefaultUpdateCheck.versionAction.getAction() == Version.UPDATE_ACTION_FORCE) { // 强制升级
						new AlertDialog.Builder(SettingActivity.this)
								.setTitle(getString(R.string.exit_title))
								.setMessage(
										DefaultUpdateCheck.versionAction.getMessage() == null ? ""
												: DefaultUpdateCheck.versionAction.getMessage())
								.setPositiveButton(R.string.exit_submit,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int whichButton) {
												// forceUpdateNewVersion = true;

												// //////////////////////////////////////////////////////////////
												String downUrl = DefaultUpdateCheck.versionAction
														.getUrl();
												if (downUrl == null || "".equals(downUrl)) {
													// url错误提示
													Toast.makeText(SettingActivity.this, "url为空",
															Toast.LENGTH_LONG).show();
												} else {
													Uri uri = Uri.parse(downUrl);
													Intent intent = new Intent(Intent.ACTION_VIEW,
															uri);
													startActivity(intent);
												}
												// //////////////////////////////////////////////////////////////
											}
										})
								.setNeutralButton(R.string.exit_cancel,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int whichButton) { // 退出
												// NotificationManager
												// notiManager =
												// (NotificationManager)
												// getSystemService(NOTIFICATION_SERVICE);
												// notiManager.cancel(0x7f030000);
												exit();
											}
										}).show();
					} else if (DefaultUpdateCheck.versionAction != null
							&& DefaultUpdateCheck.versionAction.getAction() == Version.UPDAATE_ACTION_ALERT) { // 提示升级
						new AlertDialog.Builder(SettingActivity.this)
								.setTitle(getString(R.string.exit_title))
								.setMessage(
										DefaultUpdateCheck.versionAction.getMessage() == null ? ""
												: DefaultUpdateCheck.versionAction.getMessage())
								.setPositiveButton(R.string.exit_submit,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int whichButton) {

												// //////////////////////////////////////////////////////////////
												String downUrl = DefaultUpdateCheck.versionAction
														.getUrl();
												if (downUrl == null || "".equals(downUrl)) {
													// url错误提示
													Toast.makeText(SettingActivity.this, "url为空",
															Toast.LENGTH_LONG).show();
												} else {
													Uri uri = Uri.parse(downUrl);
													Intent intent = new Intent(Intent.ACTION_VIEW,
															uri);
													startActivity(intent);
												}
												// ///////////////////////////////////////////////////////////////

											}
										})
								.setNeutralButton(R.string.exit_cancel,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int whichButton) {

											}
										}).show();
					} else if (DefaultUpdateCheck.versionAction != null
							&& DefaultUpdateCheck.versionAction.getAction() == Version.UPDATE_ACTION_NORMAL) { // 提示
						new AlertDialog.Builder(SettingActivity.this)
								.setTitle(getString(R.string.exit_title))
								.setMessage(getString(R.string.no_update))
								.setPositiveButton(R.string.exit_submit,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int whichButton) {

											}
										}).show();
					}
					break;
				}
			}
		};

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Intent intentS = new Intent();
		intentS.setAction(TongGouService.TONGGOU_ACTION_START);
		intentS.putExtra("com.tonggou.server","STOP");
		sendBroadcast(intentS);
	}

	// private void deInit(){
	// TongGouService.allowPollingMessage = false;
	// if(TongGouApplication.obdLists!=null){
	// TongGouApplication.obdLists.clear();
	// }
	// MainActivity.haveFaultCode = false;
	// sharedPreferences.edit().putBoolean(BaseActivity.LOGINED,
	// false).commit();
	// getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
	// // .putString(BaseActivity.NAME, null)
	// // .putString(BaseActivity.PASSWORD, null)
	// .putString(BaseActivity.PHONENAME, null)
	// .putString(BaseActivity.SERVICE, null)
	// .putString(BaseActivity.PHONE, null)
	// .putString(BaseActivity.BRAND, null)
	// .putString(BaseActivity.MODEL, null)
	// .putString(BaseActivity.VEHICLENUM, null)
	// .putString(BaseActivity.CHECKVOICE, null)
	// .putString(BaseActivity.VEHICLE_MODE_ID, "")
	// .putString(BaseActivity.APPCONFIG_OIL_LAST_STATUS, "2")
	// .putInt(BaseActivity.NEW_MESSAGE_COUNT,0).commit();
	//
	// CarConditionQueryActivity.ssyhStr = "- -";
	// CarConditionQueryActivity.pjyhStr = "- - l/h";
	// CarConditionQueryActivity.syylStr = "- -";
	// CarConditionQueryActivity.sxwdStr = "- -";
	// TongGouApplication.connetedVehicleName = "";
	// TongGouApplication.connetedVIN = "";
	// TongGouApplication.connetedObdSN = "";
	// MainActivity.defaultBrandAndModle = "";
	// BaseConnectOBDService.cmile = null;
	// //TongGouApplication.loginSuccess = false;
	// }

	private void logout() {
		String url = INFO.HTTP_HEAD + INFO.HOST_IP + "/logout";
		LogOutParser logOutParser = new LogOutParser();
		String valuePairs = "{\"userNo\":\"" + userNo + "\"}";
		Network.getNetwork(SettingActivity.this).httpPutUpdateString(url, valuePairs.getBytes(),
				logOutParser);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
