package com.tonggou.andclient.jsonresponse;

import org.json.JSONObject;

public class VehicleInfoSuggestionResponse extends BaseResponse {
	
	private static final long serialVersionUID = 3671791911622431869L;
	
	private JSONObject result;

	public JSONObject getResult() {
		return result;
	}

	public void setResult(JSONObject result) {
		this.result = result;
	}

}
