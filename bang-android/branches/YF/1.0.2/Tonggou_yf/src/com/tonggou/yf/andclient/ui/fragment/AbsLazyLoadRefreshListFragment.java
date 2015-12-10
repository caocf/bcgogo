package com.tonggou.yf.andclient.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tonggou.yf.andclient.App;
import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.bean.Pager;
import com.tonggou.yf.andclient.widget.RefreshViewLoadMoreProxy;
import com.tonggou.yf.andclient.widget.RefreshViewLoadMoreProxy.OnLoadDataActionListener;

public abstract class AbsLazyLoadRefreshListFragment extends BaseFragment implements OnLoadDataActionListener {

	PullToRefreshListView mRefreshListView;
	RefreshViewLoadMoreProxy mRefreshProxy;
	
	Pager mPager;
	boolean mIsLoadedData = false;
	boolean mIsLoadDataImmediateAfterCreateView = false;

	abstract int getPullToRefreshViewId();
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRefreshListView =  findViewById(getPullToRefreshViewId());
		
		afterViews();
	}
	
	void afterViews() {
		mRefreshProxy = new RefreshViewLoadMoreProxy(mRefreshListView);
		mRefreshProxy.setOnLoadDataActionListener(this);
		
		ListAdapter adapter = createAdapter();
		mRefreshListView.setAdapter(adapter);
		
		afterAdapterCreated(adapter);

		if( mIsLoadDataImmediateAfterCreateView ) {
			lazyLoadData();
		}
	}
	
	public void lazyLoadData() {
		if( mRefreshListView == null || mIsLoadedData) {
			return;
		}
		mIsLoadedData = true;
		mRefreshListView.post(new Runnable() {
			
			@Override
			public void run() {
				mRefreshProxy.refreshing();
			}
		});
	}
	
	abstract ListAdapter createAdapter();
	
	abstract void afterAdapterCreated(ListAdapter adapter);
	
	abstract void requestData(int pageNo, final boolean isRefresh);
	
	@Override
	public void onRefresh(int page) {
		requestData(1, true);
	}

	@Override
	public void onLoadMore(int page) {
		if( mPager == null || !mPager.isHasNextPage() ) {
			App.showShortToast(getString(R.string.info_no_more_data));
			mRefreshProxy.loadDataActionComplete(false);
			return;
		}
		requestData(mPager.getCurrentPage() + 1, false);
	}
	
	void updatePager(Pager pager) {
		mPager = pager;
	}
	
	void onHandleFininsh() {
		ListView listView = mRefreshListView.getRefreshableView();
		if( mPager != null && mPager.isHasNextPage() 
				&& (listView.getAdapter().getCount() - listView.getHeaderViewsCount() - listView.getFooterViewsCount()) <= 3) {
			App.showLongToast(getString(R.string.info_please_pull_up_load_data));
		}
	}
}
