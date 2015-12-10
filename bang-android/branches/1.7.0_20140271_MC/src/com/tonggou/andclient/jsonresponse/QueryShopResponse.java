package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.Pager;
import com.tonggou.andclient.vo.Shop;

/**
 * 查询店面请求响应类
 * @author lwz
 *
 */
public class QueryShopResponse extends BaseResponse {

	private static final long serialVersionUID = -1459662660480936328L;

	private List<Shop> shopList;
	private Pager pager;
	
	public List<Shop> getShopList() {
		return shopList;
	}
	public void setShopList(List<Shop> shopList) {
		this.shopList = shopList;
	}
	public Pager getPager() {
		return pager;
	}
	public void setPager(Pager pager) {
		this.pager = pager;
	}
}
