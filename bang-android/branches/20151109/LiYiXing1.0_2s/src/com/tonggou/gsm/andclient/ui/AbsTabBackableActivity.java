package com.tonggou.gsm.andclient.ui;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.tonggou.gsm.andclient.ui.view.DoubleTabView;
import com.tonggou.gsm.andclient.ui.view.DoubleTabView.onTabSelectedListener;

/**
 * 带 tab 及 ViewPager 的Activity
 * @author lwz
 *
 */
public abstract class AbsTabBackableActivity extends BackableTitleBarActivity implements OnPageChangeListener, onTabSelectedListener {

	final int FRAGMENT_COUNT = 2;
	DoubleTabView mTabView;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(getLayoutRes());

		mTabView = (DoubleTabView) findViewById(getTabViewId());
		mTabView.setOnTabSelectedListener(this);
		mViewPager = (ViewPager) findViewById(getViewPagerId());
		mViewPager.setAdapter(createPagerAdapter());
		mViewPager.setOnPageChangeListener(this);
	}
	abstract int getLayoutRes();

	abstract int getTabViewId();

	abstract int getViewPagerId();

	abstract PagerAdapter createPagerAdapter();

	@Override
	public void onPageScrollStateChanged(int pos) {

	}

	@Override
	public void onPageScrolled(int pos, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int pos) {
		mTabView.setCurrentTab(pos);
	}

}
