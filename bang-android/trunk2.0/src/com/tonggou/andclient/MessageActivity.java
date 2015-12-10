package com.tonggou.andclient;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.NotificationManager;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.TonggouMessage;

public class MessageActivity extends BaseActivity {
	private static final int  MESSAGE_SUCCEED = 1;
	
	private LinearLayout layout;
	private DevicesListAdapter devicesListAdapter;
	private ListView devicesList;
	private LayoutInflater layoutInflater;
	private ArrayList<TonggouMessage> mes = new ArrayList<TonggouMessage>();
	private Handler myhandler;
	View delete;
	
	private TonggouMessage selectCommentMes;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messages);
		layoutInflater = LayoutInflater.from(this);			
		delete=findViewById(R.id.delect_iv);
		delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mes==null||mes.size()==0){
					return;
				}
				new AlertDialog.Builder(MessageActivity.this) 		
				.setTitle(getString(R.string.exit_title)) 
				.setMessage(getString(R.string.message_delete_all)) 
				.setPositiveButton(getString(R.string.exit_submit), new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int whichButton) {
						//请求网络
						new Thread(){
							public void run(){
								boolean res = SaveDB.getSaveDB(MessageActivity.this).deleteAllMessage(sharedPreferences.getString(BaseActivity.NAME, null));
							    if(res){
							    	if(mes!=null){
							    		mes.clear();
							    		sendMessage(myhandler,MESSAGE_SUCCEED, null);
							    	}
							    }else{
							    	
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
		myhandler = new Handler(){
			public void handleMessage(Message msg){		
				switch(msg.what){	
				case MESSAGE_SUCCEED: 
					displayList();
					break;
				}
			}
		};
		
		displayMessageReceiver = new DisplayMessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(TongGouService.TONGGOU_ACTION_DISPLAY_MESSAGE);
		registerReceiver(displayMessageReceiver, filter);
		
		cancelNotice();
		
		new Thread(){
			public void run(){
				getMessage();				
			}
		}.start();
	
	}
	
	 private void cancelNotice(){
		 NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		 notiManager.cancel(0x7f030301);	
	}
	
	public void getMessage(){
		if( !TongGouApplication.getInstance().isLogin() ) {
			return;
		}
		mes = SaveDB.getSaveDB(this).getAllMyMessages(sharedPreferences.getString(BaseActivity.NAME, null));
		if(mes!=null){
			sendMessage(myhandler,MESSAGE_SUCCEED, null);
		}
	}
	
	
	private void displayList(){
		devicesListAdapter = new DevicesListAdapter();
		devicesList = (ListView) findViewById(R.id.my_messages);
		devicesList.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
				final int pos = position;
				new AlertDialog.Builder(MessageActivity.this) 		
				.setTitle(getString(R.string.exit_title)) 
				.setMessage(getString(R.string.message_delete)) 
				.setPositiveButton(getString(R.string.exit_submit), new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int whichButton) {
						//请求网络
						new Thread(){
							public void run(){
								SaveDB.getSaveDB(MessageActivity.this).deleteOneMessage(sharedPreferences.getString(BaseActivity.NAME, null),mes.get(pos).getId());
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
		
		devicesList.setAdapter(devicesListAdapter);
	}
	
	
	
	 public class DevicesListAdapter extends BaseAdapter{		
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
					   convertView = layoutInflater.inflate(R.layout.message_list_item, null);
				   }	
				   final TonggouMessage itemMs = (TonggouMessage)this.getItem(position);
				   ((TextView)convertView.findViewById(R.id.name_txtview)).setText(itemMs.getTitle());	
				   ((TextView)convertView.findViewById(R.id.content_txtview)).setText(itemMs.getContent());	
				   ((TextView)convertView.findViewById(R.id.time_txtview)).setText(SomeUtil.longToStringDate(itemMs.getTime()));	
				   
				   //按钮动作处理
				   TextView actionBut = (TextView)convertView.findViewById(R.id.more_imgview);
				   //actionBut.setVisibility(View.VISIBLE);
				   if(itemMs.getActionType()!=null){					  
				       if("SEARCH_SHOP".equals(itemMs.getActionType())){   //跳转到店铺查询 params 参数格式：orderId,shopId
				    	   actionBut.setText("查看店铺");
				    	   actionBut.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									Intent toHome = new Intent(MessageActivity.this,StoreQueryActivity.class);
									toHome.putExtra("tonggou.shop.category",itemMs.getParams());       //机修保养
									//toHome.putExtra("tonggou.shop.categoryname",getString(R.string.reservation_weixiu));  
									toHome.putExtra("tonggou.shop.categoryname",StoreQueryActivity.ALLCATEGORY);     
									startActivity(toHome);
								}
							});
				    	   actionBut.setVisibility(View.VISIBLE);
				       }else if("SERVICE_DETAIL".equals(itemMs.getActionType())){  //跳转到具体的服务 params 参数格式：orderId,shopId
				    	   actionBut.setText("查看服务");
				    	   actionBut.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									String ss = itemMs.getParams();
									if(ss!=null&&!"".equals(ss)){
									Intent intent=new Intent(MessageActivity.this,OrderDetilActivity.class);
									intent.putExtra("tonggou.server.orderid", ss.substring(0, ss.indexOf(",")));
									intent.putExtra("tonggou.server.from", "MessageActivity");
									startActivity(intent);
									}
								}
							});
				    	   actionBut.setVisibility(View.VISIBLE);
				       }else if("CANCEL_ORDER".equals(itemMs.getActionType())){    //取消服务     params  参数格式：orderId,shopId
				    	   actionBut.setText("取消");
				    	   actionBut.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									String ss = itemMs.getParams();
									if(ss!=null&&!"".equals(ss)){
									Intent intent=new Intent(MessageActivity.this,OrderDetilActivity.class);
									intent.putExtra("tonggou.server.orderid", ss.substring(0, ss.indexOf(",")));
									intent.putExtra("tonggou.server.from", "MessageActivity");
									startActivity(intent);
									}
								}
							});
				    	   actionBut.setVisibility(View.VISIBLE);
				       }else if("ORDER_DETAIL".equals(itemMs.getActionType())){   //查看单据详情 params 服务范围枚举 （OVERHAUL_AND_MAINTENANCE、INSURANCE）
				    	   actionBut.setText("查看单据");
				    	   actionBut.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									String ss = itemMs.getParams();
									if(ss!=null&&!"".equals(ss)){
									Intent intent=new Intent(MessageActivity.this,OrderDetilActivity.class);
									intent.putExtra("tonggou.server.orderid", ss.substring(0, ss.indexOf(",")));
									intent.putExtra("tonggou.server.from", "MessageActivity");
									startActivity(intent);
									}
								}
							});
				    	   actionBut.setVisibility(View.VISIBLE);
				       }else if("COMMENT_SHOP".equals(itemMs.getActionType())){    //评价单据     params  参数格式：orderId,shopId
				    	   actionBut.setText("评价");
				    	   actionBut.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									String ss = itemMs.getParams();
									if(ss!=null&&!"".equals(ss)){
										selectCommentMes = itemMs;
									    String[] params = ss.split(",");
										Intent intent=new Intent(MessageActivity.this,SetScroeActivity.class);
										intent.putExtra("tonggou.server.orderid", params[params.length-1]);
										intent.putExtra("tonggou.server.from", "MessageActivity");
										startActivityForResult(intent, 7070);
										//startActivity(intent);
									}
								}
							});
				    	   actionBut.setVisibility(View.VISIBLE);
				       }else{
				    	   actionBut.setVisibility(View.GONE);
				       }
					   
				   }else{
					   actionBut.setVisibility(View.GONE);
				   }
					 
				   return convertView;			
			}		
	   }
	 
	 
	 public void onBackPressed() {
			new AlertDialog.Builder(MessageActivity.this) 		
	        .setTitle(getString(R.string.exit_title)) 
	        .setMessage(getString(R.string.exit_sure_tonggou)) 
	        .setPositiveButton(getString(R.string.exit_submit), new DialogInterface.OnClickListener() { 
	            public void onClick(DialogInterface dialog, int whichButton) {
	    			exit();
	        } 
	        }).setNeutralButton(getString(R.string.exit_cancel), new DialogInterface.OnClickListener(){ 
	        	public void onClick(DialogInterface dialog, int whichButton){ 
	        	} 
	        }).show();
		}
	 
	 
	 
	 	private DisplayMessageReceiver displayMessageReceiver;	
		private class DisplayMessageReceiver extends BroadcastReceiver{
	        public void onReceive(Context context, Intent intent) {
	        	Log.d("testthread", "display message BROADCAST....");
	        	MessageActivity.this.runOnUiThread(new Runnable(){
					  public void run() {
						    cancelNotice();							
							new Thread(){
								public void run(){
									getMessage();				
								}
							}.start();
					  }
				});
	        	
	        	
	        }                       
	    }
		
		
		
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if(resultCode==7171){		
				//请求网络
				if("yes".equals(data.getStringExtra("tonggou.isOk"))){		
					    if(selectCommentMes==null){
					    	return;
					    }
						new Thread(){
							public void run(){
								SaveDB.getSaveDB(MessageActivity.this).upDateOneMessage(sharedPreferences.getString(BaseActivity.NAME, null),selectCommentMes.getId());
								getMessage();				
							}
						}.start();
				}
			}
		}
}
