package com.tonggou.andclient.network.request;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tonggou.andclient.network.API;
import com.tonggou.andclient.vo.DrivingJournalItem;

public class DJItemUploadRequest extends AbsTonggouHttpRequest {

	@Override
	public HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	public void setRequestParams(DrivingJournalItem djItem) {
		JsonObject jsonObject = new Gson().toJsonTree(djItem).getAsJsonObject();
		jsonObject.remove("status");
		HttpRequestParams params = new HttpRequestParams(jsonObject);
		super.setRequestParams(params);
	}

	@Override
	public String getAPI() {
		return API.UPLOAD_DJITEM;
	}

}
