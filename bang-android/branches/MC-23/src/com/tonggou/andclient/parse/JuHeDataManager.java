package com.tonggou.andclient.parse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.baidu.mapapi.map.LocationData;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.tonggou.andclient.GasStationMapActivity;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.vo.GasStation;
@Deprecated
public class JuHeDataManager {
	private static JuHeDataManager sDataManager;

	public static final String TAG = "JuHeDataManager";
	public static final String AUTHORITY = "http://apis.juhe.cn/oil/local";
	public static final String KEY = "c8adc805a4a1fdeb7a79798d03d06a46";
	public static final String DTYPE = "json";
	public static final int RANGE = 10000;
	public static final int TIMEOUT = 10 * 1000;
	private Context mContext;
	private SyncHttpClient mSyncHttpClient;
	private Handler mErrorHandler;

	private JuHeDataManager() {
	}

	public static JuHeDataManager getInstance(Context context, Handler errorHandler) {
		if (sDataManager == null) {
			sDataManager = new JuHeDataManager();
			sDataManager.mContext = context;
			sDataManager.mSyncHttpClient = new SyncHttpClient();
			sDataManager.mErrorHandler = errorHandler;
		}
		return sDataManager;
	}

	public String getBaseUri(LocationData locData) {
		String baseUri = AUTHORITY + "?key=" + KEY + "&dtype=" + DTYPE
				+ "&lon=" + locData.longitude + "&lat=" + locData.latitude
				+ "&r=" + RANGE;
		return baseUri;
	}

	public ArrayList<GasStation> getGasStations(String baseUri, int page)
			throws JSONException {
		
		final ArrayList<GasStation> gasStations = new ArrayList<GasStation>();
		String uri = baseUri + "&page=" + page;
		TongGouApplication.showLog( "ddddddddddddddd " + baseUri );
		mSyncHttpClient.get(mContext, uri, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				TongGouApplication.showLog( "ddddddddddddddd " + new String(arg2) );
				try {
					JSONObject rootJsonObj = new JSONObject(new String(arg2));
					int resultCode = rootJsonObj.getInt("resultcode");
					if (resultCode == 200) {
						JSONArray dataJsonArr = rootJsonObj.getJSONObject("result").getJSONArray("data");
						final int length = dataJsonArr.length();
						for (int i = 0; i < length; i++) {
							JSONObject itemJsonObj = dataJsonArr.getJSONObject(i);
							GasStation gasStation = jsonToBean(itemJsonObj);
							gasStations.add(gasStation);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
					Throwable error) {
				TongGouApplication.showLog( "ddddddddddddddd onFailure" );
//				if( mErrorHandler != null )
//					mErrorHandler.sendEmptyMessage(GasStationMapActivity.PARSING_NETWORK_ERROR);
			}
			
		});
		return gasStations;
	}

	private GasStation jsonToBean(JSONObject itemJsonObj) throws JSONException {
		GasStation gasStation = null;
		if (itemJsonObj != null) {
			gasStation = new GasStation();
			gasStation.setId(itemJsonObj.getInt("id"));
			gasStation.setName(itemJsonObj.getString("name"));
			gasStation.setAddress(itemJsonObj.getString("address"));
			gasStation.setType(itemJsonObj.getString("type"));
			gasStation.setDiscount(itemJsonObj.getString("discount"));
			gasStation.setLon(itemJsonObj.getDouble("lon"));
			gasStation.setLat(itemJsonObj.getDouble("lat"));
			gasStation.setDistance(itemJsonObj.getInt("distance"));
			gasStation.setGasPriceInfos(parseGasPriceInfo(itemJsonObj.getJSONObject("gastprice")));
		}
		return gasStation;
	}
	
	private List<String> parseGasPriceInfo(JSONObject rootObj) throws JSONException {
		List<String> result = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		Iterator<String> it = rootObj.keys();
		while( it.hasNext() ) {
			String key = it.next();
			Object value = rootObj.get(key);
			result.add( key + ":" + value + " " );
		}
		return result;
	}
	
	public void cancelRequest() {
		mSyncHttpClient.cancelRequests(mContext, true);
	}
}
