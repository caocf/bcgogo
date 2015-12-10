package com.tonggou.andclient;


import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshActionSlideListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tonggou.andclient.StoreDetilActivity.EvaluateAdapter;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.ShopEvaluateResponse;
import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.network.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.ShopEvaluateRequest;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.RefreshViewLoadMoreProxy;
import com.tonggou.andclient.util.RefreshViewLoadMoreProxy.OnLoadDataActionListener;
import com.tonggou.andclient.vo.AppShopCommentDTO;
import com.tonggou.andclient.vo.CarCondition;
import com.tonggou.andclient.vo.Pager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 评价详情
 * @author fbl
 */
public class ShopEvaluateActivity extends BaseActivity implements OnLoadDataActionListener{
	private PullToRefreshListView mPullToRefreshListView;
	private RefreshViewLoadMoreProxy mLoadMoreProxy;
	private ShopEvaluateAdapter mShopEvaluateAdapter;
	private Handler evaluateHandler;

	private ImageView backImageView;
	private Pager mPager; 
	private String shopId;
    private ImageView lView1,lView2,lView3,lView4,lView5;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.storeevaluatemore);

		evaluateHandler = new Handler();
		backImageView = (ImageView) findViewById(R.id.back);
		mPullToRefreshListView = 
				(PullToRefreshListView)findViewById(R.id.moreEvaluate_List);

		shopId = getIntent().getStringExtra("mShopid");

		afterViews();
		setListener();

	}


	protected void afterViews() {
		mPager = new Pager();
		mPager.setCurrentPage(1);
		mPager.setHasNextPage(true);

		mShopEvaluateAdapter = new ShopEvaluateAdapter(this,new ArrayList<AppShopCommentDTO>(), R.layout.storedetil_evaluate_item);
		mPullToRefreshListView.setAdapter(mShopEvaluateAdapter);
		mLoadMoreProxy = new RefreshViewLoadMoreProxy(mPullToRefreshListView, 1, INFO.ITEMS_PER_PAGE);
		mLoadMoreProxy.setOnLoadDataActionListener(this);

	}


	private void setListener() {
		backImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ShopEvaluateActivity.this.finish();
			}
		});

		// 若不延时就会没有效果
		evaluateHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mPullToRefreshListView.setRefreshing(true);
				updateData(true, 1);
			}
		}, 500);

	}


	@Override
	public void onRefresh(int page, int dataCount) {
		updateData(true, 1);

	}


	@Override
	public void onLoadMore(int page, int dataCount) {
		if( mPager.isHasNextPage() ) {
			updateData(false, mPager.getCurrentPage() + 1); 
		} else {
			evaluateHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					TongGouApplication.showToast("已经是最后一页");
					mLoadMoreProxy.loadDataActionComplete();
				}
			}, 1000);
		}
	}

	protected void updateData(final boolean isRefresh, int page)  {
		ShopEvaluateRequest request = new ShopEvaluateRequest();
		request.setApiParams(shopId, page, INFO.ITEMS_PER_PAGE);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<ShopEvaluateResponse>() {

			//			返回成功
			@Override
			public void onParseSuccess(ShopEvaluateResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				TongGouApplication.showLog("result====="+result.toString());
				mPager = result.getPager();

				if( isRefresh ) {
					mShopEvaluateAdapter.update(result.getResults());
				} else {
					mShopEvaluateAdapter.append(result.getResults());
				}
			}
			public Class<ShopEvaluateResponse> getTypeClass() {
				return ShopEvaluateResponse.class;
			}
			
			public void onFinish() {
				super.onFinish();
				mLoadMoreProxy.loadDataActionComplete();
			}

		});
		
		
	}



	@Override
	public void onStop() {
		if( evaluateHandler != null )
			evaluateHandler.removeCallbacksAndMessages(null);
		super.onStop();
	}

	class ShopEvaluateAdapter extends AbsViewHolderAdapter<AppShopCommentDTO>{

		public ShopEvaluateAdapter(Context context,
				List<AppShopCommentDTO> data, int layoutRes) {
			super(context, data, layoutRes);

		}

		@Override
		protected void setData(int pos, View convertView,
				AppShopCommentDTO itemData) {

			TextView userNameText = getViewFromHolder(convertView, R.id.userName_text);
			TextView timeText = getViewFromHolder(convertView, R.id.time_text);
			TextView shoplistlikesorceText = getViewFromHolder(convertView, R.id.pjshoplistlikesorce);
			TextView evaluateText = (TextView)convertView.findViewById(R.id.evaluate_content_text);

			userNameText.setText(itemData.getCommentatorName());
			timeText.setText(itemData.getCommentTimeStr());
			evaluateText.setText(itemData.getCommentContent());

			lView1 = getViewFromHolder(convertView,R.id.shoplistlike11);
			lView2 = getViewFromHolder(convertView,R.id.shoplistlike21);
			lView3 = getViewFromHolder(convertView,R.id.shoplistlike31);
			lView4 = getViewFromHolder(convertView,R.id.shoplistlike41);
			lView5 = getViewFromHolder(convertView,R.id.shoplistlike51);

			if(itemData.getCommentScore() != 0){
				shoplistlikesorceText.setText(itemData.getCommentScore()+"分");
				lView1.setVisibility(View.VISIBLE);
				lView2.setVisibility(View.VISIBLE);
				lView3.setVisibility(View.VISIBLE);
				lView4.setVisibility(View.VISIBLE);
				lView5.setVisibility(View.VISIBLE);
				setLikes(itemData.getCommentScore()+"",lView1,lView2,lView3,lView4,lView5);
			}else{
				lView1.setVisibility(View.GONE);
				lView2.setVisibility(View.GONE);
				lView3.setVisibility(View.GONE);
				lView4.setVisibility(View.GONE);
				lView5.setVisibility(View.GONE);
				shoplistlikesorceText.setText("暂无评分");
			}




		}

	}

}
