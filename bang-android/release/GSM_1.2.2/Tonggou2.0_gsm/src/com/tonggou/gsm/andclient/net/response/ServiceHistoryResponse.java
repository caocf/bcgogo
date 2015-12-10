package com.tonggou.gsm.andclient.net.response;

import java.util.ArrayList;

import com.tonggou.gsm.andclient.bean.Pager;
import com.tonggou.gsm.andclient.bean.ServiceHistory;

public class ServiceHistoryResponse extends BaseResponse {

	private static final long serialVersionUID = 8033565355222526710L;
	
	private float finishedServiceTotal;		// 已完成服务的总额 Double
	private ArrayList<ServiceHistory> unFinishedServiceList;
	private ArrayList<ServiceHistory> finishedServiceList;
	private Pager pager;
	
	
	public float getFinishedServiceTotal() {
		return finishedServiceTotal;
	}
	public void setFinishedServiceTotal(float finishedServiceTotal) {
		this.finishedServiceTotal = finishedServiceTotal;
	}
	public ArrayList<ServiceHistory> getUnFinishedServiceList() {
		return unFinishedServiceList;
	}
	public void setUnFinishedServiceList(
			ArrayList<ServiceHistory> unFinishedServiceList) {
		this.unFinishedServiceList = unFinishedServiceList;
	}
	public ArrayList<ServiceHistory> getFinishedServiceList() {
		return finishedServiceList;
	}
	public void setFinishedServiceList(ArrayList<ServiceHistory> finishedServiceList) {
		this.finishedServiceList = finishedServiceList;
	}
	public Pager getPager() {
		return pager;
	}
	public void setPager(Pager pager) {
		this.pager = pager;
	}
	
}
