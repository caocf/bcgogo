package com.tonggou.gsm.andclient.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy.OnLoadDataActionListener;

/**
 * 未付款服务
 * @author lwz
 *
 */
public class UnpayListFragment extends AbsQueryServiceListFragment implements OnLoadDataActionListener {
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mTotalMoneyIndicatorContaienr.setVisibility(View.GONE);
	}
	
	@Override
	boolean isQueryFinishedService() {
		return false;
	}
}
