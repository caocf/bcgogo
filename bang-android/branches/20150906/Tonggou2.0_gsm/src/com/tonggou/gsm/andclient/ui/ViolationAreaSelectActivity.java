package com.tonggou.gsm.andclient.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.Area;
import com.tonggou.gsm.andclient.bean.type.JuheCityStatus;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.QueryJuheCityRequest;
import com.tonggou.gsm.andclient.net.response.QueryJuheCityResponse;
import com.tonggou.gsm.andclient.ui.view.AbsViewHolderAdapter;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.ContextUtil;

/**
 * 违章城市查询
 * @author lwz
 *
 */
public class ViolationAreaSelectActivity extends BackableTitleBarActivity implements OnItemClickListener {
	
	public static final String EXTRA_AREA = "extra_area";
	public static final int REQUEST_CODE_SELECT_CITY = 0x1234;
	
	ListView mListView;
	AreaAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_violation_city_select);
		
		mListView = (ListView) findViewById(R.id.listview);
//		mListView.setEmptyView(findViewById(R.id.empty_view));
		mListView.setOnItemClickListener(this);
		mAdapter = new AreaAdapter(this, R.layout.item_list_area);
		mListView.setAdapter(mAdapter);
		
		initData();
	}
	
	void initData() {
		doRequestCity();
	}
	
	void doRequestCity() {
		showLoadingDialog();
		QueryJuheCityRequest request = new QueryJuheCityRequest();
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<QueryJuheCityResponse>() {

			@Override
			public void onParseSuccess(QueryJuheCityResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				mAdapter.update(result.getAreaList());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
			}
			
			@Override
			public Class<QueryJuheCityResponse> getTypeClass() {
				return QueryJuheCityResponse.class;
			}
			
		});
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_violation_city_select);
	}
	
	class AreaAdapter extends AbsViewHolderAdapter<Area> {

		public AreaAdapter(Context context, int layoutRes) {
			super(context, layoutRes);
		}

		@Override
		protected void bindData(int pos, Area itemData) {
			TextView cityNameText = (TextView) getViewFromHolder(android.R.id.text1);
			cityNameText.setText(itemData.getName());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Area area = mAdapter.getData().get(position);
		if( area.getJuheStatus() == JuheCityStatus.ACTIVE ) {
			resultOK(area);
			
		} else {
			Bundle args = new Bundle();
			args.putParcelable(EXTRA_AREA, area);
			ContextUtil.startActivityForResult(this, ViolationCitySelectActivity.class, REQUEST_CODE_SELECT_CITY, args);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( requestCode == REQUEST_CODE_SELECT_CITY && resultCode == RESULT_OK ) {
			// 直接把数据返回
			resultOk(data);
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	void resultOK(Area area) {
		Intent data = new Intent();
		data.putExtra(EXTRA_AREA, area);
		resultOk(data);
	}
	
	void resultOk(Intent data) {
		setResult(RESULT_OK, data);
		finish();
	}
}
