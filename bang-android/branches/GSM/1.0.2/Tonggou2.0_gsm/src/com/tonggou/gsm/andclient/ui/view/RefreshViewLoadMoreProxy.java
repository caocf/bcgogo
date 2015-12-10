package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.util.PreferenceUtil;

@SuppressWarnings({"rawtypes", "unchecked"})

/**
 * 刷新视图（PullToRefreshView） 的刷新加载代理类
 * @author lwz
 *
 */
public class RefreshViewLoadMoreProxy implements OnRefreshListener2<View>{
	
	public interface OnLoadDataActionListener {
		public void onRefresh( int page );
		public void onLoadMore( int page );
	}
	
	private Context mContext;
	private PullToRefreshAdapterViewBase mPullToRefreshView;
	private int START_PAGE = 0;
	private int mCurrentPage = START_PAGE;
	private OnLoadDataActionListener mOnLoadDataActionListener;
	private Mode mRefreshMode = Mode.BOTH;
	
	public RefreshViewLoadMoreProxy(PullToRefreshAdapterViewBase pullToRefreshView) {
		this(pullToRefreshView, Mode.BOTH, 0, 0);
	}
	
	public RefreshViewLoadMoreProxy(PullToRefreshAdapterViewBase pullToRefreshView, Mode mode) {
		this(pullToRefreshView, mode, 0, 0);
	}
	
	public RefreshViewLoadMoreProxy(PullToRefreshAdapterViewBase pullToRefreshView, int startPage, int currentPage) {
		this(pullToRefreshView, Mode.BOTH, startPage, currentPage);
	}
	
	public RefreshViewLoadMoreProxy(PullToRefreshAdapterViewBase pullToRefreshView, Mode mode, int startPage, int currentPage) {
		mPullToRefreshView = pullToRefreshView;
		START_PAGE = startPage;
		mCurrentPage = currentPage;
		mContext = pullToRefreshView.getContext();
		mRefreshMode = mode;
		updateLastUpdatedLabel(mPullToRefreshView);
		init();
	}
	
	public int getPage() {
		return mCurrentPage;
	}

	private void init() {
		mPullToRefreshView.setMode(mRefreshMode);
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
		updateLastUpdatedLabel(refreshView);
		refresh();
	}
	
	/**
	 * 更新 上次加载时间标签中的值
	 * 
	 * <p>NOTE: 上次加载时间存储在 sharedPreferences 中， key 是根据 {@link View #getContentDescription()} 方法取得的，
	 * 故需要在xml布局文件中设置 view 的   android:contentDescription 属性或者调用  {@link View #setContentDescription(CharSequence)}
	 * @param refreshView
	 */
	public static void updateLastUpdatedLabel(PullToRefreshBase<View> refreshView) {
		CharSequence cacheKey = refreshView.getContentDescription();
		if( TextUtils.isEmpty(cacheKey) ) {
			return;
		}
		Context context = refreshView.getContext();
		long lastUpdateTime = PreferenceUtil.getLong(context, Constants.PREF.PREF_NAME_OTHER_INFO, String.valueOf(cacheKey));
		if( lastUpdateTime <= 0 ) {
			return;
		}
		updateLastUpdateLabel(refreshView, lastUpdateTime);
	}
	
	/**
	 * 更新 上次更新时间
	 * @param refreshView
	 * @param lastUpdateTime
	 */
	public static void updateLastUpdateLabel(PullToRefreshBase<View>refreshView, long lastUpdateTime) {
		Context context = refreshView.getContext();
		String label = DateUtils.formatDateTime(context, lastUpdateTime,
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
		refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(context.getString(R.string.last_update_time_prex_lable) + label);
	}
	
	/**
	 * 存储 上次加载时间
	 * 
	 * <p>NOTE: 上次加载时间存储在 sharedPreferences 中， key 是根据 {@link View #getContentDescription()} 方法取得的，
	 * 故需要在xml布局文件中设置 view 的   android:contentDescription 属性或者调用  {@link View #setContentDescription(CharSequence)}
	 * @param refreshView
	 */
	public static void storeAndUpdateLastUpdateTime(PullToRefreshBase<View> refreshView) {
		CharSequence cacheKey = refreshView.getContentDescription();
		if( TextUtils.isEmpty(cacheKey) ) {
			return;
		}
		Context context = refreshView.getContext();
		long time = System.currentTimeMillis();
		updateLastUpdateLabel(refreshView, time);
		PreferenceUtil.putLong(context, Constants.PREF.PREF_NAME_OTHER_INFO, String.valueOf(cacheKey), time);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<View> refreshView) {
		loadMode();
	}
	
	private void refresh() {
		resetPage();
		if( mOnLoadDataActionListener != null ) {
			mOnLoadDataActionListener.onRefresh(mCurrentPage);
		}
	}
	
	private void loadMode() {
		mCurrentPage ++;
		if( mOnLoadDataActionListener != null ) {
			mOnLoadDataActionListener.onLoadMore(mCurrentPage);
		}
	}
	
	/**
	 * 得到初始 pageNo
	 * @return
	 */
	public int resetPage() {
		return mCurrentPage = START_PAGE;
	}
	
	/**
	 * 直接刷新，适合第一次进入页面自动刷新使用
	 */
	public void refreshing() {
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mPullToRefreshView.setRefreshing(true);
			}
		}, 500);
	}
	
	/**
	 * 加载数据完毕调用此方法
	 */
	public void loadDataActionComplete(final boolean isRefresh) {
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mPullToRefreshView.onRefreshComplete();
				if( isRefresh )
					storeAndUpdateLastUpdateTime(mPullToRefreshView);
			}
		}, 200);
	}
}
