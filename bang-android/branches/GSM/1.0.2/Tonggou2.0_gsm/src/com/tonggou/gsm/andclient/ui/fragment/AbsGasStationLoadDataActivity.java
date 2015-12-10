package com.tonggou.gsm.andclient.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;

import com.google.gson.Gson;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.bean.GasStation;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonResponseParseHandler;
import com.tonggou.gsm.andclient.net.parser.IResponseParser;
import com.tonggou.gsm.andclient.net.request.QueryJuheGasStationRequest;
import com.tonggou.gsm.andclient.net.response.JuheGasStationResponse;
import com.tonggou.gsm.andclient.ui.BackableTitleBarActivity;

/**
 * 加油站请求数据 Fragment
 * @author lwz
 *
 */
public abstract class AbsGasStationLoadDataActivity extends BackableTitleBarActivity {

	public static final String TAG = "AbsGasStationLoadDataFragment";
	
	private final String KEY_GAS_STATION_DATA = "key_gas_station_data";
	
	private static final int START_PAGE = 1;
	private int mCurrentPage = START_PAGE;
	
	private ArrayList<GasStation> mData;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if( !restoreExtras(savedInstanceState) ) {
			mData = new ArrayList<GasStation>();
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					requestData(true);
				}
			}, 1000);
		}
	}
	
	protected boolean restoreExtras(Bundle extra) {
		if( extra == null || !extra.containsKey(KEY_GAS_STATION_DATA)) {
			return false;
		}
		mData = extra.getParcelableArrayList(KEY_GAS_STATION_DATA);
		return true;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		if( mData != null && !mData.isEmpty() ) {
			outState.putParcelableArrayList(KEY_GAS_STATION_DATA, mData);
		}
		super.onSaveInstanceState(outState);
	}
	
	public ArrayList<GasStation> getData() {
		return mData;
	}
	
	/**
	 * 请求加油站数据
	 * @param isRefresh 是否刷新
	 */
	public void requestData(boolean isRefresh) {
		requestData(isRefresh ? START_PAGE : mCurrentPage + 1);
	}
	
	private void requestData(int page) {
		final boolean isRefresh = (page == START_PAGE);
		QueryJuheGasStationRequest request = new QueryJuheGasStationRequest();
		request.setRequestParams(120.733165, 31.296266, page);
		request.doRequest(this, new AsyncJsonResponseParseHandler<JuheGasStationResponse>() {

			private boolean isSuccess = false;
			
			@Override
			public void onParseSuccess(JuheGasStationResponse result,
					String originResult) {
				isSuccess = true;
				if( isRefresh ) {
					mData.clear();
				}
				mData.addAll(result.getResult().data);
				
				AbsGasStationLoadDataActivity.this.onUpdateData(result.getResult().data, isRefresh);
				mCurrentPage = result.getResult().pageinfo.current;
				App.debug(TAG, result.getResult().pageinfo.current + "   " + result.getResult().pageinfo.pnums);
				// 一直循环请求数据，直到请求错误 或者 总请求次数超过 N 次
				if( mCurrentPage <= 3 ) {
					requestData(false);
				}
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
				super.onParseFailure(errorCode, errorMsg);
				App.showShortToast(errorMsg);
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				AbsGasStationLoadDataActivity.this.onRequestDataFinish(isSuccess, isRefresh);
			}
			
			@Override
			public IResponseParser<JuheGasStationResponse> getJsonResponseParser() {
				// 由于是聚合返回的结果，故需要定制解析器
				return new IResponseParser<JuheGasStationResponse>() {
					
					@Override
					public JuheGasStationResponse parse(String jsonData) {
						try {
							JSONObject root = new JSONObject(jsonData);
							if( root.getInt("resultcode") != 200 ) {
								// 解决 JsonSyntaxException
								root.remove("result");
								jsonData = root.toString();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						return new Gson().fromJson(jsonData, JuheGasStationResponse.class);
					}
				};
			}
		});
	}
	
	/**
	 * 更新数据
	 * @param data
	 * @param isRefresh true the data is refresh, false otherwise
	 */
	public abstract void onUpdateData(List<GasStation> data, boolean isRefresh);
	
	/**
	 * 请求数据完成
	 * @param isSuccess 是否加载数据成功 
	 */
	public abstract void onRequestDataFinish(boolean isSuccess, boolean isRefresh);
}
