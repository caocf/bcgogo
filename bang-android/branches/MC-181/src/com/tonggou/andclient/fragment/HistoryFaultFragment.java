package com.tonggou.andclient.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.tonggou.andclient.R;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.CarConditionResponse;
import com.tonggou.andclient.network.parser.AsyncJsonResponseParseHandler;
import com.tonggou.andclient.network.parser.IResponseParser;
import com.tonggou.andclient.network.request.HttpRequestClient;
import com.tonggou.andclient.network.request.QueryFaultCodeListRequest;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.vo.CarCondition;
import com.tonggou.andclient.vo.FaultCodeInfo;
import com.tonggou.andclient.vo.Pager;
import com.tonggou.andclient.vo.type.FaultCodeStatusType;

public class HistoryFaultFragment extends AbsFaultListFragment {
	
	public static final String TAG = "HistoryFaultFragment"; 
	private Pager mPager; 
	
	public static Fragment newInstance() {
		return new HistoryFaultFragment();
	}
	
	@Override
	protected void afterViews() {
		super.afterViews();
		mPager = new Pager();
		mPager.setCurrentPage(1);
		mPager.setHasNextPage(true);
	}
	
	@Override
	protected void updateData(final boolean isRefresh, int page)  {
		if( isLostActivity() ) {
			return;
		}
		QueryFaultCodeListRequest request = new QueryFaultCodeListRequest();
		request.setRequestParams(page, INFO.ITEMS_PER_PAGE, FaultCodeStatusType.FIXED);
		request.doRequest(getActivity(), new AsyncJsonResponseParseHandler<CarConditionResponse>() {
			
			@Override
			public void onParseSuccess(CarConditionResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				if( isLostActivity() ) {
					return;
				}
				mPager = result.getPager();
				if( isRefresh ) {
					mAdapter.update(result.getResult());
				} else {
					mAdapter.append(result.getResult());
				}
			}
			
			@Override
			public IResponseParser<CarConditionResponse> getResponseParser() {
				return new HistoryCarConditionParser();
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				mLoadMoreProxy.loadDataActionComplete();
			}
			
		});
	}

	@Override
	int getHandledButtonImageResource() {
		return R.drawable.fault_delete;
	}

	@Override
	void onHandledAction(CarCondition itemData) {
		modifyFaultCodeStatusRequest(itemData.getAlarmId(), 
				itemData.getFaultCode(), 
				itemData.getStatus(),
				FaultCodeStatusType.DELETED,
				itemData.getVehicleId());
	}
	
	class HistoryCarConditionParser implements IResponseParser<CarConditionResponse> {

		@Override
		public CarConditionResponse parse(String jsonData) {
			CarConditionResponse response = new CarConditionResponse();
			try {
				JSONObject root = new JSONObject(jsonData);
				response.setMessage( root.getString("message") );
				response.setMsgCode( root.getInt("msgCode") );
				response.setStatus( root.getString("status") );
				response.setPager( new Gson().fromJson( root.getString("pager") , Pager.class) );
				response.setResult( parseCarConditions(root.getJSONArray("result")));
			} catch (JSONException e) {
			}
			return response;
		}
		
		private ArrayList<CarCondition> parseCarConditions(JSONArray root ) throws JSONException {
			int size = root.length();
			ArrayList<CarCondition> list = new ArrayList<CarCondition>();
			for( int i=0; i<size; i++ ) {
				JSONObject item = root.getJSONObject(i);
				list.add( parseCarCondition(item) );
			}
			return list;
			
		}
		
		private CarCondition parseCarCondition(JSONObject data) throws JSONException {
			CarCondition condition = new CarCondition();
			condition.setAlarmId( data.getString("id") );
			condition.setObdSN( data.getString("obdId") );
			condition.setVehicleId( data.getString("appVehicleId") );
			condition.setStatus( FaultCodeStatusType.valueOf( data.getString("status") ) );
			condition.setReportTime( data.getString("reportTime") );
			condition.setStatusStr( data.getString("statusStr") );
				FaultCodeInfo info = new FaultCodeInfo();
				info.setBackgroundInfo( data.getString("backgroundInfo") );
				info.setCategory( data.getString("category") );
				info.setDescription(data.getString("content") );
				info.setFaultCode( data.getString("errorCode") );
			condition.setFaultCodeInfo(info);
			return condition;
		}
	}

	@Override
	void onHandleSuccess(String faultCodeId, String faultCode, String vehicleId) {
		onRefresh(1, 1);
	}
	
	@Override
	public void onStop() {
		HttpRequestClient.cancelRequest(getActivity(), true);
		super.onStop();
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
			mPostLoadHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					TongGouApplication.showToast("已经是最后一页");
					mLoadMoreProxy.loadDataActionComplete();
				}
			}, 1000);
		}
	};
	
	
}
