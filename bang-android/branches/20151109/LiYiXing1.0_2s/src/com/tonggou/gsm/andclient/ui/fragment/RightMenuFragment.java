package com.tonggou.gsm.andclient.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.LogoutRequest;
import com.tonggou.gsm.andclient.net.response.BaseResponse;
import com.tonggou.gsm.andclient.service.NewVersionBroadcastReceiver.OnGotNewVersionListener;
import com.tonggou.gsm.andclient.service.UpdateService;
import com.tonggou.gsm.andclient.service.UpdateService.onUpdateCheckRequestListener;
import com.tonggou.gsm.andclient.ui.BaseActivity;
import com.tonggou.gsm.andclient.ui.MainActivity;
import com.tonggou.gsm.andclient.ui.LoginActivity;
import com.tonggou.gsm.andclient.ui.ModifyPwdActivity;
import com.tonggou.gsm.andclient.ui.MyDeviceActivity;
import com.tonggou.gsm.andclient.ui.PayListManagerActivity;
import com.tonggou.gsm.andclient.ui.ShopDetailActivity;
import com.tonggou.gsm.andclient.ui.VehicleManagerActivity;
import com.tonggou.gsm.andclient.ui.view.AbsViewHolderAdapter;
import com.tonggou.gsm.andclient.ui.view.ImageWithIndicatorView;

public class RightMenuFragment extends AbsMenuAdapterViewFragment implements OnItemClickListener, OnGotNewVersionListener {

	@Override
	int getLayoutId() {
		return	R.layout.fragment_menu_right;
	}

	@Override
	int getAdapterViewId() {
		return R.id.right_menu_grid;
	}

	@Override
	int getAdapterItemLayoutId() {
		return R.layout.item_grid_right_menu;
	}

	@Override
	int getItemCount() {
		return getDestActivitys().length;
	}

	@Override
	int getContentsArrayRes() {
		return R.array.right_menu_arr;
	}

	@Override
	int getIconsTypeArrayRes() {
		return R.array.right_menu_icon;
	}

	@Override
	Class<?>[] getDestActivitys() {
		return new Class<?>[]{
				MyDeviceActivity.class, ModifyPwdActivity.class,
				VehicleManagerActivity.class, PayListManagerActivity.class,
				ShopDetailActivity.class, null
			};
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if( position == 5 ) {
			doUpdateCheck();

		} else {
			super.onItemClick(parent, view, position, id);
		}
	}

	private void doUpdateCheck() {
		UpdateService.update(getActivity(), false, new onUpdateCheckRequestListener() {

			@Override
			public void onStart() {
				((MainActivity)mActivity).showLoadingDialog();
			}

			@Override
			public void onFinish() {
				((MainActivity)mActivity).dismissLoadingDialog();
			}
		});
	}

	@Override
	void setMenuAdapterViewData(AbsViewHolderAdapter<MenuItem> adapter,
			int pos, MenuItem itemData) {
		ImageWithIndicatorView numberImage = adapter.getViewFromHolder(R.id.number_icon);
		TextView txtContent = adapter.getViewFromHolder(R.id.menu_item_content);

		numberImage.setImageDrawable(itemData.icon);
		numberImage.setIndicator(itemData.indicator);
		txtContent.setText(itemData.content);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		((TextView)findViewById(R.id.phone_no_text)).setText(
				getString(R.string.menu_right_user_mobile, UserBaseInfo.getUserInfo().getMobile()));

		findViewById(R.id.logout_btn).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				doLogout();
			}
		});
	}

	private void doLogout() {
		((BaseActivity)mActivity).showLoadingDialog();
		LogoutRequest request = new LogoutRequest();
		request.setRequestParams(UserBaseInfo.getUserInfo().getUserNo());
		request.doRequest(mActivity, new AsyncJsonBaseResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				LoginActivity.logout();
			}

			@Override
			public void onFinish() {
				((BaseActivity)mActivity).dismissLoadingDialog();
				super.onFinish();
			}

			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
		});
	}

	@Override
	public void onGotNewVersion() {
		mMenuAdapter.getItem(5).icon = getResources().getDrawable(R.drawable.ic_update_new);
		mMenuAdapter.notifyDataSetChanged();
	}
}