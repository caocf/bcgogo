package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.InsuranceOrderDTO;
import com.bcgogo.txn.dto.InsuranceOrderItemDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-7
 * Time: 下午6:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "insurance_order_item")
public class InsuranceOrderItem extends LongIdentifier {
  private Long insuranceOrderId;
  private Double price;
  private Double total;
  private Double amount;
  private Long productId;
  private String commodityCode;
  private String productName;
  private String brand;
  private String model;
  private String spec;
  private String vehicleBrand;
  private String vehicleModel;
  private String unit;

  public InsuranceOrderItemDTO toDTO() {
    InsuranceOrderItemDTO insuranceOrderItemDTO = new InsuranceOrderItemDTO();
    insuranceOrderItemDTO.setId(getId());
    insuranceOrderItemDTO.setPrice(getPrice());
    insuranceOrderItemDTO.setTotal(getTotal());
    insuranceOrderItemDTO.setAmount(getAmount());
    insuranceOrderItemDTO.setProductId(getProductId());
    insuranceOrderItemDTO.setCommodityCode(getCommodityCode());
    insuranceOrderItemDTO.setProductName(getProductName());
    insuranceOrderItemDTO.setBrand(getBrand());
    insuranceOrderItemDTO.setModel(getModel());
    insuranceOrderItemDTO.setSpec(getSpec());
    insuranceOrderItemDTO.setVehicleBrand(getVehicleBrand());
    insuranceOrderItemDTO.setVehicleModel(getVehicleModel());
    insuranceOrderItemDTO.setUnit(getUnit());
    return insuranceOrderItemDTO;
  }

  public void fromDTO(InsuranceOrderItemDTO insuranceOrderItemDTO,InsuranceOrderDTO insuranceOrderDTO) {
    if (insuranceOrderItemDTO != null) {
      this.setId(insuranceOrderItemDTO.getId());
      this.setPrice(insuranceOrderItemDTO.getPrice());
      this.setTotal(insuranceOrderItemDTO.getTotal());
      this.setAmount(insuranceOrderItemDTO.getAmount());
      this.setProductId(insuranceOrderItemDTO.getProductId());
      this.setCommodityCode(insuranceOrderItemDTO.getCommodityCode());
      this.setProductName(insuranceOrderItemDTO.getProductName());
      this.setBrand(insuranceOrderItemDTO.getBrand());
      this.setModel(insuranceOrderItemDTO.getModel());
      this.setSpec(insuranceOrderItemDTO.getSpec());
      this.setVehicleBrand(insuranceOrderItemDTO.getVehicleBrand());
      this.setVehicleModel(insuranceOrderItemDTO.getVehicleModel());
      this.setUnit(insuranceOrderItemDTO.getUnit());
    }
    if (insuranceOrderDTO != null) {
      this.setInsuranceOrderId(insuranceOrderDTO.getId());
    }

  }

  @Column(name = "insurance_order_id")
  public Long getInsuranceOrderId() {
    return insuranceOrderId;
  }

  public void setInsuranceOrderId(Long insuranceOrderId) {
    this.insuranceOrderId = insuranceOrderId;
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

  @Column(name = "commodity_code")
  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  @Column(name = "product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  @Column(name = "brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
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

  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

}
