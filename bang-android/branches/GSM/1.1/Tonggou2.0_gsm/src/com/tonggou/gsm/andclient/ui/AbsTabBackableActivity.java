package com.tonggou.gsm.andclient.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.tonggou.gsm.andclient.ui.fragment.AbsPullToRefreshLazyLoadFragment;
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
	SparseArray<AbsPullToRefreshLazyLoadFragment> mFragmentMap;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(getLayoutRes());
		
		mTabView = (DoubleTabView) findViewById(getTabViewId());
		mTabView.setOnTabSelectedListener(this);
		mViewPager = (ViewPager) findViewById(getViewPagerId());
		mViewPager.setAdapter(new TabViewPagerAdapter());
		mViewPager.setOnPageChangeListener(this);
	}
	
	abstract int getLayoutRes();
	
	abstract int getTabViewId();
	
	abstract int getViewPagerId();
	
	@Override
	public void onTabSelected(int index) {
		mViewPager.setCurrentItem(index, true);
		AbsPullToRefreshLazyLoadFragment fragment = mFragmentMap.get(index);
		if( fragment != null ) {
			fragment.startLoadData();
		}
	}
	
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
	
	class TabViewPagerAdapter extends FragmentStatePagerAdapter {

		public TabViewPagerAdapter() {
			this(getSupportFragmentManager());
		}
		
		private TabViewPagerAdapter(FragmentManager fm) {
			super(fm);
			mFragmentMap = new SparseArray<AbsPullToRefreshLazyLoadFragment>(FRAGMENT_COUNT);
		}

		@Override
		public Fragment getItem(int pos) {
			AbsPullToRefreshLazyLoadFragment fragment = mFragmentMap.get(pos);
			if( fragment == null ) {
				fragment = getViewPagerFragmentItem(pos);
				mFragmentMap.put(pos, fragment);
			}
			return fragment;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			mFragmentMap.remove(position);
			super.destroyItem(container, position, object);
		}

		@Override
		public int getCount() {
			return FRAGMENT_COUNT;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}
	
	abstract AbsPullToRefreshLazyLoadFragment getViewPagerFragmentItem(int pos);
}
