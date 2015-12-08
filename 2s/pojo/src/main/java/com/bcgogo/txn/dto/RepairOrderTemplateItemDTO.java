package com.bcgogo.txn.dto;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-10-11
 * Time: 下午2:48
 * To change this template use File | Settings | File Templates.
 */
public class RepairOrderTemplateItemDTO {

    // Repair_Order_Template 表ID
  private Long repairOrderTemplateId;

  //商品数量
  private Double amount;

  //商品单价
  private Double price;

    //商品单位
  private String unit;

  private Long productId;

  private RepairOrderItemDTO repairOrderItemDTO;

  private Long id;

  private String idStr;

  private String businessCategoryName;

  private Long businessCategoryId;

  private String businessCategoryIdStr;

  public Long getRepairOrderTemplateId() {
    return repairOrderTemplateId;
  }

  public void setRepairOrderTemplateId(Long repairOrderTemplateId) {
    this.repairOrderTemplateId = repairOrderTemplateId;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public RepairOrderItemDTO getRepairOrderItemDTO() {
    return repairOrderItemDTO;
  }

  public void setRepairOrderItemDTO(RepairOrderItemDTO repairOrderItemDTO) {
    this.repairOrderItemDTO = repairOrderItemDTO;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id != null)
    {
       this.idStr = String.valueOf(id);
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
