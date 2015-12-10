package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;

/**
 * 柱状图图表柱子
 * @author lwz
 *
 */
public class ChartItem extends LinearLayout {
	
	TextView mTitleText;
	TextView mValueText;

	ChartItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	ChartItem(Context context) {
		super(context);
		init();
	}

	private void init() {
		setOrientation(LinearLayout.HORIZONTAL);
		
		mValueText = new TextView(getContext());
		mValueText.setGravity( Gravity.CENTER_VERTICAL | Gravity.LEFT );
		mValueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		mValueText.setTextColor(Color.WHITE);
		final int paddingLeft = getResources().getDimensionPixelOffset(R.dimen.dimen_10dp);
		final int padding = getResources().getDimensionPixelOffset(R.dimen.dimen_5dp);
		mValueText.setPadding(paddingLeft, padding, 0, padding);
		LayoutParams valueLP = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(mValueText, valueLP);
		
		mTitleText = new TextView(getContext());
		mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		mTitleText.setGravity(Gravity.CENTER_VERTICAL);
		mTitleText.setTextColor(Color.GRAY);
		LayoutParams indicatorLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		indicatorLP.leftMargin = getResources().getDimensionPixelOffset(R.dimen.dimen_5dp);
		indicatorLP.weight = 1;
		addView(mTitleText, indicatorLP);
		
	}
	
	/**
	 * 设置柱子的项值
	 */
	public void setValues(ChartItemValue value, int valueMinLength) {
		mTitleText.setText(value.title);
		mValueText.setText(value.value);
		mValueText.setBackgroundColor(value.color);
		
		LayoutParams lp = (LayoutParams) mValueText.getLayoutParams();
		int defaultWidth = getResources().getDimensionPixelOffset(R.dimen.driving_track_detail_chart_width);
		float valueMinWidth = ( valueMinLength / 2f ) * mValueText.getTextSize();	// 数字只占半个位置
		float titleWidth = mTitleText.getTextSize() * mTitleText.getText().length();
		float measureWidth = (defaultWidth - valueMinWidth - titleWidth) * value.ratio
				+ valueMinWidth;
		lp.width = (int)measureWidth;
		mValueText.setLayoutParams(lp);
	}
	
	public static class ChartItemValue {
		/** 标题 */
		CharSequence title;
		/** 柱子的真实值，用来 显示的 */
		CharSequence value;
		/** 柱子的颜色 */
		int color;
		/** 柱子高度的比例,值的区间为 [0, 1] */
		float ratio;
		
		/**
		 * 
		 * @param title 标题
		 * @param value 柱子的真实值，用来 显示的
		 * @param unit 值的单位
		 * @param color 柱子的颜色
		 * @param ratio 柱子高度的比例,值的区间为 [0, 1]
		 */
		public ChartItemValue(CharSequence title, CharSequence value, int color, float ratio) {
			super();
			this.title = title;
			this.value = value;
			this.color = color;
			this.ratio = ratio;
		}
		
	}
}
