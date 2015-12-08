package com.bcgogo.txn.dto;


import com.bcgogo.enums.ConsumeType;
import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-12
 * Time: 上午9:59
 * To change this template use File | Settings | File Templates.
 */
public class WashBeautyOrderItemDTO extends BcgogoOrderItemDto {
  private Long washBeautyOrderId;
  private Long serviceId;
  private String serviceIdStr;
  private Long serviceHistoryId;
  private ConsumeType payType;
  private Double price;
  private String priceStr;
  private String memo;
  private Long shopId;
  private Double percentage;
  private Double percentageAmount;
  private String salesManIds;
  private String salesMan;
  private String surplusTimes;
  private ConsumeType consumeTypeStr;
  private String consumeTypeName;
  private String serviceName;
  private String salesManNames;
  private Integer memberServiceTime;
  private List<SalesManDTO> salesManDTOList;
  private Long businessCategoryId;
  private String businessCategoryName;
  private String couponType;
  private String couponNo;

  public String getSalesManNamesFromSalesManDTOList() {
    if (CollectionUtils.isEmpty(salesManDTOList)) return null;
    salesManNames = "";
    for (SalesManDTO salesManDTO : salesManDTOList) {
      salesManNames += salesManDTO.getName() + ",";
  }
    salesManNames = StringUtils.substring(salesManNames, 0, salesManNames.length()-1);
    return salesManNames;
  }


  public ConsumeType getConsumeTypeStr() {
    return consumeTypeStr;
  }

  public void setConsumeTypeStr(ConsumeType consumeTypeStr) {
    this.consumeTypeStr = consumeTypeStr;
    this.consumeTypeName=consumeTypeStr==null?"":consumeTypeStr.getType();
  }

  public String getConsumeTypeName() {
    return consumeTypeName;
  }

  public void setConsumeTypeName(String consumeTypeName) {
    this.consumeTypeName = consumeTypeName;
  }

  public String getSurplusTimes() {
    return surplusTimes;
  }

  public void setSurplusTimes(String surplusTimes) {
    this.surplusTimes = surplusTimes;
  }

  public String getSalesManIds() {
    return salesManIds;
  }

  public void setSalesManIds(String salesManIds) {
    this.salesManIds = salesManIds;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getWashBeautyOrderId() {
    return washBeautyOrderId;
  }

  public void setWashBeautyOrderId(Long washBeautyOrderId) {
    this.washBeautyOrderId = washBeautyOrderId;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
    this.serviceIdStr= StringUtil.valueOf(serviceId);
  }

  public String getServiceIdStr() {
    return serviceIdStr;
  }

  public void setServiceIdStr(String serviceIdStr) {
    this.serviceIdStr = serviceIdStr;
  }

  public Long getServiceHistoryId() {
    return serviceHistoryId;
  }

  public void setServiceHistoryId(Long serviceHistoryId) {
    this.serviceHistoryId = serviceHistoryId;
  }

  public ConsumeType getPayType() {
    return payType;
  }

  public void setPayType(ConsumeType payType) {
    this.payType = payType;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getPriceStr() {
    return priceStr;
  }

  public void setPriceStr(String priceStr) {
    this.priceStr = priceStr;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Double getPercentage() {
    return percentage;
  }

  public void setPercentage(Double percentage) {
    this.percentage = percentage;
  }

  public Double getPercentageAmount() {
    return percentageAmount;
  }

  public void setPercentageAmount(Double percentageAmount) {
    this.percentageAmount = percentageAmount;
  }

  public Integer getMemberServiceTime() {
    return memberServiceTime;
  }

  public void setMemberServiceTime(Integer memberServiceTime) {
    this.memberServiceTime = memberServiceTime;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public List<SalesManDTO> getSalesManDTOList() {
    return salesManDTOList;
  }

  public void setSalesManDTOList(List<SalesManDTO> salesManDTOList) {
    this.salesManDTOList = salesManDTOList;
  }

  public String getSalesMan() {
    return salesMan;
  }

  public void setSalesMan(String salesMan) {
    this.salesMan = salesMan;
  }

  public String getSalesManNames() {
    return salesManNames;
  }

  public void setSalesManNames(String salesManNames) {
    this.salesManNames = salesManNames;
  }

  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

  public String getCouponType() {
    return couponType;
  }

  public void setCouponType(String couponType) {
    this.couponType = couponType;
  }

  public String getCouponNo() {
    return couponNo;
  }

  public void setCouponNo(String couponNo) {
    this.couponNo = couponNo;
  }

  public ItemIndexDTO toItemIndexDTO(WashBeautyOrderDTO washBeautyOrderDTO) {
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();
    itemIndexDTO.setShopId(washBeautyOrderDTO.getShopId());
    itemIndexDTO.setCustomerId(washBeautyOrderDTO.getCustomerId());
    itemIndexDTO.setCustomerOrSupplierName(washBeautyOrderDTO.getCustomer());
    itemIndexDTO.setCustomerOrSupplierStatus(washBeautyOrderDTO.getCustomerStatus()==null? CustomerStatus.ENABLED.toString():washBeautyOrderDTO.getCustomerStatus().toString());
    itemIndexDTO.setOrderReceiptNo(washBeautyOrderDTO.getReceiptNo());
    itemIndexDTO.setOrderStatus(washBeautyOrderDTO.getStatus());
    itemIndexDTO.setOrderTimeCreated(washBeautyOrderDTO.getVestDate() == null ? washBeautyOrderDTO.getCreationDate() : washBeautyOrderDTO.getVestDate());
    itemIndexDTO.setVehicle(washBeautyOrderDTO.getVechicle());
    itemIndexDTO.setOrderId(washBeautyOrderDTO.getId());
    itemIndexDTO.setItemPrice(this.getPrice());
    itemIndexDTO.setItemCount(1d);
    if(getPayType()==null || !ConsumeType.MONEY.equals(getPayType())){
        itemIndexDTO.setItemTotalAmount(0d);
    }else{
        itemIndexDTO.setItemTotalAmount(this.getPrice());
    }
    itemIndexDTO.setConsumeType(this.getPayType());
    itemIndexDTO.setItemName(this.getServiceName());
    itemIndexDTO.setServices(this.getServiceName());
    itemIndexDTO.setItemType(ItemTypes.SERVICE);
    itemIndexDTO.setServiceId(this.getServiceId());
    itemIndexDTO.setItemMemo(this.getMemo());
    itemIndexDTO.setOrderType(OrderTypes.WASH_BEAUTY);
    itemIndexDTO.setServiceWorker(this.getSalesMan());//服务人员
    itemIndexDTO.setBusinessCategoryId(this.getBusinessCategoryId());
    itemIndexDTO.setBusinessCategoryName(this.getBusinessCategoryName());
    itemIndexDTO.setCouponType(getCouponType());
    return itemIndexDTO;
  }

  public void fromServiceDTO(ServiceDTO serviceDTO) {
    if (serviceDTO == null) {
      return;
    }
    this.setServiceId(serviceDTO.getId());
    this.setServiceName(serviceDTO.getName());
    this.setPrice(NumberUtil.doubleVal(serviceDTO.getPrice()));
    this.setBusinessCategoryId(serviceDTO.getCategoryId());
    this.setBusinessCategoryName(serviceDTO.getCategoryName());
  }

  public void setServiceHistoryDTO(ServiceHistoryDTO serviceHistory){
    if (serviceHistory == null) {
      return;
    }
    this.setServiceId(serviceHistory.getId());
    this.setServiceName(serviceHistory.getName());
    this.setPrice(NumberUtil.doubleVal(serviceHistory.getPrice()));
  }

}