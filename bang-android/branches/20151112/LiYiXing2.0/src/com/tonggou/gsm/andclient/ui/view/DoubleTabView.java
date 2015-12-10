package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tonggou.gsm.andclient.R;

public class DoubleTabView extends LinearLayout implements OnClickListener {
	private Button[] mTabItems;
	private onTabSelectedListener mOnTabSelectedListener;
	private int mCurrentSelectedPos = -1;

	public DoubleTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public DoubleTabView(Context context) {
		super(context);

		init(null);
	}

	public void init(AttributeSet attrs) {
		setOrientation(LinearLayout.HORIZONTAL);
		mTabItems = new Button[] { createTabItem(), createTabItem() };
		mTabItems[0].setId(R.id.tab_first);
		mTabItems[1].setId(R.id.tab_second);

		addView(mTabItems[0]);
		addView(mTabItems[1]);

		if( attrs != null ) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DoubleTabView);

			setTabTextSize(a.getDimension(R.styleable.DoubleTabView_tabTextSize,
					getResources().getDimension(R.dimen.text_h3)));

			setTabContent(a.getString(R.styleable.DoubleTabView_fristTabText),
					a.getString(R.styleable.DoubleTabView_secondTabText));

			setTabBackground(a.getResourceId(R.styleable.DoubleTabView_fristTabBackground, 0),
					a.getResourceId(R.styleable.DoubleTabView_secondTabBackground, 0));

			if(  a.hasValue(R.styleable.DoubleTabView_tabTextColor) ) {
				ColorStateList stateList = a.getColorStateList(R.styleable.DoubleTabView_tabTextColor);
				mTabItems[0].setTextColor(stateList);
				mTabItems[1].setTextColor(stateList);
			}

			a.recycle();
		}
		setCurrentTab(0);
	}

	private Button createTabItem() {
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
		Button tab = new Button(getContext());
		tab.setGravity(Gravity.CENTER);
		tab.setLayoutParams(lp);
		tab.setOnClickListener(this);
		return tab;
	}

	/**
	 * 设置 tab 字体大小
	 * @param textSize
	 */
	public void setTabTextSize(float textSize) {
		mTabItems[0].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		mTabItems[1].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
	}

	/**
	 * 设置 tab 文字颜色
	 * @param color
	 */
	public void setTabTextColor( int color ) {
		if( color <= 0 ) {
			return;
		}
		mTabItems[0].setTextColor(color);
		mTabItems[1].setTextColor(color);
	}

	/**
	 * 设置 tab 的文字
	 * @param firstTabContent
	 * @param secondTabContent
	 */
	public void setTabContent(CharSequence firstTabContent, CharSequence secondTabContent) {
		mTabItems[0].setText(firstTabContent);
		mTabItems[1].setText(secondTabContent);
	}

	/**
	 * 设置 tab 背景
	 * @param firstTabbackgroundRes
	 * @param secondTabbackgroundRes
	 */
	public void setTabBackground(int firstTabbackgroundRes, int secondTabbackgroundRes) {
		if( firstTabbackgroundRes > 0 ) {
			mTabItems[0].setBackgroundResource(firstTabbackgroundRes);
		}
		if( secondTabbackgroundRes > 0 ) {
			mTabItems[1].setBackgroundResource(secondTabbackgroundRes);
		}
	}

	/**
	 * 设置当前选中的 tab
	 * @param index
	 */
	public void setCurrentTab(int index) {
		final int lastPos = mCurrentSelectedPos;
		mCurrentSelectedPos = index % mTabItems.length;
		if( mCurrentSelectedPos == lastPos ) {
			return;
		}
		boolean isFirstTabSelected = (mCurrentSelectedPos == 0);

		mTabItems[0].setSelected( isFirstTabSelected );
		mTabItems[1].setSelected( !isFirstTabSelected );

		mTabItems[0].setEnabled(!isFirstTabSelected);
		mTabItems[1].setEnabled(isFirstTabSelected);
		if(  lastPos != -1 ) {
			perfomTabSelected();
		}
	}

	public void perfomTabSelected() {
		if( mOnTabSelectedListener != null ) {
			mOnTabSelectedListener.onTabSelected(mCurrentSelectedPos);
		}
	}

	public void setOnTabSelectedListener(onTabSelectedListener l) {
		mOnTabSelectedListener = l;
	}

	public static interface onTabSelectedListener {
		public void onTabSelected( int index );
	}

	@Override
	public void onClick(View v) {
		setCurrentTab(v.getId() == R.id.tab_first ? 0 : 1);
	}
}
