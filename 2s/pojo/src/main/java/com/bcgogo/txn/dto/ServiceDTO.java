package com.bcgogo.txn.dto;

import com.bcgogo.enums.CategoryType;
import com.bcgogo.enums.MemberStatus;
import com.bcgogo.enums.ServiceStatus;
import com.bcgogo.enums.ServiceTimeType;
import com.bcgogo.enums.assistantStat.AchievementType;
import com.bcgogo.user.dto.MemberServiceDTO;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.ArrayUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-1-6
 * Time: 上午10:56
 * To change this template use File | Settings | File Templates.
 */
public class ServiceDTO implements Serializable {
  public ServiceDTO() {
  }

  private Long id;
  private Long version;
  private String idStr;
  private Long shopId;
  private String name;
  private Double price;
  private String memo;
  private Double percentage;
  private Double percentageAmount;
  private String pointsExchangeable;
  private String surplusTimes;
  private String categoryName;
  private CategoryType categoryType;
  private Long categoryId;
  private String light;
  private String priceStr;
  private String percentageAmountStr;
  private ServiceStatus status;
  private ServiceTimeType timeType;

  private Double standardHours;//标准工时
  private Double standardUnitPrice;//标准工时单价
  private AchievementType achievementType;//提成类型
  private String achievementTypeStr;//提成类型
  private Double achievementAmount;//提成金额

  private Long useTimes;//使用次数

  public Double getAchievementAmount() {
    return achievementAmount;
  }

  public void setAchievementAmount(Double achievementAmount) {
    this.achievementAmount = achievementAmount;
  }

  public String getLight() {
    return light;
  }

  public void setLight(String light) {
    this.light = light;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public void setCategoryDTO(CategoryDTO categoryDTO){
    if(categoryDTO==null) return;
    this.setCategoryId(categoryDTO.getId());
    this.setCategoryName(categoryDTO.getCategoryName());
    this.setCategoryType(categoryDTO.getCategoryType());
  }
  public CategoryType getCategoryType() {
    return categoryType;
  }

  public void setCategoryType(CategoryType categoryType) {
    this.categoryType = categoryType;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public String getSurplusTimes() {
    return surplusTimes;
  }

  public void setSurplusTimes(String surplusTimes) {
    this.surplusTimes = surplusTimes;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.idStr = String.valueOf(id);
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;

    this.priceStr = StringUtil.doubleToString(price,2, "");
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Double getPercentage() {
      return percentage;
  }

  public Double getPercentageAmount() {
      return percentageAmount;
  }

  public String getPointsExchangeable() {
      return pointsExchangeable;
  }

  public void setPercentage(Double percentage) {
      this.percentage = percentage;
  }

  public void setPercentageAmount(Double percentageAmount) {
      this.percentageAmount = percentageAmount;
    this.percentageAmountStr = StringUtil.doubleToString(percentageAmount,2, "");
  }

  public void setPointsExchangeable(String pointsExchangeable) {
      this.pointsExchangeable = pointsExchangeable;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getIdStr() {
    return idStr;
  }

  public String getPriceStr() {
    return priceStr;
  }

  public void setPriceStr(String priceStr) {
    this.priceStr = priceStr;
  }

  public String getPercentageAmountStr() {
    return percentageAmountStr;
  }

  public void setPercentageAmountStr(String percentageAmountStr) {
    this.percentageAmountStr = percentageAmountStr;
  }

  public ServiceStatus getStatus() {
    return status;
  }

  public void setStatus(ServiceStatus status) {
    this.status = status;
  }

  public ServiceTimeType getTimeType() {
    return timeType;
  }

  public void setTimeType(ServiceTimeType timeType) {
    this.timeType = timeType;
  }

  /**
   * 把洗车放最前；
   */
  public static ServiceDTO[] toSort(ServiceDTO[] serviceDTOs) {
    if (ArrayUtils.isEmpty(serviceDTOs)) {
      return null;
    }
    List<ServiceDTO> serviceDTOList = Arrays.asList(serviceDTOs);
    Collections.sort(serviceDTOList, new Comparator<ServiceDTO>() {
      public int compare(ServiceDTO arg0, ServiceDTO arg1) {
        if (arg0.getName().indexOf("洗车") != -1 && arg1.getName().indexOf("洗车") != -1) {
          if (arg0.getName().indexOf("洗车") > arg1.getName().indexOf("洗车")) {
            return 1;
          } else if (arg0.getName().indexOf("洗车") < arg1.getName().indexOf("洗车")) {
            return -1;
          } else {
            if (arg0.getName().length() > arg1.getName().length()) {
              return 1;
            } else if (arg0.getName().length() < arg1.getName().length()) {
              return -1;
            } else {
              return 0;
      }
    }
        } else if (arg0.getName().indexOf("洗车") == -1 && arg1.getName().indexOf("洗车") != -1) {
          return 1;
        } else {
          return -1;
  }
      }
    });
    return serviceDTOList.toArray(new ServiceDTO[serviceDTOList.size()]);
  }

  public Double getStandardHours() {
    return standardHours;
  }

  public void setStandardHours(Double standardHours) {
    this.standardHours = standardHours;
  }

  public Double getStandardUnitPrice() {
    return standardUnitPrice;
  }

  public void setStandardUnitPrice(Double standardUnitPrice) {
    this.standardUnitPrice = standardUnitPrice;
  }

  public AchievementType getAchievementType() {
    return achievementType;
  }

  public void setAchievementType(AchievementType achievementType) {
    this.achievementType = achievementType;
    if(achievementType != null){
      this.setAchievementTypeStr(achievementType.getName());
    }else{
      this.setAchievementTypeStr("");
    }
  }

  public String getAchievementTypeStr() {
    return achievementTypeStr;
  }

  public void setAchievementTypeStr(String achievementTypeStr) {
    this.achievementTypeStr = achievementTypeStr;
  }

  public Long getUseTimes() {
    return useTimes;
  }

  public void setUseTimes(Long useTimes) {
    this.useTimes = useTimes;
  }

  public Map toRepairServiceDropDownItemMap(MemberServiceDTO memberServiceDTO) {
    Map<String, String> dropDownItem = new HashMap<String, String>();
    dropDownItem.put("id", this.getId().toString());
    dropDownItem.put("price", String.valueOf(NumberUtil.doubleVal(this.getPrice())));
    dropDownItem.put("name", this.getName());
    dropDownItem.put("business_category_id", this.getCategoryId() == null ? "" : this.getCategoryId().toString());
    if (memberServiceDTO != null) {
      dropDownItem.put("remain_times", memberServiceDTO.getStatus() == MemberStatus.DISABLED ? "0" : String.valueOf(memberServiceDTO.getTimes()));
    }
    return dropDownItem;
  }

}
