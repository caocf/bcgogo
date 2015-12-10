package com.tonggou.gsm.andclient.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.ui.PaymentDetailActivity;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy.OnLoadDataActionListener;
import com.tonggou.gsm.andclient.util.ContextUtil;

/**
 * 故障列表
 * @author lwz
 *
 */
public class PaidListFragment extends AbsQueryServiceListFragment implements OnLoadDataActionListener {
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final int pos = position -1;
		String serviceHistoryId = mAdapter.getData().get(pos).getOrderId();
		Bundle args = new Bundle();
		args.putString(PaymentDetailActivity.EXTRA_SERVICE_HISTORY_ID, serviceHistoryId);
		ContextUtil.startActivity(mActivity, PaymentDetailActivity.class, args);
	}

	@Override
	boolean isQueryFinishedService() {
		return true;
	}
	
	@Override
	protected void onFinishedServiceTotalMoney(float totalMoney) {
		mTotalMoneyIndicatorText.setTextValue(getString(R.string.format_paid_total_money, totalMoney));
	}

}
