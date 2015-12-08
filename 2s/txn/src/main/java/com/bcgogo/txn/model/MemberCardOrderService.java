package com.bcgogo.txn.model;

import com.bcgogo.enums.ServiceLimitTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.MemberCardOrderServiceDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-4
 * Time: 上午11:32
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "member_card_order_service")
public class MemberCardOrderService extends LongIdentifier {
  private Long shopId;
  private Long memberCardOrderId;
  private Long serviceId;
  private Long serviceHistoryId;
  private Integer increasedTimes;
  private String vehicles;
  private Long deadline;
  private Integer cardTimes;
  private Integer balanceTimes;
  private Integer oldTimes;
  private ServiceLimitTypes increasedTimesLimitType;
  private ServiceLimitTypes cardTimesLimitType;
  private ServiceLimitTypes oldTimesLimitType;
  private ServiceLimitTypes balanceTimesLimitType;

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }
  @Column(name="member_card_order_id")
  public Long getMemberCardOrderId() {
    return memberCardOrderId;
  }
  @Column(name="service_id")
  public Long getServiceId() {
    return serviceId;
  }

  @Column(name = "service_history_id")
  public Long getServiceHistoryId() {
    return serviceHistoryId;
  }

  public void setServiceHistoryId(Long serviceHistoryId) {
    this.serviceHistoryId = serviceHistoryId;
  }

  @Column(name="increased_times")
  public Integer getIncreasedTimes() {
    return increasedTimes;
  }

  @Column(name="vehicles")
  public String getVehicles() {
    return vehicles;
  }

  @Column(name="deadline")
  public Long getDeadline() {
    return deadline;
  }

  @Column(name="card_times")
  public Integer getCardTimes() {
    return cardTimes;
  }

  @Column(name="balance_times")
  public Integer getBalanceTimes() {
    return balanceTimes;
  }

  @Column(name="old_times")
  public Integer getOldTimes() {
    return oldTimes;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="increased_times_limit_type")
  public ServiceLimitTypes getIncreasedTimesLimitType() {
    return increasedTimesLimitType;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="card_times_limit_type")
  public ServiceLimitTypes getCardTimesLimitType() {
    return cardTimesLimitType;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="old_times_limit_type")
  public ServiceLimitTypes getOldTimesLimitType() {
    return oldTimesLimitType;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="balance_times_limit_type")
  public ServiceLimitTypes getBalanceTimesLimitType() {
    return balanceTimesLimitType;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setMemberCardOrderId(Long memberCardOrderId) {
    this.memberCardOrderId = memberCardOrderId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public void setIncreasedTimes(Integer increasedTimes) {
    this.increasedTimes = increasedTimes;
  }

  public void setVehicles(String vehicles) {
    this.vehicles = vehicles;
  }

  public void setDeadline(Long deadline) {
    this.deadline = deadline;
  }

  public void setCardTimes(Integer cardTimes) {
    this.cardTimes = cardTimes;
  }

  public void setBalanceTimes(Integer balanceTimes) {
    this.balanceTimes = balanceTimes;
  }

  public void setOldTimes(Integer oldTimes) {
    this.oldTimes = oldTimes;
  }

  public void setIncreasedTimesLimitType(ServiceLimitTypes increasedTimesLimitType) {
    this.increasedTimesLimitType = increasedTimesLimitType;
  }

  public void setCardTimesLimitType(ServiceLimitTypes cardTimesLimitType) {
    this.cardTimesLimitType = cardTimesLimitType;
  }

  public void setOldTimesLimitType(ServiceLimitTypes oldTimesLimitType) {
    this.oldTimesLimitType = oldTimesLimitType;
  }

  public void setBalanceTimesLimitType(ServiceLimitTypes balanceTimesLimitType) {
    this.balanceTimesLimitType = balanceTimesLimitType;
  }

  public MemberCardOrderService()
  {

  }

  public MemberCardOrderService(MemberCardOrderServiceDTO memberCardOrderServiceDTO)
  {
    if(null != memberCardOrderServiceDTO)
    {
      this.setIncreasedTimes(memberCardOrderServiceDTO.getIncreasedTimes());
      this.setDeadline(memberCardOrderServiceDTO.getDeadline());
      this.setMemberCardOrderId(memberCardOrderServiceDTO.getMemberCardOrderId());
      this.setServiceId(memberCardOrderServiceDTO.getServiceId());
      this.setShopId(memberCardOrderServiceDTO.getShopId());
      this.setVehicles(memberCardOrderServiceDTO.getVehicles());
      this.setId(memberCardOrderServiceDTO.getId());
      this.setCardTimes(memberCardOrderServiceDTO.getCardTimes());
      this.setCardTimesLimitType(memberCardOrderServiceDTO.getCardTimesLimitType());
      this.setIncreasedTimesLimitType(memberCardOrderServiceDTO.getIncreasedTimesLimitType());
      this.setOldTimes(memberCardOrderServiceDTO.getOldTimes());
      this.setOldTimesLimitType(memberCardOrderServiceDTO.getOldTimesLimitType());
      this.setBalanceTimes(memberCardOrderServiceDTO.getBalanceTimes());
      this.setBalanceTimesLimitType(memberCardOrderServiceDTO.getBalanceTimesLimitType());
      this.setServiceHistoryId(memberCardOrderServiceDTO.getServiceHistoryId());
    }
  }

  public MemberCardOrderServiceDTO toDTO()
  {
    MemberCardOrderServiceDTO memberCardOrderServiceDTO = new MemberCardOrderServiceDTO();
    memberCardOrderServiceDTO.setId(this.getId());
    memberCardOrderServiceDTO.setShopId(this.getShopId());
    memberCardOrderServiceDTO.setServiceId(this.getServiceId());
    memberCardOrderServiceDTO.setIncreasedTimes(this.getIncreasedTimes());
    memberCardOrderServiceDTO.setVehicles(this.getVehicles());
    memberCardOrderServiceDTO.setDeadline(this.getDeadline());
    memberCardOrderServiceDTO.setCardTimes(this.getCardTimes());
    memberCardOrderServiceDTO.setBalanceTimes(this.getBalanceTimes());
    memberCardOrderServiceDTO.setOldTimes(this.getOldTimes());
    memberCardOrderServiceDTO.setIncreasedTimesLimitType(this.getIncreasedTimesLimitType());
    memberCardOrderServiceDTO.setCardTimesLimitType(this.getCardTimesLimitType());
    memberCardOrderServiceDTO.setOldTimesLimitType(this.getOldTimesLimitType());
    memberCardOrderServiceDTO.setBalanceTimesLimitType(this.getBalanceTimesLimitType());
    memberCardOrderServiceDTO.setServiceHistoryId(this.getServiceHistoryId());
    return memberCardOrderServiceDTO;
  }
}
