package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.app.BaseConnectOBDService;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.guest.GuestVehicleManager;
import com.tonggou.andclient.jsonresponse.BaseResponse;
import com.tonggou.andclient.jsonresponse.VehicleListResponse;
import com.tonggou.andclient.jsonresponse.VehicleResponse;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.network.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.DeleteVehicleRequest;
import com.tonggou.andclient.network.request.QueryVehicleListRequest;
import com.tonggou.andclient.network.request.UpdateDefaultVehicleRequest;
import com.tonggou.andclient.parse.DeleteVehicleParser;
import com.tonggou.andclient.parse.SetCheckParser;
import com.tonggou.andclient.parse.VehicleListParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.vo.VehicleInfo;
/**
 * ∞Û∂®≥µ¡æ“≥√Ê
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
		userNo=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME, null);

		bindcarsListView = (ListView) findViewById(R.id.bindcars);
		addView=LayoutInflater.from(this).inflate(R.layout.addbindcar_item, null);
		bindcarsListView.setDividerHeight(0);
		bindcarsListView.addFooterView(addView);
		bindcarsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> eachAdaper, View arg1, int position, long arg3) {
				if (position == vehicleList.size()) {
					Intent intent = new Intent(BindCarsActivity.this,AddBindCarActivity.class);
					startActivityForResult(intent, 3030);
					return;
				}else{
					Intent intent = new Intent(BindCarsActivity.this,ChangeBindCarsActivity.class);
					intent.putExtra("tonggou.veId",vehicleList.get(position).getVehicleId());
					startActivityForResult(intent, 5050);
				}
			}
		});
		bindcarsListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 <vehicleList.size()) {
					final  int  position=arg2;
					AlertDialog.Builder builder = new AlertDialog.Builder(BindCarsActivity.this);
					builder.setMessage("…æ≥˝≥µ¡æ?")
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
			GuestVehicleManager manager = new GuestVehicleManager();
			updateVehicleData(manager.getAllVehicle());
		} else {
			//«Î«Û≥µ¡æ¡–±ÌÕ¯¬Á
			doQueryVehicleList();
		}
		
	}
	
	private void updateVehicleData(List<VehicleInfo> vehicles) {
		TongGouApplication.getInstance().setDefaultVehicleBindOBDs(vehicles);
		vehicleList.clear();
		vehicleList.addAll(vehicles);
		bindcarsAdapter.notifyDataSetChanged();
	}
	
	private void deleteVehicle(final VehicleInfo vehicleInfo) {
		
		if( !TongGouApplication.getInstance().isLogin() ) {
			GuestVehicleManager manager = new GuestVehicleManager();
			manager.delete(vehicleInfo.getVehicleId());
			initVehicleList();
			
		} else {
			doDeleteVehicle(vehicleInfo.getVehicleId(), vehicleInfo.getIsDefault(), vehicleInfo.getVehicleModelId());
		}
	}
	
	private  void doQueryVehicleList(){
		showLoadingDialog("º”‘ÿ≥µ¡æ–≈œ¢...");
		QueryVehicleListRequest request = new QueryVehicleListRequest();
		request.setApiParams(userNo);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<VehicleListResponse>() {

			@Override
			public void onParseSuccess(VehicleListResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				updateVehicleData(result.getVehicleList());
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
		});
		
	}
	
	private void doDeleteVehicle(String vehicleId,final String ifdefault,final String modelId){ 
		showLoadingDialog("…æ≥˝≥µ¡æ...");
		DeleteVehicleRequest request = new DeleteVehicleRequest();
		request.setApiParams(vehicleId);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				if( ifdefault!=null && "YES".equals(ifdefault) ){
					getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
					.putString(BaseActivity.BRAND, "")
					.putString(BaseActivity.MODEL, "")
					.putString(BaseActivity.VEHICLENUM,"").commit();
				}
				//…æ≥˝◊÷µ‰
				SaveDB.getSaveDB(BindCarsActivity.this).deleteModleFaultCodes(currentUsername+modelId);
				TongGouApplication.showToast(result.getMessage());
				doQueryVehicleList();
				TongGouApplication.getInstance().queryVehicleList(BindCarsActivity.this);
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
								//«Î«ÛÕ¯¬Á
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
			GuestVehicleManager manager = new GuestVehicleManager();
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
		showLoadingDialog("…Ë÷√÷–...");
		UpdateDefaultVehicleRequest request = new UpdateDefaultVehicleRequest();
		request.setRequestParams(vehicleId);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, byte[] originResult) {
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
			//«Î«ÛÕ¯¬Á
			if("yes".equals(data.getStringExtra("tonggou.isOk"))){		
				
				initVehicleList();
			}
		}/*if(resultCode==5050){		
			//«Î«ÛÕ¯¬Á
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
