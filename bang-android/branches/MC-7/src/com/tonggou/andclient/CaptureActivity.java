package com.tonggou.andclient;



import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.myview.SimpleTitleBar;
import com.tonggou.andclient.scan.BeepManager;
import com.tonggou.andclient.scan.CameraPreview;
import com.tonggou.andclient.util.ZBarConstants;

@SuppressLint("NewApi")
public class CaptureActivity extends AbsBackableActivity implements Camera.PreviewCallback, ZBarConstants {
	
	public static final String KEY_ARG_IS_SHOW_BACK_BUTTON = "is_show_back_button";
	
	private BeepManager beepManager;
	private CameraPreview mPreview;
	private Camera mCamera;
	private ImageScanner mScanner;
	private boolean mPreviewing = true;
	private boolean mIsShowBackButton = true;
	
	
	static {
		System.loadLibrary("iconv");
	}
	public static boolean isFlashOn=false;
	public static boolean isFlashButtonClick=false;

	Handler handlerLocal=new Handler();
	
	@Override
	protected int getContentLayout() {
		return R.layout.capturez;
	}
	
	@Override
	protected void afterTitleBarCreated(SimpleTitleBar titleBar, Bundle savedInstanceState) {
		super.afterTitleBarCreated(titleBar, savedInstanceState);
		if( !restoreArgs( getIntent() ) ) {
			restoreArgs(savedInstanceState);
		}
		titleBar.setTitle(R.string.title_scan_shop);
		if( !mIsShowBackButton ) {
			titleBar.hideLeftButton();
			titleBar.setRightButton("Ã¯π˝", R.drawable.ic_titlebar_btn_bg);
			titleBar.setOnRightButtonClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
		}
	}
	
	private boolean  restoreArgs(Bundle args) {
		if( args != null && args.containsKey(KEY_ARG_IS_SHOW_BACK_BUTTON) ) {
			mIsShowBackButton = args.getBoolean(KEY_ARG_IS_SHOW_BACK_BUTTON, true);
			return true;
		}
		return false;
	}
	
	private boolean restoreArgs( Intent intent ) {
		if( intent != null && intent.hasExtra(KEY_ARG_IS_SHOW_BACK_BUTTON) ) {
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
		if(!isCameraAvailable()) {
			cancelRequest();
			return;
		}
		setupScanner();
		beepManager = new BeepManager(this);//Â£∞Èü≥
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
		mCamera = Camera.open();
		if(mCamera == null) {
			// Cancel request if mCamera is null.
			cancelRequest();
			return;
		}

		mPreview.setCamera(mCamera);
		if(mCamera != null) {
			mCamera.setDisplayOrientation( 90);
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
			// According to Jason Kuang on http://stackoverflow.com/questions/6519120/how-to-recover-camera-preview-from-sleep,
			// there might be surface recreation problems when the device goes to sleep. So lets just hide it and
			// recreate on resume
			mPreview.hideSurfaceView();

			mPreviewing = false;
			mCamera = null;
		}
	}
	/**Êù??Ê≠ªÊ≠§Activity*/
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**********************‰ª•‰∏ä‰∏∫CaptureActivityÁöÑÊâÄÊúâÁîüÂëΩÂë®Êú??**************************/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
		} else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
			//ËøáÊª§ÊåâÈîÆ
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void setDisplayOrientation(Camera camera, int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod(
					"setDisplayOrientation", new Class[] { int.class });
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
		TongGouApplication.showToast("…®√Ë≥ˆ¥Ì£∫…„œÒÕ∑µ±«∞≤ªø…”√");
		TongGouApplication.getInstance().notifyBindShopCancle();
		finish();
	}

	public static final String ICON_NAMES = "iconNames";
	public static final String ICON_ID = "iconID";
	
	
	public boolean processing = false;
	public void onPreviewFrame(byte[] data, Camera camera) {
		if(!processing){  		

			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = mScanner.scanImage(barcode);

			if (!processing&&result != 0) {
				processing = true;
				//Â§ÑÁêÜÈÄªËæë   Ê≥®ÊÑèÂçïÁã¨Ëµ∑‰∏Ä‰∏™Á∫øÁ®??	        	
				new Thread(){
					public void run(){
						SymbolSet syms = mScanner.getResults();
						String strOrg = "";            //‘≠ ˝æ›
						String strName= "";            //µÍ∆Ã√˚◊÷
						String strId= "";              //µÍ∆Ãid
						//String strNameResult= "";      //◊Ó÷’µÍ∆Ã√˚◊÷


						for (Symbol sym : syms) {
							try {
								strOrg = sym.getData();
								if(strOrg==null){
									return;
								}
								byte[] b ;
								Charset charSet = Charset.forName("sjis");
								CharsetEncoder  cd = charSet.newEncoder();
								if(cd.canEncode(strOrg)){
									b = strOrg.getBytes("sjis");
								}else{
									b = strOrg.getBytes("UTF-8");
								}
								strOrg = new String(b);
								
								if(strOrg.indexOf(",")!=-1){									
									strId = strOrg.substring(0, strOrg.indexOf(","));
									strName = strOrg.substring(strOrg.indexOf(",")+1);
								}else{
									strId = strOrg;
									strName = strOrg;
								}
							
								//strNameResult = new String(b);
//								if(strNameResult.contains("??")){ 
//									System.out.println("¬“¬Î");
//									b = strName.getBytes("sjis");
//									/*b = tempC.getBytes("ISO-8859-1");
//									System.out.println("333="+new String(b));
//									b = tempC.getBytes("UTF-8");
//									b = tempC.getBytes("GB2312");
//									System.out.println("444="+new String(b));
//									b = tempC.getBytes("UTF-8");
//									b = tempC.getBytes("GBK");
//									System.out.println("555="+new String(b));
//									b = tempC.getBytes("UTF-8");
//									b = tempC.getBytes("UTF-16be");
//									System.out.println("666="+new String(b));
//									b = tempC.getBytes("UTF-8");
//									b = tempC.getBytes("UTF-16le");
//									System.out.println("777="+new String(b));*/
//									strNameResult = new String(b);
//								} 

								if (!TextUtils.isEmpty(strId)) {  ;
									beepManager.playBeepSoundAndVibrate(); //1Êí≠ÊîæÂ£∞Èü≥	
									Intent intent = new Intent();
									intent.putExtra(ICON_NAMES,strName);
									intent.putExtra(ICON_ID,strId);
									TongGouApplication.getInstance().notifyBindShopSuccess(strName, strId);
									setResult(4040, intent);       //
									CaptureActivity.this.finish();
									break;

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
		}else{
			//Log.d("CALLBACK","processing...........................");
		}
	}
	public void onPreviewFrame0(byte[] data, Camera camera) {

		if(!processing){  		
			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = parameters.getPreviewSize();
			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);
			int result = mScanner.scanImage(barcode);
			//Log.d("CALLBACK","decoding result:"+result);

			if (!processing&&result != 0) {
				processing = true;

				//Â§ÑÁêÜÈÄªËæë   Ê≥®ÊÑèÂçïÁã¨Ëµ∑‰∏Ä‰∏™Á∫øÁ®??	        	
				new Thread(){
					public void run(){
						SymbolSet syms = mScanner.getResults();
						for (Symbol sym : syms) {
							String symData = sym.getData();
							if (!TextUtils.isEmpty(symData)) {
								//ÊàêÂäü‰∫ÜÁöÑÂ§ÑÁêÜÈÄªËæë
								beepManager.playBeepSoundAndVibrate(); //1Êí≠ÊîæÂ£∞Èü≥	
								//handleDecodeExternally(symData);

								break;
							}else{
								//Log.d("CALLBACK","ISBN=Á©??");
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
		}else{
			//Log.d("CALLBACK","processing...........................");
		}
	}
	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if(mCamera != null && mPreviewing) {
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



	//ÂÆûÁé∞ËøûÁª≠Êâ´Êèè
	private class ScanThread extends Thread{

		private int time;//Èó¥ÈöîÊó∂Èó¥
		public ScanThread(int time) {
			super();
			this.time=time;
		}

		@Override
		public void run() {
			super.run();
			mPreview.post(
					new Runnable() {
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
