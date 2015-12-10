package com.tonggou.andclient.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshActionSlideListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.tjerkw.slideexpandable.library.AbstractSlideExpandableListAdapter.OnItemExpandCollapseListener;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView.OnActionClickListener;
import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;
import com.tonggou.andclient.FaultCodeCenterActivity;
import com.tonggou.andclient.R;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.BaseResponse;
import com.tonggou.andclient.myview.AbsCustomAlertDialog;
import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.myview.InfoDialog;
import com.tonggou.andclient.myview.ViewHolder;
import com.tonggou.andclient.network.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.ModifyVehicleFaultStatusRequest;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.RefreshViewLoadMoreProxy;
import com.tonggou.andclient.util.RefreshViewLoadMoreProxy.LOAD_MODE;
import com.tonggou.andclient.util.RefreshViewLoadMoreProxy.OnLoadDataActionListener;
import com.tonggou.andclient.vo.CarCondition;
import com.tonggou.andclient.vo.FaultCodeInfo;
import com.tonggou.andclient.vo.type.FaultCodeStatusType;

/**
 * 故障码列表页面（下拉刷新）
 * @author lwz
 *
 */
public abstract class AbsFaultListFragment extends BaseFragment implements OnLoadDataActionListener {

	protected PullToRefreshActionSlideListView mPullToRefreshActionSlideListView;
	protected ActionSlideExpandableListView mASEListView;
	protected RefreshViewLoadMoreProxy mLoadMoreProxy;
	protected CarConditionAdapter mAdapter;
	private InfoDialog mInfoDialog;
	protected Handler mPostLoadHandler;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mPostLoadHandler = new Handler();
		return inflater.inflate(R.layout.fragment_fault_code, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mPullToRefreshActionSlideListView = (PullToRefreshActionSlideListView) view.findViewById(R.id.list);
		mASEListView = mPullToRefreshActionSlideListView.getRefreshableView();
		View emptyView = View.inflate(getActivity(), R.layout.widget_fault_code_empty_view, null);
		mPullToRefreshActionSlideListView.getRefreshableViewWrapper().addView(emptyView);
		mASEListView.setEmptyView(emptyView);
		
		// 避免 ActionSlideExpandableListView 与 PullToRefreshView 冲突
//		View emptyFooter = View.inflate(getActivity(), R.layout.widget_empty_footer_for_slide_list_view, null);
//		mASEListView.addFooterView(emptyFooter, null, false);
		
		afterViews();
	}
	
	protected void afterViews() {
		mAdapter = new CarConditionAdapter(getActivity(), new ArrayList<CarCondition>(), R.layout.item_fault_list);
		mPullToRefreshActionSlideListView.setAdapter(mAdapter);
		mLoadMoreProxy = new RefreshViewLoadMoreProxy(mPullToRefreshActionSlideListView, 1, INFO.ITEMS_PER_PAGE);
		mLoadMoreProxy.setOnLoadDataActionListener(this);
		if( !TongGouApplication.getInstance().isLogin() ) {
			mPullToRefreshActionSlideListView.setMode(Mode.DISABLED);
			return;
		}
		setListener();
		// 若已经加载过数据，就直接显示缓存数据
		if( getAttachActivity().hasCacheData(getTag()) ) {
			mAdapter.update( getAttachActivity().getCacheData(getTag()) );
			
		} else {
			postRefresh();
		}
	}
	
	protected void postRefresh() {
		// 若不延时就会没有效果
		mPostLoadHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// 只有设置了 Mode.PULL_FROM_START 在调用setRefreshing() 就会调用刷新回调
				mPullToRefreshActionSlideListView.setMode(Mode.PULL_FROM_START);
				mPullToRefreshActionSlideListView.setRefreshing();
			}
		}, 500);
	}
	
	public FaultCodeCenterActivity getAttachActivity() {
		return (FaultCodeCenterActivity)getActivity();
	}
	
	public boolean isLostActivity() {
		return getActivity() == null || getActivity().isFinishing();
	}
	
	private void setListener() {
		// 可隐藏的工具栏的两个按钮的监听
		mASEListView.setItemActionListener(new OnActionClickListener() {
			
			@Override
			public void onClick(View itemView, View clickedView, int position) {
				CarCondition itemData = mAdapter.getData().get(position);
				if( clickedView.getId() == R.id.handled_btn ) {
					onHandledAction(itemData);
					
				} else {
					String info = itemData.getFaultCodeInfo().getBackgroundInfo();
					showBackgroundInfoDialog(info);
				}
			}
		}, R.id.handled_btn, R.id.info_btn);
		
		// 箭头指示器监听
		final SlideExpandableListAdapter adapter = (SlideExpandableListAdapter)((HeaderViewListAdapter)mASEListView.getAdapter()).getWrappedAdapter();
		adapter.setItemExpandCollapseListener(new OnItemExpandCollapseListener() {
			
			@Override
			public void onExpand(View itemView, int position) {
				setArrowIndicatorDrection(itemView, false);
			}
			
			@Override
			public void onCollapse(View itemView, int position) {
				setArrowIndicatorDrection(itemView, true);
			}
			
			/**
			 * 设置 箭头指示器的方向
			 * @param itemView
			 * @param isDown 是否向下
			 */
			private void setArrowIndicatorDrection(View itemView, boolean isDown) {
				View listItemView = (View)itemView.getTag();
				ViewHolder.getView(listItemView, R.id.expand_collapse_indicator).setEnabled(isDown);
			}
		});
		
	}
	
	/**
	 * 
	 * @param faultCodeId		can be null, 若为 null 则说明是本地故障，不为 null 则说明是 历史故障
	 * @param faultCode			故障码
	 * @param currentStatus		当前状态
	 * @param destStatus		目标状态
	 * @param vehicleId			发生故障的车辆 id
	 */
	protected void modifyFaultCodeStatusRequest(final String faultCodeId, final String faultCode, FaultCodeStatusType currentStatus, FaultCodeStatusType destStatus, final String vehicleId) {
		ModifyVehicleFaultStatusRequest request = new ModifyVehicleFaultStatusRequest();
		request.setRequestParams(faultCodeId, faultCode, currentStatus, destStatus, vehicleId);
		request.doRequest(getActivity(), new AsyncJsonBaseResponseParseHandler<BaseResponse>() {
			
			@Override
			public void onStart() {
				super.onStart();
				getBaseActivity().showLoadingDialog("正在处理中...");
			}
			
			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				onHandleSuccess(faultCodeId, faultCode, vehicleId);
				TongGouApplication.showToast("操作成功");
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				getBaseActivity().dismissLoadingDialog();
			}

			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
		});
	}
	
	abstract void updateData(final int page, final LOAD_MODE mode);
	
	abstract void onHandleSuccess(final String faultCodeId, final String faultCode, final String vehicleId);
	
	/**
	 * 显示背景知识对话框
	 * @param info
	 */
	private void showBackgroundInfoDialog(String info) {
		dismissBackgroundInfoDialog();
		mInfoDialog = new InfoDialog(getActivity());
		mInfoDialog.showDialog(info);
	}
	
	/**
	 * 卸载背景知识对话框
	 */
	private void dismissBackgroundInfoDialog() {
		AbsCustomAlertDialog.dismissDialog(mInfoDialog);
	}
	
	@Override
	public void onStop() {
		if( mPostLoadHandler != null )
			mPostLoadHandler.removeCallbacksAndMessages(null);
		super.onStop();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		dismissBackgroundInfoDialog();
	}
	
	class CarConditionAdapter extends AbsViewHolderAdapter<CarCondition> {

		public CarConditionAdapter(Context context, List<CarCondition> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, CarCondition itemData) {
			TextView faultCodeText = (TextView) getViewFromHolder(convertView, R.id.fault_code);
			TextView faultDescText = (TextView) getViewFromHolder(convertView, R.id.fault_desc);
			TextView faultCatsText = (TextView) getViewFromHolder(convertView, R.id.fault_category);
			TextView reportTimeText = (TextView) getViewFromHolder(convertView, R.id.report_time);
			ImageButton handledBtn = (ImageButton) getViewFromHolder(convertView, R.id.handled_btn);
			
			handledBtn.setImageResource(getHandledButtonImageResource());
			String reportDate = "未知";
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
				reportDate = sdf.format(new Date(Long.valueOf(itemData.getReportTime())));
			} catch (NumberFormatException e) {
			}
			reportTimeText.setText(reportDate);
			FaultCodeInfo info = itemData.getFaultCodeInfo();
			faultCodeText.setText( info.getFaultCode() );
			faultDescText.setText( info.getDescription() );
			faultCatsText.setText( info.getCategory() );
			
		}
		
		@Override
		public synchronized void update(Collection<? extends CarCondition> newData) {
			if( isLostActivity() ) {
				return;
			}
			super.update(newData);
			getAttachActivity().cacheData(getTag(), getData());
		}
		
		@Override
		public synchronized void append(Collection<? extends CarCondition> appendData) {
			if( isLostActivity() ) {
				return;
			}
			super.append(appendData);
			getAttachActivity().cacheData(getTag(), getData());
		}
	}
	
	abstract int getHandledButtonImageResource();
	
	/**
	 * 操作处理动作
	 */
	abstract void onHandledAction(CarCondition itemData);
	
}
