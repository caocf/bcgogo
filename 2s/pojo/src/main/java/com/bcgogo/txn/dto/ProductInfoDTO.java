package com.bcgogo.txn.dto;

import com.bcgogo.product.dto.ProductDTO;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-11-21
 * Time: 下午5:16
 * To change this template use File | Settings | File Templates.
 */
public class ProductInfoDTO implements Serializable {
  public ProductInfoDTO() {
  }

  private String supplier;
  private String name;
  private String brand;
  private String spec;
  private String model;
  private String vbrand;
  private String vmodel;
  private String vyear;
  private String vengine;
  private Double price;
  private Double amount;
  private Long editDate;
  private String editDateStr;
  private Double total;
  private Long productId;
  private String productIdStr;
  private Double purchasePrice;
  private Double recommendedPrice;
  private Long productLocalInfoId;
  private String productLocalInfoIdStr;

   private String storageUnit;
  private String sellUnit;
  private Long rate;

  private Double lowerLimit;
  private Double upperLimit;

  public Long getProductId() {
    return productId;
  }

  public String getProductIdStr() {
    return productIdStr;
  }

  public String getProductLocalInfoIdStr() {
    return productLocalInfoIdStr;
  }

  public void setProductIdStr(String productIdStr) {
    this.productIdStr = productIdStr;
  }

  public void setProductLocalInfoIdStr(String productLocalInfoIdStr) {
    this.productLocalInfoIdStr = productLocalInfoIdStr;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
    this.productIdStr = String.valueOf(productId);
  }

  public String getSupplier() {
    return supplier;
  }

  public void setSupplier(String supplier) {
    this.supplier = supplier;
  }

  public String getEditDateStr() {
    return editDateStr;
  }

  public void setEditDateStr(String editDateStr) {
    this.editDateStr = editDateStr;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getVbrand() {
    return vbrand;
  }

  public void setVbrand(String vbrand) {
    this.vbrand = vbrand;
  }

  public String getVmodel() {
    return vmodel;
  }

  public void setVmodel(String vmodel) {
    this.vmodel = vmodel;
  }

  public String getVyear() {
    return vyear;
  }

  public void setVyear(String vyear) {
    this.vyear = vyear;
  }

  public String getVengine() {
    return vengine;
  }

  public void setVengine(String vengine) {
    this.vengine = vengine;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(Double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  public Double getRecommendedPrice() {
    return recommendedPrice;
  }

  public void setRecommendedPrice(Double recommendedPrice) {
    this.recommendedPrice = recommendedPrice;
  }

  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
	  this.productLocalInfoId = productLocalInfoId;
	  if (productLocalInfoId != null) {
		  this.productLocalInfoIdStr = String.valueOf(productLocalInfoId);
	  } else {
		  this.productLocalInfoIdStr = null;
	  }

  }

  public String getStorageUnit() {
    return storageUnit;
  }

  public void setStorageUnit(String storageUnit) {
    this.storageUnit = storageUnit;
  }

  public String getSellUnit() {
    return sellUnit;
  }

  public void setSellUnit(String sellUnit) {
    this.sellUnit = sellUnit;
  }

  public Long getRate() {
    return rate;
  }

  public void setRate(Long rate) {
    this.rate = rate;
  }

  public Double getLowerLimit() {
    return lowerLimit;
  }

  public void setLowerLimit(Double lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  public Double getUpperLimit() {
    return upperLimit;
  }

  public void setUpperLimit(Double upperLimit) {
    this.upperLimit = upperLimit;
  }

  public ProductInfoDTO(ProductDTO productDTO){
          setBrand(productDTO.getBrand());
          setName(productDTO.getName());
          setAmount(productDTO.getInventoryNum());
          setSpec(productDTO.getSpec());
          setModel(productDTO.getModel());
          setVbrand(productDTO.getProductVehicleBrand());
          setVmodel(productDTO.getProductVehicleModel());
          setVengine(productDTO.getProductVehicleEngine());
          setVyear(productDTO.getProductVehicleYear());
          setProductId(productDTO.getId());
          setPurchasePrice(productDTO.getPurchasePrice());
          setRecommendedPrice(productDTO.getRecommendedPrice());
          setProductLocalInfoId(productDTO.getProductLocalInfoId());
          setStorageUnit(productDTO.getStorageUnit());
          setSellUnit(productDTO.getSellUnit());
          setRate(productDTO.getRate());
          setLowerLimit(productDTO.getLowerLimit());
          setUpperLimit(productDTO.getUpperLimit());
  }
}
