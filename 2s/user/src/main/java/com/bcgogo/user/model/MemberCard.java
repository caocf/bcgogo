package com.bcgogo.user.model;

import com.bcgogo.enums.MemberStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.MemberCardDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午2:15
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="member_card")
public class MemberCard extends LongIdentifier{
  private Long shopId;
  private String type;
  private MemberStatus status;
  private String name;
  private Double price;
  private Double worth;
  private Integer accumulatePoints;
  private Double serviceDiscount;
  private Double materialDiscount;
  private Integer worthTerm;
  private Double percentage;
  private Double percentageAmount;

  public MemberCard()
  {

  }
  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }
  @Column(name="type")
  public String getType() {
    return type;
  }
  @Enumerated(EnumType.STRING)
  @Column(name="status")
  public MemberStatus getStatus() {
    return status;
  }
  @Column(name="name")
  public String getName() {
    return name;
  }
  @Column(name="price")
  public Double getPrice() {
    return price;
  }
  @Column(name="worth")
  public Double getWorth() {
    return worth;
  }
  @Column(name="accumulate_points")
  public Integer getAccumulatePoints() {
    return accumulatePoints;
  }
  @Column(name="service_discount")
  public Double getServiceDiscount() {
    return serviceDiscount;
  }
  @Column(name="material_discount")
  public Double getMaterialDiscount() {
    return materialDiscount;
  }
  @Column(name="worth_term")
  public Integer getWorthTerm() {
    return worthTerm;
  }
  @Column(name="percentage")
  public Double getPercentage() {
    return percentage;
  }
  @Column(name="percentage_amount")
  public Double getPercentageAmount() {
    return percentageAmount;
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

  public void setPercentage(Double percentage) {
    this.percentage = percentage;
  }

  public void setPercentageAmount(Double percentageAmount) {
    this.percentageAmount = percentageAmount;
  }

  public MemberCardDTO toDTO()
  {
    MemberCardDTO memberCardDTO = new MemberCardDTO();
    memberCardDTO.setAccumulatePoints(this.getAccumulatePoints());
    memberCardDTO.setId(this.getId());
    memberCardDTO.setMaterialDiscount(this.getMaterialDiscount());
    memberCardDTO.setName(this.getName());
    memberCardDTO.setPercentage(this.getPercentage());
    memberCardDTO.setPercentageAmount(this.getPercentageAmount());
    memberCardDTO.setPrice(this.getPrice());
    memberCardDTO.setServiceDiscount(this.getServiceDiscount());
    memberCardDTO.setShopId(this.getShopId());
    memberCardDTO.setStatus(this.getStatus());
    memberCardDTO.setWorthTerm(this.getWorthTerm());
    memberCardDTO.setType(this.getType());
    memberCardDTO.setWorth(this.getWorth());
    return memberCardDTO;
  }

  public MemberCard(MemberCardDTO memberCardDTO)
  {
    if(null != memberCardDTO)
    {
      this.setAccumulatePoints(memberCardDTO.getAccumulatePoints());
      this.setMaterialDiscount(memberCardDTO.getMaterialDiscount());
      this.setName(memberCardDTO.getName());
      this.setPercentage(memberCardDTO.getPercentage());
      this.setPercentageAmount(memberCardDTO.getPercentageAmount());
      this.setPrice(memberCardDTO.getPrice());
      this.setWorth(memberCardDTO.getWorth());
      this.setServiceDiscount(memberCardDTO.getServiceDiscount());
      this.setShopId(memberCardDTO.getShopId());
      this.setStatus(memberCardDTO.getStatus());
      this.setType(memberCardDTO.getType());
      this.setWorthTerm(memberCardDTO.getWorthTerm());
      if(null != memberCardDTO.getId())
      {
        this.setId(memberCardDTO.getId());
      }
    }
  }

  public void copyFromMemberCardDTO(MemberCardDTO memberCardDTO)
  {
    if(null != memberCardDTO)
    {
      this.setAccumulatePoints(memberCardDTO.getAccumulatePoints());
      this.setMaterialDiscount(memberCardDTO.getMaterialDiscount());
      this.setName(memberCardDTO.getName());
      this.setPercentage(memberCardDTO.getPercentage());
      this.setPercentageAmount(memberCardDTO.getPercentageAmount());
      this.setPrice(memberCardDTO.getPrice());
      this.setWorth(memberCardDTO.getWorth());
      this.setServiceDiscount(memberCardDTO.getServiceDiscount());
      this.setShopId(memberCardDTO.getShopId());
      this.setStatus(memberCardDTO.getStatus());
      this.setType(memberCardDTO.getType());
      this.setWorthTerm(memberCardDTO.getWorthTerm());
      if(null != memberCardDTO.getId())
      {
        this.setId(memberCardDTO.getId());
      }
    }
  }
}
