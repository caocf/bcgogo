package com.tonggou.andclient.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.tonggou.andclient.R;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.CarConditionResponse;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.network.parser.AsyncLoadCacheJsonResponseParseHandler;
import com.tonggou.andclient.network.parser.IResponseParser;
import com.tonggou.andclient.network.request.HttpRequestClient;
import com.tonggou.andclient.network.request.QueryFaultCodeListRequest;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.RefreshViewLoadMoreProxy.LOAD_MODE;
import com.tonggou.andclient.vo.CarCondition;
import com.tonggou.andclient.vo.FaultCodeInfo;
import com.tonggou.andclient.vo.Pager;
import com.tonggou.andclient.vo.type.FaultCodeStatusType;

/**
 * 历史故障
 * @author lwz
 *
 */
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
		mPager.setCurrentPage(0);
		mPager.setHasNextPage(true);
	}
	
	@Override
	protected void updateData(final int page, final LOAD_MODE mode)  {
		if( isLostActivity() ) {
			return;
		}
		if( mPager.getCurrentPage() == 0 ) {
			mPager.setCurrentPage(1);
		}
		QueryFaultCodeListRequest request = new QueryFaultCodeListRequest();
		request.setRequestParams(page, INFO.ITEMS_PER_PAGE, FaultCodeStatusType.FIXED);
		request.doRequest(getActivity(), new AsyncLoadCacheJsonResponseParseHandler<CarConditionResponse>() {
			
			@Override
			public void onLoadCache(CarConditionResponse result, String originResult,
					boolean isNetworkConnected) {
				if( result != null && mode == LOAD_MODE.REFRESH_LOAD_CACHE) { 
					mAdapter.update( result.getResult() );
					
				} else if( !isNetworkConnected ) {
					TongGouApplication.showToast(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE);
				}
			}
			
			@Override
			public void onParseSuccess(CarConditionResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				if( isLostActivity() ) {
					return;
				}
				mPager = result.getPager();
				if(  mode != LOAD_MODE.LOAD_MORE ) {
					mAdapter.update(result.getResult());
				} else {
					mAdapter.append(result.getResult());
				}
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				mLoadMoreProxy.loadDataActionComplete();
				mPullToRefreshActionSlideListView.setMode(Mode.BOTH);
			}

			@Override
			public IResponseParser<CarConditionResponse> getJsonResponseParser() {
				return new HistoryCarConditionParser();
			}
			
			@Override
			public String getCacheKey() {
				return String.valueOf( getRequestURI() );
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
		updateData( 1, 
				mPager.getCurrentPage() == 0 ? LOAD_MODE.REFRESH_LOAD_CACHE : LOAD_MODE.REFRESH_NOT_LOAD_CACHE );
	}

	@Override
	public void onLoadMore(int page, int dataCount) {
		if( mPager.isHasNextPage() ) {
			updateData( mPager.getCurrentPage() + 1, 
					// 初始化时 page=0， 区分缓存数据
					(mPager.getCurrentPage() == 0 || false) ? LOAD_MODE.REFRESH_NOT_LOAD_CACHE : LOAD_MODE.LOAD_MORE);
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
}
