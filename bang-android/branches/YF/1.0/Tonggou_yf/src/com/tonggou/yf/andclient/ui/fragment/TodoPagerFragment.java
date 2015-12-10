package com.tonggou.yf.andclient.ui.fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.bean.type.TodoType;
import com.tonggou.yf.andclient.util.TodoFactory;
import com.tonggou.yf.andclient.widget.view.SegmentView;

@EFragment(R.layout.fragment_todo_page)
public class TodoPagerFragment extends BaseFragment {

	@FragmentArg @InstanceState TodoType[] mTodoTypes;
	@ViewById(R.id.segment) SegmentView mSegmentView;
	@ViewById(R.id.viewPager) ViewPager mViewPager;
	
	@AfterViews
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
