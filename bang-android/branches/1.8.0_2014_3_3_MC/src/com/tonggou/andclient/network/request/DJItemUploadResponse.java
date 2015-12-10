package com.tonggou.andclient.network.request;

import com.tonggou.andclient.jsonresponse.BaseResponse;
import com.tonggou.andclient.vo.DrivingJournalItem;

public class DJItemUploadResponse extends BaseResponse {
	private static final long serialVersionUID = 2552878352438452241L;

	private DrivingJournalItem result;

	public DrivingJournalItem getResult() {
		return result;
	}

	public void setResult(DrivingJournalItem result) {
		this.result = result;
	}

}
