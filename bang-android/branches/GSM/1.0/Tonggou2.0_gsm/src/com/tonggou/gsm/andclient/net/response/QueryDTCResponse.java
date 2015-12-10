package com.tonggou.gsm.andclient.net.response;

import java.util.ArrayList;

import com.tonggou.gsm.andclient.bean.DTCInfo;
import com.tonggou.gsm.andclient.bean.Pager;

public class QueryDTCResponse extends BaseResponse {

	private static final long serialVersionUID = 4378097576357049738L;

	private ArrayList<DTCInfo> result;
	private Pager pager;
	public ArrayList<DTCInfo> getResult() {
		return result;
	}
	public void setResult(ArrayList<DTCInfo> result) {
		this.result = result;
	}
	public Pager getPager() {
		return pager;
	}
	public void setPager(Pager pager) {
		this.pager = pager;
	}
	
}
