package com.tonggou.andclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;


/**
 *  *  提醒评价
 * @author fbl 
 */
public class RemindEvaluateActivity extends BaseActivity{


	///////////////////////////////////////////////////
	private int alertCounts = 0;
	private String messString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.carerror);
		
		Intent intent = getIntent();
		messString = intent.getStringExtra("message");
		
		new Thread(){
			public void run() {
				  RemindEvaluateActivity.this.runOnUiThread(new Runnable(){
					  public void run() {
						  showRemindDialog();
					  }
				  });
			}

		}.start();

	}
	
	private void showRemindDialog(){
		alertCounts++;
		final AlertDialog wrongAlert= new AlertDialog.Builder(this).create();		
		wrongAlert.show();	
		Window window = wrongAlert.getWindow();
		window.setContentView(R.layout.reminddialog);
		TextView remindtitle = (TextView)window.findViewById(R.id.alarm_title);
		TextView remindcontent= (TextView)window.findViewById(R.id.content_text_alert);

		wrongAlert.setCancelable(false);
		wrongAlert.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface arg0) {
				alertCounts--;
				if(alertCounts==0){
					RemindEvaluateActivity.this.finish();
				}
			}			
		});
		
		View retryOper = window.findViewById(R.id.connect_again);  //确定
		retryOper.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wrongAlert.cancel();
				wrongAlert.dismiss();
				
				if(messString!=null&&!"".equals(messString)){
				    String[] params = messString.split(",");
					Intent intent=new Intent(RemindEvaluateActivity.this,OrderDetilActivity.class);
					intent.putExtra("tonggou.server.orderid", params[params.length-1]);
					startActivity(intent);
				}
				
				RemindEvaluateActivity.this.finish();
			}
		});
		
		View cancelOper = window.findViewById(R.id.connect_cancel);  //取消
		cancelOper.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				wrongAlert.cancel();
				wrongAlert.dismiss();
				RemindEvaluateActivity.this.finish();
			}
			
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
