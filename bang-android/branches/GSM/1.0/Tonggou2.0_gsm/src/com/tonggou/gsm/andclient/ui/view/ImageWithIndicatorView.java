package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;

public class ImageWithIndicatorView extends FrameLayout {
	
	private ImageView mIcon;
	private TextView mIndicatorText;

	public ImageWithIndicatorView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ImageWithIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ImageWithIndicatorView(Context context) {
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
		
		mIndicatorText = new TextView(getContext());
		mIndicatorText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		mIndicatorText.setBackgroundResource(R.drawable.number_indicator_bg);
		final int minSize = getResources().getDimensionPixelOffset(R.dimen.dimen_20dp);
		mIndicatorText.setMinWidth(minSize);
		mIndicatorText.setMinHeight(minSize);
		mIndicatorText.setGravity(Gravity.CENTER);
		mIndicatorText.setTextColor(Color.WHITE);
		mIndicatorText.setVisibility(View.GONE);
		LayoutParams indicatorLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		indicatorLP.gravity = Gravity.TOP | Gravity.RIGHT;
		addView(mIndicatorText, indicatorLP);
	}

	public void setImageResource(int resId) {
		mIcon.setImageResource(resId);
	}
	
	public void setImageDrawable(Drawable drawable) {
		mIcon.setImageDrawable(drawable);
	}
	
	public void setIndicatorBackground(int res) {
		mIndicatorText.setBackgroundResource(res);
	}
	
	public void setIndicator(String indicator) {
		if( TextUtils.isEmpty(indicator) ) {
			mIndicatorText.setVisibility(View.GONE);
			return;
		}
		mIndicatorText.setVisibility(View.VISIBLE);
		mIndicatorText.setText(indicator);
	}
	
}
