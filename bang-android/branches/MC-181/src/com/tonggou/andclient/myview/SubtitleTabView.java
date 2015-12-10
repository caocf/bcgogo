package com.tonggou.andclient.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.tonggou.andclient.R;

public class SubtitleTabView extends FrameLayout {
	
	private Button mFirstTab;
	private Button mSecondTab;

	public SubtitleTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SubtitleTabView(Context context) {
		super(context);
		init();
	}

	public SubtitleTabView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		View parentView = View.inflate(getContext(), R.layout.widget_subtitle_tab, null);
		addView(parentView);
		mFirstTab = (Button) parentView.findViewById(R.id.first_tab_btn);
		mSecondTab = (Button) parentView.findViewById(R.id.second_tab_btn);
		mFirstTab.setSelected(true);
		mSecondTab.setSelected(false);
		// ≥ı ºªØ
		setOnFirstTabClickListener(null);
		setOnSecondTabClickListener(null);
	}
	
	public void setTabText(CharSequence firstTab, CharSequence secondTab) {
		mFirstTab.setText(firstTab);
		mSecondTab.setText(secondTab);
	}
	
	public void setTabText(int firstTabTextRes, int secondTabTextRes) {
		setTabText( getResources().getText(firstTabTextRes) , getResources().getText(secondTabTextRes));
	}
	
	public void setOnFirstTabClickListener(final View.OnClickListener l) {
		mFirstTab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( mFirstTab.isSelected() ) {
					return;
				}
				mFirstTab.setSelected(true);
				mSecondTab.setSelected(false);
				if( l != null ) {
					l.onClick(v);
				}
			}
		});
	}
	
	public void setOnSecondTabClickListener(final View.OnClickListener l) {
		mSecondTab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( mSecondTab.isSelected() ) {
					return;
				}
				mFirstTab.setSelected(false);
				mSecondTab.setSelected(true);
				if( l != null ) {
					l.onClick(v);
				}
			}
		});
	}
	
}
