package com.bcgogo.user.dto;

import com.bcgogo.enums.MemberStatus;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午6:00
 * To change this template use File | Settings | File Templates.
 */
public class MemberCardDTO implements Serializable,Comparable<MemberCardDTO>{
  private Long id;
  private Long shopId;
  private String type; //计次和储值
  private MemberStatus status;//卡的状态，ENABLED为有效，DISABLED为无效
  private String name;
  private Double price; //购卡的钱
  private Double worth; //卡上的储值
  private Integer accumulatePoints;//积分，暂时不用到
  private Double serviceDiscount;  //服务打折
  private Double materialDiscount; //材料打折
  private Integer worthTerm;
  private Double percentage;
  private Double percentageAmount; //员工提成
  private int sort;//只用来给洗车卡，银卡，金卡，vip卡排序
  private List<MemberCardServiceDTO> memberCardServiceDTOs;

  public Long getId() {
    return id;
  }

  public Long getShopId() {
    return shopId;
  }

  public String getType() {
    return type;
  }

  public MemberStatus getStatus() {
    return status;
  }

  public String getName() {
    return name;
  }

  public Double getPrice() {
    return price;
  }

  public Double getWorth() {
    return worth;
  }

  public Integer getAccumulatePoints() {
    return accumulatePoints;
  }

  public Double getServiceDiscount() {
    return serviceDiscount;
  }

  public Double getMaterialDiscount() {
    return materialDiscount;
  }

  public Integer getWorthTerm() {
    return worthTerm;
  }

  public Double getPercentage() {
    return percentage;
  }

  public Double getPercentageAmount() {
    return percentageAmount;
  }

  public List<MemberCardServiceDTO> getMemberCardServiceDTOs() {
    return memberCardServiceDTOs;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setStatus(MemberStatus status) {
    this.status = status;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public void setWorth(Double worth) {
    this.worth = worth;
  }

  public void setAccumulatePoints(Integer accumulatePoints) {
    this.accumulatePoints = accumulatePoints;
  }

  public void setServiceDiscount(Double serviceDiscount) {
    this.serviceDiscount = serviceDiscount;
  }

  public void setMaterialDiscount(Double materialDiscount) {
    this.materialDiscount = materialDiscount;
  }

  public void setWorthTerm(Integer worthTerm) {
    this.worthTerm = worthTerm;
  }

  public void setPercentageAmount(Double percentageAmount) {
    this.percentageAmount = percentageAmount;
  }

  public void setPercentage(Double percentage) {
    this.percentage = percentage;
  }

  public void setMemberCardServiceDTOs(List<MemberCardServiceDTO> memberCardServiceDTOs) {
    this.memberCardServiceDTOs = memberCardServiceDTOs;
  }

  public int getSort() {
    return sort;
  }

  public void setSort(int sort) {
    this.sort = sort;
  }

  public int compareTo(MemberCardDTO memberCardDTO) {
    return memberCardDTO.getSort()-this.getSort();
  }
}
