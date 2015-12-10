package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import com.tonggou.gsm.andclient.R;

/**
 * 带有指示器的 EidtText
 * @author lwz
 *
 */
public class IndicatorEditText extends AbsIndicatorView {
	
	private EditText mEditText;

	public IndicatorEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IndicatorEditText(Context context) {
		super(context);
	}
	
	@Override
	View createMainView() {
		mEditText = new EditText(getContext());
		mEditText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		mEditText.setBackgroundColor(Color.TRANSPARENT);
		final int padding = getResources().getDimensionPixelSize(R.dimen.dimen_5dp);
		mEditText.setPadding(padding, 0, padding, 0);
		setTextViewCommonAttributes(mEditText);
		return mEditText;
	}
	
	/**
	 * 设置 EditText 是否可以被编辑
	 * @param writable
	 */
	public void setWritable(boolean writable) {
		mEditText.setEnabled(writable);
		if(!writable) {
			mEditText.clearFocus();
		}
	}
	
	/**
	 * 设置 EditText 的内容
	 * @param value
	 */
	public void setEditTextValue(CharSequence value) {
		mEditText.setText(value);
		if( !TextUtils.isEmpty(value) ) {
			try{
				mEditText.setSelection(value.length());
			} catch (IndexOutOfBoundsException e) {
				// do-nothing
			}
		}
	}
	
	/**
	 * 设置 EditText 的内容，并设置是否可以更改
	 * @param value
	 * @param writable
	 */
	public void setEditTextValue(CharSequence value, boolean writable) {
		setEditTextValue(value);
		setWritable(writable);
	}
	
	public void setEditTextMinLines(int lines) {
		mEditText.setSingleLine(false);
		mEditText.setMinLines(5);
		mEditText.setGravity(Gravity.TOP | Gravity.LEFT);
		
		LayoutParams lp = (LayoutParams) mLeftTextIndiactor.getLayoutParams();
		lp.gravity = Gravity.TOP;
		mLeftTextIndiactor.setLayoutParams(lp);
	}
	
	/**
	 * 得到 EditText, 以便设置其他属性
	 * @return
	 */
	public EditText getEditText() {
		return mEditText;
	}
	
	/**
	 * 得到 EditText 的内容
	 * @return
	 */
	public String getEditTextValue() {
		return mEditText.getText().toString().trim();
	}
	
	public void setEditTextMaxLength(int length) {
		mEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(length) });
	}
}
