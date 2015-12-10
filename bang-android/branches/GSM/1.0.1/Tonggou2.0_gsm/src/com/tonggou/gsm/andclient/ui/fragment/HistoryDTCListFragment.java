package com.tonggou.gsm.andclient.ui.fragment;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.type.DTCStatus;

/**
 * 历史故障列表
 * @author lwz
 *
 */
public class HistoryDTCListFragment extends DTCListFragment {

	@Override
	int getHandlerBtnTextRes() {
		return R.string.btn_delete;
	}
	
	@Override
	DTCStatus getDTCQueryStatus() {
		return DTCStatus.FIXED;
	}
	
	@Override
	DTCStatus getHandledDTCStatus() {
		return DTCStatus.DELETED;
	}

}
