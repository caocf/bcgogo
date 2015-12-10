package com.tonggou.andclient;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.image.SmartImageView;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.SearchServiceResponse;
import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.myview.SimpleTitleBar;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.network.parser.AsyncLoadCacheJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.QueryServiceHistoryListRequest;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.NetworkUtil;
import com.tonggou.andclient.util.RefreshViewLoadMoreProxy;
import com.tonggou.andclient.util.RefreshViewLoadMoreProxy.LOAD_MODE;
import com.tonggou.andclient.util.RefreshViewLoadMoreProxy.OnLoadDataActionListener;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.Pager;
import com.tonggou.andclient.vo.TGService;
/**
 * 服务查询页面
 * @author lwz
 *
 */
public class SearchServiceActivity extends AbsBackableActivity 
							implements OnLoadDataActionListener, OnItemClickListener {

	private PullToRefreshListView mPullToRefreshListView;
	private RefreshViewLoadMoreProxy mLoadMoreProxy;
	private TGServiceAdapter mAdapter;
	private Pager mCurrentPager;
	protected Handler mPostLoadHandler;
	
	@Override
	protected int getContentLayout() {
		return R.layout.search_service;
	}
	
	@Override
	protected void afterTitleBarCreated(SimpleTitleBar titleBar,
			Bundle savedInstanceState) {
		super.afterTitleBarCreated(titleBar, savedInstanceState);
		titleBar.setRightImageButton(R.drawable.searching_obd_refresh, android.R.color.transparent);
		titleBar.setTitle(R.string.service_title);
		titleBar.setOnRightButtonClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCurrentPager.setCurrentPage(1);
				postRefresh();
			}
		});
	}
	
	@Override
	protected void findViews(Bundle savedInstanceState) {
		super.findViews(savedInstanceState);
		
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
	}
	
	@Override
	protected void afterViews() {
		super.afterViews();
		mPostLoadHandler = new Handler();
		mLoadMoreProxy = new RefreshViewLoadMoreProxy(mPullToRefreshListView, 0, INFO.ITEMS_PER_PAGE);
		mAdapter = new TGServiceAdapter(this, new ArrayList<TGService>(), R.layout.search_service_item);
		mPullToRefreshListView.setAdapter(mAdapter);
		mPullToRefreshListView.getRefreshableView().setOnItemClickListener(this);
		mLoadMoreProxy.setOnLoadDataActionListener(this);
		
		mCurrentPager = new Pager();
		mCurrentPager.setHasNextPage(true);
		mCurrentPager.setCurrentPage(0);
		
		postRefresh();
	}
	
	private void postRefresh() {
		// 若不延时就会没有效果
		mPostLoadHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// 只有设置了 Mode.PULL_FROM_START 在调用setRefreshing() 就会调用刷新回调
				mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
				mPullToRefreshListView.setRefreshing();
			}
		}, 500);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if( !NetworkUtil.isNetworkConnected(getBaseContext()) ) {
			TongGouApplication.showToast(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE);
			return;
		}
		try {
			Intent intent=new Intent(SearchServiceActivity.this,OrderDetilActivity.class);
			intent.putExtra("tonggou.server.orderid", mAdapter.getData().get(position).getOrderId());
			intent.putExtra("tonggou.server.from", "SearchServiceActivity");
			startActivityForResult(intent, 6060);
		} catch ( IndexOutOfBoundsException e ) {
			TongGouApplication.showToast(getString(R.string.please_try_again));
			return;
		}
	}

	public void updateData(final int pageNo, final LOAD_MODE mode) {
		final QueryServiceHistoryListRequest request = new QueryServiceHistoryListRequest();
		request.setApiParams(pageNo, INFO.ITEMS_PER_PAGE);
		request.doRequest(this, new AsyncLoadCacheJsonBaseResponseParseHandler<SearchServiceResponse>() {

			@Override
			public void onLoadCache(SearchServiceResponse result, String originResult,
					boolean isNetworkConnected) {
				if( result != null && mode == LOAD_MODE.REFRESH_LOAD_CACHE) { 
					mAdapter.update( result.getResults() );
					
				} else if( !isNetworkConnected ) {
					TongGouApplication.showToast(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE);
				}
			}
			
			@Override
			public void onParseSuccess(SearchServiceResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				mCurrentPager = result.getPager();
				if( mode != LOAD_MODE.LOAD_MORE ) {
					mAdapter.update( result.getResults() );
				} else {
					mAdapter.append( result.getResults() );
				}
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				mLoadMoreProxy.loadDataActionComplete();
				mPullToRefreshListView.setMode(Mode.BOTH);
			}
			
			@Override
			public Class<SearchServiceResponse> getTypeClass() {
				return SearchServiceResponse.class;
			}
			
			@Override
			public String getCacheKey() {
				return request.getOriginApi();
			}
		
		});
		
	}
	
	@Override
	public void onRefresh(int page, int dataCount) {
		updateData( 1, 
				mCurrentPager.getCurrentPage() == 0 ? LOAD_MODE.REFRESH_LOAD_CACHE : LOAD_MODE.REFRESH_NOT_LOAD_CACHE );
	}
	
	@Override
	public void onLoadMore(int page, int dataCount) {
		if( mCurrentPager.isHasNextPage() ) {
			updateData( mCurrentPager.getCurrentPage() + 1, 
					// 初始化时 page=0， 区分缓存数据
					(mCurrentPager.getCurrentPage() == 0 || false) ? LOAD_MODE.REFRESH_NOT_LOAD_CACHE : LOAD_MODE.LOAD_MORE);
		} else {
			mPostLoadHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					TongGouApplication.showToast(getString(R.string.nextpage_nodata));
					mLoadMoreProxy.loadDataActionComplete();
				}
			}, 1000);
		}
	}
	
	class TGServiceAdapter extends AbsViewHolderAdapter<TGService> {

		public TGServiceAdapter(Context context, List<TGService> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, TGService itemData) {
			TextView name = getViewFromHolder(convertView, R.id.shoplistname);
			TextView num = getViewFromHolder(convertView, R.id.shoplistnum_tv);
			TextView state = getViewFromHolder(convertView, R.id.shopliststate_tv);
			TextView service = getViewFromHolder(convertView, R.id.shoplistservice_tv);
			TextView time = getViewFromHolder(convertView, R.id.shoplisttime);
			SmartImageView shoplistpicView = getViewFromHolder(convertView, R.id.shoplistpicView);
			
			shoplistpicView.setImageUrl(itemData.getShopImageUrl());
			name.setText(itemData.getShopName());
			num.setText(itemData.getShopId());
			state.setText(itemData.getStatus());
			service.setText(itemData.getOrderType());
			time.setText(SomeUtil.longToStringDate(itemData.getOrderTime()));
		}
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==6060){		
			//请求网络
			if("yes".equals(data.getStringExtra("tonggou.isOk"))){		
				onRefresh(1, -1);
			}
		}
	}
}
