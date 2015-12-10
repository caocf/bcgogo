package com.tonggou.gsm.andclient.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;

/**
 * 带有指示器的 View
 * @author lwz
 */
public abstract class AbsIndicatorView extends LinearLayout {

	protected TextView mLeftTextIndiactor;
	private View mMainView;
	private int mIndicatorTextSize;
	private ArrayList<TextView> mRightIndicatorViews;
	
	public AbsIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public AbsIndicatorView(Context context) {
		super(context);
		init(null);
	}
	
	abstract View createMainView();

	@SuppressWarnings("deprecation")
	private void init(AttributeSet attrs) {
		setOrientation(LinearLayout.HORIZONTAL);
		mRightIndicatorViews = new ArrayList<TextView>();
		
		mMainView = createMainView();
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.weight = 1;
		addView(mMainView, lp);
		
		if( attrs != null ) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.IndicatorView); 
			
			mIndicatorTextSize = a.getDimensionPixelSize(R.styleable.IndicatorView_indicatorTextSize, 0);
			
			if( a.hasValue(R.styleable.IndicatorView_leftIndicatorText) ) {
				
				mLeftTextIndiactor = addIndicatorTextView(
						a.getString(R.styleable.IndicatorView_leftIndicatorText), 
						Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0);
			}
			
			if( mLeftTextIndiactor != null && a.hasValue(R.styleable.IndicatorView_leftIndicatorTextColor) ) {
				mLeftTextIndiactor.setTextColor( a.getColor(R.styleable.IndicatorView_leftIndicatorTextColor, Color.BLACK) );
			}
			
			setLeftIndicatorLength(a.getInteger(R.styleable.IndicatorView_leftIndicatorTextLength, 0));
			
			if( a.hasValue(R.styleable.IndicatorView_rightFirstIndicatorText) ) {
				TextView firstIndicatorText = addIndicatorTextView(
						a.getString(R.styleable.IndicatorView_rightFirstIndicatorText),
						Gravity.LEFT | Gravity.CENTER_VERTICAL, getChildCount());
				mRightIndicatorViews.add(firstIndicatorText);
			}
			
			if( a.hasValue(R.styleable.IndicatorView_rightSecondIndicatorText) ) {
				TextView secondIndicatorText = addIndicatorTextView(
						a.getString(R.styleable.IndicatorView_rightSecondIndicatorText),
						Gravity.LEFT | Gravity.CENTER_VERTICAL, getChildCount());
				mRightIndicatorViews.add(secondIndicatorText);
			}
			
			if( a.hasValue(R.styleable.IndicatorView_mainTextViewBackground) ) {
				mMainView.setBackgroundDrawable(a.getDrawable(R.styleable.IndicatorView_mainTextViewBackground) );
			}
			
			if ( mMainView instanceof TextView ) {	
				TextView mainTextView = (TextView)mMainView;
				if( a.hasValue(R.styleable.IndicatorView_mainTextViewTextColor)) {
					mainTextView.setTextColor( 
							a.getColor(R.styleable.IndicatorView_mainTextViewTextColor, Color.BLACK) );
				}
				
				if( a.hasValue(R.styleable.IndicatorView_mainTextViewMinLine)) {
					
					mainTextView.setSingleLine(false);
					mainTextView.setMinLines(a.getInt(R.styleable.IndicatorView_mainTextViewMinLine, 1));
					mainTextView.setGravity(Gravity.TOP | Gravity.LEFT);
					LayoutParams mainViewLP = (LayoutParams) mMainView.getLayoutParams();
					mainViewLP.gravity = Gravity.TOP;
					mainTextView.setLayoutParams(mainViewLP);
					
					LayoutParams leftIndicatorLp = (LayoutParams) mLeftTextIndiactor.getLayoutParams();
					leftIndicatorLp.gravity = Gravity.TOP | Gravity.LEFT;
					mLeftTextIndiactor.setLayoutParams(leftIndicatorLp);
				}
				
				if( a.hasValue(R.styleable.IndicatorView_mainTextViewHint) ) {
					mainTextView.setHint( a.getString(R.styleable.IndicatorView_mainTextViewHint) );
				}
				
				if( a.hasValue(R.styleable.IndicatorView_mainTextViewHintTextColor) ) {
					mainTextView.setHintTextColor( ColorStateList.valueOf(
							a.getColor(R.styleable.IndicatorView_mainTextViewHintTextColor, Color.GRAY)));
				}
				
				if( a.hasValue(R.styleable.IndicatorView_mainTextViewInputType) ) {
					mainTextView.setInputType( 
							a.getInt(R.styleable.IndicatorView_mainTextViewInputType, EditorInfo.TYPE_NULL) );
				}
				if( a.hasValue(R.styleable.IndicatorView_mainTextViewMaxLength) ) {
					mainTextView.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							a.getInt(R.styleable.IndicatorView_mainTextViewMaxLength, -1)) });
				}
			}
			
			a.recycle();
		}
		
	}
	
	public ArrayList<TextView> getRightIndicators() {
		return mRightIndicatorViews;
	}
	
	public TextView getLeftIndicator() {
		return mLeftTextIndiactor;
	}
	
	private TextView addIndicatorTextView(String text, int grivty, int pos) {
		TextView indicatorView = new TextView(getContext());
		indicatorView.setGravity(grivty);
		indicatorView.setText(text);
		setTextViewCommonAttributes(indicatorView);
		addView(indicatorView, pos);
		return indicatorView;
	}
	
	/**
	 * 设置左边指示文本的宽度
	 * @param length
	 */
	public void setLeftIndicatorLength(int length) {
		if( length <= 0 ) {
			return;
		}
		if( mLeftTextIndiactor == null ) {
			mLeftTextIndiactor = addIndicatorTextView("", Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0);
		}
		mLeftTextIndiactor.setWidth( (int)(mLeftTextIndiactor.getTextSize() * length) );
	}
	
	
	/**
	 * 设置指示文本框的内容
	 * @param leftIndiactorText
	 * @param rightIndicatorsText
	 */
	public void setIndicatorTextValues(CharSequence leftIndiactorText, CharSequence...rightIndicatorsText ) {
		if( mLeftTextIndiactor == null ) {
			mLeftTextIndiactor = addIndicatorTextView(leftIndiactorText.toString()
					, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0);
		}
		mLeftTextIndiactor.setText(leftIndiactorText);
		
		if( rightIndicatorsText!= null && rightIndicatorsText.length > 0 ) {
			final int SIZE = rightIndicatorsText.length;
			final int rightIndicatorTextSize = mRightIndicatorViews.size();
			if( rightIndicatorTextSize < SIZE ) {
				for(int i=0; i<SIZE - rightIndicatorTextSize; i++) {
					TextView tv = addIndicatorTextView("", Gravity.LEFT | Gravity.CENTER_VERTICAL, getChildCount());
					mRightIndicatorViews.add(tv);
				} 
			}
			for( int i=0; i<SIZE; i++ ) {
				mRightIndicatorViews.get(i).setText(rightIndicatorsText[i]);
			}
		}
	}
	
	/**
	 * 设置指示文本框的内容
	 * @param leftIndiactorTextRes
	 * @param rightIndicatorsSTextRes
	 */
	public void setIndicatorTextValues(int leftIndicatorTextRes, int... rightIndicatorsTextRes) {
		Resources res = getResources();
		final int SIZE = rightIndicatorsTextRes.length;
		CharSequence[] rightIndicatorsText = new CharSequence[SIZE];
		for( int i=0; i<SIZE; i++ ) {
			rightIndicatorsText[i] = res.getString(rightIndicatorsTextRes[i]);
		}
		setIndicatorTextValues(res.getString(leftIndicatorTextRes), rightIndicatorsText);
	}
	
	/**
	 * 设置图片指示器
	 * @param res
	 * @param l
	 */
	public void setImageIndicator(int res, View.OnClickListener l) {
		ImageButton imgBtn = new ImageButton(getContext());
		imgBtn.setImageResource(res);
		imgBtn.setBackgroundColor(Color.TRANSPARENT);
		imgBtn.setScaleType(ScaleType.FIT_CENTER);
		imgBtn.setAdjustViewBounds(true);
		setClickableIndicator(imgBtn, l);
		if( mMainView instanceof TextView ) {
			mMainView.setOnClickListener( l );
		}
	}
	
	/**
	 * 设置可点击的指示 View
	 * @param view
	 * @param l
	 */
	public void setClickableIndicator(View view, View.OnClickListener l) {
		view.setOnClickListener(l);
		addView(view, 2);
	}
	
	/**
	 * 设置通用属性
	 * @param textView
	 */
	protected void setTextViewCommonAttributes(TextView textView) {
		textView.setSingleLine(true);
		if( mIndicatorTextSize <= 0 ) {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		} else {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mIndicatorTextSize);
		}
		textView.setTextColor(Color.BLACK);
		textView.setEllipsize(TruncateAt.END);
	}
}
