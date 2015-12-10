package com.tonggou.andclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.parse.JuHeDataManager;
import com.tonggou.andclient.vo.GasStation;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GasStationDetailActivity extends BaseActivity {
	public static final String TAG2 = "JuHeDataManager";
	private Resources mResources;
	private Toast mToast;
	private ListView mlvGasStation;
	private LinearLayout mllPB;
	private boolean isPrepared;

	private BMapManager mBMapManager;
	private LocationClient mLocationClient;
	private double mCurrLat;
	private double mCurrLon;
	private float mCurrAccu;
	private float mCurrDire;
	private ArrayList<GasStation> mGasStations = new ArrayList<GasStation>();

	public static final String GAS_STATIONS = "GasStations";
	public static final String DEST_GAS_STATION = "currGasStation";
	public static final String CURR_LAT = "currLat";
	public static final String CURR_LON = "currLon";
	public static final String CURR_ACCU = "currAccu";
	public static final String CURR_DIRE = "currDire";

	private static final String BD09LL = "bd09ll";
	private static final String GCJ02 = "gcj02";
	private static final int PARSING_NETWORK_ERROR = 10001;
	private static final int SUCCESS_FROM_GPS = 61;
	private static final int SUCCESS_FROM_NETWORK = 161;
	private static final int SORT_BY_DISTANCE = 1;
	private static final int SORT_BY_PRICE = 2;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PARSING_NETWORK_ERROR:
				showToast("获取加油站信息错误");
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBMap();
		mResources = getResources();
		setContentView(R.layout.gas_station_detail);
		initViews();
		initLocationClient();
	}

	private void initBMap() {
		mBMapManager = new BMapManager(this);
		mBMapManager.init(TongGouApplication.strKey, mMKGeneralListener);
		mBMapManager.start();
	}

	private void initLocationClient() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(mLocationListener);
		LocationClientOption clientOption = new LocationClientOption();
		clientOption.setOpenGps(true);
		clientOption.setAddrType("all");
		clientOption.setCoorType(BD09LL);
		clientOption.disableCache(true);
		clientOption.setPriority(LocationClientOption.NetWorkFirst);
		mLocationClient.setLocOption(clientOption);
		mLocationClient.start();
	}

	private void initViews() {
		mlvGasStation = (ListView) findViewById(R.id.lv_gas_station_detail);
		mllPB = (LinearLayout) findViewById(R.id.ll_gas_staion_detail_pb);
		LinearLayout llSortByPrice = (LinearLayout) findViewById(R.id.ll_gas_staion_detail_sort_by_price);
		LinearLayout llSortByDist = (LinearLayout) findViewById(R.id.ll_gas_staion_detail_sort_by_dist);
		RelativeLayout rlBack = (RelativeLayout) findViewById(R.id.rl_gas_staion_detail_back);
		RelativeLayout rlShowMap = (RelativeLayout) findViewById(R.id.rl_gas_staion_detail_map);

		mlvGasStation.setAdapter(mlvAdapter);
		mlvGasStation.setOnItemClickListener(mOnItemClickListener);
		llSortByPrice.setOnClickListener(mClickListener);
		llSortByDist.setOnClickListener(mClickListener);
		rlBack.setOnClickListener(mClickListener);
		rlShowMap.setOnClickListener(mClickListener);
	}

	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ll_gas_staion_detail_sort_by_price:
				Collections.sort(mGasStations, getComparator(SORT_BY_PRICE));
				mlvAdapter.notifyDataSetChanged();
				mlvGasStation.setSelection(0);
				break;
			case R.id.ll_gas_staion_detail_sort_by_dist:
				Collections.sort(mGasStations, getComparator(SORT_BY_DISTANCE));
				mlvAdapter.notifyDataSetChanged();
				mlvGasStation.setSelection(0);
				break;
			case R.id.rl_gas_staion_detail_back:
				GasStationDetailActivity.this.finish();
				break;
			case R.id.rl_gas_staion_detail_map:
				if (isPrepared) {
					startMapActivity();
				} else {
					showToast("信息尚未获取完全 ，请稍候");
				}
				break;
			}
		}
	};

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			GasStation gasStation = (GasStation) parent
					.getItemAtPosition(position);
			startMapActivity(gasStation);
		}
	};

	private BaseAdapter mlvAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder;
			if (view == null) {
				view = View.inflate(GasStationDetailActivity.this,
						R.layout.gas_station_detail_item, null);
				viewHolder = new ViewHolder();
				viewHolder.tvName = (TextView) view
						.findViewById(R.id.gas_station_detail_item_name);
				viewHolder.tvDist = (TextView) view
						.findViewById(R.id.gas_station_detail_item_distance);
				viewHolder.tvAddr = (TextView) view
						.findViewById(R.id.gas_station_detail_item_addr);
				viewHolder.tvPrice = (TextView) view
						.findViewById(R.id.gas_station_detail_item_price);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			GasStation gasStation = mGasStations.get(position);
			if (gasStation != null) {
				viewHolder.tvName.setText(gasStation.getName());
				viewHolder.tvDist.setText("距离:"
						+ ((float) gasStation.getDistance()) / 1000 + "km");
				viewHolder.tvAddr.setText(gasStation.getAddress());
				viewHolder.tvPrice.setText(String.format(mResources
						.getString(R.string.gas_station_detail_item_price),
						gasStation.getE0() + "", gasStation.getE93() + "",
						gasStation.getE97() + ""));
			}
			return view;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return mGasStations.get(position);
		}

		@Override
		public int getCount() {
			return mGasStations.size();
		}
	};

	private class ViewHolder {
		public TextView tvName;
		public TextView tvDist;
		public TextView tvAddr;
		public TextView tvPrice;
	}

	private BDLocationListener mLocationListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			int type = location.getLocType();
			if (location != null
					&& (type == SUCCESS_FROM_GPS || type == SUCCESS_FROM_NETWORK)) {
				LocationData locData = getLocData(location);
				new GasStationsTask().execute(locData);
			}
		}

		@Override
		public void onReceivePoi(BDLocation location) {
		}
	};

	private class GasStationsTask extends
			AsyncTask<LocationData, Void, ArrayList<GasStation>> {
		@Override
		protected ArrayList<GasStation> doInBackground(LocationData... params) {
			ArrayList<GasStation> allGasStations = new ArrayList<GasStation>();
			JuHeDataManager dataManager = JuHeDataManager.getInstance();
			LocationData locData = params[0];
			dataManager.initRequest();
			String baseUri = dataManager.getBaseUri(locData);
			int i = 1;
			while (true) {
				int page = i++;
				try {
					ArrayList<GasStation> gasStations = dataManager
							.getGasStations(baseUri, page);
					if (gasStations != null && gasStations.size() == 0) {
						Log.d(TAG2, "ending page：" + page);
						break;
					}
					allGasStations.addAll(gasStations);
				} catch (ClientProtocolException e) {
					Log.e(TAG2, "page：" + page, e);
					break;
				} catch (IOException e) {
					handler.sendEmptyMessage(PARSING_NETWORK_ERROR);
					Log.e(TAG2, "page：" + page, e);
					break;
				} catch (JSONException e) {
					Log.e(TAG2, "page：" + page, e);
					break;
				}
			}
			return allGasStations;
		}

		@Override
		protected void onPostExecute(ArrayList<GasStation> result) {
			if (result.size() > 0) {
				mGasStations.clear();
				mGasStations.addAll(result);
				Collections.sort(mGasStations, getComparator(SORT_BY_DISTANCE));
				mlvAdapter.notifyDataSetChanged();
				mllPB.setVisibility(View.INVISIBLE);
				isPrepared = true;
			}
		}

	}

	private void startMapActivity(GasStation... gasStations) {
		Intent intent = new Intent(GasStationDetailActivity.this,
				GasStationMapActivity.class);
		if (gasStations != null && gasStations.length != 0) {
			intent.putExtra(DEST_GAS_STATION, gasStations[0]);
		}
		intent.putParcelableArrayListExtra(GAS_STATIONS, mGasStations);
		intent.putExtra(CURR_LAT, mCurrLat);
		intent.putExtra(CURR_LON, mCurrLon);
		intent.putExtra(CURR_ACCU, mCurrAccu);
		intent.putExtra(CURR_DIRE, mCurrDire);

		startActivity(intent);
	}

	public Comparator<GasStation> getComparator(final int sortType) {
		Comparator<GasStation> comparator = new Comparator<GasStation>() {
			@Override
			public int compare(GasStation lhs, GasStation rhs) {
				float lE93 = lhs.getE93();
				float rE93 = rhs.getE93();
				int lDist = lhs.getDistance();
				int rDist = rhs.getDistance();

				if (sortType == SORT_BY_DISTANCE) {
					if (lDist != rDist) {
						return lDist - rDist;
					} else if (lE93 != rE93) {
						if (lE93 > rE93) {
							return 1;
						} else {
							return -1;
						}
					} else {
						return lhs.getId() - rhs.getId();
					}
				} else {
					if (lE93 != rE93) {
						if (lE93 > rE93) {
							return 1;
						} else {
							return -1;
						}
					} else if (lDist != rDist) {
						return lDist - rDist;
					} else {
						return lhs.getId() - rhs.getId();
					}
				}
			}
		};

		return comparator;
	}

	private LocationData getLocData(BDLocation location) {
		LocationData locData = new LocationData();
		locData.latitude = location.getLatitude();
		locData.longitude = location.getLongitude();
		locData.accuracy = location.getRadius();
		locData.direction = location.getDerect();

		mCurrLon = locData.longitude;
		mCurrLat = locData.latitude;
		mCurrAccu = locData.accuracy;
		return locData;
	}

	private MKGeneralListener mMKGeneralListener = new MKGeneralListener() {
		@Override
		public void onGetNetworkState(int iError) {
			// 一些网络状态的错误处理回调函数
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				showToast("您的网络出错啦！");
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			// 授权错误的时候调用的回调函数
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				showToast("API KEY错误, 请检查！");
			}
		}
	};

	private void showToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(GasStationDetailActivity.this, text,
					Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBMapManager != null) {
			mBMapManager = null;
		}
	}

}
