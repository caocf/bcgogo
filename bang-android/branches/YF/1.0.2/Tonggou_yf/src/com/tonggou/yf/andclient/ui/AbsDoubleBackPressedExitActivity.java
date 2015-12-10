package com.tonggou.yf.andclient.ui;

import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.tonggou.yf.andclient.App;
import com.tonggou.yf.andclient.R;

public abstract class AbsDoubleBackPressedExitActivity extends SimpleTitleBarActivity implements Handler.Callback {
	
	private final int HANDLER_MSG_EXIT = 1;

	private static Boolean isExit = false; 						// 是否退出变量
	private Handler mExitHandler = null;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if( isCanExit() && keyCode == KeyEvent.KEYCODE_BACK ){
			if(mExitHandler == null) {
				mExitHandler = new Handler(this);
			}
			exitAppAction();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	abstract boolean isCanExit();
	
	@Override
	public boolean handleMessage(Message msg) {
		if( isExit ) {
			doExit();
		} else {
			if( msg.what == HANDLER_MSG_EXIT ) {
				prepareEixt();
			}
		}
		
		return true;
	}

	protected void doExit() {
		mExitHandler.removeCallbacksAndMessages(null);
		isExit = false;
//		LauncherActivity.exitApp(this);
		finish();
	}
	
	private void prepareEixt() {
		 isExit = true;
	     App.showShortToast(getString(R.string.info_pressed_back_key_one_more_time_to_exit_app));
	     mExitHandler.postDelayed(new CancelExitActionRunnable(), 2000);
	}
	
	private void exitAppAction() {
		mExitHandler.sendEmptyMessage(HANDLER_MSG_EXIT);
	}
	
	class CancelExitActionRunnable implements Runnable {

		@Override
		public void run() {
			isExit = false;
		}
	}
}
