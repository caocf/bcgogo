package com.tonggou.yf.andclient.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.bean.type.TodoType;
import com.tonggou.yf.andclient.util.TodoFactory;
import com.tonggou.yf.andclient.widget.view.SegmentView;

public class TodoPagerFragment extends BaseFragment {

	private static final String EXTRA_TODO_TYPE_ARR = "extra_todo_type_arr";
	
	public static Fragment newInstance(TodoType[] todoTypes) {
		Fragment fragment = new TodoPagerFragment();
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_TODO_TYPE_ARR, todoTypes);
		fragment.setArguments(args);
		return fragment;
	}
	
	TodoType[] mTodoTypes;
	SegmentView mSegmentView;
	ViewPager mViewPager;
	
	@Override
	public int getLayoutRes() {
		return R.layout.fragment_todo_page;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if( !restoreArgs(getArguments()) ) {
			restoreArgs(savedInstanceState);
		}
		
		mSegmentView = findViewById(R.id.segment);
		mViewPager = findViewById(R.id.viewPager);
		
		afterViews();
	}
	
	boolean restoreArgs(Bundle args) {
		if( args != null && args.containsKey(EXTRA_TODO_TYPE_ARR) ) {
			mTodoTypes = (TodoType[])args.getSerializable(EXTRA_TODO_TYPE_ARR);
			return true;
		}
		return false;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(EXTRA_TODO_TYPE_ARR, mTodoTypes);
		super.onSaveInstanceState(outState);
	}
	
	protected void afterViews() {
		mViewPager.setAdapter(new TodoPagerAdapter(getFragmentManager()));
		
		mSegmentView.setEntries(
				TodoFactory.getTodoTitles(getResources(), mTodoTypes));
		mSegmentView.setOnCheckedChangedListener(new SegmentView.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChange(int pos) {
				mViewPager.setCurrentItem(pos);
			}
		});
		
		mViewPager.setOffscreenPageLimit( Math.min(mTodoTypes.length, 3));
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pos) {
				mSegmentView.setCurrentCheckedItem(pos);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
	}
	
	class TodoPagerAdapter extends FragmentStatePagerAdapter {

		public TodoPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			AbsLazyLoadRefreshListFragment todoFragment = TodoFactory.getTodoFragment(mTodoTypes[pos % getCount()]);
			if( pos == 0 ) {
				todoFragment.mIsLoadDataImmediateAfterCreateView = true;
			}
			return todoFragment;
		}

		@Override
		public int getCount() {
			return mTodoTypes.length;
		}
		
		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			super.setPrimaryItem(container, position, object);
			if( position != 0 && object instanceof AbsLazyLoadRefreshListFragment) {
				((AbsLazyLoadRefreshListFragment)object).lazyLoadData();
			}
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
		
	}
	
}
