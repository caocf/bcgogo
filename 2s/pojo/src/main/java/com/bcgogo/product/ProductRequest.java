package com.bcgogo.product;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 上午11:10
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.NONE)
public class ProductRequest {
  @XmlElement(name = "kindId")
  private Long kindId;
  @XmlElement(name = "brand")
  private String brand;
  @XmlElement(name = "model")
  private String model;
  @XmlElement(name = "spec")
  private String spec;
  @XmlElement(name = "name")
  private String name;
  @XmlElement(name = "nameEn")
  private String nameEn;
  @XmlElement(name = "mfr")
  private String mfr;
  @XmlElement(name = "mfrEn")
  private String mfrEn;
  @XmlElement(name = "originNo")
  private Integer originNo;
  @XmlElement(name = "origin")
  private String origin;
  @XmlElement(name = "unit")
  private String unit;
  @XmlElement(name = "state")
  private Long state;
  @XmlElement(name = "memo")
  private String memo;
  @XmlElement(name = "productVehicleBrand")
  private String productVehicleBrand;
  @XmlElement(name = "productVehicleModel")
  private String productVehicleModel;
//  @XmlElement(name = "price")
//  private BigDecimal price;
  @XmlElement(name = "parentId")
  private Long parentId;
  @XmlElement(name = "checkStatus")
  private Integer checkStatus;
  @XmlElement(name = "shopId")
  private Long shopId;
  @XmlElement(name = "productVehicleStatus")
  private Integer productVehicleStatus;
  @XmlElement(name = "firstLetter")
  private String firstLetter;      //产品名称首字母
  @XmlElement(name = "firstLetterCombination")
  private String firstLetterCombination;   //产品名称首字母组合

  public String getProductVehicleBrand() {
    return productVehicleBrand;
  }

  public void setProductVehicleBrand(String productVehicleBrand) {
    this.productVehicleBrand = productVehicleBrand;
  }

  public String getProductVehicleModel() {
    return productVehicleModel;
  }

  public void setProductVehicleModel(String productVehicleModel) {
    this.productVehicleModel = productVehicleModel;
  }

  public String getFirstLetter() {
    return firstLetter;
  }

  public void setFirstLetter(String firstLetter) {
    this.firstLetter = firstLetter;
  }

  public String getFirstLetterCombination() {
    return firstLetterCombination;
  }

  public void setFirstLetterCombination(String firstLetterCombination) {
    this.firstLetterCombination = firstLetterCombination;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getKindId() {
    return kindId;
  }

  public void setKindId(Long kindId) {
    this.kindId = kindId;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNameEn() {
    return nameEn;
  }

  public void setNameEn(String nameEn) {
    this.nameEn = nameEn;
  }

  public String getMfr() {
    return mfr;
  }

  public void setMfr(String mfr) {
    this.mfr = mfr;
  }

  public String getMfrEn() {
    return mfrEn;
  }

  public void setMfrEn(String mfrEn) {
    this.mfrEn = mfrEn;
  }

  public Integer getOriginNo() {
    return originNo;
  }

  public void setOriginNo(Integer originNo) {
    this.originNo = originNo;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public Integer getCheckStatus() {
    return checkStatus;
  }

  public void setCheckStatus(Integer checkStatus) {
    this.checkStatus = checkStatus;
  }

  //  public BigDecimal getPrice() {
//    return price;
//  }
//
//  public void setPrice(BigDecimal price) {
//    this.price = price;
//  }

  public Integer getProductVehicleStatus() {
    return productVehicleStatus;
  }

  public void setProductVehicleStatus(Integer productVehicleStatus) {
    this.productVehicleStatus = productVehicleStatus;
  }
}
