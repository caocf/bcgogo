package com.tonggou.gsm.andclient.ui.fragment;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.type.DTCStatus;

/**
 * 历史故障列表
 * @author lwz
 *
 */
public class CurrentDTCListFragment extends DTCListFragment {

	@Override
	int getHandlerBtnTextRes() {
		return R.string.btn_handled;
	}

	@Override
	DTCStatus getDTCQueryStatus() {
		return DTCStatus.UNTREATED;
	}

	@Override
	DTCStatus getHandledDTCStatus() {
		return DTCStatus.FIXED;
	}

}
