package com.tonggou.andclient.network;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tonggou.andclient.CarErrorActivity;
import com.tonggou.andclient.OrderDetilActivity;
import com.tonggou.andclient.R;
import com.tonggou.andclient.SettingActivity;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.jsonresponse.UpgradeCheckResponse;
import com.tonggou.andclient.network.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.andclient.network.request.UpdateCheekRequest;

/**
 * 检查更新
 * @author lwz
 *
 */
public class DefaultUpdateCheck implements DialogInterface.OnClickListener {

	private String downUrl;
	private Handler handler;
	private String updateMessage; 
	/**
	 * 升级检测监听
	 * @author lwz
	 *
	 */
	public class OnUpdateCheckListener {
		/**
		 * 有新版本
		 * @param isForceUpdate true：强制  ; false：提醒
		 * @param updateMesssage 升级提示信息
		 */
		//		delete by fbl
		/*public void onShouldUpdate(boolean isForceUpdate, String updateMesssage) {
			showUpdateAlertDialog(isForceUpdate, updateMesssage);
		}*/
		/**
		 * 没有新版本
		 */
		public void onNothingToUpdate() {
		}

		/**
		 * 检测结束
		 */
		public void onFinish() {

		}
	}

	private Activity mContext;
	private AlertDialog mUpdateDialog,mforceDialog;
	private UpgradeCheckResponse mResponse;

	public DefaultUpdateCheck(Activity context) {
		mContext = context;
	}

	/**
	 * 更新动作
	 * @param listener 不能为 null
	 */
	public void doCheckUpdate(final OnUpdateCheckListener listener) {
		UpdateCheekRequest request = new UpdateCheekRequest();
		request.doRequest(mContext, new AsyncJsonBaseResponseParseHandler<UpgradeCheckResponse>() {

			@Override
			public void onParseSuccess(UpgradeCheckResponse result, byte[] originResult) {
				super.onParseSuccess(result, originResult);
				mResponse = result;

				String action = result.getAction();
				updateMessage = result.getDescription();
				
				TongGouApplication.showLog("升级信息-------"+updateMessage);
				TongGouApplication.showLog("提示升级形式-----"+action);
				if( "normal".equals( action ) ) {  //没有新版本

					listener.onNothingToUpdate();

				}else if("force".equals(action)){ //强制升级 

					showforceUpdateAlertDialog(updateMessage);

				}else {   //提示升级

					showUpdateAlertDialog(updateMessage);
					//listener.onShouldUpdate( "force".equals(action), result.getMessage() );
				}
			}

			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
				listener.onNothingToUpdate();
			}

			@Override
			public void onFinish() {
				super.onFinish();	
				listener.onFinish();
			}

			@Override
			public Class<UpgradeCheckResponse> getTypeClass() {
				return UpgradeCheckResponse.class;
			}

		});
	}
	/**
	 * * add fbl 2014.2.16
	 * 
	 * **/


	private void showforceUpdateAlertDialog(String msg){

		final AlertDialog wrongAlert= new AlertDialog.Builder(mContext).create();		
		wrongAlert.show();	
		Window window = wrongAlert.getWindow();
		window.setContentView(R.layout.force_update_dialog);
		wrongAlert.setCancelable(false);

		TextView remindtitle = (TextView)window.findViewById(R.id.alarm_title);
		TextView remindcontent= (TextView)window.findViewById(R.id.content_text_alert);
		remindcontent.setText(msg);

		View retryOper = window.findViewById(R.id.connect_again);  //升级
		retryOper.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doUPdate();
			}
		});

		View cancelOper = window.findViewById(R.id.connect_cancel);  //取消
		cancelOper.setVisibility(View.GONE);
		if( mContext != null && !mContext.isFinishing() ) {
			mforceDialog.show();
		} else {
			mforceDialog = null;
		}
	}

	private void showUpdateAlertDialog(String msg) {


		final AlertDialog wrongAlert= new AlertDialog.Builder(mContext).create();		
		wrongAlert.show();	
		Window window = wrongAlert.getWindow();
		window.setContentView(R.layout.force_update_dialog);
		wrongAlert.setCancelable(false);

		TextView remindtitle = (TextView)window.findViewById(R.id.alarm_title);
		TextView remindcontent= (TextView)window.findViewById(R.id.content_text_alert);
		remindcontent.setText(msg);

		View retryOper = window.findViewById(R.id.connect_again);  //升级
		retryOper.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doUPdate();
				wrongAlert.cancel();
				wrongAlert.dismiss();
			}
		});


		View cancelOper = window.findViewById(R.id.connect_cancel);  //取消
		cancelOper.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				wrongAlert.cancel();
				wrongAlert.dismiss();	
			}

		});

		if( mContext != null && !mContext.isFinishing() ) {
			mforceDialog.show();
		} else {
			mforceDialog = null;
		}


		/*
		dismissDoalog();
		mUpdateDialog = new AlertDialog.Builder(mContext).create();
		mUpdateDialog.setTitle(isForceUpdate ? "强制升级提示" : "升级提示");
		mUpdateDialog.setButton(DialogInterface.BUTTON_POSITIVE, "是", this);
		mUpdateDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "否", (Message)null);
		mUpdateDialog.setMessage( msg );
		if( mContext != null && !mContext.isFinishing() ) {
			mUpdateDialog.show();
		} else {
			mUpdateDialog = null;
		}
		 */}

	public void dismissDoalog() {
		if( mUpdateDialog != null && mUpdateDialog.isShowing() ) {
			mUpdateDialog.dismiss();
		}
		mUpdateDialog = null;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if( which == DialogInterface.BUTTON_POSITIVE ) {
			doUPdate();
		}
	}

	//屏蔽返回键
	OnKeyListener keylistener = new DialogInterface.OnKeyListener(){
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	} ;
	private void doUPdate(){
		downUrl = mResponse.getUrl();    //下载地址
		if(downUrl==null||"".equals(downUrl)){
			//url错误提示
			TongGouApplication.showToast("升级出错了!");
		}else{
			Uri uri = Uri.parse(downUrl);  
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
			if( !mContext.isFinishing() ) {
				mContext.startActivity(intent);
			}
		}
	}

}
