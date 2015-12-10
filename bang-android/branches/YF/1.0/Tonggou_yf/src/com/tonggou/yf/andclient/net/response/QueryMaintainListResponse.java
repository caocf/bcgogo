package com.tonggou.yf.andclient.net.response;

import java.util.ArrayList;

import com.tonggou.lib.net.response.BaseResponse;
import com.tonggou.yf.andclient.bean.Pager;
import com.tonggou.yf.andclient.bean.ServiceJobDTO;

public class QueryMaintainListResponse extends BaseResponse {

	private static final long serialVersionUID = 6812763215270266788L;
	
	ArrayList<ServiceJobDTO> serviceJobDTOList;
	Pager pager;
	
	public ArrayList<ServiceJobDTO> getServiceJobDTOList() {
		return serviceJobDTOList;
	}
	public Pager getPager() {
		return pager;
	}
}
