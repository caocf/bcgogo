package com.tonggou.andclient.jsonresponse;

/**
 * 升级检测响应数据
 * @author lwz
 *
 */
public class UpgradeCheckResponse extends BaseResponse {
	
	private static final long serialVersionUID = 875438183711345759L;
	
	private String url;
	private String action;
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
