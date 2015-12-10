package com.tonggou.andclient.myview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.tonggou.andclient.R;

/**
 * º”‘ÿ ∂‘ª∞øÚ
 * @author lwz
 *
 */
public class LoadingDialog extends AbsCustomAlertDialog {
	
	public LoadingDialog(Context context) {
		super(context);
	}

	@Override
	protected View getCustomContentView(CharSequence msg) {
		View view = View.inflate(getContext(), R.layout.widget_dialog_loading, null);
		TextView msgText =(TextView) view.findViewById(R.id.message);
		msgText.setText(msg);
		return view;
	}
	
	@Override
	public void showDialog(CharSequence msg) {
		super.showDialog(msg);
		mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
		WindowManager.LayoutParams lp=mAlertDialog.getWindow().getAttributes();
		lp.dimAmount=0.01f;
		mAlertDialog.getWindow().setAttributes(lp);
		mAlertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}
	
}
