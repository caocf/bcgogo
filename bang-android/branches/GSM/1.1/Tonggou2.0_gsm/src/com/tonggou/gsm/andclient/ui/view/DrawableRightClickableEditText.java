package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class DrawableRightClickableEditText extends EditText {
	
	private Drawable drawableRight;

    int actionX, actionY;

    private DrawableRightClickListener clickListener;


	public DrawableRightClickableEditText(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public DrawableRightClickableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DrawableRightClickableEditText(Context context) {
		super(context);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public void setCompoundDrawables(Drawable left, Drawable top,
			Drawable right, Drawable bottom) {
		if (right != null) {
			drawableRight = right;
		}
		super.setCompoundDrawables(left, top, right, bottom);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Rect bounds;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			actionX = (int) event.getX();
			actionY = (int) event.getY();
			if (drawableRight != null) {
				bounds = null;
				bounds = drawableRight.getBounds();

				int x, y;
				int extraTapArea = 13;

				/**
				 * IF USER CLICKS JUST OUT SIDE THE RECTANGLE OF THE DRAWABLE
				 * THAN ADD X AND SUBTRACT THE Y WITH SOME VALUE SO THAT AFTER
				 * CALCULATING X AND Y CO-ORDINATE LIES INTO THE DRAWBABLE
				 * BOUND. - this process help to increase the tappable area of
				 * the rectangle.
				 */
				x = (int) (actionX + extraTapArea);
				y = (int) (actionY - extraTapArea);

				/**
				 * Since this is right drawable subtract the value of x from the
				 * width of view. so that width - tappedarea will result in x
				 * co-ordinate in drawable bound.
				 */
				x = getWidth() - x;

				/*
				 * x can be negative if user taps at x co-ordinate just near the
				 * width. e.g views width = 300 and user taps 290. Then as per
				 * previous calculation 290 + 13 = 303. So subtract X from
				 * getWidth() will result in negative value. So to avoid this
				 * add the value previous added when x goes negative.
				 */

				if (x <= 0) {
					x += extraTapArea;
				}

				/*
				 * If result after calculating for extra tappable area is
				 * negative. assign the original value so that after subtracting
				 * extratapping area value doesn't go into negative value.
				 */

				if (y <= 0)
					y = actionY;

				/**
				 * If drawble bounds contains the x and y points then move
				 * ahead.
				 */
				if (bounds.contains(x, y) && clickListener != null) {
					clickListener.onClick();
					event.setAction(MotionEvent.ACTION_CANCEL);
					return false;
				}
				return super.onTouchEvent(event);
			}

		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void finalize() throws Throwable {
		drawableRight = null;
		super.finalize();
	}

	public void setOnDrawableRightClickListener(DrawableRightClickListener listener) {
		this.clickListener = listener;
	}

	public static interface DrawableRightClickListener {

		public void onClick();
	}

}
