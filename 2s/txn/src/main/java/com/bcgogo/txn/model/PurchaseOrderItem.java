package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PurchaseOrderItemDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "purchase_order_item")
public class PurchaseOrderItem extends LongIdentifier {

  public PurchaseOrderItem() {
  }

  @Column(name = "purchase_order_id")
  public Long getPurchaseOrderId() {
    return purchaseOrderId;
  }

  public void setPurchaseOrderId(Long purchaseOrderId) {
    this.purchaseOrderId = purchaseOrderId;
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

  @Column(name = "unit", length = 20)
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name="product_kind")
  public String getProductKind() {
    return productKind;
  }

  public void setProductKind(String productKind) {
    this.productKind = productKind;
  }


  @Column(name = "supplier_product_id", length = 20)
  public Long getSupplierProductId() {
    return supplierProductId;
  }

  public void setSupplierProductId(Long supplierProductId) {
    this.supplierProductId = supplierProductId;
  }

  private Long purchaseOrderId;
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
  private Long supplierProductId;
  private Double quotedPrice;

  private Long quotedPreBuyOrderItemId;

  private String promotionsId;
  private Boolean customPriceFlag;

  public PurchaseOrderItem fromDTO(PurchaseOrderItemDTO purchaseOrderItemDTO) {
    if(purchaseOrderItemDTO==null)
      return this;
    this.purchaseOrderId = purchaseOrderItemDTO.getPurchaseOrderId();
    this.productId = purchaseOrderItemDTO.getProductId();
    this.productHistoryId = purchaseOrderItemDTO.getProductHistoryId();
    this.amount = purchaseOrderItemDTO.getAmount();
    this.price = purchaseOrderItemDTO.getPrice();
    this.total = purchaseOrderItemDTO.getTotal();
    this.memo = purchaseOrderItemDTO.getMemo();
    this.vehicleBrand = purchaseOrderItemDTO.getVehicleBrand();
    this.vehicleModel = purchaseOrderItemDTO.getVehicleModel();
    this.vehicleYear = purchaseOrderItemDTO.getVehicleYear();
    this.vehicleEngine = purchaseOrderItemDTO.getVehicleEngine();
    this.unit = purchaseOrderItemDTO.getUnit();
    this.productKind = purchaseOrderItemDTO.getProductKind();
    this.supplierProductId = purchaseOrderItemDTO.getSupplierProductId();
    this.quotedPrice = purchaseOrderItemDTO.getQuotedPrice();
    this.setQuotedPreBuyOrderItemId(purchaseOrderItemDTO.getQuotedPreBuyOrderItemId());
    Set<Long> promotionsIdList=purchaseOrderItemDTO.getPromotionsIds();
    StringBuffer sb=new StringBuffer();
    if(CollectionUtil.isNotEmpty(promotionsIdList)){
      for(Long promotionId:promotionsIdList){
        if(sb.length()==0){
          sb.append(promotionId);
        }else{
          sb.append(",");
          sb.append(promotionId);
        }
      }
    }
    this.setPromotionsId(sb.toString());
    this.setCustomPriceFlag(purchaseOrderItemDTO.getCustomPriceFlag());
    return this;
  }

  public PurchaseOrderItemDTO toDTO() {
    PurchaseOrderItemDTO purchaseOrderItemDTO = new PurchaseOrderItemDTO();
    purchaseOrderItemDTO.setId(getId());
    purchaseOrderItemDTO.setPurchaseOrderId(getPurchaseOrderId());
    purchaseOrderItemDTO.setProductId(getProductId());
    purchaseOrderItemDTO.setProductHistoryId(getProductHistoryId());
    purchaseOrderItemDTO.setAmount(getAmount());
    purchaseOrderItemDTO.setPrice(getPrice());
    purchaseOrderItemDTO.setTotal(getTotal());
    purchaseOrderItemDTO.setMemo(getMemo());
    purchaseOrderItemDTO.setVehicleBrand(getVehicleBrand());
    purchaseOrderItemDTO.setVehicleModel(getVehicleModel());
    purchaseOrderItemDTO.setVehicleYear(getVehicleYear());
    purchaseOrderItemDTO.setVehicleEngine(getVehicleEngine());
    purchaseOrderItemDTO.setUnit(getUnit());
    purchaseOrderItemDTO.setProductKind(getProductKind());
    purchaseOrderItemDTO.setSupplierProductId(getSupplierProductId());
    purchaseOrderItemDTO.setPromotionsIds(null);  //todo  error
    purchaseOrderItemDTO.setQuotedPrice(getQuotedPrice());
    purchaseOrderItemDTO.setQuotedPreBuyOrderItemId(this.getQuotedPreBuyOrderItemId());
    purchaseOrderItemDTO.setPromotionsId(getPromotionsId());
    purchaseOrderItemDTO.setCustomPriceFlag(getCustomPriceFlag());
    if(getCustomPriceFlag() == null){
      if(getQuotedPreBuyOrderItemId()!=null){
        purchaseOrderItemDTO.setCustomPriceFlag(false);
      }else if(getPromotionsId()!=null){
        purchaseOrderItemDTO.setCustomPriceFlag(false);
      }else if(!NumberUtil.isEqual(getQuotedPrice(), getPrice())){
        purchaseOrderItemDTO.setCustomPriceFlag(true);
      }else{
        purchaseOrderItemDTO.setCustomPriceFlag(false);
      }
    }
    return purchaseOrderItemDTO;
  }
  @Column(name="quoted_pre_buy_order_item_id")
  public Long getQuotedPreBuyOrderItemId() {
    return quotedPreBuyOrderItemId;
  }

  public void setQuotedPreBuyOrderItemId(Long quotedPreBuyOrderItemId) {
    this.quotedPreBuyOrderItemId = quotedPreBuyOrderItemId;
  }

  @Column(name="quoted_price")
  public Double getQuotedPrice() {
    return quotedPrice;
  }

  public void setQuotedPrice(Double quotedPrice) {
    this.quotedPrice = quotedPrice;
  }

  @Column(name="promotions_id")
  public String getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(String promotionsId) {
    this.promotionsId = promotionsId;
  }

  @Column(name="custom_price_flag")
  public Boolean getCustomPriceFlag() {
    return customPriceFlag;
  }

  public void setCustomPriceFlag(Boolean customPriceFlag) {
    this.customPriceFlag = customPriceFlag;
  }
}
