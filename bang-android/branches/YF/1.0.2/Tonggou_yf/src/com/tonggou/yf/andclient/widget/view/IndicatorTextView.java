package com.tonggou.yf.andclient.widget.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.tonggou.yf.andclient.R;

/**
 * 带有指示器的 TextView
 * @author lwz
 *
 */
public class IndicatorTextView extends AbsIndicatorView {
	
	private TextView mTextView;

	public IndicatorTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IndicatorTextView(Context context) {
		super(context);
	}
	
	@Override
	View createMainView() {
		mTextView = new TextView(getContext());
		mTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		mTextView.setBackgroundColor(Color.TRANSPARENT);
		final int padding = getResources().getDimensionPixelSize(R.dimen.dimen_5dp);
		mTextView.setPadding(padding, 0, padding, 0);
		setTextViewCommonAttributes(mTextView);
		return mTextView;
	}
	
	/**
	 * 设置 TextView 的内容
	 * @param value
	 */
	public void setTextValue(CharSequence value) {
		mTextView.setText(value);
	}
	
	public void setTextMinLines(int lines) {
		mTextView.setSingleLine(false);
		mTextView.setMinLines(lines);
		mTextView.setGravity(Gravity.TOP | Gravity.LEFT);
		
		LayoutParams lp = (LayoutParams) mLeftTextIndiactor.getLayoutParams();
		lp.gravity = Gravity.TOP;
		mLeftTextIndiactor.setLayoutParams(lp);
	}
	
	/**
	 * 得到 TextView, 以便设置其他属性
	 * @return
	 */
	public TextView getTextView() {
		return mTextView;
	}
	
	/**
	 * 得到 TextView 的内容
	 * @return
	 */
	public String getTextValue() {
		return mTextView.getText().toString().trim();
	}
}
