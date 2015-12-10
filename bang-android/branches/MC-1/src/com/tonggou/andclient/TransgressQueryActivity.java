package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.myview.DoubleListPopupWindow;
import com.tonggou.andclient.myview.SimpleTitleBar;
import com.tonggou.andclient.myview.SingleListPopupWindow;

/**
 * 违章查询
 * @author lwz
 *
 */
public class TransgressQueryActivity extends AbsBackableActivity implements View.OnClickListener {
	
	private TextView mVehicleSelectText;	// 车辆选择框
	private TextView mCitySelectText;		// 城市选择框
	private Button mQueryButton;			// 查询按钮
	private ViewGroup mTotalDeductScoreContainer;	// 累积扣分父容器
	private TextView mTotalDeductScoreText;			// 累积扣分
	private TextView mTotalForfeitText;				// 累积罚款
	private ListView mQueryResultList;				// 查询结果列表
	private SingleListPopupWindow mVehicleSelectPopupWindow;		// 车辆选择弹出框
	private DoubleListPopupWindow mCitySelectPopupWindow;			// 城市选择弹出框

	@Override
	protected int getContentLayout() {
		return R.layout.activity_transgress_query;
	}
	
	@Override
	protected void afterTitleBarCreated(SimpleTitleBar titleBar) {
		super.afterTitleBarCreated(titleBar);
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
	protected void findViews() {
		super.findViews();
		
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

	/**
	 * 选择该用户绑定的汽车
	 */
	private void onSelectVehicle() {
		List<String> data = new ArrayList<String>();
		for( int i=0; i<9; i++ ) {
			data.add("苏E1234" + i);
		}
		VehicleAadpter adapter = new VehicleAadpter(this, data, R.layout.popview_item);
		mVehicleSelectPopupWindow = new SingleListPopupWindow(this, adapter);
		mVehicleSelectPopupWindow.showAsDropDown(mVehicleSelectText);
	}
	
	/**
	 * 选择查询违章的城市
	 */
	private void onSelectCity() {
		List<String> provinceData = new ArrayList<String>();
		List<String> cityData = new ArrayList<String>();
		
		for( int i=0; i<4; i++ ) {
			if( i<5 ) { 
				provinceData.add("江苏" + i);
			}
			
			cityData.add("苏州" + i);
		}
		
		VehicleAadpter provinceAdapter = new VehicleAadpter(this, provinceData, R.layout.popview_item);
		VehicleAadpter cityAdapter = new VehicleAadpter(this, cityData, R.layout.popview_item);
		mCitySelectPopupWindow = new DoubleListPopupWindow(this, provinceAdapter, cityAdapter);
		mCitySelectPopupWindow.showAsDropDown(mCitySelectText);
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

	class VehicleAadpter extends AbsViewHolderAdapter<String> {

		public VehicleAadpter(Context context, List<String> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, String itemData) {
			TextView name = getViewFromHolder(convertView, R.id.popview_name);
			name.setText(itemData);
		}
		
	}
	
}
