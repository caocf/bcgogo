package com.bcgogo.txn.model.app;

import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.AppointOrderMaterialDTO;
import com.bcgogo.txn.dto.InStorageRecordDTO;
import com.bcgogo.txn.dto.OutStorageRelationDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-28
 * Time: 下午3:20
 */
@Entity
@Table(name = "appoint_order_material")
public class AppointOrderMaterial extends LongIdentifier {
  private Long shopId;
  private Long appointOrderId;
  private Long productId; //对应 product_local_info 的id
  private Double price;
  private Double total;
  private Double amount;
  private String commodityCode;    //商品编码
  private String productName;
  private String brand;
  private String model;
  private String spec;
  private String unit;
  private String vehicleBrand;
  private String vehicleModel;
  private ObjectStatus status;

  public void fromDTO(AppointOrderMaterialDTO itemDTO) {
    if(itemDTO != null){
      this.setShopId(itemDTO.getShopId());
      this.setAppointOrderId(itemDTO.getAppointOrderId());
      this.setProductId(itemDTO.getProductId());
      this.setPrice(itemDTO.getPrice());
      this.setTotal(itemDTO.getTotal());
      this.setAmount(itemDTO.getAmount());
      this.setCommodityCode(itemDTO.getCommodityCode());
      this.setProductName(itemDTO.getProductName());
      this.setBrand(itemDTO.getBrand());
      this.setModel(itemDTO.getModel());
      this.setSpec(itemDTO.getSpec());
      this.setUnit(itemDTO.getUnit());
      this.setVehicleBrand(itemDTO.getVehicleBrand());
      this.setVehicleModel(itemDTO.getVehicleModel());
      this.setStatus(itemDTO.getStatus());
    }
  }

  public AppointOrderMaterialDTO toDTO() {
    AppointOrderMaterialDTO itemDTO = new AppointOrderMaterialDTO();
    itemDTO.setShopId(this.getShopId());
    itemDTO.setAppointOrderId(this.getAppointOrderId());
    itemDTO.setProductId(this.getProductId());
    itemDTO.setPrice(this.getPrice());
    itemDTO.setTotal(this.getTotal());
    itemDTO.setAmount(this.getAmount());
    itemDTO.setCommodityCode(this.getCommodityCode());
    itemDTO.setProductName(this.getProductName());
    itemDTO.setBrand(this.getBrand());
    itemDTO.setModel(this.getModel());
    itemDTO.setSpec(this.getSpec());
    itemDTO.setUnit(this.getUnit());
    itemDTO.setVehicleBrand(this.getVehicleBrand());
    itemDTO.setVehicleModel(this.getVehicleModel());
    itemDTO.setStatus(this.getStatus());
    return itemDTO;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "appoint_order_id")
  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
  }

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name = "product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  @Column(name = "commodity_code")
  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
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

  @Column(name = "model")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name = "spec")
  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public ObjectStatus getStatus() {
    return status;
  }

  public void setStatus(ObjectStatus status) {
    this.status = status;
  }



}
