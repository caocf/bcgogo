package com.tonggou.andclient;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.BaseResponse;
import com.tonggou.andclient.network.Network;
import com.tonggou.andclient.network.NetworkState;
import com.tonggou.andclient.network.parser.AsyncJSONResponseParseHandler;
import com.tonggou.andclient.network.request.FeedbackRequest;
import com.tonggou.andclient.parse.FeedBackParser;
import com.tonggou.andclient.parse.UserDateParser;
import com.tonggou.andclient.util.INFO;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.UserInfo;

public class FeedbackActivity extends BaseActivity {

	private static final int  NETWORK_FAILD=-1;
	private static final int  SAVE_SUCCEED=0x003;
	private static final int  SAVE_FAILD=0x004;

	private Handler handler;
	private TextView send;
	private View call,back;
	private 	EditText feedbackInformation,feedbackphone;
	private String userNo;
	private boolean canClick=false;
	private UserInfo user;
	private ProgressBar shopdetilmappro;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userNo=getSharedPreferences(BaseActivity.SETTING_INFOS, 0).getString(BaseActivity.NAME, null);
		setContentView(R.layout.feedback);
		shopdetilmappro=(ProgressBar) findViewById(R.id.shopdetilmappro);   //预约时间
		feedbackInformation=(EditText) findViewById(R.id.feedback_information);
		feedbackphone=(EditText) findViewById(R.id.feedback_phone);
		send=(TextView) findViewById(R.id.feedbacksend);
		call=findViewById(R.id.feedbackcall);
		back=findViewById(R.id.left_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FeedbackActivity.this.finish();
			}
		});

		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String info=feedbackInformation.getText().toString();
				final String num=feedbackphone.getText().toString();
				if(canClick){
					if(info!=null&&!"".equals(info)){
						if(num!=null&&!"".equals(num)){
							if(SomeUtil.isPhoneNumberValid(num)){

								doFeedback(info, num);
							}else{
								Toast.makeText(FeedbackActivity.this,getString(R.string.wrongnumber),Toast.LENGTH_SHORT).show();

							}

						}else{
							Toast.makeText(FeedbackActivity.this,"联系方式不能为空",Toast.LENGTH_SHORT).show();

						}	
					}else{
						Toast.makeText(FeedbackActivity.this,"反馈内容不能为空",Toast.LENGTH_SHORT).show();

					}	
				}else{
					Toast.makeText(FeedbackActivity.this,"读取数据中，请稍等。。。",Toast.LENGTH_SHORT).show();

				}
				
			}
		});
		call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse("tel:0512-66733331"); 
				Intent it = new Intent(Intent.ACTION_DIAL, uri);   
				startActivity(it);  
			}
		});
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){		
				switch(msg.what){
				case NETWORK_FAILD: 
					Toast.makeText(FeedbackActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
					break;
				case SAVE_SUCCEED:
					shopdetilmappro.setVisibility(View.GONE);
					canClick=true;
					showMessge();
					break;
				case SAVE_FAILD: 							
					shopdetilmappro.setVisibility(View.GONE);	
					canClick=true;
					Toast.makeText(FeedbackActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();

					break;
				}
			}
		};
		
		if( TongGouApplication.getInstance().isLogin() ) {
			new Thread(){
				public void run(){						
					getInfo();
				}
			}.start();
		} else {
			shopdetilmappro.setVisibility(View.GONE);
			canClick=true;
		}
	}
	private void getInfo(){
		String url = INFO.HTTP_HEAD+INFO.HOST_IP+"/user/information/userNo/"+userNo;
		UserDateParser userDateParser = new UserDateParser();		
		NetworkState ns = Network.getNetwork(FeedbackActivity.this).httpGetUpdateString(url,userDateParser);	

		if(ns.isNetworkSuccess()){
			if(userDateParser.isSuccessfull()){
				user=userDateParser.getUserDateResponse().getUserInfo();
				if(user!=null){
					sendMessage(SAVE_SUCCEED, null);
				}else{
					sendMessage(SAVE_FAILD,"没有列表数据");
				}

			}else{
				//解析出错
				sendMessage(SAVE_FAILD, userDateParser.getErrorMessage());
			}
		}else{
			//网络出错
			sendMessage(SAVE_FAILD, ns.getErrorMessage());
		}
	}
	private void showMessge(){ 
		if(user.getMobile()!=null){
			feedbackphone.setText(user.getMobile());
		}

		getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit()
		.putString(BaseActivity.PHONENAME, user.getName())
		.putString(BaseActivity.PHONE, user.getMobile()).commit();
	}
	
	private void doFeedback(String content,String mobile){
		showLoadingDialog("发送中...");
		FeedbackRequest request = new FeedbackRequest();
		request.setGuestMode( !TongGouApplication.getInstance().isLogin() );
		request.setGuestRequestParams(content, mobile);
		request.setRequestParams(userNo, content, mobile);
		request.doRequest(this, new AsyncJSONResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				TongGouApplication.showToast(result.getMessage());
				FeedbackActivity.this.finish();
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
				super.onParseFailure(errorCode, errorMsg);
				TongGouApplication.showToast(errorMsg);
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
