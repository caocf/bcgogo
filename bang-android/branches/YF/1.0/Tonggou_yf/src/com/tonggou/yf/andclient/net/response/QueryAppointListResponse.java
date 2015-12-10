package com.tonggou.yf.andclient.net.response;

import java.util.ArrayList;

import com.tonggou.lib.net.response.BaseResponse;
import com.tonggou.yf.andclient.bean.AppointOrderDTO;
import com.tonggou.yf.andclient.bean.Pager;

public class QueryAppointListResponse extends BaseResponse {

	private static final long serialVersionUID = -7120515436410149720L;

	ArrayList<AppointOrderDTO> appointOrderDTOList;
	Pager pager;
	
	public ArrayList<AppointOrderDTO> getAppointOrderDTOList() {
		return appointOrderDTOList;
	}
	public Pager getPager() {
		return pager;
	}
}
