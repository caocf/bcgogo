package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.guest.GuestVehicleManager;
import com.tonggou.andclient.jsonresponse.BaseResponse;
import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.myview.SimpleTitleBar;
import com.tonggou.andclient.network.parser.AsyncJSONResponseParseHandler;
import com.tonggou.andclient.network.request.StoreVehicleInfosRequest;
import com.tonggou.andclient.vo.VehicleInfo;

/**
 * 绑定游客模式添加的本地车辆（注册流程）
 * @author lwz
 *
 */
public class RegisterBindLocaleVehicleActivity extends AbsTitleBarActivity implements OnItemClickListener {

	private ListView mVehicleListView;
	private VehicleAdapter mAdapter;
	private List<VehicleInfo> mVehicles;
	
	@Override
	protected int getContentLayout() {
		return R.layout.activity_register_bind_local_vechile;
	}

	@Override
	protected void afterTitleBarCreated(SimpleTitleBar titlebar, Bundle savedInstanceState) {
		super.afterTitleBarCreated(titlebar, savedInstanceState);
		titlebar.setTitle("绑定车辆");
		titlebar.setRightButtonText("确定");
		titlebar.setOnRightButtonClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onSubmit();
			}
		});
	}
	
	@Override
	protected void findViews(Bundle savedInstanceState) {
		super.findViews(savedInstanceState);
		mVehicleListView = (ListView) findViewById(R.id.vehicle_listView);
		
		afterViews();
	}

	private void afterViews() {
		GuestVehicleManager manager = new GuestVehicleManager();
		mVehicles = manager.getAllVehicle();
		mAdapter = new VehicleAdapter(this, mVehicles, R.layout.bind_obd_vehicle_item);
		mVehicleListView.setAdapter(mAdapter);
		mVehicleListView.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		VehicleInfo vehicle = mVehicles.get(position);
		vehicle.setIsDefault( "YES".equals(vehicle.getIsDefault()) ? "NO" : "YES" );
		mAdapter.notifyDataSetChanged();
	}
	
	public void onSubmit() {
		List<VehicleInfo> bindVehicles = new ArrayList<VehicleInfo>();
		for( VehicleInfo vehicle : mVehicles ) {
			if( "YES".equals(vehicle.getIsDefault()) ) {
				bindVehicles.add(vehicle);
			}
		}
		
		if( bindVehicles.isEmpty() ) {
			TongGouApplication.showToast("请选择要绑定的车辆");
			return;
		}
		
		doBindVechiles(bindVehicles);
	}
	
	private void doBindVechiles(List<VehicleInfo> bindVehicles) {
		showLoadingDialog("保存中...");
		String userNo = sharedPreferences.getString(NAME, "");
		StoreVehicleInfosRequest request = new StoreVehicleInfosRequest();
		request.setRequestParams(userNo, bindVehicles);
		request.doRequest(this, new AsyncJSONResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				setResult(RESULT_OK);
				finish();
			}
			
			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
			}
		});
	}

	class VehicleAdapter extends AbsViewHolderAdapter<VehicleInfo> {
		public VehicleAdapter(Context context, List<VehicleInfo> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, VehicleInfo itemData) {
			View container = getViewFromHolder(convertView, R.id.real_item_container);
			container.setBackgroundResource(R.drawable.register_editback);
			TextView brandText = (TextView) getViewFromHolder(convertView, R.id.bind_obd_vehicle_item_brand);
			TextView modelText = (TextView) getViewFromHolder(convertView, R.id.bind_obd_vehicle_item_model);
			TextView vehicleNoText = (TextView) getViewFromHolder(convertView, R.id.bind_obd_vehicle_item_vehicleNo);
			CheckBox isBindCheck = (CheckBox) getViewFromHolder(convertView, R.id.bind_obd_vehicle_item_isBind);
			
			brandText.setText(itemData.getVehicleBrand());
			modelText.setText(itemData.getVehicleModel());
			vehicleNoText.setText(itemData.getVehicleNo());
			isBindCheck.setText("是否绑定该车辆");
			isBindCheck.setChecked( "YES".equals( itemData.getIsDefault() ));
			
		}
	}

}
