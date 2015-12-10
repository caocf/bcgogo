package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;

/**
 * 行车轨迹统计子项布局
 * @author lwz
 *
 */
public class DrivingTrackStatisticsItem extends LinearLayout {

	ImageView mIcon;
	TextView mValueText;
	TextView mUnitText;
	
	public DrivingTrackStatisticsItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DrivingTrackStatisticsItem(Context context) {
		super(context);
		init();
	}

	protected void init() {
		setOrientation(LinearLayout.HORIZONTAL);
		
		// addViews
		mIcon = new ImageView(getContext());
		int iconSize = getResources().getDimensionPixelSize(R.dimen.driving_track_icon_size);
		mIcon.setAdjustViewBounds(true);
		mIcon.setScaleType(ScaleType.FIT_CENTER);
		LayoutParams iconLP = new LayoutParams(iconSize, iconSize);
		iconLP.rightMargin = getResources().getDimensionPixelOffset(R.dimen.margin_right_5dp);
		addView(mIcon, iconLP);
		
		mValueText = new TextView(getContext());
		mValueText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.driving_track_statisics_item_text));
		mValueText.setGravity(Gravity.BOTTOM);
		LayoutParams valueLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		valueLP.weight = 1;
		addView(mValueText, valueLP);
		
		mUnitText = new TextView(getContext());
		mUnitText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.driving_track_statisics_item_unit_text));
		LayoutParams unitLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addView(mUnitText, unitLP);
	}
	
	/**
	 * 设置显示的值
	 * @param iconRes	图标
	 * @param value		显示的值
	 * @param valueColor 值的颜色
	 * @param unit 单位
	 */
	public void setItemValue(int iconRes, CharSequence value, int valueColor, CharSequence unit) {
		mIcon.setImageResource(iconRes);
		mValueText.setText(value);
		mValueText.setTextColor(getResources().getColor(valueColor));
		mUnitText.setText(unit);
	}
	
	/**
	 * 设置显示的值
	 * @param icon		图标
	 * @param value		显示的值
	 * @param valueColor 值的颜色
	 * @param unit 单位
	 */
	public void setItemValue(Drawable icon, CharSequence value, int valueColor, CharSequence unit) {
		mIcon.setImageDrawable(icon);
		mValueText.setText(value);
		mValueText.setTextColor(valueColor);
		mUnitText.setText(unit);
	}
	
}
