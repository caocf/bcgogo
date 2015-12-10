package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.AppShopCommentDTO;
import com.tonggou.andclient.vo.Shop;

public class StoreDetilResponse extends BaseResponse {
	
	private static final long serialVersionUID = -1534207952512501618L;
	
	private Shop  shop;
	private int commentCount;   //评论条数
	private List<AppShopCommentDTO> appShopCommentDTOs;  //评论内容列表
	
	
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public List<AppShopCommentDTO> getAppShopCommentDTOs() {
		return appShopCommentDTOs;
	}
	public void setAppShopCommentDTOs(List<AppShopCommentDTO> appShopCommentDTOs) {
		this.appShopCommentDTOs = appShopCommentDTOs;
	}
	public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}

}
