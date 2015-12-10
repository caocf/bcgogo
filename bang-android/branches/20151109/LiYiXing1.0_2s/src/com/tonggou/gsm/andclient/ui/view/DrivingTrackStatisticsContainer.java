package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 行车轨迹统计容器
 * @author lwz
 *
 */
public class DrivingTrackStatisticsContainer extends LinearLayout {

	DrivingTrackStatisticsItem[] items;

	public DrivingTrackStatisticsContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DrivingTrackStatisticsContainer(Context context) {
		super(context);
		init();
	}

	void init() {
		setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.weight = 1;
		items = getItems();
		addView(getItemsContainer(items[0], items[2]), lp);
		addView(getItemsContainer(items[1], items[3]), lp);
	}

	DrivingTrackStatisticsItem[] getItems() {
		return new DrivingTrackStatisticsItem[]{
				new DrivingTrackStatisticsItem(getContext()),
				new DrivingTrackStatisticsItem(getContext()),
				new DrivingTrackStatisticsItem(getContext()),
				new DrivingTrackStatisticsItem(getContext())
		};
	}

	LinearLayout getItemsContainer(DrivingTrackStatisticsItem item1, DrivingTrackStatisticsItem item2) {
		LinearLayout itemContainer = new LinearLayout(getContext());
		itemContainer.setOrientation(LinearLayout.VERTICAL);
		itemContainer.addView(item1);
		itemContainer.addView(item2);
		return itemContainer;
	}

	/**
	 * 设置每个统计子项的属性，数组大小为 4
	 * @param iconsRes	要现实的图片
	 * @param values	数值
	 * @param units		数值单位
	 */
	public void setItemsValue(int[] iconsRes, CharSequence[] values, int[] valuesColor, CharSequence[] units) {
		final int SIZE = getMinLength( items.length, iconsRes.length, values.length, units.length );
		for( int i=0; i<SIZE; i++) {
			items[i].setItemValue(iconsRes[i], values[i], valuesColor[i], units[i]);
		}
	}

	/**
	 * 设置每个统计子项的属性，数组大小为 4
	 * @param iconsRes	要现实的图片
	 * @param values	数值
	 * @param units		数值单位
	 */
	public void setItemsValue(Drawable[] icons, CharSequence[] values, int[] valuesColor, CharSequence[] units) {
		final int SIZE = getMinLength( items.length, icons.length, values.length, units.length );
		for( int i=0; i<SIZE; i++) {
			items[i].setItemValue(icons[i], values[i], valuesColor[i], units[i]);
		}
	}

	int getMinLength(int...lengths) {
		final int SIZE = lengths.length;
		int minLength = 0;
		minLength = lengths[0];
		for( int i=1; i<SIZE; i++ ) {
			minLength = Math.min(minLength, lengths[i]);
		}
		return minLength;
	}
}
