package com.tonggou.gsm.andclient.net.response;

import java.util.ArrayList;

import com.tonggou.gsm.andclient.bean.Pager;
import com.tonggou.gsm.andclient.bean.ShopNotice;

public class QueryShopNoticeResponse extends BaseResponse {

	private static final long serialVersionUID = 90002705988828094L;

	private ArrayList<ShopNotice> advertDTOList;
	
	private Pager pager;

	public ArrayList<ShopNotice> getAdvertDTOList() {
		return advertDTOList;
	}

	public void setAdvertDTOList(ArrayList<ShopNotice> advertDTOList) {
		this.advertDTOList = advertDTOList;
	}

	public Pager getPager() {
		return pager;
	}

	public void setPager(Pager pager) {
		this.pager = pager;
	}
	
}
   