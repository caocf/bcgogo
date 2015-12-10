package com.tonggou.andclient.jsonresponse;

import java.util.List;

import com.tonggou.andclient.vo.Pager;
import com.tonggou.andclient.vo.TGService;

public class SearchServiceResponse extends BaseResponse {
	
	private static final long serialVersionUID = -5305870238143289707L;
	
	private List<TGService> results;  //未完成服务列表
	private Pager pager;
	public List<TGService> getResults() {
		return results;
	}
	public void setResults(List<TGService> results) {
		this.results = results;
	}
	public Pager getPager() {
		return pager;
	}
	public void setPager(Pager pager) {
		this.pager = pager;
	}
	
}
