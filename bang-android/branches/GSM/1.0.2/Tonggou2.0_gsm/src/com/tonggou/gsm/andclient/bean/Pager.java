package com.tonggou.gsm.andclient.bean;

public class Pager {
	private int currentPage ;        	//当前分页位置  int
	private int pageSize;      			//分页大小    int
	private boolean hasNextPage; 		// 是否有下一页
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
	public boolean isHasNextPage() {
		return hasNextPage;
	}
	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}
}
