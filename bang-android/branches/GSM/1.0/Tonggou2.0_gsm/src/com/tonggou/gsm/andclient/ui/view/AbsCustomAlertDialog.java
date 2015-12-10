package com.tonggou.gsm.andclient.ui.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public abstract class AbsCustomAlertDialog {

	private Context mContext;
	protected AlertDialog mAlertDialog;
	
	public AbsCustomAlertDialog(Context context) {
		mContext = context;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	public Dialog getDialog() {
		return mAlertDialog;
	}
	
	public void showDialog(CharSequence msg) {
		dismissDialog(this);
		mAlertDialog = new AlertDialog.Builder(mContext).create();
		mAlertDialog.setCanceledOnTouchOutside(false);
		if( mContext instanceof Activity ) {
			if( ((Activity)mContext).isFinishing() ) {
				return;
			}
		}
		mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		mAlertDialog.show();
		Window window = mAlertDialog.getWindow();
		window.setContentView(getCustomContentView(msg));
	}
	
	public void showDialog( int msgRes ) {
		showDialog( msgRes <= 0 ? null : mContext.getResources().getString(msgRes) );
	}
	
	protected abstract View getCustomContentView(CharSequence msg);
	
	public static void dismissDialog(AbsCustomAlertDialog dialog) {
		if( dialog != null && dialog.isShowing() ) {
			dialog.dismiss();
		}
	}
	
	public boolean isShowing() {
		return mAlertDialog != null && mAlertDialog.isShowing();
	}
	
	public void dismiss() {
		if( isShowing() ) {
			mAlertDialog.dismiss();
		}
		mAlertDialog = null;
	}
}
