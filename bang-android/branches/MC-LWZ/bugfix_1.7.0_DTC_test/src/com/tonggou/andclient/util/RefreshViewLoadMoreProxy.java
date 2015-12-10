package com.tonggou.andclient.util;

import android.content.Context;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.tonggou.andclient.R;

@SuppressWarnings({"rawtypes", "unchecked"})

/**
 * 刷新视图（PullToRefreshView） 的刷新加载代理类
 * @author lwz
 *
 */
public class RefreshViewLoadMoreProxy implements OnRefreshListener2<View>{
	
	public interface OnLoadDataActionListener {
		public void onRefresh(int page, int dataCount);
		public void onLoadMore( int page, int dataCount );
	}
	
	private Context mContext;
	private PullToRefreshAdapterViewBase mPullToRefreshView;
	private int mPage = 0;
	private int mNum = 20;
	private OnLoadDataActionListener mOnLoadDataActionListener;
	
	public RefreshViewLoadMoreProxy(PullToRefreshAdapterViewBase pullToRefreshView) {
		this(pullToRefreshView, 0);
	}
	
	public RefreshViewLoadMoreProxy(PullToRefreshAdapterViewBase pullToRefreshView, int page) {
		this(pullToRefreshView, page, 20);
	}
	
	public RefreshViewLoadMoreProxy(PullToRefreshAdapterViewBase pullToRefreshView, int page, int num) {
		mPullToRefreshView = pullToRefreshView;
		mPage = page;
		mNum = num;
		mContext = pullToRefreshView.getContext();
		init();
	}
	
	public int getPage() {
		return mPage;
	}
	
	public int getNum() {
		return mNum;
	}

	private void init() {
		mPullToRefreshView.setMode(Mode.BOTH);
		mPullToRefreshView.getLoadingLayoutProxy(false, true).setPullLabel(getString(R.string.pull_to_refresh_from_bottom_pull_label));
		mPullToRefreshView.getLoadingLayoutProxy(false, true).setReleaseLabel(getString(R.string.pull_to_refresh_from_bottom_release_label));
		mPullToRefreshView.setOnRefreshListener(this);
	}
	
	private String getString(int resId) {
		return mContext.getString(resId);
	}
	
	public void setOnLoadDataActionListener(OnLoadDataActionListener l) {
		mOnLoadDataActionListener = l;
	}
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<View> refreshView) {
//		String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
//				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
//		refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(getString(R.string.last_update_time_prex_lable) + label);
		refresh();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<View> refreshView) {
		loadMode();
	}
	
	private void refresh() {
		resetPage();
		if( mOnLoadDataActionListener != null ) {
			mOnLoadDataActionListener.onRefresh(mPage, mNum);
		}
	}
	
	private void loadMode() {
		mPage ++;
		if( mOnLoadDataActionListener != null ) {
			mOnLoadDataActionListener.onLoadMore(mPage, mNum);
		}
	}
	
	public void resetPage() {
		mPage = 1;
	}
	
	public void loadDataActionComplete() {
		mPullToRefreshView.onRefreshComplete();
	}
}
