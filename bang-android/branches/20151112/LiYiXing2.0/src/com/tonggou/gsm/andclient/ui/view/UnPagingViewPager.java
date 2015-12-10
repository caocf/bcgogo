package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class UnPagingViewPager extends ViewPager {

	public UnPagingViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UnPagingViewPager(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return false;
	}
}
