package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.myview.SimpleTitleBar;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.CarCondition;
/**
 * 车况查询页面
 * @author think
 *
 */
public class CarConditionQueryActivity extends AbsBackableActivity {
	public static final String ACTION_RECEIVED_OBD_DTC = "com.tonggou.condition.ACTION_RECEIVED_OBD_DTC";
	
	public static final String CONDITION_VALUE_SSYH = "com.tonggou.condition.ssyh";  //瞬时油耗
	public static final String CONDITION_VALUE_PJYH = "com.tonggou.condition.pjyh";  //平均油耗
	public static final String CONDITION_VALUE_SYYL = "com.tonggou.condition.syyl";  //剩余油量
	public static final String CONDITION_VALUE_SXWD = "com.tonggou.condition.sxwd";  //水箱温度
	
    public static String ssyhStr="- -";
    public static String pjyhStr="- - l/h";
    public static String syylStr="- -";
    public static String sxwdStr="- -";
	
	private static final int  GET_MSG_SUCCESS = 1;
	private static final int  OK_SUCCEED = 2;
	
	private CarConditionAdapter mConditionAdapter;
	private ListView mConditionListView;
	
	private UPDateUiReceiver mUpdateUiReceiver;
	private ReceivedOBDDTCReceiver mReceivedOBDDTCReceiver;
	private View mNoListContainer;
	private View mOkContainer;
	private View mNotConnectContainer;
	private View mToBindView;
	private TextView ssyhTV,pjyhTV, syylTV, sxwdTV ,syylTVP;
	
	private AlertDialog mDeleteDialog;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg){		
			switch(msg.what){	
			case GET_MSG_SUCCESS: 
				TongGouApplication.showLog("GET_MSG_SUCCESS " + ((List<CarCondition>)msg.obj).size());
				mConditionListView.setVisibility(View.VISIBLE);
				mConditionAdapter.update((List<CarCondition>)msg.obj);
				mNotConnectContainer.setVisibility(View.GONE);
				mNoListContainer.setVisibility(View.GONE);
				
				break;
			case OK_SUCCEED: 
				TongGouApplication.showLog("OK_SUCCEED");
				mConditionAdapter.clear();
				mConditionListView.setVisibility(View.GONE);
				mNoListContainer.setVisibility(View.VISIBLE);
				if(!TongGouApplication.connetedOBD){
					mNotConnectContainer.setVisibility(View.VISIBLE);
					mOkContainer.setVisibility(View.GONE);
				}else{
					mOkContainer.setVisibility(View.VISIBLE);
					mNotConnectContainer.setVisibility(View.GONE);
				}
				break;
				
			}
		}
	};;
	@Override
	protected int getContentLayout() {
		return R.layout.car_condition;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mUpdateUiReceiver = new UPDateUiReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TongGouService.TONGGOU_ACTION_UPDATEUI);
        registerReceiver(mUpdateUiReceiver, filter);
        
        mReceivedOBDDTCReceiver = new ReceivedOBDDTCReceiver();
        IntentFilter receivedDTCFilter = new IntentFilter(ACTION_RECEIVED_OBD_DTC);
        registerReceiver(mReceivedOBDDTCReceiver, receivedDTCFilter);
        
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void afterTitleBarCreated(SimpleTitleBar titleBar, Bundle savedInstanceState) {
		super.afterTitleBarCreated(titleBar, savedInstanceState);
		titleBar.setTitle("车况查询");
		titleBar.setRightImageButton(R.drawable.delete, android.R.color.transparent);
		titleBar.setRightSecondImageButton(R.drawable.searching_obd_refresh, android.R.color.transparent);
		titleBar.setOnRightButtonClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doDelete(getString(R.string.message_delete_all), null);
			}
		});
		titleBar.setOnRightSecondButtonClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doRefresh();
			}
		});
	}

	@Override
	protected void findViews(Bundle savedInstanceState) {
		super.findViews(savedInstanceState);
		
		mConditionListView = (ListView) findViewById(R.id.my_messages);
		mNoListContainer = findViewById(R.id.car_condition_ok);
		mOkContainer = findViewById(R.id.car_condition_okalert);
		mNotConnectContainer = findViewById(R.id.car_condition_notconnect);
		mToBindView = findViewById(R.id.car_condition_notconnect_lianjie);
		
		ssyhTV = (TextView)findViewById(R.id.speed_set_tx);
		pjyhTV = (TextView)findViewById(R.id.rotate_set_tv);
		syylTV = (TextView)findViewById(R.id.coolant_set_tv);
		sxwdTV = (TextView)findViewById(R.id.oid_set_tv);
		syylTVP = (TextView)findViewById(R.id.coolant_set_tv_p);
		
        afterViews();
	}
	
	private void afterViews() {
		ssyhTV.setText(ssyhStr);
    	pjyhTV.setText(pjyhStr);
    	syylTV.setText(syylStr);
    	sxwdTV.setText(sxwdStr);
    	mConditionListView.setVisibility(View.VISIBLE);
		mNoListContainer.setVisibility(View.GONE);
    	
		setListener();
		mConditionAdapter = 
	        		new CarConditionAdapter(this, new ArrayList<CarCondition>(), R.layout.car_condition_list_item);
		mConditionListView.setAdapter(mConditionAdapter);
		
		onSendReadRTDOrder();
	}
	
	private void setListener() {
		mToBindView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent =new Intent(CarConditionQueryActivity.this,BindCarsActivity.class);
				startActivity(intent);
			}
		});
		
		mConditionListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int pos, long arg3) {
				doDelete(getString(R.string.message_delete), 
						mConditionAdapter.getData().get(pos).getFaultCode());
				return true;
			}
			
		});
		
		mConditionListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,long arg3) {
				Intent intent = new Intent(CarConditionQueryActivity.this, StoreQueryActivity.class);
				intent.putExtra("tonggou.shop.categoryname",getString(R.string.shopslist_service)); 
				CarCondition itemData = mConditionAdapter.getData().get(pos);
				String discrip = itemData.getContent();
				if("不能识别的故障码".equals(discrip)){
					intent.putExtra("tonggou.shop.conditionStr", itemData.getFaultCode());
				}else{
					intent.putExtra("tonggou.shop.conditionStr", itemData.getFaultCode() + ":" + itemData.getContent());
				}
				startActivity(intent);
			}
			
		});
	}

	private void onSendReadRTDOrder() {
		//读取车况
 		Intent intent = new Intent();
 		intent.setAction(TongGouService.TONGGOU_ACTION_READ_CURRENT_RTD_CONDITION);
 		sendBroadcast(intent);
	}
	
	private void onSendReadDTCOrder() {
		//读取车况
 		Intent intent = new Intent();
 		intent.setAction(TongGouService.TONGGOU_ACTION_READ_CURRENT_DTC_CONDITION);
 		sendBroadcast(intent);
	}
	
	private void doRefresh() {
		if(!TongGouApplication.connetedOBD){
			Toast.makeText(getApplicationContext(),"车辆处于未连接状态",Toast.LENGTH_LONG).show();
			return;
    	}
		onSendReadRTDOrder();
		onSendReadDTCOrder();
        doGetDTCMessage();
	}
	
	private void doGetDTCMessage() {
		new Thread(){
			public void run(){
				getMessage();
			}
		}.start();
	}
	
	private void doDelete(String message, String faultCode) {
		if( mConditionAdapter.getData().isEmpty() ) {
			TongGouApplication.showToast("当前没有消息可以删除");
			return;
		}
		showDeleteDialog(message, faultCode);
	}
	
	private void showDeleteDialog(String message, final String faultCode) {
		dismissDeleteDialog();
		
		mDeleteDialog = new AlertDialog.Builder(CarConditionQueryActivity.this) 		
			.setTitle(getString(R.string.exit_title)) 
			.setMessage(message) 
			.setPositiveButton(getString(R.string.exit_submit), new DialogInterface.OnClickListener() { 
				public void onClick(DialogInterface dialog, int whichButton) {
					deleteAlarm(faultCode);
				} 
			}).setNeutralButton(getString(R.string.exit_cancel), new DialogInterface.OnClickListener(){ 
				public void onClick(DialogInterface dialog, int whichButton){ } 
			}).create();
		
		if( !isFinishing() ) {
			mDeleteDialog.show();
		}
	}
	
	private void dismissDeleteDialog() {
		if( mDeleteDialog != null && mDeleteDialog.isShowing() ) {
			mDeleteDialog.show();
		}
		mDeleteDialog = null;
	}
	
	/**
	 * 删除警告
	 * @param faultCode 警告码，当为 null 时删除全部警告
	 */
	private void deleteAlarm( final String faultCode ) {
		new Thread() {
			
			@Override
			public void run() {
				if( faultCode != null ) {
					SaveDB.getSaveDB(CarConditionQueryActivity.this).deleteOneAlarm(currentUserId, faultCode);
					getMessage();
				} else {
					boolean isSuccess = SaveDB.getSaveDB(CarConditionQueryActivity.this).deleteAllAlarm(currentUserId);
				    if(isSuccess){
			    		MainActivity.haveFaultCode = false;
			    		//sendMessage(myhandler,LOGIN_SUCCEED, null);
			    		sendMessage(OK_SUCCEED, null);
				    }
				}
			};
		}.start();
	}
	
	public void onResume(){
		super.onResume();
		doGetDTCMessage();
	}
	
	public void getMessage() {
		List<CarCondition> data = SaveDB.getSaveDB(CarConditionQueryActivity.this).getAllCarConditons(currentUserId);
		if(data !=null && !data.isEmpty() ){
			sendMessage(GET_MSG_SUCCESS, data);
		} else {
			sendMessage(OK_SUCCEED, null);
		}
	}
	
	private void sendMessage(int what, Object data) {
		mHandler.sendMessage(  
				mHandler.obtainMessage(what, data) );
	}
	
	@Override
	protected void onStop() {
		dismissDeleteDialog();
		super.onStop();
	}
	
	
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(this, HomePageActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		super.onBackPressed();
	}
	
	public void onDestroy(){
		unregisterReceiver(mUpdateUiReceiver);
		unregisterReceiver(mReceivedOBDDTCReceiver);
		super.onDestroy();
	}
	
	class CarConditionAdapter extends AbsViewHolderAdapter<CarCondition> {

		public CarConditionAdapter(Context context, List<CarCondition> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, CarCondition itemData) {
			TextView name = getViewFromHolder(convertView, R.id.name_txtview);
			TextView content = getViewFromHolder(convertView, R.id.content_txtview);
			TextView time = getViewFromHolder(convertView, R.id.time_txtview);
			
			name.setText(itemData.getFaultCode());
			content.setText(itemData.getContent());
			String longStrTime = itemData.getReportTime();
			time.setText(SomeUtil.longToStringDate2(longStrTime));
		}
		 
	 }
	 
	 class UPDateUiReceiver extends BroadcastReceiver{
		 
        public void onReceive(Context context, Intent intent) {
        	Log.d("CONTEETTT","Update car condition ui.....");
        	String ssyh = intent.getStringExtra(CarConditionQueryActivity.CONDITION_VALUE_SSYH);
        	String pjyh = intent.getStringExtra(CarConditionQueryActivity.CONDITION_VALUE_PJYH);
        	String syyl = intent.getStringExtra(CarConditionQueryActivity.CONDITION_VALUE_SYYL);
        	String sxwd = intent.getStringExtra(CarConditionQueryActivity.CONDITION_VALUE_SXWD);

        	ssyhTV.setText(ssyh);
        	pjyhTV.setText(pjyh);
        	syylTV.setText(syyl);
        	sxwdTV.setText(sxwd);
        	
        	try{
        		float ssyhFloat = Float.parseFloat(syyl);
                if(ssyhFloat<25){  //剩余油量
                	syylTV.setTextColor(0xfffd7801);
                	syylTVP.setTextColor(0xffd7801);
                }else{
                	syylTV.setTextColor(0xffffffff);
                	syylTVP.setTextColor(0xffffffff);
                }
    		}catch(NumberFormatException er){
    		}
        	
        	Toast.makeText(getApplicationContext(),"刷新车况成功",Toast.LENGTH_SHORT).show();
        }                       
	 }
	 
	 class ReceivedOBDDTCReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if( ! ACTION_RECEIVED_OBD_DTC.equals( intent.getAction() )) {
				return;
			}
			
			doGetDTCMessage();
		}
		 
	 }
}
