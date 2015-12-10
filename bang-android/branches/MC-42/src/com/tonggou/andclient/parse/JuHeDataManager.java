package com.tonggou.andclient.parse;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.baidu.mapapi.map.LocationData;
import com.tonggou.andclient.vo.GasStation;

public class JuHeDataManager {
	private static JuHeDataManager sDataManager;
	private HttpClient mHttpClient;

	public static final String TAG = "JuHeDataManager";
	public static final String AUTHORITY = "http://apis.juhe.cn/oil/local";
	public static final String KEY = "c8adc805a4a1fdeb7a79798d03d06a46";
	public static final String DTYPE = "json";
	public static final int RANGE = 10000;
	public static final int TIMEOUT = 10 * 1000;

	private JuHeDataManager() {
	}

	public static JuHeDataManager getInstance() {
		if (sDataManager == null) {
			sDataManager = new JuHeDataManager();
		}
		return sDataManager;
	}

	public String getBaseUri(LocationData locData) {
		String baseUri = AUTHORITY + "?key=" + KEY + "&dtype=" + DTYPE
				+ "&lon=" + locData.longitude + "&lat=" + locData.latitude
				+ "&r=" + RANGE;
		return baseUri;
	}

	public void initRequest() {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, TIMEOUT);
		mHttpClient = new DefaultHttpClient(params);
	}

	public ArrayList<GasStation> getGasStations(String baseUri, int page)
			throws ClientProtocolException, IOException, JSONException {
		ArrayList<GasStation> gasStations = null;
		String uri = baseUri + "&page=" + page;
		Log.d(TAG, "request uri:" + uri);
		HttpGet httpGet = new HttpGet(uri);
		HttpResponse response = mHttpClient.execute(httpGet);
		if (response.getStatusLine().getStatusCode() == 200) {
			gasStations = new ArrayList<GasStation>();
			HttpEntity httpEntity = response.getEntity();
			byte[] byteArray = EntityUtils.toByteArray(httpEntity);
			String charSet = EntityUtils.getContentCharSet(httpEntity);
			JSONObject rootJsonObj = new JSONObject(new String(byteArray,
					charSet));
			int resultCode = rootJsonObj.getInt("resultcode");
			if (resultCode == 200) {
				String result = rootJsonObj.getString("result");
				String data = new JSONObject(result).getString("data");
				JSONArray dataJsonArr = new JSONArray(data);
				for (int i = 0; i < dataJsonArr.length(); i++) {
					JSONObject itemJsonObj = dataJsonArr.getJSONObject(i);
					GasStation gasStation = jsonToBean(itemJsonObj);
					gasStations.add(gasStation);
				}
			}
		}
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
			gasStation.setE90((float) itemJsonObj.getDouble("E90"));
			gasStation.setE93((float) itemJsonObj.getDouble("E93"));
			gasStation.setE97((float) itemJsonObj.getDouble("E97"));
			gasStation.setE0((float) itemJsonObj.getDouble("E0"));
			gasStation.setDistance(itemJsonObj.getInt("distance"));
		}
		return gasStation;
	}
}
