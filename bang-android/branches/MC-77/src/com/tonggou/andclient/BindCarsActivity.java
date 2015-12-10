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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.app.BaseConnectOBDService;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.guest.GuestVehicleManager;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.DeleteVehicleParser;
import com.tonggou.andclient.parse.SetCheckParser;
import com.tonggou.andclient.parse.VehicleListParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.vo.VehicleInfo;
/**
 * 绑定车辆页面
 * @author think
 *
 */
public class BindCarsActivity extends BaseActivity{
	private static final int  NETWORK_FAILD=-1;
	private static final int  NETWORK_SUCCEED=0x003;
	private static final int  NETWORK_NODATA=0x004;
	private static final int  NETWORK_DELETE_SUCCEED=0x005;
	private static final int  NETWORK_DELETE_NODATA=0x006;
	private static final int  CHECK_SUCCEED=0x007;
	private static final int  CHECK_FAILD=0x008;

	private ProgressBar progressBar;
	private int checkNum=0;
	private Handler handler;
	private BindcarsAdapter bindcarsAdapter;
	private ListView bindcarsListView;
	private View addView,back;
	private String  userNo;
	private List<VehicleInfo> vehicleList;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bindcars);
		userNo=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME, null);
		progressBar=(ProgressBar) findViewById(R.id.shopdetilmappro);

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
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				
				dismissLoadingDialog();
				
				switch(msg.what){
				case NETWORK_FAILD: 
					progressBar.setVisibility(View.GONE);
					Toast.makeText(BindCarsActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case NETWORK_SUCCEED: 
					progressBar.setVisibility(View.GONE);
					bindcarsAdapter = new BindcarsAdapter(BindCarsActivity.this,vehicleList); 
					bindcarsAdapter.notifyDataSetChanged();
					bindcarsListView.setAdapter(bindcarsAdapter);
					break;
				case NETWORK_NODATA: 	
					progressBar.setVisibility(View.GONE);						
					Toast.makeText(BindCarsActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

					break;
				case NETWORK_DELETE_SUCCEED: 	
					progressBar.setVisibility(View.GONE);				
					Toast.makeText(BindCarsActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();
					new Thread(){
						public void run(){
							networking(userNo);
						}
					}.start();
					
					break;
				case NETWORK_DELETE_NODATA: 
					progressBar.setVisibility(View.GONE);							
					Toast.makeText(BindCarsActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();
					break;
				case CHECK_SUCCEED: 			
					progressBar.setVisibility(View.GONE);
					checkNum=Integer.parseInt((String)msg.obj);
					for(int i=0;i<vehicleList.size();i++){

						if(checkNum==i){
							vehicleList.get(i).setIsDefault("YES");
							MainActivity.defaultBrandAndModle = vehicleList.get(i).getVehicleBrand()+" " +vehicleList.get(i).getVehicleModel();							
						}else{
							vehicleList.get(i).setIsDefault("NO");
						}
					}

					bindcarsAdapter.notifyDataSetChanged();
					
					break;
				case CHECK_FAILD: 	
					progressBar.setVisibility(View.GONE);
					bindcarsAdapter.notifyDataSetChanged();
					Toast.makeText(BindCarsActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

					break;
				}
			}
		};

		initVehicleList();
	}
	
	private void initVehicleList() {
		
		if( !TongGouApplication.getInstance().isLogin() ) {
			GuestVehicleManager manager = new GuestVehicleManager();
			vehicleList = manager.getAllVehicle();
			bindcarsAdapter = new BindcarsAdapter(BindCarsActivity.this,vehicleList); 
			bindcarsListView.setAdapter(bindcarsAdapter);
			bindcarsListView.invalidate();
			
		} else {
			showLoadingDialog("加载车辆信息...");
			//请求网络
			new Thread(){
				public void run(){
					networking(userNo);
				}
			}.start();
		}
		
	}
	
	private void deleteVehicle(final VehicleInfo vehicleInfo) {
		
		if( !TongGouApplication.getInstance().isLogin() ) {
			GuestVehicleManager manager = new GuestVehicleManager();
			manager.delete(vehicleInfo.getVehicleId());
			initVehicleList();
			
		} else {
			progressBar.setVisibility(View.VISIBLE);
			new Thread(){
				public void run(){                               
					networkingDelete(vehicleInfo.getVehicleId(), vehicleInfo.getIsDefault(), vehicleInfo.getVehicleModelId());
				}
			}.start();
		}
	}
	
	private  void networking(String userNo){
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/list/userNo/"+userNo;
		VehicleListParser vehicleListParser = new VehicleListParser();		
		NetworkState ns = Network.getNetwork(BindCarsActivity.this).httpGetUpdateString(url,vehicleListParser);	

		if(ns.isNetworkSuccess()){
			if(vehicleListParser.isSuccessfull()){
				vehicleList=vehicleListParser.getVehicleListResponse().getVehicleList();
				if(vehicleList!=null){
					if(vehicleList.size()==0){
						//删除列表后 原来的连接该停掉
						Intent intentS = new Intent();
						intentS.setAction(TongGouService.TONGGOU_ACTION_START);
						intentS.putExtra("com.tonggou.server","STOP");
						sendBroadcast(intentS);
						MainActivity.defaultBrandAndModle = "";
					}
					sendMessage(NETWORK_SUCCEED, vehicleListParser.getVehicleListResponse().getMessage());
				}else{
					sendMessage(NETWORK_NODATA,vehicleListParser.getVehicleListResponse().getMessage());
				}

			}else{
				//解析出错
				sendMessage(NETWORK_NODATA, vehicleListParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
		}
	}
	private  void networkingDelete(String veId,String ifdefault,String modelId){ 
		String url=INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/singleVehicle/vehicleId/"+veId;
		DeleteVehicleParser deletevehicleParser = new DeleteVehicleParser();		
		NetworkState ns = Network.getNetwork(BindCarsActivity.this).httpDeleteUpdateString(url,deletevehicleParser);

		if(ns.isNetworkSuccess()){
			if(deletevehicleParser.isSuccessfull()){
				
				if(ifdefault!=null&&"YES".equals(ifdefault)){
					getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
					.putString(BaseActivity.BRAND, "")
					.putString(BaseActivity.MODEL, "")
					.putString(BaseActivity.VEHICLENUM,"").commit();
				}
				//删除字典
				SaveDB.getSaveDB(BindCarsActivity.this).deleteModleFaultCodes(currentUsername+modelId);
				sendMessage(NETWORK_DELETE_SUCCEED, deletevehicleParser.getDeleteVehicleResponse().getMessage());
			}else{
				//解析出错
				sendMessage(NETWORK_DELETE_NODATA, deletevehicleParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
		}
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
			GuestVehicleManager manager = new GuestVehicleManager();
			manager.updateVehicle2Default(vehicleList.get(positionID));
			initVehicleList();
		} else {
			progressBar.setVisibility(View.VISIBLE);
			new Thread(){
				public void run(){
					setCheck(vehicleId,positionID);
				}
			}.start();
		}
	}
	
	public void onResume(){
		super.onResume();
		BaseConnectOBDService.addingCar = false;
	}
	
	private  void setCheck(String veId,int position){ 
		String url=INFO.HTTP_HEAD+INFO.HOST_IP+"/vehicle/updateDefault";
		SetCheckParser setCheckParser = new SetCheckParser();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("vehicleId",veId));	
		NetworkState ns = Network.getNetwork(BindCarsActivity.this).httpPostUpdateString(url,nameValuePairs,setCheckParser);	
		if(ns.isNetworkSuccess()){
			if(setCheckParser.isSuccessfull()){
				sendMessage(CHECK_SUCCEED, position+"");
			}else{
				//解析出错
				sendMessage(CHECK_FAILD, setCheckParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
		}
	}
	protected void sendMessage(int what, String content) {
		if (what < 0) {
			what = BaseActivity.SEND_MESSAGE;
		}
		Message msg = Message.obtain(handler, what, content);
		if(msg!=null){
			msg.sendToTarget();
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( resultCode != Activity.RESULT_OK ) {
			return;
		}
		TongGouApplication.showLog("requestCode   " + requestCode);
		if(requestCode==3030||requestCode==5050){		
			//请求网络
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
