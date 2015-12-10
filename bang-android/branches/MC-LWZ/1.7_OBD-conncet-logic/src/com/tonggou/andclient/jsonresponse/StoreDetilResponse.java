package com.tonggou.andclient.jsonresponse;

import com.tonggou.andclient.vo.Shop;

public class StoreDetilResponse extends BaseResponse {
	
	private static final long serialVersionUID = -1534207952512501618L;
	
	private Shop  shop;
	
	public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}

}
