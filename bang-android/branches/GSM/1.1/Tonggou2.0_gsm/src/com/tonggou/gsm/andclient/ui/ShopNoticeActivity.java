package com.tonggou.gsm.andclient.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.ShopNotice;
import com.tonggou.gsm.andclient.ui.view.AbsEmptyViewAdapter;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy.OnLoadDataActionListener;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.ContextUtil;
import com.tonggou.gsm.andclient.util.StringUtil;

/**
 * 4S 店公告
 * @author lwz
 *
 */
public class ShopNoticeActivity extends BackableTitleBarActivity 
                implements OnLoadDataActionListener, OnItemClickListener {
    
    PullToRefreshListView mListView;
    NoticeAdapter mAdapter;
    RefreshViewLoadMoreProxy mLoadMoreProxy;
    
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_4s_notice);
        
        mListView = (PullToRefreshListView) findViewById(R.id.ptr_view);
        mAdapter = new NoticeAdapter(this, R.layout.item_list_notice);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AbsEmptyViewAdapter.OnItemClickListenerWrapper(this));
        mLoadMoreProxy = new RefreshViewLoadMoreProxy(mListView);
        mLoadMoreProxy.setOnLoadDataActionListener(this);
        mLoadMoreProxy.refreshing();
    }
    
    void initData() {
        new Handler().postDelayed(new Runnable() {
            
            @Override
            public void run() {
                if( isFinishing() ) {
                    return;
                }
                for( int i=0; i<10; i++ ) {
                    ShopNotice notice = new ShopNotice();
                    notice.setTimestamp(System.currentTimeMillis());
                    notice.setContent("Micro：第一款真正面向大众的3D打印机");
                    mAdapter.add(notice);
                }
                mLoadMoreProxy.loadDataActionComplete(false);
            }
        }, 2000);
        
    }
    
    @Override
    protected void onTitleBarCreated(SimpleTitleBar titleBar) {
        super.onTitleBarCreated(titleBar);
        titleBar.setTitle(R.string.title_4s_shop);
    }

    @Override
    public void onRefresh(int page) {
        mAdapter.clear();
        initData();
    }

    @Override
    public void onLoadMore(int page) {
        initData();
    }
    
    class NoticeAdapter extends AbsEmptyViewAdapter<ShopNotice> {

        public NoticeAdapter(Context context, int layoutRes) {
            super(context, layoutRes);
        }

        @Override
        protected void bindData(int pos, ShopNotice itemData) {
            TextView timestampText = getViewFromHolder(R.id.timestamp_text);
            TextView contentText = getViewFromHolder(R.id.content_text);
            
            timestampText.setText(StringUtil.formatDateTimeYYYYMMddHHmm(itemData.getTimestamp()));
            contentText.setText(itemData.getContent());
        }
        
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ContextUtil.startActivity(this, ShopNoticeDetailActivity.class);
	}
}
