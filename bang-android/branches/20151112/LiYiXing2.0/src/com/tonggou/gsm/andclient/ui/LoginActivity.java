package com.tonggou.gsm.andclient.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.ToggleButton;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.LoginRequest;
import com.tonggou.gsm.andclient.net.response.LoginResponse;
import com.tonggou.gsm.andclient.service.PollingMessageService;
import com.tonggou.gsm.andclient.service.PollingVehicleLocationService;
import com.tonggou.gsm.andclient.ui.view.SingleButtonDialog;
import com.tonggou.gsm.andclient.util.ContextUtil;
import com.tonggou.gsm.andclient.util.PreferenceUtil;
import com.tonggou.gsm.andclient.util.StringUtil;

/**
 * 登录页面
 * @author lwz
 *
 */
public class LoginActivity extends BaseActivity {
	
	public static final int REQUEST_CODE_REGISTER = 0x01;
	private static final String EXTRA_IS_LOGIN_EXPIRE = "extra_is_login_expire";
	
	private EditText mPhoneNoEdit;
	private EditText mPasswordEdit;
	private ToggleButton mRememberPwdToggleBtn;
	private ScrollView mScrollview;
	private SingleButtonDialog mLoginExpireDialog;
	
	public static void logout() {
		logout(false);
	}

	private static void logout(boolean isLoginExpire) {
		PollingMessageService.stopPolling(App.getInstance());
		PollingVehicleLocationService.stopPolling(App.getInstance());
		
		Bundle args = new Bundle();
		args.putBoolean(EXTRA_IS_LOGIN_EXPIRE, isLoginExpire);
		ContextUtil.startActivity(App.getInstance(), LoginActivity.class, args, Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
	}
	
	public static void loginExpire() {
		logout(true);
	}
	
	private void showLoginExpireDialog() {
		dismissLoginExpireDialog();
		mLoginExpireDialog = new SingleButtonDialog(this);
		mLoginExpireDialog.showDialog(getString(R.string.network_login_expire), getString(R.string.btn_confirm), null);
	}

	private void dismissLoginExpireDialog() {
		if(mLoginExpireDialog != null && mLoginExpireDialog.isShowing()) {
			mLoginExpireDialog.dismiss();
		}
		mLoginExpireDialog = null;
 	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mScrollview = (ScrollView) findViewById(R.id.scrollview);
		mPhoneNoEdit = (EditText) findViewById(R.id.phone_no_edit);
		mPasswordEdit = (EditText) findViewById(R.id.pwd_edit);
		mRememberPwdToggleBtn = (ToggleButton) findViewById(R.id.remeber_pwd_toggle_btn);
		
		mPhoneNoEdit.setHintTextColor(Color.WHITE);
		mPasswordEdit.setHintTextColor(Color.WHITE);
		
		mPhoneNoEdit.setText(UserBaseInfo.getUserInfo().getMobile());
		mPhoneNoEdit.setSelection(getText(mPhoneNoEdit).length());
		mPasswordEdit.setText(restorePwdByPhoneNo(getText(mPhoneNoEdit)));
		mPasswordEdit.setSelection(getText(mPasswordEdit).length());
		
		registerScrollViewScrollToBottomListener();
		initRememberPwdToggleBtnStatus();
		
		Intent intent = getIntent();
		if( intent != null && intent.getBooleanExtra(EXTRA_IS_LOGIN_EXPIRE, false)) {
			showLoginExpireDialog();
		}
	}
	
	/**
	 * 当用户要输入时，软键盘弹出会导致 ScrollView 的布局改变，这时可以使 ScrollView 滑动到底部。
	 * 将输入框和按钮全部显示出来，方便用户操作
	 */
	private void registerScrollViewScrollToBottomListener() {
		mScrollview.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mScrollview.post(new Runnable() {
							public void run() {
								// 记录原始焦点状态
								// NOTE: 使用 mScrollview.fullScroll(View.FOCUS_DOWN); 会自动将焦点移到最后一个可以被焦点的控件上
								boolean isPhoneNoFocused = mPhoneNoEdit.isFocused();
								mScrollview.fullScroll(View.FOCUS_DOWN);
								// 还原焦点状态
								if (isPhoneNoFocused) {
									mPhoneNoEdit.requestFocus();
								}
//								else {	// 该布局的最后一个可以被焦点的控件为 passwordEdit, 故可以不需要次代码
//									mPasswordEdit.requestFocus();
//								}
							}
						});
					}
				});
	}
	
	private void initRememberPwdToggleBtnStatus() {
		mRememberPwdToggleBtn.setChecked(
				PreferenceUtil.getBoolean(this, Constants.PREF.PREF_NAME_LONGIN_INFO,
							Constants.PREF.PREF_KEY_LONGIN_INFO_IS_REMEMBER_PWD));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		storeRememberPwdToggleBtnStatus();
		dismissLoginExpireDialog();
	}
	
	private void storeRememberPwdToggleBtnStatus() {
		PreferenceUtil.putBoolean(this, Constants.PREF.PREF_NAME_LONGIN_INFO,
				Constants.PREF.PREF_KEY_LONGIN_INFO_IS_REMEMBER_PWD, mRememberPwdToggleBtn.isChecked());
	}
	
	private void storeOrRemovePwdByPhoneNo(String phoneNo) {
		if( mRememberPwdToggleBtn.isChecked() ) {
			PreferenceUtil.putString(this, Constants.PREF.PREF_NAME_LONGIN_INFO, phoneNo, getText(mPasswordEdit));
		} else {
			PreferenceUtil.remove(this, Constants.PREF.PREF_NAME_LONGIN_INFO, phoneNo);
		}
	}
	
	private String restorePwdByPhoneNo(String phoneNo) {
		return PreferenceUtil.getString(this, Constants.PREF.PREF_NAME_LONGIN_INFO, phoneNo);
	}
	
	public void onLoginBtnClick(View view) {
		if( invalidateText(mPhoneNoEdit, R.string.info_please_input_phone_no)
				|| invalidateText(mPasswordEdit, R.string.info_please_input_pwd_no)) {
			return;
		}
		
		if( !StringUtil.isPhoneNo(getText(mPhoneNoEdit)) ) {
			App.showShortToast( getString(R.string.txt_info_phone_invalidate) );
			return;
		}
		doLogin(getText(mPhoneNoEdit), getText(mPasswordEdit));
	}
	
	public void doLogin(final String phoneNo, String pwd) {
		showLoadingDialog();
		LoginRequest request = new LoginRequest();
		request.setRequestParams(phoneNo, pwd);
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<LoginResponse>() {

			@Override
			public void onParseSuccess(LoginResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				UserBaseInfo.setInfos(result);
				storeOrRemovePwdByPhoneNo(phoneNo);
				toMainActivity();
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

	public void onRegisterBtnClick(View view) {
		ContextUtil.startActivityForResult(this, RegisterValidateActivity.class, REQUEST_CODE_REGISTER);
	}
	
	public void toMainActivity() {
		ContextUtil.startActivity(this, MainActivity.class);
	}
	
	public void onForgotPwdBtnClick(View view) {
		Bundle args = new Bundle();
		args.putString(ForgotPwdActivity.EXTRA_PHONE_NO, getText(mPhoneNoEdit));
		ContextUtil.startActivity(this, ForgotPwdActivity.class, args);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( resultCode != RESULT_OK )
			return;
		
		// 说明注册成功，跳转到 MainActivity
		if( requestCode == REQUEST_CODE_REGISTER ) {
			toMainActivity();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		LauncherActivity.exitApp(this);
	}
	
}
