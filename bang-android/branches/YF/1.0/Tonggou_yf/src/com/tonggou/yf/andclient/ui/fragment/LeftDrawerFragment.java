package com.tonggou.yf.andclient.ui.fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.text.TextUtils;
import android.widget.TextView;

import com.tonggou.lib.net.response.BaseResponse;
import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.net.TonggouResponseParseHandler;
import com.tonggou.yf.andclient.net.request.LogoutRequest;
import com.tonggou.yf.andclient.service.UpdateService;
import com.tonggou.yf.andclient.ui.LoginActivity_;
import com.tonggou.yf.andclient.util.PackageInfoUtil;
import com.tonggou.yf.andclient.util.UserAccountManager;

@EFragment(R.layout.fragment_left_drawer)
public class LeftDrawerFragment extends BaseFragment {

	@ViewById(R.id.version_text) TextView mVersionText;
	
	@AfterViews
	void afterViews() {
		mVersionText.setText(
				getString(R.string.format_version, PackageInfoUtil.getVersionName(getActivity())));
	}
	
	@Click(R.id.update_btn) 
	void updateVersion() {
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
	
	@Click(R.id.logout_btn)
	void logout() {
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
		LoginActivity_.logout(getActivity());
		getActivity().finish();
	}
	
}
