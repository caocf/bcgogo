package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.Button;

import com.tonggou.gsm.andclient.util.HandlerTimer;

/**
 * 计时器 按钮
 * <p>防止在一定时间间隔内重复点击的按钮.如果要防止快速点击造成的逻辑问题，那么可以使用该Button用来代替原生的 Button。
 * 
 * @author lwz
 *
 */
public class TimerButton extends Button implements HandlerTimer.OnHandleTimerListener {
	
	private final int TIMER_TOKEN = 0x1;
	private final int DEFAULT_ALLOW_CLICK_INTERVAL = 3 * 1000;	// 默认点击间隔
	private HandlerTimer mRepeatetimer;
	private int mAllowClickInterval = DEFAULT_ALLOW_CLICK_INTERVAL;
	
	public TimerButton(Context context) {
		super(context);
		init();
	}
	
	public TimerButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TimerButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		mRepeatetimer = new HandlerTimer(TIMER_TOKEN, this);
	}
	
	/**
	 * 设置每次点击过后，再次允许点击的时长
 	 * @param millisecond 每次点击过后，再次允许点击的时长， 如果 值 millisecond<=0 那么计时器不起作用
	 */
	public void setAllowClickInterval( int millisecond ) {
		mAllowClickInterval = millisecond;
	}

	@Override
	public void onHandleTimerMessage(int token, Message msg) {
		if( token == TIMER_TOKEN ) {
			if( !isEnabled() ) {
				setEnabled(true);
			}
			mRepeatetimer.stop();
		}
	}
	
	@Override
	public boolean performClick() {
		if( mAllowClickInterval >0 ) {
			setEnabled(false);
			mRepeatetimer.start(mAllowClickInterval, 0);
		}
		return super.performClick();
	}
	
	/**
	 * 停掉计时器，设置可使用
	 */
	public void setEnabledForever() {
		mRepeatetimer.stop();
		setEnabled(true);
	}
	
	/**
	 * 停掉计时器，设置不可使用
	 */
	public void setDisabledForever() {
		mRepeatetimer.stop();
		setEnabled(false);
	}
	
}
