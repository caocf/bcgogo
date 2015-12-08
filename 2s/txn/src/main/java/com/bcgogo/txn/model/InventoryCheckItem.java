package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.InventoryCheckItemDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-10-19
 * Time: 下午4:21
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "inventory_check_item")
public class InventoryCheckItem extends LongIdentifier {

  //库存盘点表ID
  private Long inventoryCheckId;

  //产品ID
  private Long productId;

  //当前库存量
  private Double inventoryAmount;

  //新在库存量
  private Double actualInventoryAmount;

  //当前入库价平均价
  private Double inventoryAveragePrice;

  //新入库价平均价
  private Double actualInventoryAveragePrice;

  private Long productHistoryId;

  private String unit;//商品的单位

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "inventory_amount")
  public Double getInventoryAmount() {
    return inventoryAmount;
  }

  public void setInventoryAmount(Double inventoryAmount) {
    this.inventoryAmount = inventoryAmount;
  }


  @Column(name = "inventoryCheck_id")
  public Long getInventoryCheckId() {
    return inventoryCheckId;
  }

  public void setInventoryCheckId(Long inventoryCheckId) {
    this.inventoryCheckId = inventoryCheckId;
  }

  @Column(name = "actual_inventory_amount")
  public Double getActualInventoryAmount() {
    return actualInventoryAmount;
  }

  public void setActualInventoryAmount(Double actualInventoryAmount) {
    this.actualInventoryAmount = actualInventoryAmount;
  }

  @Column(name = "inventory_average_price")
  public Double getInventoryAveragePrice() {
    return inventoryAveragePrice;
  }

  public void setInventoryAveragePrice(Double inventoryAveragePrice) {
    this.inventoryAveragePrice = inventoryAveragePrice;
  }

  @Column(name = "actual_inventory_average_price")
  public Double getActualInventoryAveragePrice() {
    return actualInventoryAveragePrice;
  }

  public void setActualInventoryAveragePrice(Double actualInventoryAveragePrice) {
    this.actualInventoryAveragePrice = actualInventoryAveragePrice;
  }

  @Column(name="product_history_id")
  public Long getProductHistoryId() {
    return productHistoryId;
  }

  public void setProductHistoryId(Long productHistoryId) {
    this.productHistoryId = productHistoryId;
  }


  @Column(name="unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public InventoryCheckItem fromDTO(InventoryCheckItemDTO inventoryCheckItemDTO) {
    if (inventoryCheckItemDTO == null) {
      return this;
    }
    this.setInventoryAmount(inventoryCheckItemDTO.getInventoryAmount());
    this.setInventoryCheckId(inventoryCheckItemDTO.getInventoryCheckId());
    this.setInventoryAveragePrice(inventoryCheckItemDTO.getInventoryAveragePrice());
    this.setProductId(inventoryCheckItemDTO.getProductId());
    this.setActualInventoryAmount(inventoryCheckItemDTO.getActualInventoryAmount());
    this.setActualInventoryAveragePrice(inventoryCheckItemDTO.getActualInventoryAveragePrice());
    this.setProductHistoryId(inventoryCheckItemDTO.getProductHistoryId());
    this.setUnit(inventoryCheckItemDTO.getUnit());
    return this;
  }

  public InventoryCheckItemDTO toDTO(){
    InventoryCheckItemDTO item=new InventoryCheckItemDTO();
    item.setActualInventoryAmount(this.getActualInventoryAmount());
    item.setInventoryAmount(this.getInventoryAmount());
    item.setInventoryAmountAdjustment(NumberUtil.doubleVal(this.getActualInventoryAmount()) - NumberUtil.doubleVal(this.getInventoryAmount()));
    item.setInventoryCheckId(this.getInventoryCheckId());
    item.setInventoryAveragePrice(this.getInventoryAveragePrice());
    item.setInventoryAdjustmentPrice(NumberUtil.round(NumberUtil.doubleVal(item.getInventoryAmountAdjustment())*NumberUtil.doubleVal(this.getInventoryAveragePrice()),2));
    item.setProductId(this.getProductId());
    item.setProductHistoryId(this.getProductHistoryId());
    item.setUnit(this.getUnit());
    item.setId(this.getId());
    return item;
  }
}
