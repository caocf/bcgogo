package com.tonggou.andclient.myview;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tonggou.andclient.R;

public class InfoDialog extends AbsCustomAlertDialog {

	public InfoDialog(Context context) {
		super(context);
	}

	@Override
	protected View getCustomContentView(CharSequence msg) {
		View view = View.inflate(getContext(), R.layout.widget_dialog_info, null);
		TextView msgText =(TextView) view.findViewById(R.id.content);
		msgText.setMovementMethod(ScrollingMovementMethod.getInstance());
		msgText.setText(msg);
		
		view.findViewById(R.id.dismiss_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		return view;
	}
	
	

}
