package com.tonggou.yf.andclient.ui;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.tonggou.lib.net.HttpRequestClient;
import com.tonggou.lib.util.PreferenceUtil;
import com.tonggou.yf.andclient.App;
import com.tonggou.yf.andclient.Constants;
import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.bean.UserScope;
import com.tonggou.yf.andclient.net.TonggouResponseParseHandler;
import com.tonggou.yf.andclient.net.request.LoginRequest;
import com.tonggou.yf.andclient.net.response.LoginResponse;
import com.tonggou.yf.andclient.util.StringUtil;
import com.tonggou.yf.andclient.util.UserAccountManager;

@EActivity(R.layout.activity_login)
@WindowFeature({Window.FEATURE_NO_TITLE})
public class LoginActivity extends BaseActivity {
	
	Dialog mLoginExpressDialog;
	
	public static void logout(Context context) {
		logout(context, false);
	}
	
	private static void logout(Context context, boolean isLoginExpire) {
		HttpRequestClient.removeUserToken(context);
		LoginActivity_.intent(context).mIsLoginExpire(isLoginExpire)
			.flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();
	}
	
	public static void loginExpire() {
		logout(App.getInstance(), true);
	}
	
	@ViewById(R.id.phone_no_edit) EditText mPhoneNoEdit;
	@ViewById(R.id.pwd_edit) EditText mPasswordEdit;
	@ViewById(R.id.remeber_pwd_toggle_btn) ToggleButton mRememberPwdToggleBtn;
	@Extra boolean mIsLoginExpire;
	
	@AfterInject
	void autoLogin() {
		LoginResponse response = restoreUserLoginInfo();
		if( response != null && HttpRequestClient.hasUserToken(this)) {
			onLoginSuccess(response.getUserScope());
			return;
		}
	}
	
	@AfterViews
	void afterViews() {
		mPhoneNoEdit.setHintTextColor(Color.WHITE);
		mPasswordEdit.setHintTextColor(Color.WHITE);
		
		mPhoneNoEdit.setText(UserAccountManager.restoreUserNo(this));
		mPasswordEdit.setText(UserAccountManager.restoreUserPwd(this));
		initRememberPwdToggleBtnStatus();
		
		if( mIsLoginExpire ) {
			showExpireDialog();
		}
	}
	
	private void initRememberPwdToggleBtnStatus() {
		mRememberPwdToggleBtn.setChecked(
				PreferenceUtil.getBoolean(this, Constants.PREF.NAME_LONGIN_INFO,
							Constants.PREF.KEY_LONGIN_INFO_IS_REMEMBER_PWD));
	}
	
	private void showExpireDialog() {
		dismissExpireDialog();
		mLoginExpressDialog = new AlertDialog.Builder(this)
			.setTitle(R.string.dialog_title_login_express)
			.setMessage(R.string.dialog_msg_login_express)
			.setPositiveButton(R.string.dialog_btn_comfirm, null)
			.create();
		if( !isFinishing() ) {
			mLoginExpressDialog.show();
		}
	}
	
	private void dismissExpireDialog() {
		if( mLoginExpressDialog != null && mLoginExpressDialog.isShowing() ) {
			mLoginExpressDialog.dismiss();
		}
		mLoginExpressDialog = null;
	}
	
	public void onLoginBtnClick(View view) {
		if( !validateInputValue() ) {
			return;
		}
		
		doLoginRequest(getText(mPhoneNoEdit), getText(mPasswordEdit));
	}
	
	private boolean validateInputValue() {
		if( TextUtils.isEmpty(getText(mPhoneNoEdit)) ) {
			App.showShortToast(getString(R.string.info_please_input_phone_no));
			return false;
		}
		if( !StringUtil.validatePhoneNo(mPhoneNoEdit) ) {
			App.showShortToast(getString(R.string.info_phone_invalidate));
			return false;
		}
		if( TextUtils.isEmpty(getText(mPasswordEdit)) ) {
			App.showShortToast(getString(R.string.info_please_input_pwd));
			return false;
		}
		return true;
	}
	
	private void doLoginRequest(final String userNo, final String pwd) {
		showLoadingDialog(R.string.info_login_loading);
		LoginRequest request = new LoginRequest();
//		request.setRequestParams("15501501556", "326285");
		request.setRequestParams(userNo, pwd);
		request.doRequest(this, new TonggouResponseParseHandler<LoginResponse>() {

			@Override
			public void onParseSuccess(LoginResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				storeUserLoginInfo(originResult);
				storeUserAccountInfo(userNo, pwd);
				onLoginSuccess(result.getUserScope());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dismissLoadingDialog();
			}
			
			@Override
			public Class<LoginResponse> getTypeClass() {
				return LoginResponse.class;
			}
		});
	}
	
	private void onLoginSuccess(UserScope userScope) {
		MainActivity_.intent(this).mUserScope(userScope).start();
		finish();
	}
	
	private void storeUserAccountInfo(final String userNo, final String pwd) {
		boolean isRemeberPwd = mRememberPwdToggleBtn.isChecked();
		UserAccountManager.storeUserAccount(this, userNo, isRemeberPwd ? pwd : "");
		PreferenceUtil.putBoolean(this, Constants.PREF.NAME_LONGIN_INFO,
				Constants.PREF.KEY_LONGIN_INFO_IS_REMEMBER_PWD, isRemeberPwd);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		LauncherActivity.exitApp(this);
		finish();
	}
	
	private void storeUserLoginInfo(String response) {
		PreferenceUtil.putString(this, Constants.PREF.NAME_LONGIN_INFO, Constants.PREF.KEY_LONGIN_INFO, response);
	}
	
	private LoginResponse restoreUserLoginInfo() {
		String response = PreferenceUtil.getString(this, Constants.PREF.NAME_LONGIN_INFO, Constants.PREF.KEY_LONGIN_INFO);
		if( TextUtils.isEmpty(response) ) {
			return null;
		}
		return new Gson().fromJson(response, LoginResponse.class);
	}
	
	@Override
	protected void onDestroy() {
		dismissExpireDialog();
		super.onDestroy();
	}
}
