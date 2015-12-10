package com.tonggou.gsm.andclient.net.response;

import java.util.ArrayList;

import com.tonggou.gsm.andclient.bean.VehicleDataStatistic;

public class QueryVehicleDataStatisticResponse extends BaseResponse {
	
	private static final long serialVersionUID = -6262598440096661953L;
	
	private VehicleDataStatistic yearStat;
	private ArrayList<VehicleDataStatistic> monthStats;
	
	public VehicleDataStatistic getYearStat() {
		return yearStat;
	}
	public void setYearStat(VehicleDataStatistic yearStat) {
		this.yearStat = yearStat;
	}
	public ArrayList<VehicleDataStatistic> getMonthStats() {
		return monthStats;
	}
	public void setMonthStats(ArrayList<VehicleDataStatistic> monthStats) {
		this.monthStats = monthStats;
	}
	
	
}
