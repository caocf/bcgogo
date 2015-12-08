package com.bcgogo.txn.model;

import com.bcgogo.enums.ServiceStatus;
import com.bcgogo.enums.ServiceTimeType;
import com.bcgogo.enums.assistantStat.AchievementType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.ServiceDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-19
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "service")
public class Service extends LongIdentifier {
  public Service() {
  }

  public Service(ServiceDTO serviceDTO) {
    if (serviceDTO != null) {
      this.setMemo(serviceDTO.getMemo());
      this.setName(serviceDTO.getName());
      this.setPercentage(serviceDTO.getPercentage());
      this.setPercentageAmount(serviceDTO.getPercentageAmount());
      this.setPointsExchangeable(serviceDTO.getPointsExchangeable());
      this.setPrice(serviceDTO.getPrice());
      this.setShopId(serviceDTO.getShopId());
      this.setTimeType(serviceDTO.getTimeType());
      if (serviceDTO.getId() != null) {
        this.setId(serviceDTO.getId());
      }

      this.setStandardHours(serviceDTO.getStandardHours());
      this.setStandardUnitPrice(serviceDTO.getStandardUnitPrice());
      this.setAchievementAmount(serviceDTO.getAchievementAmount());
      this.setAchievementType(serviceDTO.getAchievementType());
      this.setUseTimes(NumberUtil.longValue(serviceDTO.getUseTimes()));
    }
  }

  public Service(Long shopId, String name, Double price, ServiceStatus status,Double standardHours,Double standardUnitPrice) {
    this.shopId = shopId;
    this.name = name;
    this.price = price;
    this.status = status;
    this.standardHours =standardHours;
    this.standardUnitPrice = standardUnitPrice;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "name", length = 200)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Column(name = "memo", length = 100)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  private Long shopId;
  private String name;
  private Double price;
  private String memo;
  private Double percentage;
  private Double percentageAmount;
  private String pointsExchangeable;
  private ServiceStatus status;
  private ServiceTimeType timeType;

  private Double standardHours;//标准工时
  private Double standardUnitPrice;//标准工时单价
  private AchievementType achievementType;//提成类型
  private Double achievementAmount;//提成金额

  private Long useTimes;//使用次数

  @Column(name = "percentage")
  public Double getPercentage() {
    return percentage;
  }

  @Column(name = "percentage_amount")
  public Double getPercentageAmount() {
    return percentageAmount;
  }

  @Column(name = "points_exchangeable")
  public String getPointsExchangeable() {
    return pointsExchangeable;
  }

  public void setPercentage(Double percentage) {
    this.percentage = percentage;
  }

  public void setPercentageAmount(Double percentageAmount) {
    this.percentageAmount = percentageAmount;
  }

  public void setPointsExchangeable(String pointsExchangeable) {
    this.pointsExchangeable = pointsExchangeable;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public ServiceStatus getStatus() {
    return status;
  }

  public void setStatus(ServiceStatus status) {
    this.status = status;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "time_type")
  public ServiceTimeType getTimeType() {
    return timeType;
  }

  public void setTimeType(ServiceTimeType timeType) {
    this.timeType = timeType;
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


  @Column(name = "use_times")
  public Long getUseTimes() {
    return useTimes;
  }

  public void setUseTimes(Long useTimes) {
    this.useTimes = useTimes;
  }


  public void fromDTO(ServiceDTO serviceDTO) {
    if (serviceDTO != null) {
      this.setMemo(serviceDTO.getMemo());
      this.setName(serviceDTO.getName());
      this.setPercentage(serviceDTO.getPercentage());
      this.setPercentageAmount(serviceDTO.getPercentageAmount());
      this.setPointsExchangeable(serviceDTO.getPointsExchangeable());
      this.setPrice(serviceDTO.getPrice());
      this.setShopId(serviceDTO.getShopId());
      this.setStatus(serviceDTO.getStatus());
      this.setTimeType(serviceDTO.getTimeType());
      if (serviceDTO.getId() != null) {
        this.setId(serviceDTO.getId());
      }
      this.setStandardHours(serviceDTO.getStandardHours());
      this.setStandardUnitPrice(serviceDTO.getStandardUnitPrice());
      this.setAchievementType(serviceDTO.getAchievementType());
      this.setAchievementAmount(serviceDTO.getAchievementAmount());
      this.setUseTimes(NumberUtil.longValue(serviceDTO.getUseTimes()));

    }
  }

  public void updateByDTO(ServiceDTO serviceDTO) {
      if (serviceDTO != null) {
        this.setName(serviceDTO.getName());
        this.setPrice(serviceDTO.getPrice());
        this.setStandardHours(serviceDTO.getStandardHours());
        this.setStandardUnitPrice(serviceDTO.getStandardUnitPrice());
        this.setPrice(NumberUtil.round(NumberUtil.doubleVal(serviceDTO.getStandardHours())*NumberUtil.doubleVal(serviceDTO.getStandardUnitPrice()),2));
        this.setAchievementType(serviceDTO.getAchievementType());
        this.setAchievementAmount(serviceDTO.getAchievementAmount());
        this.setUseTimes(NumberUtil.longValue(serviceDTO.getUseTimes()));
      }
    }


  public ServiceDTO toDTO() {
    ServiceDTO serviceDTO = new ServiceDTO();
    serviceDTO.setId(this.getId());
    serviceDTO.setIdStr(this.getId() == null ? "" : this.getId().toString());
    serviceDTO.setMemo(this.getMemo());
    serviceDTO.setName(this.getName());
    serviceDTO.setPercentage(this.getPercentage());
    serviceDTO.setPercentageAmount(this.getPercentageAmount());
    serviceDTO.setPointsExchangeable(this.getPointsExchangeable());
    serviceDTO.setPrice(this.getPrice());
    serviceDTO.setShopId(this.getShopId());
    serviceDTO.setStatus(this.getStatus());
    serviceDTO.setTimeType(this.getTimeType());
    serviceDTO.setVersion(getVersion());

    serviceDTO.setAchievementAmount(getAchievementAmount());
    serviceDTO.setAchievementType(getAchievementType());
    serviceDTO.setStandardHours(getStandardHours());
    serviceDTO.setStandardUnitPrice(getStandardUnitPrice());
    serviceDTO.setUseTimes(NumberUtil.longValue(getUseTimes()));
    return serviceDTO;
  }
}