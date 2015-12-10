package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.BaseResponse;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.network.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.SendAppointmentRequest;
import com.tonggou.andclient.parse.UserDateParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.ShopServiceCategoryDTO;
import com.tonggou.andclient.vo.UserInfo;
import com.tonggou.andclient.vo.VehicleInfo;
/**
 * 在线预约服务页面
 * @author think
 *
 */
public class AppointmentNetWork extends BaseActivity{
	private static final int  NETWORK_FAILD=-1;
	private static final int  NETWORK_SUCCEED=2;
	private static final int  LOGIN_SUCCEED=3;
	private static final int  LOGIN_FAILD=4;


	private String carBrandId;              //品牌id
	private String carMoldId;               //车型id
	private String carBrandName;
	private String carModelName;
	private String carVehicleVin;              //车辆VIN
	private String    bindcarnumberStr,bindcarnextmileStr,bindcarnexttimeStr,bindcarnexttime2Str;
	private TextView  bindcar_submit;
	private ImageView back;
	private View bindnewcar;
	private Handler handler;
	private EditText  bindcarnumber,bindcarnextmile,bindcarnexttime2;
	private TextView registercarnum2,registercarnum1,appointmentTime;
	private TextView serverType;
	private AlertDialog selectServerTypeAlert;
	private LayoutInflater layoutInflater;
	private SelectListAdapter selectListAdapter;
	private ListView selectList;
	private String conditionStr;
	private String shopName;
	private String shopId;
	private String shopServiceStr,userNo;
	private String phoneName,phone,vehicleNum;

	private TextView  appoinTime;
	private EditText servicephone ,contact,beizhu,vehicleNo;
	private String selectTypeId="";
	private ArrayList<ShopServiceCategoryDTO> categorys;
	private boolean canClick=false;
	private UserInfo user;
	private ProgressBar shopdetilmappro;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_network);
		findViewById(R.id.back).setFocusableInTouchMode(true);
		shopName = getIntent().getStringExtra("tonggou.shop.name");
		shopId = getIntent().getStringExtra("tonggou.shop.id");

		conditionStr = getIntent().getStringExtra("tonggou.conditionStr");
		
		shopServiceStr=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.SERVICE, null);
		userNo=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME, null);

		((TextView)findViewById(R.id.name)).setText(shopName);

		categorys = (ArrayList<ShopServiceCategoryDTO>) getIntent().getSerializableExtra("tonggou.shop.categorys");
		carBrandName=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.BRAND, "");
		carModelName=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.MODEL, "");
		phoneName=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.PHONENAME, "");
		phone=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.PHONE, "");
		vehicleNum=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.VEHICLENUM, "");

		layoutInflater = LayoutInflater.from(this);	

		View back=findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AppointmentNetWork.this.finish();
			}
		});

		shopdetilmappro=(ProgressBar) findViewById(R.id.shopdetilmappro);   //预约时间
		appointmentTime=(TextView) findViewById(R.id.app_network_servicetime);   //预约时间
		appointmentTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {AlertDialog.Builder  	builder = new AlertDialog.Builder(AppointmentNetWork.this);
			View view = LayoutInflater.from(AppointmentNetWork.this).inflate(R.layout.date_time_dialog, null);
			final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
			final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker);

			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

			timePicker.setIs24HourView(true);
			timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
			timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

			builder.setView(view);


			builder.setTitle("选取时间");
			builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					timePicker.clearFocus();
					StringBuffer sb = new StringBuffer();
					sb.append(String.format("%d-%02d-%02d", 
							datePicker.getYear(), 
							datePicker.getMonth() + 1, 
							datePicker.getDayOfMonth()));
					sb.append("  ");
					if(timePicker.getCurrentHour()<10){
						sb.append("0");
					}
					sb.append(timePicker.getCurrentHour())
					.append(":");
					if(timePicker.getCurrentMinute()<10){
						sb.append("0");
					}
					sb.append(timePicker.getCurrentMinute());


					appointmentTime.setText(sb);

					dialog.cancel();
				}
			});
			Dialog dialog = builder.create();
			dialog.show();
			}
		});
		setDateTime(); 
		registercarnum2=(TextView) findViewById(R.id.registercarnum2);
		registercarnum2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AppointmentNetWork.this,AppointmentNetWorkSearch.class);
				intent.putExtra("tonggou.from", "pinpai");
				intent.putExtra("tonggou.pinpai", "");
				startActivityForResult(intent, 1010);
			}
		});
		registercarnum1=(TextView) findViewById(R.id.registercarnum1);
		registercarnum1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(carBrandId!=null&&!"".equals(carBrandId)){
					Intent intent = new Intent(AppointmentNetWork.this,AppointmentNetWorkSearch.class);
					intent.putExtra("tonggou.from", "chexing");
					intent.putExtra("tonggou.pinpai",carBrandId);
					startActivityForResult(intent, 2020);
				}else{
					Toast.makeText(AppointmentNetWork.this,getString(R.string.brand_first),Toast.LENGTH_SHORT).show();

				}
			}
		});
		registercarnum1.setText(carModelName);
		registercarnum2.setText(carBrandName);

		serverType =(TextView) findViewById(R.id.app_network_service);
		if(categorys!=null&&categorys.size()>0){
			//默认第一个
			serverType.setText(categorys.get(0).getServiceCategoryName());
			selectTypeId = categorys.get(0).getServiceCategoryId();
			for(int i=0;i<categorys.size();i++){
				if(categorys.get(i).getServiceCategoryName().equals(shopServiceStr)){
					serverType.setText(categorys.get(i).getServiceCategoryName());
					selectTypeId = categorys.get(i).getServiceCategoryId();
					categorys.get(i).setSelect(true);
					break;
				}	
			}
		}
		serverType.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showSelectServerTypeAlert();
			}
		});


		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){	
				switch(msg.what){

				case NETWORK_SUCCEED: 
					Toast.makeText(AppointmentNetWork.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					AppointmentNetWork.this.finish();
					break;				
				case NETWORK_FAILD :
					Toast.makeText(AppointmentNetWork.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;				
				case LOGIN_SUCCEED: 
					shopdetilmappro.setVisibility(View.GONE);
					canClick=true;
					showMessge();
					break;
				case LOGIN_FAILD: 	
					shopdetilmappro.setVisibility(View.GONE);	
					canClick=true;
					Toast.makeText(AppointmentNetWork.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

					break;

				}
			}
		};

		//appoinTime =(TextView) findViewById(R.id.registernexttime);          
		servicephone = (EditText) findViewById(R.id.app_network_servicephone); 
		if(phone!=null){
			servicephone.setText(phone);
		}
		contact  = (EditText) findViewById(R.id.app_network_serviceman); 
		if(phoneName!=null){
			contact.setText(phoneName);
		}
		beizhu = (EditText) findViewById(R.id.app_network_serviceothers); 
		if(conditionStr!=null&&!"".equals(conditionStr)&&!"null".equals(conditionStr)){
			beizhu.setText(conditionStr);
		}else{
			beizhu.setText("");
		}
		vehicleNo = (EditText) findViewById(R.id.app_network_servicecarnum);   //车牌号
		if(vehicleNum!=null){
			vehicleNo.setText(vehicleNum.toUpperCase());
		}

		View appointmentAction = findViewById(R.id.submit_btn);
		appointmentAction.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(canClick){
					beforeUpdateCheck(selectTypeId,servicephone.getText().toString(),contact.getText().toString(),vehicleNo.getText().toString());
				}else{
					Toast.makeText(AppointmentNetWork.this,"读取数据中，请稍等。。。",Toast.LENGTH_SHORT).show();

				}
			}
		});
		beizhu.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					vehicleNo.requestFocus(); 
				} 
				return false; 
			} 
		});
		vehicleNo.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					contact.requestFocus(); 
				} 
				return false; 
			} 
		}); 
		contact.setOnKeyListener(new OnKeyListener() { 
			public boolean onKey(View v, int keyCode, KeyEvent event) { 
				if(keyCode == 66&& event.getAction() == KeyEvent.ACTION_UP) { 
					servicephone.requestFocus(); 
				} 
				return false; 
			} 
		}); 

		beizhu.clearFocus();
		vehicleNo.clearFocus();
		contact.clearFocus();
		new Thread(){
			public void run(){						
				getInfo();
			}
		}.start();

	}
	private void getInfo(){
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/user/information/userNo/"+userNo;
		UserDateParser userDateParser = new UserDateParser();		
		NetworkState ns = Network.getNetwork(AppointmentNetWork.this).httpGetUpdateString(url,userDateParser);	

		if(ns.isNetworkSuccess()){
			if(userDateParser.isSuccessfull()){
				user=userDateParser.getUserDateResponse().getUserInfo();
				if(user!=null){
					sendMessage(LOGIN_SUCCEED, null);
				}else{
					sendMessage(LOGIN_FAILD,"没有列表数据");
				}

			}else{
				//解析出错
				sendMessage(LOGIN_FAILD, userDateParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(LOGIN_FAILD, ns.getErrorMessage());
		}
	}

	private void showMessge(){ 
		if(user.getMobile()!=null){
			servicephone.setText(user.getMobile());
			phone=user.getMobile();
		}
		if(user.getName()!=null){
			contact.setText(user.getName());
			phoneName=user.getName();
		}

		getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
		.putString(BaseActivity.PHONENAME, user.getName())
		.putString(BaseActivity.PHONE, user.getMobile()).commit();
	}
	private void beforeUpdateCheck(final String serviceId,final String mobile,final String contact,final String vehicleNo){
		if(serviceId!=null&&!"".equals(serviceId)){
			if(vehicleNo!=null&&!"".equals(vehicleNo)){
				if(contact!=null&&!"".equals(contact)){					
					if(mobile!=null&&!"".equals(mobile)){
						if(SomeUtil.isPhoneNumberValid(mobile)){

							Time localTime = new Time("Asia/Hong_Kong");  
							localTime.setToNow();  
							long nextInsuranceTimeLong = SomeUtil.StringDateToLong(appointmentTime.getText().toString());  //保险时间
							long nowTime = SomeUtil.StringDateToLong(localTime.format("%Y-%m-%d %H:%M").toString());  //保险时间
							if(appointmentTime.getText().toString()!=null&&nextInsuranceTimeLong>=nowTime){	
								doAppointment(serviceId,appointmentTime.getText().toString(),mobile,contact,
										sharedPreferences.getString(BaseActivity.NAME, ""),shopId,vehicleNo.toUpperCase(),
										/*String vehicleBrand*/carBrandName,/*String vehicleBrandId*/carBrandId,
										/*String vehicleModel*/carModelName,/*String vehicleModelId*/carMoldId,
										/*String vehicleVin*/carVehicleVin,beizhu.getText().toString());
							}else{

								Toast.makeText(AppointmentNetWork.this, "预约的时间不能早于当前时间", Toast.LENGTH_SHORT).show();
							}

						}else{
							Toast.makeText(AppointmentNetWork.this,getString(R.string.wrongnumber), Toast.LENGTH_SHORT).show();

						}
					}else{
						Toast.makeText(AppointmentNetWork.this,getString(R.string.reservation_mobile_alert), Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(AppointmentNetWork.this,getString(R.string.reservation_contact_alert), Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(AppointmentNetWork.this,getString(R.string.reservation_vehicleNo_alert), Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(AppointmentNetWork.this,getString(R.string.reservation_category_alert), Toast.LENGTH_SHORT).show();
		}


	}

	/**
	 * 
	 * @param serviceCategoryId
	 * @param appointTime
	 * @param mobile
	 * @param contact
	 * @param userNo
	 * @param shopId
	 * @param vehicleNo
	 * @param vehicleBrand
	 * @param vehicleBrandId
	 * @param vehicleModel
	 * @param vehicleModelId
	 * @param vehicleVin
	 * @param remark
	 */
	private void doAppointment(String serviceCategoryId,String appointTime,String mobile,String contact,
			String userNo,String shopId,String vehicleNo,
			String vehicleBrand,String vehicleBrandId,
			String vehicleModel,String vehicleModelId,
			String vehicleVin,String remark){
		VehicleInfo vehicleInfo = TongGouApplication.getInstance().getDefaultVehicle();
		List<Map<String, String>> faultInfoItems = null;
		if(vehicleInfo != null) {
			String vehicleId = vehicleInfo.getVehicleId();
			faultInfoItems = SaveDB.getSaveDB(this).getFaultInfoItems(userNo, vehicleId);
		}
		long appTime = SomeUtil.StringDateToLong(appointTime);
		SendAppointmentRequest request = new SendAppointmentRequest();
		request.setRequestParams( shopId, serviceCategoryId,
				mobile, contact, userNo, appTime,
				vehicleNo, vehicleVin, vehicleBrand, vehicleBrandId, 
				vehicleModel, vehicleModelId, remark, faultInfoItems);
		
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {

			@Override
			public void onStart() {
				showLoadingDialog(getString(R.string.register_waiting));
				super.onStart();
			}
			
			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				TongGouApplication.showToast(result.getMessage());
				finish();
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
		
//		CommonParser commonParser = new CommonParser();
//		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/service/appointment";
//		StringBuffer sb = new StringBuffer();
//		String value1 = "{\"serviceCategoryId\":\""+serviceCategoryId+"\"" +
//				",\"appointTime\":\""+appTime+"\"" +
//				",\"mobile\":\""+mobile+"\"" +
//				",\"contact\":\""+contact+"\"" +
//				",\"userNo\":\""+userNo+"\"" +
//				",\"shopId\":\""+shopId+"\"" +
//				",\"vehicleNo\":\""+vehicleNo+"\"" ;
//
//		sb.append(value1);
//		if(vehicleBrand!=null&&!"".equals(vehicleBrand)){
//			String valueVB = ",\"vehicleBrand\":\""+vehicleBrand+"\"" ;
//			sb.append(valueVB);
//		}
//		if(vehicleBrandId!=null&&!"".equals(vehicleBrandId)){
//			String valueVBID = ",\"vehicleBrandId\":\""+vehicleBrandId+"\"" ;
//			sb.append(valueVBID);
//		}
//		if(vehicleModel!=null&&!"".equals(vehicleModel)){
//			String valueVM = ",\"vehicleModel\":\""+vehicleModel+"\""  ;
//			sb.append(valueVM);
//		}
//		if(vehicleModelId!=null&&!"".equals(vehicleModelId)){
//			String valueVMID = ",\"vehicleModelId\":\""+vehicleModelId+"\"" ;
//			sb.append(valueVMID);
//		}
//		if(vehicleVin!=null&&!"".equals(vehicleVin)){
//			String valueVIN = ",\"vehicleVin\":\""+vehicleVin+"\""  ;
//			sb.append(valueVIN);
//		}
//
//		String valueMK = ",\"remark\":\""+remark+"\"}";
//		sb.append(valueMK);
//
//		String resultStr = sb.toString();
//
//
//
//
//
//		NetworkState ns = Network.getNetwork(AppointmentNetWork.this).httpPutUpdateString(url,resultStr.getBytes(),commonParser);
//		if(ns.isNetworkSuccess()){
//			if(commonParser.isSuccessfull()){
//				//正确的处理逻辑 
//				String mes = commonParser.getCommonResponse().getMessage();
//				sendMessage(NETWORK_SUCCEED, mes);
//			}else{
//				//提示用户错误
//				String errorAlert = commonParser.getErrorMessage();
//				sendMessage(NETWORK_FAILD, errorAlert);
//			}
//		}else{
//			//网络错误
//			sendMessage(NETWORK_FAILD, ns.getErrorMessage());
//		}
	}


	private void sendMessage(int what, String content) {
		if (what < 0) {
			what = BaseActivity.SEND_MESSAGE;
		}
		Message msg = Message.obtain(handler, what, content);
		if(msg!=null){
			msg.sendToTarget();
		}
	}
	/**
	 * 打开选择服务类型
	 */
	private void showSelectServerTypeAlert(){
		if(categorys==null){
			return;
		}
		final View modifyPassView = layoutInflater.inflate(R.layout.select_server_type, null);
		selectListAdapter = new SelectListAdapter(categorys);
		selectList = (ListView) modifyPassView.findViewById(R.id.typelistview);

		selectList.setAdapter(selectListAdapter);
		selectList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> eachAdaper, View arg1, int position, long arg3) {

				selectTypeId = categorys.get(position).getServiceCategoryId();
				selectServerTypeAlert.cancel();
				selectServerTypeAlert.dismiss();		
				serverType.setText(categorys.get(position).getServiceCategoryName());
				categorys.get(position).setSelect(true);
				//去掉以前选择
				for(int i=0;i<categorys.size();i++){
					if(position!=i){
						categorys.get(i).setSelect(false);
					}
				}
			}
		});

		selectServerTypeAlert = new AlertDialog.Builder(AppointmentNetWork.this).create();	
		selectServerTypeAlert.show();
		Window window = selectServerTypeAlert.getWindow();
		window.setContentView(modifyPassView);	

	}
	private void setDateTime(){
		Time localTime = new Time("Asia/Hong_Kong");  
		localTime.setToNow();  
		appointmentTime.setText(localTime.format("%Y-%m-%d %H:%M"));  
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode==1010){		
			if(registercarnum2.getText().toString()!=null&&!"".equals(registercarnum2.getText().toString())){
				if(!data.getStringExtra("pinpai").equals(registercarnum2.getText().toString())){

					registercarnum1.setText("");	
					carMoldId=null;
				}
			}
			registercarnum2.setText(data.getStringExtra("pinpai"));
			carBrandId=data.getStringExtra("pinpaiId");
			carBrandName = data.getStringExtra("pinpai");

		}
		if(resultCode==2020){		
			registercarnum1.setText(data.getStringExtra("chexing"));
			carModelName = data.getStringExtra("chexing");
			carMoldId=data.getStringExtra("chexingId");

		}
	}


	private class SelectListAdapter extends BaseAdapter{
		private ArrayList<ShopServiceCategoryDTO>  types;

		public SelectListAdapter(ArrayList<ShopServiceCategoryDTO> mytypes){
			this.types = mytypes;
		}

		public int getCount() {			
			return types.size();
		}
		public Object getItem(int position) {
			return types.get(position);
		}
		public long getItemId(int position) {		
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {					
			if(convertView == null){
				convertView = layoutInflater.inflate(R.layout.select_server_type_item, null);
			}	
			final ShopServiceCategoryDTO serverType = (ShopServiceCategoryDTO)this.getItem(position);

			((TextView)convertView.findViewById(R.id.devices_name)).setText(serverType.getServiceCategoryName());	
			if(serverType.isSelect()){
				convertView.findViewById(R.id.select_iv).setVisibility(View.VISIBLE);
			}else{
				convertView.findViewById(R.id.select_iv).setVisibility(View.INVISIBLE);
			}


			return convertView;			
		}		
	}
}
