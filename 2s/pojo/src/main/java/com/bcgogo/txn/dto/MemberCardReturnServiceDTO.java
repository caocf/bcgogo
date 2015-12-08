package com.bcgogo.txn.dto;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.ServiceLimitTypes;
import com.bcgogo.search.dto.ItemIndexDTO;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-4
 * Time: 上午11:38
 * To change this template use File | Settings | File Templates.
 */
public class MemberCardReturnServiceDTO {
  private Long id;

  private Long shopId;
  private Long memberCardReturnId;
  private Long serviceId;
  private String serviceName;
  private Long serviceHistoryId;
  private String vehicles;
  private Integer lastBuyTimes;
  private Integer usedTimes;
  private Integer remainTimes;
  private ServiceLimitTypes lastBuyTimesLimitType;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getMemberCardReturnId() {
    return memberCardReturnId;
  }

  public void setMemberCardReturnId(Long memberCardReturnId) {
    this.memberCardReturnId = memberCardReturnId;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public Long getServiceHistoryId() {
    return serviceHistoryId;
  }

  public void setServiceHistoryId(Long serviceHistoryId) {
    this.serviceHistoryId = serviceHistoryId;
  }

  public String getVehicles() {
    return vehicles;
  }

  public void setVehicles(String vehicles) {
    this.vehicles = vehicles;
  }

  public Integer getLastBuyTimes() {
    return lastBuyTimes;
  }

  public void setLastBuyTimes(Integer lastBuyTimes) {
    this.lastBuyTimes = lastBuyTimes;
  }

  public Integer getRemainTimes() {
    return remainTimes;
  }

  public void setRemainTimes(Integer remainTimes) {
    this.remainTimes = remainTimes;
  }

  public Integer getUsedTimes() {
    return usedTimes;
  }

  public void setUsedTimes(Integer usedTimes) {
    this.usedTimes = usedTimes;
  }

  public ServiceLimitTypes getLastBuyTimesLimitType() {
    return lastBuyTimesLimitType;
  }

  public void setLastBuyTimesLimitType(ServiceLimitTypes lastBuyTimesLimitType) {
    this.lastBuyTimesLimitType = lastBuyTimesLimitType;
  }

  public ItemIndexDTO toItemIndexDTO(MemberCardReturnDTO memberCardReturnDTO){
    ItemIndexDTO itemIndexDTO= new ItemIndexDTO();
    itemIndexDTO.setItemId(memberCardReturnDTO.getId());
    itemIndexDTO.setItemName(this.getServiceName());
    itemIndexDTO.setOldTimes(this.getLastBuyTimes());
    itemIndexDTO.setBalanceTimes(this.getRemainTimes());
    itemIndexDTO.setItemType(ItemTypes.SALE_MEMBER_CARD_SERVICE);
    return itemIndexDTO;
  }
}
