package com.tonggou.yf.andclient.ui;

import android.os.Bundle;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;

import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.widget.view.SimpleTitleBar;

/**
 * 标题栏 Activity
 * @author lwz
 *
 */
public class SimpleTitleBarActivity extends BaseActivity {
	
	private SimpleTitleBar mTitleBar;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		this.setContentView(getLayoutInflater().inflate(layoutResID, null));
	}
	
	@Override
	public void setContentView(View view) {
		this.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	@Override
	public void setContentView(View view, LayoutParams params) {
		if( view instanceof LinearLayout ) {
			mTitleBar = new SimpleTitleBar(this);
			LinearLayout rootView = (LinearLayout)view;
			rootView.addView( mTitleBar, 0, 
					new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT, 
							getResources().getDimensionPixelSize(R.dimen.titlebar_default_height)));
			onTitleBarCreated(mTitleBar);
		} else {
			new InflateException("父布局应该为 LinearLayout");
		}
		super.setContentView(view, params);
	}
	
	/**
	 * 标题栏创建好后的回调
	 */
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		
	}
	
	public SimpleTitleBar getTitleBar() {
		return mTitleBar;
	}
	
}
