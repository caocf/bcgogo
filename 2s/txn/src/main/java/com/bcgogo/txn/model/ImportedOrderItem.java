package com.bcgogo.txn.model;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.txn.dto.ImportedOrderItemDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-11-6
 * Time: 下午1:13
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "imported_order_item")
public class ImportedOrderItem extends LongIdentifier implements Cloneable{
  private Long shopId;
  private Long orderId;
  private String productCode;
  private String productName;

  private String brand;
  private String spec;
  private String model;
  private String vehicleBrand;
  private String vehicleModel;
  private String unit;
  private String serviceContent;
  private Double serviceTotal;
  private String serviceWorker;
  private Double price;
  private Double amount;
  private String itemType;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "order_id")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  @Column(name = "product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  @Column(name = "spec")
  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  @Column(name = "model")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name = "product_code")
  public String getProductCode() {
    return productCode;
  }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }

  @Column(name = "brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name = "vehicle_brand")
  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Column(name = "vehicle_model")
  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Column(name = "service_total")
  public Double getServiceTotal() {
    return serviceTotal;
  }

  public void setServiceTotal(Double serviceTotal) {
    this.serviceTotal = serviceTotal;
  }

  @Column(name = "service_worker")
  public String getServiceWorker() {
    return serviceWorker;
  }

  public void setServiceWorker(String serviceWorker) {
    this.serviceWorker = serviceWorker;
  }

  @Column(name = "service_content")
  public String getServiceContent() {
    return serviceContent;
  }

  public void setServiceContent(String serviceContent) {
    this.serviceContent = serviceContent;
  }

  @Column(name = "item_type")
  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  public ItemIndexDTO generateItemDTO(){
    ItemIndexDTO orderItemDTO=new ImportedOrderItemDTO();
    orderItemDTO.setCommodityCode(this.getProductCode());
    orderItemDTO.setItemName(this.getProductName());
    orderItemDTO.setItemBrand(this.getBrand());
    orderItemDTO.setItemSpec(this.getSpec());
    orderItemDTO.setItemModel(this.getModel());
    orderItemDTO.setVehicleBrand(this.getVehicleBrand());
    orderItemDTO.setVehicleModel(this.getVehicleModel());
    orderItemDTO.setUnit(this.getUnit());
    orderItemDTO.setServices(this.getServiceContent());  //前台都用service
    orderItemDTO.setServiceWorker(this.getServiceWorker());
    orderItemDTO.setItemPrice(this.getServiceTotal());
    orderItemDTO.setItemCount(this.getAmount());
    orderItemDTO.setItemPrice(this.getPrice());
    orderItemDTO.setItemCostPrice(this.getPrice());
//  orderItemDTO.setItemMemo(this.get);  //todo 施工内容的备注
    if(com.bcgogo.utils.StringUtil.isNotEmpty(this.getItemType())&&this.getItemType().equals(ImportedOrderTemp.SERVICE)){
      orderItemDTO.setItemType(ItemTypes.SERVICE);
      orderItemDTO.setItemPrice(this.getServiceTotal());
    }
    return orderItemDTO;
  }

   public ImportedOrderItem clone() throws CloneNotSupportedException{
    ImportedOrderItem item=(ImportedOrderItem)super.clone();
    item.setId(null);
    return item;
  }

}
