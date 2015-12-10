package com.tonggou.gsm.andclient.ui.view;

import com.tonggou.gsm.andclient.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Spinner;

public class IndicatorSpinner extends AbsIndicatorView {

	private Spinner mSpinner; 
	
	public IndicatorSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IndicatorSpinner(Context context) {
		super(context);
	}

	@SuppressLint("NewApi")
	@Override
	View createMainView() {
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			mSpinner = new Spinner(getContext(), Spinner.MODE_DROPDOWN);
		} else {
			mSpinner = new Spinner(getContext());
		}
		mSpinner.setBackgroundResource(R.drawable.spinner_indicator);
		return mSpinner;
	}
	
	public Spinner getSpinner() {
		return mSpinner;
	}

}
