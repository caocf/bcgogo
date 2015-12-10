package com.tonggou.gsm.andclient.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;

/**
 * 简单的标题栏（即 自定义 ActionBar）
 *
 * @author lwz
 */
public class SimpleTitleBar extends FrameLayout {
	private LinearLayout mTitleBarRightContainer; // 标题栏右边布局
	private ImageButton mLeftButton;		// 标题栏左边的按钮，一般为返回键
	private Button mRightButton;			// 标题栏右边的按钮，该按钮是文字加背景的
	private ImageButton mRightImageButton;	// 标题栏右边的纯图片按钮
	private Button mRightSecondButton;		// 标题栏右边按钮
	private ImageButton mRightSecondImageButton; // 标题栏右边第二个图片按钮
	private TextView mTitle;				// 标题栏的标题
	private View mLeftIndicator;			// 左边指示器
	private View mRightIndicator;			// 右边指示器

	public SimpleTitleBar(Context context) {
		super(context);
		init();
	}

	public SimpleTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SimpleTitleBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		ViewGroup rootView = (ViewGroup) View.inflate(getContext(), R.layout.widget_simple_titlebar, null);
		super.addView(rootView);
		mTitleBarRightContainer = (LinearLayout) rootView.findViewById(R.id.titlebar_right_container);
		mLeftButton = (ImageButton)rootView.findViewById(R.id.titlebar_left_btn);
		mRightButton = (Button)rootView.findViewById(R.id.titlebar_right_btn);
		mRightSecondButton  = (Button)rootView.findViewById(R.id.titlebar_right_second_btn);
		mTitle = (TextView)rootView.findViewById(R.id.titlebar_title);
		mLeftIndicator = rootView.findViewById(R.id.left_indicator);
		mRightIndicator = rootView.findViewById(R.id.right_indicator);
		mLeftButton.setVisibility(View.GONE);
		mRightButton.setVisibility(View.GONE);
		mRightSecondButton.setVisibility(View.GONE);
		hideIndicator(true, true);
	}

	/**
	 * 这个对象自己
	 * @return
	 */
	private SimpleTitleBar self() {
		return this;
	}

	/**
	 * 设置标题文字
	 * @param titleRes
	 * @return
	 */
	public SimpleTitleBar setTitle(int titleRes) {
		if( titleRes >0 ) mTitle.setText(titleRes);
		return self();
	}

	/**
	 * 设置标题文字
	 * @param title 字符串
	 * @return
	 */
	public SimpleTitleBar setTitle(String title) {
		setTitle(title, Color.WHITE);
		return self();
	}

	/**
	 * 设置标题文字以及颜色
	 * @param title
	 * @param color
	 * @return
	 */
	public SimpleTitleBar setTitle( String title, int color ){
		if( ! TextUtils.isEmpty(title) ) {
			mTitle.setText(title);
		}
		mTitle.setTextColor(color);
		return self();
	}

	/**
	 * 设置标题文字以及颜色
	 * @param titleRes
	 * @param color
	 * @return
	 */
	public SimpleTitleBar setTitle( int titleRes, int color ){
		setTitle(titleRes);
		mTitle.setTextColor(color);
		return self();
	}

	/**
	 * 设置左边按钮显示的图片
	 * @param backgroundRes 背景资源图片
	 * @return
	 */
	public SimpleTitleBar setLeftButton(int backgroundRes) {
		if( backgroundRes > 0 ) {
			mLeftButton.setImageResource(backgroundRes);
		}
		return self();
	}

	/**
	 * 设置右边按钮显示的背景图片
	 * @param backgroundRes 背景资源图片
	 * @return
	 */
	public SimpleTitleBar setRightButton( int backgroundRes) {
		if( backgroundRes > 0 ) {
			mRightButton.setBackgroundResource(backgroundRes);
		}

		return self();
	}

	/**
	 * 设置右边第二个按钮的背景图片
	 * @param backgroundRes 背景资源图片
	 * @return
	 */
	public SimpleTitleBar setRightSecondButton( int backgroundRes ) {
		if( backgroundRes > 0 ) {
			mRightSecondButton.setBackgroundResource(backgroundRes);
		}

		return self();
	}

	/**
	 * 设置右边按钮文字
	 * @param textRes
	 * @return
	 */
	public SimpleTitleBar setRightButtonText(int textRes) {
		if( textRes > 0 ) mRightButton.setText(textRes);
		return self();
	}

	/**
	 * 设置右边第二个按钮文字
	 * @param textRes
	 * @return
	 */
	public SimpleTitleBar setRightSecondButtonText(int textRes) {
		if( textRes > 0 ) mRightSecondButton.setText(textRes);
		return self();
	}

	/**
	 * 设置右边按钮显示的文字
	 * @param text 按钮显示的文字
	 * @return
	 */
	public SimpleTitleBar setRightButtonText( String text) {
		if( ! TextUtils.isEmpty(text) ) {
			mRightButton.setText(text);
		}
		return self();
	}

	/**
	 * 设置右边第二个按钮显示的文字
	 * @param text 按钮显示的文字
	 * @return
	 */
	public SimpleTitleBar setRightSecondButtonText( String text) {
		if( ! TextUtils.isEmpty(text) ) {
			mRightSecondButton.setText(text);
		}
		return self();
	}

	/**
	 * 设置右边按钮显示的文字及背景图片
	 * @param backgroundRes 背景资源图片
	 * @return
	 */
	public SimpleTitleBar setRightButton(String text, int backgroundRes) {
		setRightButtonText(text);
		setRightButton(backgroundRes);
		return self();
	}

	/**
	 * 设置右边第二个按钮显示的文字及背景图片
	 * @param backgroundRes 背景资源图片
	 * @return
	 */
	public SimpleTitleBar setRightSecondButton(String text, int backgroundRes) {
		setRightSecondButtonText(text);
		setRightSecondButton(backgroundRes);
		return self();
	}

	/**
	 * 设置右边图片按钮
	 * @param imageRes src	    当设置为 0 时，不设置该图片
	 * @param backgroundRes 背景    当设置为 0 时，不设置该图片
	 * @return
	 */
	public SimpleTitleBar setRightImageButton(int imageRes, int backgroundRes) {
		mRightImageButton = new ImageButton(getContext());
		mRightImageButton.setAdjustViewBounds(true);
		mRightButton.setVisibility(View.GONE);
		if( imageRes > 0 )  mRightImageButton.setImageResource(imageRes);
		if( backgroundRes > 0 )	 mRightImageButton.setBackgroundResource(backgroundRes);
		android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mRightButton.getLayoutParams();
		lp.gravity = Gravity.RIGHT;
		mRightImageButton.setLayoutParams(lp);
		mRightImageButton.setPadding(0, 0, 0, 0);
		mRightImageButton.setScaleType(ScaleType.FIT_CENTER);
		mRightImageButton.setPadding(
				mRightImageButton.getPaddingLeft(), 0, mRightImageButton.getPaddingBottom() , 0);
		mTitleBarRightContainer.addView(mRightImageButton);
		return self();
	}

	/**
	 * 设置右边第二个图片按钮
	 * @param imageRes src	    当设置为 0 时，不设置该图片
	 * @param backgroundRes 背景    当设置为 0 时，不设置该图片
	 * @return
	 */
	public SimpleTitleBar setRightSecondImageButton(int imageRes, int backgroundRes) {
		mRightSecondImageButton = new ImageButton(getContext());
		mRightSecondImageButton.setAdjustViewBounds(true);
		mRightSecondButton.setVisibility(View.GONE);
		if( imageRes > 0 )  mRightSecondImageButton.setImageResource(imageRes);
		if( backgroundRes > 0 )	 mRightSecondImageButton.setBackgroundResource(backgroundRes);
		mRightSecondImageButton.setLayoutParams(mRightSecondButton.getLayoutParams());
		mRightSecondImageButton.setPadding(
				mRightSecondImageButton.getPaddingLeft(), 0, mRightSecondImageButton.getPaddingBottom() , 0);
		mRightSecondImageButton.setScaleType(ScaleType.FIT_CENTER);
		mTitleBarRightContainer.addView(mRightSecondImageButton, 1);
		return self();
	}

	/**
	 * 左边按钮点击监听
	 * @param l
	 * @return
	 */
	public SimpleTitleBar setOnLeftButtonClickListener(View.OnClickListener l){
		if( l != null ) {
			mLeftButton.setOnClickListener(l);
			mLeftButton.setVisibility(View.VISIBLE);
		}

		return self();
	}

	/**
	 * 右边边按钮点击监听
	 * @param l
	 * @return
	 */
	public SimpleTitleBar setOnRightButtonClickListener(View.OnClickListener l){
		if( l != null ) {
			if( mRightImageButton == null ) {
				mRightButton.setOnClickListener(l);
				mRightButton.setVisibility(View.VISIBLE);
			} else {
				mRightImageButton.setOnClickListener(l);
				mRightImageButton.setVisibility(View.VISIBLE);
			}
		} else {
			if( mRightImageButton == null ) {
				mRightButton.setOnClickListener(l);
				mRightButton.setVisibility(View.GONE);
			} else {
				mRightImageButton.setOnClickListener(l);
				mRightImageButton.setVisibility(View.GONE);
			}
		}
		return self();
	}

	/**
	 * 右边第二个按钮点击监听
	 * @param l
	 * @return
	 */
	public SimpleTitleBar setOnRightSecondButtonClickListener(View.OnClickListener l){
		if( l != null ) {
			if( mRightSecondImageButton == null ) {
				mRightSecondButton.setOnClickListener(l);
				mRightSecondButton.setVisibility(View.VISIBLE);
			} else {
				mRightSecondImageButton.setOnClickListener(l);
			}
		}
		return self();
	}

	/**
	 * 隐藏左边按钮
	 */
	public void hideLeftButton() {
		mLeftButton.setVisibility(View.GONE);
	}

	/**
	 * 隐藏右边按钮
	 */
	public void hideRightButton() {
		mRightButton.setVisibility(View.GONE);
		mRightImageButton.setVisibility(View.GONE);
	}

	/**
	 * 显示左右指示器
	 * <p>NOTE: 若为 false, 则保持原状态，不会变为 {@link View #INVISIBLE} 或 {@link View #GONE}
	 * @param showLeft
	 * @param showRight
	 */
	public void showIndicator(boolean showLeft, boolean showRight) {
		if( showLeft )
			mLeftIndicator.setVisibility(View.VISIBLE);
		if( showRight )
			mRightIndicator.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏左右指示器
	 * <p>NOTE: 若为 false, 则保持原状态，不会变为 {@link View #VISIBLE}
	 * @param showLeft
	 * @param showRight
	 */
	public void hideIndicator(boolean hideLeft, boolean hideRight) {
		if( hideLeft )
			mLeftIndicator.setVisibility(View.GONE);
		if( hideRight )
			mRightIndicator.setVisibility(View.GONE);
	}

	public Button getRightButton() {
		return mRightButton;
	}
}