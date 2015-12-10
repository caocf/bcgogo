package com.tonggou.gsm.andclient.ui.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.DTCInfo;
import com.tonggou.gsm.andclient.bean.Pager;
import com.tonggou.gsm.andclient.bean.type.DTCStatus;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.ModifyDTCStatusRequest;
import com.tonggou.gsm.andclient.net.request.QueryDTCRequest;
import com.tonggou.gsm.andclient.net.response.BaseResponse;
import com.tonggou.gsm.andclient.net.response.QueryDTCResponse;
import com.tonggou.gsm.andclient.ui.DTCManagerActivity;
import com.tonggou.gsm.andclient.ui.view.AbsEmptyViewAdapter;
import com.tonggou.gsm.andclient.ui.view.DTCKnowledgeDialog;
import com.tonggou.gsm.andclient.ui.view.IndicatorTextView;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy.OnLoadDataActionListener;
import com.tonggou.gsm.andclient.util.StringUtil;

/**
 * 故障列表
 * @author lwz
 *
 */
public abstract class DTCListFragment extends AbsPullToRefreshLazyLoadFragment implements OnLoadDataActionListener {
	
	DTCAdapter mAdapter;
	DTCKnowledgeDialog mKnowledgeDialog;
	Pager mPager;
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mPager = new Pager();
		mPager.setCurrentPage(1);
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	int getLayoutRes() {
		return R.layout.fragment_dtc_list;
	}
	
	@Override
	int getRefreshViewId() {
		return R.id.ptr_view;
	}

	@Override
	AbsEmptyViewAdapter<?> createAdapter() {
		return mAdapter = new DTCAdapter(mActivity, R.layout.item_list_dtc);
	}
	
	@Override
	public void onRefresh(int page) {
		mPager.setCurrentPage(1);
		doQueryDTC(1, true);
	}

	@Override
	public void onLoadMore(int page) {
		doQueryDTC(mPager.getCurrentPage() + 1, false);
	}
	
	public ArrayList<DTCInfo> getData() {
		return (ArrayList<DTCInfo>) mAdapter.getData();
	}
	
	public void doQueryDTC(final int pageNo, final boolean isRefresh) {
		QueryDTCRequest request = new QueryDTCRequest();
		request.setRequestParams(pageNo, getDTCQueryStatus());
		request.doRequest(mActivity, new AsyncJsonBaseResponseParseHandler<QueryDTCResponse>() {

			@Override
			public void onParseSuccess(QueryDTCResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				ArrayList<DTCInfo> data = result.getResult();
				if( isRefresh ) {
					showAppointmentButton(data.isEmpty());
					mAdapter.update(data);
				} else {
					if( !data.isEmpty() ) {
						mAdapter.append(data);
					} else {
						App.showShortToast(getString(R.string.info_no_more_data));
					}
				}
				mPager = result.getPager();
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				mLoadMoreProxy.loadDataActionComplete(isRefresh);
			}
			
			@Override
			public Class<QueryDTCResponse> getTypeClass() {
				return QueryDTCResponse.class;
			}
		});
	}
	
	private void showAppointmentButton(boolean isDataEmpty) {
		if( this instanceof CurrentDTCListFragment ) {
			if( isDataEmpty ) {
				((DTCManagerActivity)mActivity).hideAppointmentButton();
			} else {
				((DTCManagerActivity)mActivity).showAppointmentButton();
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		DTCKnowledgeDialog.dismissDialog(mKnowledgeDialog);
	}
	
	class DTCAdapter extends AbsEmptyViewAdapter<DTCInfo> {

		static final String EMPTY_INDICATOR = "--";
		
		public DTCAdapter(Context context, int layoutRes) {
			super(context, layoutRes);
		}

		@Override
		protected void bindData(int pos, final DTCInfo itemData) {
			IndicatorTextView dtcCodeIndText = getViewFromHolder(R.id.dtc_code_indicator_text);
			IndicatorTextView dtcTypeIndText = getViewFromHolder(R.id.dtc_type_indicator_text);
			IndicatorTextView dtcDescIndText = getViewFromHolder(R.id.dtc_desc_indicator_text);
			TextView timestameText = getViewFromHolder(R.id.timestamp_text);
			Button handleBtn = getViewFromHolder(R.id.handled_btn);
			Button backgroundKnowledgeBtn = getViewFromHolder(R.id.background_knowledge_btn);
			
			dtcCodeIndText.setTextValue(itemData.getErrorCode());
			String cats = itemData.getCategory();
			dtcTypeIndText.setTextValue(TextUtils.isEmpty(cats) ? EMPTY_INDICATOR : cats);
			String content = itemData.getContent();
			dtcDescIndText.setTextValue(TextUtils.isEmpty(content) ? EMPTY_INDICATOR : content);
			timestameText.setText(StringUtil.formatDateYYYYMMdd(itemData.getReportTime()));
			handleBtn.setText( getString(getHandlerBtnTextRes()));
			handleBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onHandlerBtnClick(itemData);
				}
			});
			
			// 没有背景知识就不让点击
			backgroundKnowledgeBtn.setEnabled( !TextUtils.isEmpty(itemData.getBackgroundInfo()) );
			backgroundKnowledgeBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showKnowledgeDialog(itemData.getErrorCode(), itemData.getBackgroundInfo());
				}
			});
		}
	}
	
	public void showKnowledgeDialog(String title, String msg) {
		if( mKnowledgeDialog == null ) {
			mKnowledgeDialog = new DTCKnowledgeDialog(getActivity());
		}
		mKnowledgeDialog.showDialog(title, msg);
	}
	
	abstract int getHandlerBtnTextRes();
	
	abstract DTCStatus getDTCQueryStatus();
	
	void onHandlerBtnClick(final DTCInfo itemData) {
		ModifyDTCStatusRequest request = new ModifyDTCStatusRequest();
		request.setRequestParams(itemData.getId(), itemData.getErrorCode(), 
				DTCStatus.valueOf(itemData.getStatus()), getHandledDTCStatus(), itemData.getAppVehicleId());
		request.doRequest(mActivity, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				App.showShortToast(result.getMessage());
				getData().remove(itemData);
				showAppointmentButton(getData().isEmpty());
				mAdapter.notifyDataSetChanged();
			}
			
			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
		});
	}
	
	abstract DTCStatus getHandledDTCStatus();

}
