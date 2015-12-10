package com.tonggou.andclient;

import java.util.ArrayList;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyVehicleConditionActivity extends FragmentActivity {
	private Resources mResources;
	private TextView[] mTxtSubtitles;
	private ViewPager mViewPager;
	private SparseArray<Fragment> mFramentsMap = new SparseArray<Fragment>();

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.my_car_condition);
		initViews();
		initFragments();
	}

	private void initViews() {
		mResources = getResources();
		mViewPager = (ViewPager) findViewById(R.id.vp_myCcarCondition);
		RelativeLayout rlBack = (RelativeLayout) findViewById(R.id.rl_myCcarCondition_back);
		LinearLayout llCurrCondition = (LinearLayout) findViewById(R.id.ll_myCcarCondition_curr_condtion);
		LinearLayout llDrivingJournal = (LinearLayout) findViewById(R.id.ll_myCcarCondition_driving_journal);
		mTxtSubtitles = new TextView[2];
		mTxtSubtitles[0] = (TextView) findViewById(R.id.txt_myCcarCondition_curr_condtion);
		mTxtSubtitles[1] = (TextView) findViewById(R.id.txt_myCcarCondition_driving_journal);

		rlBack.setOnClickListener(mOnClickListener);
		llCurrCondition.setOnClickListener(mOnClickListener);
		llDrivingJournal.setOnClickListener(mOnClickListener);
	}

	private void initFragments() {
		PagerAdapter mPagerAdapter = new VehicleConditionPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(mOnPageChangeListener);
	}

	private class VehicleConditionPagerAdapter extends FragmentStatePagerAdapter {
		private ArrayList<Fragment> mFraments = new ArrayList<Fragment>();

		public VehicleConditionPagerAdapter(FragmentManager fm) {
			super(fm);
			mFraments.add(new CurrentCondtionFragment());
			mFraments.add(new DrivingJournalFragment());
		}

		@Override
		public Fragment getItem(int postion) {
			Fragment fragment = mFramentsMap.get(postion);
			if (fragment == null) {
				fragment = mFraments.get(postion);
				mFramentsMap.put(postion, fragment);
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return mFraments.size();
		}
	}

	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			setSubtitle(position);
		}
	};

	private void setSubtitle(int postion) {
		mTxtSubtitles[postion].setTextColor(mResources.getColor(android.R.color.white));
		mTxtSubtitles[(postion + 1) % mTxtSubtitles.length].setTextColor(mResources
				.getColor(android.R.color.darker_gray));
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rl_myCcarCondition_back:
				MyVehicleConditionActivity.this.finish();
				break;
			case R.id.ll_myCcarCondition_curr_condtion:
				mViewPager.setCurrentItem(0);
				setSubtitle(0);
				break;
			case R.id.ll_myCcarCondition_driving_journal:
				mViewPager.setCurrentItem(1);
				setSubtitle(1);
				break;
			}
		}
	};
}
