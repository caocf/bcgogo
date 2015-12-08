package com.bcgogo.txn.dto;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-10-11
 * Time: 下午2:47
 * To change this template use File | Settings | File Templates.
 */
public class RepairOrderTemplateServiceDTO {

    // Repair_Order_Template 表ID
  private Long repairOrderTemplateId;

  //Service 表ID
  private Long serviceId;

  //施工内容名称
  private String serviceName;

  //施工单工时费
  private Double price;

  Long id;

  String idStr;

  private String businessCategoryName;

  private Long businessCategoryId;

  private String businessCategoryIdStr;

  private RepairOrderServiceDTO repairOrderServiceDTO ;

  public RepairOrderServiceDTO getRepairOrderServiceDTO() {
    return repairOrderServiceDTO;
  }

  public void setRepairOrderServiceDTO(RepairOrderServiceDTO repairOrderServiceDTO) {
    this.repairOrderServiceDTO = repairOrderServiceDTO;
  }

  public Long getRepairOrderTemplateId() {
    return repairOrderTemplateId;
  }

  public void setRepairOrderTemplateId(Long repairOrderTemplateId) {
    this.repairOrderTemplateId = repairOrderTemplateId;
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

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id != null)
    {
      this.idStr = id.toString();
    }
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
    if(StringUtils.isNotBlank(idStr))
    {
       this.id = Long.valueOf(idStr);
    }

  }

  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
    if(null != this.businessCategoryId)
    {
      this.businessCategoryIdStr = this.businessCategoryId.toString();
    }
  }

  public String getBusinessCategoryIdStr() {
    return businessCategoryIdStr;
  }

  public void setBusinessCategoryIdStr(String businessCategoryIdStr) {
    this.businessCategoryIdStr = businessCategoryIdStr;
  }
}
