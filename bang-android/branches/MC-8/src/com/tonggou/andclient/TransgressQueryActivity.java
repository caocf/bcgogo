package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.myview.DoubleListPopupWindow;
import com.tonggou.andclient.myview.SimpleTitleBar;
import com.tonggou.andclient.myview.SingleListPopupWindow;
import com.tonggou.andclient.network.AsyncRequestHandler;
import com.tonggou.andclient.network.RequestClient;
import com.tonggou.andclient.parse.VehicleListParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.PreferenceUtil;
import com.tonggou.andclient.vo.JuheTransgressArea;
import com.tonggou.andclient.vo.VehicleInfo;

/**
 * 违章查询
 * @author lwz
 *
 */
public class TransgressQueryActivity extends AbsBackableActivity implements View.OnClickListener {
	
	private final String EMPTY_VEHICLE_MSG = "您还没有车辆信息";
	private final String PREF_NAME_TRANSGRES = "pref_transgress";
	private final String PREF_KEY_LAST_SELECTED_CITY = "last_selected_city";
	private final String PREF_KEY_JUHE_SUPPORT_CITY = "juhe_support_city";
	
	private TextView mVehicleSelectText;	// 车辆选择框
	private TextView mCitySelectText;		// 城市选择框
	private Button mQueryButton;			// 查询按钮
	private ViewGroup mTotalDeductScoreContainer;	// 累积扣分父容器
	private TextView mTotalDeductScoreText;			// 累积扣分
	private TextView mTotalForfeitText;				// 累积罚款
	private ListView mQueryResultList;				// 查询结果列表
	private SingleListPopupWindow mVehicleSelectPopupWindow;		// 车辆选择弹出框
	private DoubleListPopupWindow mCitySelectPopupWindow;			// 城市选择弹出框
	
	private VehicleAadpter mVehicleAdapter;
	private AreaAdapter mProvinceAdapter;
	private AreaAdapter mCityAdapter;
	
	private SelectedAreaList mSelectedCity = new SelectedAreaList();

	@Override
	protected int getContentLayout() {
		return R.layout.activity_transgress_query;
	}
	
	@Override
	protected void afterTitleBarCreated(SimpleTitleBar titleBar, Bundle saveInstanceState) {
		super.afterTitleBarCreated(titleBar, saveInstanceState);
		titleBar.setTitle(R.string.transgress_query_title);
		titleBar.setRightButtonText(R.string.set_bindcar_title);
		titleBar.setOnRightButtonClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				turnToBindCarsActivity();
			}
		});
	}
	
	/**
	 * 跳转到车辆管理界面
	 */
	private void turnToBindCarsActivity() {
		Intent intent = new Intent(this, BindCarsActivity.class);
		startActivity(intent);
	}

	@Override
	protected void findViews(Bundle saveInstanceState) {
		super.findViews(saveInstanceState);
		
		mVehicleSelectText = (TextView) findViewById(R.id.select_vehicle_text);
		mCitySelectText = (TextView) findViewById(R.id.select_city_text);
		mQueryButton = (Button) findViewById(R.id.query_btn);
		mTotalDeductScoreContainer = (ViewGroup) findViewById(R.id.total_deduct_container);
		mTotalDeductScoreText = (TextView) findViewById(R.id.total_deduct_score);
		mTotalForfeitText = (TextView) findViewById(R.id.total_forfeit);
		mQueryResultList = (ListView) findViewById(R.id.query_result_list);
		afterViews();
	}

	private void afterViews() {
		createAdapter();
		setListener();
	}

	private void setListener() {
		mVehicleSelectText.setOnClickListener(this);
		mCitySelectText.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.select_vehicle_text: onSelectVehicle(); break;
			case R.id.select_city_text: onSelectCity(); break;
		}
	}
	
	private void createAdapter() {
		mVehicleAdapter = new VehicleAadpter(this, new ArrayList<VehicleInfo>(), R.layout.popview_item);
		mProvinceAdapter = new AreaAdapter(this, new ArrayList<JuheTransgressArea>(), R.layout.popview_item);
		mCityAdapter = new AreaAdapter(this, new ArrayList<JuheTransgressArea>(), R.layout.popview_item);
	}

	/**
	 * 选择该用户绑定的汽车
	 */
	private void onSelectVehicle() {
		mVehicleSelectPopupWindow = new SingleListPopupWindow(this, mVehicleAdapter);
		mVehicleSelectPopupWindow.setOnListItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				VehicleInfo vehicleInfo = mVehicleAdapter.getData().get(position);
				if( EMPTY_VEHICLE_MSG.equals(vehicleInfo.getVehicleNo()) ) {
					return;
				} else {
					mVehicleSelectText.setText(vehicleInfo.getVehicleNo());
				}
			}
		});
		mVehicleSelectPopupWindow.showAsDropDown(mVehicleSelectText);
	}
	
	/**
	 * 选择查询违章的城市
	 */
	private void onSelectCity() {
		mCitySelectPopupWindow = new DoubleListPopupWindow(this, mProvinceAdapter, mCityAdapter);
		mCitySelectPopupWindow.setListItemWidthAndHeight(-1, 10000);
		mCitySelectPopupWindow.setOnLeftListItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mCityAdapter.updateAreaByProvince( mProvinceAdapter.getData().get(position) );
			}
		});
		
		mCitySelectPopupWindow.setOnRightListItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				JuheTransgressArea area = mCityAdapter.getData().get(position);
				if( mSelectedCity.contains( area ) ) {
					mSelectedCity.remove(area);
				} else {
					mSelectedCity.add(area);
				}
				mCitySelectText.setText(getSelectedCityNames());
			}
		});
		mCitySelectPopupWindow.showAsDropDown(mCitySelectText);
	}
	
	private String getSelectedCityNames() {
		String names = mSelectedCity.toString();
		return names.substring(1, names.length() - 1);
	}
	
	/**
	 * 显示累计扣分及罚款的布局
	 * @param deductScore 扣分数
	 * @param forfeit 罚款数
	 */
	private void showTotalDeductContainer(String deductScore, String forfeit) {
		mTotalDeductScoreContainer.setVisibility(View.VISIBLE);
		mTotalDeductScoreText.setText(deductScore);
		mTotalForfeitText.setText(forfeit);
	}
	
	/**
	 * 隐藏累计扣分及罚款的布局
	 */
	private void hideTotalDeductContainer() {
		mTotalDeductScoreContainer.setVisibility(View.GONE);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		requestBindVehiclesData();
		requestJuheSupportCity();
		TongGouApplication.showLog( mSelectedCity == null );
		mSelectedCity.addAll( restoreLastSelectedCities() );
		mCitySelectText.setText(getSelectedCityNames());
	}
	
	@Override
	protected void onPause() {
		storeLastSelectedCities(mSelectedCity);
		super.onPause();
	}
	
	private void requestBindVehiclesData() {
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/list/userNo/"+ sharedPreferences.getString(NAME, "");
		RequestClient client = new RequestClient(this);
		client.get(url, new AsyncRequestHandler() {

			@Override
			public void onStart() {
				onRequestStart();
			}

			@Override
			public void onSuccess(String result) {
				if( mVehicleSelectPopupWindow != null ) {
					mVehicleSelectPopupWindow.dismiss();
				}
				VehicleListParser vehicleListParser = new VehicleListParser();
				vehicleListParser.parsing(result);
				if( vehicleListParser.isSuccessfull()) {
					mVehicleAdapter.update(vehicleListParser.getVehicleListResponse().getVehicleList());
				} else {
					TongGouApplication.showToast(vehicleListParser.getVehicleListResponse().getMessage());
				}
			}

			@Override
			public void onFailure(String msg) {
				onRequestFilure(msg);
			}

			@Override
			public void onFinish() {
				onRequestFinish();
			}
			
		});
	}
	
	private void requestJuheSupportCity() {
		String cacheAreaList = PreferenceUtil.getString(this, PREF_NAME_TRANSGRES, PREF_KEY_JUHE_SUPPORT_CITY);
		if( !TextUtils.isEmpty(cacheAreaList) ) {
			parseArea(cacheAreaList);
			return;
		}
		
		RequestClient request = new RequestClient(this);
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+ "/area/juhe/list";
			request.get(url, new AsyncRequestHandler() {

			@Override
			public void onStart() {
				super.onStart();
				onRequestStart();
			}
			
			@Override
			public void onSuccess(String result) {
				super.onSuccess(result);
				TongGouApplication.showLog(result);
				try {
					JSONObject root = new JSONObject(result);
					if( "SUCCESS".equalsIgnoreCase(root.getString("status")) ) {
						String areaList = root.getJSONArray("areaList").toString();
						parseArea(areaList);
						PreferenceUtil.putString(TransgressQueryActivity.this, PREF_NAME_TRANSGRES, PREF_KEY_JUHE_SUPPORT_CITY, areaList);
					} else {
						TongGouApplication.showToast( root.getString("message") );
						TongGouApplication.showLog( root.getString("message") );
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(String msg) {
				onRequestFilure(msg);
			} 

			@Override
			public void onFinish() {
				onRequestFinish();
			}

		});
	}
	
	private void parseArea(String areaList){
		Gson gson = new Gson();
		List<JuheTransgressArea> data = gson.fromJson(areaList,
				new TypeToken<List<JuheTransgressArea>>(){}.getType());
		mProvinceAdapter.update(data);
		mCityAdapter.updateAreaByProvince( data.get(0) );
	}
	
	private synchronized void onRequestStart() {
		TongGouApplication.showLog("onStart");
		getSimpleTitle().showLoadingIndicator();
	}
	
	private synchronized void onRequestFilure(String msg) {
		TongGouApplication.showLog("onFailure  - " + msg);
		TongGouApplication.showToast(msg);
	}
	
	private synchronized void onRequestFinish() {
		TongGouApplication.showLog("onFinish");
		getSimpleTitle().hideLoadingIndicator();
	}
	
	private void storeLastSelectedCities(SelectedAreaList cities) {
		Gson gson = new Gson();
		PreferenceUtil.putString(this, PREF_NAME_TRANSGRES, PREF_KEY_LAST_SELECTED_CITY, gson.toJson(cities));
	}
	
	private ArrayList<JuheTransgressArea> restoreLastSelectedCities() {
		String cities = PreferenceUtil.getString(this, PREF_NAME_TRANSGRES, PREF_KEY_LAST_SELECTED_CITY);
		TongGouApplication.showLog("restoreLastSelectedCities | " + cities);
		if( TextUtils.isEmpty( cities ) || "null".equalsIgnoreCase(cities) ) {
			return new SelectedAreaList();
		}
		Gson gson = new Gson();
		return gson.fromJson(cities, new TypeToken<ArrayList<JuheTransgressArea>>() {}.getType() );
	}
	
	class VehicleAadpter extends AbsViewHolderAdapter<VehicleInfo> {

		public VehicleAadpter(Context context, List<VehicleInfo> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, VehicleInfo itemData) {
			TextView name = getViewFromHolder(convertView, R.id.popview_name);
			name.setText(itemData.getVehicleNo());
		}
		
		@Override
		public void update(List<VehicleInfo> newData) {
			if(newData.isEmpty()) {
				VehicleInfo emptyVehicle = new VehicleInfo();
				emptyVehicle.setVehicleNo(EMPTY_VEHICLE_MSG);
				newData.add(emptyVehicle);
			}
			super.update(newData);
		}
		
	}
	
	class AreaAdapter extends AbsViewHolderAdapter<JuheTransgressArea> {

		public AreaAdapter(Context context, List<JuheTransgressArea> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, JuheTransgressArea itemData) {
			TextView name = getViewFromHolder(convertView, R.id.popview_name);
			name.setText(itemData.getName());
			if( "ACTIVE".equals(itemData.getJuheStatus()) && mSelectedCity.contains(itemData) ) {
				name.setBackgroundResource(R.drawable.cityback);
			} else {
				name.setBackgroundColor(Color.TRANSPARENT);
			}
		}
		
		public void updateAreaByProvince(JuheTransgressArea province) {
			List<JuheTransgressArea> childrenArea = province.getChildren();
			if( childrenArea.isEmpty() ) {
				// 当只有省，没有城市时，那么该省就是城市。例如上海
				childrenArea = new ArrayList<JuheTransgressArea>();
				JuheTransgressArea singleChild = province.clone();
				singleChild.setJuheStatus("ACTIVE");
				province.setJuheStatus("IN_ACTIVE");
				childrenArea.add( singleChild );
				
			}
			update(childrenArea);
		}
		
	}
	
	class SelectedAreaList extends ArrayList<JuheTransgressArea> {

		private static final long serialVersionUID = -3055107039811859485L;
		
		@Override
		public boolean add(JuheTransgressArea object) {
			if(size() >= 3) {
				TongGouApplication.showToast("一次最多只能选择 3 个城市");
				return true;
			}
			remove(object);
			return super.add(object);
		}
		
		@Override
		public boolean addAll( Collection<? extends JuheTransgressArea> collection) {
			clear();
			Iterator<? extends JuheTransgressArea> it = collection.iterator();
			boolean flag = true;
			while( it.hasNext() ) {
				flag = flag && add(it.next());
			}
			return flag;
		}
	}
}
