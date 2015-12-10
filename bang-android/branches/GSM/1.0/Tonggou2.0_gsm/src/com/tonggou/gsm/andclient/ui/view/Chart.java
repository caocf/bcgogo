package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.ui.view.ChartItem.ChartItemValue;

public class Chart extends LinearLayout {

	ChartItem[] mChartItems;
	TextView mUnitText;
	final int SIZE = 4;
	
	public Chart(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Chart(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		
		mUnitText = new TextView(getContext());
		mUnitText.setGravity( Gravity.CENTER_VERTICAL | Gravity.LEFT );
		mUnitText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_h4));
		mUnitText.setTextColor(Color.GRAY);
		final int padding = getResources().getDimensionPixelOffset(R.dimen.dimen_2dp);
		mUnitText.setPadding(0, padding, 0, padding);
		addView(mUnitText);
		
		mChartItems = new ChartItem[SIZE];
		for( int i=0; i<SIZE; i++ ) {
			ChartItem item = new ChartItem(getContext());
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			lp.weight = 1;
			lp.topMargin = getResources().getDimensionPixelOffset(R.dimen.driving_track_detail_chart_item_top_margin);
			mChartItems[i] = item;
			addView(item, lp);
		}
	}
	
	public void setUnit(CharSequence unit) {
		mUnitText.setText(getResources().getString(R.string.chinese_unit, unit));
	}
	
	/**
	 * 设置 各个柱子的值， values.length = 4
	 * @param values
	 */
	public void setValues(ChartItemValue[] values) {
		calculateRatio(values);
		int maxWidth = calculateValueMaxLength(values);
		for( int i=0; i<SIZE; i++ ) {
			mChartItems[i].setValues(values[i], maxWidth);
		}
	}
	
	private int calculateValueMaxLength(final ChartItemValue[] values) {
		int maxLength = values[0].value.length();
		for( int i=1; i<values.length; i++ ) {
			maxLength = Math.max(maxLength, values[i].value.length());
		}
		return maxLength;
	}
	
	private void calculateRatio(final ChartItemValue[] values) {
		float maxRatio = values[0].ratio;
		for( int i=1; i<SIZE; i++ ) {
			maxRatio = Math.max(maxRatio, values[i].ratio);
		}
		for( int i=0; i<SIZE; i++ ) {
			if( maxRatio == 0 ) {
				values[i].ratio = 1;
			} else {
				values[i].ratio /= maxRatio;
			}
		}
	}
	
}
