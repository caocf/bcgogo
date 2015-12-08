package com.bcgogo.api;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午5:22
 */
public class MemberInfoDTO {
  private String memberNo;
  private String type;//会员类型
  private String status;//状态
  private Double balance;//余额
  private Double memberConsumeTotal;//会员卡累计消费
  private Integer accumulatePoints;//会员积分
  private Double memberDiscount;//会员卡折扣
  private Double serviceDiscount;//服务折扣
  private Double materialDiscount;//商品折扣
  private Long joinDate;//办理时间  unixTime
  private Long deadline;//有效期  unixTime
  private List<ApiMemberServiceDTO> memberServiceList = new ArrayList<ApiMemberServiceDTO>();// 会员卡中购买的几次服务列表

  public MemberInfoDTO() {
  }

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  public Double getMemberConsumeTotal() {
    return memberConsumeTotal;
  }

  public void setMemberConsumeTotal(Double memberConsumeTotal) {
    this.memberConsumeTotal = memberConsumeTotal;
  }

  public Integer getAccumulatePoints() {
    return accumulatePoints;
  }

  public void setAccumulatePoints(Integer accumulatePoints) {
    this.accumulatePoints = accumulatePoints;
  }

  public Double getMemberDiscount() {
    return memberDiscount;
  }

  public void setMemberDiscount(Double memberDiscount) {
    this.memberDiscount = memberDiscount;
  }

  public Double getServiceDiscount() {
    return serviceDiscount;
  }

  public void setServiceDiscount(Double serviceDiscount) {
    this.serviceDiscount = serviceDiscount;
  }

  public Double getMaterialDiscount() {
    return materialDiscount;
  }

  public void setMaterialDiscount(Double materialDiscount) {
    this.materialDiscount = materialDiscount;
  }

  public Long getJoinDate() {
    return joinDate;
  }

  public void setJoinDate(Long joinDate) {
    this.joinDate = joinDate;
  }

  public Long getDeadline() {
    return deadline;
  }

  public void setDeadline(Long deadline) {
    this.deadline = deadline;
  }

  public List<ApiMemberServiceDTO> getMemberServiceList() {
    return memberServiceList;
  }

  public void setMemberServiceList(List<ApiMemberServiceDTO> memberServiceList) {
    this.memberServiceList = memberServiceList;
  }
}
