package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PurchaseInventoryItemDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-9
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "purchase_inventory_item")
public class PurchaseInventoryItem extends LongIdentifier {
  public PurchaseInventoryItem() {
  }

  @Column(name = "purchase_inventory_id")
  public Long getPurchaseInventoryId() {
    return purchaseInventoryId;
  }

  public void setPurchaseInventoryId(Long purchaseInventoryId) {
    this.purchaseInventoryId = purchaseInventoryId;
  }

  @Column(name = "purchase_item_id")
  public Long getPurchaseItemId() {
    return purchaseItemId;
  }

  public void setPurchaseItemId(Long purchaseItemId) {
    this.purchaseItemId = purchaseItemId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name="product_history_id")
  public Long getProductHistoryId() {
    return productHistoryId;
  }

  public void setProductHistoryId(Long productHistoryId) {
    this.productHistoryId = productHistoryId;
  }

  @Column(name = "amount")
  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  @Column(name = "price")
  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  @Column(name = "total")
  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  @Column(name = "memo", length = 100)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
  @Column(name = "vehicle_brand", length = 50)
  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }
  @Column(name = "vehicle_model", length = 50)
  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }
  @Column(name = "vehicle_year", length = 50)
  public String getVehicleYear() {
    return vehicleYear;
  }

  public void setVehicleYear(String vehicleYear) {
    this.vehicleYear = vehicleYear;
  }
  @Column(name = "vehicle_engine", length = 50)
  public String getVehicleEngine() {
    return vehicleEngine;
  }

  public void setVehicleEngine(String vehicleEngine) {
    this.vehicleEngine = vehicleEngine;
  }
  @Column(name = "unit" , length = 20)
  public String getUnit(){
    return unit;
  }

  public void setUnit(String unit){
    this.unit = unit;
  }

  @Column(name="product_kind")
  public String getProductKind() {
    return productKind;
  }

  public void setProductKind(String productKind) {
    this.productKind = productKind;
  }


  private Long purchaseInventoryId;
  private Long purchaseItemId;
  private Long productId;
  private Long productHistoryId;
  private double amount;
  private double price;
  private double total;
  private String memo;
  private String vehicleBrand;
  private String vehicleModel;
  private String vehicleYear;
  private String vehicleEngine;
  private String unit;
  private String productKind;
  private Double remainAmount;

  public PurchaseInventoryItem fromDTO(PurchaseInventoryItemDTO itemDTO) {
    if(itemDTO==null)
      return this;
    setId(itemDTO.getId());
    this.purchaseInventoryId = itemDTO.getPurchaseInventoryId();
    this.purchaseItemId = itemDTO.getPurchaseItemId();
    this.productId = itemDTO.getProductId();
    this.productHistoryId = itemDTO.getProductHistoryId();
    this.amount = itemDTO.getAmount()==null?0d:itemDTO.getAmount();
    this.total = itemDTO.getTotal()==null?0d:itemDTO.getTotal();
    this.memo = itemDTO.getMemo();
    this.vehicleBrand = itemDTO.getVehicleBrand();
    this.vehicleModel = itemDTO.getVehicleModel();
    this.vehicleYear = itemDTO.getVehicleYear();
    this.vehicleEngine = itemDTO.getVehicleEngine();
    this.unit = itemDTO.getUnit();
    this.productKind = itemDTO.getProductKind();
    this.setPurchaseInventoryId(itemDTO.getPurchaseInventoryId());
    this.setPrice(itemDTO.getPurchasePrice() == null ? 0d : itemDTO.getPurchasePrice());
    return this;
  }

  public PurchaseInventoryItemDTO toDTO() {
    PurchaseInventoryItemDTO purchaseInventoryItemDTO = new PurchaseInventoryItemDTO();
    purchaseInventoryItemDTO.setId(getId());
    purchaseInventoryItemDTO.setPurchaseInventoryId(getPurchaseInventoryId());
    purchaseInventoryItemDTO.setPurchaseItemId(getPurchaseItemId());
    purchaseInventoryItemDTO.setProductId(getProductId());
    purchaseInventoryItemDTO.setProductHistoryId(getProductHistoryId());
    purchaseInventoryItemDTO.setAmount(getAmount());
    purchaseInventoryItemDTO.setPrice(getPrice());
    purchaseInventoryItemDTO.setPurchasePrice(getPrice());
    purchaseInventoryItemDTO.setTotal(getTotal());
    purchaseInventoryItemDTO.setMemo(getMemo());
    purchaseInventoryItemDTO.setVehicleBrand(getVehicleBrand());
    purchaseInventoryItemDTO.setVehicleModel(getVehicleModel());
    purchaseInventoryItemDTO.setVehicleYear(getVehicleYear());
    purchaseInventoryItemDTO.setVehicleEngine(getVehicleEngine());
    purchaseInventoryItemDTO.setUnit(getUnit());
    purchaseInventoryItemDTO.setProductKind(getProductKind());
    return purchaseInventoryItemDTO;
  }
}
