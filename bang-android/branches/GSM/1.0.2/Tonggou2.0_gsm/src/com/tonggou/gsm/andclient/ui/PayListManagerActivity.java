package com.tonggou.gsm.andclient.ui;

import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.ui.fragment.AbsPullToRefreshLazyLoadFragment;
import com.tonggou.gsm.andclient.ui.fragment.PaidListFragment;
import com.tonggou.gsm.andclient.ui.fragment.UnpayListFragment;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;

/**
 * 账单界面
 * @author lwz
 *
 */
public class PayListManagerActivity extends AbsTabBackableActivity implements OnPageChangeListener {
	
	@Override
	int getLayoutRes() {
		return R.layout.activity_play_list_manager;
	}

	@Override
	int getTabViewId() {
		return R.id.tab_view;
	}

	@Override
	int getViewPagerId() {
		return R.id.viewpager;
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_pay_list_manager);
	}
	
	@Override
	AbsPullToRefreshLazyLoadFragment getViewPagerFragmentItem(int pos) {
		boolean isUnpay = (pos == 0);
		return AbsPullToRefreshLazyLoadFragment.newInstance( isUnpay ? UnpayListFragment.class : PaidListFragment.class, isUnpay);
	}
	
}
