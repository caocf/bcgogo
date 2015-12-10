package com.tonggou.gsm.andclient.net.response;

import java.util.ArrayList;

import com.tonggou.gsm.andclient.bean.ViolationRecord;

public class QueryViolationResponse extends BaseResponse {

	private static final long serialVersionUID = 4724010843285733044L;

	private QueryResponse queryResponse;
	
	public ArrayList<ViolationRecord> getLists() {
		return queryResponse.result.lists;
	}
	
	class QueryResponse {
		QueryResult result;
	}
	
	class QueryResult {
		ArrayList<ViolationRecord> lists;

	}
}
