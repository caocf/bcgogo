package com.tonggou.andclient;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.myview.SimpleTitleBar;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.CommonParser;
import com.tonggou.andclient.scan.BeepManager;
import com.tonggou.andclient.scan.CameraPreview;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.ZBarConstants;
import com.tonggou.andclient.vo.VehicleInfo;

@SuppressLint("NewApi")
public class CaptureActivity extends AbsBackableActivity implements Camera.PreviewCallback,
		ZBarConstants {

	public static final String KEY_ARG_IS_SHOW_BACK_BUTTON = "is_show_back_button";
	private static final String TAG = "CaptureActivity";

	private BeepManager beepManager;
	private CameraPreview mPreview;
	private Camera mCamera;
	private ImageScanner mScanner;
	private boolean mPreviewing = true;
	private boolean mIsShowBackButton = true;
	private boolean isFromSettings;
	private ArrayList<VehicleInfo> mVehicleInfos;
	private boolean isGetShopInfoOK;
	private boolean receivedVehicleInfoBroadcast;
	private String mShopId;
	private String mShopName;

	static {
		System.loadLibrary("iconv");
	}
	public static boolean isFlashOn = false;
	public static boolean isFlashButtonClick = false;

	private static final int MSG_BIND_SUCCEED = 1001;
	private static final int MSG_RESULT_FAILD = 1002;
	private static final int MSG_NETWORK_FAILD = 1003;
	private static final int MSG_GET_VECHILEINFOS_ING = 2001;
	private static final int MSG_GET_VECHILEINFOS_ERROR = 2002;
	private static final int MSG_ERROR_DATA = 3001;

	Handler handlerLocal = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_ERROR_DATA:
				showAsureDialog();
				break;
			case MSG_GET_VECHILEINFOS_ING:
				TongGouApplication.showLongToast("正在获取车辆信息，请稍候...");
				break;
			case MSG_GET_VECHILEINFOS_ERROR:
				TongGouApplication.showLongToast("网络连接异常，获取车辆列表信息失败");
				finish();
				break;
			case MSG_BIND_SUCCEED:
				TongGouApplication.showToast("店铺绑定成功！");
				finish();
				break;
			case MSG_RESULT_FAILD:
				TongGouApplication.showToast((String) msg.obj);
				finish();
				break;
			case MSG_NETWORK_FAILD:
				TongGouApplication.showToast((String) msg.obj);
				finish();
				break;
			}
		};
	};

	private void sendMessage(int what, Object obj) {
		Message msg = Message.obtain(handlerLocal, what, obj);
		if (msg != null) {
			msg.sendToTarget();
		}
	}

	@Override
	protected int getContentLayout() {
		return R.layout.capturez;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (intent != null) {
			String strExtra = intent.getStringExtra("tonggou.connectobd.from");
			if (strExtra != null && "fromSettings".equals(strExtra)) {
				isFromSettings = true;
				registerReceiver();
				startService(new Intent(CommonRequestService.ACTION_GET_VEHICLE_LIST));
			}
		}
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(CommonRequestService.ACTION_GET_VEHICLE_LIST_RESULT);
		registerReceiver(mBroadcastReceiver, filter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (CommonRequestService.ACTION_GET_VEHICLE_LIST_RESULT.equals(intent.getAction())) {
				receivedVehicleInfoBroadcast = true;
				int resultState = intent.getIntExtra(CommonRequestService.EXTRA_RESULT_STATE, 0);
				Log.d(TAG, "onReceive resultState:" + resultState);
				if (CommonRequestService.RESULT_SUCCESS == resultState) {
					mVehicleInfos = intent
							.getParcelableArrayListExtra(CommonRequestService.EXTRA_VEHICLE_LIST);
					if (isGetShopInfoOK) {
						handleBindingShop();
					}
				} else {
					if (isGetShopInfoOK) {
						handlerLocal.sendEmptyMessage(MSG_GET_VECHILEINFOS_ERROR);
					}
				}
			}
		}
	};

	@Override
	protected void afterTitleBarCreated(SimpleTitleBar titleBar, Bundle savedInstanceState) {
		super.afterTitleBarCreated(titleBar, savedInstanceState);
		if (!restoreArgs(getIntent())) {
			restoreArgs(savedInstanceState);
		}
		titleBar.setTitle(R.string.title_scan_shop);
		if (!mIsShowBackButton) {
			titleBar.hideLeftButton();
			titleBar.setRightButton("跳过", R.drawable.ic_titlebar_btn_bg);
			titleBar.setOnRightButtonClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
		}
	}

	private boolean restoreArgs(Bundle args) {
		if (args != null && args.containsKey(KEY_ARG_IS_SHOW_BACK_BUTTON)) {
			mIsShowBackButton = args.getBoolean(KEY_ARG_IS_SHOW_BACK_BUTTON, true);
			return true;
		}
		return false;
	}

	private boolean restoreArgs(Intent intent) {
		if (intent != null && intent.hasExtra(KEY_ARG_IS_SHOW_BACK_BUTTON)) {
			mIsShowBackButton = intent.getBooleanExtra(KEY_ARG_IS_SHOW_BACK_BUTTON, true);
			return true;
		}
		return false;
	}

	@Override
	protected void findViews(Bundle savedInstanceState) {
		super.findViews(savedInstanceState);
		mPreview = (CameraPreview) findViewById(R.id.random_bg_txtview);
		afterViews();
	}

	private void afterViews() {
		if (!isCameraAvailable()) {
			cancelRequest();
			return;
		}
		setupScanner();
		beepManager = new BeepManager(this);// 澹伴煶
		mPreview.setCameraPreview(this, this, autoFocusCB);
	}

	public void setupScanner() {
		mScanner = new ImageScanner();
		mScanner.setConfig(0, Config.X_DENSITY, 3);
		mScanner.setConfig(0, Config.Y_DENSITY, 3);

		int[] symbols = getIntent().getIntArrayExtra(SCAN_MODES);
		if (symbols != null) {
			mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
			for (int symbol : symbols) {
				mScanner.setConfig(symbol, Config.ENABLE, 1);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Open the default i.e. the first rear facing camera.
		try {
			mCamera = Camera.open();
		} catch (Exception e) {
		}
		if (mCamera == null) {
			// Cancel request if mCamera is null.
			cancelRequest();
			return;
		}

		mPreview.setCamera(mCamera);
		if (mCamera != null) {
			mCamera.setDisplayOrientation(90);
		}
		mPreview.showSurfaceView();
		mPreviewing = true;
		beepManager.updatePrefs();

	}

	@Override
	protected void onPause() {
		super.onPause();

		// Because the Camera object is a shared resource, it's very
		// important to release it when the activity is paused.
		if (mCamera != null) {
			mPreview.setCamera(null);
			mCamera.cancelAutoFocus();
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			// According to Jason Kuang on
			// http://stackoverflow.com/questions/6519120/how-to-recover-camera-preview-from-sleep,
			// there might be surface recreation problems when the device goes
			// to sleep. So lets just hide it and
			// recreate on resume
			mPreview.hideSurfaceView();

			mPreviewing = false;
			mCamera = null;
		}
	}

	/** 鏉??姝绘Activity */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isFromSettings) {
			unregisterReceiver(mBroadcastReceiver);
		}
	}

	/********************** 浠ヤ笂涓篊aptureActivity鐨勬墍鏈夌敓鍛藉懆鏈?? **************************/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
		} else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
			// 杩囨护鎸夐敭
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void setDisplayOrientation(Camera camera, int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod("setDisplayOrientation",
					new Class[] { int.class });
			if (downPolymorphic != null)
				downPolymorphic.invoke(camera, new Object[] { angle });
		} catch (Exception e1) {
		}
	}

	public boolean isCameraAvailable() {
		PackageManager pm = getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	public void cancelRequest() {
		Intent dataIntent = new Intent();
		dataIntent.putExtra(ERROR_INFO, "Camera unavailable");
		setResult(3030, dataIntent);
		TongGouApplication.showToast("扫描出错：摄像头当前不可用");
		TongGouApplication.getInstance().notifyBindShopCancle();
		finish();
	}

	public static final String ICON_NAMES = "iconNames";
	public static final String ICON_ID = "iconID";

	public boolean processing = false;

	public void onPreviewFrame(byte[] data, Camera camera) {
		if (!processing) {

			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = mScanner.scanImage(barcode);

			if (!processing && result != 0) {
				processing = true;
				// 澶勭悊閫昏緫 娉ㄦ剰鍗曠嫭璧蜂竴涓嚎绋??
				new Thread() {
					public void run() {
						SymbolSet syms = mScanner.getResults();
						String strOrg = ""; // 原数据
						String strName = ""; // 店铺名字
						String strId = ""; // 店铺id
						// String strNameResult= ""; //最终店铺名字

						for (Symbol sym : syms) {
							try {
								strOrg = sym.getData();
								if (strOrg == null) {
									return;
								}
								byte[] b;
								Charset charSet = Charset.forName("sjis");
								CharsetEncoder cd = charSet.newEncoder();
								if (cd.canEncode(strOrg)) {
									b = strOrg.getBytes("sjis");
								} else {
									b = strOrg.getBytes("UTF-8");
								}
								strOrg = new String(b);

								if (strOrg.indexOf(",") != -1) {
									strId = strOrg.substring(0, strOrg.indexOf(","));
									strName = strOrg.substring(strOrg.indexOf(",") + 1);
									if (!isNumberic(strId)) {
										sendMessage(MSG_ERROR_DATA, null);
									}
								} else {
									sendMessage(MSG_ERROR_DATA, null);
								}

								// strNameResult = new String(b);
								// if(strNameResult.contains("??")){
								// System.out.println("乱码");
								// b = strName.getBytes("sjis");
								// /*b = tempC.getBytes("ISO-8859-1");
								// System.out.println("333="+new String(b));
								// b = tempC.getBytes("UTF-8");
								// b = tempC.getBytes("GB2312");
								// System.out.println("444="+new String(b));
								// b = tempC.getBytes("UTF-8");
								// b = tempC.getBytes("GBK");
								// System.out.println("555="+new String(b));
								// b = tempC.getBytes("UTF-8");
								// b = tempC.getBytes("UTF-16be");
								// System.out.println("666="+new String(b));
								// b = tempC.getBytes("UTF-8");
								// b = tempC.getBytes("UTF-16le");
								// System.out.println("777="+new String(b));*/
								// strNameResult = new String(b);
								// }

								if (!TextUtils.isEmpty(strId)) {
									synchronized (CaptureActivity.this) {
										isGetShopInfoOK = true;
										mShopId = strId;
										mShopName = strName;
										beepManager.playBeepSoundAndVibrate(); // 1鎾斁澹伴煶
										Intent intent = new Intent();
										intent.putExtra(ICON_NAMES, strName);
										intent.putExtra(ICON_ID, strId);
										TongGouApplication.getInstance().notifyBindShopSuccess(
												strName, strId);
										setResult(4040, intent); //
										Log.d(TAG, "isFromSettings:" + isFromSettings + " // "
												+ "receivedVehicleInfoBroadcast:"
												+ receivedVehicleInfoBroadcast);
										if (isFromSettings) {
											if (receivedVehicleInfoBroadcast) {
												if (mVehicleInfos != null) {
													handleBindingShop();
												} else {
													handlerLocal
															.sendEmptyMessage(MSG_GET_VECHILEINFOS_ERROR);
												}
											} else {
												handlerLocal
														.sendEmptyMessage(MSG_GET_VECHILEINFOS_ING);
											}
										} else {
											CaptureActivity.this.finish();
										}
										continue;
									}
								}
							} catch (UnsupportedEncodingException e) {
								System.out.println(e.getMessage().toString());
								e.printStackTrace();
							}
						}
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
						}

						processing = false;
					}
				}.start();
			}
		} else {
			// Log.d("CALLBACK","processing...........................");
		}
	}

	private boolean isNumberic(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
	
	private void showAsureDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
		builder.setMessage("扫描二维码错误");
		builder.setTitle("友情提示");
		builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});

		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}

	private void handleBindingShop() {
		if (mVehicleInfos.size() > 0) {
			Intent intent = new Intent();
			intent.putExtra(ICON_NAMES, mShopName);
			intent.putExtra(ICON_ID, mShopId);
			intent.putExtra(CommonRequestService.EXTRA_VEHICLE_LIST, mVehicleInfos);
			intent.setClass(CaptureActivity.this, BindShopActivity.class);
			startActivity(intent);
			finish();
		} else {
			new Thread() {
				public void run() {
					bindShop(mShopId);
				};
			}.start();
		}

	}

	private void bindShop(String shopId) {
		String url = INFO.HTTP_HEAD + INFO.HOST_IP + "/shop/binding";
		CommonParser commonParser = new CommonParser();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("shopId", shopId));
		final NetworkState ns = Network.getNetwork(this).httpPostUpdateString(url, nameValuePairs,
				commonParser);
		if (ns.isNetworkSuccess()) {
			if (commonParser.isSuccessfull()) {
				// 更新字典
				sendMessage(MSG_BIND_SUCCEED, commonParser.getCommonResponse().getMessage());
			} else {
				// 提示用户错误
				sendMessage(MSG_RESULT_FAILD, commonParser.getErrorMessage());
			}
		} else {
			// 网络出错
			sendMessage(MSG_NETWORK_FAILD, ns.getErrorMessage());
		}
	}

	public void onPreviewFrame0(byte[] data, Camera camera) {

		if (!processing) {
			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = parameters.getPreviewSize();
			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);
			int result = mScanner.scanImage(barcode);
			// Log.d("CALLBACK","decoding result:"+result);

			if (!processing && result != 0) {
				processing = true;

				// 澶勭悊閫昏緫 娉ㄦ剰鍗曠嫭璧蜂竴涓嚎绋??
				new Thread() {
					public void run() {
						SymbolSet syms = mScanner.getResults();
						for (Symbol sym : syms) {
							String symData = sym.getData();
							if (!TextUtils.isEmpty(symData)) {
								// 鎴愬姛浜嗙殑澶勭悊閫昏緫
								beepManager.playBeepSoundAndVibrate(); // 1鎾斁澹伴煶
								// handleDecodeExternally(symData);

								break;
							} else {
								// Log.d("CALLBACK","ISBN=绌??");
							}
						}

						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
						}
						processing = false;
					}
				}.start();

			}
		} else {
			// Log.d("CALLBACK","processing...........................");
		}
	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (mCamera != null && mPreviewing) {
				mCamera.autoFocus(autoFocusCB);
			}
		}
	};

	// Mimic continuous auto-focusing
	Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			handlerLocal.postDelayed(doAutoFocus, 1000);
		}
	};

	// 瀹炵幇杩炵画鎵弿
	private class ScanThread extends Thread {

		private int time;// 闂撮殧鏃堕棿

		public ScanThread(int time) {
			super();
			this.time = time;
		}

		@Override
		public void run() {
			super.run();
			mPreview.post(new Runnable() {
				public void run() {
					try {
						Thread.sleep(time);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	public void onBackPressed() {
		TongGouApplication.getInstance().notifyBindShopCancle();
		finish();
		super.onBackPressed();
	}

}
