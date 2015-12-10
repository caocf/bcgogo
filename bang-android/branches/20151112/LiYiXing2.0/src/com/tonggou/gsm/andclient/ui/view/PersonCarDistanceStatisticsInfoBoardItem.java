package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PersonCarDistanceStatisticsInfoBoardItem extends LinearLayout {

	private TextView mTitleText;
	private TextView mValueText;
	
	public PersonCarDistanceStatisticsInfoBoardItem(Context context,
			AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PersonCarDistanceStatisticsInfoBoardItem(Context context) {
		super(context);
		init();
	}

	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;

		mTitleText = new TextView(getContext());
		mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		mTitleText.setTextColor(Color.WHITE);
		addView(mTitleText, lp);
		
		mValueText = new TextView(getContext());
		mValueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		mValueText.setTextColor(Color.WHITE);
		addView(mValueText, lp);
	}
	
	public void setValues(Value value) {
		mTitleText.setText(value.title);
		mValueText.setText(value.value);
	}
	
	public void updateValue(CharSequence value) {
		mValueText.setText(value);
	}
	
	public static class Value {
		public CharSequence title;
		public CharSequence value;
		public CharSequence unit;
		
		public Value(CharSequence title, CharSequence value) {
			super();
			this.title = title;
			this.value = value;
		}
	}
}
