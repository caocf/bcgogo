package com.bcgogo.txn.dto;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.ServiceLimitTypes;
import com.bcgogo.search.dto.ItemIndexDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-4
 * Time: 上午11:38
 * To change this template use File | Settings | File Templates.
 */
public class MemberCardOrderServiceDTO {
  private Long id;
  private Long shopId;
  private Long memberCardOrderId;
  private Long serviceId;
  private String serviceIdStr;
  private Long serviceHistoryId;
  private Integer increasedTimes;
  private Integer term;
  private String vehicles;
  private String serviceName;

  private Long deadline;//过期时间
  private String deadlineStr;//过期时间格式YY-MM-DD
  private String oldDeadlineStr;//隐藏的合并前的服务失效时间
  private Integer addTerm;//把新增的日期隐藏的返回前台
  private Integer oldTimes;//原来次数
  private Integer balanceTimes;//剩余次数

  private int timesStatus;//0（有限）1（无限）
  private int deadlineStatus;//0（有限）1（无限）

  private Integer cardTimes;

  private ServiceLimitTypes increasedTimesLimitType;
  private ServiceLimitTypes cardTimesLimitType;
  private ServiceLimitTypes oldTimesLimitType;
  private ServiceLimitTypes balanceTimesLimitType;

  private String cardTimesStatus; //0或者null表示不是从card服务中带出来的，1表示是从卡服务中带出来的

  public Long getId() {
    return id;
  }

  public Long getShopId() {
    return shopId;
  }

  public Long getMemberCardOrderId() {
    return memberCardOrderId;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public Integer getIncreasedTimes() {
    return increasedTimes;
  }

  public Integer getTerm() {
    return term;
  }

  public String getVehicles() {
    return vehicles;
  }

  public int getTimesStatus() {
    return timesStatus;
  }

  public int getDeadlineStatus() {
    return deadlineStatus;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setMemberCardOrderId(Long memberCardOrderId) {
    this.memberCardOrderId = memberCardOrderId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
    if(null != serviceId)
    {
      this.serviceIdStr = serviceId.toString();
  }
  }

  public Long getServiceHistoryId() {
    return serviceHistoryId;
  }

  public void setServiceHistoryId(Long serviceHistoryId) {
    this.serviceHistoryId = serviceHistoryId;
  }

  public void setIncreasedTimes(Integer increasedTimes) {
    this.increasedTimes = increasedTimes;
  }

  public void setTerm(Integer term) {
    this.term = term;
  }

  public void setVehicles(String vehicles) {
    this.vehicles = vehicles;
  }

  public Long getDeadline() {
    return deadline;
  }

  public Integer getOldTimes() {
    return oldTimes;
  }

  public Integer getBalanceTimes() {
    return balanceTimes;
  }

  public Integer getCardTimes() {
    return cardTimes;
  }

  public ServiceLimitTypes getIncreasedTimesLimitType() {
    return increasedTimesLimitType;
  }

  public ServiceLimitTypes getCardTimesLimitType() {
    return cardTimesLimitType;
  }

  public ServiceLimitTypes getOldTimesLimitType() {
    return oldTimesLimitType;
  }

  public ServiceLimitTypes getBalanceTimesLimitType() {
    return balanceTimesLimitType;
  }

  public String getCardTimesStatus() {
    return cardTimesStatus;
  }

  public void setDeadline(Long deadline) {
    this.deadline = deadline;
  }

  public void setOldTimes(Integer oldTimes) {
    this.oldTimes = oldTimes;
  }

  public void setBalanceTimes(Integer balanceTimes) {
    this.balanceTimes = balanceTimes;
  }

  public String getDeadlineStr() {
    return deadlineStr;
  }

  public void setDeadlineStr(String deadlineStr) {
    this.deadlineStr = deadlineStr;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setTimesStatus(int timesStatus) {
    this.timesStatus = timesStatus;
  }

  public void setDeadlineStatus(int deadlineStatus) {
    this.deadlineStatus = deadlineStatus;
  }

  public void setCardTimes(Integer cardTimes) {
    this.cardTimes = cardTimes;
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

  public void setCardTimesStatus(String cardTimesStatus) {
    this.cardTimesStatus = cardTimesStatus;
  }

  public String getServiceIdStr() {
    return serviceIdStr;
  }

  public void setServiceIdStr(String serviceIdStr) {
    this.serviceIdStr = serviceIdStr;
  }

  public String getOldDeadlineStr() {
    return oldDeadlineStr;
  }

  public void setOldDeadlineStr(String oldDeadlineStr) {
    this.oldDeadlineStr = oldDeadlineStr;
  }

  public Integer getAddTerm() {
    return addTerm;
  }

  public void setAddTerm(Integer addTerm) {
    this.addTerm = addTerm;
  }

  public static Map<Long,MemberCardOrderServiceDTO> listToMap(List<MemberCardOrderServiceDTO> memberCardOrderServiceDTOs)
  {
    Map<Long,MemberCardOrderServiceDTO> memberCardOrderServiceDTOMap = new HashMap<Long, MemberCardOrderServiceDTO>();
    if(null == memberCardOrderServiceDTOs)
    {
      return memberCardOrderServiceDTOMap;
    }

    for(MemberCardOrderServiceDTO memberCardOrderServiceDTO : memberCardOrderServiceDTOs)
    {
      if(null != memberCardOrderServiceDTO.getServiceId())
      {
        memberCardOrderServiceDTOMap.put(memberCardOrderServiceDTO.getServiceId(),memberCardOrderServiceDTO);
      }
    }

    return memberCardOrderServiceDTOMap;
  }

  public ItemIndexDTO toItemIndexDTO(MemberCardOrderDTO memberCardOrderDTO){
    ItemIndexDTO itemIndexDTO= new ItemIndexDTO();
    itemIndexDTO.setItemId(memberCardOrderDTO.getId());
    itemIndexDTO.setItemType(ItemTypes.SALE_MEMBER_CARD_SERVICE);

    itemIndexDTO.setItemName(this.getServiceName());
    itemIndexDTO.setOldTimes(this.getOldTimes());
    itemIndexDTO.setBalanceTimes(this.getBalanceTimes());
    itemIndexDTO.setIncreasedTimes(this.getIncreasedTimes());
    itemIndexDTO.setVehicles(this.getVehicles());
    itemIndexDTO.setDeadline(this.getDeadline());
    itemIndexDTO.setIncreasedTimesLimitType(this.getIncreasedTimesLimitType());
    return itemIndexDTO;
  }
}
