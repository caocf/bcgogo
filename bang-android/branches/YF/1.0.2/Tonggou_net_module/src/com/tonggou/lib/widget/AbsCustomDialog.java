package com.tonggou.lib.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public abstract class AbsCustomDialog {

	private Context mContext;
	public Dialog mDialog;
	
	public AbsCustomDialog(Context context) {
		mContext = context;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	public void show(boolean isCanceledOnTouchOutside, boolean isCanclable) {
		dismissDialog(this);
		mDialog = new AlertDialog.Builder(mContext).create();
		mDialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
		mDialog.setCancelable(isCanclable);
		if( mContext instanceof Activity ) {
			if( ((Activity)mContext).isFinishing() ) {
				return;
			}
		}
		Window window = mDialog.getWindow();
		window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND | LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		mDialog.show();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = getWindowDimAmount();
		window.setAttributes(lp);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		window.setContentView(createContentView());
	}
	
	/**
	 * 创建该 dialog 中的 View
	 * @return
	 */
	public abstract View createContentView();
	
	public float getWindowDimAmount() {
		return 0.04f;
	}
	
	public static void dismissDialog(AbsCustomDialog dialog) {
		if( dialog != null && dialog.isShowing() ) {
			dialog.dismiss();
		}
	}
	
	public boolean isShowing() {
		return mDialog != null && mDialog.isShowing();
	}
	
	public void dismiss() {
		if( isShowing() ) {
			mDialog.dismiss();
		}
		mDialog = null;
	}
}
