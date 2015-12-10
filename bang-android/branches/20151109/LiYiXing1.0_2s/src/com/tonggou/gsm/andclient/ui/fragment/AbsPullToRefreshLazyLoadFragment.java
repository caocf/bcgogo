package com.tonggou.gsm.andclient.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.ui.view.AbsEmptyViewAdapter;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy.OnLoadDataActionListener;

public abstract class AbsPullToRefreshLazyLoadFragment extends BaseFragment implements OnLoadDataActionListener, OnItemClickListener {

	private static final String EXTRA_IS_LOAD_DATA = "extra_is_load_data";

	public static AbsPullToRefreshLazyLoadFragment newInstance(Class<? extends AbsPullToRefreshLazyLoadFragment> fragmentClass, boolean isLoadData) {
		try {
			Bundle args = new Bundle();
			args.putBoolean(EXTRA_IS_LOAD_DATA, isLoadData);
			AbsPullToRefreshLazyLoadFragment fragment = fragmentClass.newInstance();
			fragment.setArguments(args);
			return fragment;
		} catch (java.lang.InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	boolean isDataLoaded = false;
	PullToRefreshListView mPullToRefreshListView;
	RefreshViewLoadMoreProxy mLoadMoreProxy;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(getLayoutRes(), container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mPullToRefreshListView = findViewById(getRefreshViewId());
		mPullToRefreshListView.setAdapter(createAdapter());
		mPullToRefreshListView.getRefreshableView().setOnItemClickListener(
				new AbsEmptyViewAdapter.OnItemClickListenerWrapper(this));
		mLoadMoreProxy = new RefreshViewLoadMoreProxy(mPullToRefreshListView, Mode.BOTH);
		mLoadMoreProxy.setOnLoadDataActionListener(this);

		isDataLoaded = getArguments().getBoolean(EXTRA_IS_LOAD_DATA, false);
		if( isDataLoaded ) {
			mLoadMoreProxy.refreshing();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		App.debug(TAG,	mPullToRefreshListView.getRefreshableView().getHeaderViewsCount() + "  " + position );
	}

	abstract int getLayoutRes();

	abstract int getRefreshViewId();

	abstract AbsEmptyViewAdapter<?> createAdapter();

	public void startLoadData() {
		if( !isDataLoaded ) {
			mLoadMoreProxy.refreshing();
		}
		isDataLoaded = true;
	}

}
