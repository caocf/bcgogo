package com.tonggou.andclient.myview;

import java.util.Locale;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 所有字符都是大写字母的文本框
 * 
 * @author lwz
 *
 */
public class CapitalEditText extends EditText {

	public CapitalEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CapitalEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CapitalEditText(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		addTextChangedListener(null);
	}
	
	@Override
	public void addTextChangedListener(final TextWatcher watcher) {
		super.addTextChangedListener( new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if( watcher != null )
					watcher.onTextChanged(s, start, before, count);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if( watcher != null )
					watcher.beforeTextChanged(s, start, count, after);
			}
			
			@Override
			public void afterTextChanged(Editable et) {
				String s = et.toString();
			    if( !s.equals( s.toUpperCase( Locale.getDefault() ) ) ) {
			      s=s.toUpperCase( Locale.getDefault() );
			      setText(s);
			      setSelection(s.length()); // 设置光标位置
			    }
			    if( watcher != null )
			    	watcher.afterTextChanged(et);
			}
		} );
	}
}
