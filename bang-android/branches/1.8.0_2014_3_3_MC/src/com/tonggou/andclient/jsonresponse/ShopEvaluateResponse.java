package com.tonggou.andclient.jsonresponse;

import java.util.ArrayList;

import com.tonggou.andclient.vo.AppShopCommentDTO;
import com.tonggou.andclient.vo.Pager;


public class ShopEvaluateResponse extends BaseResponse{
//	private static final long serialVersionUID = -805227647385088742L;
	
	private ArrayList<AppShopCommentDTO> results;
	private Pager pager;
	
	
	public ArrayList<AppShopCommentDTO> getResults() {
		return results;
	}
	public void setResults(ArrayList<AppShopCommentDTO> results) {
		this.results = results;
	}
	public Pager getPager() {
		return pager;
	}
	public void setPager(Pager pager) {
		this.pager = pager;
	}
	@Override
	public String toString() {
		return "ShopEvaluateResponse [results=" + results + "]";
	} 
	
	
	
}
