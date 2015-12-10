package com.tonggou.andclient.myview;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.tonggou.andclient.R;

/**
 * 简单的标题栏（即 自定义 ActionBar）
 * 
 * @author lwz
 */
public class SimpleTitleBar extends FrameLayout {
	private ViewGroup mTitleBarContainer;	// 标题栏的整个布局
	private ImageButton mLeftButton;		// 标题栏左边的按钮，一般为返回键
	private Button mRightButton;			// 标题栏右边的按钮，该按钮是文字加背景的
	private ImageButton mRightImageButton;	// 标题栏右边的纯图片按钮
	private TextView mTitle;				// 标题栏的标题
	private ProgressBar mLoadingIndicator;	// 正在加载指示器
	
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
		mTitleBarContainer = (ViewGroup) View.inflate(getContext(), R.layout.widget_simple_titlebar, null);
		super.addView(mTitleBarContainer);
		mLeftButton = (ImageButton)mTitleBarContainer.findViewById(R.id.titlebar_left_btn);
		mRightButton = (Button)mTitleBarContainer.findViewById(R.id.titlebar_right_btn);
		mTitle = (TextView)mTitleBarContainer.findViewById(R.id.titlebar_title);
		mLoadingIndicator = (ProgressBar)mTitleBarContainer.findViewById(R.id.load_progress);
		
		mLeftButton.setVisibility(View.GONE);
		mRightButton.setVisibility(View.GONE);
		hideLoadingIndicator();
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
	 * 设置右边按钮文字
	 * @param textRes
	 * @return
	 */
	public SimpleTitleBar setRightButtonText(int textRes) {
		if( textRes > 0 ) mRightButton.setText(textRes);
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
	 * 设置右边图片按钮
	 * @param imageRes src      当设置为 0 时，不设置该图片
	 * @param backgroundRes 背景    当设置为 0 时，不设置该图片
	 * @return
	 */
	public SimpleTitleBar setRightImageButton(int imageRes, int backgroundRes) {
		mRightImageButton = new ImageButton(getContext());
		mRightButton.setVisibility(View.GONE);
		if( imageRes > 0 )  mRightImageButton.setImageResource(imageRes);
		if( backgroundRes > 0 )  mRightImageButton.setBackgroundResource(backgroundRes);
		mRightImageButton.setLayoutParams(mRightButton.getLayoutParams());
		mRightImageButton.setScaleType(ScaleType.FIT_CENTER);
		mTitleBarContainer.addView(mRightImageButton);
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
			}
		}
		return self();
	}
	
	/**
	 * 显示加载指示器
	 */
	public void showLoadingIndicator() {
		mLoadingIndicator.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 隐藏加载指示器
	 */
	public void hideLoadingIndicator() {
		mLoadingIndicator.setVisibility(View.GONE);
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
}
