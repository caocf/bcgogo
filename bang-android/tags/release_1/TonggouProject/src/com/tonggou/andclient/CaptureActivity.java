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
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.tonggou.andclient.scan.BeepManager;
import com.tonggou.andclient.scan.CameraPreview;
import com.tonggou.andclient.util.ZBarConstants;

@SuppressLint("NewApi")
public class CaptureActivity extends Activity implements Camera.PreviewCallback, ZBarConstants {
	private BeepManager beepManager;
	private CameraPreview mPreview;
	private Camera mCamera;
	private ImageScanner mScanner;
	private boolean mPreviewing = true;
	static {
		System.loadLibrary("iconv");
	}
	/*é—ªå…‰ç??*/public static boolean isFlashOn=false;
	/*é—ªå…‰ç??*/public static boolean isFlashButtonClick=false;
	Handler handlerLocal=new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(!isCameraAvailable()) {
			cancelRequest();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setupScanner();
		setContentView(R.layout.capturez);

		View back=findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CaptureActivity.this.finish();
			}
		});
		// Create a RelativeLayout container that will hold a SurfaceView,
		// and set it as the content of our activity.


		beepManager = new BeepManager(this);//å£°éŸ³
		mPreview = (CameraPreview) findViewById(R.id.random_bg_txtview);
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
	/**æ??æ­»æ­¤Activity*/
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**********************ä»¥ä¸Šä¸ºCaptureActivityçš„æ‰€æœ‰ç”Ÿå‘½å‘¨æœ??**************************/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
		} else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
			//è¿‡æ»¤æŒ‰é”®
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
		finish();
	}

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
				//å¤„ç†é€»è¾‘   æ³¨æ„å•ç‹¬èµ·ä¸€ä¸ªçº¿ç¨??	        	
				new Thread(){
					public void run(){
						SymbolSet syms = mScanner.getResults();
						String strOrg = "";            //Ô­Êı¾İ
						String strName= "";            //µêÆÌÃû×Ö
						String strId= "";              //µêÆÌid
						//String strNameResult= "";      //×îÖÕµêÆÌÃû×Ö


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
//									System.out.println("ÂÒÂë");
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
									beepManager.playBeepSoundAndVibrate(); //1æ’­æ”¾å£°éŸ³	
									Intent intent = new Intent();
									intent.putExtra("iconNames",strName);
									intent.putExtra("iconID",strId);
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

				//å¤„ç†é€»è¾‘   æ³¨æ„å•ç‹¬èµ·ä¸€ä¸ªçº¿ç¨??	        	
				new Thread(){
					public void run(){
						SymbolSet syms = mScanner.getResults();
						for (Symbol sym : syms) {
							String symData = sym.getData();
							if (!TextUtils.isEmpty(symData)) {
								//æˆåŠŸäº†çš„å¤„ç†é€»è¾‘
								beepManager.playBeepSoundAndVibrate(); //1æ’­æ”¾å£°éŸ³	
								//handleDecodeExternally(symData);

								break;
							}else{
								//Log.d("CALLBACK","ISBN=ç©??");
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



	//å®ç°è¿ç»­æ‰«æ
	private class ScanThread extends Thread{

		private int time;//é—´éš”æ—¶é—´
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
}
