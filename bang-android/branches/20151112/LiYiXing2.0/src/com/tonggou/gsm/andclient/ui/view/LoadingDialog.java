package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;

/**
 * Loading 对话框
 * @author lwz
 */
public class LoadingDialog extends AbsCustomAlertDialog {
	
	public LoadingDialog(Context context) {
		super(context);
	}

	@Override
	protected View getCustomContentView(CharSequence msg) {
		View view = View.inflate(getContext(), R.layout.widget_dialog_loading, null);
		TextView msgText =(TextView) view.findViewById(R.id.message);
		msgText.setText(TextUtils.isEmpty(msg) ? "" : msg);
		return view;
	}
	
	@Override
	public void showDialog(CharSequence msg) {
		super.showDialog(msg);
		Window window = mAlertDialog.getWindow();
		window.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount=0.3f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}
	
}
