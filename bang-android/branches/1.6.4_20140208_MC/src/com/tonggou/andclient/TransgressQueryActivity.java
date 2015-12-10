package com.tonggou.andclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.guest.GuestVehicleManager;
import com.tonggou.andclient.jsonresponse.JuheCityListResponse;
import com.tonggou.andclient.jsonresponse.VehicleListResponse;
import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.myview.DoubleListPopupWindow;
import com.tonggou.andclient.myview.SimpleTitleBar;
import com.tonggou.andclient.myview.SingleListPopupWindow;
import com.tonggou.andclient.network.RequestClient;
import com.tonggou.andclient.network.parser.AsyncJSONResponseParseHandler;
import com.tonggou.andclient.network.request.QueryJuheCityListRequest;
import com.tonggou.andclient.network.request.QueryTransgressReqeust;
import com.tonggou.andclient.network.request.QueryVehicleListRequest;
import com.tonggou.andclient.util.PreferenceUtil;
import com.tonggou.andclient.vo.JuheTransgress;
import com.tonggou.andclient.vo.JuheTransgressArea;
import com.tonggou.andclient.vo.JuheTransgressSearchCondition;
import com.tonggou.andclient.vo.VehicleInfo;
import com.tonggou.andclient.vo.VehicleType;

/**
 * 违章查询
 * @author lwz
 *
 */
public class TransgressQueryActivity extends AbsBackableActivity implements View.OnClickListener {
	
	private final String EMPTY_VEHICLE_MSG = "请点击添加车辆";
	private final String PREF_NAME_TRANSGRES = "pref_transgress";
	private final String PREF_KEY_LAST_SELECTED_CITY = "last_selected_city";
	private final String PREF_KEY_LAST_SELECTED_VEHICLE = "last_selected_vehicle";
	
	public final int REQUEST_CODE_CHANGE_VEHICLE_INFO = 0x123;
	
	private TextView mVehicleTypeSelectText;	// 车辆选择框
	private TextView mVehicleSelectText;		// 车辆选择框
	private TextView mCitySelectText;			// 城市选择框
	private ViewGroup mTotalDeductScoreContainer;	// 累积扣分父容器
	private TextView mTotalDeductScoreText;			// 累积扣分
	private TextView mTotalForfeitText;				// 累积罚款
	private ListView mQueryResultList;				// 查询结果列表
	private Button mQueryButton;					// 查询按钮
	private SingleListPopupWindow mVehicleTypeSelectPopupWindow;	// 车辆选择弹出框
	private SingleListPopupWindow mVehicleSelectPopupWindow;		// 车辆选择弹出框
	private DoubleListPopupWindow mCitySelectPopupWindow;			// 城市选择弹出框
	
	private VehicleTypeAdapter mVehicleTypeAdapter;
	private VehicleAadpter mVehicleAdapter;
	private AreaAdapter mProvinceAdapter;
	private AreaAdapter mCityAdapter;
	private TransgressAdapter mTransgressAdapter;
	
	private VehicleType mVehicleType;				// 选中的 车辆类型
	private VehicleInfo mVehicleInfo;				// 选中的车（待查询）
	
	private int mScore = 0;		// 累积扣分
	private int mMoney = 0;		// 累积罚款
	
	private boolean isVehicleDataRequestFinished = true;
	private boolean isJuheCityRequestFinished = true;
	
	private JuheTransgressArea mSelectedCity;
	
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
		
		mVehicleTypeSelectText = (TextView) findViewById(R.id.select_vehicle_type_text);
		mVehicleSelectText = (TextView) findViewById(R.id.select_vehicle_text);
		mCitySelectText = (TextView) findViewById(R.id.select_city_text);
		mTotalDeductScoreContainer = (ViewGroup) findViewById(R.id.total_deduct_container);
		mTotalDeductScoreText = (TextView) findViewById(R.id.total_deduct_score);
		mTotalForfeitText = (TextView) findViewById(R.id.total_forfeit);
		mQueryResultList = (ListView) findViewById(R.id.query_result_list);
		mQueryButton = (Button) findViewById(R.id.query_btn);
		afterViews();
	}

	private void afterViews() {
		setIsRequestVehicleDataFinished(true);
		setIsJuheCityRequestFinished(true);
		createAdapter();
		setDefaultVehicleType();
		updateVehicleType();
		mQueryResultList.setEmptyView(findViewById(R.id.listview_empty_view));
		mQueryResultList.setAdapter(mTransgressAdapter);
		setListener();
	}

	private void setListener() {
		mVehicleTypeSelectText.setOnClickListener(this);
		mVehicleSelectText.setOnClickListener(this);
		mCitySelectText.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.select_vehicle_type_text: onSelectVehicleType(); break;
			case R.id.select_vehicle_text: onSelectVehicle(); break;
			case R.id.select_city_text: onSelectCity(); break;
		}
	}
	
	public void onQueryBtnClick(View v) {
		if( ! isVehicleDataRequestFinished ) {
			TongGouApplication.showToast("车辆数据正在获取中,请稍候...");
			return;
		}
		String vehicleNo = mVehicleSelectText.getText().toString().trim();
		if( TextUtils.isEmpty(vehicleNo) ) {
			TongGouApplication.showToast("请选择车牌号");
			return;
		}
		if(  mSelectedCity == null ) {
			TongGouApplication.showToast("请选择城市");
			return;
		}
		if( ! isCanQuery(mVehicleInfo, mSelectedCity) ) {
//			TongGouApplication.showToast("您的车辆信息不完整，请补全");
			turnToChangeBindCarActivity(mVehicleInfo);
			return;
		}
		
		mTransgressAdapter.clear();
		doRequestQuery();
	}
	
	private void doRequestQuery() {
		mQueryButton.setEnabled(false);
		requestQueryResult(mSelectedCity);
	}

	private void createAdapter() {
		mVehicleTypeAdapter = new VehicleTypeAdapter(this, new ArrayList<VehicleType>(), R.layout.popview_item);
		mVehicleAdapter = new VehicleAadpter(this, new ArrayList<VehicleInfo>(), R.layout.popview_item);
		mProvinceAdapter = new AreaAdapter(this, new ArrayList<JuheTransgressArea>(), R.layout.popview_item, true);
		mCityAdapter = new AreaAdapter(this, new ArrayList<JuheTransgressArea>(), R.layout.popview_item, false);
		mTransgressAdapter = new TransgressAdapter(this, new ArrayList<JuheTransgress>(), R.layout.item_list_transgress);
	}
	
	private void setDefaultVehicleType() {
		mVehicleType = new VehicleType();
		mVehicleType.setCar("小型车");
		mVehicleType.setId("02");
		mVehicleTypeSelectText.setText(mVehicleType.getCar());
	}
	
	private void onSelectVehicleType() {
		mVehicleTypeSelectPopupWindow = new SingleListPopupWindow(this, mVehicleTypeAdapter);
		mVehicleTypeSelectPopupWindow.setOnListItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				VehicleType type = mVehicleTypeAdapter.getData().get(position);
				mVehicleType = type;
				mVehicleTypeSelectText.setText(type.getCar());
			}
		});
		mVehicleTypeSelectPopupWindow.showAsDropDown(mVehicleTypeSelectText);
	}

	/**
	 * 选择该用户绑定的汽车
	 */
	private void onSelectVehicle() {
		
		if( mVehicleAdapter.isEmpty() && isVehicleDataRequestFinished) {
			requestBindVehiclesData();
		}
		mVehicleSelectPopupWindow = new SingleListPopupWindow(this, mVehicleAdapter);
		
		mVehicleSelectPopupWindow.setOnListItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				VehicleInfo vehicleInfo = mVehicleAdapter.getData().get(position);
				if( EMPTY_VEHICLE_MSG.equals(vehicleInfo.getVehicleNo()) ) {
					turnToAddBindCarActivit();
					return;
				} else {
					setDefaultSelectVehicle(vehicleInfo);
				}
			}
		});
		mVehicleSelectPopupWindow.showAsDropDown(mVehicleSelectText);
	}
	
	private void turnToAddBindCarActivit() {
		Intent intent = new Intent();
		intent.setClass(this, AddBindCarActivity.class);
		startActivity(intent);
	}
	
	private void turnToChangeBindCarActivity(VehicleInfo vehicleInfo) {
		Intent intent = new Intent();
		intent.setClass(this, ChangeTransgressQueryConditionActivity.class);
		intent.putExtra(ChangeTransgressQueryConditionActivity.KEY_PARAM_VEHICLE_INFO, vehicleInfo);
		intent.putExtra(ChangeTransgressQueryConditionActivity.KEY_PARAM_SELECTED_CITY, mSelectedCity);
		startActivityForResult(intent, REQUEST_CODE_CHANGE_VEHICLE_INFO);
	}
	
	/**
	 * 选择查询违章的城市
	 */
	private void onSelectCity() {
		if( mProvinceAdapter.isEmpty() ) {
			if( isJuheCityRequestFinished ) {
				requestJuheSupportCity();
			} else {
				TongGouApplication.showToast("正在获取城市数据，请稍候");
			}
			
			return;
		}
		
		if(mCitySelectPopupWindow != null) {
			mCitySelectPopupWindow.dismiss();
		}
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
				mSelectedCity = area;
				mCitySelectText.setText(area.getName());
			}
		});
		mCitySelectPopupWindow.showAsDropDown(mCitySelectText);
	}
	
	/**
	 * 显示累计扣分及罚款的布局
	 * @param deductScore 扣分数
	 * @param forfeit 罚款数
	 */
	@SuppressWarnings("unused")
	private void showTotalDeductContainer(String deductScore, String forfeit) {
		mTotalDeductScoreContainer.setVisibility(View.VISIBLE);
		mTotalDeductScoreText.setText(deductScore);
		mTotalForfeitText.setText(forfeit);
	}
	
	/**
	 * 隐藏累计扣分及罚款的布局
	 */
	@SuppressWarnings("unused")
	private void hideTotalDeductContainer() {
		mTotalDeductScoreContainer.setVisibility(View.GONE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		queryBindVehiclesData();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		requestJuheSupportCity();
		mSelectedCity = restoreLastSelectedCity();
		if( mSelectedCity != null ) {
			mCitySelectText.setText(mSelectedCity.getName());
		}
	}
	
	@Override
	protected void onPause() {
		storeLastSelectedCities(mSelectedCity);
		super.onPause();
	}
	
	private void updateVehicleType() {
		String vehicleJsonTypeStr = "[{\"car\":\"大型车\",\"id\":\"01\"},{\"car\":\"小型车\",\"id\":\"02\"},{\"car\":\"使馆汽车\",\"id\":\"03\"},{\"car\":\"领馆汽车\",\"id\":\"04\"},{\"car\":\"境外汽车\",\"id\":\"05\"},{\"car\":\"外籍汽车\",\"id\":\"06\"},{\"car\":\"教练汽车\",\"id\":\"16\"}]";
		Gson gson = new Gson();
		List<VehicleType> vehicleTypeData = 
				gson.fromJson(vehicleJsonTypeStr, new TypeToken<List<VehicleType>>(){}.getType());
		mVehicleTypeAdapter.update(vehicleTypeData);
	}
	
	
	private void queryBindVehiclesData() {
		if( !TongGouApplication.getInstance().isLogin() ) {
			// 游客模式
			getGuestVehicleData();
		} else {
			// 登录模式
//			requestBindVehiclesData();
			updateVehicleData( TongGouApplication.sVehicleList );
		}
	}
	
	private void getGuestVehicleData() {
		GuestVehicleManager manager = new GuestVehicleManager();
		updateVehicleData( manager.getAllVehicle() );
	}
	
	/**
	 * 登录用户调用该方法
	 */
	private void requestBindVehiclesData() {
		setIsRequestVehicleDataFinished(false);
		String userName = PreferenceUtil.getString(this, BaseActivity.SETTING_INFOS, BaseActivity.NAME);
		QueryVehicleListRequest request = new QueryVehicleListRequest();
		request.setApiParams(userName);
		request.doRequest(this, new AsyncJSONResponseParseHandler<VehicleListResponse>() {

			@Override
			public void onParseSuccess(VehicleListResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				updateVehicleData( result.getVehicleList() );
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				super.onFailure(arg0, arg1, arg2, arg3);
				TongGouApplication.showToast("获取车辆信息失败");
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				setIsRequestVehicleDataFinished(true);
				onRequestFinish();
				if( mVehicleTypeSelectPopupWindow != null ) {
					mVehicleSelectPopupWindow.dismiss();
				}
			}
			
			@Override
			public Class<VehicleListResponse> getTypeClass() {
				return VehicleListResponse.class;
			}
			
		});
	}
	
	/**
	 * 更新车辆信息
	 * @param data
	 */
	private void updateVehicleData(List<VehicleInfo> data) {
		checkDefaultVehicle(data);
		mVehicleAdapter.update(data);
	}
	
	private synchronized void setIsRequestVehicleDataFinished(boolean isFinished) {
		this.isVehicleDataRequestFinished = isFinished;
	}
	
	private void checkDefaultVehicle(List<VehicleInfo> vehicleInfos) {
		if( vehicleInfos == null ) {
			vehicleInfos = new ArrayList<VehicleInfo>();
		}
		String selectedVehicleStr = PreferenceUtil.getString(this, PREF_NAME_TRANSGRES, getPrefKeyByUser(PREF_KEY_LAST_SELECTED_VEHICLE));
		if( !TextUtils.isEmpty( selectedVehicleStr)  ) {
			Gson gson = new Gson();
			VehicleInfo selectedVehicle = gson.fromJson(selectedVehicleStr, VehicleInfo.class);
			if( vehicleInfos.contains(selectedVehicle) ) {
				// 数据可能变化，故重新设置
				selectedVehicle = vehicleInfos.get( vehicleInfos.indexOf(selectedVehicle) );
				setDefaultSelectVehicle(selectedVehicle);
				return;
			}
		}
		
		for( VehicleInfo vehicleInfo : vehicleInfos ) {
			if( "YES".equals( vehicleInfo.getIsDefault() )) {
				setDefaultSelectVehicle(vehicleInfo);
				break;
			}
		}
	}
	
	private void setDefaultSelectVehicle(VehicleInfo v) {
		mVehicleInfo = v;
		mVehicleSelectText.setText(mVehicleInfo.getVehicleNo());
		PreferenceUtil.putString(this, PREF_NAME_TRANSGRES, getPrefKeyByUser(PREF_KEY_LAST_SELECTED_VEHICLE), new Gson().toJson(v));
	}
	
	/**
	 * 该车辆是否可查询（信息是否完整）
	 * @param vehicleInfo
	 * @return
	 */
	private boolean isCanQuery(VehicleInfo vehicleInfo, JuheTransgressArea selectCity) {
		
		boolean[] isNeed = getQueryCondition(selectCity);
		if( isNeed[0] && TextUtils.isEmpty( vehicleInfo.getEngineNo() )) {
			return false;
		}
		if( isNeed[1] && TextUtils.isEmpty(vehicleInfo.getVehicleVin()) ) {
			return false;
		} 
		if( isNeed[2] && TextUtils.isEmpty( vehicleInfo.getRegistNo() ) ) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 得到查询条件
	 * @return boolean[0] = 发动机号，boolean[1] = 车架号， boolean[2] = 登记证书号，当为 true 时必须要
	 */
	public static boolean[] getQueryCondition(JuheTransgressArea selectCity) {
		boolean[] isNeed = new boolean[3];
		JuheTransgressSearchCondition condition = selectCity.getJuheViolateRegulationCitySearchCondition();
		isNeed[0] = isNeed[0] || ( condition.getEngine() != 0);
		isNeed[1] = isNeed[1] || ( condition.getClassa() != 0);
		isNeed[2] = isNeed[2] || ( condition.getRegist() != 0);
		return isNeed;
	}
	
	
	private void requestJuheSupportCity() {
		setIsJuheCityRequestFinished(false);
		showLoadingDialog("加载中...");
		onRequestStart();
		QueryJuheCityListRequest request = new QueryJuheCityListRequest();
		request.doRequest(this, new AsyncJSONResponseParseHandler<JuheCityListResponse>() {

			@Override
			public void onParseSuccess(JuheCityListResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				List<JuheTransgressArea> data = result.getResult();
				if( data == null || data.isEmpty() ) {
					TongGouApplication.showToast("城市列表获取失败");
					return;
				}
				mProvinceAdapter.update(data);
				mCityAdapter.updateAreaByProvince( data.get(0) );
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
				super.onParseFailure(errorCode, errorMsg);
				onRequestFilure(errorMsg);
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				setIsJuheCityRequestFinished(true);
				onRequestFinish();
				dismissLoadingDialog();
			}
			
			@Override
			public Class<JuheCityListResponse> getTypeClass() {
				return JuheCityListResponse.class;
			}
			
		});
		
	}
	
	private synchronized void setIsJuheCityRequestFinished(boolean isFinished) {
		this.isJuheCityRequestFinished = isFinished;
	}
	
	private void requestQueryResult(JuheTransgressArea city) {
		showLoadingDialog("查询中...");
		QueryTransgressReqeust request = new QueryTransgressReqeust();
		JuheTransgressSearchCondition condition = city.getJuheViolateRegulationCitySearchCondition();
		request.setRequestParams(
				city.getJuheCityCode(), 
				mVehicleInfo.getVehicleNo(), 
				mVehicleType.getId(), 
				mVehicleInfo.getEngineNo(), 
				mVehicleInfo.getVehicleVin(), 
				mVehicleInfo.getRegistNo(), 
				condition);
		request.doRequest(this, new JsonHttpResponseHandler() {

			private boolean isQuerySuccess = false;
			private String reason = null;
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				TongGouApplication.showLog(getRequestURI());
				try {
					if( "200".equals( response.getString("resultcode")) ) {
						Gson gson = new Gson();
						List<JuheTransgress> data = 
								gson.fromJson(response.getJSONObject("result").getJSONArray("lists").toString(), 
												new TypeToken<List<JuheTransgress>>(){}.getType());
						mTransgressAdapter.update(data);
					}
					reason = response.getString("reason");
					isQuerySuccess = true;
					TongGouApplication.showLog(response.toString());
				} catch (JSONException e) {
					TongGouApplication.showToast("系统维护中，暂时无法查询");
				}
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
				onRequestQueryFinish(isQuerySuccess, reason);
			}
			
		});
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
		if( isJuheCityRequestFinished && isVehicleDataRequestFinished ) {
			getSimpleTitle().hideLoadingIndicator();
		}
	}
	
	/**
	 * 违章查询结束
	 * @param isSuccess 是否查询成功
	 */
	private synchronized void onRequestQueryFinish(boolean isSuccess, String msg) {
		if( !isSuccess ) {
			// 所有查询都出错了
			TongGouApplication.showToast("违章记录查询失败");
		} else if( mTransgressAdapter.isEmpty()) {
			TongGouApplication.showToast(msg);
		}
		mQueryButton.setEnabled(true);
		getSimpleTitle().hideLoadingIndicator();
	}
	
	private void storeLastSelectedCities(JuheTransgressArea city) {
		Gson gson = new Gson();
		PreferenceUtil.putString(this, PREF_NAME_TRANSGRES, getPrefKeyByUser(PREF_KEY_LAST_SELECTED_CITY), gson.toJson(city));
	}
	
	private JuheTransgressArea restoreLastSelectedCity() {
		String cities = PreferenceUtil.getString(this, PREF_NAME_TRANSGRES, getPrefKeyByUser(PREF_KEY_LAST_SELECTED_CITY));
		TongGouApplication.showLog("restoreLastSelectedCities | " + cities);
		if( !TextUtils.isEmpty( cities ) && !"null".equalsIgnoreCase(cities) ) {
			Gson gson = new Gson();
			try {
				JuheTransgressArea data = (JuheTransgressArea)gson.fromJson(cities, JuheTransgressArea.class );
				if( data != null ) {
					return data;
				}
			} catch (Exception e) {
				PreferenceUtil.remove(this, PREF_NAME_TRANSGRES, getPrefKeyByUser(PREF_KEY_LAST_SELECTED_CITY));
			}
			
		}
		
		return null;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( resultCode == RESULT_OK && resultCode == REQUEST_CODE_CHANGE_VEHICLE_INFO) {
			mCitySelectText.setText("");
			mVehicleInfo = null;
		}
	}
	
	@Override
	protected void onStop() {
		RequestClient.cancle();
		super.onStop();
	}
	
	/**
	 * 通过用户名来得到 sharedPreferences 的键（键和用户绑定）
	 * @param prefKey
	 * @return
	 */
	private String getPrefKeyByUser( String prefKey ) {
		String prefSuffix = null;
		if( !TongGouApplication.getInstance().isLogin() ) {
			prefSuffix = "guest";
		} else {
			prefSuffix = sharedPreferences.getString(NAME, "");
		}
		return prefKey + prefSuffix;
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(this, HomePageActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		super.onBackPressed();
	}
	
	class VehicleTypeAdapter extends AbsViewHolderAdapter<VehicleType> {

		public VehicleTypeAdapter(Context context, List<VehicleType> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, VehicleType itemData) {
			TextView name = getViewFromHolder(convertView, R.id.popview_name);
			name.setText(itemData.getCar());
		}
		
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
		
		@SuppressWarnings("unchecked")
		@Override
		public synchronized void update( Collection<? extends VehicleInfo> newData) {
			if( newData == null ) {
				newData = new ArrayList<VehicleInfo>();
			}
			if(newData.isEmpty()) {
				mVehicleSelectText.setText("");
				VehicleInfo emptyVehicle = new VehicleInfo();
				emptyVehicle.setVehicleNo(EMPTY_VEHICLE_MSG);
				((ArrayList<VehicleInfo>)newData).add(emptyVehicle);
			}
			super.update(newData);
		}
		
	}
	
	class AreaAdapter extends AbsViewHolderAdapter<JuheTransgressArea> {

		private boolean isProvince = true;
		
		public AreaAdapter(Context context, List<JuheTransgressArea> data, int layoutRes, boolean isProvince) {
			super(context, data, layoutRes);
			this.isProvince = isProvince;
		}

		@Override
		protected void setData(int pos, View convertView, JuheTransgressArea itemData) {
			TextView name = getViewFromHolder(convertView, R.id.popview_name);
			name.setText(itemData.getName());
			if( "ACTIVE".equals(itemData.getJuheStatus()) && itemData.equals(mSelectedCity) && !isProvince ) {
				name.setBackgroundColor(Color.parseColor("#55FFFFFF"));
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
	
	class TransgressAdapter extends AbsViewHolderAdapter<JuheTransgress> {

		public TransgressAdapter(Context context, List<JuheTransgress> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, JuheTransgress itemData) {
			TextView time = getViewFromHolder(convertView, R.id.transgress_date);
			TextView area = getViewFromHolder(convertView, R.id.transgress_area);
			TextView act = getViewFromHolder(convertView, R.id.transgress_act);
			TextView code = getViewFromHolder(convertView, R.id.transgress_code);
			TextView score = getViewFromHolder(convertView, R.id.transgress_score);
			TextView money = getViewFromHolder(convertView, R.id.transgress_money);
			
			time.setText(itemData.getDate());
			area.setText(itemData.getArea());
			act.setText(itemData.getAct());
			code.setText("# " + itemData.getCode());
			score.setText(itemData.getFen());
			money.setText("￥ " + itemData.getMoney());
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public synchronized void update(Collection<? extends JuheTransgress> newData) {
			resetScoreAndMoney();
			Collections.sort((List<JuheTransgress>)newData, mSortByTimComparator);
			for( JuheTransgress jt : newData ) {
				int fen = 0;
				int money = 0;
				try{
					fen = Integer.valueOf( jt.getFen() );
				} catch (NumberFormatException e) {
					;
				}
				try{
					money = Integer.valueOf( jt.getMoney() );
				} catch (NumberFormatException e) {
					;
				}
				mScore += fen;
				mMoney += money;
			}
			setScoreAndMoney(mScore + "", mMoney + "");
			super.update(newData);
		}
		
		@Override
		public void clear() {
			resetScoreAndMoney();
			super.clear();
		}
		
		private Comparator<JuheTransgress> mSortByTimComparator = new Comparator<JuheTransgress>() {
			
			@Override
			public int compare(JuheTransgress lhs, JuheTransgress rhs) {
				return getTimeByDateStr( rhs.getDate() ).compareTo( 
								getTimeByDateStr( lhs.getDate() ));
			}
			
			private Long getTimeByDateStr( String date ) {
				SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
				try {
					return formatDate.parse( date ).getTime();
				} catch (ParseException e) {
					return 0L;
				}
			}
		};
		
	}
	
	/**
	 * 重置扣分和罚款
	 */
	private void resetScoreAndMoney() {
		mScore = 0;
		mMoney = 0;
		setScoreAndMoney(mScore + "", mMoney + "");
	}
	
	private void setScoreAndMoney( String score, String money ) {
		mTotalDeductScoreText.setText( score );
		mTotalForfeitText.setText( money );
	}
}
