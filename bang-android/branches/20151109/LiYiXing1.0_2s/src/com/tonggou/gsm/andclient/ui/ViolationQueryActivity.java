package com.tonggou.gsm.andclient.ui;

import java.util.ArrayList;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.bean.AppVehicleDTO;
import com.tonggou.gsm.andclient.bean.ViolationRecord;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.QueryViolationRequest;
import com.tonggou.gsm.andclient.net.response.QueryViolationResponse;
import com.tonggou.gsm.andclient.ui.view.AbsEmptyViewAdapter;
import com.tonggou.gsm.andclient.ui.view.IndicatorTextView;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy.OnLoadDataActionListener;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.ContextUtil;

/**
 * 违章查询 Activity
 * @author lwz
 *
 */
public class ViolationQueryActivity extends BackableTitleBarActivity implements OnLoadDataActionListener {

	private final int REQUEST_CODE_VIOLDATION_SETTING = 0x112;

	private PullToRefreshListView mRefreshListView;
	private RefreshViewLoadMoreProxy mLoadMoreProxy;
	private ViolationAdapter mAdapter;
	private TextView mViolationCountText;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_violation_query);

		((TextView) findViewById(R.id.vehicle_no_text)).setText("\t" + UserBaseInfo.getVehicleInfo().getVehicleNo());
		mViolationCountText = (TextView) findViewById(R.id.violation_count_text);
		mRefreshListView = (PullToRefreshListView) findViewById(R.id.ptr_view);
	mRefreshListView.getRefreshableView().setDividerHeight(getResources().getDimensionPixelOffset(R.dimen.dimen_10dp));
	mRefreshListView.getRefreshableView().setSelector(new ColorDrawable(Color.TRANSPARENT));
		mAdapter = new ViolationAdapter(this, R.layout.item_list_violation_record);
		mRefreshListView.setAdapter(mAdapter);

		updateViolationCount(0);

		mLoadMoreProxy = new RefreshViewLoadMoreProxy(mRefreshListView, Mode.PULL_FROM_START);
		mLoadMoreProxy.setOnLoadDataActionListener(this);
		mLoadMoreProxy.refreshing();
	}

    @Override
    protected void onTitleBarCreated(SimpleTitleBar titleBar) {
	super.onTitleBarCreated(titleBar);
	titleBar.setTitle(R.string.title_violation_query);
	titleBar.setRightButton(getString(R.string.subtitle_violation_query_setting), Color.TRANSPARENT);
	titleBar.setOnRightButtonClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				toViolationQuerySettingActivity();
			}
		});
    }

    private void toViolationQuerySettingActivity() {
	ContextUtil.startActivityForResult(this, ViolationQuerySettingActivity.class, REQUEST_CODE_VIOLDATION_SETTING);
    }

    private void updateViolationCount(int count) {
	mViolationCountText.setText(getString(R.string.txt_format_violation_count, count));
    }

	private void requestData() {
		AppVehicleDTO vehicleInfo = UserBaseInfo.getVehicleInfo();
		if( !vehicleInfo.isCanQueryViolation() ) {
			toViolationQuerySettingActivity();
			return;
		}
		QueryViolationRequest request = new QueryViolationRequest();
		request.setApiParams(vehicleInfo.getJuheCityCode(),
				vehicleInfo.getVehicleNo(), vehicleInfo.getEngineNo(),
				vehicleInfo.getVehicleVin(), vehicleInfo.getRegistNo());
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<QueryViolationResponse>() {

			@Override
			public void onParseSuccess(QueryViolationResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				ArrayList<ViolationRecord> data = result.getLists();
				if( data == null ) {
					data = new ArrayList<ViolationRecord>();
				}
				if( data.isEmpty() ) {
					App.showShortToast(result.getMessage());
				}
				updateViolationCount(data.size());
				mAdapter.update(data);
			}

			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
				super.onParseFailure(errorCode, errorMsg);
				mAdapter.clear();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
				mAdapter.clear();
				if(statusCode == 0) {
					App.showShortToast(getString(R.string.txt_info_query_timeout));
				} else {
					super.onFailure(statusCode, headers, responseBody, error);
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				mLoadMoreProxy.loadDataActionComplete(true);
			}

			@Override
			public Class<QueryViolationResponse> getTypeClass() {
				return QueryViolationResponse.class;
			}

		});
	}

	@Override
	public void onRefresh(int page) {
		requestData();
	}

	@Override
	public void onLoadMore(int page) {
		// nothing
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( REQUEST_CODE_VIOLDATION_SETTING == requestCode && resultCode == RESULT_OK )
			mLoadMoreProxy.refreshing();
	}

    class ViolationAdapter extends AbsEmptyViewAdapter<ViolationRecord> {

		public ViolationAdapter(Context context, int layoutRes) {
			super(context, layoutRes);
		}

		@Override
		public int getNodataLayoutRes() {
			return R.layout.widget_no_violation_data_view;
		}

		@Override
		protected void bindData(int pos, ViolationRecord itemData) {
	    setText(R.id.date_time_text, itemData.getDate());
	    setText(R.id.violation_area_indicator_text, itemData.getArea());
	    setText(R.id.violation_action_indicator_text, itemData.getAct());
	    setText(R.id.violation_money_indicator_text, getString(R.string.format_paid_total_money, itemData.getFloatMoney()));
	    setText(R.id.violation_score_indicator_text, itemData.getIntFen());
		}

	private void setText(int id, Object value) {
	    View view = getViewFromHolder(id);
	    if( view instanceof TextView ) {
		((TextView)view).setText(String.valueOf(value));
	    } else if( view instanceof IndicatorTextView ) {
		((IndicatorTextView)view).setTextValue(String.valueOf(value));
	    }
		}
	}

}
