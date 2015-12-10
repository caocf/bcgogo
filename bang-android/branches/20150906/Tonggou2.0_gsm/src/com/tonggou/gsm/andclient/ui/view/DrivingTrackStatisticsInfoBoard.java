package com.tonggou.gsm.andclient.ui.view;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.ui.view.DrivingTrackStatisticsInfoBoardItem.Value;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

public class DrivingTrackStatisticsInfoBoard extends LinearLayout {

	public static final int ITEM_SIZE = 4; 
	
	private DrivingTrackStatisticsInfoBoardItem[] items;
	
	public DrivingTrackStatisticsInfoBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DrivingTrackStatisticsInfoBoard(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		setOrientation(LinearLayout.HORIZONTAL);
		int padding = getResources().getDimensionPixelSize(R.dimen.dimen_10dp);
		setPadding(0, padding, 0, padding);
		items = new DrivingTrackStatisticsInfoBoardItem[] {
			new DrivingTrackStatisticsInfoBoardItem(getContext()),	
			new DrivingTrackStatisticsInfoBoardItem(getContext()),	
			new DrivingTrackStatisticsInfoBoardItem(getContext()),	
			new DrivingTrackStatisticsInfoBoardItem(getContext())
		};
		
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.weight = 1;
		lp.gravity = Gravity.CENTER;
		
		for( int i=0; i<items.length; i++ ) {
			addView(items[i], lp);
		}
	}
	
	public void setItemsValue(Value[] values) {
		final int length = Math.min( values.length , items.length);
		for( int i=0; i<length; i++ ) {
			items[i].setValues(values[i]);
		}
	}
	
	public void updateItemsValue(CharSequence[] values) {
		final int length = Math.min( values.length , items.length);
		for( int i=0; i<length; i++ ) {
			items[i].updateValue(values[i]);
		}
	}
}
