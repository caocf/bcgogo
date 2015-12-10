package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ScrollView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.util.PreferenceUtil;

/**
 * 刷新视图（PullToRefreshView） 的刷新加载代理类
 * @author lwz
 *
 */
public class ScrollViewRefreshProxy implements OnRefreshListener<ScrollView>{
	
	public interface OnRefreshActionListener {
		public void onRefresh();
	}
	
	private PullToRefreshBase<ScrollView> mPullToRefreshView;
	private OnRefreshActionListener mOnRefreshActionListener;
	
	public ScrollViewRefreshProxy(PullToRefreshBase<ScrollView> pullToRefreshView) {
		mPullToRefreshView = pullToRefreshView;
		updateLastUpdatedLabel(mPullToRefreshView);
		mPullToRefreshView.setOnRefreshListener(this);
	}
	
	public void setOnRefreshActionListener(OnRefreshActionListener l) {
		mOnRefreshActionListener = l;
	}
	
	@Override
	public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
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
	public static void updateLastUpdatedLabel(PullToRefreshBase<? extends View> refreshView) {
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
	public static void updateLastUpdateLabel(PullToRefreshBase<? extends View>refreshView, long lastUpdateTime) {
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
	public static void storeAndUpdateLastUpdateTime(PullToRefreshBase<? extends View> refreshView) {
		CharSequence cacheKey = refreshView.getContentDescription();
		if( TextUtils.isEmpty(cacheKey) ) {
			return;
		}
		Context context = refreshView.getContext();
		long time = System.currentTimeMillis();
		updateLastUpdateLabel(refreshView, time);
		PreferenceUtil.putLong(context, Constants.PREF.PREF_NAME_OTHER_INFO, String.valueOf(cacheKey), time);
	}

	private void refresh() {
		if( mOnRefreshActionListener != null ) {
			mOnRefreshActionListener.onRefresh();
		}
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
	public void loadRefreshActionComplete() {
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mPullToRefreshView.onRefreshComplete();
				storeAndUpdateLastUpdateTime(mPullToRefreshView);
			}
		}, 200);
	}
}
