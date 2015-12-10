package com.tonggou.gsm.andclient.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.GasStation;
import com.tonggou.gsm.andclient.ui.view.AbsViewHolderAdapter;

/**
 * 加油站列表
 * @author lwz
 *
 */
public class GasStationListActivity extends BackableTitleBarActivity implements OnItemClickListener {

	/** 加油站列表数据 */
	public static final String EXTRA_GAS_STATION_LIST_DATA = "extra_gas_station_list_data";
	
	private ListView mListView;
	private GasStationAdapter mAdapter;
	private SortAsyncTask mSortAsyncTask;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_gas_station_list);
		
		getTitleBar().setTitle(R.string.title_gas_station_list, R.color.black);
		
		mListView = (ListView) findViewById(R.id.listview);
		mAdapter = new GasStationAdapter(this, R.layout.item_list_gas_station);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		
		if( !restoreExtras(getIntent()) ) {
			restoreExtras(savedInstance);
		}
	}
	
	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra == null || !extra.containsKey(EXTRA_GAS_STATION_LIST_DATA)) {
			return false;
		}
		ArrayList<GasStation> data = extra.getParcelableArrayList(EXTRA_GAS_STATION_LIST_DATA);
		doSortByDistance(data);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void doSortByDistance(ArrayList<GasStation> data) {
		mSortAsyncTask = new SortAsyncTask(this);
		mSortAsyncTask.execute(data);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		ArrayList<GasStation> data = (ArrayList<GasStation>) mAdapter.getData();
		if( data != null && !data.isEmpty() ) {
			outState.putParcelableArrayList(EXTRA_GAS_STATION_LIST_DATA, data);
		}
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * 按距离排序 AsyncTask
	 * @author lwz
	 */
	class SortAsyncTask extends AsyncTask<ArrayList<GasStation>, Void, ArrayList<GasStation>> {

		// 防止内存泄漏
		private WeakReference<Activity> reference;
		
		SortAsyncTask(Activity activity) {
			this.reference = new WeakReference<Activity>(activity);
		}
		
		@Override
		protected ArrayList<GasStation> doInBackground(ArrayList<GasStation>... params) {
			Collections.sort(params[0], new Comparator<GasStation>() {

				@Override
				public int compare(GasStation lhs, GasStation rhs) {
					Integer lhsdistance = lhs.getDistance();
					Integer rhsdistance = rhs.getDistance();
					return lhsdistance.compareTo(rhsdistance);
				}
			});
			return params[0];
		}
		
		@Override
		protected void onPostExecute(ArrayList<GasStation> result) {
			if( reference.get() != null ) {
				mAdapter.update( result );
			}
		}
	}
	
	class GasStationAdapter extends AbsViewHolderAdapter<GasStation> {

		public GasStationAdapter(Context context, int layoutRes) {
			super(context, layoutRes);
		}

		@Override
		protected void bindData(int pos, GasStation itemData) {
			setText(R.id.name_text, itemData.getName());
			setText(R.id.address_text, itemData.getAddress());
			setText(R.id.distance_text, itemData.getDistance());
			setText(R.id.price_text, itemData.getGastprice());
		}
		
		private void setText(int id, Object value) {
			((TextView)getViewFromHolder(id)).setText(String.valueOf(value));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent data = new Intent();
		long stationId = mAdapter.getData().get(arg2).getId();
		data.putExtra( GasStationMapActivity.EXTRA_SELECTED_GAS_STATION_ID , stationId);
		setResult(RESULT_OK, data);
		finish();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if( mSortAsyncTask == null ) {
			mSortAsyncTask.cancel(true);
		}
	}
	
}
