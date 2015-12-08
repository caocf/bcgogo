package com.bcgogo.txn.model.assistantStat;

import com.bcgogo.enums.assistantStat.AchievementType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.assistantStat.ServiceAchievementHistoryDTO;

import javax.persistence.*;

/**
 * 会员业绩统计-施工服务员工业绩提成记录表
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:05
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "service_achievement_history")
public class ServiceAchievementHistory extends LongIdentifier {

  private Long shopId;
  private Long serviceId;
  private String serviceName;
  private AchievementType achievementType;//提成方式 按金额 按比率
  private Double achievementAmount;//提成数额
  private Long categoryId;//营业分类
  private String categoryName;//营业分类
  private Double standardHours;//标准工时
  private Double standardUnitPrice;//标准工时费（单价）
  private Long changeTime;//更改时间
  private Long changeUserId;//更改时用户id


  public ServiceAchievementHistoryDTO toDTO() {
    ServiceAchievementHistoryDTO serviceAchievementHistoryDTO = new ServiceAchievementHistoryDTO();
    serviceAchievementHistoryDTO.setId(getId());
    serviceAchievementHistoryDTO.setShopId(getShopId());
    serviceAchievementHistoryDTO.setServiceId(getServiceId());
    serviceAchievementHistoryDTO.setServiceName(getServiceName());
    serviceAchievementHistoryDTO.setAchievementType(getAchievementType());
    serviceAchievementHistoryDTO.setAchievementAmount(getAchievementAmount());
    serviceAchievementHistoryDTO.setCategoryId(getCategoryId());
    serviceAchievementHistoryDTO.setCategoryName(getCategoryName());
    serviceAchievementHistoryDTO.setStandardHours(getStandardHours());
    serviceAchievementHistoryDTO.setStandardUnitPrice(getStandardUnitPrice());
    serviceAchievementHistoryDTO.setChangeTime(getChangeTime());
    serviceAchievementHistoryDTO.setChangeUserId(getChangeUserId());
    return serviceAchievementHistoryDTO;
  }


  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "service_id")
  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  @Column(name = "service_name")
  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  @Column(name = "achievement_type")
  @Enumerated(EnumType.STRING)
  public AchievementType getAchievementType() {
    return achievementType;
  }

  public void setAchievementType(AchievementType achievementType) {
    this.achievementType = achievementType;
  }

  @Column(name = "achievement_amount")
  public Double getAchievementAmount() {
    return achievementAmount;
  }

  public void setAchievementAmount(Double achievementAmount) {
    this.achievementAmount = achievementAmount;
  }

  @Column(name = "category_id")
  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  @Column(name = "category_name")
  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  @Column(name = "standard_hours")
  public Double getStandardHours() {
    return standardHours;
  }

  public void setStandardHours(Double standardHours) {
    this.standardHours = standardHours;
  }

  @Column(name = "standard_unit_price")
  public Double getStandardUnitPrice() {
    return standardUnitPrice;
  }

  public void setStandardUnitPrice(Double standardUnitPrice) {
    this.standardUnitPrice = standardUnitPrice;
  }

  @Column(name = "change_time")
  public Long getChangeTime() {
    return changeTime;
  }

  public void setChangeTime(Long changeTime) {
    this.changeTime = changeTime;
  }

  @Column(name = "change_user_id")
  public Long getChangeUserId() {
    return changeUserId;
  }

  public void setChangeUserId(Long changeUserId) {
    this.changeUserId = changeUserId;
  }
}
