package com.tonggou.andclient.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 手机屏幕工具类
 * <p>该类可获得手机屏幕的高度和宽度</p>
 * 
 * @author lwz
 *
 */
public class ScreenSizeUtil {

	private final String PREF_NAME = "screen_pref";  				// preferences 的名字
	private final String KEY_WIDTH = "screen_width";				// preferences 中屏幕宽的键
	private final String KEY_HEIGHT = "screen_height"; 				// preferences 中屏幕高的键
	private final String KEY_DENSITY_DPI = "screen_density_dpi"; 	// preferences 中屏幕密度Dpi的键
	private final String KEY_DENSITY = "screen_density"; 			// preferences 中屏幕密度的键
	
	private Context mContext; 		// 上下文对象
	
	private int mScreenWidth;	// 屏幕的宽
	private int mScreenHeight; 	// 屏幕的高
	private int mDensityDpi;		// 屏幕密度Dpi
	private float mDensity; 		// 屏幕密度
	
	private ScreenSizeUtil() {
	}
	
	public ScreenSizeUtil( Context context) {
		this();
		mContext = context;
		getScreenDefaultSize();
	}
	
	/**
	 *  得到屏幕默认的尺寸
	 */
	private void getScreenDefaultSize(){
		mScreenWidth = PreferenceUtil.getInt(mContext, PREF_NAME, KEY_WIDTH);
		mScreenHeight = PreferenceUtil.getInt(mContext, PREF_NAME, KEY_HEIGHT);
		mDensityDpi = PreferenceUtil.getInt(mContext, PREF_NAME, KEY_DENSITY_DPI);
		mDensity = PreferenceUtil.getFloat(mContext, PREF_NAME, KEY_DENSITY);
		
		// 若 preferences 中不存在键值，那么就获取
		// 然后存到 preferences 中
		if( mScreenWidth == 0 || mScreenHeight == 0 || mDensityDpi == 0 || mDensity == 0F){
			DisplayMetrics metrics = new DisplayMetrics(); // 显示的像素衡量对象
			WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
			manager.getDefaultDisplay().getMetrics(metrics);
			
			mScreenWidth = metrics.widthPixels;
			mScreenHeight = metrics.heightPixels;
			mDensityDpi = metrics.densityDpi;
			mDensity = metrics.density;
			
			PreferenceUtil.putInt(mContext, PREF_NAME, KEY_WIDTH, mScreenWidth);
			PreferenceUtil.putInt(mContext, PREF_NAME, KEY_HEIGHT, mScreenHeight);
			PreferenceUtil.putInt(mContext, PREF_NAME, KEY_DENSITY_DPI, mDensityDpi);
			PreferenceUtil.putFloat(mContext, PREF_NAME, KEY_DENSITY, mDensity);
		}
	}
	
	/**
	 * 得到屏幕的宽
	 * @return int 屏幕的宽
	 */
	public int getScreenWidth(){
		return mScreenWidth;
	}
	/**
	 * 得到屏幕的高
	 * @return int 屏幕的高
	 */
	public int getScreenHeight(){
		return Integer.valueOf(mScreenHeight);
	}
	
	/**
	 * 得到屏幕的密度Dpi
	 * @return int 屏幕的密度Dpi
	 */
	public int getScreenDensityDpi(){
		return  mDensityDpi;
	}
	
	/**
	 * 得到屏幕的密度
	 * @return float 屏幕的密度
	 */
	public float getScreenDensity(){
		return mDensity;
	}
	
	/**
	 * dip 转为 px
	 * @param dipValue
	 * @return
	 */
	public int dip2px(float dipValue) {
		return (int) (dipValue * getScreenDensity() + 0.5f);
	}
	
	/**
	 * dip 转为 px
	 * @param res Resources 对象
	 * @param dipRes dip 在 xml 中配置文件中的 id
	 * @return
	 */
	public int dip2px(Resources res, int dipRes) {
		return dip2px(res.getDimension(dipRes));
	}

	/**
	 * px 转为 dip
	 * @param pxValue
	 * @return
	 */
	public int px2dip( float pxValue) {
		return (int) (pxValue / getScreenDensity() + 0.5f);
	}
}