package com.tonggou.gsm.andclient.ui;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.ui.view.CameraPreview;

/**
 * 扫描 码页面抽象类
 * @author lwz
 *
 */
public abstract class AbsScanBarCodeActivity extends BackableTitleBarActivity implements Camera.PreviewCallback {
	
	/** 成功扫描条码后返回的结果 */
	public static final String EXTRA_SCAN_RESULT_STR = "extra_scan_result_str";
	
	private CameraPreview mPreview;
	private Camera mCamera;
	private ImageScanner mScanner;
	private HandlerScanResultTask mHandlerTask;
	
	private boolean mIsPreviewing = true;
	private boolean mIsProcessing = false;
	private boolean mIsSuccess = false;
	
	// 导入 zbar 库
	static {
		System.loadLibrary("iconv");
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_scan_bar_code);
		
		mPreview = (CameraPreview) findViewById(R.id.camera_preview);
		afterViews();
	}

	private void afterViews() {
		if (!isCameraAvailable()) {
			cancelRequest();
			return;
		}
		setupScanner();
		mPreview.setCameraPreview(this, this, mAautoFocusCB);
	}
	
	public void setupScanner() {
		mScanner = new ImageScanner();
		mScanner.setConfig(0, Config.X_DENSITY, 3);
		mScanner.setConfig(0, Config.Y_DENSITY, 3);

//		int[] symbols = new int[]{
//				Symbol.QRCODE, Symbol.CODABAR,
//				Symbol.CODE128, Symbol.ISBN13, 
//				Symbol.CODE39, Symbol.CODE93,
//                Symbol.DATABAR, Symbol.DATABAR_EXP, 
//                Symbol.EAN13, Symbol.EAN8, 
//                Symbol.I25, Symbol.ISBN10 };
		int[] symbols = getSymbols();
		if (symbols != null) {
			mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
			for (int symbol : symbols) {
				mScanner.setConfig(symbol, Config.ENABLE, 1);
			}
		}
	}
	
	/**
	 * 得到扫描类型
	 * @return
	 */
	abstract int[] getSymbols();
	
	@Override
	public void onResume() {
		super.onResume();
		
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
		mIsPreviewing = true;
	}
	
	@Override
	public void onPause() {
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

			mIsPreviewing = false;
			mCamera = null;
		}
	}
	
//	protected void setDisplayOrientation(Camera camera, int angle) {
//		Method downPolymorphic;
//		try {
//			downPolymorphic = camera.getClass().getMethod("setDisplayOrientation",
//					new Class[] { int.class });
//			if (downPolymorphic != null)
//				downPolymorphic.invoke(camera, new Object[] { angle });
//		} catch (Exception e1) {
//		}
//	}

	private boolean isCameraAvailable() {
		PackageManager pm = getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	private void cancelRequest() {
		finish();
	}
	
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// 若正在处理或者处理成功了，那么就不进入
		if ( !(mIsProcessing||mIsSuccess) ) {
			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = parameters.getPreviewSize();
			// 通过之前取得的size.width和size.height,
			// 加上一种图片格式,这里用的是'Y800' 创建一个Image(图片), ImageScanner就是通过扫描这个image来实现解码的
			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = mScanner.scanImage(barcode);
			
			if (result != 0) {
				mIsProcessing = true;
				mHandlerTask = new HandlerScanResultTask(this);
				mHandlerTask.execute(mScanner.getResults());
			}
		}
	}
	
	/**
	 * 处理扫描结果异步任务
	 * @author lwz
	 *
	 */
	class HandlerScanResultTask extends AsyncTask<SymbolSet, Void, String> {

		WeakReference<Activity> mReference;
		
		public HandlerScanResultTask(Activity activity) {
			mReference = new WeakReference<Activity>(activity);
		}
		
		@Override
		protected String doInBackground(SymbolSet... params) {
			String strOrg = null;
			for (Symbol sym : params[0]) {
				strOrg = sym.getData();
				if( !TextUtils.isEmpty(strOrg) ) {
					try {
						Charset charset = Charset.forName("SJIS");
						CharsetEncoder cd = charset.newEncoder();
						strOrg = new String(cd.canEncode(strOrg) ? 
								strOrg.getBytes("SJIS") : strOrg.getBytes("UTF-8"), "UTF-8");
					} catch (UnsupportedEncodingException e) {
					}
					return strOrg;
				}
			}
			return strOrg;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Activity activity = mReference.get();
			if( activity == null || activity.isFinishing() ) {
				return;
			}
			mIsProcessing = false;
			if( !TextUtils.isEmpty(result) ) {
				mIsSuccess = true;
				App.debug(TAG, result);
				onScanSuccess(result);
			}
		}
	}
	
	/**
	 * 扫描完成回调
	 * @param result
	 */
	abstract void onScanSuccess(String result);
	
	/**
	 * 重新扫描
	 */
	public void rescan() {
		mIsProcessing = false;
		mIsSuccess = false;
		if( mCamera != null ) {
			mCamera.autoFocus(mAautoFocusCB);
		}
	}
	
	// Mimic continuous auto-focusing
	Camera.AutoFocusCallback mAautoFocusCB = new Camera.AutoFocusCallback() {
		
		private Handler autoFacusHandler = new Handler(Looper.getMainLooper());
		
		public void onAutoFocus(boolean success, Camera camera) {
			App.debug(TAG, "AutoFocusCallback");
			autoFacusHandler.postDelayed(doAutoFocus, 1000);
		}
		
		private Runnable doAutoFocus = new Runnable() {
			
			public void run() {
				if (mCamera != null && mIsPreviewing && !mIsSuccess) {
					mCamera.autoFocus(mAautoFocusCB);
				}
			}
		};
	};
	
	protected void onDestroy() {
		if( mHandlerTask != null ) {
			mHandlerTask.cancel(true);
		}
		super.onDestroy();
	};
}
