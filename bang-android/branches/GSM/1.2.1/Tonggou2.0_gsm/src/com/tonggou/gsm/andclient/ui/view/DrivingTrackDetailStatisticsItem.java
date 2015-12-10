package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.R.color;

/**
 * 行车轨迹详情统计子项布局
 * @author lwz
 *
 */
public class DrivingTrackDetailStatisticsItem extends DrivingTrackStatisticsItem {

	private TextView mLeftIndiactorText;
	
	public DrivingTrackDetailStatisticsItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DrivingTrackDetailStatisticsItem(Context context) {
		super(context);
	}

	@Override
	protected void init() {
		super.init();
		mLeftIndiactorText = new TextView(getContext());
		mLeftIndiactorText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_h3));
		mLeftIndiactorText.setTextColor(color.gray);
		LayoutParams indicatorLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		mLeftIndiactorText.setGravity(Gravity.BOTTOM | Gravity.LEFT);
		indicatorLP.leftMargin = getResources().getDimensionPixelOffset(R.dimen.dimen_10dp);
		addView(mLeftIndiactorText, 0, indicatorLP);
		
		mValueText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_h1));
		mValueText.setGravity(Gravity.BOTTOM);
		LayoutParams valueLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		valueLP.weight = 1;
		mValueText.setLayoutParams(valueLP);
		
		LayoutParams unitLP = (LayoutParams) mUnitText.getLayoutParams();
		unitLP.height = LayoutParams.MATCH_PARENT;
		unitLP.rightMargin = getContext().getResources().getDimensionPixelOffset(R.dimen.dimen_10dp);
		mUnitText.setGravity(Gravity.BOTTOM);
		mUnitText.setLayoutParams(unitLP);
	}
	
	/**
	 * 设置显示的值
	 * @param leftIndicatorValue	左边指示的文字
	 * @param iconRes	图标
	 * @param value		显示的值
	 * @param valueColor 值的颜色
	 * @param unit 单位
	 */
	public void setItemValue(CharSequence leftIndicatorValue, int iconRes, CharSequence value, int valueColor, CharSequence unit) {
		super.setItemValue(iconRes, value, valueColor, unit);
		mLeftIndiactorText.setText(leftIndicatorValue);
	}
	
	/**
	 * 设置显示的值
	 * @param leftIndicatorValue	左边指示的文字
	 * @param icon		图标
	 * @param value		显示的值
	 * @param valueColor 值的颜色
	 * @param unit 单位
	 */
	public void setItemValue(CharSequence leftIndicatorValue, Drawable icon, CharSequence value, int valueColor, CharSequence unit) {
		super.setItemValue(icon, value, valueColor, unit);
		mLeftIndiactorText.setText(leftIndicatorValue);
	}
	
}
