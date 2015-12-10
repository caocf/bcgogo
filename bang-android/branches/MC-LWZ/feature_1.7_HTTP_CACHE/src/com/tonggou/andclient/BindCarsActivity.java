package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.tonggou.andclient.app.BaseConnectOBDService;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.db.VehicleManager;
import com.tonggou.andclient.jsonresponse.BaseResponse;
import com.tonggou.andclient.jsonresponse.VehicleListResponse;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.network.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.parser.AsyncLoadCacheJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.DeleteVehicleRequest;
import com.tonggou.andclient.network.request.QueryVehicleListRequest;
import com.tonggou.andclient.network.request.UpdateDefaultVehicleRequest;
import com.tonggou.andclient.util.NetworkUtil;
import com.tonggou.andclient.util.PreferenceUtil;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.vo.VehicleInfo;
/**
 * 绑定车辆页面
 * @author think
 *
 */
public class BindCarsActivity extends BaseActivity{

	private BindcarsAdapter bindcarsAdapter;
	private ListView bindcarsListView;
	private View addView,back;
	private String  userNo;
	private List<VehicleInfo> vehicleList;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bindcars);

		bindcarsListView = (ListView) findViewById(R.id.bindcars);
		addView=LayoutInflater.from(this).inflate(R.layout.addbindcar_item, null);
		bindcarsListView.setDividerHeight(0);
		bindcarsListView.addFooterView(addView);
		bindcarsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> eachAdaper, View arg1, int position, long arg3) {
				if (position == vehicleList.size()) {
					if( TongGouApplication.getInstance().isLogin() && 
							!NetworkUtil.isNetworkConnected(BindCarsActivity.this) ) {
						TongGouApplication.showToast(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE);
						return;
					}
					Intent intent = new Intent(BindCarsActivity.this,AddBindCarActivity.class);
					startActivityForResult(intent, 3030);
					return;
				}else{
					Intent intent = new Intent(BindCarsActivity.this,ChangeBindCarsActivity.class);
					intent.putExtra(ChangeBindCarsActivity.EXTRA_VEHICLE_INFO, vehicleList.get(position));
					startActivityForResult(intent, 5050);
				}
			}
		});
		bindcarsListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if( !NetworkUtil.isNetworkConnected(BindCarsActivity.this) ) {
					TongGouApplication.showToast(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE);
					return true;
				}
				// TODO Auto-generated method stub
				if (arg2 <vehicleList.size()) {
					final  int  position=arg2;
					AlertDialog.Builder builder = new AlertDialog.Builder(BindCarsActivity.this);
					builder.setMessage("删除车辆?")
					.setCancelable(false)
					.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							deleteVehicle( vehicleList.get(position) );
						}
					}).setNegativeButton(getString(R.string.exit_cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					}).show();
				}

				return true;
			};
		});

		back= findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BindCarsActivity.this.finish();
			}
		});

		initVehicleList();
	}
	
	private void initVehicleList() {
		vehicleList = new ArrayList<VehicleInfo>();
		bindcarsAdapter = new BindcarsAdapter(BindCarsActivity.this,vehicleList); 
		bindcarsListView.setAdapter(bindcarsAdapter);
		if( !TongGouApplication.getInstance().isLogin() ) {
			userNo = null;
			updateVehicleData();
		} else {
			//请求车辆列表网络
			userNo = getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME, null);
			doQueryVehicleList();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		updateVehicleData();
	}
	
	private void updateVehicleData() {
		vehicleList.clear();
		vehicleList.addAll(new VehicleManager().getAllVehicle(userNo));
		bindcarsAdapter.notifyDataSetChanged();
	}
	
	private void deleteVehicle(final VehicleInfo vehicleInfo) {
		
		if( !TongGouApplication.getInstance().isLogin() ) {
			new VehicleManager().deleteByVehicleNo(null, vehicleInfo.getVehicleNo());
			initVehicleList();
			
		} else {
			doDeleteVehicle(vehicleInfo.getVehicleId(), vehicleInfo.getIsDefault(), vehicleInfo.getVehicleModelId());
		}
	}
	
	private  void doQueryVehicleList(){
		showLoadingDialog("加载车辆信息...");
		QueryVehicleListRequest request = new QueryVehicleListRequest();
		request.setApiParams(userNo);
		request.doRequest(this, new AsyncLoadCacheJsonBaseResponseParseHandler<VehicleListResponse>() {

			@Override
			public void onParseSuccess(VehicleListResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				String userNo = PreferenceUtil.getString(BindCarsActivity.this, BaseActivity.SETTING_INFOS, BaseActivity.NAME);
				new VehicleManager().updateAllVehicle(userNo, result.getVehicleList());
				TongGouApplication.getInstance().setDefaultVehicleBindOBDs(result.getVehicleList());
				updateVehicleData();
			}
			
			@Override
			public void onFinish() {
				dismissLoadingDialog();
				super.onFinish();
			}
			
			@Override
			public Class<VehicleListResponse> getTypeClass() {
				return VehicleListResponse.class;
			}

			@Override
			public void onLoadCache(VehicleListResponse result, String originResult,
					boolean isNetworkConnected) {
				if( result != null ) {
					TongGouApplication.getInstance().setDefaultVehicleBindOBDs(result.getVehicleList());
				}
				updateVehicleData();
			}
		});
		
	}
	
	private void doDeleteVehicle(final String vehicleId,final String ifdefault,final String modelId){ 
		showLoadingDialog("删除车辆...");
		DeleteVehicleRequest request = new DeleteVehicleRequest();
		request.setApiParams(vehicleId);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				if( ifdefault!=null && "YES".equals(ifdefault) ){
					getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
					.putString(BaseActivity.BRAND, "")
					.putString(BaseActivity.MODEL, "")
					.putString(BaseActivity.VEHICLENUM,"").commit();
				}
				//删除字典
				SaveDB.getSaveDB(BindCarsActivity.this).deleteModleFaultCodes(currentUsername+modelId);
				TongGouApplication.showToast(result.getMessage());
				new VehicleManager().deleteByVehicleId(userNo, vehicleId);
				doQueryVehicleList();
			}
			
			@Override
			public void onFinish() {
				dismissLoadingDialog();
				super.onFinish();
			}

			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
			
		});
		
	}
	private class BindcarsAdapter extends BaseAdapter{	
		private LayoutInflater layoutInflater;
		private List<VehicleInfo>  cars;
		Context context;
		public BindcarsAdapter(Context context, List<VehicleInfo>  cars){
			layoutInflater = LayoutInflater.from(context);	
			this.context=context;
			this.cars =  cars;
		}
		public int getCount() {			
			return cars.size();
		}
		public VehicleInfo getItem(int position) {
			return cars.get(position);
		}
		public long getItemId(int position) {		
			return position;
		}
		
		public List<VehicleInfo> getData() {
			return cars;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {					
			if(convertView == null){
				convertView = layoutInflater.inflate(R.layout.bindcars_item, null);
			}				
			if(position==0){
				convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bindcar_up));
			}else{
				convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bindcarmiddle));
			}
			TextView brandView = ((TextView)convertView.findViewById(R.id.carbrand));
			if(getItem(position).getVehicleBrand()!=null&&!"".equals(getItem(position).getVehicleBrand())){
				brandView.setText(getResources().getString(R.string.bindcar_carbrand)+getItem(position).getVehicleBrand());
			}
			TextView numView = ((TextView)convertView.findViewById(R.id.carnum));
			if(getItem(position).getVehicleNo()!=null&&!"".equals(getItem(position).getVehicleNo())){
				numView.setText(getResources().getString(R.string.app_network_servicecarnum)+getItem(position).getVehicleNo());
			}
			TextView classView = ((TextView)convertView.findViewById(R.id.carmodel));
			if(getItem(position).getVehicleModel()!=null&&!"".equals(getItem(position).getVehicleModel())){
				classView.setText(getResources().getString(R.string.orderdetil_carstate)+getItem(position).getVehicleModel());
			}
			TextView numberView = ((TextView)convertView.findViewById(R.id.carnumber));
			if(getItem(position).getObdSN()!=null&&!"".equals(getItem(position).getObdSN())&&!"null".equals(getItem(position).getObdSN())){
				numberView.setText(getResources().getString(R.string.bindcar_carvin)+getItem(position).getObdSN());
			}else{
				numberView.setText(getResources().getString(R.string.bindcar_carvin));
			}
			final String vehicleId=getItem(position).getVehicleId();
			final int positionID=position;
			CheckBox check = ((CheckBox)convertView.findViewById(R.id.set_checkbox));
			if(getItem(position).getIsDefault()!=null&&"YES".equals(getItem(position).getIsDefault())){
				check.setChecked(true);
				getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
				.putString(BaseActivity.BRAND, vehicleList.get(position).getVehicleBrand())
				.putString(BaseActivity.MODEL, vehicleList.get(position).getVehicleModel())
				.putString(BaseActivity.VEHICLENUM,vehicleList.get(position).getVehicleNo()).commit();
				MainActivity.defaultBrandAndModle = vehicleList.get(position).getVehicleBrand()+" " +vehicleList.get(position).getVehicleModel();
			}else{
				check.setChecked(false);
			}
			
			if( TongGouApplication.getInstance().isLogin() 
					&& !NetworkUtil.isNetworkConnected(getBaseContext())) {
				check.setEnabled(false);
				return convertView;
			}
			check.setEnabled(true);
			check.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(((CheckBox)v).isChecked()){
						((CheckBox)v).setChecked(false);
						new AlertDialog.Builder(BindCarsActivity.this) 		
						.setTitle(getString(R.string.exit_title)) 
						.setMessage(getString(R.string.set_bindcar_default))
						.setPositiveButton(getString(R.string.exit_submit), new DialogInterface.OnClickListener() { 
							public void onClick(DialogInterface dialog, int whichButton) {
								//请求网络
								resetDefaultVehicle(vehicleId, positionID);
							} 
						}).setNeutralButton(getString(R.string.exit_cancel), new DialogInterface.OnClickListener(){ 
							public void onClick(DialogInterface dialog, int whichButton){ 
							} 
						}).show();
					}else{

					}
				}
			});
			return convertView;			
		}		
	}
	
	private void resetDefaultVehicle(final String vehicleId, final int positionID) {
		if( !TongGouApplication.getInstance().isLogin() ) {
			VehicleManager manager = new VehicleManager();
			manager.updateVehicle2Default(vehicleList.get(positionID));
			initVehicleList();
		} else {
			setCheck(vehicleId,positionID);
		}
	}
	
	public void onResume(){
		super.onResume();
		BaseConnectOBDService.addingCar = false;
	}
	
	private void setCheck(String vehicleId,final int position){ 
		showLoadingDialog("设置中...");
		UpdateDefaultVehicleRequest request = new UpdateDefaultVehicleRequest();
		request.setRequestParams(vehicleId);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				for(int i=0;i<vehicleList.size();i++){

					if(position == i){
						vehicleList.get(i).setIsDefault("YES");
						MainActivity.defaultBrandAndModle = vehicleList.get(i).getVehicleBrand()+" " +vehicleList.get(i).getVehicleModel();							
					}else{
						vehicleList.get(i).setIsDefault("NO");
					}
				}
//				TongGouApplication.getInstance().queryVehicleList();
				bindcarsAdapter.notifyDataSetChanged();
				TongGouApplication.getInstance().setDefaultVehicleBindOBDs(vehicleList);
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
			}
			
			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
		});
		
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( resultCode != Activity.RESULT_OK ) {
			return;
		}
		TongGouApplication.showLog("requestCode   " + requestCode);
		if(requestCode==3030||requestCode==5050){		
			if("yes".equals(data.getStringExtra("tonggou.isOk"))){		
				
				initVehicleList();
			}
		}/*if(resultCode==5050){		
			//请求网络
			if("yes".equals(data.getStringExtra("tonggou.isOk"))){			
				new Thread(){
					public void run(){
						networking(userNo);
					}
				}.start();
			}
		}*/
	}
}
