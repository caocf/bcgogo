package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;

/**
 * 单个按钮的 对话框
 * @author lwz
 */
public class DoubleButtonDialog extends AbsCustomAlertDialog {
	
	Button mPositiveButton;
	Button mNegativeButton;
	
	public DoubleButtonDialog(Context context) {
		super(context);
	}

	@Override
	protected View getCustomContentView(CharSequence msg) {
		mAlertDialog.setCanceledOnTouchOutside(true);
		View view = View.inflate(getContext(), R.layout.widget_dialog_double, null);
		TextView msgText =(TextView) view.findViewById(R.id.message);
		mNegativeButton = (Button) view.findViewById(R.id.btn_negative);
		mPositiveButton = (Button) view.findViewById(R.id.btn_positive);
		msgText.setText(TextUtils.isEmpty(msg) ? "" : msg);
		mNegativeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
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
	
	public void showDialog(CharSequence msg, final View.OnClickListener positiveBtnClickListener) {
		showDialog(msg);
		mPositiveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				if( positiveBtnClickListener != null ) {
					positiveBtnClickListener.onClick(v);
				}
			} 
		});
	}
	
}
