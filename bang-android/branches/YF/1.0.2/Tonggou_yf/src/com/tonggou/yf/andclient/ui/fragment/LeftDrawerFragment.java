package com.tonggou.yf.andclient.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tonggou.lib.net.response.BaseResponse;
import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.net.TonggouResponseParseHandler;
import com.tonggou.yf.andclient.net.request.LogoutRequest;
import com.tonggou.yf.andclient.service.UpdateService;
import com.tonggou.yf.andclient.ui.LoginActivity;
import com.tonggou.yf.andclient.util.PackageInfoUtil;
import com.tonggou.yf.andclient.util.UserAccountManager;

public class LeftDrawerFragment extends BaseFragment implements View.OnClickListener {

	@Override
	public int getLayoutRes() {
		return R.layout.fragment_left_drawer;
	}
	
	TextView mVersionText;
	TextView mUsernameText;
	Button mUpdateBtn;
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mVersionText = findViewById(R.id.version_text);
		mUsernameText = findViewById(R.id.username_text);
		mUsernameText.setText(
				getString(R.string.format_current_username, UserAccountManager.restoreUserNo(getActivity())));
		findViewById(R.id.update_btn).setOnClickListener(this);
		findViewById(R.id.logout_btn).setOnClickListener(this);
		
		mVersionText.setText(
				getString(R.string.format_version, PackageInfoUtil.getVersionName(getActivity())));
	}
	
	void onUpdateBtnClick() {
		UpdateService.update(getActivity(), false, new UpdateService.onUpdateCheckRequestListener() {
			
			@Override
			public void onStart() {
				showLoadingDialog(R.string.info_loading_update_check);
			}
			
			@Override
			public void onFinish() {
				dismissLoadingDialog();
			}
		});
	}
	
	void onLogoutBtnClick() {
		String userNo = UserAccountManager.restoreUserNo(getActivity());
		if(TextUtils.isEmpty(userNo)) {
			onLogoutSuccess();
			
		} else {
			showLoadingDialog(R.string.info_loading_logout);
			LogoutRequest request = new LogoutRequest();
			request.setRequestParams(userNo);
			request.doRequest(getActivity(), new TonggouResponseParseHandler<BaseResponse>() {
	
				@Override
				public void onParseSuccess(BaseResponse result, String originResult) {
					super.onParseSuccess(result, originResult);
					onLogoutSuccess();
				}
				
				@Override
				public void onFinish() {
					dismissLoadingDialog();
					super.onFinish();
				}
				
				@Override
				public Class<BaseResponse> getTypeClass() {
					return BaseResponse.class;
				}
			});
		}
	}
	
	void onLogoutSuccess() {
		LoginActivity.logout(getActivity());
		getActivity().finish();
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if( viewId == R.id.update_btn ) {
			onUpdateBtnClick();
		} else if( viewId == R.id.logout_btn ) {
			onLogoutBtnClick();
		}
	}
	
}
