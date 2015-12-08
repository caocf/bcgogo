package com.bcgogo.user.model;

import com.bcgogo.enums.MemberStatus;
import com.bcgogo.enums.PasswordValidateStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-19
 * Time: 下午4:47
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "member")
public class Member extends LongIdentifier implements Cloneable {
  private Long shopId;
  private Long customerId;
  private String type;
  private String memberNo;
  private MemberStatus status;
  private Double balance;
  private Integer accumulatePoints;
  private Double serviceDiscount;
  private Double materialDiscount;
  private Long joinDate;
  private Long deadline;
  private String password;
  private PasswordValidateStatus passwordStatus;
  private Double memberDiscount;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  @Column(name = "type")
  public String getType() {
    return type;
  }

  @Column(name = "member_no")
  public String getMemberNo() {
    return memberNo;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public MemberStatus getStatus() {
    return status;
  }

  @Column(name = "balance")
  public Double getBalance() {
    return balance;
  }

  @Column(name = "accumulate_points")
  public Integer getAccumulatePoints() {
    return accumulatePoints;
  }

  @Column(name = "service_discount")
  public Double getServiceDiscount() {
    return serviceDiscount;
  }

  @Column(name = "material_discount")
  public Double getMaterialDiscount() {
    return materialDiscount;
  }

  @Column(name = "join_date")
  public Long getJoinDate() {
    return joinDate;
  }

  @Column(name = "deadline")
  public Long getDeadline() {
    return deadline;
  }

  @Column(name = "password")
  public String getPassword() {
    return password;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "password_status")
  public PasswordValidateStatus getPasswordStatus() {
    return passwordStatus;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public void setStatus(MemberStatus status) {
    this.status = status;
  }

  public void setBalance(Double balance) {
    if (balance == null) {
      this.balance = balance;
    } else {
      this.balance = NumberUtil.round(balance.doubleValue(), NumberUtil.MONEY_PRECISION);
    }

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

  public void setJoinDate(Long joinDate) {
    this.joinDate = joinDate;
  }

  public void setDeadline(Long deadline) {
    this.deadline = deadline;
  }

  @Column(name = "member_discount")
  public Double getMemberDiscount() {
    return memberDiscount;
  }

  public void setMemberDiscount(Double memberDiscount) {
    this.memberDiscount = memberDiscount;
  }

  public MemberDTO toDTO() {
    MemberDTO memberDTO = new MemberDTO();
    memberDTO.setAccumulatePoints(this.getAccumulatePoints());
    memberDTO.setBalance(NumberUtil.doubleVal(this.getBalance()));
    memberDTO.setCustomerId(this.getCustomerId());
    memberDTO.setDeadline(this.getDeadline());
    memberDTO.setId(this.getId());
    memberDTO.setJoinDate(this.getJoinDate());
    memberDTO.setJoinDateStr(DateUtil.dateLongToStr(this.getJoinDate(), DateUtil.DATE_STRING_FORMAT_DAY2));
    memberDTO.setMaterialDiscount(this.getMaterialDiscount());
    memberDTO.setMemberNo(this.getMemberNo());
    memberDTO.setPassword(this.getPassword());
    memberDTO.setServiceDiscount(this.getServiceDiscount());
    memberDTO.setShopId(this.getShopId());
    memberDTO.setStatus(this.getStatus());
    memberDTO.setType(this.getType());
    memberDTO.setPasswordStatus(this.getPasswordStatus());
    if (this.getStatus() != null) {
      memberDTO.setStatusStr(this.getStatus().getStatus());
    }
    memberDTO.setMemberDiscount(this.getMemberDiscount());
    return memberDTO;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void fromDTO(MemberDTO memberDTO) {
    if (memberDTO != null) {
      this.setAccumulatePoints(memberDTO.getAccumulatePoints());
      this.setBalance(memberDTO.getBalance());
      this.setCustomerId(memberDTO.getCustomerId());
      this.setDeadline(memberDTO.getDeadline());
      this.setJoinDate(memberDTO.getJoinDate());
      this.setMaterialDiscount(memberDTO.getMaterialDiscount());
      this.setMemberNo(memberDTO.getMemberNo());
      this.setPassword(memberDTO.getPassword());
      this.setServiceDiscount(memberDTO.getServiceDiscount());
      this.setShopId(memberDTO.getShopId());
      this.setStatus(memberDTO.getStatus());
      this.setType(memberDTO.getType());
      this.setPasswordStatus(memberDTO.getPasswordStatus());
      if (memberDTO.getId() != null) {
        this.setId(memberDTO.getId());
      }
      this.setMemberDiscount(memberDTO.getMemberDiscount());
    }
  }

  public Member() {

  }

  public Member(MemberDTO memberDTO) {
    if (memberDTO != null) {
      this.setAccumulatePoints(memberDTO.getAccumulatePoints());
      this.setBalance(memberDTO.getBalance());
      this.setCustomerId(memberDTO.getCustomerId());
      this.setDeadline(memberDTO.getDeadline());
      this.setJoinDate(memberDTO.getJoinDate());
      this.setMaterialDiscount(memberDTO.getMaterialDiscount());
      this.setMemberNo(memberDTO.getMemberNo());
      this.setPassword(memberDTO.getPassword());
      this.setServiceDiscount(memberDTO.getServiceDiscount());
      this.setShopId(memberDTO.getShopId());
      this.setStatus(memberDTO.getStatus());
      this.setType(memberDTO.getType());
      this.setPasswordStatus(memberDTO.getPasswordStatus());
      if (memberDTO.getId() != null) {
        this.setId(memberDTO.getId());
      }
      this.setMemberDiscount(memberDTO.getMemberDiscount());
    }
  }

  public void setPasswordStatus(PasswordValidateStatus passwordStatus) {
    this.passwordStatus = passwordStatus;
  }

  public Member clone() throws CloneNotSupportedException {
    Member newMember = (Member) super.clone();
    newMember.setId(null);
    return newMember;
  }

}

