package com.bcgogo.txn.model;

import com.bcgogo.enums.ConsumeType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.WashBeautyOrderItemDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-12
 * Time: 上午9:50
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "wash_beauty_order_item")
public class WashBeautyOrderItem extends LongIdentifier {
  private Long washBeautyOrderId;
  private Long serviceId;
  private Long serviceHistoryId;
  private ConsumeType payType;
  private Double price;
  private String memo;
  private Double reserved;
  private Long shopId;
  private Double percentage;
  private Double percentageAmount;
  private String salesManIds;
  private String salesMan;
  private Long businessCategoryId;
  private String businessCategoryName;

  private String couponType;
  private String couponNo;

  public WashBeautyOrderItem() {

  }

  @Column(name = "wash_beauty_order_id")
  public Long getWashBeautyOrderId() {
    return washBeautyOrderId;
  }

  public void setWashBeautyOrderId(Long washBeautyOrderId) {
    this.washBeautyOrderId = washBeautyOrderId;
  }

  @Column(name = "service_id")
  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  @Column(name="service_history_id")
  public Long getServiceHistoryId() {
    return serviceHistoryId;
  }

  public void setServiceHistoryId(Long serviceHistoryId) {
    this.serviceHistoryId = serviceHistoryId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "pay_type", length = 20)
  public ConsumeType getPayType() {
    return payType;
  }

  public void setPayType(ConsumeType payType) {
    this.payType = payType;
  }

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "reserved")
  public Double getReserved() {
    return reserved;
  }

  public void setReserved(Double reserved) {
    this.reserved = reserved;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "percentage")
  public Double getPercentage() {
    return percentage;
  }

  public void setPercentage(Double percentage) {
    this.percentage = percentage;
  }

  @Column(name = "percentage_amount")
  public Double getPercentageAmount() {
    return percentageAmount;
  }

  public void setPercentageAmount(Double percentageAmount) {
    this.percentageAmount = percentageAmount;
  }

  @Column(name = "sales_man_ids")
  public String getSalesManIds() {
    return salesManIds;
  }

  public void setSalesManIds(String salesManIds) {
    this.salesManIds = salesManIds;
  }

  @Column(name = "sales_man")
  public String getSalesMan() {
    return salesMan;
  }

  public void setSalesMan(String salesMan) {
    this.salesMan = salesMan;
  }

  @Column(name="business_category_id")
  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  @Column(name="business_category_name")
  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

  @Column(name="coupon_type")
  public String getCouponType() {
    return couponType;
  }

  public void setCouponType(String couponType) {
    this.couponType = couponType;
  }

  @Column(name="coupon_no")
  public String getCouponNo() {
    return couponNo;
  }

  public void setCouponNo(String couponNo) {
    this.couponNo = couponNo;
  }

  public WashBeautyOrderItemDTO toDTO() {
    WashBeautyOrderItemDTO washBeautyOrderItemDTO = new WashBeautyOrderItemDTO();
    washBeautyOrderItemDTO.setId(this.getId());
    washBeautyOrderItemDTO.setMemo(this.getMemo());
    washBeautyOrderItemDTO.setPayType(this.getPayType());
    washBeautyOrderItemDTO.setPercentage(this.getPercentage());
    washBeautyOrderItemDTO.setPercentageAmount(this.getPercentageAmount());
    washBeautyOrderItemDTO.setPrice(this.getPrice());
    washBeautyOrderItemDTO.setReserved(this.getReserved());
    washBeautyOrderItemDTO.setServiceId(this.getServiceId());
    washBeautyOrderItemDTO.setShopId(this.getShopId());
    washBeautyOrderItemDTO.setWashBeautyOrderId(this.getWashBeautyOrderId());
    washBeautyOrderItemDTO.setSalesManIds(this.getSalesManIds());
    washBeautyOrderItemDTO.setSalesMan(this.getSalesMan());
    washBeautyOrderItemDTO.setConsumeTypeStr(this.getPayType());
    washBeautyOrderItemDTO.setBusinessCategoryId(this.getBusinessCategoryId());
    washBeautyOrderItemDTO.setBusinessCategoryName(this.getBusinessCategoryName());
    washBeautyOrderItemDTO.setServiceHistoryId(getServiceHistoryId());
    washBeautyOrderItemDTO.setCouponNo(getCouponNo());
    washBeautyOrderItemDTO.setCouponType(getCouponType());
    return washBeautyOrderItemDTO;
  }

  public void fromDTO(WashBeautyOrderItemDTO washBeautyOrderItemDTO) {
    if (washBeautyOrderItemDTO != null) {
      this.setServiceId(washBeautyOrderItemDTO.getServiceId());
      this.setShopId(washBeautyOrderItemDTO.getShopId());
      this.setWashBeautyOrderId(washBeautyOrderItemDTO.getWashBeautyOrderId());
      this.setMemo(washBeautyOrderItemDTO.getMemo());
      this.setPayType(washBeautyOrderItemDTO.getPayType());
      this.setPercentage(washBeautyOrderItemDTO.getPercentage());
      this.setPercentageAmount(washBeautyOrderItemDTO.getPercentageAmount());
      this.setPrice(washBeautyOrderItemDTO.getPrice());
      this.setReserved(washBeautyOrderItemDTO.getReserved());
      this.setSalesManIds(washBeautyOrderItemDTO.getSalesManIds());
      this.setSalesMan(washBeautyOrderItemDTO.getSalesMan());
      this.setPayType(washBeautyOrderItemDTO.getConsumeTypeStr());
      this.setBusinessCategoryId(washBeautyOrderItemDTO.getBusinessCategoryId());
      this.setBusinessCategoryName(washBeautyOrderItemDTO.getBusinessCategoryName());
      this.setServiceHistoryId(washBeautyOrderItemDTO.getServiceHistoryId());
      this.setCouponNo(washBeautyOrderItemDTO.getCouponNo());
      this.setCouponType(washBeautyOrderItemDTO.getCouponType());
      if(washBeautyOrderItemDTO.getId() != null){
        this.setId(washBeautyOrderItemDTO.getId());
      }
    }
  }

  public WashBeautyOrderItem(WashBeautyOrderItemDTO washBeautyOrderItemDTO) {
    if (washBeautyOrderItemDTO != null) {
      this.setServiceId(washBeautyOrderItemDTO.getServiceId());
      this.setShopId(washBeautyOrderItemDTO.getShopId());
      this.setWashBeautyOrderId(washBeautyOrderItemDTO.getWashBeautyOrderId());
      this.setMemo(washBeautyOrderItemDTO.getMemo());
      this.setPayType(washBeautyOrderItemDTO.getConsumeTypeStr());
      this.setPercentage(washBeautyOrderItemDTO.getPercentage());
      this.setPercentageAmount(washBeautyOrderItemDTO.getPercentageAmount());
      this.setPrice(NumberUtil.doubleVal(washBeautyOrderItemDTO.getPrice()));
      this.setReserved(washBeautyOrderItemDTO.getReserved());
      this.setSalesManIds(washBeautyOrderItemDTO.getSalesManIds());
      this.setSalesMan(washBeautyOrderItemDTO.getSalesMan());
      this.setPayType(washBeautyOrderItemDTO.getConsumeTypeStr());
      this.setBusinessCategoryId(washBeautyOrderItemDTO.getBusinessCategoryId());
      this.setBusinessCategoryName(washBeautyOrderItemDTO.getBusinessCategoryName());
      this.setServiceHistoryId(washBeautyOrderItemDTO.getServiceHistoryId());
      this.setCouponType(washBeautyOrderItemDTO.getCouponType());
      this.setCouponNo(washBeautyOrderItemDTO.getCouponNo());
    }
  }

}
