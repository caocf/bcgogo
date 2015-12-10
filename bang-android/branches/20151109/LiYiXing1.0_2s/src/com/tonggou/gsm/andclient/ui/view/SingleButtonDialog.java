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
public class SingleButtonDialog extends AbsCustomAlertDialog {
	
	Button mConfirmButton;
	boolean mCanceledOnTouchOutside = true;
	
	public SingleButtonDialog(Context context, boolean canceledOnTouchOutside) {
		super(context);
		mCanceledOnTouchOutside = canceledOnTouchOutside;
	}
	
	public SingleButtonDialog(Context context) {
		super(context);
	}

	@Override
	protected View getCustomContentView(CharSequence msg) {
		mAlertDialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
		View view = View.inflate(getContext(), R.layout.widget_dialog_single, null);
		TextView msgText =(TextView) view.findViewById(R.id.message);
		mConfirmButton = (Button) view.findViewById(R.id.btn_confirm);
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
	
	public void showDialog(CharSequence msg, CharSequence buttonText, final View.OnClickListener l) {
		showDialog(msg);
		mConfirmButton.setText(buttonText);
		mConfirmButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				if( l != null ) {
					l.onClick(v);
				}
			} 
		});
	}
	
}
