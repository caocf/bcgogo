package com.tonggou.yf.andclient.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tonggou.yf.andclient.R;

public class SegmentView extends SegmentedRadioGroup implements RadioGroup.OnCheckedChangeListener {
	
	public static interface OnCheckedChangeListener {
		public void onCheckedChange(int pos);
	}
	
	private final int RADIO_ID_OFFSET = 0xFF;
	private CharSequence[] mEntries = new CharSequence[]{};
	private LayoutInflater mInflater;
	private OnCheckedChangeListener mCheckedChangeListener;
	
	public SegmentView(Context context) {
		this(context, null);
	}
	
	public SegmentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		if( attrs != null ) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SegmentedView);
			if( a.hasValue(R.styleable.SegmentedView_entries) ) {
				mEntries = a.getTextArray(R.styleable.SegmentedView_entries);
			}
			a.recycle();
		}
		
		mInflater = LayoutInflater.from(context);
		init();
	}

	private void init() {
		removeAllViews();
		setOrientation(HORIZONTAL);
		for( int i=0; i<mEntries.length; i++ ) {
			addView(createItemView(mEntries[i], i));
		}
		changeButtonsStyle();
		setCurrentCheckedItem(0);
		super.setOnCheckedChangeListener(this);
	}

	private View createItemView(CharSequence text, int index) {
		RadioButton button = (RadioButton) mInflater.inflate(R.layout.widget_segment_item, this, false);
		button.setId(RADIO_ID_OFFSET + index);
		button.setText(text);
		return button;
	}
	
	/**
	 * 设置 item 标题
	 * @param entries
	 */
	public void setEntries(CharSequence[] entries) {
		mEntries = entries;
		init();
	}
	
	/**
	 * 设置 item 选中改变监听
	 * @param l
	 */
	public void setOnCheckedChangedListener(OnCheckedChangeListener l) {
		mCheckedChangeListener = l;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if( mCheckedChangeListener != null ) {
			mCheckedChangeListener.onCheckedChange(checkedId - RADIO_ID_OFFSET);
		}
	}
	
	public void setCurrentCheckedItem(int pos) {
		if( mRadioButtonIndexList.isEmpty() ) {
			return;
		}
		int index = mRadioButtonIndexList.get(pos % mRadioButtonIndexList.size());
		((RadioButton)getChildAt(index)).setChecked(true);
	}
	
}
