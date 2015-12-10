package com.tonggou.andclient;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.util.SaveDB;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.TonggouMessage;
import com.tonggou.andclient.vo.type.ServiceScopeType;

public class MessageActivity extends BaseActivity {
	
	private ListView mMessageListView;
	private MessageAdapter mMessageAdapter;
	private View mTitleBarDeleteBtn;
	private DisplayMessageReceiver mDisplayMessageReceiver;
	private AlertDialog mAlertDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messages);
		
		
		mMessageListView = (ListView) findViewById(R.id.my_messages);
		mTitleBarDeleteBtn = findViewById(R.id.delect_iv);
		afterViews();
	}
	
	private void afterViews() {
		mMessageAdapter = new MessageAdapter(this, new ArrayList<TonggouMessage>(), R.layout.message_list_item);
		mMessageListView.setAdapter(mMessageAdapter);
		setListener();
		registerMessageReceiver();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		clearNotice();
		updateData();
	}
	
	public void updateData() {
		QueryMessageAsyncTask task = new QueryMessageAsyncTask();
		task.execute((Void)null);
	}
	
	private void registerMessageReceiver() {
		mDisplayMessageReceiver = new DisplayMessageReceiver();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(TongGouService.TONGGOU_ACTION_DISPLAY_MESSAGE);
		registerReceiver(mDisplayMessageReceiver, filter);   
		
	}

	private void setListener() {
		 mTitleBarDeleteBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(mMessageAdapter.getCount() == 0){
						return;
					}
					showAlertDialog(null);
				}
			});
		 
		 mMessageListView.setOnItemLongClickListener(new OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
					showAlertDialog(position);
					return true;
				}
				
			});
	}
	
	/**
	 * 
	 * @param position	在列表的位置， null 或 -1 为删除全部消息
	 */
	private void showAlertDialog(final Integer position) {
		dismissAlertDialog();
		final boolean isDeleteAll = (position == null || position == -1);
		mAlertDialog = new AlertDialog.Builder(MessageActivity.this) 		
			.setTitle(getString(R.string.exit_title)) 
			.setMessage(isDeleteAll ? R.string.message_delete_all : R.string.message_delete)
			.setPositiveButton(getString(R.string.exit_submit), new DialogInterface.OnClickListener() { 
				public void onClick(DialogInterface dialog, int whichButton) {
					SaveDB db = SaveDB.getSaveDB(MessageActivity.this);
					String userNo = sharedPreferences.getString(BaseActivity.NAME, null);
					if( isDeleteAll ) {
						db.deleteAllMessage(userNo);
					} else {
						TonggouMessage itemData = mMessageAdapter.getData().get(position);
						db.deleteOneMessage(userNo,itemData.getId());
					}
					updateData();
				} 
			}).setNegativeButton(R.string.exit_cancel, null)
			.create();
		if( !isFinishing() ) {
			mAlertDialog.show();
		}
	}
	
	private void dismissAlertDialog() {
		if( mAlertDialog != null && mAlertDialog.isShowing() ) {
			mAlertDialog.dismiss();
		}
		mAlertDialog = null;
	}

	/**
	 * 清空有消息的通知栏信息
	 */
	private void clearNotice(){
		 NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		 notiManager.cancel(0x7f030301);	
	}
	
	class MessageAdapter extends AbsViewHolderAdapter<TonggouMessage> {

		public MessageAdapter(Context context, List<TonggouMessage> data,
				int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, final TonggouMessage itemData) {
			TextView titleText = getViewFromHolder(convertView, R.id.name_txtview);
			TextView contentText = getViewFromHolder(convertView, R.id.content_txtview);
			TextView timeText = getViewFromHolder(convertView, R.id.time_txtview);
			TextView actionBut = (TextView)convertView.findViewById(R.id.more_imgview);
			 
			titleText.setText(itemData.getTitle());
			contentText.setText(itemData.getContent());
			timeText.setText(SomeUtil.longToStringDate(itemData.getTime()));
			
			   //按钮动作处理
			   if(itemData.getActionType()!=null){	
				   getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit().putInt(BaseActivity.NEW_MESSAGE_COUNT,0).commit();
				   
				   
			       if("SEARCH_SHOP".equals(itemData.getActionType())){   //跳转到店铺查询 params 参数格式：orderId,shopId
			    	   actionBut.setText("查看店铺");
			    	   actionBut.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Intent toHome = new Intent(MessageActivity.this,StoreQueryActivity.class);
								toHome.putExtra("tonggou.shop.category", ServiceScopeType.OVERHAUL_AND_MAINTENANCE);       //机修保养
								//toHome.putExtra("tonggou.shop.categoryname",getString(R.string.reservation_weixiu));  
								toHome.putExtra("tonggou.shop.categoryname",StoreQueryActivity.ALLCATEGORY);     
								startActivity(toHome);
							}
						});
			    	   actionBut.setVisibility(View.VISIBLE);
			       }else if("SERVICE_DETAIL".equals(itemData.getActionType())){  //跳转到具体的服务 params 参数格式：orderId,shopId
			    	   actionBut.setText("查看服务");
			    	   actionBut.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								String ss = itemData.getParams();
								if(ss!=null&&!"".equals(ss)){
								Intent intent=new Intent(MessageActivity.this,OrderDetilActivity.class);
								intent.putExtra("tonggou.server.orderid", ss.substring(0, ss.indexOf(",")));
								intent.putExtra("tonggou.server.from", "MessageActivity");
								startActivity(intent);
								}
							}
						});
			    	   actionBut.setVisibility(View.VISIBLE);
			       }else if("CANCEL_ORDER".equals(itemData.getActionType())){    //取消服务     params  参数格式：orderId,shopId
			    	   actionBut.setText("取消");
			    	   actionBut.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								String ss = itemData.getParams();
								if(ss!=null&&!"".equals(ss)){
								Intent intent=new Intent(MessageActivity.this,OrderDetilActivity.class);
								intent.putExtra("tonggou.server.orderid", ss.substring(0, ss.indexOf(",")));
								intent.putExtra("tonggou.server.from", "MessageActivity");
								startActivity(intent);
								}
							}
						});
			    	   actionBut.setVisibility(View.VISIBLE);
			       }else if("ORDER_DETAIL".equals(itemData.getActionType())){   //查看单据详情 params 服务范围枚举 （OVERHAUL_AND_MAINTENANCE、INSURANCE）
			    	   actionBut.setText("查看单据");
			    	   actionBut.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								String ss = itemData.getParams();
								if(ss!=null&&!"".equals(ss)){
								Intent intent=new Intent(MessageActivity.this,OrderDetilActivity.class);
								intent.putExtra("tonggou.server.orderid", ss.substring(0, ss.indexOf(",")));
								TongGouApplication.showLog("orderid---------"+ss.substring(0, ss.indexOf(",")));
								intent.putExtra("tonggou.server.from", "MessageActivity");
								startActivity(intent);
								}
							}
						});
			    	   actionBut.setVisibility(View.VISIBLE);
			       }else if("COMMENT_SHOP".equals(itemData.getActionType())){    //评价单据     params  参数格式：orderId,shopId
			    	   actionBut.setText("已结算");
			    	   actionBut.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								String ss = itemData.getParams();
								if(ss!=null&&!"".equals(ss)){
									String[] params = ss.split(",");
										Intent intent=new Intent(MessageActivity.this,OrderDetilActivity.class);
										intent.putExtra("tonggou.server.orderid", params[params.length-1]);
										intent.putExtra("tonggou.server.from", "MessageActivity");
										startActivity(intent);
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
		}
		 
	 }
	 
	 class QueryMessageAsyncTask extends AsyncTask<Void, Void, ArrayList<TonggouMessage>> {

		@Override
		protected ArrayList<TonggouMessage> doInBackground(Void... params) {
			return SaveDB.getSaveDB(MessageActivity.this).getAllMyMessages(sharedPreferences.getString(BaseActivity.NAME, null));
		}
		
		@Override
		protected void onPostExecute(ArrayList<TonggouMessage> result) {
			mMessageAdapter.update(result);
		}
		 
	 }
	 
	public void onBackPressed() {
		new AlertDialog.Builder(MessageActivity.this)
				.setTitle(getString(R.string.exit_title))
				.setMessage(getString(R.string.exit_sure_tonggou))
				.setPositiveButton(getString(R.string.exit_submit),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								exit();
							}
						})
				.setNegativeButton(R.string.exit_cancel, null).show();
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mDisplayMessageReceiver);
		super.onDestroy();
	}

	/**
	 * @author fbl
	 * 接收到广播后更新UI
	 */
	private class DisplayMessageReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			clearNotice();
			updateData();
			}
	}
		
}
