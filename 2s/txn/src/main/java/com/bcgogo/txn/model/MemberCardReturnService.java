package com.bcgogo.txn.model;

import com.bcgogo.enums.ServiceLimitTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.MemberCardReturnServiceDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-10-16
 * Time: 上午10:08
 */
@Entity
@Table(name = "member_card_return_service")
public class MemberCardReturnService extends LongIdentifier {
  private Long shopId;
  private Long memberCardReturnId;
  private Long serviceId;
  private Long serviceHistoryId;
  private Integer lastBuyTimes;
  private Integer usedTimes;
  private Integer remainTimes;
  private ServiceLimitTypes lastBuyTimesLimitType;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  @Column(name = "member_card_return_id")
  public Long getMemberCardReturnId() {
    return memberCardReturnId;
  }

  @Column(name = "service_id")
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

  @Column(name = "remain_times")
  public Integer getRemainTimes() {
    return remainTimes;
  }

  public void setRemainTimes(Integer remainTimes) {
    this.remainTimes = remainTimes;
  }

  @Column(name = "used_times")
  public Integer getUsedTimes() {
    return usedTimes;
  }

  public void setUsedTimes(Integer usedTimes) {
    this.usedTimes = usedTimes;
  }

  @Column(name = "last_buy_times")
  public Integer getLastBuyTimes() {
    return lastBuyTimes;
  }

  public void setLastBuyTimes(Integer lastBuyTimes) {
    this.lastBuyTimes = lastBuyTimes;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "last_buy_times_limit_type")
  public ServiceLimitTypes getLastBuyTimesLimitType() {
    return lastBuyTimesLimitType;
  }

  public void setLastBuyTimesLimitType(ServiceLimitTypes lastBuyTimesLimitType) {
    this.lastBuyTimesLimitType = lastBuyTimesLimitType;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setMemberCardReturnId(Long memberCardReturnId) {
    this.memberCardReturnId = memberCardReturnId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public MemberCardReturnService() {
  }

  public MemberCardReturnService(MemberCardReturnServiceDTO memberCardReturnServiceDTO) {
    this.shopId = memberCardReturnServiceDTO.getShopId();
    this.memberCardReturnId = memberCardReturnServiceDTO.getMemberCardReturnId();
    this.serviceId = memberCardReturnServiceDTO.getServiceId();
    this.lastBuyTimes = memberCardReturnServiceDTO.getLastBuyTimes();
    this.remainTimes = memberCardReturnServiceDTO.getRemainTimes();
    this.usedTimes = memberCardReturnServiceDTO.getUsedTimes();
    this.lastBuyTimesLimitType = memberCardReturnServiceDTO.getLastBuyTimesLimitType();
    this.serviceHistoryId = memberCardReturnServiceDTO.getServiceHistoryId();
  }

  public MemberCardReturnServiceDTO toDTO() {
    MemberCardReturnServiceDTO memberCardReturnServiceDTO = new MemberCardReturnServiceDTO();
    memberCardReturnServiceDTO.setShopId(shopId);
    memberCardReturnServiceDTO.setMemberCardReturnId(memberCardReturnId);
    memberCardReturnServiceDTO.setServiceId(serviceId);
    memberCardReturnServiceDTO.setLastBuyTimes(lastBuyTimes);
    memberCardReturnServiceDTO.setRemainTimes(remainTimes);
    memberCardReturnServiceDTO.setUsedTimes(usedTimes);
    memberCardReturnServiceDTO.setLastBuyTimesLimitType(lastBuyTimesLimitType);
    memberCardReturnServiceDTO.setServiceHistoryId(serviceHistoryId);
    return memberCardReturnServiceDTO;
  }
}
