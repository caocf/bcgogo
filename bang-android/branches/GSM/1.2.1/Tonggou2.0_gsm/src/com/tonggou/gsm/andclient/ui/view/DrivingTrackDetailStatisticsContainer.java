package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.tonggou.gsm.andclient.R;

/**
 * 行车轨迹统计容器
 * @author lwz
 *
 */
public class DrivingTrackDetailStatisticsContainer extends DrivingTrackStatisticsContainer {
	
	public DrivingTrackDetailStatisticsContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DrivingTrackDetailStatisticsContainer(Context context) {
		super(context);
	}
	
	@Override
	DrivingTrackStatisticsItem[] getItems() {
		return new DrivingTrackDetailStatisticsItem[]{
				new DrivingTrackDetailStatisticsItem(getContext()),
				new DrivingTrackDetailStatisticsItem(getContext()),
				new DrivingTrackDetailStatisticsItem(getContext()),
				new DrivingTrackDetailStatisticsItem(getContext())
		};
	}
	
	@Override
	LinearLayout getItemsContainer(DrivingTrackStatisticsItem item1, DrivingTrackStatisticsItem item2) {
		LinearLayout itemContainer = new LinearLayout(getContext());
		itemContainer.setOrientation(LinearLayout.VERTICAL);
		item1.setBackgroundResource(R.drawable.divider_horizontal_bg);
		item2.setBackgroundResource(R.drawable.divider_horizontal_bg);
		item1.setGravity(Gravity.CENTER_VERTICAL);
		item2.setGravity(Gravity.CENTER_VERTICAL);
		final int padding = getResources().getDimensionPixelOffset(R.dimen.dimen_10dp);
		item1.setPadding(0, padding, 0, padding);
		item2.setPadding(0, padding, 0, padding);
		itemContainer.addView(item1);
		itemContainer.addView(item2);
		return itemContainer;
	}
	
	/**
	 * 设置每个统计子项的属性，数组大小为 4
	 * @param leftIndicatorValues 左边指示文字
	 * @param iconsRes	要现实的图片
	 * @param values	数值
	 * @param units		数值单位
	 */
	public void setItemsValue(CharSequence[] leftIndicatorValues, int[] iconsRes, CharSequence[] values, int[] valuesColor, CharSequence[] units) {
		final int SIZE = getMinLength( leftIndicatorValues.length, items.length, iconsRes.length, values.length, units.length );
		for( int i=0; i<SIZE; i++) {
			((DrivingTrackDetailStatisticsItem)items[i]).setItemValue(leftIndicatorValues[i], iconsRes[i], values[i], valuesColor[i], units[i]);
		}
	}
	
	/**
	 * 设置每个统计子项的属性，数组大小为 4
	 * @param leftIndicatorValues 左边指示文字
	 * @param iconsRes	要现实的图片
	 * @param values	数值
	 * @param units		数值单位
	 */
	public void setItemsValue(CharSequence[] leftIndicatorValues, Drawable[] icons, CharSequence[] values, int[] valuesColor, CharSequence[] units) {
		final int SIZE = getMinLength( leftIndicatorValues.length, items.length, icons.length, values.length, units.length );
		for( int i=0; i<SIZE; i++) {
			((DrivingTrackDetailStatisticsItem)items[i]).setItemValue(leftIndicatorValues[i], icons[i], values[i], valuesColor[i], units[i]);
		}
	}
}
