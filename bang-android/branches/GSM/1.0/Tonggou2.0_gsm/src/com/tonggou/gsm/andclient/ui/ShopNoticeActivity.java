package com.tonggou.gsm.andclient.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.image.SmartImageView;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.bean.Pager;
import com.tonggou.gsm.andclient.bean.ShopNotice;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.QueryShopNoticeRequest;
import com.tonggou.gsm.andclient.net.response.QueryShopNoticeResponse;
import com.tonggou.gsm.andclient.ui.view.AbsEmptyViewAdapter;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy.OnLoadDataActionListener;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.ContextUtil;
import com.tonggou.gsm.andclient.util.PreferenceUtil;
import com.tonggou.gsm.andclient.util.StringUtil;

/**
 * 4S 店公告
 * @author lwz
 *
 */
public class ShopNoticeActivity extends BackableTitleBarActivity 
                implements OnLoadDataActionListener, OnItemClickListener {
    
    PullToRefreshListView mRefreshListView;
    NoticeAdapter mAdapter;
    RefreshViewLoadMoreProxy mLoadMoreProxy;
    Pager mPager = new Pager();
    
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_4s_notice);
        
        mRefreshListView = (PullToRefreshListView) findViewById(R.id.ptr_view);
        mRefreshListView.getRefreshableView().setSelector(R.drawable.selector_list_item_holo_blue);
        mRefreshListView.setOnItemClickListener(new AbsEmptyViewAdapter.OnItemClickListenerWrapper(this));
        mAdapter = new NoticeAdapter(this, R.layout.item_list_notice);
        mRefreshListView.setAdapter(mAdapter);
        
        mLoadMoreProxy = new RefreshViewLoadMoreProxy(mRefreshListView, Mode.PULL_FROM_START);
        mLoadMoreProxy.setFromStartLabel(R.string.txt_pull_to_load_old_notice, R.string.txt_release_to_load_old_notice);
        mLoadMoreProxy.setOnLoadDataActionListener(this);
        
        ArrayList<ShopNotice> cacheData = restoreCachedData();
        if( !cacheData.isEmpty() ) {
        	updateData(cacheData);
        	requestData(1);
        } else {
    		mPager.setCurrentPage(0);
    		mPager.setHasNextPage(true);
    		mLoadMoreProxy.refreshing();
        }
    }
    
    private ArrayList<ShopNotice> restoreCachedData() {
    	String cacheJsonStr = PreferenceUtil.getString(this,
    			Constants.PREF.PREF_NAME_OTHER_INFO, createPersonalPrefKey());
    	if( TextUtils.isEmpty(cacheJsonStr) ) {
    		return new ArrayList<ShopNotice>();
    	}
    	return new Gson().fromJson(cacheJsonStr, new TypeToken<ArrayList<ShopNotice>>(){}.getType());
    }
    
    private void storeCachedData(ArrayList<ShopNotice> data) {
    	PreferenceUtil.putString(this, Constants.PREF.PREF_NAME_OTHER_INFO,
    			createPersonalPrefKey(), new Gson().toJson(data));
    }
    
    private String createPersonalPrefKey() {
		return Constants.PREF.PREF_KEY_SHOP_NOTICE_JSON_STR + "_" + UserBaseInfo.getUserInfo().getUserNo();
	}
    
    private void requestData(final int pageNo) {
    	QueryShopNoticeRequest request = new QueryShopNoticeRequest();
    	request.setApiParams(pageNo);
    	request.doRequest(this, new AsyncJsonBaseResponseParseHandler<QueryShopNoticeResponse>() {

    		@Override
    		public void onParseSuccess(QueryShopNoticeResponse result, String originResult) {
    			super.onParseSuccess(result, originResult);
    			mPager = result.getPager();
    			ArrayList<ShopNotice> data = result.getAdvertDTOList();
    			if( data != null ) {
    				Collections.reverse(data);
    			}
    			updateData(data);
    		}
    		
    		@Override
    		public void onFinish() {
    			super.onFinish();
    			mLoadMoreProxy.loadDataActionComplete(false);
    		}
    		
			@Override
			public Class<QueryShopNoticeResponse> getTypeClass() {
				return QueryShopNoticeResponse.class;
			}
		});
    }
    
    private void updateData(ArrayList<ShopNotice> data) {
		if( data != null && !data.isEmpty() ) {
			if( mPager != null && mPager.getCurrentPage() == 1 ) {
				storeCachedData(data);
	    		mAdapter.update(data);
	    	} else {
	    		mAdapter.appendFromStart(data);
	    	}
			mRefreshListView.getRefreshableView().setSelectionFromTop(data.size(), mRefreshListView.getHeaderSize());
		}
    }
    
    @Override
    protected void onTitleBarCreated(SimpleTitleBar titleBar) {
        super.onTitleBarCreated(titleBar);
        titleBar.setTitle(R.string.title_4s_shop);
    }

    @Override
    public void onRefresh(int page) {
    	// 这里虽然是刷新的回调，但是是用来加载更多。
    	if( mPager.isHasNextPage() ) {
    		requestData( mPager.getCurrentPage() + 1);
    	} else {
    		App.showShortToast(getString(R.string.no_more_data));
    		mLoadMoreProxy.loadDataActionComplete(false);
    	}
    }

    @Override
    public void onLoadMore(int page) {
    	// do nothing
    }
    
    @Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final int pos = position - mRefreshListView.getRefreshableView().getHeaderViewsCount();
		Bundle args = new Bundle();
		args.putParcelable(ShopNoticeDetailActivity.EXTRA_SHOP_NOTICE, mAdapter.getData().get(pos));
		ContextUtil.startActivity(this, ShopNoticeDetailActivity.class, args);
	}
    
    class NoticeAdapter extends AbsEmptyViewAdapter<ShopNotice> {

        public NoticeAdapter(Context context, int layoutRes) {
            super(context, layoutRes);
        }

        @Override
        protected void bindData(int pos, ShopNotice itemData) {
        	SmartImageView img = getViewFromHolder(R.id.smart_image);
        	TextView timestampText = getViewFromHolder(R.id.timestamp_text);
        	TextView contentText = getViewFromHolder(R.id.content_text);
        	
        	img.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
        	img.setImageUrl(itemData.getImgUrl());
        	timestampText.setText(StringUtil.formatDateTimeYYYYMMddHHmm(itemData.getTimestamp()));
        	contentText.setText(itemData.getTitle());
        }
        
		public synchronized void appendFromStart(Collection<? extends ShopNotice> appendData) {
			if( appendData == null || appendData.isEmpty() ) {
				return;
			}
			getData().addAll(0, appendData);
			notifyDataSetChanged();
		}
		
    }

}
