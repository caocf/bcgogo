package com.tonggou.yf.andclient.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tonggou.lib.widget.AbsEmptyViewAdapter;
import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.widget.view.IndicatorTextView;

public abstract class AbsTodoAdapter<T> extends AbsEmptyViewAdapter<T> {

	private boolean isHandleBtnlock = false;
	
	public AbsTodoAdapter(Context context, int layoutRes) {
		super(context, layoutRes);
	}
	
	@Override
	public int getNoDataLayoutRes() {
		return R.layout.widget_no_data;
	}

	@Override
	public void bindData(int pos, T itemData) {
		bindHandledListener(getHandledBtnId(), pos);
		bindHandledListener(getHandledLeftBtnId(), pos);
		bindHandledListener(getHandledRightBtnId(), pos);
	}
	
	public void bindText(int viewId, String text) {
		View view = getViewFromHolder(viewId);
		if( view instanceof TextView ) {
			((TextView)view).setText(text);
		} else if( view instanceof IndicatorTextView ){
			((IndicatorTextView)view).setTextValue(text);
		}
	}
	
	public void bindHandledListener(int handledViewId, int pos) {
		if( handledViewId == 0 ) {
			return;
		}
		View handledView = getViewFromHolder(handledViewId);
		if( ! (handledView.getTag() instanceof AbsTodoAdapter.HandledClickListener) ) {
			HandledClickListener listener = new HandledClickListener();
			handledView.setTag(listener);
		}
		handledView.setOnClickListener(
				((AbsTodoAdapter<?>.HandledClickListener)handledView.getTag()).bindPosition(pos));
	}
	
	public abstract int getHandledBtnId();
	
	public abstract int getHandledLeftBtnId();
	
	public abstract int getHandledRightBtnId();
	
	public abstract void onHandledBtnClick(final int pos);
	
	public abstract void onHandledLeftBtnClick(final int pos);
	
	public abstract void onHandledRightBtnClick(final int pos);
	
	class HandledClickListener implements View.OnClickListener {

		private int pos;
		
		public HandledClickListener bindPosition(int pos) {
			this.pos = pos;
			return this;
		}
		
		@Override
		public void onClick(View v) {
			if( isHandleBtnLocked() ) {
				return;
			}
			isHandleBtnlock = true;
			int viewId = v.getId();
			if( viewId == getHandledLeftBtnId() ) {
				onHandledLeftBtnClick(pos);
				return;
			}
			if( viewId == getHandledRightBtnId() ) {
				onHandledRightBtnClick(pos);
				return;
			}
			if( viewId == getHandledBtnId() ) {
				onHandledBtnClick(pos);
				return;
			}
		}
		
	}
	
	public synchronized void releaseHandleBtnlock() {
		isHandleBtnlock = false;
	}
	
	private synchronized boolean isHandleBtnLocked() {
		return isHandleBtnlock;
	}
}
