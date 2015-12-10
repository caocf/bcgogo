package com.tonggou.andclient;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.parse.SetScroeParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.MapToJson;

public class SetScroeActivity extends BaseActivity{
	private static final int  NETWORK_FAILD=-1;
	private static final int  LOGIN_SUCCEED=0x001;
	private static final int  LOGIN_FAILD=0x002;

	private Handler handler;
	private   String userNo,scroe="0",orderId;
	private TextView shoplistlikesorce;
	private View feedbackcall;
	private EditText  feedback_information;
	private ImageView shoplistlike1,shoplistlike2,shoplistlike3,shoplistlike4,shoplistlike5;
	private String from;
	private boolean isNetwork=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setscroe);
		userNo=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME, null);
		orderId = getIntent().getStringExtra("tonggou.server.orderid");
		from=getIntent().getStringExtra("tonggou.server.from");
		View back=findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SetScroeActivity.this.finish();
			}
		});
		feedback_information=(EditText) findViewById(R.id.feedback_information);
		shoplistlike1=(ImageView) findViewById(R.id.shoplistlike1);
		shoplistlike2=(ImageView) findViewById(R.id.shoplistlike2);
		shoplistlike3=(ImageView) findViewById(R.id.shoplistlike3);
		shoplistlike4=(ImageView) findViewById(R.id.shoplistlike4);
		shoplistlike5=(ImageView) findViewById(R.id.shoplistlike5);
		shoplistlikesorce=(TextView) findViewById(R.id.shoplistlikesorce);
		feedbackcall=findViewById(R.id.feedbackcall);
		shoplistlike1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				shoplistlike1.setImageDrawable(getResources().getDrawable(R.drawable.yellowstarbig));
				shoplistlike2.setImageDrawable(getResources().getDrawable(R.drawable.whitestarbig));
				shoplistlike3.setImageDrawable(getResources().getDrawable(R.drawable.whitestarbig));
				shoplistlike4.setImageDrawable(getResources().getDrawable(R.drawable.whitestarbig));
				shoplistlike5.setImageDrawable(getResources().getDrawable(R.drawable.whitestarbig));
				shoplistlikesorce.setText("1.0分");scroe="1";
			}
		});
		shoplistlike2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				shoplistlike1.setImageDrawable(getResources().getDrawable(R.drawable.yellowstarbig));
				shoplistlike2.setImageDrawable(getResources().getDrawable(R.drawable.yellowstarbig));
				shoplistlike3.setImageDrawable(getResources().getDrawable(R.drawable.whitestarbig));
				shoplistlike4.setImageDrawable(getResources().getDrawable(R.drawable.whitestarbig));
				shoplistlike5.setImageDrawable(getResources().getDrawable(R.drawable.whitestarbig));
				shoplistlikesorce.setText("2.0分");scroe="2";
			}
		});
		shoplistlike3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				shoplistlike1.setImageDrawable(getResources().getDrawable(R.drawable.yellowstarbig));
				shoplistlike2.setImageDrawable(getResources().getDrawable(R.drawable.yellowstarbig));
				shoplistlike3.setImageDrawable(getResources().getDrawable(R.drawable.yellowstarbig));
				shoplistlike4.setImageDrawable(getResources().getDrawable(R.drawable.whitestarbig));
				shoplistlike5.setImageDrawable(getResources().getDrawable(R.drawable.whitestarbig));
				shoplistlikesorce.setText("3.0分");scroe="3";
			}
		});
		shoplistlike4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				shoplistlike1.setImageDrawable(getResources().getDrawable(R.drawable.yellowstarbig));
				shoplistlike2.setImageDrawable(getResources().getDrawable(R.drawable.yellowstarbig));
				shoplistlike3.setImageDrawable(getResources().getDrawable(R.drawable.yellowstarbig));
				shoplistlike4.setImageDrawable(getResources().getDrawable(R.drawable.yellowstarbig));
				shoplistlike5.setImageDrawable(getResources().getDrawable(R.drawable.whitestarbig));
				shoplistlikesorce.setText("4.0分");scroe="4";
			}
		});
		shoplistlike5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				shoplistlike1.setImageDrawable(getResources().getDrawable(R.drawable.yellowstarbig));
				shoplistlike2.setImageDrawable(getResources().getDrawable(R.drawable.yellowstarbig));
				shoplistlike3.setImageDrawable(getResources().getDrawable(R.drawable.yellowstarbig));
				shoplistlike4.setImageDrawable(getResources().getDrawable(R.drawable.yellowstarbig));
				shoplistlike5.setImageDrawable(getResources().getDrawable(R.drawable.yellowstarbig));
				shoplistlikesorce.setText("5.0分");scroe="5";
			}
		});
		feedbackcall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isNetwork){		
					if( "0".equals( scroe ) ) {
						Toast.makeText(SetScroeActivity.this,"请打分", Toast.LENGTH_SHORT).show();
						return;
					}
					final String feedback_info=feedback_information.getText().toString();
					if(feedback_info!=null&&!"".equals(feedback_info)){
						new Thread(){
							public void run(){
								setInfo(feedback_info,scroe);
							}
						}.start();
					}else{
						Toast.makeText(SetScroeActivity.this,"请输入您的评价", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				switch(msg.what){
				case NETWORK_FAILD: 
					isNetwork=false;
					Toast.makeText(SetScroeActivity.this,(String)msg.obj,Toast.LENGTH_LONG).show();
					break;
				case LOGIN_SUCCEED: 
					isNetwork=false;
					Toast.makeText(SetScroeActivity.this,(String)msg.obj,Toast.LENGTH_LONG).show();
					if("OrderDetilActivity".equals(from)){
						Intent dataIntent = new Intent();
						dataIntent.putExtra("tonggou.isOk","yes");
						setResult(7070, dataIntent);
					}else{
						Intent dataIntent = new Intent();
						dataIntent.putExtra("tonggou.isOk","yes");
						setResult(7171, dataIntent);
					}
					SetScroeActivity.this.finish();
					break;
				case LOGIN_FAILD: 
					isNetwork=false;
					Toast.makeText(SetScroeActivity.this,(String)msg.obj, Toast.LENGTH_LONG).show();

					break;
				}
			}
		};
	}
	private void setInfo(String feedback_info,String commentScore){
        isNetwork=true;
		SetScroeParser setScroeParser = new SetScroeParser();
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/shop/score";	
//		String valuePairs = "{\"userNo\":\""+userNo+"\", " +" \"orderId\":\""+orderId+"\", " +" \"commentScore\":\""+commentScore+"\", "+
//				" \"commentContent\":\""+feedback_info+"\"}";
		
		Map<String, String> values = new HashMap<String,String>();
		values.put("userNo", userNo);
		values.put("orderId", orderId);
		values.put("commentScore", commentScore);
		values.put("commentContent", feedback_info);
		String valuePairs = MapToJson.mapToJsonStr(values);
		NetworkState ns = Network.getNetwork(SetScroeActivity.this).httpPutUpdateString(url,valuePairs.getBytes(),setScroeParser);
		
		if(ns.isNetworkSuccess()){
			if(setScroeParser.isSuccessfull()){
				//正确的处理逻辑 
				String mes = setScroeParser.getSetScroeResponse().getMessage();
				sendMessage(LOGIN_SUCCEED, mes);
			}else{
				//提示用户错误
				String errorAlert = setScroeParser.getErrorMessage();
				sendMessage(LOGIN_FAILD, errorAlert);
			}
		}else{
			//网络错误
			sendMessage(LOGIN_FAILD, ns.getErrorMessage());
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
}
