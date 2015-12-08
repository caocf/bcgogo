package com.bcgogo.txn.dto.assistantStat;

/**
 * 服务提成更改历史记录
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:41
 * To change this template use File | Settings | File Templates.
 */
public class ServiceAchievementHistoryDTO extends AssistantAchievementBaseDTO{
  private Long serviceId;
  private String serviceName;
  private Long categoryId;//营业分类
  private String categoryName;//营业分类
  private Double standardHours;//标准工时
  private Double standardUnitPrice;//标准工时费（单价）

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


}
