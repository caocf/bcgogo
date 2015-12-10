package com.tonggou.yf.andclient.net.response;

import com.tonggou.lib.net.response.BaseResponse;
import com.tonggou.yf.andclient.bean.type.UpdateAction;

public class UpdateResponse extends BaseResponse {

	private static final long serialVersionUID = 489858267302536663L;

	private String url;
	private UpdateAction action;
	private String description;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public UpdateAction getAction() {
		return action;
	}

	public void setAction(UpdateAction action) {
		this.action = action;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
