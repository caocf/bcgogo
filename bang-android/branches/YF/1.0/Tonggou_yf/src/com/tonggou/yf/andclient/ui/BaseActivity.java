package com.tonggou.yf.andclient.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.tonggou.lib.net.HttpRequestClient;
import com.tonggou.yf.andclient.App;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

public class BaseActivity extends FragmentActivity {

	public final String TAG = getClass().getSimpleName();
	
	private ProgressDialog mLoadingDialog;
	
	/**
	 * 从 Intent 中取出存储的值。{@link #restoreExtras(Bundle)}
	 * @param intent
	 * @return
	 */
	protected boolean restoreExtras(final Intent intent) {
		if( intent == null ) {
			return false;
		}
		return restoreExtras(intent.getExtras());
	}
	
	/**
	 * 取得 bundle 中存储的值.
	 * <p>NOTE : 若要使用该方法，应该在子类中覆写此方法
	 * @param extra 
	 * @return true 成功取值  | false 取值不成功
	 */
	protected boolean restoreExtras(final Bundle extra) {
		return false;
	}
	
	/**
	 * bundle 是否为空
	 * @param extra
	 * @return true 为空 | false 不成功
	 */
	protected boolean isBundleEmpty(Bundle extra) {
		return extra == null || extra.isEmpty();
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		PushAgent.getInstance(this).onAppStart();
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart(getClass().getName());
	    MobclickAgent.onResume(this);
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd(getClass().getName()); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
	    MobclickAgent.onPause(this);
	}
	
	public void showLoadingDialog() {
		showLoadingDialog(0);
	}
	
	public void showLoadingDialog(int msgRes) {
		dismissLoadingDialog();
		if( isFinishing() ) {
			return;
		}
		mLoadingDialog = ProgressDialog.show(this, null, msgRes > 0 ? getString(msgRes) : null );
		mLoadingDialog.setCanceledOnTouchOutside(false);
	}
	
	public void dismissLoadingDialog() {
		if( mLoadingDialog != null && mLoadingDialog.isShowing() ) {
			mLoadingDialog.dismiss();
		}
		mLoadingDialog = null;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.debug("Activity finish", TAG);
		dismissLoadingDialog();
		HttpRequestClient.cancelRequest(this, true);
	}
	
	/**
	 * 验证 TextView 中的内容是否为空，若为空就弹出 Toast，内容为  errorStringRes
	 * @param textView
	 * @param errorStringRes
	 * @return true 验证未通过，false 验证通过
	 */
	protected boolean isTextEmpty(TextView textView, int errorStringRes) {
		boolean isEmpty = isTextEmpty(textView);
		if( isEmpty ) 
			App.showShortToast(getString(errorStringRes));
		return isEmpty;
	}
	
	/**
	 * 验证 TextView 中的内容是否为空
	 * @param textView
	 * @return true 验证未通过，false 验证通过
	 */
	protected boolean isTextEmpty(TextView textView) {
		return TextUtils.isEmpty(getText(textView));
	}
	
	protected String getText(TextView textView) {
		return textView.getText().toString().trim();
	}
	
}
