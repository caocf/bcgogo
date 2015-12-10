package com.tonggou.andclient.vo;

public class Pager {

	private int currentPage ;        //当前分页位置  int
	private int pageSize;      //分页大小    int
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
