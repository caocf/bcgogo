package com.tonggou.gsm.andclient.ui.view;

import android.app.Activity;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.tonggou.gsm.andclient.R;

public class FindPwdPopupView {
	
	private PopupWindow mFindPwdPop;
	
	public interface OnSendPwdBtnClickListener {
		public void onSend(EditText inputEdit, String content);
	}
	
	public void showFindPwdPopup(Activity activity, View anchorView, final String phoneNo, final OnSendPwdBtnClickListener l) {
		Resources res = anchorView.getResources();
		View contentView = View.inflate(activity, R.layout.widget_dialog_forgot_pwd, null);
		final EditText inputEdit = (EditText) contentView.findViewById(R.id.phoneNo_editText);
		inputEdit.setText(phoneNo);
		inputEdit.setSelection(inputEdit.getText().toString().length());
		(contentView.findViewById(R.id.send_new_pwd_btn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( l != null ) {
					l.onSend(inputEdit, inputEdit.getText().toString());
				}
			}
		});
		int location[] = new int[2];
		anchorView.getLocationOnScreen(location);
		int width = res.getDimensionPixelOffset(R.dimen.find_pwd_popup_width);
		int height = res.getDimensionPixelOffset(R.dimen.find_pwd_popup_height);
		mFindPwdPop = new PopupWindow(contentView, width, height, true );
		mFindPwdPop.setOutsideTouchable(true);
		mFindPwdPop.setBackgroundDrawable(res.getDrawable(R.drawable.bg_popup_find_pwd));
		if( !activity.isFinishing() ) {
			mFindPwdPop.showAtLocation(anchorView, Gravity.CENTER_HORIZONTAL | Gravity.TOP,
					0, location[1] - height);
		}
	}
	
	public PopupWindow getPopupWindow() {
		return mFindPwdPop;
	}
	
	public void dismiss() {
		if( mFindPwdPop != null && mFindPwdPop.isShowing() ) {
			mFindPwdPop.dismiss();
		}
		mFindPwdPop = null;
	}
	
}
