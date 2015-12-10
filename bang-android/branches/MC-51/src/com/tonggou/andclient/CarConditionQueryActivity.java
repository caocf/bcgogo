package com.tonggou.andclient;

import java.util.ArrayList;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.CarCondition;
/**
 * 车况查询页面
 * @author think
 *
 */
public class CarConditionQueryActivity extends BaseActivity {
	public static final String CONDITION_VALUE_SSYH = "com.tonggou.condition.ssyh";  //瞬时油耗
	public static final String CONDITION_VALUE_PJYH = "com.tonggou.condition.pjyh";  //平均油耗
	public static final String CONDITION_VALUE_SYYL = "com.tonggou.condition.syyl";  //剩余油量
	public static final String CONDITION_VALUE_SXWD = "com.tonggou.condition.sxwd";  //水箱温度
	
    public static String ssyhStr="- -";
    public static String pjyhStr="- - l/h";
    public static String syylStr="- -";
    public static String sxwdStr="- -";
	
	private static final int  LOGIN_SUCCEED = 1;
	private static final int  OK_SUCCEED = 2;
	
	private LinearLayout layout;
	private DevicesListAdapter devicesListAdapter;
	private ListView devicesList;
	private LayoutInflater layoutInflater;
	private ArrayList<CarCondition> mes;
	private Handler myhandler;
	private UPDateUiReceiver updateUiReceiver;
	private View noListIv;
	private View okIv;
	private View notConnect;
	private View toBing;
	private TextView ssyhTV,pjyhTV, syylTV, sxwdTV ,syylTVP;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.car_condition);
		layoutInflater = LayoutInflater.from(this);	
		View back = findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CarConditionQueryActivity.this.finish();
			}
		});
		devicesList = (ListView) findViewById(R.id.my_messages);
		noListIv = findViewById(R.id.car_condition_ok);
		okIv = findViewById(R.id.car_condition_okalert);
		notConnect = findViewById(R.id.car_condition_notconnect);
		toBing = findViewById(R.id.car_condition_notconnect_lianjie);
		toBing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent =new Intent(CarConditionQueryActivity.this,BindCarsActivity.class);
				startActivity(intent);
			}
		});
		View delete = findViewById(R.id.delect_iv);
		delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mes==null||mes.size()==0){
					return;
				}
				// TODO Auto-generated method stub
				new AlertDialog.Builder(CarConditionQueryActivity.this) 		
				.setTitle(getString(R.string.exit_title)) 
				.setMessage(getString(R.string.message_delete_all)) 
				.setPositiveButton(getString(R.string.exit_submit), new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int whichButton) {
						new Thread(){
							public void run(){
								boolean res = SaveDB.getSaveDB(CarConditionQueryActivity.this).deleteAllAlarm(currentUserId);
							    if(res){
							    	if(mes!=null){
							    		mes.clear();
							    		MainActivity.haveFaultCode = false;
							    		//sendMessage(myhandler,LOGIN_SUCCEED, null);
							    		sendMessage(myhandler,OK_SUCCEED, null);
							    	}
							    }
							    
							}
						}.start();
						
					} 
				}).setNeutralButton(getString(R.string.exit_cancel), new DialogInterface.OnClickListener(){ 
					public void onClick(DialogInterface dialog, int whichButton){ 
					} 
				}).show();
			}
		});
		
		View refresh = findViewById(R.id.refresh_iv);
		refresh.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(!TongGouApplication.connetedOBD){
					Toast.makeText(getApplicationContext(),"车辆处于未连接状态",Toast.LENGTH_LONG).show();
					return;
	        	}
				Intent intent = new Intent();//创建Intent对象
		        intent.setAction(TongGouService.TONGGOU_ACTION_READ_CURRENT_CONDITION);
		        sendBroadcast(intent);//发送广播
		        
		        new Thread(){
					public void run(){
						getMessage();
					}
				}.start();
			}
		});
		
		myhandler = new Handler(){
			public void handleMessage(Message msg){		
				switch(msg.what){	
				case LOGIN_SUCCEED: 
					displayList();
					break;
				case OK_SUCCEED: 
					devicesList.setVisibility(View.GONE);
					noListIv.setVisibility(View.VISIBLE);
					if(!TongGouApplication.connetedOBD){
						notConnect.setVisibility(View.VISIBLE);
						okIv.setVisibility(View.GONE);
					}else{
						okIv.setVisibility(View.VISIBLE);
						notConnect.setVisibility(View.GONE);
					}
					break;
					
				}
			}
		};
		
		ssyhTV = (TextView)findViewById(R.id.speed_set_tx);
		pjyhTV = (TextView)findViewById(R.id.rotate_set_tv);
		syylTV = (TextView)findViewById(R.id.coolant_set_tv);
		sxwdTV = (TextView)findViewById(R.id.oid_set_tv);
		
		syylTVP = (TextView)findViewById(R.id.coolant_set_tv_p);
		
		ssyhTV.setText(ssyhStr);
    	pjyhTV.setText(pjyhStr);
    	syylTV.setText(syylStr);
    	sxwdTV.setText(sxwdStr);
		
		updateUiReceiver = new UPDateUiReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TongGouService.TONGGOU_ACTION_UPDATEUI);
        registerReceiver(updateUiReceiver, filter);
		
//		new Thread(){
//			public void run(){
//				getMessage();
//			}
//		}.start();
		
		
		//读取车况
 		Intent intent = new Intent();
 		intent.setAction(TongGouService.TONGGOU_ACTION_READ_CURRENT_CONDITION);
 		sendBroadcast(intent);
	
	}
	
	public void getMessage(){
		mes = SaveDB.getSaveDB(CarConditionQueryActivity.this).getAllCarConditons(currentUserId);
		if(mes!=null&&mes.size()>0){
			sendMessage(myhandler,LOGIN_SUCCEED, null);
		}else{
			sendMessage(myhandler,OK_SUCCEED, null);
		}
	}

	public void onResume(){
		super.onResume();
		new Thread(){
			public void run(){
				getMessage();
			}
		}.start();
	}
	
	public void onDestroy(){
		super.onDestroy();
		this.unregisterReceiver(updateUiReceiver);
	}
	
	private void displayList(){
		devicesListAdapter = new DevicesListAdapter();
		
		devicesList.setVisibility(View.VISIBLE);
		noListIv.setVisibility(View.GONE);
		devicesList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				final int pos = arg2;
				new AlertDialog.Builder(CarConditionQueryActivity.this) 		
				.setTitle(getString(R.string.exit_title)) 
				.setMessage(getString(R.string.message_delete)) 
				.setPositiveButton(getString(R.string.exit_submit), new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int whichButton) {
						//请求网络
						new Thread(){
							public void run(){
								SaveDB.getSaveDB(CarConditionQueryActivity.this).deleteOneAlarm(currentUserId,mes.get(pos).getFaultCode());
								getMessage();
							}
						}.start();
						
					} 
				}).setNeutralButton(getString(R.string.exit_cancel), new DialogInterface.OnClickListener(){ 
					public void onClick(DialogInterface dialog, int whichButton){ 
					} 
				}).show();
				return false;
			}
			
		});
		
		devicesList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Intent intent = new Intent(CarConditionQueryActivity.this, StoreQueryActivity.class);
				intent.putExtra("tonggou.shop.categoryname",getString(R.string.shopslist_service)); 
				String discrip = mes.get(arg2).getContent();
				if("不能识别的故障码".equals(discrip)){
					intent.putExtra("tonggou.shop.conditionStr",mes.get(arg2).getFaultCode());
				}else{
					intent.putExtra("tonggou.shop.conditionStr",mes.get(arg2).getFaultCode()+":"+mes.get(arg2).getContent());
				}
				startActivity(intent);
			}
		});
		devicesList.setAdapter(devicesListAdapter);
		devicesListAdapter.notifyDataSetChanged();
	}
	
	
	
	 private class DevicesListAdapter extends BaseAdapter{		
			public int getCount() {			
				return mes.size();
			}
			public Object getItem(int position) {
				return mes.get(position);
			}
			public long getItemId(int position) {		
				return position;
			}
			public View getView(int position, View convertView, ViewGroup parent) {					
				   if(convertView == null){
					   convertView = layoutInflater.inflate(R.layout.car_condition_list_item, null);
				   }	
				   ((TextView)convertView.findViewById(R.id.name_txtview)).setText(((CarCondition)this.getItem(position)).getFaultCode());	
				   ((TextView)convertView.findViewById(R.id.content_txtview)).setText(((CarCondition)this.getItem(position)).getContent());	
				   String longStrTime = ((CarCondition)this.getItem(position)).getReportTime();
				   ((TextView)convertView.findViewById(R.id.time_txtview)).setText(SomeUtil.longToStringDate2(longStrTime));	
					
					 
				   return convertView;			
			}		
	   }
	 
	 private class UPDateUiReceiver extends BroadcastReceiver{
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
}
