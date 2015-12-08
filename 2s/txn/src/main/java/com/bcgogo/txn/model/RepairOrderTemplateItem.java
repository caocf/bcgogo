package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.RepairOrderTemplateItemDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-14
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "repair_order_template_item")
public class RepairOrderTemplateItem extends LongIdentifier {
  public RepairOrderTemplateItem() {
  }

  // Repair_Order_Template 表ID
  private Long repairOrderTemplateId;

  //Product_Local_Info 表ID
  private Long productId;

  //商品数量
  private Double amount;

  //商品单价
  private Double price;

  //商品单位
  private String unit;

  private String businessCategoryName;

  private Long businessCategoryId;

  @Column(name = "repair_order_template_id")
  public Long getRepairOrderTemplateId() {
    return repairOrderTemplateId;
  }

  public void setRepairOrderTemplateId(Long repairOrderTemplateId) {
    this.repairOrderTemplateId = repairOrderTemplateId;
  }

    @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

    @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

    @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

   @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name="business_category_name")
  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

  @Column(name = "business_category_id")
  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  public RepairOrderTemplateItemDTO toDTO()
  {
    RepairOrderTemplateItemDTO templateItemDTO = new RepairOrderTemplateItemDTO();
    templateItemDTO.setAmount(this.amount);
    templateItemDTO.setProductId(this.productId );
    templateItemDTO.setRepairOrderTemplateId(this.repairOrderTemplateId);
    templateItemDTO.setUnit(this.unit);
    templateItemDTO.setAmount(this.amount);
    templateItemDTO.setPrice(this.price);
    templateItemDTO.setId(this.getId());
    templateItemDTO.setIdStr(this.getId().toString());
    templateItemDTO.setBusinessCategoryId(this.getBusinessCategoryId());
    templateItemDTO.setBusinessCategoryName(this.getBusinessCategoryName());
    return templateItemDTO;
  }

}