package com.tonggou.andclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.CommonParser;
import com.tonggou.andclient.parse.ServerDetailParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.OrderItem;
import com.tonggou.andclient.vo.ServerDetail;
/**
 * 单据详情页面
 * 
 * @author think
 *
 */
public class OrderDetilActivity extends BaseActivity{
	private static final int  NETWORK_FAILD=-1;
	private static final int  ACTION_SUCCEED=0x001;
	private static final int  ACTION_FAILD=0x002;
	private static final int  CANCEL_SUCCEED=0x003;

	private ProgressBar networkingPB;
	private Handler handler;
	private String orderID,from;
	private TextView ordername,action,orderstate;
	private ServerDetail sDetail;
	private AlertDialog cancelingAlert;
	private LinearLayout orderItems;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderdetil);

		orderID = getIntent().getStringExtra("tonggou.server.orderid");
		from = getIntent().getStringExtra("tonggou.server.from");


		View back=findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				OrderDetilActivity.this.finish();
			}
		});
		ordername=(TextView)findViewById(R.id.ordername);

		orderstate=((TextView)findViewById(R.id.order_state_tv));
		action = (TextView)findViewById(R.id.order_state_iv);                  //相应动作

	    orderItems = (LinearLayout)findViewById(R.id.shopkeep);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){	
				networkingPB.setVisibility(View.GONE);
				switch(msg.what){
				case ACTION_SUCCEED: 
					//Toast.makeText(OrderDetilActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					((TextView)findViewById(R.id.ordernum_tx)).setText(sDetail.getReceiptNo());           //单据号   
					((TextView)findViewById(R.id.orderstate_tx)).setText(sDetail.getServiceType());           //服务类型
					((TextView)findViewById(R.id.ordertime_tx)).setText(SomeUtil.longToStringDate(""+sDetail.getOrderTime()));           //时间
					ordername.setText(sDetail.getShopName());           //店面名称
					ordername.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							if(sDetail.getShopId()!=null&&!"".equals(sDetail.getShopId())){
								Intent intent=new Intent(OrderDetilActivity.this,StoreDetilActivity.class);
								intent.putExtra("tonggou.shopId",sDetail.getShopId()+"");
								intent.putExtra("tonggou.shopname",sDetail.getShopName());
								intent.putExtra("tonggou.shopmeter","");
								startActivity(intent);
							}
						}
					});
					orderstate.setText(sDetail.getStatus());           //状态
					if("已结算".equals(sDetail.getStatus())){

						action.setText("评论");  						
						if(sDetail.getComment()==null||sDetail.getComment().getCommentContent()==null){
							action.setVisibility(View.VISIBLE);
						}else{
							action.setVisibility(View.INVISIBLE);
						}
						action.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Intent intent=new Intent(OrderDetilActivity.this,SetScroeActivity.class);
								intent.putExtra("tonggou.server.orderid",sDetail.getOrderId());
								intent.putExtra("tonggou.server.from", "OrderDetilActivity");
								startActivityForResult(intent, 7070);
							}
						});
						findViewById(R.id.order_comment).setVisibility(View.VISIBLE);
						findViewById(R.id.shoplike).setVisibility(View.VISIBLE);
						findViewById(R.id.accounts_part).setVisibility(View.VISIBLE);

						findViewById(R.id.order_beizhu).setVisibility(View.VISIBLE);
						findViewById(R.id.order_line).setVisibility(View.VISIBLE);
						findViewById(R.id.appinfo).setVisibility(View.GONE);
						ImageView like1=(ImageView)findViewById(R.id.shoplistlike1);
						ImageView like2=(ImageView)findViewById(R.id.shoplistlike2);
						ImageView like3=(ImageView)findViewById(R.id.shoplistlike3);
						ImageView like4=(ImageView)findViewById(R.id.shoplistlike4);
						ImageView like5=(ImageView)findViewById(R.id.shoplistlike5);
						if(sDetail.getComment()!=null){
							((TextView)findViewById(R.id.shoplistlikesorce)).setText(sDetail.getComment().getCommentScore()+"分");
							OrderDetilActivity.this.setLikes(sDetail.getComment().getCommentScore()+"", like1, like2, like3, like4, like5);
							if(sDetail.getComment().getCommentContent()!=null){
								((TextView)findViewById(R.id.order_beizhu)).setText(sDetail.getComment().getCommentContent());           //备注
							}	
						}         //备注
					}else{
						if("待确认".equals(sDetail.getStatus())||"已接受".equals(sDetail.getStatus())){
							action.setText("取消");  
							action.setVisibility(View.VISIBLE);
							action.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									cancelAlert();
								}
							});
						}else {	
							action.setVisibility(View.INVISIBLE); 


						}
						findViewById(R.id.order_beizhu).setVisibility(View.GONE);
						findViewById(R.id.shoplike).setVisibility(View.GONE);
						findViewById(R.id.accounts_part).setVisibility(View.GONE);
						findViewById(R.id.order_comment).setVisibility(View.GONE);
						findViewById(R.id.order_line).setVisibility(View.GONE);
						findViewById(R.id.appinfo).setVisibility(View.VISIBLE);
					}
					((TextView)findViewById(R.id.orderman_tx)).setText(sDetail.getCustomerName());           //客户名
					((TextView)findViewById(R.id.ordercarman_tx)).setText(sDetail.getVehicleNo());           //车牌号
					if(sDetail.getVehicleContact()!=null){
						((TextView)findViewById(R.id.appman_tx)).setText(sDetail.getVehicleContact());           //客户名
					}
					if(sDetail.getVehicleMobile()!=null){
						((TextView)findViewById(R.id.appcall_tx)).setText(sDetail.getVehicleMobile());           //客户名
					}
					((TextView)findViewById(R.id.apptime_tx)).setText(SomeUtil.longToStringDate(""+sDetail.getOrderTime()));           //客户名
					if(sDetail.getRemark()!=null){
						((TextView)findViewById(R.id.appother_tx)).setText(sDetail.getRemark());           //客户名
					}
					if(sDetail.getVehicleBrandModelStr()!=null){
						((TextView)findViewById(R.id.ordercarstate_tx)).setText(sDetail.getVehicleBrandModelStr());           //客户名
					}//单据项列表
					//					LinearLayout orderItem = (LinearLayout)findViewById(R.id.shopkeep);
					//	
					//					View tempIte = getLayoutInflater().inflate(R.layout.orderdetil_item, null);
					//					orderItem.addView(tempIte,new LayoutParams(LayoutParams.FILL_PARENT ,LayoutParams.WRAP_CONTENT ) );
					//					View tempIte1 = getLayoutInflater().inflate(R.layout.orderdetil_item, null);
					//					orderItem.addView(tempIte1,new LayoutParams(LayoutParams.FILL_PARENT ,LayoutParams.WRAP_CONTENT ) );


                    
					if(sDetail.getOrderItems()!=null&&sDetail.getOrderItems().size()>0){
						orderItems.removeAllViews();
						for(int i=0;i<sDetail.getOrderItems().size();i++){
							OrderItem ot = sDetail.getOrderItems().get(i);
							View tempItem = getLayoutInflater().inflate(R.layout.orderdetil_item, null);
							TextView content = (TextView)tempItem.findViewById(R.id.item_content);
							content.setText(ot.getContent());
							TextView type = (TextView)tempItem.findViewById(R.id.item_type);
							type.setText(ot.getType());
							TextView amount = (TextView)tempItem.findViewById(R.id.item_amount);
							amount.setText(ot.getAmount());
							orderItems.addView(tempItem,new LayoutParams(LayoutParams.FILL_PARENT ,LayoutParams.WRAP_CONTENT ));
						}
					}else{
						//findViewById(R.id.accounts_part).setVisibility(View.VISIBLE);
					}

					//价格部分
					if(sDetail.getSettleAccounts()==null){
						findViewById(R.id.accounts_part).setVisibility(View.GONE);
					}else{
						findViewById(R.id.accounts_part).setVisibility(View.VISIBLE);
						TextView total = (TextView)findViewById(R.id.totalnum_tx);
						total.setText(sDetail.getSettleAccounts().getTotalAmount());
						TextView settled = (TextView)findViewById(R.id.settlednum_tx);
						settled.setText(sDetail.getSettleAccounts().getSettledAmount());
						TextView discount = (TextView)findViewById(R.id.discountnum_tx);
						discount.setText(sDetail.getSettleAccounts().getDiscount());
						TextView debt = (TextView)findViewById(R.id.debtnum_tx);
						debt.setText(sDetail.getSettleAccounts().getDebt());
					}

					//networkingPB.setVisibility(View.GONE);
					break;
				case NETWORK_FAILD: 
					if(cancelingAlert!=null){					
						cancelingAlert.cancel();
						cancelingAlert.dismiss();
					}
					Toast.makeText(OrderDetilActivity.this,(String)msg.obj,Toast.LENGTH_LONG).show();
					break;
				case CANCEL_SUCCEED: 
					if(cancelingAlert!=null){					
						cancelingAlert.cancel();
						cancelingAlert.dismiss();
					}
					Toast.makeText(OrderDetilActivity.this,(String)msg.obj,Toast.LENGTH_LONG).show();
					if("SearchServiceActivity".equals(from)){
						Intent dataIntent = new Intent();
						dataIntent.putExtra("tonggou.isOk","yes");
						setResult(6060, dataIntent);
						OrderDetilActivity.this.finish();	
					}else{
						orderstate.setText("已取消");
						action.setVisibility(View.INVISIBLE); 
					}
					break;
				case ACTION_FAILD: 	
					if(cancelingAlert!=null){					
						cancelingAlert.cancel();
						cancelingAlert.dismiss();
					}
					Toast.makeText(OrderDetilActivity.this,(String)msg.obj,Toast.LENGTH_LONG).show();

					break;
				}
			}
		};
		networkingPB = (ProgressBar) findViewById(R.id.orderdetail_pb);
		new Thread(){
			public void run(){
				getServerDetail("NULL",orderID);
			}
		}.start();
	}

	private void getServerDetail(String serviceScope,String orderId) {
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/service/historyDetail/orderId/"+orderId+"/serviceScope/"+serviceScope;

		ServerDetailParser serverDetailParser = new ServerDetailParser();		
		NetworkState ns = Network.getNetwork(OrderDetilActivity.this).httpGetUpdateString(url,serverDetailParser);	

		if(ns.isNetworkSuccess()){
			if(serverDetailParser.isSuccessfull()){
				sDetail = serverDetailParser.getServerDetailReponse().getServerDetail();
				if(sDetail!=null){					
					sendMessage(ACTION_SUCCEED,serverDetailParser.getServerDetailReponse().getMessage());				
				}else{
					sendMessage(ACTION_FAILD,serverDetailParser.getServerDetailReponse().getMessage());
				}

			}else{
				sendMessage(ACTION_FAILD, serverDetailParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(ACTION_FAILD, ns.getErrorMessage());
		}
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


	private void cancelAlert() {
		new AlertDialog.Builder(this) 		
		.setTitle(getString(R.string.exit_title)) 
		.setMessage("你确定取消单据吗？") 
		.setPositiveButton(getString(R.string.exit_submit), new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int whichButton) {
				cancelingAlert= new AlertDialog.Builder(OrderDetilActivity.this).create();
				cancelingAlert.show();			
				Window window = cancelingAlert.getWindow();
				window.setContentView(R.layout.logining);
				TextView waiting_message =(TextView) window.findViewById(R.id.loging_alerttext);
				waiting_message.setText(R.string.register_waiting);
				new Thread(){
					public void run(){
						cancelServerNetwork(orderID);
					}
				}.start();
			} 
		}).setNeutralButton(getString(R.string.exit_cancel), new DialogInterface.OnClickListener(){ 
			public void onClick(DialogInterface dialog, int whichButton){ 
			} 
		}).show();
	}


	private void cancelServerNetwork(String orderid) {
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/service/singleService/orderId/"+orderid+"/userNo/"+sharedPreferences.getString(BaseActivity.NAME, null);

		CommonParser commonParser = new CommonParser();		
		NetworkState ns = Network.getNetwork(OrderDetilActivity.this).httpDeleteUpdateString(url,commonParser);	

		if(ns.isNetworkSuccess()){
			if(commonParser.isSuccessfull()){					
				sendMessage(CANCEL_SUCCEED,commonParser.getCommonResponse().getMessage());				
			}else{
				sendMessage(ACTION_FAILD, commonParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(ACTION_FAILD, ns.getErrorMessage());
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==7070){		
			//请求网络
			if("yes".equals(data.getStringExtra("tonggou.isOk"))){		
				networkingPB.setVisibility(View.VISIBLE);
				new Thread(){
					public void run(){
						getServerDetail("NULL",orderID);
					}
				}.start();
			}
		}
	}
}
