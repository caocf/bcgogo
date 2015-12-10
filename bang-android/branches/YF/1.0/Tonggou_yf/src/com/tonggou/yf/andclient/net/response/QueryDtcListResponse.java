package com.tonggou.yf.andclient.net.response;

import java.util.ArrayList;

import com.tonggou.lib.net.response.BaseResponse;
import com.tonggou.yf.andclient.bean.Pager;
import com.tonggou.yf.andclient.bean.FaultInfoToShopDTO;

public class QueryDtcListResponse extends BaseResponse {
	
	private static final long serialVersionUID = -28076588520584684L;

	ArrayList<FaultInfoToShopDTO> faultInfoToShopDTOList;
	Pager pager;
	
	public ArrayList<FaultInfoToShopDTO> getFaultInfoToShopDTOList() {
		return faultInfoToShopDTOList;
	}
	public Pager getPager() {
		return pager;
	}
	
}
