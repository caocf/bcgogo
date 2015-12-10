package com.tonggou.gsm.andclient.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityHelper;
import com.tonggou.gsm.andclient.R;

/**
 * 侧滑菜单基础 Activity
 * 
 * @author lwz
 */
public abstract class SlidingMenuBaseActivity extends SimpleTitleBarActivity implements SlidingActivityBase {

	private SlidingActivityHelper mHelper;
	public static final float SCROLL_SCALE_RATE = 0f;
	public static final float FADE_DEGREE = 0.35f;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHelper = new SlidingActivityHelper(this);
		mHelper.onCreate(savedInstanceState);

		// customize the SlidingMenu
		costomizeSlidingMenu();
	}

	private void costomizeSlidingMenu() {
		SlidingMenu slidingMenu = getSlidingMenu();
		setBehindContentView(getLeftMenuLayoutRes());
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		int rightMenuLayoutRes = getRightMenuLayoutRes();
		if( rightMenuLayoutRes > 0 ) {
			slidingMenu.setSecondaryMenu(rightMenuLayoutRes);
			slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
			slidingMenu.setSecondaryShadowDrawable(R.drawable.shadow_right);
		} else {
			slidingMenu.setMode(SlidingMenu.LEFT);
		}
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate(savedInstanceState);
	}

	@Override
	public View findViewById(int id) {
		View v = super.findViewById(id);
		if (v != null)
			return v;
		return mHelper.findViewById(id);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mHelper.onSaveInstanceState(outState);
	}

	@Override
	public void setContentView(int id) {
		setContentView(getLayoutInflater().inflate(id, null));
	}

	@Override
	public void setContentView(View v) {
		setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	@Override
	public void setContentView(View v, LayoutParams params) {
		super.setContentView(v, params);
		mHelper.registerAboveContentView(v, params);
		
		int leftMenuToggleViewRes = getLeftMenuToggleViewRes();
		if( leftMenuToggleViewRes > 0 ) {
			findViewById(leftMenuToggleViewRes).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					SlidingMenu slidingMenu = getSlidingMenu();
					if( slidingMenu.isMenuShowing() ) {
						slidingMenu.showContent(true);
					} else {
						slidingMenu.showMenu(true);
					}
				}
			});
		}
		
		int rightMenuToggleViewRes = getRightMenuToggleViewRes();
		if( rightMenuToggleViewRes > 0 ) {
			findViewById(rightMenuToggleViewRes).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					SlidingMenu slidingMenu = getSlidingMenu();
					if( slidingMenu.isSecondaryMenuShowing() ) {
						slidingMenu.showContent(true);
					} else {
						slidingMenu.showSecondaryMenu(true);
					}
				}
			});
		}
	}

	@Override
	public void setBehindContentView(int id) {
		setBehindContentView(getLayoutInflater().inflate(id, null));
	}

	@Override
	public void setBehindContentView(View v) {
		setBehindContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	@Override
	public void setBehindContentView(View v, LayoutParams params) {
		mHelper.setBehindContentView(v, params);
	}

	@Override
	public SlidingMenu getSlidingMenu() {
		return mHelper.getSlidingMenu();
	}

	@Override
	public void toggle() {
		mHelper.toggle();
	}

	@Override
	public void showContent() {
		mHelper.showContent();
	}

	@Override
	public void showMenu() {
		mHelper.showMenu();
	}

	@Override
	public void showSecondaryMenu() {
		mHelper.showSecondaryMenu();
	}

	@Override
	public void setSlidingActionBarEnabled(boolean b) {
		mHelper.setSlidingActionBarEnabled(b);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !getSlidingMenu().isMenuShowing()) {
			showMenu();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	/**
	 * 得到左边菜单栏的布局资源 id
	 * @return layoutRes
	 */
	public abstract int getLeftMenuLayoutRes();
	
	/**
	 * 得到显示或者不显示左边菜单的切换 View 的 id
	 * @return viewRes
	 */
	public int getLeftMenuToggleViewRes() {
		return 0;
	}
	
	/**
	 * 得到右边菜单栏的布局资源 id
	 * @return layoutRes
	 */
	public int getRightMenuLayoutRes() {
		return 0;
	}
	
	/**
	 * 得到显示或者不显示右边菜单的切换 View 的 id
	 * @return viewRes
	 */
	public int getRightMenuToggleViewRes() {
		return 0;
	}

}
