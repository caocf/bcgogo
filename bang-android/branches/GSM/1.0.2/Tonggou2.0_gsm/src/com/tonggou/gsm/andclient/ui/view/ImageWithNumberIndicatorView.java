package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;

public class ImageWithNumberIndicatorView extends FrameLayout {
	
	private ImageView mIcon;
	private TextView mNumberIndicator;

	public ImageWithNumberIndicatorView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ImageWithNumberIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ImageWithNumberIndicatorView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		mIcon = new ImageView(getContext());
		mIcon.setScaleType(ScaleType.FIT_CENTER);
		mIcon.setAdjustViewBounds(true);
//		final int padding = getResources().getDimensionPixelOffset(R.dimen.menu_left_icon_padding);
//		mIcon.setPadding(padding, padding, padding, padding);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		addView(mIcon, lp);
		
		mNumberIndicator = new TextView(getContext());
		mNumberIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		mNumberIndicator.setBackgroundResource(R.drawable.number_indicator_bg);
		final int minSize = getResources().getDimensionPixelOffset(R.dimen.dimen_20dp);
		mNumberIndicator.setMinWidth(minSize);
		mNumberIndicator.setMinHeight(minSize);
		mNumberIndicator.setGravity(Gravity.CENTER);
		mNumberIndicator.setTextColor(Color.WHITE);
		mNumberIndicator.setVisibility(View.GONE);
		LayoutParams indicatorLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		indicatorLP.gravity = Gravity.TOP | Gravity.RIGHT;
		addView(mNumberIndicator, indicatorLP);
	}

	public void setImageResource(int resId) {
		mIcon.setImageResource(resId);
	}
	
	public void setImageDrawable(Drawable drawable) {
		mIcon.setImageDrawable(drawable);
	}
	
	public void setIndicatorNumber(int num) {
		if( num <= 0 ) {
			mNumberIndicator.setVisibility(View.GONE);
			return;
		}
		mNumberIndicator.setVisibility(View.VISIBLE);
		mNumberIndicator.setText(String.valueOf(num));
	}
	
}
