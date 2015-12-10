package com.tonggou.andclient.vo;

import java.util.List;

public class MemberInfo {
	String  type;//会员类型            
	String memberNo;	// 会员卡号
	String  status;//状态                          
	double  balance;//余额                         
	double  memberConsumeTotal;//会员卡累计消费    
	int accumulatePoints;//会员积分            
	double  memberDiscount;//会员卡折扣            
	double   serviceDiscount;//服务折扣             
	double  materialDiscount;//商品折扣            
	long  joinDate;//办理时间           unixtime
	long  deadline;//有效期          unixtime
	List<MemberService>   memberServiceList;//  会员卡中购
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getMemberNo() {
		return memberNo;
	}
	public void setMemberNo(String memberNo) {
		this.memberNo = memberNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public double getMemberConsumeTotal() {
		return memberConsumeTotal;
	}
	public void setMemberConsumeTotal(double memberConsumeTotal) {
		this.memberConsumeTotal = memberConsumeTotal;
	}
	public int getAccumulatePoints() {
		return accumulatePoints;
	}
	public void setAccumulatePoints(int accumulatePoints) {
		this.accumulatePoints = accumulatePoints;
	}
	public double getMemberDiscount() {
		return memberDiscount;
	}
	public void setMemberDiscount(double memberDiscount) {
		this.memberDiscount = memberDiscount;
	}
	public double getServiceDiscount() {
		return serviceDiscount;
	}
	public void setServiceDiscount(double serviceDiscount) {
		this.serviceDiscount = serviceDiscount;
	}
	public double getMaterialDiscount() {
		return materialDiscount;
	}
	public void setMaterialDiscount(double materialDiscount) {
		this.materialDiscount = materialDiscount;
	}
	public long getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(long joinDate) {
		this.joinDate = joinDate;
	}
	public long getDeadline() {
		return deadline;
	}
	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}
	public List<MemberService> getMemberServiceList() {
		return memberServiceList;
	}
	public void setMemberServiceList(List<MemberService> memberServiceList) {
		this.memberServiceList = memberServiceList;
	}
}
