package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DrivingTrackStatisticsInfoBoardItem extends LinearLayout {

	private TextView mTitleText;
	private TextView mValueText;
	private TextView mUnitText;
	
	public DrivingTrackStatisticsInfoBoardItem(Context context,
			AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DrivingTrackStatisticsInfoBoardItem(Context context) {
		super(context);
		init();
	}

	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		
		mTitleText = new TextView(getContext());
		mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		mTitleText.setTextColor(Color.WHITE);
		addView(mTitleText, lp);
		
		mValueText = new TextView(getContext());
		mValueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		mValueText.setTextColor(Color.WHITE);
		addView(mValueText, lp);
		
		mUnitText = new TextView(getContext());
		mUnitText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		mUnitText.setTextColor(Color.WHITE);
		addView(mUnitText, lp);
	}
	
	public void setValues(Value value) {
		mTitleText.setText(value.title);
		mValueText.setText(value.value);
		mUnitText.setText(value.unit);
	}
	
	public void updateValue(CharSequence value) {
		mValueText.setText(value);
	}
	
	public static class Value {
		public CharSequence title;
		public CharSequence value;
		public CharSequence unit;
		
		public Value(CharSequence title, CharSequence value, CharSequence unit) {
			super();
			this.title = title;
			this.value = value;
			this.unit = unit;
		}
	}
}
