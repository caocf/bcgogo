package com.tonggou.gsm.andclient.ui.fragment;

import com.tonggou.gsm.andclient.net.response.QueryVehicleDataStatisticResponse;

public abstract class AbsDataLoadedCallbackFragment extends BaseFragment {

	public abstract void onDataLoadedCallback(QueryVehicleDataStatisticResponse response);
}