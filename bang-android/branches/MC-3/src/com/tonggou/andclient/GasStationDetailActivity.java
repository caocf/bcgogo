package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.vo.GasStation;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GasStationDetailActivity extends BaseActivity {
	private static final String TAG = "GasStationDetailActivity";
	public static final String ACTION_GAS_STATION_DETAIL = "com.tonggou.andclient.GasStationDetailActivity";
	private ListView mlvGasStation;
	private TextView mtvSortByPrice;
	private TextView mtvSortByDist;
	private Resources mResources;

	private ArrayList<GasStation> mGasStations;
	private int mCurrSortType;
	private float mMinE93;

	private static final int SORT_BY_DISTANCE = 1;
	private static final int SORT_BY_PRICE = 2;
	public static final String GAS_STATION = "GasStation";
	private static final CharSequence CHINA_FULL_STOP = "。";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		receiveIntentData();
		if (mGasStations == null) {
			TongGouApplication.showToast("加载数据失败!");
			finish();
			return;
		}
		setContentView(R.layout.gas_station_detail);
		initViews();
	}

	private void receiveIntentData() {
		Intent data = getIntent();
		mGasStations = data.getParcelableArrayListExtra(GasStationMapActivity.GAS_STATIONS);
	}

	private void initViews() {
		mResources = getResources();
		mlvGasStation = (ListView) findViewById(R.id.lv_gas_station_detail);
		LinearLayout llSortByPrice = (LinearLayout) findViewById(R.id.ll_gas_staion_detail_sort_by_price);
		LinearLayout llSortByDist = (LinearLayout) findViewById(R.id.ll_gas_staion_detail_sort_by_dist);
		mtvSortByPrice = (TextView) findViewById(R.id.tv_gas_staion_detail_sort_by_price);
		mtvSortByDist = (TextView) findViewById(R.id.tv_gas_staion_detail_sort_by_dist);
		RelativeLayout rlBack = (RelativeLayout) findViewById(R.id.rl_gas_staion_detail_back);
		RelativeLayout rlShowMap = (RelativeLayout) findViewById(R.id.rl_gas_staion_detail_map);

		Collections.sort(mGasStations, getComparator(SORT_BY_DISTANCE));
		mlvGasStation.setAdapter(mlvAdapter);
		mlvGasStation.setOnItemClickListener(mOnItemClickListener);
		mlvGasStation.setOnScrollListener(mOnScrollListener);
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
				mtvSortByPrice.setTextColor(mResources
						.getColor(R.color.gas_station_detail_title_blue));
				mtvSortByDist.setTextColor(mResources.getColor(R.color.white));
				break;
			case R.id.ll_gas_staion_detail_sort_by_dist:
				Collections.sort(mGasStations, getComparator(SORT_BY_DISTANCE));
				mlvAdapter.notifyDataSetChanged();
				mlvGasStation.setSelection(0);
				mtvSortByDist.setTextColor(mResources
						.getColor(R.color.gas_station_detail_title_blue));
				mtvSortByPrice.setTextColor(mResources.getColor(R.color.white));
				break;
			case R.id.rl_gas_staion_detail_back:
				GasStationDetailActivity.this.finish();
				break;
			case R.id.rl_gas_staion_detail_map:
				GasStationDetailActivity.this.finish();
				break;
			}
		}
	};

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			GasStation gasStation = (GasStation) parent.getItemAtPosition(position);
			jumpToMap(gasStation);
		}
	};

	private void jumpToMap(GasStation gasStation) {
		Intent intent = new Intent();
		intent.putExtra(GAS_STATION, gasStation);
		setResult(0, intent);
		finish();
	}

	private OnScrollListener mOnScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
				int totalItemCount) {
			if (firstVisibleItem + visibleItemCount == totalItemCount) {
				TongGouApplication.showToast("已是最后一页了");
			}
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
				viewHolder.tvName = (TextView) view.findViewById(R.id.gas_station_detail_item_name);
				viewHolder.tvDist = (TextView) view
						.findViewById(R.id.gas_station_detail_item_distance);
				viewHolder.tvAddr = (TextView) view.findViewById(R.id.gas_station_detail_item_addr);
				viewHolder.tvPrice = (TextView) view
						.findViewById(R.id.gas_station_detail_item_price);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			GasStation gasStation = mGasStations.get(position);
			if (mCurrSortType == SORT_BY_PRICE && position == 0) {
				mMinE93 = gasStation.getE93();
			}

			if (gasStation != null) {
				viewHolder.tvName.setText(gasStation.getName());
				viewHolder.tvDist.setText(String.format(
						mResources.getString(R.string.gas_station_dist),
						((float) gasStation.getDistance()) / 1000 + ""));
				viewHolder.tvAddr.setText(gasStation.getAddress().replace(CHINA_FULL_STOP, ""));
				String strE93 = gasStation.getE93() + "";
				String strPrice = String.format(
						mResources.getString(R.string.gas_station_detail_item_price),
						gasStation.getE0() + "", strE93, gasStation.getE97() + "");
				if (mCurrSortType == SORT_BY_PRICE && gasStation.getE93() == mMinE93) {
					String strLight = "93#:" + strE93;
					int start = strPrice.indexOf(strLight);
					int end = start + strLight.length();
					SpannableStringBuilder builder = new SpannableStringBuilder(strPrice);
					builder.setSpan(new ForegroundColorSpan(0xfff9ad2b), start, end,
							Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
					viewHolder.tvPrice.setText(builder);
				} else {
					viewHolder.tvPrice.setText(strPrice);
					viewHolder.tvPrice.setTextColor(mResources
							.getColor(R.color.gas_station_detail_font_gray));
				}
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

	public Comparator<GasStation> getComparator(final int sortType) {
		mCurrSortType = sortType;
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

}
